package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "login_error")
public class LoginError {
	
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
	@DatabaseField(useGetSet=true,columnName="name")
	private String name;
	
	@DatabaseField(useGetSet=true,columnName="time")
	private String time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public LoginError() {
		super();
	}

	public LoginError(String name, String time) {
		super();
		this.name = name;
		this.time = time;
	}		

}
