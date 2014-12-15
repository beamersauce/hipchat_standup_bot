package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandTurn extends BotCommand
{		
	private static String commandName = "turn";

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
			String poll = args.get(0);			
			try
			{
				int poll_secs = Integer.parseInt(poll);
				botData.max_secs_between_turns = poll_secs;
				BotData.saveData(botData);
				hippy_bot.sendMessage("Set time between turn timeouts to: " + poll_secs + " secs", room_bot.current_room);
			}
			catch (Exception ex)
			{
				hippy_bot.sendMessage("Error trying to set turn timeout secs to: " + poll, room_bot.current_room);
			}			
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send turn seconds as an option", room_bot.current_room);
		}	
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("seconds");
		return new BotCommandHelp(help_commands, "time between turns if a user does not respond");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "Seconds between turns if user does not respond: " + botData.max_secs_between_turns;
	}
}
