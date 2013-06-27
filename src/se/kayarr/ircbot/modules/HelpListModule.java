package se.kayarr.ircbot.modules;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.backend.ModuleManager;
import se.kayarr.ircbot.shared.Subcommands;

import com.google.common.base.Joiner;

public class HelpListModule extends Module {

	@Override
	public void initialize() {
		setName("Help and List");
		
		CommandManager.get().newCommand()
			.addAlias("help")
			.handler( help )
			.add();
		
		CommandManager.get().newCommand()
			.addAlias("list")
			.handler( list )
			.add();
	}
	
	private CommandHandler help = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			bot.sendMessage(channel, "Nope, no help to be had here.");
		}
	};
	
	private CommandHandler list = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			switch(r.command.toLowerCase()) {
				case "modules": {
					List<Module> modules = ModuleManager.get().getModules();
					String[] moduleNames = new String[modules.size()];
					
					int i = 0;
					for(Module m : modules) {
						moduleNames[i++] = Colors.BOLD + m.getName() + Colors.BOLD;
					}
					
					bot.sendMessage(channel, "Current modules: " + Joiner.on(", ").join(moduleNames));
					
					break;
				}
				
				case "commands": {
					bot.sendMessage(channel, "This is a list of commands!");
					
					break;
				}
				
				default: {
					bot.sendMessage(channel, "Unknown subcommand '" + r.command + "', try 'modules' or 'commands' instead!");
					break;
				}
			}
		}
	};

}
