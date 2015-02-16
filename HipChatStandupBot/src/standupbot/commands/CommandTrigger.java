package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandTrigger extends StandupCommand
{		
	private static String commandName = "trigger";
	
	//@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String word = args.get(0);
			if ( botData.speaking_trigger_words.contains(word))
			{
				botData.speaking_trigger_words.remove(word);
				bot.sendMessage("Removed trigger word: " + word);
			}
			else
			{
				botData.speaking_trigger_words.add(word);
				bot.sendMessage("Added trigger word: " + word);
			}
			bot.createSpeakingTriggerPattern();
		}
		else
		{
			bot.sendMessage("Oops you forgot to send trigger word as an option");
		}			
	}

	//@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("word");
		return new StandupBotCommandHelp(help_commands, "set words to end your turn, can be at the begginning or end of a line (e.g. 'today')");
	}

	//@Override
	public String getCommandName()
	{		
		return commandName;
	}

	//@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		return "Turn trigger words: " + botData.speaking_trigger_words.toString();
	}
}
