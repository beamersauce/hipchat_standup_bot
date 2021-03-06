package standup;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.ep.hippyjava.model.HipchatUser;


public class NextStandupTask extends TimerTask
{
	StandupBot bot = null;	
	Timer timer = new Timer();
	Timer timeout_timer = new Timer();
	Timer poll_timer = new Timer();
	boolean isPoll = false;
	private static List<Timer> timers = new ArrayList<Timer>();	
	private static Map<String, Long> user_time_ran = null;
	
	public NextStandupTask(StandupBot bot, boolean isPoll, boolean newRun)
	{
		this.bot = bot;
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
		if ( (isPoll && bot.did_user_speak) || (!isPoll))
		{
			StringBuilder sb = new StringBuilder();
			if ( bot.current_standup_user != null )
			{
				if ( bot.did_user_speak )
				{
					sb.append("Thanks " + getFirstName(bot.current_standup_user.getName()) + "! ");					
				}
				else
				{					
					if ( bot.did_user_say_anything )
						sb.append(getFirstName(bot.current_standup_user.getName()) + " spoke but didn't say terminating word, burned on a technicality! ");
					else
						sb.append(getFirstName(bot.current_standup_user.getName()) + " must be sleepy! ");
				}
				Date end_time = new Date();
				user_time_ran.put(bot.current_standup_user.getMentionName(), end_time.getTime()-bot.curr_users_start_time.getTime());
			}
			//reset checks
			bot.current_standup_user = null;
			bot.did_user_speak = false;
			bot.did_user_say_anything = false;
			
			//turn off timers always
			cancelAllTimers();
			//poll_timer.cancel();
			//timeout_timer.cancel();
			
			HipchatUser next_user = bot.getNextStandupUser();
			if ( next_user != null )
			{
				//choose a person to go
				//bot.current_standup_user = remaining_users.remove( new Random().nextInt(remaining_users.size()) );		
				sb.append("Your turn @" + next_user.getMentionName());
				bot.sendMessage(sb.toString());
				
				//set poll timer
				Date next_poll_time = new Date();
				next_poll_time.setTime(next_poll_time.getTime() + 1000);
				poll_timer = new Timer();				
				poll_timer.schedule(new NextStandupTask(bot, true, false), next_poll_time, bot.botData.speaking_poll_secs*1000);
				timers.add(poll_timer);
												
				//set 20s timeout
				Date next_user_time = new Date();
				next_user_time.setTime(next_user_time.getTime() + (bot.botData.max_secs_between_turns*1000));
				timeout_timer = new Timer();
				timeout_timer.schedule(new NextStandupTask(bot, false, false), next_user_time);	
				timers.add(timeout_timer);
				
				bot.curr_users_start_time = new Date();
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
				bot.users_early_standup.clear();
				bot.sendMessage(sb.toString());
				bot.endStandup();		
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
