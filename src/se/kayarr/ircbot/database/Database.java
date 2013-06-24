package se.kayarr.ircbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

public class Database {
	@Accessors(fluent=true) @Getter private static Database moduleData = null;
	
	static {
		try {
			moduleData = connectToDatabase("module_data");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Getter private String name;
	@Getter(value=AccessLevel.PACKAGE) private Connection conn;
	
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
		Table t = new Table(this, name, version, handler);
		t.initialize();
		return t;
	}
	
	//TODO Temporary implementation
	public boolean tableExists(String name) {
		return false;
	}
	
	//TODO Temporary implementation
	int tableVersion(String name) {
		return 1;
	}
	
	public void sql(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
	}
}
