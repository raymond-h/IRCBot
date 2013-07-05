package se.kayarr.ircbot.users;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class UserData {
	private Map<String, Object> data = new HashMap<>();
	
	public void put(String key, int value) {
		put(key, Integer.valueOf(value));
	}
	
	private void put(String key, Number value) {
		data.put(key, value);
	}
	
	public void put(String key, String value) {
		data.put(key, value);
	}
	
	public int getInt(String key) {
		return getNumber(key).intValue();
	}
	
	private Number getNumber(String key) {
		return (Number) data.get(key);
	}
	
	public String getString(String key) {
		return (String) data.get(key);
	}
	
	public boolean has(String key) {
		return data.containsKey(key);
	}
	
	public UserData defaultValue(String key, int value) {
		setDefaultValue(key, value);
		return this;
	}
	
	public UserData defaultValue(String key, String value) {
		setDefaultValue(key, value);
		return this;
	}
	
	public boolean setDefaultValue(String key, int value) {
		return setDefaultValueImpl(key, value);
	}
	
	public boolean setDefaultValue(String key, String value) {
		return setDefaultValueImpl(key, value);
	}
	
	private boolean setDefaultValueImpl(String key, Object value) {
		if(has(key)) return false;
		
		data.put(key, value);
		return true;
	}
	
	public static class TypeAdapter implements JsonSerializer<UserData>, JsonDeserializer<UserData> {

		@Override
		public UserData deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			UserData inst = new UserData();
			Type dataType = new TypeToken< Map<String,Object> >(){}.getType();
			inst.data = context.deserialize(json, dataType);
			return inst;
		}

		@Override
		public JsonElement serialize(UserData src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			return context.serialize(src.data);
		}
		
	}
}
