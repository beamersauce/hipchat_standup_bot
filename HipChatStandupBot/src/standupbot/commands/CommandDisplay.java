package standupbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import standup.BotData;
import standup.StandupBot;
import standup.StandupBotCommands;

public class CommandDisplay extends StandupCommand
{		
	private static String commandName = "display";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Next Standup will occur at: " + bot.nextRunTime.toString());
		for ( Entry<String, StandupCommand> command : StandupBotCommands.commands.entrySet())
		{
			String message = command.getValue().getDisplayMessage(bot, botData);
			if ( message != null && message.length() > 0 )
				sb.append("\n[" + command.getKey() + "] " + message);
		}
		bot.sendMessage(sb.toString());
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "shows information about the StandupBot's current settings");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return null;
	}
}
