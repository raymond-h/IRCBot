package se.kayarr.ircbot.users;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class UserManager {
	
	//*** This needs to be replaced to make each bot instance have a separate UserManager
	private static final UserManager instance = new UserManager();
	
	public static UserManager get() {
		return instance;
	}
	//***
	
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
	
}
