package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandPlusPlus extends BotCommand
{		
	private static String commandName = "plusplus";

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		botData.enablePlusPlus = !botData.enablePlusPlus;
		BotData.saveData(botData);
		if ( botData.enablePlusPlus )
			hippy_bot.sendMessage("PlusPlus Bot is now toggled on, anytime word++ or word-- are used in chat, their score will be incremented/decremented", room_bot.current_room);
		else
			hippy_bot.sendMessage("PlusPlus Bot is now toggled off", room_bot.current_room);	
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new BotCommandHelp(help_commands, "turns on or off plusplus bot");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "PlusPlusBot on: " + botData.enablePlusPlus;
	}
}
