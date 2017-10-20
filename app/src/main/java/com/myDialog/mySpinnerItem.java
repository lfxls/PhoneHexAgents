package com.myDialog;

public class mySpinnerItem {

	private String ID = "";
	private String value = "";
	public mySpinnerItem(){
		
	}
	public mySpinnerItem(String ID,String value){
		this.ID = ID;
		this.value = value;
	}
	public String getID(){
		return ID;
	}
	public String getvalue(){
		return value;
	}
    @Override
    public String toString() {           //适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
      return value.toString();
    }
}
