package standupbot.bot;
import java.util.TimerTask;

import com.ep.hippyjava.bot.HippyBot;


public class StandupTask extends TimerTask
{
	
	private RoomBot room_bot = null;
	private HippyBot hippy_bot = null;
	
	private NextStandupTask task = null;
	
	public StandupTask(HippyBot hippy_bot, RoomBot room_bot)
	{
		this.room_bot = room_bot;
		this.hippy_bot = hippy_bot;
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
		//1 get users present at start of standup and print out their names for fun
		/*List<HipchatUser> remaining_users = bot.getStandupUsers();
		String users_string = Utils.listOfUsers(remaining_users);		
		bot.sendMessage("Users present for standup are: " + users_string);*/
		room_bot.setStandupParticipants();
		task = new NextStandupTask(hippy_bot, room_bot, false, true);
		task.run();
		
	}	
}


