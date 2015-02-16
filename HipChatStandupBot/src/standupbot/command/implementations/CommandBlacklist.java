package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandBlacklist extends BotCommand
{		
	private static String commandName = "blacklist";

	//@Override
	public String getCommandName()
	{		
		return commandName;
	}

	//@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String name =  args.get(0);
			if ( name.charAt(0) != '@' )
			{
				hippy_bot.sendMessage("Please use the @<user_name> notation to remove users", room_bot.current_room);
			}
			else
			{
				name = name.substring(1);
				if ( botData.blacklist.contains(name))
				{
					botData.blacklist.remove(name);
					hippy_bot.sendMessage("Added: " + name + " back to standup, send message again to blacklist", room_bot.current_room);
				}
				else
				{				
					botData.blacklist.add(name);
					hippy_bot.sendMessage("Removed: " + name + " from standup, send message again to re-enable", room_bot.current_room);
				}
			}
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send username as an option", room_bot.current_room);
		}	
	}

	//@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("user_name");
		return new BotCommandHelp(help_commands, "prevent this user from being called on during standup");
	}

	//@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "Blacklist Users: " + botData.blacklist.toString();
	}
}
