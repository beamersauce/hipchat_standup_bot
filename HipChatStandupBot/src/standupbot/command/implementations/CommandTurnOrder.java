package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandTurnOrder extends BotCommand
{		
	private static String commandName = "turn_order";

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		if ( args.size() > 1 )
		{
			String name =  args.get(0);
			if ( name.charAt(0) != '@' )
			{
				hippy_bot.sendMessage("Please use the @<user_name> notation", room_bot.current_room);
			}
			else
			{
				name = name.substring(1);
				String turn_pos_str = args.get(1);
				int turn_pos = Integer.MAX_VALUE;
				try
				{
					turn_pos = Integer.parseInt(turn_pos_str);
				}
				catch (Exception ex)
				{
					hippy_bot.sendMessage("Error parsing turn order position, did you send a number?", room_bot.current_room);
					return;
				}
				
				//STEP 1, remove old turn position
				botData.turn_order.remove(name);
				
				//STEP 2, try to insert in new turn position
				if ( turn_pos >= 0 )
				{
					if ( turn_pos > botData.turn_order.size() )
						turn_pos = botData.turn_order.size();
					botData.turn_order.add(turn_pos, name);
					hippy_bot.sendMessage("Put " + name + " at position " + turn_pos, room_bot.current_room);
				}
				else
				{
					hippy_bot.sendMessage("Removed: " + name + " from turn order", room_bot.current_room);
				}
				
			}
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send username and turn position as an option", room_bot.current_room);
		}	
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("user_name");
		help_commands.add("turn number");
		return new BotCommandHelp(help_commands, "adds a user to a specific turn order if they are online");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "Turn Order: " + botData.turn_order.toString();
	}
}
