package standupbot.command;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.implementations.CommandBlacklist;
import standupbot.command.implementations.CommandDisplay;
import standupbot.command.implementations.CommandEarlyTrigger;
import standupbot.command.implementations.CommandMeeting;
import standupbot.command.implementations.CommandPlusPlus;
import standupbot.command.implementations.CommandRun;
import standupbot.command.implementations.CommandSilentStart;
import standupbot.command.implementations.CommandSummary;
import standupbot.command.implementations.CommandTrigger;
import standupbot.command.implementations.CommandTurn;
import standupbot.command.implementations.CommandTurnOrder;
import standupbot.command.implementations.CommandVersion;
import standupbot.command.implementations.CommandWarning;
import standupbot.data_model.BotData;
import standupbot.data_model.BotDataManager;


public class RoomBotCommands
{
	public static SortedMap<String, BotCommand> commands = null;
	
	public static void handleCommand(String message, HippyBot hippy_bot, RoomBot room_bot,  BotData botdata)
	{
		if ( message != null )
		{
			String[] splits = message.split(" ");
			if ( splits.length > 0 )
			{
				String command = "help";
				if ( splits.length > 1 )
					command = splits[1];
				List<String> args = Arrays.asList(splits);
				int start_pos = 2;
				if ( start_pos > args.size())
					start_pos = args.size();
				args = args.subList(start_pos, args.size());			
				parseCommands(command, hippy_bot, room_bot, args, botdata);
			}
		}
	}
	
	private static void parseCommands(String command, HippyBot hippy_bot, RoomBot room_bot, List<String> args, BotData botData)
	{
		if ( commands == null )
			init();
		
		if ( command == null || command.length() == 0 )
		{
			//is just /standup, display help
			command = "help";					
		}
		
		command = command.toLowerCase();
		
		try
		{
			if ( command.equals("help") )
			{
				handleHelp(hippy_bot, room_bot);
			}
			else if ( commands.containsKey(command))
			{				
				commands.get(command).handleCommand(hippy_bot, room_bot, args, botData);				
			}
			else
			{
				showUnknownCommand(command, hippy_bot, room_bot);
			}
		} 
		catch (Exception ex)
		{
			hippy_bot.sendMessage("Error creating instance of command: " + ex.getMessage(), room_bot.current_room);
		}
		
		//save data back in case file changed
		BotDataManager.saveData(room_bot.current_room.getXMPPName(), botData);
		//BotData.saveData(botData);		
	}

	private static void handleHelp(HippyBot hippy_bot, RoomBot room_bot) throws InstantiationException, IllegalAccessException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("StandupBot Help");		
		sb.append("\nType /standup <command> <options> to issue commands");
		for ( Entry<String, BotCommand> entry : commands.entrySet())
		{
			sb.append("\n" + entry.getKey());
			BotCommandHelp help = entry.getValue().getHelpMessage();
			for ( String arg : help.arg_names)
				sb.append(" <" + arg + ">");
			sb.append(" - " + help.help_message);
		}
				
		hippy_bot.sendMessage(sb.toString(), room_bot.current_room);
	}

	private static void showUnknownCommand(String command, HippyBot hippy_bot, RoomBot room_bot)
	{
		hippy_bot.sendMessage("I'm sorry Dave, I'm afraid I can't do that.\nUnknown command '" + command +"'", room_bot.current_room);
	}
	
	private static void init()
	{
		//TODO figure out a way to load these up w/o this method??
		//TODO maybe have a second list for master (sudo) commands?
		
		commands = new TreeMap<String, BotCommand>();
		CommandSummary c15 = new CommandSummary();
		commands.put(c15.getCommandName(), c15);
		CommandPlusPlus c4 = new CommandPlusPlus();
		commands.put(c4.getCommandName(), c4);
		CommandMeeting c3 = new CommandMeeting();
		commands.put(c3.getCommandName(), c3);
		CommandDisplay c2 = new CommandDisplay();		
		commands.put(c2.getCommandName(), c2);
		CommandTurn c8 = new CommandTurn();
		commands.put(c8.getCommandName(), c8);
		
		CommandEarlyTrigger c14 = new CommandEarlyTrigger();
		commands.put(c14.getCommandName(), c14);
		CommandWarning c13 = new CommandWarning();
		commands.put(c13.getCommandName(), c13);
		CommandRun c12 = new CommandRun();
		commands.put(c12.getCommandName(), c12);
		CommandVersion c11 = new CommandVersion();
		commands.put(c11.getCommandName(), c11);
		CommandBlacklist c1 = new CommandBlacklist();
		commands.put(c1.getCommandName(), c1);
		CommandTrigger c7 = new CommandTrigger();
		commands.put(c7.getCommandName(), c7);
		
		CommandTurnOrder c9 = new CommandTurnOrder();
		commands.put(c9.getCommandName(), c9);
		CommandSilentStart c10 = new CommandSilentStart();
		commands.put(c10.getCommandName(), c10);
		
	}
}
