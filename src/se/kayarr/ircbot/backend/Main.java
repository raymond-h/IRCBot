package se.kayarr.ircbot.backend;

import java.io.IOException;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

public class Main {

	public static void main(String[] args) {
		try {
			PircBotX bot = new PircBotX();
			bot.setVerbose(true);
			bot.setName("Kirierath");
			bot.getListenerManager().addListener(new CommandListener());
			bot.getListenerManager().addListener(new ModuleIrcEventListener());
			
			ModuleManager.get().initialize();

			try {
				bot.connect("portlane.esper.net");

				bot.joinChannel("#warcan");
			}
			catch (NickAlreadyInUseException e) {
				System.out.println("Nickname '" + bot.getName()
						+ "' already in use");
			}
			catch (IOException | IrcException e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
