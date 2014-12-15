package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandWarning extends BotCommand
{		
	private static String commandName = "warning";

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String minute = args.get(0);
						
			try
			{
				int min = Integer.parseInt(minute);
				if ( min > 0 )
					botData.warning_minutes = min;
			}
			catch (Exception ex)
			{
				//just ignore bad things					
			}
			
			room_bot.handleWarningCommand();
			
			hippy_bot.sendMessage("Will warn users " + botData.warning_minutes + " minutes before standup", room_bot.current_room);
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send enough args as an option", room_bot.current_room);
		}
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("minute");
		return new BotCommandHelp(help_commands, "set how many minutes ahead of standup you'd like a warning, set to 0 for no warning");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		if ( botData.warning_minutes > 0 )
			return "Will warn users " + botData.warning_minutes + " minutes before next standup";
		else
			return "No warning ahead of standup currently";
	}	
}
