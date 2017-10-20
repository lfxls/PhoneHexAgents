package com.common.powertech.bussiness;

import android.os.Build;

import com.common.powertech.activity.LoginActivity;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;
import org.apache.axis.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 */
public class Request_Signup_Confirm extends ParentRequset {
	private static String STORE_NAME = "";//代理商名称
	private static String AGENT_TYPE = "";//代理商类型
	private static String BUSINESS_SCOPE = "";//经营范围
	private static String BALANCE_REM = "";//开通余额提醒
	private static String REM_AMT = "";//余额
	private static String LAW_NAME = "";//法人代表
	private static String BUSINESS_CHANNELS = "";//购电渠道
	private static String LAW_PERSON_CRET_TYPE = "";//证件类型
	private static String CRET_FRONT_PICTURE = "";//证件正面
	private static String CRET_BACK_PICTURE = "";//证件反面
	private static String CRET_FRONT_PICTURE_TYPE = "";//证件正面类型
	private static String CRET_BACK_PICTURE_TYPE = "";//证件反面类型
	private static String LAW_PERSON_CRET_NO = "";//证件号码
	private static String CERT_DATE_END = "";//证件有效期
	private static String BUY_ELE_WAY = "";//无支付密码购电 
	private static String PAY_PASSWD = "";//支付密码
	private static String TAX_REGISTRATION_NO = "";//税务证号
	private static String SERVICE_TEL = "";//服务电话
	private static String SUB_ADDRESS = "";//办公地址
	private static String CONTACTS_NAME = "";//联系人
	private static String CONTACTS_PHONE = "";//手机
	private static String CONTACTS_EMAIL = "";//联系人邮箱
	private static String POST_CODE = "";//邮政编码
	private static String LOGIN_USER_ID = "";//登录名
	private static String LOGIN_USER_PASSWD = "";//登录密码
	
	
	public static String getSTORE_NAME() {
		return STORE_NAME;
	}

	public static void setSTORE_NAME(String sTORE_NAME) {
		STORE_NAME = sTORE_NAME;
	}

	public static String getAGENT_TYPE() {
		return AGENT_TYPE;
	}

	public static void setAGENT_TYPE(String aGENT_TYPE) {
		AGENT_TYPE = aGENT_TYPE;
	}

	public static String getBUSINESS_SCOPE() {
		return BUSINESS_SCOPE;
	}

	public static void setBUSINESS_SCOPE(String bUSINESS_SCOPE) {
		BUSINESS_SCOPE = bUSINESS_SCOPE;
	}

	public static String getBALANCE_REM() {
		return BALANCE_REM;
	}

	public static void setBALANCE_REM(String bALANCE_REM) {
		BALANCE_REM = bALANCE_REM;
	}

	public static String getREM_AMT() {
		return REM_AMT;
	}

	public static void setREM_AMT(String rEM_AMT) {
		REM_AMT = rEM_AMT;
	}

	public static String getLAW_NAME() {
		return LAW_NAME;
	}

	public static void setLAW_NAME(String lAW_NAME) {
		LAW_NAME = lAW_NAME;
	}

	public static String getBUSINESS_CHANNELS() {
		return BUSINESS_CHANNELS;
	}

	public static void setBUSINESS_CHANNELS(String bUSINESS_CHANNELS) {
		BUSINESS_CHANNELS = bUSINESS_CHANNELS;
	}


	public static String getLAW_PERSON_CRET_TYPE() {
		return LAW_PERSON_CRET_TYPE;
	}

	public static void setLAW_PERSON_CRET_TYPE(String lAW_PERSON_CRET_TYPE) {
		LAW_PERSON_CRET_TYPE = lAW_PERSON_CRET_TYPE;
	}
	
	public static String getCRET_FRONT_PICTURE() {
		return CRET_FRONT_PICTURE;
	}

	public static void setCRET_FRONT_PICTURE(String cRET_FRONT_PICTURE) {
		CRET_FRONT_PICTURE = cRET_FRONT_PICTURE;
	}

	public static String getCRET_BACK_PICTURE() {
		return CRET_BACK_PICTURE;
	}

	public static void setCRET_BACK_PICTURE(String cRET_BACK_PICTURE) {
		CRET_BACK_PICTURE = cRET_BACK_PICTURE;
	}

	public static String getCRET_FRONT_PICTURE_TYPE() {
		return CRET_FRONT_PICTURE_TYPE;
	}

	public static void setCRET_FRONT_PICTURE_TYPE(String cRET_FRONT_PICTURE_TYPE) {
		CRET_FRONT_PICTURE_TYPE = cRET_FRONT_PICTURE_TYPE;
	}

	public static String getCRET_BACK_PICTURE_TYPE() {
		return CRET_BACK_PICTURE_TYPE;
	}

