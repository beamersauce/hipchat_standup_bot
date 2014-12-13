package standupbot.util;

import java.util.List;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.HipchatUser;
import com.ep.hippyjava.model.Room;

public class Utils
{
	public static String listOfUsers(List<HipchatUser> users)
	{
		StringBuilder sb = new StringBuilder();		
		int count = 0;
		for ( HipchatUser user : users )
		{
			if ( count > 0 )
				sb.append(", ");
			sb.append(user.getName() );
			count++;
		}
		return sb.toString();
	}

	public static String getUsersMentionName(String name, HippyBot hippy_bot)
	{
		for ( HipchatUser user : hippy_bot.getUsers() )
		{
			if ( user.getName().equals(name))
			{
				return user.getMentionName();
			}
		}
		return null;
	}
	
	/**
	 * Checks to see if we are already in the room (otherwise this crazy thing will join multiple
	 * times and receive a message for each time they've joined a room.
	 * 
	 * @param hippy_bot
	 * @param room_name
	 * @return
	 */
	public static void joinRoom(HippyBot hippy_bot, String room_name)
	{
		for ( Room room : hippy_bot.getRooms() )
		{
			if ( room.getXMPPName().equals(room_name) )
			{
				return;
			}
		}
		hippy_bot.joinRoom(room_name);
	}
}
