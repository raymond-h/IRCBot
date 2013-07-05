package se.kayarr.ircbot.modules;

import java.util.TimeZone;

import lombok.Getter;

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
	public static final String GENDER_KEY = "gender";
	public static final String TIMEZONE_KEY = "timezone";

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
				.defaultValue(GENDER_KEY, Gender.UNSPECIFIED)
				.defaultValue(TIMEZONE_KEY, TimeZone.getDefault());
	}
	
	private CommandHandler set = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			Api api = getApi(user.getNick());
			
			switch(r.command.toLowerCase()) {
				case "gender": {
					if(r.parameters.length() == 0) {
						bot.sendMessage(channel, "Your gender is currently '" + api.getGender() + "'");
					}
					else {
						Gender gender = Gender.valueOf(r.parameters);
						api.setGender(gender);
						
						bot.sendMessage(channel, "Your gender has been set to '" + api.getGender() + "'");
					}
					
					break;
				}
				
				case "timezone": {
					if(r.parameters.length() == 0) {
						bot.sendMessage(channel, "Your timezone is currently '" + api.getTimezone().getDisplayName() + "'");
					}
					else {
						TimeZone timezone = TimeZone.getTimeZone(r.parameters);
						api.setTimezone(timezone);
						
						bot.sendMessage(channel, "Your timezone has been set to '" + api.getTimezone().getDisplayName() + "'");
					}
					
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
	}
	
	public class Api {
		
		@Getter private String user;
		@Getter private UserData userInfo;
		
		private Api(String user) {
			this.user = user;
			userInfo = UserInfoModule.this.getUserInfo(user);
		}
		
		public Gender getGender() {
			return userInfo.getAs(GENDER_KEY, Gender.class);
		}
		
		public void setGender(Gender gender) {
			userInfo.put(GENDER_KEY, gender);
		}
		
		public TimeZone getTimezone() {
			return userInfo.getAs(TIMEZONE_KEY, TimeZone.class);
		}
		
		public void setTimezone(TimeZone timezone) {
			userInfo.put(TIMEZONE_KEY, timezone);
		}
	}
	
	public static Api getApi(String user) {
		UserInfoModule m = ModuleManager.get().findModuleByType(UserInfoModule.class);
		
		return m.new Api(user);
	}

}
