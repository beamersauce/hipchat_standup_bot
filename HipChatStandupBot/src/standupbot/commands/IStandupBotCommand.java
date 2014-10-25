package standupbot.commands;

import java.util.List;

import standup.BotData;
import standup.StandupBot;

public interface IStandupBotCommand
{	
	public void handleCommand(StandupBot bot, List<String> args, BotData botData);
	public StandupBotCommandHelp getHelpMessage();
	public String getCommandName();
	public String getDisplayMessage(StandupBot bot, BotData botData);
}
