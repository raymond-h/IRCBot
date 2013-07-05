package se.kayarr.ircbot.modules;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.Module;
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
	
	private CommandHandler set = new CommandHandler() {
		
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			Subcommands.Result r = Subcommands.splitSubcommand(parameters);
			
			UserData userInfo = UserManager.get().getUserData(user.getNick(), USER_INFO_KEY)
													.defaultValue("gender", "unspecified")
													.defaultValue("timezone", "GMT");
			
			switch(r.command.toLowerCase()) {
				case "gender": {
					if(r.parameters.length() == 0) {
						bot.sendMessage(channel, "Your gender is currently '" + userInfo.getString("gender") + "'");
					}
					else {
						userInfo.put("gender", r.parameters);
						
						bot.sendMessage(channel, "Your gender has been set to '" + userInfo.getString("gender") + "'");
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

}
