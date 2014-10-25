package standup;
import java.util.List;
import java.util.TimerTask;

import standupbot.util.Utils;

import com.ep.hippyjava.model.HipchatUser;


public class StandupTask extends TimerTask
{
	
	StandupBot bot = null;	
	
	private NextStandupTask task = null;
	
	public StandupTask(StandupBot standupBot)
	{
		bot = standupBot;
	}

	@Override
	public void run()
	{
		//run a standup
		StringBuilder sb = new StringBuilder();
		sb.append("@all Time for standup!");
		if ( bot.botData.speaking_trigger_words != null )
		{
			sb.append("\nStart or end a sentence with '" + bot.botData.speaking_trigger_words.toString() + "' to end your turn.");
		}
		bot.sendMessage(sb.toString());
		//1 get users present at start of standup and print out their names for fun
		List<HipchatUser> remaining_users = bot.getStandupUsers();
		String users_string = Utils.listOfUsers(remaining_users);		
		bot.sendMessage("Users present for standup are: " + users_string);
		bot.setStandupParticipants(remaining_users);
		task = new NextStandupTask(bot, false);
		task.run();
		
	}	
}


