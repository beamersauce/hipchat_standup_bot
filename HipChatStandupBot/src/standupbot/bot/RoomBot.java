package standupbot.bot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.HipchatUser;
import com.ep.hippyjava.model.Room;

import standupbot.command.RoomBotCommands;
import standupbot.data_model.BotData;
import standupbot.data_model.BotDataManager;
import standupbot.data_model.RoomMessage;
import standupbot.meeting.MeetingTask;
import standupbot.meeting.SummaryTask;
import standupbot.util.Utils;

public class RoomBot implements Runnable
{
	public final BlockingQueue<RoomMessage> message_queue = new LinkedBlockingQueue<RoomMessage>();
	private boolean stopRequested = false;
	public BotData bot_data = null;
	private HippyBot hippy_bot = null;
	public Room current_room = null;
	public HipchatUser current_standup_user = null;
	public boolean did_user_speak = false;
	public boolean did_user_say_anything = false;
	public Date nextRunTime;
	Pattern speakingTrigger = null;
	Pattern speakingEarlyTrigger = null;
	private int num_spoke_on_turn = 0;
	private int num_spoke_out_of_turn = 0;
	private int num_participants = 0;
	public boolean changing_room = false;
	public Date curr_users_start_time = null;
	//public Set<String> users_early_standup = new HashSet<String>();
	public Map<String, String> users_early_standup = new HashMap<String, String>();
	private Timer timer_standup;
	private Timer timer_warning;
	public String summary;
	public List<String> current_standup_user_messages;
	
	//standup stuff
	List<HipchatUser> users_present_at_start = null;
	List<HipchatUser> users_late_to_standup = null;	
	List<HipchatUser> remaining_users_for_standup = null;
	List<HipchatUser> users_called_on_in_standup = null;
	LinkedList<String> users_left_in_turn_order = null;
	LinkedList<HipchatUser> user_order_for_standup = null;
	
	//plusplus bot stuff
	Pattern plusplus = Pattern.compile("(\\S+)\\+\\+");
	Pattern minusminus = Pattern.compile("(\\S+)\\-\\-");
	
	//worker thread for silencing bot during room change
	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	
	
	public RoomBot( HippyBot hippy_bot, BotData bot_data, String room_name)
	{
		changing_room = true;
		
		this.bot_data = bot_data;
		this.hippy_bot = hippy_bot;
		
		//join room
		Utils.joinRoom(hippy_bot, room_name);
		current_room = hippy_bot.findRoom(room_name);
				
		//setup init vars
		timer_standup = new Timer();
		timer_warning = new Timer();
		
		if ( bot_data.speaking_trigger_words != null )
		{
			createSpeakingTriggerPattern();
		}
		if ( bot_data.speaking_early_trigger_words != null )
		{
			createSpeakingEarlyTriggerPattern();
		}
		
		if ( !bot_data.silentStart )
		{
			hippy_bot.sendMessage("Hello cylons, I am StandupBot, type /standup to control me.");
		}
		
		scheduleStandup(true, -1);
		
		//turn changing room off after 5 seconds (we get all the previous messages in a room
		//when joining so do this to ignore them).		
		worker.schedule(new ChangingRoomRunnable(this), 5, TimeUnit.SECONDS);
	}
	
	//@Override
	public void run()
	{
		while ( !stopRequested )
		{
			try
			{
				RoomMessage room_message = message_queue.take();
				//need the extra check here, because bot will stay in room until another message is sent after shutdown call
				if ( !stopRequested ) 
				{
					//System.out.println("room: [" + current_room.getTrueName() + "] received message: " + room_message.message);
					handleMessage(room_message);
				}
			}
			catch (InterruptedException ex)
			{
				System.out.println("was interrupted, kick out");
			}
		}
		hippy_bot.sendMessage("Stop requested, turning off in this room", current_room);
		System.out.println("stop was requested, returning from run, room: " + current_room.getXMPPName());
	}
	
	private void handleMessage(RoomMessage room_message)
	{		
		if (!changing_room )
		{
			if ( bot_data.enablePlusPlus )
			{	
				//TODO maybe I can make a special type of command that can get all text instead of matching on command? 
				//can use to run standup and plusplusbot?
				handlePlusPlusBot(room_message);
			}
			
			//check if it is an early standup request
			handleEarlyStandup(room_message);
			
		
			if ( room_message.message.startsWith("/standup") )
			{
				//handle bot commands
				RoomBotCommands.handleCommand(room_message.message, hippy_bot, this, bot_data);			
			}
			else if ( current_standup_user != null && room_message.from.equals(current_standup_user.getName()))
			{
				current_standup_user_messages.add(room_message.message);
				//TODO ID LIKE TO MOVE THIS STUFF OUT OF HERE?
				//handle speaking during standup, current user
				did_user_say_anything = true;
				if ( bot_data.speaking_trigger_words != null )
				{
					//check if user used trigger word
					Matcher tmatcher = speakingTrigger.matcher(room_message.message);
					if ( tmatcher.find() )
					{
						if ( !did_user_speak )
						{
							did_user_speak = true;
							num_spoke_on_turn++;
						}
						//super.sendMessage(from + " spoke on his turn");
					}
				}				
			}
			else if ( current_standup_user != null )
			{
				//handle speaking during standup, other user
				num_spoke_out_of_turn++;
			}
		}
	}

