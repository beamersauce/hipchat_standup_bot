package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandEarlyTrigger extends BotCommand
{		
	private static String commandName = "early_trigger";

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
			String word = args.get(0);
			//test if word is a username
			if ( word.charAt(0) == '@' )
			{
				//try to remove user
				String name = word.substring(1);
				if ( room_bot.users_early_standup.contains(name))
				{
					room_bot.users_early_standup.remove(name);
					hippy_bot.sendMessage("Removed user: " + word + " from early standup", room_bot.current_room);
				}
				else
				{
					hippy_bot.sendMessage("User: " + word + " was not set for early standup", room_bot.current_room);
				}
			}
			else
			{			
				if ( botData.speaking_early_trigger_words.contains(word))
				{
					botData.speaking_early_trigger_words.remove(word);
					hippy_bot.sendMessage("Removed early trigger word: " + word, room_bot.current_room);
				}
				else
				{
					botData.speaking_early_trigger_words.add(word);
					hippy_bot.sendMessage("Added early trigger word: " + word, room_bot.current_room);
				}
				room_bot.createSpeakingEarlyTriggerPattern();
			}
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send early trigger word as an option", room_bot.current_room);
		}	
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("word");
		return new BotCommandHelp(help_commands, "set words to give your standup early, can be at the begginning or end of a line (e.g. 'early standup ...') or will cancel a user being set for early standup by passing @username");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "Early trigger words: " + botData.speaking_early_trigger_words.toString() );
		sb.append( "\n    Users marked as early (skipping next standup): " + room_bot.users_early_standup.toString() );
		return sb.toString();
	}
}
