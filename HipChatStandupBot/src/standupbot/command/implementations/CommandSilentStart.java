package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandSilentStart extends BotCommand
{		
	private static String commandName = "silentstart";

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		botData.silentStart = !botData.silentStart;
		if ( botData.silentStart )
			hippy_bot.sendMessage("Enabled silent start", room_bot.current_room);
		else
			hippy_bot.sendMessage("Disabled silent start", room_bot.current_room);
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new BotCommandHelp(help_commands, "turns on/off startup text when bot first turns on");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "SilentStart enabled: " + botData.silentStart;
	}
}
