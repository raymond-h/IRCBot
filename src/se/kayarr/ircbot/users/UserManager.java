package se.kayarr.ircbot.users;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserManager {
	
	public static abstract class Data {}
	
	//*** This needs to be replaced to make each bot instance have a separate UserManager
	private static final UserManager instance = new UserManager();
	
	public static UserManager get() {
		return instance;
	}
	//***
	
	private Map<String, Class<? extends Data>> classMappings = new HashMap<>();
	
	private Map<String, Map<String, Data>> userData = new HashMap<>();
	
	private static String getKeyFromClass(Class<? extends Data> clazz) {
		try {
			Field keyField = clazz.getField("KEY");
			
			if( 
					Modifier.isStatic( keyField.getModifiers() ) &&
					Modifier.isFinal( keyField.getModifiers() ) &&
					keyField.getType() == String.class ) {
				return (String)keyField.get(null);
			}
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void registerDataClass(Class<? extends Data> clazz) {
		String key = getKeyFromClass(clazz);
		
		if(key != null) registerDataClass(clazz, key);
		else {
			//TODO Throw a RuntimeException
		}
	}
	
	public void registerDataClass(Class<? extends Data> clazz, String key) {		
		classMappings.put(key, clazz);
		
		System.out.println("*** Registered " + clazz.getCanonicalName() + " to key '" + key + "'");
	}
	
	public <T extends Data> T getUserData(String user, Class<T> clazz) {
		String key = getKeyFromClass(clazz);
		
		if(key != null) {
			return clazz.cast( getUserData(user, key) );
		}
		else return null;
	}
	
	public Data getUserData(String user, String key) {
		if( !hasUserData(user, key) )
			try {
				Class<? extends Data> clazz = classMappings.get(key);
				Data inst = clazz.newInstance();
				putUserData(user, key, inst);
				return inst;
			}
			catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		
		return userData.get(user).get(key);
	}
	
	public void putUserData(String user, String key, Data data) {
		if( !userData.containsKey(user) ) userData.put(user, new HashMap<String, Data>());
		
		userData.get(user).put(key, data);
	}
	
	public boolean hasUser(String user) {
		return userData.containsKey(user);
	}
	
	public boolean hasUserData(String user, String key) {
		return hasUser(user) && userData.get(user).containsKey(key);
	}
	
	public String serializeTest() {
		Gson gson = new Gson();
		String json = gson.toJson( userData );
		
		System.out.println( "My JSON is currently " + json );
		
		return json;
	}
	
	public void deserializeTest(String json) {
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		JsonObject users = parser.parse(json).getAsJsonObject();
		for(Entry<String,JsonElement> userEntry : users.entrySet()) {
			String user = userEntry.getKey();
			
			JsonObject keys = userEntry.getValue().getAsJsonObject();
			
			for(Entry<String,JsonElement> dataEntry : keys.entrySet()) {
				String key = dataEntry.getKey();
				Class<? extends Data> clazz = classMappings.get(key);
				
				System.out.println("Found data for user '" + user + "', with key '" + key + "' and data class: " + clazz.getCanonicalName());
				
				JsonElement dataElement = dataEntry.getValue();
				
				Data data = gson.fromJson(dataElement, clazz);
				
				putUserData(user, key, data);
			}
		}
	}
	
}
