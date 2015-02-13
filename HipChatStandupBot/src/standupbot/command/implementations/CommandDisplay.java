package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.command.RoomBotCommands;
import standupbot.data_model.BotData;

public class CommandDisplay extends BotCommand
{		
	private static String commandName = "display";

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("/quote ");
		sb.append("Next Standup will occur at: " + room_bot.nextRunTime.toString());
		for ( Entry<String, BotCommand> command : RoomBotCommands.commands.entrySet())
		{
			String message = command.getValue().getDisplayMessage(hippy_bot, room_bot, botData);
			if ( message != null && message.length() > 0 )
				sb.append("\n[" + command.getKey() + "] " + message);
		}
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new BotCommandHelp(help_commands, "shows information about the StandupBot's current settings");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return null;
	}
}
