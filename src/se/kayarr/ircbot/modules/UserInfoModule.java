package se.kayarr.ircbot.modules;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
	public static final String TIME_ZONE_KEY = "timezone";
	
	private static final Map<String, Gender> genderStringMappings = new HashMap<>();
	
	static {
		genderStringMappings.put("male", Gender.MALE);
		genderStringMappings.put("man", Gender.MALE);
		
		genderStringMappings.put("female", Gender.FEMALE);
		genderStringMappings.put("woman", Gender.FEMALE);
		
		genderStringMappings.put("neither", Gender.NEITHER);
		
		genderStringMappings.put("unspecified", Gender.UNSPECIFIED);
	}
	
	public static Gender getGenderByString(String str) {
		return genderStringMappings.get(str);
	}

	@Override
	public void initialize() {
		
		setName("User Info");
		setHelpMessage("A module that keeps track of information about users (specified by themselves): their time zone and gender.");
		
		newCommand()
			.addAlias("gender")
			.handler(gender)
			.helpMessage("Sets or checks your current gender.")
			.add();
		
		newCommand()
			.addAlias("timezone")
			.addAlias("time-zone")
			.handler(timezone)
			.helpMessage("Sets or checks your current time zone.")
			.add();
	}
	
	private UserData getUserInfo(String user) {
		return UserManager.get().getUserData(user, USER_INFO_KEY)
				.defaultValue(GENDER_KEY, Gender.UNSPECIFIED)
				.defaultValue(TIME_ZONE_KEY, TimeZone.getDefault());
	}
	
	private CommandHandler gender = new CommandHandler() {
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			Api api = getApi(user.getNick());
			
			switch(r.command.toLowerCase()) {
				case "set": {
					Gender gender = getGenderByString(r.parameters.toLowerCase());
					
					if(gender == null) {
						bot.sendMessage(channel, "That is not a valid gender!");
						return;
					}
					
					api.setGender(gender);
					
					bot.sendMessage(channel, "Your gender has been set to '" + api.getGender() + "'");
					
					break;
				}
				default: {
					bot.sendMessage(channel, "Your gender is currently '" + api.getGender() + "'");
					
					break;
				}
			}
		}
	};
	
	private CommandHandler timezone = new CommandHandler() {
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			Api api = getApi(user.getNick());
			
			Locale locale = Locale.ENGLISH;
			
			switch(r.command.toLowerCase()) {
				case "set": {
					TimeZone timezone = TimeZone.getTimeZone(r.parameters);
					api.setTimeZone(timezone);
					
					bot.sendMessage(channel, "Your timezone has been set to '" + api.getTimeZone().getDisplayName(locale) + "'");
					
					break;
				}
				default: {
					bot.sendMessage(channel, "Your timezone is currently '" + api.getTimeZone().getDisplayName(locale) + "'");
					
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
		
		public TimeZone getTimeZone() {
			return userInfo.getAs(TIME_ZONE_KEY, TimeZone.class);
		}
		
		public void setTimeZone(TimeZone timezone) {
			userInfo.put(TIME_ZONE_KEY, timezone);
		}
	}
	
	public static Api getApi(String user) {
		UserInfoModule m = ModuleManager.get().findModuleByType(UserInfoModule.class);
		
		return m.new Api(user);
	}

}
