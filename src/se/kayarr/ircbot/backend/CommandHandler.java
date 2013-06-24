package se.kayarr.ircbot.backend;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public interface CommandHandler {
	public void onHandleCommand(PircBotX bot, Channel channel, User user, String command, String parameters);
}
