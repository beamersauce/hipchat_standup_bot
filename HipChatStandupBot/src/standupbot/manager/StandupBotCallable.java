package standupbot.manager;

import java.net.InetAddress;
import java.util.concurrent.Callable;

import com.ep.hippyjava.HippyJava;
import com.ep.hippyjava.bot.Bot;

public class StandupBotCallable implements Callable<Integer>
{
	/**
	 * Creates a bot and waits for an error/network issue
	 * 
	 * returns 0 for exit fine (intentional close?)
	 * returns 1 for network issue
	 * returns 2 for other issue
	 * 
	 */
	public Integer call() throws Exception
	{
		int exit_code = -1;
		// loop until we manually kill the program
		while ( exit_code < 0 )
		{
			StandupBotManager bot = new StandupBotManager();
			Thread t = HippyJava.runBotDesync(bot);
			t.start();
			while ( bot.getConnection() == null || bot.getConnection().isConnected() )
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (Exception ex)
				{
					System.out.println("Bot crash: " + ex.getMessage());
				}
			}
			//if we got here, the bot is no longer connected, but doesn't shutdown
			System.out.println(bot.getConnection());
			bot.shutdown();
			bot.getConnection().disconnect();
			//t.interrupt();
			//t.stop(); //force shutdown (there's probably some better way to do this)
			exit_code = 1;						
		}
		
		return exit_code;
	}

}
