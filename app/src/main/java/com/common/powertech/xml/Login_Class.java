package com.common.powertech.xml;

import java.util.Map;
import java.util.List;

public class Login_Class {
	
	private String KEY,SESSION_ID,LAW_NAME,OPER_NAME,CASH_AC_BAL,BUY_ELE_WAY
	               ,PAY_PWD,OPER_LIST,DISPLAY,VOICE,LANGUAGE,LOCKTIME,ZIP
	               ,READ4428,READ4442,MCARD,B4,De,B9,LASERH,PNO,MINRECHARGE,PRDTYPEALL,PHONEAMOUNTCONFIG;

	
	private List<String> MENU_ID,MENU_NAME,MENU_NAME_EN,MENU_NAME_FR;
	private Map<String,String> ENEL_GRP1,ENEL_GRP2,ENEL_GRP3,ENEL_GRP4;

	public String getMINRECHARGE() {
		return MINRECHARGE;
	}
	public void setMINRECHARGE(String mINRECHARGE) {
		MINRECHARGE = mINRECHARGE;
	}
	public Map<String,String> getENEL_GRP1() {
		return ENEL_GRP1;
	}
	public void setENEL_GRP1(Map<String,String> eNEL_GRP1) {
		ENEL_GRP1 = eNEL_GRP1;
	}
	public Map<String, String> getENEL_GRP2() {
		return ENEL_GRP2;
	}
	public void setENEL_GRP2(Map<String, String> eNEL_GRP2) {
		ENEL_GRP2 = eNEL_GRP2;
	}
	public Map<String, String> getENEL_GRP3() {
		return ENEL_GRP3;
	}
	public void setENEL_GRP3(Map<String, String> eNEL_GRP3) {
		ENEL_GRP3 = eNEL_GRP3;
	}
	public Map<String, String> getENEL_GRP4() {
		return ENEL_GRP4;
	}
	public void setENEL_GRP4(Map<String, String> eNEL_GRP4) {
		ENEL_GRP4 = eNEL_GRP4;
	}
	public String getPRDTYPEALL() {
		return PRDTYPEALL;
	}
	public void setPRDTYPEALL(String pRDTYPEALL) {
		PRDTYPEALL = pRDTYPEALL;
	}
	
	public String getPHONEAMOUNTCONFIG() {
		return PHONEAMOUNTCONFIG;
	}
	public void setPHONEAMOUNTCONFIG(String pHONEAMOUNTCONFIG) {
		PHONEAMOUNTCONFIG = pHONEAMOUNTCONFIG;
	}

	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	public String getSESSION_ID() {
		return SESSION_ID;
	}
	public void setSESSION_ID(String sESSION_ID) {
		SESSION_ID = sESSION_ID;
	}
	public String getLAW_NAME() {
		return LAW_NAME;
	}
	public void setLAW_NAME(String lAW_NAME) {
		LAW_NAME = lAW_NAME;
	}
	public String getOPER_NAME() {
		return OPER_NAME;
	}
	public void setOPER_NAME(String oPER_NAME) {
		OPER_NAME = oPER_NAME;
	}
	public String getCASH_AC_BAL() {
		return CASH_AC_BAL;
	}
	public void setCASH_AC_BAL(String cASH_AC_BAL) {
		CASH_AC_BAL = cASH_AC_BAL;
	}
	public String getBUY_ELE_WAY() {
		return BUY_ELE_WAY;
	}
	public void setBUY_ELE_WAY(String bUY_ELE_WAY) {
		BUY_ELE_WAY = bUY_ELE_WAY;
	}
	public String getPAY_PWD() {
		return PAY_PWD;
	}
	public void setPAY_PWD(String pAY_PWD) {
		PAY_PWD = pAY_PWD;
	}
	public String getOPER_LIST() {
		return OPER_LIST;
	}
	public void setOPER_LIST(String oPER_LIST) {
		OPER_LIST = oPER_LIST;
	}
	public String getDISPLAY() {
		return DISPLAY;
	}
	public void setDISPLAY(String dISPLAY) {
		DISPLAY = dISPLAY;
	}
	public String getVOICE() {
		return VOICE;
	}
	public void setVOICE(String vOICE) {
		VOICE = vOICE;
	}
	public String getLANGUAGE() {
		return LANGUAGE;
	}
	public void setLANGUAGE(String lANGUAGE) {
		LANGUAGE = lANGUAGE;
	}
	public String getLOCKTIME() {
		return LOCKTIME;
	}
	public void setLOCKTIME(String lOCKTIME) {
		LOCKTIME = lOCKTIME;
	}
	public String getZIP() {
		return ZIP;
	}
	public void setZIP(String zIP) {
		ZIP = zIP;
	}
	public String getREAD4428() {
		return READ4428;
	}
	public void setREAD4428(String rEAD4428) {
		READ4428 = rEAD4428;
	}
	public String getREAD4442() {
		return READ4442;
	}
	public void setREAD4442(String rEAD4442) {
		READ4442 = rEAD4442;
	}
	public List<String> getMENU_ID() {
		return MENU_ID;
	}
	public void setMENU_ID(List<String> mENU_ID) {
		MENU_ID = mENU_ID;
	}
	public List<String> getMENU_NAME() {
		return MENU_NAME;
	}
	public void setMENU_NAME(List<String> mENU_NAME) {
		MENU_NAME = mENU_NAME;
	}
	public List<String> getMENU_NAME_EN() {
		return MENU_NAME_EN;
	}
	public void setMENU_NAME_EN(List<String> mENU_NAME_EN) {
		MENU_NAME_EN = mENU_NAME_EN;
	}
	public List<String> getMENU_NAME_FR() {
		return MENU_NAME_FR;
	}
	public void setMENU_NAME_FR(List<String> mENU_NAME_FR) {
		MENU_NAME_FR = mENU_NAME_FR;
	}
	public String getMCARD() {
		return MCARD;
	}
	public void setMCARD(String mCARD) {
		MCARD = mCARD;
	}
	public String getB4() {
		return B4;
	}
	public void setB4(String b4) {
		B4 = b4;
	}
	public String getDe() {
		return De;
	}
	public void setDe(String de) {
		De = de;
	}
	public String getB9() {
		return B9;
	}
	public void setB9(String b9) {
		B9 = b9;
	}
	public String getPNO() {
		return PNO;
	}
	public void setPNO(String pNO) {
		PNO = pNO;
	}
	public String getLASERH() {
		return LASERH;
	}
	public void setLASERH(String lASERH) {
		LASERH = lASERH;
	}
		
}
