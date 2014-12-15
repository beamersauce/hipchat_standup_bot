package standupbot.meeting;

import java.util.List;
import java.util.TimerTask;

import standupbot.bot.RoomBot;
import standupbot.manager.StandupBotManager;
import com.ep.hippyjava.bot.HippyBot;

public class SummaryTask extends TimerTask
{
	private RoomBot room_bot = null;
	private HippyBot hippy_bot = null;
	
	public SummaryTask(HippyBot hippy_bot, RoomBot room_bot)
	{
		this.room_bot = room_bot;
		this.hippy_bot = hippy_bot;
	}

	@Override
	public void run()
	{
		//run a standup
		StringBuilder sb = new StringBuilder();
		sb.append("@all Time for summary standup!");
		List<String> summaries = StandupBotManager.getSummaries();
		for ( String summary : summaries )
		{
			if ( summary != null && !summary.isEmpty() )
			{
				sb.append("\n").append(summary);
			}
		}				
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
		room_bot.scheduleStandup(false, -1);
	}
}
