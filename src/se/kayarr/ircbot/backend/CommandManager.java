package se.kayarr.ircbot.backend;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

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
		
		private Module owner;
		
		private CommandEntryBuilder(Module owner) {
			this.owner = owner;
		}

		@Getter @Setter private CommandHandler handler;
		private Set<String> aliases = new HashSet<>();
		@Getter @Setter private String helpMessage = "";

		public CommandEntryBuilder addAlias(String alias) {
			this.aliases.add(alias);
			return this;
		}

		public void add() {
			Command cmd = new Command(handler, aliases, helpMessage);
			if(owner != null) {
				owner.addCommand(cmd);
				cmd.owner = new WeakReference<Module>(owner);
			}
			registeredCommands.add(cmd);
		}
	}

	@RequiredArgsConstructor
	public class Command {
		public WeakReference<Module> owner;
		
		public Module getOwner() {
			return owner != null ? owner.get() : null;
		}
		
		@NonNull private CommandHandler handler;

		@NonNull @Getter private Set<String> aliases;
		
		@NonNull @Getter private String helpMessage;

		public boolean matchesCommandString(String command) {
			return aliases.contains(command);
		}
		
		public String usage() {
			return "[" + Joiner.on(" | ").join(aliases) + "]";
		}
	}

	public CommandEntryBuilder newCommand() {
		return newCommand(null);
	}
	
	public CommandEntryBuilder newCommand(Module owner) {
		return new CommandEntryBuilder(owner);
	}

	public Command findMatchingCommand(String command) {
		for (Command cmd : registeredCommands) {
			if (cmd.matchesCommandString(command))
				return cmd;
		}

		return null;
	}
	
	public List<Command> getCommands() {
		return ImmutableList.copyOf(registeredCommands);
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
