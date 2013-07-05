package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.backend.ModuleManager;
import se.kayarr.ircbot.shared.Subcommands;
import se.kayarr.ircbot.users.UserData;
import se.kayarr.ircbot.users.UserManager;

public class UserInfoModule extends Module {
	
	public static final String USER_INFO_KEY = "se.kayarr.user_info";

	@Override
	public void initialize() {
		
		setName("User Info");
		setHelpMessage("A module that keeps track of information about users (specified by themselves): their timezone and gender.");
		
		newCommand()
			.addAlias("set")
			.handler(set)
			.helpMessage("Sets your timezone or gender to the one you specify (if valid).")
			.add();
	}
	
	private UserData getUserInfo(String user) {
		return UserManager.get().getUserData(user, USER_INFO_KEY)
				.defaultValue(Gender.KEY, Gender.UNSPECIFIED)
				.defaultValue("timezone", "GMT");
	}
	
	private CommandHandler set = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			UserData userInfo = getUserInfo(user.getNick());
			
			switch(r.command.toLowerCase()) {
				case "gender": {
					if(r.parameters.length() == 0) {
						bot.sendMessage(channel, "Your gender is currently '" + userInfo.get(Gender.KEY) + "'");
					}
					else {
						bot.sendMessage(channel, "Sorry, setting gender is unsupported so far!");
					}
					
					break;
				}
				
				case "timezone": {
					bot.sendMessage(channel, "Gurrgurr timezone is set");
					
					break;
				}
				
				default: {
					bot.sendMessage(channel, "Error: '" + r.command + "' is not a valid property.");
					
					break;
				}
			}
		}
	};
	
	public static enum Gender {
		MALE(),
		FEMALE(),
		NEITHER(),
		UNSPECIFIED();
		
		public static final String KEY = "gender";
	}
	
	public class Api {
		
		public Gender getGender(String user) {
			UserData userInfo = getUserInfo(user);
			
			return userInfo.getAs(Gender.KEY, Gender.class);
		}
	}
	
	public static Api getApi() {
		UserInfoModule m = ModuleManager.get().findModuleByType(UserInfoModule.class);
		
		return m.new Api();
	}

}
