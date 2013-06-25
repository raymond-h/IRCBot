package se.kayarr.ircbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	
	Table tableVersions = null;
	
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
			
			sql("CREATE SCHEMA IF NOT EXISTS MODULE_META");
			tableVersions = table("MODULE_META.TABLE_VERSIONS", -1, null);
			tableVersions.createIfNonexistant("TABLE_NAME VARCHAR", "VERSION INTEGER");
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

	int tableVersion(String name) {
		try {
			List<Map<String,Object>> version = tableVersions.select(new String[] {"VERSION"}, "TABLE_NAME = '" + name + "'");
			
			if(version.size() == 0) return -1;
			
			int ver = (Integer)version.get(0).get("VERSION");
			
			System.out.println("Version of '" + name + "' is " + ver);
			
			return ver;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	@Accessors(fluent=true,chain=true)
	public final class SqlQueryBuilder {
		
		@Getter private String query;
		
		private Map<Integer, Object> objects = new HashMap<>();
		
		private SqlQueryBuilder(String query) {
			this.query = query;
		}
		
		public SqlQueryBuilder set(int index, Object o) {
			objects.put(index, o);
			return this;
		}
		
		private PreparedStatement createPreparedStatement() throws SQLException {
			PreparedStatement stmt = conn.prepareStatement(query);
			
			for(Map.Entry<Integer, Object> e : objects.entrySet()) {
				stmt.setObject(e.getKey(), e.getValue());
			}
			
			return stmt;
		}
		
		public void execute() throws SQLException {
			PreparedStatement stmt = createPreparedStatement();
			
			stmt.execute();
			stmt.close();
		}
		
		public int executeUpdate() throws SQLException {
			PreparedStatement stmt = createPreparedStatement();
			
			int r = stmt.executeUpdate();
			stmt.close();
			return r;
		}
		
		public List<Map<String, Object>> executeQuery() throws SQLException {
			PreparedStatement stmt = createPreparedStatement();
			
			ResultSet rs = stmt.executeQuery();
			List<Map<String,Object>> result = convertResultSetToList(rs);
			stmt.close();
			return result;
		}
	}
	
	public SqlQueryBuilder newSqlQuery(String query) {
		return new SqlQueryBuilder(query);
	}
	
	public void sql(String query) throws SQLException {
		newSqlQuery(query).execute();
	}
	
	public int sqlUpdate(String query) throws SQLException {
		return newSqlQuery(query).executeUpdate();
	}
	
	public List<Map<String,Object>> sqlQuery(String query) throws SQLException {
		return newSqlQuery(query).executeQuery();
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