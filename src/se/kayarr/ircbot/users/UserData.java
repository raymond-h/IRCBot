package se.kayarr.ircbot.users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UserData implements Serializable {

	private static final long serialVersionUID = 8323882601258076215L;
	
	private Map<String, Serializable> data = new HashMap<>();
	
	public void put(String key, Serializable value) {
		data.put(key, value);
	}
	
	public Serializable get(String key) {
		return data.get(key);
	}
	
	public boolean has(String key) {
		return data.containsKey(key);
	}
	
	public int getInt(String key) {
		return (Integer) get(key);
	}
	
	public String getString(String key) {
		return (String) get(key);
	}
	
	public <T extends Serializable> T getAs(String key, Class<T> clazz) {
		return clazz.cast(get(key));
	}
	
	public UserData defaultValue(String key, Serializable value) {
		setDefaultValue(key, value);
		return this;
	}
	
	public boolean setDefaultValue(String key, Serializable value) {
		if(has(key)) return false;
		
		data.put(key, value);
		return true;
	}
	
	public static class TypeAdapter implements JsonSerializer<UserData>, JsonDeserializer<UserData> {

		@Override
		public UserData deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			try {
				String base64str = json.getAsString();
				
				StringReader reader = new StringReader(base64str);
				ObjectInputStream in = new ObjectInputStream( BaseEncoding.base64().decodingStream(reader) );
				
				UserData o = (UserData) in.readObject();
				
				in.close();
				reader.close();
				
				return o;
			}
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		public JsonElement serialize(UserData src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			try {
				StringWriter writer = new StringWriter();
				ObjectOutputStream out = new ObjectOutputStream( BaseEncoding.base64().encodingStream(writer) );
				out.writeObject(src);
				
				out.close();
				writer.close();
				
				return new JsonPrimitive(writer.toString());
			}
			catch (IOException e) {
				e.printStackTrace();
				
				return JsonNull.INSTANCE;
			}
		}
		
	}
}
