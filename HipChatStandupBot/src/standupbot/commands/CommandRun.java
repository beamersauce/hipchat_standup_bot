package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.BotData;
import standup.StandupBot;

public class CommandRun extends StandupCommand
{		
	private static String commandName = "run";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		//Run a standup now		
		bot.scheduleStandup(false, 0);
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "runs a standup immediately");
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
