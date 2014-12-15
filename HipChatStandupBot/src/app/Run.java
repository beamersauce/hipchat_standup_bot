package app;

import standupbot.manager.StandupBotManager;

import com.ep.hippyjava.HippyJava;

public class Run
{
	public static void main(String[] args) throws InterruptedException
	{
		HippyJava.runBot(new StandupBotManager());
	}
}
