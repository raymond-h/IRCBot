package se.kayarr.ircbot.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
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
					
					owner.tableVersions.newInsert()
						.set("TABLE_NAME", name)
						.set("VERSION", version)
						.insert();
				}
				else {
					int oldVersion = owner.tableVersion(name);
					
					if(oldVersion < version) {
						//Upgrade table
						System.out.println("Table '" + name + "' needs an upgrade, calling onTableUpgrade...");
						handler.onTableUpgrade(this, oldVersion, version);
						
						int updated = owner.tableVersions.newUpdate()
							.where("TABLE_NAME = ?", name)
							.set("VERSION", version)
							.update();
						
						if(updated == 0) {
							owner.tableVersions.newInsert()
								.set("TABLE_NAME", name)
								.set("VERSION", version)
								.insert();
						}
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
	
	public List<Map<String,Object>> select(String[] columns, String whereCond) throws SQLException {
		//TODO Have this class return some object that contains a list of key-value mappings
		String columnsStr = Joiner.on(",").join(columns);
		
		return owner.sqlQuery("SELECT " + columnsStr + " FROM " + name + (whereCond == null ? "" : " WHERE " + whereCond));
	};
	
	@Accessors(fluent=true,chain=true)
	public final class InsertBuilder {
		private InsertBuilder() {};
		
		private Map<String, Object> insertValues = new HashMap<>();
		
		public InsertBuilder set(String column, Object value) {
			insertValues.put(column, value);
			return this;
		}
		
		private String generateParameterString(int n) {
			if(n <= 0) return "";
			
			StringBuilder b = new StringBuilder("?");
			
			while(--n > 0) {
				b.append(",?");
			}
			
			return b.toString();
		}
		
		public void insert() throws SQLException {
//			String[] columns = new String[insertValues.size()];
//			String[] values = new String[insertValues.size()];
//			
//			int i = 0;
//			for(Map.Entry<String,String> e : insertValues.entrySet()) {
//				columns[i] = e.getKey();
//				values[i] = e.getValue();
//				++i;
//			}
//			
//			Table.this.insert(columns, values);
			
			String parameterList = generateParameterString(insertValues.size());
			
			//INSERT INTO {this.name} ({columns}) VALUES ({values})
			Database.SqlQueryBuilder qb = owner.newSqlQuery(null);
			StringBuilder sb = new StringBuilder();
			
			int i = 1;
			for(Map.Entry<String,Object> e : insertValues.entrySet()) {
				if(sb.length() > 0) sb.append(",");
				
				sb.append(e.getKey());
				qb.set(i++, e.getValue());
			}
			
			//INSERT INTO {this.name} ({columns}) VALUES ({values})
			qb.query("INSERT INTO " + name + " (" + sb.toString() + ") VALUES (" + parameterList + ")");
			
			System.out.println("Running query: " + qb.query());
			
			qb.executeUpdate();
		}
	}
	
	public InsertBuilder newInsert() {
		return new InsertBuilder();
	}
	
	public void insert(String[] columns, Object[] values) throws SQLException {
		InsertBuilder b = newInsert();
		
		for(int i = 0; i < columns.length; i++) {
			b.set(columns[i], values[i]);
		}
		
		b.insert();
	}
	
	@Accessors(fluent=true,chain=true)
	public final class UpdateBuilder {
		private UpdateBuilder() {};
		
		private Map<String, Object> updateValues = new HashMap<>();
		
		@Getter private String where;
		private Object[] whereParams;
		
		public UpdateBuilder set(String column, Object value) {
			updateValues.put(column, value);
			return this;
		}
		
		public UpdateBuilder where(String where, Object... whereParams) {
			this.where = where;
			this.whereParams = whereParams;
			return this;
		}
		
		public int update() throws SQLException {
//			int r = 0;
//			for(Map.Entry<String,String> e : updateValues.entrySet()) {
//				r = Table.this.update(e.getKey(), e.getValue(), where);
//			}
//			return r;
			
			Database.SqlQueryBuilder qb = owner.newSqlQuery(null);
			StringBuilder sb = new StringBuilder("SET ");
			int i = 1;
			
			for(Map.Entry<String,Object> e : updateValues.entrySet()) {
				if(i > 1) sb.append(", ");
				
				sb.append(e.getKey() + " = ?");
				qb.set(i++, e.getValue());
			}
			
			for(Object param : whereParams) {
				qb.set(i++, param);
			}
			
			//UPDATE {this.name} SET {column} = {value} [WHERE {where}]
			qb.query("UPDATE " + name + " " + sb.toString() + 
					( where == null ? "" : " WHERE " + where ));
			
			System.out.println("Running query: " + qb.query());
			
			return qb.executeUpdate();
		}
	}
	
	public UpdateBuilder newUpdate() {
		return new UpdateBuilder();
	}
	
	public int update(String column, String value, String where) throws SQLException {
		return newUpdate().set(column, value).where(where).update();
	}
	
	public void delete(String where, Object... whereParams) {
		//TODO Implement this
	}
}
