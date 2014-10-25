package standup;
import java.net.SocketTimeoutException;

import com.ep.hippyjava.HippyJava;


public class Test
{

	public static void main(String[] args) throws InterruptedException
	{
		//HippyJava.runBot(new StandupBot());
		boolean badError = false;
		while ( !badError )
		{
			try
			{
				System.out.println("Attempting to startup bot");
				runBot();
				System.out.println("Bot stopped cleanly, this shouldn't happen, sleeping for 5 min");
				Thread.sleep(1000*60*5);
			}
			catch (SocketTimeoutException ex)
			{
				//probably a internet disconnect, try again in 5min
				System.out.println("Threw a Socket Timeout Exception, internet probably down, sleeping for 5 min");
				Thread.sleep(1000*60*5);
			}
			catch (Exception ex)
			{
				System.out.println("Threw a different Exception " + ex.getStackTrace());
				
				badError = true;
			}
		}

	}
	
	private static StandupBot bot = new StandupBot();
	private static Thread bot_thread = null;
	public static void runBot() throws SocketTimeoutException, InterruptedException
	{
		bot_thread = new Thread()
		{
			@Override
			public void run()
			{
				HippyJava.runBot(bot);
			}
		};
		bot_thread.start();
		while(bot.getConnection() == null || bot.getConnection().isConnected())
		{
			//wait 60s between connection checks
			Thread.sleep(1000*10);
		}
		throw new SocketTimeoutException("fake one");
		/*
		Thread t = HippyJava.runBotDesync(bot);
		t.start();
		//t.run();
		while ( bot.getConnection() == null || bot.getConnection().isConnected() )
		//while(true)
		{
			//wait 60s between connection checks
			Thread.sleep(1000*60);
		}*/
		
		/*
		//THIS IS THE OLD WAY, NEED TO FIGURE OUT HOW TO GET AROUND THIS
		HippyJava.runBot(new StandupBot());
		//we popped out of while statement meaning we are no longer connected
		throw new SocketTimeoutException("fake one");
		*/
	}

}
