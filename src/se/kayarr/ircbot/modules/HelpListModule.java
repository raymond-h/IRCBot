package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.shared.Subcommands;

public class HelpListModule extends Module {

	@Override
	public void initialize() {
		setName("HelpList");
		
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
					bot.sendMessage(channel, "Insert a list of modules here!");
					
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
