package standupbot.bot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.HipchatUser;


public class NextStandupTask extends TimerTask
{
	private HippyBot hippy_bot = null;
	private RoomBot room_bot = null;	
	Timer timer = new Timer();
	Timer timeout_timer = new Timer();
	Timer poll_timer = new Timer();
	boolean isPoll = false;
	private static List<Timer> timers = new ArrayList<Timer>();	
	private static Map<String, Long> user_time_ran = null;
	
	public NextStandupTask(HippyBot hippy_bot, RoomBot room_bot, boolean isPoll, boolean newRun)
	{
		this.hippy_bot = hippy_bot;
		this.room_bot = room_bot;
		this.isPoll = isPoll;
		if ( newRun )
			user_time_ran = new HashMap<String,Long>();
		
	}

	@Override
	public void run()
	{
		//TODO collect statistics on users for fun:
		//1. # of standups attended/missed
		//2. speed of response after called on
		//3. avg length of standup response?
		//4. # of out of turn comments
		
		//2 ways in:
		//1. is a poll check and the user spoke
		//2. is not a poll, timer expired then	
		if ( (isPoll && room_bot.did_user_speak) || (!isPoll))
		{
			StringBuilder sb = new StringBuilder();
			if ( room_bot.current_standup_user != null )
			{
				if ( room_bot.did_user_speak )
				{
					sb.append("Thanks " + getFirstName(room_bot.current_standup_user.getName()) + "! ");					
				}
				else
				{					
					if ( room_bot.did_user_say_anything )
						sb.append(getFirstName(room_bot.current_standup_user.getName()) + " spoke but didn't say terminating word, burned on a technicality! ");
					else
						sb.append(getFirstName(room_bot.current_standup_user.getName()) + " must be sleepy! ");
				}
				Date end_time = new Date();
				user_time_ran.put(room_bot.current_standup_user.getMentionName(), end_time.getTime()-room_bot.curr_users_start_time.getTime());
			}
			//reset checks
			room_bot.current_standup_user = null;
			room_bot.did_user_speak = false;
			room_bot.did_user_say_anything = false;
			
			//turn off timers always
			cancelAllTimers();
			//poll_timer.cancel();
			//timeout_timer.cancel();
			
			HipchatUser next_user = room_bot.getNextStandupUser();
			if ( next_user != null )
			{
				//choose a person to go
				//bot.current_standup_user = remaining_users.remove( new Random().nextInt(remaining_users.size()) );		
				sb.append("Your turn @" + next_user.getMentionName());
				hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
				
				//set poll timer
				Date next_poll_time = new Date();
				next_poll_time.setTime(next_poll_time.getTime() + 1000);
				poll_timer = new Timer();				
				poll_timer.schedule(new NextStandupTask(hippy_bot, room_bot, true, false), next_poll_time, room_bot.bot_data.speaking_poll_secs*1000);
				timers.add(poll_timer);
												
				//set 20s timeout
				Date next_user_time = new Date();
				next_user_time.setTime(next_user_time.getTime() + (room_bot.bot_data.max_secs_between_turns*1000));
				timeout_timer = new Timer();
				timeout_timer.schedule(new NextStandupTask(hippy_bot, room_bot, false, false), next_user_time);	
				timers.add(timeout_timer);
				
				room_bot.curr_users_start_time = new Date();
			}
			else
			{				
				//all done
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
			}
		}
	}
	
	private String getFirstName(String name)
	{
		return name.split(" ")[0];
	}

	private void cancelAllTimers()
	{
		while ( timers.size() > 0 )
		{
			timers.remove(0).cancel();
		}
		
	}
}
