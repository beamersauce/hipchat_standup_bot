package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandSummary extends BotCommand
{		
	private static String commandName = "summary";

	//@Override
	public String getCommandName()
	{		
		return commandName;
	}

	//@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		botData.isSummary = !botData.isSummary;
		BotData.saveData(botData);
		if ( botData.isSummary )
			hippy_bot.sendMessage("Bot is now a summary bot, will give a summary of all other standups at runtime", room_bot.current_room);
		else
			hippy_bot.sendMessage("Bot is now a regular bot, will perform a standup at runtime", room_bot.current_room);	
	}

	//@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new BotCommandHelp(help_commands, "switches bot between summary or regular standup");
	}

	//@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		String type = "regular";
		if ( botData.isSummary )
			type = "summary";
		return "This standup is a '" + type + "' standup";
				
	}
}
