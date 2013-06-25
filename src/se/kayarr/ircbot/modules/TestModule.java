package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.ActionEvent;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;

public class TestModule extends Module implements CommandHandler {

	@Override
	public void initialize() {
		
		CommandManager.get().newCommand()
			.addAlias("test")
			.addAlias("test2")
			.handler(this)
			.add();
	}

	@Override
	public void onHandleCommand(PircBotX bot, Channel channel, User user,
			String command, String parameters) {
		
		bot.sendMessage(channel, "Test message");
	}
	
	public void onAction(ActionEvent<PircBotX> event) {
		
		event.respond("dances");
	}

}
