package se.kayarr.ircbot.backend;

import java.util.ArrayList;
import java.util.List;

import se.kayarr.ircbot.modules.TestModule;

public class ModuleManager {
	private ModuleManager() {
		addModule(new TestModule());
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
}
