package se.kayarr.ircbot.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import se.kayarr.ircbot.modules.DatabaseTestModule;
import se.kayarr.ircbot.modules.SqlTestCommandModule;
import se.kayarr.ircbot.modules.TestModule;
import se.kayarr.ircbot.modules.TimeModule;

public class ModuleManager {
	private ModuleManager() {
		addModule(new TestModule());
		addModule(new TimeModule());
		addModule(new DatabaseTestModule());
		addModule(new SqlTestCommandModule());
	};
	
	//*** This needs to be replaced to make each bot instance have a separate ModuleManager
	private static final ModuleManager instance = new ModuleManager();
	
	public static ModuleManager get() {
		return instance;
	}
	//***
	
	private boolean initialized = false;
	
	private List<Module> modules = new ArrayList<>();
	
	private void addModule(Module module) {
		modules.add(module);
	}
	
	public void initialize() {
		if(!initialized) {
			initialized = true;
			
			for(Module m : modules) {
				m.initialize();
			}
		}
	}
	
	public void dispatchIrcEvent(Event<PircBotX> event) {
		for(Module m : modules) {
			Method callback = m.getEventCallbackMethod(event.getClass());
			
			if(callback == null) continue;
			
			try {
				callback.invoke(m, event);
			}
			catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
