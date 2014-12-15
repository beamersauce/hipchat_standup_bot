package standupbot.bot;
import java.util.TimerTask;

import com.ep.hippyjava.bot.HippyBot;


public class WarningTask extends TimerTask
{
	
	private RoomBot room_bot = null;
	private HippyBot hippy_bot = null;
	
	
	public WarningTask(HippyBot hippy_bot, RoomBot room_bot)
	{
		this.room_bot = room_bot;
		this.hippy_bot = hippy_bot;
	}

	@Override
	public void run()
	{
		//run a standup
		StringBuilder sb = new StringBuilder();
		sb.append("@all Standup will be conducted in " + room_bot.bot_data.warning_minutes + " minutes!");
		sb.append("\nCurrently blacklist users are: " + room_bot.bot_data.blacklist.toString());
		sb.append("\nCurrently early standup users are: " + room_bot.users_early_standup.toString());
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);				
	}	
}


