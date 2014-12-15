package standupbot.data_model;

import com.ep.hippyjava.model.Room;

public class RoomMessage
{
	public String message;
	public Room room;
	public String from;
	
	public RoomMessage(String message, String from, Room room)
	{
		this.message = message;
		this.from = from;
		this.room = room;
	}
}
