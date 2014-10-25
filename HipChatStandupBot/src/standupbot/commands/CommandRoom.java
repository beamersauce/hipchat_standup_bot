package standupbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import standup.BotData;
import standup.StandupBot;

public class CommandRoom extends StandupCommand
{		
	private static String commandName = "room";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String name = "";
			for ( String piece : args)
				name += piece + " ";
			name = name.substring(0, name.length()-1);	
			try
			{
				StandupBot.changing_room = true;	
				bot.joinRoom(name);
				botData.room_name = name;				
				bot.sendMessage("Hello cylons, I am StandupBot, type /standup to control me.");
				bot.sendMessage("Standups will now be held in this room");
				BotData.saveData(botData);
				Runnable task = new Runnable()
				{
					@Override
					public void run()
					{
						StandupBot.changing_room = false;
					}
				};
				StandupBot.worker.schedule(task, 5, TimeUnit.SECONDS);
			}
			catch (Exception ex)
			{
				bot.sendMessage("Could not find room: " + name);
			}
		}
		else
		{
			bot.sendMessage("Oops you forgot to send room name as an option");
		}	
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("room_name");
		return new StandupBotCommandHelp(help_commands, "set the room to run standup in");
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
