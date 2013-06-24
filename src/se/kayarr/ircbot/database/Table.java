package se.kayarr.ircbot.database;

import lombok.Getter;

public class Table {
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
}
