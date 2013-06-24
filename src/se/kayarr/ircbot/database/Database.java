package se.kayarr.ircbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Getter;

public class Database {
	@Getter private String name;
	private Connection conn;
	
	public Database(String name) {
		this.name = name;
	}
	
	public void connect() throws SQLException {
		if(conn == null) {
			
			conn = DriverManager.getConnection("jdbc:h2:"+name+";create=true");
		}
	}
	
	public static Database connectToDatabase(String dbName) throws SQLException {
		Database db = new Database(dbName);
		db.connect();
		return db;
	}
	
	public Table table(String name, int version, TableHandler handler) {
		return new Table(this, name, version, handler);
	}
	
	//TODO Temporary implementation
	public boolean tableExists(String name) {
		return false;
	}
	
	//TODO Temporary implementation
	int tableVersion(String name) {
		return 1;
	}
}
