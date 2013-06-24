package se.kayarr.ircbot.backend;

import java.lang.reflect.Method;

import org.pircbotx.hooks.Event;

public class Module {
	
	public void initialize() {
		//Initialization of the module goes here
	}
	
	@SuppressWarnings("rawtypes")
	public final Method getEventCallbackMethod(Class<? extends Event> clazz) {
		for(Method m : getClass().getMethods()) {
			if(
					m.getReturnType() == void.class &&
					m.getParameterTypes().length == 1 &&
					m.getParameterTypes()[0] == clazz)
				return m;
		}
		
		return null;
	}
	
}
