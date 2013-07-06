package se.kayarr.ircbot.backend;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandListener extends ListenerAdapter<PircBotX> {
	
	static {
		updateEventMethodMapping(CommandListener.class);
	}
	
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		String message = event.getMessage();
		
		int indexOfCommand = startOfCommand(event.getBot(), message);
		if(indexOfCommand >= 0) {
			String[] cmdParts = message.substring(indexOfCommand).split("\\s+", 2);
			
			System.out.println("Dispatching command '" + cmdParts[0] + "'");
			if(cmdParts.length > 1) System.out.println("Parameters are '" + cmdParts[1] + "'");
			
			boolean handled = CommandManager.get().dispatchCommand(
					event.getBot(),
					event.getChannel(),
					event.getUser(),
					cmdParts[0], cmdParts.length > 1 ? cmdParts[1] : "");
			
			if(!handled) event.respond("No such command");
		}
	}
	
	private static int startOfCommand(PircBotX bot, String string) {
		if(string.startsWith("!")) return 1;
		
		String namedCommand = bot.getNick() + ", ";
		if(string.substring(0, namedCommand.length()).equalsIgnoreCase(namedCommand)) return namedCommand.length();
		
		return -1;
	}
	
}
