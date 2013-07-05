package se.kayarr.ircbot.backend;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import se.kayarr.ircbot.modules.DatabaseTestModule;
import se.kayarr.ircbot.modules.HelpListModule;
import se.kayarr.ircbot.modules.SqlTestCommandModule;
import se.kayarr.ircbot.modules.TestModule;
import se.kayarr.ircbot.modules.TimeModule;
import se.kayarr.ircbot.shared.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ModuleManager {
	private ModuleManager() {
		addModules();
//		addModule(new TestModule());
//		addModule(new TimeModule());
//		addModule(new DatabaseTestModule());
//		addModule(new SqlTestCommandModule());
//		addModule(new HelpListModule());
	};
	
	//*** This needs to be replaced to make each bot instance have a separate ModuleManager
	private static final ModuleManager instance = new ModuleManager();
	
	public static ModuleManager get() {
		return instance;
	}
	//***
	
	private boolean initialized = false;
	
	private List<Module> modules = new ArrayList<>();
	
	private void addModules() {
		//Uses Guava stuff to load all classes in se.kayarr.ircbot.modules
		
		try {
			ClassPath classPath = ClassPath.from( getClass().getClassLoader() );
			Set<ClassInfo> classInfos = classPath.getTopLevelClasses("se.kayarr.ircbot.modules");
			
			for(ClassInfo classInfo : classInfos) {
				System.out.println("Processing " + classInfo.getName());
				
				Class<?> clazz = classInfo.load();
				
				if(!Module.class.isAssignableFrom(clazz)) {
					System.err.println("Class " + clazz.getCanonicalName() + " not a subclass of Module, ignoring...");
					continue;
				}
				
				Module m = (Module) clazz.newInstance();
				addModule(m);
			}
		}
		catch (IOException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void addModule(Module module) {
		modules.add(module);
	}
	
	public List<Module> getModules() {
		return ImmutableList.copyOf(modules);
	}
	
	public Module findModuleByName(String name) {
		for(Module m : modules) {
			if(m.getName().equalsIgnoreCase(name)) return m;
		}
		
		return null;
	}
	
	public List<Module> findModulesMatching(String pattern) {
		List<Module> r = new LinkedList<>();
		
		pattern = pattern.toLowerCase();
		
		for(Module m : modules) {
			if(Strings.matches(pattern, m.getName().toLowerCase())) r.add(m);
		}
		
		return r;
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
