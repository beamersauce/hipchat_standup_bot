package standup;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import standupbot.commands.CommandBlacklist;
import standupbot.commands.CommandDisplay;
import standupbot.commands.CommandMeeting;
import standupbot.commands.CommandPlusPlus;
import standupbot.commands.CommandPoll;
import standupbot.commands.CommandRoom;
import standupbot.commands.CommandRun;
import standupbot.commands.CommandSilence;
import standupbot.commands.CommandSilentStart;
import standupbot.commands.CommandTrigger;
import standupbot.commands.CommandTurn;
import standupbot.commands.CommandTurnOrder;
import standupbot.commands.CommandVersion;
import standupbot.commands.StandupBotCommandHelp;
import standupbot.commands.StandupCommand;


public class StandupBotCommands
{
	public static SortedMap<String, StandupCommand> commands = null;
	
	public static void handleCommand(String message, StandupBot bot, BotData botdata)
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
				parseCommands(command, bot, args, botdata);
			}
		}
	}
	
	private static void parseCommands(String command, StandupBot bot, List<String> args, BotData botData)
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
				handleHelp(bot);
			}
			else if ( commands.containsKey(command))
			{				
				commands.get(command).handleCommand(bot, args, botData);				
			}
			else
			{
				showUnknownCommand(command, bot);
			}
		} 
		catch (Exception ex)
		{
			bot.sendMessage("Error creating instance of command: " + ex.getMessage());
		}
		
		//save data back in case file changed
		BotData.saveData(botData);		
	}

	private static void handleHelp(StandupBot bot) throws InstantiationException, IllegalAccessException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("StandupBot Help");		
		sb.append("\nType /standup <command> <options> to issue commands");
		for ( Entry<String, StandupCommand> entry : commands.entrySet())
		{
			sb.append("\n" + entry.getKey());
			StandupBotCommandHelp help = entry.getValue().getHelpMessage();
			for ( String arg : help.arg_names)
				sb.append(" <" + arg + ">");
			sb.append(" - " + help.help_message);
		}
				
		bot.sendMessage(sb.toString());
	}

	private static void showUnknownCommand(String command, StandupBot bot)
	{
		bot.sendMessage("I'm sorry Dave, I'm afraid I can't do that.\nUnknown command '" + command +"'");
	}
	
	private static void init()
	{
		commands = new TreeMap<String, StandupCommand>();
		//TODO figure out a way to load these up w/o this method
		CommandRun c12 = new CommandRun();
		commands.put(c12.getCommandName(), c12);
		CommandVersion c11 = new CommandVersion();
		commands.put(c11.getCommandName(), c11);
		CommandSilence c0 = new CommandSilence();
		commands.put(c0.getCommandName(), c0);
		CommandBlacklist c1 = new CommandBlacklist();
		commands.put(c1.getCommandName(), c1);
		CommandDisplay c2 = new CommandDisplay();		
		commands.put(c2.getCommandName(), c2);
		CommandMeeting c3 = new CommandMeeting();
		commands.put(c3.getCommandName(), c3);
		CommandPlusPlus c4 = new CommandPlusPlus();
		commands.put(c4.getCommandName(), c4);
		CommandPoll c5 = new CommandPoll();
		commands.put(c5.getCommandName(), c5);
		CommandRoom c6 = new CommandRoom();
		commands.put(c6.getCommandName(), c6);
		CommandTrigger c7 = new CommandTrigger();
		commands.put(c7.getCommandName(), c7);
		CommandTurn c8 = new CommandTurn();
		commands.put(c8.getCommandName(), c8);
		CommandTurnOrder c9 = new CommandTurnOrder();
		commands.put(c9.getCommandName(), c9);
		CommandSilentStart c10 = new CommandSilentStart();
		commands.put(c10.getCommandName(), c10);
		
	}
}
