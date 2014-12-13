package standupbot.commands;

import java.util.List;

import standup.StandupBot;
import standupbot.data_model.BotData;

public interface IStandupBotCommand
{	
	public void handleCommand(StandupBot bot, List<String> args, BotData botData);
	public StandupBotCommandHelp getHelpMessage();
	public String getCommandName();
	public String getDisplayMessage(StandupBot bot, BotData botData);
}
