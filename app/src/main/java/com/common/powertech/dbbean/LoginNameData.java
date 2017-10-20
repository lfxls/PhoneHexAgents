package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="login_name_data")
public class LoginNameData {
	
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
		
	//登录账号
	@DatabaseField(useGetSet=true,columnName="loginname")
	private String loginname;	
	
	//登录时间
	@DatabaseField(useGetSet=true,columnName="time")
	private String time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public LoginNameData() {
		super();
	}

	public LoginNameData(String loginname, String time) {
		super();
		this.loginname = loginname;
		this.time = time;
	}
	
}
