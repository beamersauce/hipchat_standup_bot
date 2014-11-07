package standupbot.util;

import java.util.List;

import com.ep.hippyjava.model.HipchatUser;

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
}
