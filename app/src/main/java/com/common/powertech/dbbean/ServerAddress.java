package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "server_address")
public class ServerAddress {

	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
	//服务器地址
	@DatabaseField(useGetSet=true,columnName="address")
	private String address;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ServerAddress() {
		super();
	}

	public ServerAddress(String address) {
		super();
		this.address = address;
	}	
	
}
