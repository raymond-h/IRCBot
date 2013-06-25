package se.kayarr.ircbot.backend;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class CommandManager {
	private CommandManager() {
	};

	// *** This needs to be replaced to make each bot instance have a separate
	// CommandManager
	private static final CommandManager instance = new CommandManager();

	public static CommandManager get() {
		return instance;
	}

	// ***

	private List<Command> registeredCommands = new LinkedList<>();

	@Accessors(fluent = true)
	public final class CommandEntryBuilder {
		private CommandEntryBuilder() {
		};

		@Getter @Setter private CommandHandler handler;
		private Set<String> aliases = new HashSet<>();

		public CommandEntryBuilder addAlias(String alias) {
			this.aliases.add(alias);
			return this;
		}

		public void add() {
			registeredCommands.add(new Command(handler, aliases));
		}
	}

	@AllArgsConstructor
	private class Command {
		public CommandHandler handler;

		public Set<String> aliases;

		public boolean matchesCommandString(String command) {
			return aliases.contains(command);
		}
	}

	public CommandEntryBuilder newCommand() {
		return new CommandEntryBuilder();
	}

	private Command findMatchingCommand(String command) {
		for (Command cmd : registeredCommands) {
			if (cmd.matchesCommandString(command))
				return cmd;
		}

		return null;
	}

	public boolean dispatchCommand(PircBotX bot, Channel channel, User user,
			String command, String parameters) {
		Command cmd = findMatchingCommand(command);
		if (cmd == null || cmd.handler == null)
			return false;

		cmd.handler.onHandleCommand(bot, channel, user, command, parameters);
		return true;
	}
}