	private void handlePlusPlusBot(RoomMessage room_message)
	{
		//get any plusplus terms and report
		Matcher pmatcher = plusplus.matcher(room_message.message);
		while ( pmatcher.find() )
		{
			String term = pmatcher.group(1).toLowerCase();
			Long new_value = 0L;
			if ( !bot_data.plusplus_map.containsKey(term) )
			{
				bot_data.plusplus_map.put(term, 1L);
				new_value = 1L;
			}
			else
			{
				new_value = bot_data.plusplus_map.get(term)+1;
				bot_data.plusplus_map.put(term, new_value);
			}
			hippy_bot.sendMessage("[" + room_message.from + "] " + term + "++ [woot! now at " + new_value + "]", current_room);			
		}
		//get any minueminus terms and report
		Matcher mmatcher = minusminus.matcher(room_message.message);
		while ( mmatcher.find() )
		{
			String term = mmatcher.group(1).toLowerCase();
			Long new_value = 0L;
			if ( !bot_data.plusplus_map.containsKey(term) )
			{
				bot_data.plusplus_map.put(term, -1L);
				new_value = -1L;
			}
			else
			{
				new_value = bot_data.plusplus_map.get(term)-1;
				bot_data.plusplus_map.put(term, new_value);
			}
			hippy_bot.sendMessage("[" + room_message.from + "] " + term + "-- [ouch! now at " + new_value + "]", current_room);
		}
		//save any data back
		BotData.saveData(bot_data);
		
	}

	private void handleEarlyStandup(RoomMessage room_message)
	{
		Matcher tmatcher = speakingEarlyTrigger.matcher(room_message.message);
		if ( tmatcher.find() )
		{
			String mention_name = Utils.getUsersMentionName( room_message.from, hippy_bot );
			//this.users_early_standup.add(mention_name);
			this.users_early_standup.put(mention_name, room_message.message);
			hippy_bot.sendMessage(mention_name + " marked as giving standup early.", current_room);
		}	
	}
	
