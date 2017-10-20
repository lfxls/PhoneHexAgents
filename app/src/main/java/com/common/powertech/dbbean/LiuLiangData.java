package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="liuliang_data")
public class LiuLiangData {
	
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
	@DatabaseField(useGetSet=true,columnName="time")
	private String time;
	
	@DatabaseField(useGetSet=true,columnName="traffic")
	private long traffic;

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

	public long getTraffic() {
		return traffic;
	}

	public void setTraffic(long traffic) {
		this.traffic = traffic;
	}

	public LiuLiangData() {
		super();
	}

	public LiuLiangData(String time, long traffic) {
		super();
		this.time = time;
		this.traffic = traffic;
	}

	public LiuLiangData(long traffic) {
		super();
		this.traffic = traffic;
	}		
		
}
