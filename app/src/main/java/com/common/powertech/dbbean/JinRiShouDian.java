package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="jinrishoudian")
public class JinRiShouDian {
	public JinRiShouDian(){
		super();
	}
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
	@DatabaseField(useGetSet=true,columnName="time")
	private String time;
	
	@DatabaseField(useGetSet=true,columnName="money")
	private String money;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public JinRiShouDian(String time, String money) {
		super();
		this.time = time;
		this.money = money;
	}	
	
}
