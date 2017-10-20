package com.common.powertech.dbbean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="printer_temp")
public class PrinterTemp {
	
	@DatabaseField(generatedId=true,useGetSet=true)
	private int id;
	
    @DatabaseField(useGetSet=true,columnName="temp_version")
    private String temp_version;   
    
    @DatabaseField(useGetSet=true,columnName="temp_list")
    private String temp_list;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTemp_version() {
		return temp_version;
	}

	public void setTemp_version(String temp_version) {
		this.temp_version = temp_version;
	}

	public String getTemp_list() {
		return temp_list;
	}

	public void setTemp_list(String temp_list) {
		this.temp_list = temp_list;
	}	

	public PrinterTemp() {
		super();
	}

    public PrinterTemp(String temp_version, String temp_list) {
	    super();
	    this.temp_version = temp_version;
	    this.temp_list = temp_list;
    }
       
}
