package se.kayarr.ircbot.modules;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.database.Database;

import com.google.common.base.Joiner;

public class SqlTestCommandModule extends Module implements CommandHandler {

	@Override
	public void initialize() {
		
		setName("SQL Querying");
		
		newCommand()
			.addAlias("sql-query")
			.handler(this)
			.add();
		
		newCommand()
			.addAlias("sql")
			.handler(new CommandHandler() {
				@Override
				public void onHandleCommand(PircBotX bot, Channel channel, User user,
						String command, String parameters) {
					
					try {
						int updated = Database.moduleData().sqlUpdate(parameters);
						
						bot.sendMessage(channel, Colors.GREEN + "Executed successfully " +
								"(" + Colors.BOLD + updated + Colors.BOLD + " rows updated)");
					}
					catch (SQLException e) {
						e.printStackTrace();
						
						bot.sendMessage(channel, Colors.RED + "Error: " + e.getMessage().replaceAll("[\r\n]+", " | ") +
								" (check console output for more information)");
					}
					
				}
			})
			.add();
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
			
			bot.sendMessage(channel, "Query matched " + data.size() + " rows");
			
			Joiner delimJoin = Joiner.on(" | ").useForNull('\u001d' + "null" + '\u001d');
			String columnsStr = delimJoin.join(data.get(0).keySet());
			bot.sendMessage(channel, Colors.BOLD + columnsStr);
			
			for(Map<String,Object> row : data) {
				String valuesStr = delimJoin.join(row.values());
				
				bot.sendMessage(channel, valuesStr);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			
			bot.sendMessage(channel, Colors.BOLD + Colors.RED + "Error: " + e.getMessage().replaceAll("[\r\n]+", " | ") +
					" (check console output for more information)");
		}
	}

}
