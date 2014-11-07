package standup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import standupbot.util.Utils;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.HipchatUser;
import com.ep.hippyjava.model.Room;


public class StandupBot extends HippyBot
{
	public HipchatUser current_standup_user = null;
	public boolean did_user_speak = false;
	public BotData botData = null;
	public Date nextRunTime;
	Pattern speakingTrigger = null;
	private int num_spoke_on_turn = 0;
	private int num_spoke_out_of_turn = 0;
	private int num_participants = 0;
	public static boolean changing_room = false;
	
	
	//plusplus bot stuff
	Pattern plusplus = Pattern.compile("(\\S+)\\+\\+");
	Pattern minusminus = Pattern.compile("(\\S+)\\-\\-");
	
	//worker thread for silencing during room changing
	public static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	
	@Override
	public String nickname()
	{
		return "Standup Bot";
	}
	
	@Override
	public String password()
	{		
		return "123qwe";
	}
	
	@Override
	public String username()
	{		
		return "44941_1021361@chat.hipchat.com";
	}

	@Override
	public String apiKey()
	{		
		return "8e42d322e59c7e66229ba5cda30be1";
	}
	
	private static Timer timer_standup;
	private static Timer timer_warning;
	
	@Override
	public void onLoad()
	{
		//get last data
		botData = BotData.loadData();				
		
		if (botData.room_name != null )
		{
			//join the standup room		
			super.joinRoom(botData.room_name);		
			
			timer_standup = new Timer();
			timer_warning = new Timer();
			//Date time_to_standup = new Date();
			//time_to_standup.setTime(time_to_standup.getTime() + (1000*10) ); //add 10s to now
			//timer_standup.schedule(new StandupTask(this), time_to_standup);
			
			if ( botData.speaking_trigger_words != null )
			{
				createSpeakingTriggerPattern();
			}
			
			if ( !botData.silentStart )
			{
				super.sendMessage("Hello cylons, I am StandupBot, type /standup to control me.");
			}
			scheduleStandup(true, -1);
			
		}
		else
		{
			System.out.println("Invalid room name set, need to pass that in");
		}
	}

