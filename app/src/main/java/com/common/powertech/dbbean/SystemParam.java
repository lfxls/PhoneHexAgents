package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="system_param")
public class SystemParam {
	
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
		
	//显示模式
	@DatabaseField(useGetSet=true,columnName="theme")
	private int theme;
	
	//语言
	@DatabaseField(useGetSet=true,columnName="language")
	private String language;	

    //语音设置
    @DatabaseField(useGetSet=true,columnName="voice")
    private String voice;

    //锁屏时间
    @DatabaseField(useGetSet=true,columnName="locktime")
    private String locktime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTheme() {
		return theme;
	}

	public void setTheme(int theme) {
		this.theme = theme;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getLocktime() {
		return locktime;
	}

	public void setLocktime(String locktime) {
		this.locktime = locktime;
	}

	public SystemParam() {
		super();
	}

	public SystemParam(int theme, String language, String voice, String locktime) {
		super();
		this.theme = theme;
		this.language = language;
		this.voice = voice;
		this.locktime = locktime;
	}
	
}