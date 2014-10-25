package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.BotData;
import standup.StandupBot;

public class CommandVersion extends StandupCommand
{		
	private static String commandName = "version";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		bot.sendMessage(getDisplayMessage(bot, botData));			
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "displays version number");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "Version: TODO";
	}
}
