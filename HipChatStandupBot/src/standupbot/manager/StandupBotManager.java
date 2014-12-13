package standupbot.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import standupbot.bot.RoomBot;
import standupbot.data_model.BotData;
import standupbot.data_model.BotDataManager;
import standupbot.data_model.MasterData;
import standupbot.data_model.RoomMessage;
import standupbot.util.Utils;

import com.ep.hippyjava.bot.HippyBot;
import com.ep.hippyjava.model.Room;

public class StandupBotManager extends HippyBot
{
	private static MasterData master_data;
	private static Map<String, RoomBot> room_bots;
	
	@Override
	public void onLoad()
	{
		//DEBUG
		Utils.joinRoom(this, "44941_bot_test");
		
		//TODO
		//1. Load up previous data
		master_data = BotDataManager.loadMasterData();	
		
		
		//2. Start a bot thread for each room in data room lists	
		room_bots = new HashMap<String, RoomBot>();
		for ( String room_name : master_data.room_names )
		{			
			startBot(room_name, true);
		}		
	}
	
	@Override
	public void receiveMessage(String message, String from, Room room)
	{
		//parse the message to see if we need to handle it, if not send off to appropriate room
		//TODO
		//1. parse message to see if we need to deal with it
		//2. if not send to room, let room deal with it
		System.out.println("message: " + message + " from: " + from + " room: " + room.getXMPPName());
		//ignore messages from ourself
		if ( !from.equals(this.nickname()) )
		{
			RoomMessage room_message = new RoomMessage(message, from, room);
			//if message is a sudo message, handle it here
			if ( !handleSudoMessage(room_message) )
			{
				//otherwise send it to the room's bot if it exists
				handleRoomMessage(room_message);
			}
		}
	}	
	
	private void handleRoomMessage(RoomMessage room_message)
	{		
		RoomBot room_bot = room_bots.get(room_message.room.getXMPPName());
		if ( room_bot != null  )
		{
			room_bot.message_queue.offer(room_message);
		}
		else
		{
			//DEBUG remove this
			System.out.println("no room bot for room: " + room_message.room.getXMPPName());
		}
	}

	private boolean handleSudoMessage(RoomMessage room_message)
	{
		//check if it is a sudo message
		if ( room_message.message.startsWith("/standup sudo") )
		{
			//check if this user is allowed to make sudo calls
			if ( isAllowedSudo(room_message.from) )
			{
				//handle sudo call
				System.out.println("handling sudo call");
				
				//TODO make some framework elsewhere to handle this (try to reuse same RoomBotCommands framework?)
				parseSudo(room_message.message, room_message.room);
			}
			else
			{
				//handle unauth sudo call
				super.sendMessage(room_message.from + " is not a sudo user, you cannot make sudo calls!", room_message.room);				
			}
			return true;
		}
		
		return false;
	}

	private boolean isAllowedSudo(String from)
	{
		//TODO check a list of privileged users?
		return true;
	}
	
	private void parseSudo(String message, Room room)
	{
		if ( message != null )
		{
			String[] splits = message.split(" ");
			if ( splits.length > 0 )
			{
				String command = "help";
				if ( splits.length > 2 )
					command = splits[2];
				List<String> args = Arrays.asList(splits);
				int start_pos = 3;
				if ( start_pos > args.size())
					start_pos = args.size();
				args = args.subList(start_pos, args.size());			
				parseCommands(command, room, args);
			}
		}
	}
	
	private void parseCommands(String command, Room room, List<String> args)
	{	
		if ( command == null )
			command = "";
		command = command.toLowerCase();
		if ( command.equals("room") && args.size() > 0 )
		{
			String name = "";
			for ( String piece : args)
				name += piece + " ";
			name = name.substring(0, name.length()-1);
			
			//add a room bot to given room name or remove it if one already exists in that room
			handleRoomRequest(name, room);							
		}
		else
		{		
			//is just /standup sudo, display help
			this.sendMessage("Sudo commands:\n[room] <room name> <regular|summary> - adds/removes a standup bot in that room", room);			
		}
			
	}
	
	private void handleRoomRequest(String room_name, Room room)
	{		
		String room_xmpp_name = findRoomXMPPName(room_name);
		if ( room_xmpp_name != null )
		{
			//check if we currently have a bot in that room
			if ( master_data.room_names.contains(room_xmpp_name) )
			{
				//remove bot from this room
				stopBot(room_xmpp_name);
			}
			else
			{
				//create a new bot in this room
				startBot(room_xmpp_name, false);
			}
			BotDataManager.saveMasterData(master_data);
		}
		else
		{
			super.sendMessage("Couldn't find room by name: " + room_name, room);
		}
	}
	
	/**
	 * Stupid hippy java doesn't have a way to get this so we need to try and look for it
	 * 
	 * @return
	 */
	private String findRoomXMPPName(String room_name)
	{
		for ( Room room : super.getRooms() )
		{
			//TODO I don't think it can find rooms unless its the correct case, so
			//no reason to convert to lowercase here
			if ( room.getTrueName().equals(room_name) || room.getXMPPName().equals(room_name) )
			{
				return room.getXMPPName();
			}
		}
		
		//we aren't already connected to the room, so try to find it
		try
		{
			super.joinRoom(room_name);
		}
		catch (Exception ex)
		{
			return null;
		}
		
		//try again :( now that we might be in the room
		for ( Room room : super.getRooms() )
		{
			//TODO I don't think it can find rooms unless its the correct case, so
			//no reason to convert to lowercase here
			if ( room.getTrueName().equals(room_name) || room.getXMPPName().equals(room_name) )
			{
				return room.getXMPPName();
			}
		}
		return null;
	}

	private void startBot(String room_name, boolean startup)
	{
		System.out.println("starting bot in room: " + room_name);
		if ( !startup )
			master_data.room_names.add(room_name);
		Utils.joinRoom(this, room_name);
		BotData bot_data = BotDataManager.loadData(room_name); 
		RoomBot bot = new RoomBot(this, bot_data, room_name);
		new Thread(bot).start();
		room_bots.put(room_name, bot);
	}
	
	private void stopBot(String room_name)
	{
		master_data.room_names.remove(room_name);
		RoomBot bot = room_bots.get(room_name);
		bot.shutdown();
		
	}

	@Override
	public String nickname()
	{
		return "Standup Bot";
	}
	
	@Override
	public String password()
	{		
		return "123qwe";
	}
	
	@Override
	public String username()
	{		
		return "44941_1021361@chat.hipchat.com";
	}

	@Override
	public String apiKey()
	{		
		return "8e42d322e59c7e66229ba5cda30be1";
	}

}
