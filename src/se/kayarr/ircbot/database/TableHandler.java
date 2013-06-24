package se.kayarr.ircbot.database;

import java.sql.SQLException;

public interface TableHandler {
	
	public void onTableCreate(Table table) throws SQLException;
	
	public void onTableUpgrade(Table table, int oldVersion, int newVersion) throws SQLException;
}
