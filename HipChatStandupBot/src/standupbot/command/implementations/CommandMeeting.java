package standupbot.command.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ep.hippyjava.bot.HippyBot;

import standupbot.bot.RoomBot;
import standupbot.command.BotCommand;
import standupbot.command.BotCommandHelp;
import standupbot.data_model.BotData;

public class CommandMeeting extends BotCommand
{		
	private static String commandName = "meeting";
	
	//@Override
	public String getCommandName()
	{		
		return commandName;
	}
	
	private String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private String getDaysString(Set<Integer> days_to_run)
	{
		StringBuilder sb = new StringBuilder();
		for ( int day_int : days_to_run )
		{
			if ( day_int <= days.length )
			{
				sb.append(", " + days[day_int]);
			}
		}
		return sb.substring(2);
	}

	//@Override
	public void handleCommand(HippyBot hippy_bot, RoomBot room_bot,
			List<String> args, BotData botData)
	{
		if ( args.size() > 2 )
		{
			String days = args.get(0);
			String hour = args.get(1);
			String minute = args.get(2);
			
			botData.days_to_run.clear();
			for ( char c : days.toCharArray() )
			{
				try
				{
					int day = Integer.parseInt(c + "");
					if ( day >= 0 && day <= 6)
						botData.days_to_run.add(day);
				}
				catch (Exception ex)
				{
					//just ignore bad things					
				}
			}
			
			try
			{
				int houri = Integer.parseInt(hour);
				if ( houri >= 0 && houri <= 23)
					botData.hour_to_run = houri;
			}
			catch (Exception ex)
			{
				//just ignore bad things				
			}
			
			try
			{
				int minutei = Integer.parseInt(minute);
				if ( minutei >= 0 && minutei <= 59)
					botData.minute_to_run = minutei;
			}
			catch (Exception ex)
			{
				//just ignore bad things				
			}
				
			room_bot.scheduleStandup(false, -1);			
		}
		else
		{
			hippy_bot.sendMessage("Oops you forgot to send enough args as an option", room_bot.current_room);
		}
	}

	//@Override
	public BotCommandHelp getHelpMessage()
	{
		List<String> help_commands = new ArrayList<String>();
		help_commands.add("days");
		help_commands.add("hour");
		help_commands.add("minute");
		return new BotCommandHelp(help_commands, "set time of day to conduct standup meeting, for days put 0123456 corresponding to day of week you want, 0 is sunday");
	}

	//@Override
	public String getDisplayMessage(HippyBot hippy_bot, RoomBot room_bot,
			BotData botData)
	{
		return "Standup runs at: " + getDaysString(botData.days_to_run) + " at " + botData.hour_to_run + ":" + botData.minute_to_run;
	}
	
}
