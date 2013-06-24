package se.kayarr.ircbot.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

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
		if(handler != null) {
			if(!owner.tableExists(name)) {
				//Create table
				handler.onTableCreate(this);
			}
			else {
				int oldVersion = owner.tableVersion(name);
				
				if(oldVersion < version) {
					//Upgrade table
					handler.onTableUpgrade(this, oldVersion, version);
				}
			}

		}
	}
	
	public void createIfNonexistant(String... columns) throws SQLException {
		List<String> columnsList = new ArrayList<>( Arrays.asList(columns) );
		columnsList.add(0, ID_COLUMN); //Add specs for ID column
		
		String columnsString = Joiner.on(", ").join(columnsList);
		
		owner.sql("CREATE TABLE IF NOT EXISTS " + name + " (" + columnsString + ")");
	}
}
