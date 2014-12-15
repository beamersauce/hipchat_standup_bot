package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandTrigger extends BotCommand
{		
	private static String commandName = "trigger";	

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
			if ( botData.speaking_trigger_words.contains(word))
			{
				botData.speaking_trigger_words.remove(word);
				hippy_bot.sendMessage("Removed trigger word: " + word, room_bot.current_room);
			}
			else
			{
				botData.speaking_trigger_words.add(word);
				hippy_bot.sendMessage("Added trigger word: " + word, room_bot.current_room);
			}
			room_bot.createSpeakingTriggerPattern();
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send trigger word as an option", room_bot.current_room);
		}	
	}

	@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("word");
		return new BotCommandHelp(help_commands, "set words to end your turn, can be at the begginning or end of a line (e.g. 'today')");
	}

	@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "Turn trigger words: " + botData.speaking_trigger_words.toString();
	}
}
