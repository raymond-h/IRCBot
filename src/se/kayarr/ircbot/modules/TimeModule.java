package se.kayarr.ircbot.modules;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;

public class TimeModule extends Module implements CommandHandler {
	
	@Override
	public void initialize() {
		
		setName("Time Display");
		
		CommandManager.get().newCommand(this)
			.addAlias("time")
			.handler(this)
			.add();
	}

	@Override
	public void onHandleCommand(PircBotX bot, Channel channel, User user,
			String command, String parameters) {
		
		String time = SimpleDateFormat.getTimeInstance().format(new Date());
		
		bot.sendMessage(channel, "The time is " + time);
	}

}
