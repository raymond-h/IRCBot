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
		setHelpMessage("A module for listing and getting help messages for all loaded modules/commands.");
		
		CommandManager.get().newCommand(this)
			.addAlias("help")
			.helpMessage("Shows a help message for a module/command, as well as their associated commands/module respectively.")
			.handler( help )
			.add();
		
		CommandManager.get().newCommand(this)
			.addAlias("list")
			.helpMessage("Lists all modules/commands currently added to the bot.")
			.handler( list )
			.add();
	}
	
	private CommandHandler help = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			switch(r.command.toLowerCase()) {
				case "module": case "mod": {
					Module m = ModuleManager.get().findModuleByName(r.parameters);
					
					if(m != null) {
						bot.sendMessage(channel, "Module " + Colors.BOLD + m.getName() + Colors.BOLD);
						if(m.getHelpMessage().length() > 0) bot.sendMessage(channel, "    " + m.getHelpMessage());
						
						List<CommandManager.Command> addedCommands = m.getAddedCommands();
						
						if(addedCommands.size() > 0) {
							int amnt = addedCommands.size();
							bot.sendMessage(channel, "  * This module adds " + Colors.BOLD + amnt + Colors.BOLD + " command" + (amnt > 1 ? "s" : "") + ":" );
							
							for(CommandManager.Command cmd : addedCommands) {
								String helpMsg = cmd.getHelpMessage();
								bot.sendMessage(channel, "    - " + cmd.usage() + (helpMsg.length() > 0 ? " - " + helpMsg : ""));
							}
						}
					}
					else {
						bot.sendMessage(channel, "Sorry, can't find a module called that!");
					}
					
					break;
				}
				
				case "command": case "cmd": {
					CommandManager.Command cmd = CommandManager.get().findMatchingCommand(r.parameters);
					
					if(cmd != null) {
						bot.sendMessage(channel, cmd.usage() + " -- Added by " + Colors.BOLD + cmd.getOwner().getName() + Colors.BOLD);
						String helpMsg = cmd.getHelpMessage();
						if(helpMsg.length() > 0) bot.sendMessage(channel, "    " + helpMsg);
					}
					else {
						bot.sendMessage(channel, "Sorry, can't find that command!");
					}
					
					break;
				}
				
				default: {
					bot.sendMessage(channel, "Unknown subcommand '" + r.command + "', try 'module [module name]' or 'command [command]' instead!");
					
					break;
				}
			}
		}
	};
	
	private CommandHandler list = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			switch(r.command.toLowerCase()) {
				case "modules": case "mods": {
					List<Module> modules = ModuleManager.get().getModules();
					String[] moduleNames = new String[modules.size()];
					
					int i = 0;
					for(Module m : modules) {
						moduleNames[i++] = Colors.BOLD + m.getName() + Colors.BOLD;
					}
					
					bot.sendMessage(channel, "Modules: " + Joiner.on(", ").join(moduleNames));
					
					break;
				}
				
				case "commands": case "cmds": {
					List<CommandManager.Command> commands = CommandManager.get().getCommands();
					String[] commandAliases = new String[commands.size()];
					
					int i = 0;
					for(CommandManager.Command cmd : commands) {
						commandAliases[i++] = cmd.usage();
					}
					
					bot.sendMessage(channel, "Commands: " + Joiner.on(", ").join(commandAliases));
					
					break;
				}
				
				default: {
					if(r.command.length() > 0) {
						//Default behaviour, user typed something that we don't know what it means
						bot.sendMessage(channel, "Unknown subcommand '" + r.command + "', try 'modules' or 'commands' instead!");
					}
					else {
						//Default behaviour, user typed absolutely nothing
						bot.sendMessage(channel, "Try adding 'modules' or 'commands' to the end!");
					}
					break;
				}
			}
		}
	};

}
