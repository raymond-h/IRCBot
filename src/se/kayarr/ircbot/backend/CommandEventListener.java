package se.kayarr.ircbot.backend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandEventListener extends ListenerAdapter<PircBotX> {
	
	static {
		updateEventMethodMapping(CommandEventListener.class);
	}
	
	private static Pattern commandRegex = Pattern.compile("!(.+?)(?:\\s+(.+))?$");
	
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		String message = event.getMessage();
		
		if(isValidCommand(message)) {
			Matcher cmdMatcher = commandRegex.matcher(message);
			
			cmdMatcher.find();
			String command = cmdMatcher.group(1);
			String parameters = cmdMatcher.group(2);
			
			boolean handled = CommandManager.get().dispatchCommand(
					event.getBot(),
					event.getChannel(),
					event.getUser(),
					command, parameters);
			
			if(!handled) event.respond("No such command");
		}
	}
	
	private boolean isValidCommand(String cmdStr) {
		return commandRegex.matcher(cmdStr).matches();
	}
	
}
