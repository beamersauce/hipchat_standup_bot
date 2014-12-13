package standupbot.data_model;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;


public class BotData
{
	private final static String SAVE_DATA_FILENAME = "standupbot_data1.txt";
	public String room_name;
	public Set<String> blacklist;	
	public Set<Integer> days_to_run;
	public int hour_to_run;
	public int minute_to_run;
	public int speaking_poll_secs = 2;
	public int max_secs_between_turns = 40;
	public Set<String> speaking_trigger_words;
	public Set<String> speaking_early_trigger_words;
	public boolean silentStart = true;
	public LinkedList<String> turn_order;
	public int warning_minutes = 0;
	
	//plusplus bot
	public boolean enablePlusPlus = true;
	public Map<String, Long> plusplus_map = new HashMap<String, Long>();
	
	public BotData()
	{
		blacklist = new HashSet<String>();
		turn_order = new LinkedList<String>();
		days_to_run = new HashSet<Integer>( Arrays.asList(1,2,3,4,5) );
		hour_to_run = 9;
		minute_to_run = 30;
		room_name = "44941_bot_test";
		//room_name = "44941_dev_team";
		plusplus_map = new HashMap<String, Long>();
		speaking_trigger_words = new HashSet<String>();
		speaking_trigger_words.add("today");
		speaking_early_trigger_words = new HashSet<String>();
		speaking_early_trigger_words.add("early standup");
		warning_minutes = 0;
	}

	public static BotData loadData()
	{
		//try to get data from file
		BotData data = null;
		try
		{
			FileInputStream fis = new FileInputStream(SAVE_DATA_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			String json = (String)obj;
			data = new Gson().fromJson(json, BotData.class);
			ois.close();
		}
		catch (Exception ex)
		{
			//error
		}
		if ( data == null )
			data = new BotData();
		return data;
	}
	
	public static boolean saveData(BotData data)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(SAVE_DATA_FILENAME);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			String json = new Gson().toJson(data);
			oos.writeObject(json);
			fos.close();
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}		
	}
}