	public void createSpeakingTriggerPattern()
	{
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb1.append("^(");
		sb2.append("(");
		int count = 0;
		for ( String trigger : bot_data.speaking_trigger_words )
		{
			if ( count > 0 )
			{
				sb1.append("|");
				sb2.append("|");
			}
			trigger = trigger.toLowerCase();
			sb1.append(trigger);
			sb2.append(trigger);
			count++;
		}
		sb1.append(")");
		sb2.append(")$");
		
		
		speakingTrigger = Pattern.compile(sb1.toString() + "|" + sb2.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	}
	
	public void createSpeakingEarlyTriggerPattern()
	{
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb1.append("^(");
		sb2.append("(");
		int count = 0;
		for ( String trigger : bot_data.speaking_early_trigger_words )
		{
			if ( count > 0 )
			{
				sb1.append("|");
				sb2.append("|");
			}
			trigger = trigger.toLowerCase();
			sb1.append(trigger);
			sb2.append(trigger);
			count++;
		}
		sb1.append(")");
		sb2.append(")$");
		
		
		speakingEarlyTrigger = Pattern.compile(sb1.toString() + "|" + sb2.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	}
	
	public void scheduleStandup(boolean initial_startup, long override_next_time)
	{		
		num_participants = 0;
		num_spoke_out_of_turn = 0;
		num_spoke_on_turn = 0;
		if ( override_next_time > -1 )
		{
			nextRunTime = new Date(override_next_time);
		}
		else
		{
			nextRunTime = getNextStandupTime(initial_startup);
		}
		timer_standup.cancel();
		timer_standup = new Timer();
		if ( bot_data.isSummary )
		{
			timer_standup.schedule(new SummaryTask(hippy_bot, this), nextRunTime);
		}
		else
		{
			timer_standup.schedule(new MeetingTask(hippy_bot, this), nextRunTime);
			handleWarningCommand();
		}		
	}
	
	private Date getNextStandupTime(boolean initial_startup)
	{	
		//TODO handle days to run on
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR, bot_data.hour_to_run%12); //TODO this is on 12h need to handle to get proper time
		if ( bot_data.hour_to_run > 11)
		{
			cal.set(Calendar.AM_PM, Calendar.PM);
		}
		else
		{
			cal.set(Calendar.AM_PM, Calendar.AM);
		}
		cal.set(Calendar.MINUTE, bot_data.minute_to_run);
		//add a minute to current time so we only hold standup once a day (otherwise you can 
		//start/finish at 9:30 and it will try to schedule again the same day
		if ( cal.getTime().getTime() < (new Date().getTime()+60000) )
		{
			//add a day, we are past todays standup time
			cal.add(Calendar.DATE, 1);
		}
		if ( bot_data.days_to_run.size() > 0 )
		{
			while ( !bot_data.days_to_run.contains(cal.get(Calendar.DAY_OF_WEEK)-1))
			{
				//keep adding days until we get to one we can run
				cal.add(Calendar.DATE, 1);
			}
		}
		Date date = cal.getTime();
		//only display next standup time if we aren't just turning bot on and silentStart is off
		if ( !initial_startup || !bot_data.silentStart )
			hippy_bot.sendMessage("I will orchestrate the next standup on: " + date, current_room);
		return date;
	}	
	
	public void handleWarningCommand()
	{
		if ( bot_data.warning_minutes > 0 )
		{
			Date nextWarningTime = new Date(nextRunTime.getTime() - (bot_data.warning_minutes*60000));
			timer_warning.cancel();
			timer_warning = new Timer();
			timer_warning.schedule(new WarningTask(hippy_bot, this), nextWarningTime);
		}
	}
	
	public void setStandupParticipants()
	{
		//get the list of users in room available for standup
		List<HipchatUser> remaining_users = getStandupUsers();		
		users_present_at_start = remaining_users;
		remaining_users_for_standup = new ArrayList<HipchatUser>();
		for ( HipchatUser user : remaining_users )
			remaining_users_for_standup.add( user );
		users_left_in_turn_order = new LinkedList<String>();
		user_order_for_standup = new LinkedList<HipchatUser>();
		for ( String user_name : bot_data.turn_order )
		{
			users_left_in_turn_order.add(user_name);			
			//find the hipchat user matching a turn order user
			for ( HipchatUser hip_user : remaining_users_for_standup )
			{
				if ( hip_user.getMentionName().equals(user_name) )
				{
					remaining_users_for_standup.remove(hip_user);
					user_order_for_standup.add(hip_user);
					break;
				}
			}
		}
		
		//randomize remaining users for standup
		Collections.shuffle(remaining_users_for_standup);
		user_order_for_standup.addAll(remaining_users_for_standup);
		
		hippy_bot.sendMessage("Order for standup is: [" + Utils.listOfUsers( user_order_for_standup ) + "]", current_room);
		
		users_called_on_in_standup = new ArrayList<HipchatUser>();
	}
	
	/**
	 * Returns all users in the room that are not offline or in the blacklist or gave standup early
	 * 
	 * @return
	 */
	public List<HipchatUser> getStandupUsers()
	{
		List<HipchatUser> remaining_users = new ArrayList<HipchatUser>();
		List<String> user_names = current_room.getConnectedUsers();
		List<HipchatUser> users = new ArrayList<HipchatUser>();
		for ( String user_name : user_names )
		{
			HipchatUser user = hippy_bot.findUser(user_name.substring(user_name.indexOf("/")+1));
			if ( user != null )
				users.add(user);
		}
		for ( HipchatUser user : users )
		{
			if ( !user.getStatus().equals("offline") && !user.getName().equals(hippy_bot.nickname()) && 
					!bot_data.blacklist.contains( user.getMentionName() ) && !users_early_standup.keySet().contains(user.getMentionName()) )
			{
				remaining_users.add(user);
			}
		}
		return remaining_users;
	}
	
	public void endStandup()
	{
		hippy_bot.sendMessage("Standup Complete beep-boo-beep", current_room);
		StringBuilder sb = new StringBuilder();
		sb.append("Standup Stats: ");
		double now = new Date().getTime();
		double start = nextRunTime.getTime();
		double run_time = (now-start)/1000.0;
		sb.append("\nRuntime: " + run_time + "s");
		sb.append("\nNumber of missed turns: " + (num_participants-num_spoke_on_turn));
		sb.append("\nNumber of messages out of turn: " + num_spoke_out_of_turn );
		hippy_bot.sendMessage(sb.toString(), current_room);
				
		scheduleStandup(false, -1);
	}	
	
	public HipchatUser getNextStandupUser()
	{
		current_standup_user = null;
		current_standup_user_messages = new ArrayList<String>();
		while ( user_order_for_standup.size() > 0 )
		{
			HipchatUser next_user = user_order_for_standup.pop();
			current_standup_user = next_user;
			num_participants++;
			users_called_on_in_standup.add(next_user);
			return next_user;
		}
		
		//3. rechecks the room for late comers
		users_late_to_standup = getStandupUsers();
		for ( int i = 0; i < users_late_to_standup.size(); i++) 
		{
			HipchatUser late_user = users_late_to_standup.get(i);
			//have to loop because the objects are different :(
			for ( HipchatUser start_user : users_called_on_in_standup )
			{
				if ( start_user.getEmail().equals(late_user.getEmail()) )
				{
					users_late_to_standup.remove(i);
					i--;
					break;
				}
			}
		}		
		if ( users_late_to_standup.size() > 0 )
		{
			hippy_bot.sendMessage("Tisk Tisk, these users were late to standup: " + Utils.listOfUsers(users_late_to_standup), current_room);
			user_order_for_standup.addAll(users_late_to_standup);
			users_late_to_standup.clear();
			return getNextStandupUser();
		}
		
		//finally return null when we are exhausted
		return null;
	}

	/**
	 * Tells this bot to stop executing, save its data and kill its thread
	 * 
	 */
	public void shutdown()
	{
		BotDataManager.saveData(current_room.getXMPPName(), bot_data);
		stopRequested = true;
	}
}
