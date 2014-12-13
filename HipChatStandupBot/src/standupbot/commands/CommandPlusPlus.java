package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandPlusPlus extends StandupCommand
{		
	private static String commandName = "plusplus";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		botData.enablePlusPlus = !botData.enablePlusPlus;
		BotData.saveData(botData);
		if ( botData.enablePlusPlus )
			bot.sendMessage("PlusPlus Bot is now toggled on, anytime word++ or word-- are used in chat, their score will be incremented/decremented");
		else
			bot.sendMessage("PlusPlus Bot is now toggled off");	
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "turns on or off plusplus bot");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "PlusPlusBot on: " + botData.enablePlusPlus;
	}
}
