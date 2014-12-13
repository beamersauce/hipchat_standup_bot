package standupbot.commands;

import java.util.ArrayList;
import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public class CommandEarlyTrigger extends StandupCommand
{		
	private static String commandName = "early_trigger";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		if ( args.size() > 0 )
		{
			String word = args.get(0);
			//test if word is a username
			if ( word.charAt(0) == '@' )
			{
				//try to remove user
				String name = word.substring(1);
				if ( bot.users_early_standup.contains(name))
				{
					bot.users_early_standup.remove(name);
					bot.sendMessage("Removed user: " + word + " from early standup");
				}
				else
				{
					bot.sendMessage("User: " + word + " was not set for early standup");
				}
			}
			else
			{			
				if ( botData.speaking_early_trigger_words.contains(word))
				{
					botData.speaking_early_trigger_words.remove(word);
					bot.sendMessage("Removed early trigger word: " + word);
				}
				else
				{
					botData.speaking_early_trigger_words.add(word);
					bot.sendMessage("Added early trigger word: " + word);
				}
				bot.createSpeakingEarlyTriggerPattern();
			}
		}
		else
		{
			bot.sendMessage("Oops you forgot to send early trigger word as an option");
		}			
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("word");
		return new StandupBotCommandHelp(help_commands, "set words to give your standup early, can be at the begginning or end of a line (e.g. 'early standup ...') or will cancel a user being set for early standup by passing @username");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "Early trigger words: " + botData.speaking_early_trigger_words.toString() );
		sb.append( "\n    Users marked as early (skipping next standup): " + bot.users_early_standup.toString() );
		return sb.toString();
	}
}
