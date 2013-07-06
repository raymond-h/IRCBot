package se.kayarr.ircbot.users;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class UserManager {
	
	//*** This needs to be replaced to make each bot instance have a separate UserManager
	private static final UserManager instance = new UserManager();
	
	public static UserManager get() {
		return instance;
	}
	//***
	
	private boolean initialized = false;
	
	public void initialize() {
		if(initialized) return;
		initialized = true;
			
		CommandManager.get().newCommand()
			.addAlias("user-data")
			.helpMessage("Saves or loads the current user data to disk as JSON.")
			.handler(new CommandHandler() {
				@Override
				public void onHandleCommand(PircBotX bot, Channel channel, User user,
						String command, String parameters) {
					try {
						if(parameters.equalsIgnoreCase("save")) {
							bot.sendMessage(channel, "Saving to userdata.json");
							saveUserData();
							bot.sendMessage(channel, "Done");
						}
						else if(parameters.equalsIgnoreCase("load")) {
							bot.sendMessage(channel, "Loading from userdata.json");
							loadUserData();
							bot.sendMessage(channel, "Done");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						bot.sendMessage(channel, "Error occured when saving user data: " + e.getClass().getCanonicalName() + ": " + e.getMessage());
					}
				}
			})
			.add();
			
		try {
			loadUserData();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an error while loading user data from userdata.json");
		}
		
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Saving user data to file...");
					saveUserData();
					System.out.println("Done");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 5, 10, TimeUnit.MINUTES);
	}
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private Table<String, String, UserData> userData = HashBasedTable.create();
	
	public UserData getUserData(String user, String key) {
		if(!hasUserData(user, key)) {
			UserData newData = new UserData();
			putUserData(user, key, newData);
			return newData;
		}
		
		return userData.get(user, key);
	}
	
	public void putUserData(String user, String key, UserData data) {
		userData.put(user, key, data);
	}
	
	public boolean hasUser(String user) {
		return userData.containsRow(user);
	}
	
	public boolean hasUserData(String user, String key) {
		return userData.contains(user, key);
	}

	private static final Type tableType = new TypeToken<Table<String,String,UserData>>(){}.getType();
	
	private void saveUserData() throws IOException {
		Gson gson = new GsonBuilder()
					.registerTypeAdapter(tableType, new TableJsonSerializer())
					.registerTypeAdapter(UserData.class, new UserData.TypeAdapter())
					.create();
		
		FileWriter out = new FileWriter("user_data.json");
		
		out.write(gson.toJson(userData, tableType));
		
		out.close();
	}
	
	private void loadUserData() throws IOException {
		Gson gson = new GsonBuilder()
					.registerTypeAdapter(tableType, new TableJsonSerializer())
					.registerTypeAdapter(UserData.class, new UserData.TypeAdapter())
					.create();
		
		FileReader in = new FileReader("user_data.json");
		
		userData = gson.fromJson(in, tableType);
		
		in.close();
	}
	
	private static class TableJsonSerializer
				implements JsonSerializer<Table<String,String,UserData>>, JsonDeserializer<Table<String,String,UserData>> {

		@Override
		public JsonElement serialize(Table<String, String, UserData> src,
				Type typeOfSrc, JsonSerializationContext context) {
			
			return context.serialize(src.rowMap());
		}

		@Override
		public Table<String, String, UserData> deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			
			Table<String, String, UserData> userDataTable = HashBasedTable.create();
			
			JsonObject root = json.getAsJsonObject();
			
			for(Entry<String,JsonElement> userEntry : root.entrySet()) {
				
				JsonObject userKeys = userEntry.getValue().getAsJsonObject();
				
				for(Entry<String,JsonElement> keyEntry : userKeys.entrySet()) {
					
					String user = userEntry.getKey();
					String key = keyEntry.getKey();
					UserData userData = context.deserialize(keyEntry.getValue(), UserData.class);
					
					userDataTable.put(user, key, userData);
				}
			}
			
			return userDataTable;
		}
		
	}
	
}