	@Override
	public void receiveMessage(String message, String from, Room room)
	{		
		if ( !changing_room )
		{
			//System.out.println("message: " + message + " from: " + from + " room: "+ room.getTrueName());
			if ( !from.equals(this.nickname()) )
			{
				if ( botData.enablePlusPlus )
				{				
					handlePlusPlusBot(message, from);
				}
			}
			
			if ( message.startsWith("/standup") )
			{
				StandupBotCommands.handleCommand(message, this, botData);
				//bot commands
				//parseBotCommands(message);
			}
			else if ( current_standup_user != null && from.equals(current_standup_user.getName()))
			{
				if ( botData.speaking_trigger_words != null )
				{
					//check if user used trigger word
					Matcher tmatcher = speakingTrigger.matcher(message);
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
			else if ( current_standup_user != null && !from.equals(this.nickname()))
			{
				num_spoke_out_of_turn++;
			}
		}
	}
	
	public void createSpeakingTriggerPattern()
	{
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb1.append("^(");
		sb2.append("(");
		int count = 0;
		for ( String trigger : botData.speaking_trigger_words )
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
	
	private void handlePlusPlusBot(String message, String from)
	{
		//get any plusplus terms and report
		Matcher pmatcher = plusplus.matcher(message);
		while ( pmatcher.find() )
		{
			String term = pmatcher.group(1).toLowerCase();
			Long new_value = 0L;
			if ( !botData.plusplus_map.containsKey(term) )
			{
				botData.plusplus_map.put(term, 1L);
				new_value = 1L;
			}
			else
			{
				new_value = botData.plusplus_map.get(term)+1;
				botData.plusplus_map.put(term, new_value);
			}
			super.sendMessage("[" + from + "] " + term + "++ [woot! now at " + new_value + "]");			
		}
		//get any minueminus terms and report
		Matcher mmatcher = minusminus.matcher(message);
		while ( mmatcher.find() )
		{
			String term = mmatcher.group(1).toLowerCase();
			Long new_value = 0L;
			if ( !botData.plusplus_map.containsKey(term) )
			{
				botData.plusplus_map.put(term, -1L);
				new_value = -1L;
			}
			else
			{
				new_value = botData.plusplus_map.get(term)-1;
				botData.plusplus_map.put(term, new_value);
			}
			super.sendMessage("[" + from + "] " + term + "-- [ouch! now at " + new_value + "]");
		}
		//save any data back
		BotData.saveData(botData);
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
		timer_standup.schedule(new StandupTask(this), nextRunTime);
		handleWarningCommand();
	}
	
	public void handleWarningCommand()
	{
		if ( botData.warning_minutes > 0 )
		{
			Date nextWarningTime = new Date(nextRunTime.getTime() - (botData.warning_minutes*60000));
			timer_warning.cancel();
			timer_warning = new Timer();
			timer_warning.schedule(new WarningTask(this), nextWarningTime);
		}
	}
	
	private Date getNextStandupTime(boolean initial_startup)
	{	
		//TODO handle days to run on
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR, botData.hour_to_run%12); //TODO this is on 12h need to handle to get proper time
		if ( botData.hour_to_run > 11)
		{
			cal.set(Calendar.AM_PM, Calendar.PM);
		}
		else
		{
			cal.set(Calendar.AM_PM, Calendar.AM);
		}
		cal.set(Calendar.MINUTE, botData.minute_to_run);
		if ( cal.getTime().getTime() < new Date().getTime() )
		{
			//add a day, we are past todays standup time
			cal.add(Calendar.DATE, 1);
		}
		if ( botData.days_to_run.size() > 0 )
		{
			while ( !botData.days_to_run.contains(cal.get(Calendar.DAY_OF_WEEK)-1))
			{
				//keep adding days until we get to one we can run
				cal.add(Calendar.DATE, 1);
			}
		}
		Date date = cal.getTime();
		//only display next standup time if we aren't just turning bot on and silentStart is off
		if ( !initial_startup || !botData.silentStart )
			super.sendMessage("I will orchestrate the next standup on: " + date);
		return date;
	}	

	public void endStandup()
	{
		super.sendMessage("Standup Complete beep-boo-beep");
		StringBuilder sb = new StringBuilder();
		sb.append("Standup Stats: ");
		double now = new Date().getTime();
		double start = nextRunTime.getTime();
		double run_time = (now-start)/1000.0;
		sb.append("\nRuntime: " + run_time + "s");
		sb.append("\nNumber of missed turns: " + (num_participants-num_spoke_on_turn));
		sb.append("\nNumber of messages out of turn: " + num_spoke_out_of_turn );
		super.sendMessage(sb.toString());
				
		scheduleStandup(false, -1);
	}	

	List<HipchatUser> users_present_at_start = null;
	List<HipchatUser> users_late_to_standup = null;	
	List<HipchatUser> remaining_users_for_standup = null;
	List<HipchatUser> users_called_on_in_standup = null;
	LinkedList<String> users_left_in_turn_order = null;
	LinkedList<HipchatUser> user_order_for_standup = null;
	
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
		for ( String user_name : botData.turn_order )
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
		
		this.sendMessage("Order for standup is: [" + Utils.listOfUsers( user_order_for_standup ) + "]");
		
		users_called_on_in_standup = new ArrayList<HipchatUser>();
	}
	
	public HipchatUser getNextStandupUser()
	{
		current_standup_user = null;
		while ( user_order_for_standup.size() > 0 )
		{
			HipchatUser next_user = user_order_for_standup.pop();
			users_called_on_in_standup.add(next_user);
			return next_user;
		}
		/*
		//1. picks a user left in the turn order list
		while ( users_left_in_turn_order.size() > 0 )
		{
			String next_user_name = users_left_in_turn_order.removeFirst();
			//check if this user is in users_present_at_start
			for ( HipchatUser hip_user : remaining_users_for_standup )
			{
				if ( hip_user.getMentionName().equals(next_user_name) )
				{
					remaining_users_for_standup.remove(hip_user);
					current_standup_user = hip_user;
					num_participants++;
					users_called_on_in_standup.add(hip_user);
					return hip_user;
				}
			}
		}
		
		//2. from users_present_at_start
		if ( remaining_users_for_standup.size() > 0 )
		{
			current_standup_user = remaining_users_for_standup.remove(new Random().nextInt(remaining_users_for_standup.size()));
			num_participants++;
			users_called_on_in_standup.add(current_standup_user);
			return current_standup_user;
		}*/
		
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
			sendMessage("Tisk Tisk, these users were late to standup: " + Utils.listOfUsers(users_late_to_standup));
			remaining_users_for_standup.addAll(users_late_to_standup);
			users_late_to_standup.clear();
			return getNextStandupUser();
		}
		
		//finally return null when we are exhausted
		return null;
	}
	
	/**
	 * Returns all users in the room that are not offline or in the blacklist
	 * 
	 * @return
	 */
	public List<HipchatUser> getStandupUsers()
	{
		List<HipchatUser> remaining_users = new ArrayList<HipchatUser>();
		List<String> user_names = getSelectedRoom().getConnectedUsers();
		List<HipchatUser> users = new ArrayList<HipchatUser>();
		for ( String user_name : user_names )
		{
			HipchatUser user = findUser(user_name.substring(user_name.indexOf("/")+1));
			if ( user != null )
				users.add(user);
		}
		for ( HipchatUser user : users )
		{
			if ( !user.getStatus().equals("offline") && !user.getName().equals(nickname()) && !botData.blacklist.contains( user.getMentionName() ) )
			{
				remaining_users.add(user);
			}
		}
		return remaining_users;
	}
	
}
