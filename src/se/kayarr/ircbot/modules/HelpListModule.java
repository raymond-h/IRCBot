package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.shared.Subcommands;

public class HelpListModule extends Module {
	
	private Subcommands listSubs = new Subcommands("modules", "commands");

	@Override
	public void initialize() {
		setName("HelpList");
		
		CommandManager.get().newCommand()
			.addAlias("help")
			.handler( help )
			.add();
		
		CommandManager.get().newCommand()
			.addAlias("list")
			.handler( listSubs.createHandler() )
			.add();
		
		listSubs.setHandler(listSubCmds);
	}
	
	private CommandHandler help = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			bot.sendMessage(channel, "Nope, no help to be had here.");
		}
	};
	
	private CommandHandler listSubCmds = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			if(command == null) {
				bot.sendMessage(channel, "That command was not recognized! Parameters are '" + parameters + "'");
			}
			else {
				bot.sendMessage(channel, "I recognized the command '" + command + "'! Parameters are '" + parameters + "'");
			}
		}
	};

}
