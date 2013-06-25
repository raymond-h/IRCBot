package se.kayarr.ircbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			try {
				Class.forName("org.h2.Driver");
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
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
	
	public boolean tableExists(String name) {
		try {
			String query = "SELECT COUNT(*) AS TABLE_EXISTS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + name.toUpperCase() + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(!rs.next()) return false;
			
			boolean returnval = rs.getBoolean("TABLE_EXISTS");
			
			stmt.close();
			
			return returnval;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
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
	
	//TODO Make this method actually return data
	public List<Map<String,Object>> sqlQuery(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<Map<String,Object>> result = convertResultSetToList(rs);
		stmt.close();
		return result;
	}
	
	//This is oh-so-sneakily borrowed from Vivio...
	private static List<Map<String, Object>> convertResultSetToList(
			ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(row);
		}

		return list;
	}
}