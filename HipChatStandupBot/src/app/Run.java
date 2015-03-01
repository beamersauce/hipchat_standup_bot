package app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import standupbot.manager.StandupBotCallable;
import standupbot.manager.StandupBotManager;

import com.ep.hippyjava.HippyJava;
import com.ep.hippyjava.bot.Bot;

public class Run
{
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException
	{
		HippyJava.runBot(new StandupBotManager());
		
		//TODO deal w/ netcode someday
		/*ExecutorService executor = Executors.newSingleThreadExecutor();
		int exitstatus = 99;
		//always try to restart until exit status is 0
		while ( exitstatus != 0 )
		{			
			Future<Integer> future = executor.submit(new StandupBotCallable());
			//block until we get a code back
			exitstatus = future.get();
			
			handleExitStatus(exitstatus);
		}	*/			
	}

	private static void handleExitStatus(int exitstatus) throws IOException
	{
		if ( exitstatus == 1 )
		{
			//network issue
			//block until we have a network connection
			boolean haveConnection = false;
			while ( !haveConnection )
			{
				int timeout = 2000;
				try
				{
					URL url = new URL("http://lifehacker.com");
					URLConnection connection = url.openConnection();
					if ( connection.getContentLength() == -1 )
					{
						System.out.println("Failed to connect to url.");
						Thread.sleep(10000); //wait 1 minute before trying to connect again
					}
					else
					{
						haveConnection = true;
					}
				}
				catch (Exception ex)
				{
					System.out.println("Failed to connect to url.");
					try
					{
						Thread.sleep(10000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //wait 1 minute before trying to connect again
				}
								
			}
		}
	}
}