	public static void setCRET_BACK_PICTURE_TYPE(String cRET_BACK_PICTURE_TYPE) {
		CRET_BACK_PICTURE_TYPE = cRET_BACK_PICTURE_TYPE;
	}

	public static String getLAW_PERSON_CRET_NO() {
		return LAW_PERSON_CRET_NO;
	}

	public static void setLAW_PERSON_CRET_NO(String lAW_PERSON_CRET_NO) {
		LAW_PERSON_CRET_NO = lAW_PERSON_CRET_NO;
	}
	public static String getCERT_DATE_END() {
		return CERT_DATE_END;
	}

	public static void setCERT_DATE_END(String cERT_DATE_END) {
		CERT_DATE_END = cERT_DATE_END;
	}

	public static String getBUY_ELE_WAY() {
		return BUY_ELE_WAY;
	}

	public static void setBUY_ELE_WAY(String bUY_ELE_WAY) {
		BUY_ELE_WAY = bUY_ELE_WAY;
	}

	public static String getPAY_PASSWD() {
		return PAY_PASSWD;
	}

	public static void setPAY_PASSWD(String pAY_PASSWD) {
		PAY_PASSWD = pAY_PASSWD;
	}

	public static String getTAX_REGISTRATION_NO() {
		return TAX_REGISTRATION_NO;
	}

	public static void setTAX_REGISTRATION_NO(String tAX_REGISTRATION_NO) {
		TAX_REGISTRATION_NO = tAX_REGISTRATION_NO;
	}

	public static String getSERVICE_TEL() {
		return SERVICE_TEL;
	}

	public static void setSERVICE_TEL(String sERVICE_TEL) {
		SERVICE_TEL = sERVICE_TEL;
	}

	public static String getSUB_ADDRESS() {
		return SUB_ADDRESS;
	}

	public static void setSUB_ADDRESS(String sUB_ADDRESS) {
		SUB_ADDRESS = sUB_ADDRESS;
	}

	public static String getCONTACTS_NAME() {
		return CONTACTS_NAME;
	}

	public static void setCONTACTS_NAME(String cONTACTS_NAME) {
		CONTACTS_NAME = cONTACTS_NAME;
	}

	public static String getCONTACTS_PHONE() {
		return CONTACTS_PHONE;
	}

	public static void setCONTACTS_PHONE(String cONTACTS_PHONE) {
		CONTACTS_PHONE = cONTACTS_PHONE;
	}

	public static String getCONTACTS_EMAIL() {
		return CONTACTS_EMAIL;
	}

	public static void setCONTACTS_EMAIL(String cONTACTS_EMAIL) {
		CONTACTS_EMAIL = cONTACTS_EMAIL;
	}

	public static String getPOST_CODE() {
		return POST_CODE;
	}

	public static void setPOST_CODE(String pOST_CODE) {
		POST_CODE = pOST_CODE;
	}

	public static String getLOGIN_USER_ID() {
		return LOGIN_USER_ID;
	}

	public static void setLOGIN_USER_ID(String lOGIN_USER_ID) {
		LOGIN_USER_ID = lOGIN_USER_ID;
	}

	public static String getLOGIN_USER_PASSWD() {
		return LOGIN_USER_PASSWD;
	}

	public static void setLOGIN_USER_PASSWD(String lOGIN_USER_PASSWD) {
		LOGIN_USER_PASSWD = lOGIN_USER_PASSWD;
	}
	
