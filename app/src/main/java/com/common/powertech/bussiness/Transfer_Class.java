package com.common.powertech.bussiness;

public class Transfer_Class {

    private  String PRDORDNO;   //转账订单号
    private  String TRANSFER_AMT;   //转账金额
    private  String ORDSTATUS;   //转账状态
    private  String TOKEN_TYPE; // 转账类型
    private  String TOKEN_USER; //转账对象
    private  String ORDERTIME; //订单时间
    private  String PIN_PHONE;//手机号
    
	public String getPRDORDNO() {
		return PRDORDNO;
	}
	public void setPRDORDNO(String pRDORDNO) {
		PRDORDNO = pRDORDNO;
	}
	public String getTRANSFER_AMT() {
		return TRANSFER_AMT;
	}
	public void setTRANSFER_AMT(String tRANSFER_AMT) {
		TRANSFER_AMT = tRANSFER_AMT;
	}
	public String getORDSTATUS() {
		return ORDSTATUS;
	}
	public void setORDSTATUS(String oRDSTATUS) {
		ORDSTATUS = oRDSTATUS;
	}
	public String getTOKEN_TYPE() {
		return TOKEN_TYPE;
	}
	public void setTOKEN_TYPE(String tOKEN_TYPE) {
		TOKEN_TYPE = tOKEN_TYPE;
	}
	public String getTOKEN_USER() {
		return TOKEN_USER;
	}
	public void setTOKEN_USER(String tOKEN_USER) {
		TOKEN_USER = tOKEN_USER;
	}
	public String getORDERTIME() {
		return ORDERTIME;
	}
	public void setORDERTIME(String oRDERTIME) {
		ORDERTIME = oRDERTIME;
	}
	public String getPIN_PHONE() {
		return PIN_PHONE;
	}
	public void setPIN_PHONE(String pIN_PHONE) {
		PIN_PHONE = pIN_PHONE;
	}
    

}
