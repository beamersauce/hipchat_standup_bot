package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.BotData;
import standup.StandupBot;

public class CommandBlacklist extends StandupCommand
{		
	private static String commandName = "blacklist";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String name =  args.get(0);
			if ( name.charAt(0) != '@' )
			{
				bot.sendMessage("Please use the @<user_name> notation to remove users");
			}
			else
			{
				name = name.substring(1);
				if ( botData.blacklist.contains(name))
				{
					botData.blacklist.remove(name);
					bot.sendMessage("Added: " + name + " back to standup, send message again to blacklist");
				}
				else
				{				
					botData.blacklist.add(name);
					bot.sendMessage("Removed: " + name + " from standup, send message again to re-enable");
				}
			}
		}
		else
		{
			bot.sendMessage("Oops you forgot to send username as an option");
		}		
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("user_name");
		return new StandupBotCommandHelp(help_commands, "prevent this user from being called on during standup");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "Blacklist Users: " + botData.blacklist.toString();
	}
}
