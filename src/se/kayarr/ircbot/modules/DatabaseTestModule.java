package se.kayarr.ircbot.modules;

import java.sql.SQLException;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import se.kayarr.ircbot.backend.CommandHandler;
import se.kayarr.ircbot.backend.CommandManager;
import se.kayarr.ircbot.backend.Module;
import se.kayarr.ircbot.database.Database;
import se.kayarr.ircbot.database.Table;
import se.kayarr.ircbot.database.TableHandler;

public class DatabaseTestModule extends Module implements TableHandler {
	
	private static final int TEST_TABLE_VERSION = 2;
	private Table testTable;

	@Override
	public void initialize() {
		
		CommandManager.get().newCommand()
			.addAlias("dbtest")
			.handler(dbtest)
			.add();
		
		CommandManager.get().newCommand()
			.addAlias("dbtest2")
			.handler(dbtest2)
			.add();
		
		testTable = Database.moduleData().table("test_table", TEST_TABLE_VERSION, this);
	}
	
	@Override
	public void onTableCreate(Table table) throws SQLException {
		
		table.createIfNonexistant("TEXT VARCHAR", "N INTEGER");
	}
	
	@Override
	public void onTableUpgrade(Table table, int oldVersion, int newVersion) throws SQLException {
		
		System.out.println("Table " + table.getName() + " upgrade requested! " + oldVersion + " to " + newVersion);
		
		if(oldVersion < 2 && newVersion >= 2) {
			
			table.getOwner().sql("ALTER TABLE " + table.getName() + " ADD COLUMN (N INTEGER)");
		}
	}
	
	private CommandHandler dbtest = new CommandHandler() {
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			bot.sendMessage(channel, "Adding text '" + parameters + "'");
			
			try {
				testTable.newInsert()
					.set("TEXT", parameters)
					.set("N", 5)
					.insert();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	private CommandHandler dbtest2 = new CommandHandler() {
		@Override
		public void onHandleCommand(PircBotX bot, Channel channel, User user,
				String command, String parameters) {
			
			bot.sendMessage(channel, "Alright, running update test...");
			
			try {
				int updated = testTable.newUpdate()
						.set("TEXT", parameters)
						.set("N", 2)
						.where("N = ?", 5)
						.update();
				
				bot.sendMessage(channel, "Updated " + updated + " items!");
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};

}
