package se.kayarr.ircbot.backend;

import java.util.Random;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import se.kayarr.ircbot.users.UserManager;

public class TestEventListener extends ListenerAdapter<PircBotX> {
	
	static {
		updateEventMethodMapping(TestEventListener.class);
	}
	
	public TestEventListener() {
		UserManager.get().registerDataClass(GoldUserData.class);
		
		UserManager.get().registerDataClass(CoolNickUserData.class);
	}
	
	public static class GoldUserData extends UserManager.Data {
		public static final String KEY = "se.kayarr.gold_user_data";
		
		public int gold = 0;
	}
	
	public static class CoolNickUserData extends UserManager.Data {
		public static final String KEY = "se.kayarr.cool_nick_user_data";
		
		public boolean hasCoolNick = false;
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		
		String user = event.getUser().getNick();
		
		GoldUserData d = UserManager.get().getUserData(user, GoldUserData.class);
		
		CoolNickUserData cn = UserManager.get().getUserData(user, CoolNickUserData.class);
		
		if( d.gold == 0 ) d.gold = 20;
		else d.gold += (new Random()).nextInt(30);
		
		cn.hasCoolNick = !cn.hasCoolNick;
		
		event.respond("You got " + d.gold + " gold pieces!");
		event.respond("You" + (cn.hasCoolNick ? " " : " DO NOT ") + "have a cool nick!");
		
		String json = UserManager.get().serializeTest();
		
		UserManager.get().deserializeTest(json);
		
	}
	
}
