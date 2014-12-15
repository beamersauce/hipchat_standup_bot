package standupbot.meeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import standupbot.bot.RoomBot;
import standupbot.util.Utils;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.HipchatUser;

public class MeetingTask extends TimerTask
{
	private RoomBot room_bot = null;
	private HippyBot hippy_bot = null;
	private Map<String, Long> user_time_ran = null;
	private List<String> user_messages = null;
	
	public MeetingTask(HippyBot hippy_bot, RoomBot room_bot)
	{
		this.room_bot = room_bot;
		this.hippy_bot = hippy_bot;
		user_time_ran = new HashMap<String,Long>();
	}

	@Override
	public void run()
	{
		//run a standup
		StringBuilder sb = new StringBuilder();
		sb.append("@all Time for standup!");
		if ( room_bot.bot_data.speaking_trigger_words != null )
		{
			sb.append("\nStart or end a sentence with '" + room_bot.bot_data.speaking_trigger_words.toString() + "' to end your turn.");
		}
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);		
		room_bot.setStandupParticipants();
		
		processAllParticipants();
		
	}

	/**
	 * Keep trying to get the next user, until none are left
	 * 
	 * Wait for their response or timeout
	 */
	private void processAllParticipants()
	{
		user_messages = new ArrayList<String>();
		//reset checks
		room_bot.current_standup_user = null;
		room_bot.did_user_speak = false;
		room_bot.did_user_say_anything = false;
		HipchatUser next_user = null;
		StringBuilder sb = new StringBuilder();
		while ( (next_user = room_bot.getNextStandupUser()) != null )
		{			
			sb.append("Your turn @" + next_user.getMentionName());
			hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
			sb = new StringBuilder();
			long start_time = System.currentTimeMillis(); //for tracking timeout
			
			//block while we wait for timer to end or user to speak
			while ( (System.currentTimeMillis() - start_time) < (room_bot.bot_data.max_secs_between_turns*1000L) &&
					!room_bot.did_user_speak )
			{
				//sleep for a few, then check again
				try
				{					
					Thread.sleep(200);
				} 
				catch (InterruptedException e)
				{					
					e.printStackTrace();
				}
			}
					
			long end_time = System.currentTimeMillis();
			if ( room_bot.did_user_speak )
			{
				//user ended his standup by speaking
				sb.append("Thanks " + Utils.getFirstName(room_bot.current_standup_user.getName()) + "! ");
			}
			else if ( room_bot.did_user_say_anything )
			{
				//user said something but not correct format
				sb.append(Utils.getFirstName(room_bot.current_standup_user.getName()) + " spoke but didn't say terminating word, burned on a technicality! ");
			}
			else
			{
				//user timeout
				sb.append(Utils.getFirstName(room_bot.current_standup_user.getName()) + " must be sleepy! ");
			}
			user_time_ran.put(room_bot.current_standup_user.getMentionName(), end_time-start_time);
			StringBuilder builder = new StringBuilder();
			for ( String s : room_bot.current_standup_user_messages)
				builder.append(s).append(" ");
			user_messages.add(builder.toString());
			
			//reset checks
			room_bot.current_standup_user = null;
			room_bot.did_user_speak = false;
			room_bot.did_user_say_anything = false;
		}
		
		//once we get here everyone has gone, display stats
		sb.append("That's everyone!");				
		sb.append("\nRuntime stats:");
		for ( Map.Entry<String, Long> entry : user_time_ran.entrySet() )
		{
			double run_time = (entry.getValue())/1000.0;
			sb.append("\n@" + entry.getKey() + ": " + run_time + "s");
		}
		room_bot.users_early_standup.clear();
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
		room_bot.endStandup();	
		
		//handle user messages for summary
		createSummary();
		hippy_bot.sendMessage("SUMMARY: " + room_bot.summary, room_bot.current_room);
	}

	/**
	 * Try to take a response and split it on yesterday/today, combine everyones responses
	 * 
	 */
	private void createSummary()
	{
		StringBuilder summary_yesterday = new StringBuilder();
		StringBuilder summary_today = new StringBuilder();
		user_messages.add("yesterday some beef tacos today say what what");
		for ( String user_message : user_messages )
		{
			if ( !user_message.isEmpty() )
			{
				//hard coding this to look for "today" to split on
				//maybe some time in the future we can use something more intelligent
				//TODO test if new lines come through, can remove them if so
				//(?i) means case insensitive
				String[] splits = user_message.replaceAll("\r|\n", "").split("(?i)today");
				//assume splits[0] is yesterday, everything else is today, replace first instance of yesterday 
				String yesterday = splits[0].replaceFirst("(?i)yesterday", "").trim();
				StringBuilder today = new StringBuilder();
				for ( int i = 1; i < splits.length; i++)
				{
					today.append(splits[i] + " ");
				}
				summary_yesterday.append(yesterday + " || ");
				summary_today.append(today.toString() + " || ");
			}
		}
		room_bot.summary = "[" + room_bot.current_room.getTrueName() + "]\nyesterday: " + summary_yesterday.toString() + "\ntoday: " + summary_today;
	}
	
}
