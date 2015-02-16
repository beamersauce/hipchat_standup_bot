package standupbot.command.implementations;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandVersion extends BotCommand
{		
	private static String commandName = "version";	

	//@Override
	public String getCommandName()
	{		
		return commandName;
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

	//@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		hippy_bot.sendMessage(getDisplayMessage(hippy_bot, room_bot, botData), room_bot.current_room);
	}

	//@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		return new BotCommandHelp(help_commands, "displays version number");
	}

	//@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		String version = readVersionNumber();
		return "Version: " + version;
	}
}
