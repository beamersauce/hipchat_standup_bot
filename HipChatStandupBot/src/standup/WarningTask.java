package standup;
import java.util.TimerTask;


public class WarningTask extends TimerTask
{
	
	StandupBot bot = null;	
	
	
	public WarningTask(StandupBot standupBot)
	{
		bot = standupBot;
	}

	@Override
	public void run()
	{
		//run a standup
		StringBuilder sb = new StringBuilder();
		sb.append("@all Standup will be conducted in " + bot.botData.warning_minutes + " minutes!");
		sb.append("\nCurrently blacklist users are: " + bot.botData.blacklist.toString());
		bot.sendMessage(sb.toString());				
	}	
}


