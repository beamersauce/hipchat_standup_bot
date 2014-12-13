package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandPoll extends StandupCommand
{		
	private static String commandName = "poll";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String poll = args.get(0);			
			try
			{
				int poll_secs = Integer.parseInt(poll);
				botData.speaking_poll_secs = poll_secs;
				BotData.saveData(botData);
				bot.sendMessage("Set time between poll checks to: " + poll_secs + " secs");
			}
			catch (Exception ex)
			{
				bot.sendMessage("Error trying to set poll secs to: " + poll);
			}			
		}
		else
		{
			bot.sendMessage("Oops you forgot to send poll seconds as an option");
		}		
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("seconds");
		return new StandupBotCommandHelp(help_commands, "time between checks if a user has given their standup");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "Seconds between checks if a user has spoke: " + botData.speaking_poll_secs;
	}
}
