package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;

public class TestModule extends Module {

	@Override
	public void initialize() {
		super.initialize();
		
		CommandManager.get().newCommand()
		.addAlias("hello")
		.addAlias("hi")
		.handler(new CommandHandler() {
			
			public void onHandleCommand(PircBotX bot, Channel channel, User user,
					String command, String parameters) {
				
				bot.sendMessage(channel, "Hello to you too!");
			}
		})
		.commit();
	}

}