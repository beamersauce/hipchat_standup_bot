package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandSilence extends StandupCommand
{		
	private static String commandName = "silence";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{		
		bot.sendMessage("to be implemented");
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "cancel the next days standup");
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
