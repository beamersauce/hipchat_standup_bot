package standupbot.commands;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import standup.BotData;
import standup.StandupBot;

public class CommandVersion extends StandupCommand
{		
	private static String commandName = "version";
	
	@Override
	public void handleCommand(StandupBot bot, List<String> args, BotData botData)
	{
		bot.sendMessage(getDisplayMessage(bot, botData));			
	}

	@Override
	public StandupBotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new StandupBotCommandHelp(help_commands, "displays version number");
	}

	@Override
	public String getCommandName()
	{		
		return commandName;
	}

	@Override
	public String getDisplayMessage(StandupBot bot, BotData botData)
	{
		String version = readVersionNumber();
		return "Version: " + version;
	}
	
	private static final String version_file = "dist/version/Version.properties";
	private String readVersionNumber()
	{		
		String version = null;
		Properties props = new Properties();
		InputStream is = null;
		try
		{
			is = new FileInputStream(version_file);
			props.load(is);
			version = props.getProperty("version");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			version = "error reading version file: " + ex.getMessage();
		}
		return version;
	}
}
