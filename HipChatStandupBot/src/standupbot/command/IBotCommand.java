package standupbot.command;

import java.util.List;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.data_model.BotData;

public interface IBotCommand
{	
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot, List<String> args, BotData botData);
	public BotCommandHelp getHelpMessage();
	public String getCommandName();
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot, BotData botData);
}
