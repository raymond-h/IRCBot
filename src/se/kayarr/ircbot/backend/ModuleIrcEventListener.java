package se.kayarr.ircbot.backend;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

public class ModuleIrcEventListener implements Listener<PircBotX> {

	@Override
	public void onEvent(Event<PircBotX> event) throws Exception {
		//This class is only meant to dispatch IRC events to modules,
		//so we only implement Listener to get the onEvent call
		
		ModuleManager.get().dispatchIrcEvent(event);
	}
}
