package se.kayarr.ircbot.backend;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.pircbotx.hooks.Event;

import com.google.common.collect.ImmutableList;

public abstract class Module {
	
	@Getter @Setter private String name = "";
	
	private List<CommandManager.Command> commands = new ArrayList<>();
	
	/* package-private */ void addCommand(CommandManager.Command cmd) {
		commands.add(cmd);
	}
	
	public List<CommandManager.Command> getAddedCommands() {
		return ImmutableList.copyOf(commands);
	}
	
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
