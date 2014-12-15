package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandWarning extends StandupCommand
{		
	private static String commandName = "warning";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
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
			
			bot.handleWarningCommand();
			
			bot.sendMessage("Will warn users " + botData.warning_minutes + " minutes before standup");
		}
		else
		{
			bot.sendMessage("Oops you forgot to send enough args as an option");
		}		
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("minute");
		return new StandupBotCommandHelp(help_commands, "set how many minutes ahead of standup you'd like a warning, set to 0 for no warning");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		if ( botData.warning_minutes > 0 )
			return "Will warn users " + botData.warning_minutes + " minutes before next standup";
		else
			return "No warning ahead of standup currently";
	}	
}
