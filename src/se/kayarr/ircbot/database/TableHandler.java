package se.kayarr.ircbot.database;

public interface TableHandler {
	
	public void onTableCreate(Table table);
	
	public void onTableUpdate(Table table);
}
