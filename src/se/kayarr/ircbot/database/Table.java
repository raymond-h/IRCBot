package se.kayarr.ircbot.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.common.base.Joiner;

public class Table {
	private static final String ID_COLUMN = "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY";
	
	@Getter private Database owner;
	
	@Getter private String name;
	@Getter private int version;
	
	private TableHandler handler;
	
	Table(Database owner, String name, int version, TableHandler handler) {
		this.owner = owner;
		this.name = name;
		this.version = version;
		this.handler = handler;
	}
	
	void initialize() {
		try {
			
			if(handler != null) {
				if(!owner.tableExists(name)) {
					//Create table
					System.out.println("Table '" + name + "' does not exist, calling onTableCreate...");
					handler.onTableCreate(this);
				}
				else {
					int oldVersion = owner.tableVersion(name);
					
					if(oldVersion < version) {
						//Upgrade table
						System.out.println("Table '" + name + "' needs an upgrade, calling onTableUpgrade...");
						handler.onTableUpgrade(this, oldVersion, version);
					}
				}
				
				System.out.println("Table '" + name + "' is ready to go!");

			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createIfNonexistant(String... columns) throws SQLException {
		List<String> columnsList = new ArrayList<>( Arrays.asList(columns) );
		columnsList.add(0, ID_COLUMN); //Add specs for ID column
		
		String columnsString = Joiner.on(", ").join(columnsList);
		
		owner.sql("CREATE TABLE IF NOT EXISTS " + name + " (" + columnsString + ")");
	}
	
	public void select(String[] columns, String whereCond) throws SQLException {
		//TODO Have this class return some object that contains a list of key-value mappings
		
//		Connection conn = owner.getConn();
//		String columnsStr = Joiner.on(",").join(columns);
//		
//		Statement stmt = conn.createStatement();
//		ResultSet rs = stmt.executeQuery("SELECT " + columnsStr + " FROM " + name + (whereCond == null ? "" : " WHERE " + whereCond));
	};
	
	@Accessors(fluent=true,chain=true)
	public final class InsertBuilder {
		private InsertBuilder() {};
		
		private Map<String, String> insertValues = new HashMap<>();
		
		public InsertBuilder put(String column, String value) {
			insertValues.put(column, value);
			return this;
		}
		
		public void insert() throws SQLException {
			String[] columns = new String[insertValues.size()];
			String[] values = new String[insertValues.size()];
			
			int i = 0;
			for(Map.Entry<String,String> e : insertValues.entrySet()) {
				columns[i] = e.getKey();
				values[i] = e.getValue();
				++i;
			}
			
			Table.this.insert(columns, values);
		}
	}
	
	public InsertBuilder newInsert() {
		return new InsertBuilder();
	}
	
	//INSERT INTO {this.name} ({columns}) VALUES ({values})
	public void insert(String[] columns, String[] values) throws SQLException {
		//TODO Return a builder for inserting maybe
		Joiner j = Joiner.on(",");
		String columnsStr = j.join(columns);
		String valuesStr = j.join(values);
		
		owner.sql("INSERT INTO " + name + " (" + columnsStr + ") VALUES (" + valuesStr + ")");
	}
	
	@Accessors(fluent=true,chain=true)
	public final class UpdateBuilder {
		private UpdateBuilder() {};
		
		private Map<String, String> updateValues = new HashMap<>();
		@Getter @Setter private String where;
		
		public UpdateBuilder set(String column, String value) {
			updateValues.put(column, value);
			return this;
		}
		
		public void update() throws SQLException {
			for(Map.Entry<String,String> e : updateValues.entrySet()) {
				Table.this.update(e.getKey(), e.getValue(), where);
			}
		}
	}
	
	public UpdateBuilder newUpdate() {
		return new UpdateBuilder();
	}
	
	//UPDATE {this.name} SET {column} = {value} [WHERE {whereCond}]
	public void update(String column, String value, String whereCond) throws SQLException {
		owner.sql("UPDATE " + name + " SET " + column + " = " + value +
				( whereCond == null ? "" : " WHERE " + whereCond ) );
	}
	
	public void delete(String whereCond) {
		//TODO Implement this
	}
}
