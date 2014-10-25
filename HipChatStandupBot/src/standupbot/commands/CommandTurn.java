package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.BotData;
import standup.StandupBot;

public class CommandTurn extends StandupCommand
{		
	private static String commandName = "turn";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String poll = args.get(0);			
			try
			{
				int poll_secs = Integer.parseInt(poll);
				botData.max_secs_between_turns = poll_secs;
				BotData.saveData(botData);
				bot.sendMessage("Set time between turn timeouts to: " + poll_secs + " secs");
			}
			catch (Exception ex)
			{
				bot.sendMessage("Error trying to set turn timeout secs to: " + poll);
			}			
		}
		else
		{
			bot.sendMessage("Oops you forgot to send turn seconds as an option");
		}		
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("seconds");
		return new StandupBotCommandHelp(help_commands, "time between turns if a user does not respond");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "Seconds between turns if user does not respond: " + botData.max_secs_between_turns;
	}
}
