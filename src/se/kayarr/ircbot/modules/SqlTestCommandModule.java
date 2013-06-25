package se.kayarr.ircbot.modules;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.database.Database;

import com.google.common.base.Joiner;

public class SqlTestCommandModule extends Module implements CommandHandler {

	@Override
	public void initialize() {
		
		CommandManager.get().newCommand()
			.addAlias("sql-query")
			.handler(this)
			.finish();
		
		CommandManager.get().newCommand()
			.addAlias("sql")
			.handler(new CommandHandler() {
				@Override
				public void onHandleCommand(PircBotX bot, Channel channel, User user,
						String command, String parameters) {
					
					try {
						Database.moduleData().sql(parameters);
						
						bot.sendMessage(channel, Colors.BOLD + Colors.GREEN + "Executed successfully");
					}
					catch (SQLException e) {
						e.printStackTrace();
						
						bot.sendMessage(channel, Colors.BOLD + Colors.RED + "Error: " + e.getMessage() +
								" (check console output for more information)");
					}
					
				}
			})
			.finish();
	}

	@Override
	public void onHandleCommand(PircBotX bot, Channel channel, User user,
			String command, String parameters) {
		
		try {
			List<Map<String,Object>> data = Database.moduleData().sqlQuery(parameters);
			
			if(data.size() == 0) {
				bot.sendMessage(channel, Colors.BOLD + "No data returned");
				return;
			}
			
			Joiner delimJoin = Joiner.on(" | ");
			String columnsStr = delimJoin.join(data.get(0).keySet());
			bot.sendMessage(channel, Colors.BOLD + columnsStr);
			
			for(Map<String,Object> row : data) {
				String valuesStr = delimJoin.join(row.values());
				
				bot.sendMessage(channel, valuesStr);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			
			bot.sendMessage(channel, Colors.BOLD + Colors.RED + "Error: " + e.getMessage() +
					" (check console output for more information)");
		}
	}

}