    private static String getBODY(){
        String body="";
        body = "<BODY>" +
                "<STORE_NAME>"+STORE_NAME+"</STORE_NAME>" +
                "<AGENT_TYPE>"+AGENT_TYPE+"</AGENT_TYPE>" +
                "<BUSINESS_SCOPE>"+BUSINESS_SCOPE+"</BUSINESS_SCOPE>" +
                "<BALANCE_REM>"+BALANCE_REM+"</BALANCE_REM>" +
                "<REM_AMT>"+REM_AMT+"</REM_AMT>" +
                "<LAW_NAME>"+LAW_NAME+"</LAW_NAME>" +
                "<BUSINESS_CHANNELS>"+BUSINESS_CHANNELS+"</BUSINESS_CHANNELS>" +
                "<LAW_PERSON_CRET_TYPE>"+LAW_PERSON_CRET_TYPE+"</LAW_PERSON_CRET_TYPE>" +
                "<CRET_FRONT_PICTURE>"+CRET_FRONT_PICTURE+"</CRET_FRONT_PICTURE>" +
                "<CRET_BACK_PICTURE>"+CRET_BACK_PICTURE+"</CRET_BACK_PICTURE>" +
                "<CRET_FRONT_PICTURE_TYPE>"+CRET_FRONT_PICTURE_TYPE+"</CRET_FRONT_PICTURE_TYPE>" +
                "<CRET_BACK_PICTURE_TYPE>"+CRET_BACK_PICTURE_TYPE+"</CRET_BACK_PICTURE_TYPE>" +
                "<LAW_PERSON_CRET_NO>"+LAW_PERSON_CRET_NO+"</LAW_PERSON_CRET_NO>" +
                "<CERT_DATE_END>"+CERT_DATE_END+"</CERT_DATE_END>" +
                "<BUY_ELE_WAY>"+BUY_ELE_WAY+"</BUY_ELE_WAY>" +
                "<PAY_PASSWD>"+PAY_PASSWD+"</PAY_PASSWD>" +
                "<TAX_REGISTRATION_NO>"+TAX_REGISTRATION_NO+"</TAX_REGISTRATION_NO>" +
                "<SERVICE_TEL>"+SERVICE_TEL+"</SERVICE_TEL>" +
                "<SUB_ADDRESS>"+SUB_ADDRESS+"</SUB_ADDRESS>" +
                "<CONTACTS_NAME>"+CONTACTS_NAME+"</CONTACTS_NAME>" +
                "<CONTACTS_PHONE>"+CONTACTS_PHONE+"</CONTACTS_PHONE>" +
                "<CONTACTS_EMAIL>"+CONTACTS_EMAIL+"</CONTACTS_EMAIL>" +
                "<POST_CODE>"+POST_CODE+"</POST_CODE>" +
                "<LOGIN_USER_ID>"+LOGIN_USER_ID+"</LOGIN_USER_ID>" +
                "<LOGIN_USER_PASSWD>"+LOGIN_USER_PASSWD+"</LOGIN_USER_PASSWD>" +
                "</BODY>";

        return body;
    }

    private static String getMD5str(){

        TreeMap<String, String> paramMap = new TreeMap<String, String>();
        //TOP
        paramMap.put("IMEI", SystemUtil.getIMEI(context));
        paramMap.put("SOURCE","3");
        paramMap.put("REQUEST_TIME", date);
        paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

        //BODY
        paramMap.put("STORE_NAME", STORE_NAME);
		paramMap.put("AGENT_TYPE", AGENT_TYPE);
		paramMap.put("BUSINESS_SCOPE", BUSINESS_SCOPE);
		paramMap.put("BALANCE_REM", BALANCE_REM);
		paramMap.put("REM_AMT", REM_AMT);
		paramMap.put("LAW_NAME", LAW_NAME);
		paramMap.put("BUSINESS_CHANNELS", BUSINESS_CHANNELS);
		paramMap.put("LAW_PERSON_CRET_TYPE", LAW_PERSON_CRET_TYPE);
		paramMap.put("CRET_FRONT_PICTURE", CRET_FRONT_PICTURE);
		paramMap.put("CRET_BACK_PICTURE", CRET_BACK_PICTURE);
		paramMap.put("CRET_FRONT_PICTURE_TYPE", CRET_FRONT_PICTURE_TYPE);
		paramMap.put("CRET_BACK_PICTURE_TYPE", CRET_BACK_PICTURE_TYPE);
		paramMap.put("LAW_PERSON_CRET_NO", LAW_PERSON_CRET_NO);
		paramMap.put("CERT_DATE_END", CERT_DATE_END);
		paramMap.put("BUY_ELE_WAY", BUY_ELE_WAY);
		paramMap.put("PAY_PASSWD", PAY_PASSWD);
		paramMap.put("TAX_REGISTRATION_NO ", TAX_REGISTRATION_NO);
		paramMap.put("SERVICE_TEL", SERVICE_TEL);
		paramMap.put("SUB_ADDRESS", SUB_ADDRESS);
		paramMap.put("CONTACTS_NAME", CONTACTS_NAME);
		paramMap.put("CONTACTS_PHONE", CONTACTS_PHONE);
		paramMap.put("CONTACTS_EMAIL", CONTACTS_EMAIL);
		paramMap.put("POST_CODE", POST_CODE);
		paramMap.put("LOGIN_USER_ID", LOGIN_USER_ID);
		paramMap.put("LOGIN_USER_PASSWD", LOGIN_USER_PASSWD);

        //TAIL
        paramMap.put("SIGN_TYPE", "1");

        String signStr = bulidParam(paramMap);
        String sign = Md5Algorithm.getInstance().md5Digest(signStr.getBytes());
        return sign;
    }

    public static String getRequsetXML() {
		String xmlTop = "<TOP><SOURCE>3</SOURCE><IMEI>"
				+ SystemUtil.getIMEI(context)
				+ "</IMEI><REQUEST_TIME>" + SystemUtil.getCurrentDateTimeHH24()
				+ "</REQUEST_TIME><SOURCE>3</SOURCE><LOCAL_LANGUAGE>"
				+ SystemUtil.getLocalLanguage(context)
				+ "</LOCAL_LANGUAGE></TOP>";
        return "<ROOT>" + xmlTop + getBODY() + getTAIL(getMD5str())
                + "</ROOT>";
    }

}
