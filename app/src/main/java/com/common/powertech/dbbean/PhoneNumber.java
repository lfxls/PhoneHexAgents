package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="phone_number")
public class PhoneNumber {

	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
	@DatabaseField(useGetSet=true,columnName="phone")
	private String phone;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public PhoneNumber() {
		super();
	}

	public PhoneNumber(String phone) {
		super();
		this.phone = phone;
	}
		
}
