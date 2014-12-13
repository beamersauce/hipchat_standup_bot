package standupbot.data_model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.gson.Gson;

public class BotDataManager
{
	private final static String SAVE_FILE_NAME_PREFIX = "data/standupbot_data_";
	private final static String SAVE_FILE_NAME_MASTER = "data/standupbot_data.txt";
	
	public static boolean saveMasterData(MasterData data)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(SAVE_FILE_NAME_MASTER);
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
	
	public static MasterData loadMasterData()
	{
		//try to get data from file
		MasterData data = null;
		try
		{
			FileInputStream fis = new FileInputStream(SAVE_FILE_NAME_MASTER);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			String json = (String)obj;
			data = new Gson().fromJson(json, MasterData.class);
			ois.close();
		}
		catch (Exception ex)
		{
			//error
		}
		//file didn't exists, create default data object
		if ( data == null )
			data = new MasterData();
		return data;
	}
	
	public static boolean saveData(String room_name, BotData data)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(SAVE_FILE_NAME_PREFIX + room_name + ".txt");
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
	
	public static BotData loadData(String room_name)
	{
		//try to get data from file
		BotData data = null;
		try
		{
			FileInputStream fis = new FileInputStream(SAVE_FILE_NAME_PREFIX + room_name + ".txt");
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
		//file didn't exists, create default data object
		if ( data == null )
			data = new BotData();
		return data;
	}
}
