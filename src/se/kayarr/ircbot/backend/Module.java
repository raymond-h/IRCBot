package se.kayarr.ircbot.backend;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

import org.pircbotx.hooks.Event;

public abstract class Module {
	
	@Getter @Setter private String name = "";
	
	public abstract void initialize();
	
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
