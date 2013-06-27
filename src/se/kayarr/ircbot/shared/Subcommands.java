package se.kayarr.ircbot.shared;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;

import com.google.common.collect.Sets;

public class Subcommands {
	@Getter private Set<String> subcommands;
	@Getter @Setter private CommandHandler handler;
	
	public Subcommands(String... subcmds) {
		setSubcommands(subcmds);
	}
	
	public void setSubcommands(String... subcmds) {
		subcommands = Sets.newHashSet(subcmds);
	}
	
	public void dispatchCommand(PircBotX bot, Channel channel, User user, String cmdstr) {
		Result r = splitSubcommand(cmdstr);
		
		if(subcommands.contains(r.command)) {
			//Command was recognized, dispatch to handler...
			handler.onHandleCommand(bot, channel, user, r.command, r.parameters);
		}
		else {
			//No command recognized, pass null as command and cmdstr as parameters
			handler.onHandleCommand(bot, channel, user, null, cmdstr);
		}
	}
	
	public CommandHandler createHandler() {
		return new CommandHandler() {
			
			@Override
			public void onHandleCommand(PircBotX bot, Channel channel, User user,
					String command, String parameters) {
				
				dispatchCommand(bot, channel, user, parameters);
			}
		};
	}
	
	public static final class Result {
		public String command;
		public String parameters;
	}
	
	public static Result splitSubcommand(String cmdstr) {
		Result r = new Result();
		String[] parts = cmdstr.split("\\s+", 2);
		r.command = parts[0];
		r.parameters = parts.length > 1 ? parts[1] : "";
		return r;
	}
}
