package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandSilentStart extends StandupCommand
{		
	private static String commandName = "silentstart";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		botData.silentStart = !botData.silentStart;
		if ( botData.silentStart )
			bot.sendMessage("Enabled silent start");
		else
			bot.sendMessage("Disabled silent start");
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "turns on/off startup text when bot first turns on");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "SilentStart enabled: " + botData.silentStart;
	}
}
