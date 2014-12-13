package standupbot.command;

import java.util.List;

public class BotCommandHelp
{
	public List<String> arg_names;
	public String help_message;
	
	public BotCommandHelp(List<String> arg_names, String help_message)
	{
		this.arg_names = arg_names;
		this.help_message = help_message;
	}
}
