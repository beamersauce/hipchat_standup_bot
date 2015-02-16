package standupbot.bot;

public class ChangingRoomRunnable implements Runnable
{
	private RoomBot bot = null;
	
	public ChangingRoomRunnable(RoomBot roomBot)
	{
		this.bot = roomBot;
	}

	//@Override
	public void run()
	{
		if ( bot != null )
		{
			bot.changing_room = false;
		}
	}

}
