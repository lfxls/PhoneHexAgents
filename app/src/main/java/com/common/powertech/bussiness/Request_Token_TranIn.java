package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by ouyangguozhao on 2015/10/24.
 */
public class Request_Token_TranIn extends ParentRequset {

	private static String TRANSFER_TOKEN = ""; // 转账Token
	private static String PIN = ""; // 转账验证码
	private static String OPER_TYPE = ""; // 操作方式1:查询 2:处理

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(OPER_TYPE))) {
			body += "<OPER_TYPE>" + OPER_TYPE + "</OPER_TYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(PIN))) {
			body += "<PIN>" + PIN + "</PIN>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(TRANSFER_TOKEN))) {
			body += "<TRANSFER_TOKEN>" + TRANSFER_TOKEN + "</TRANSFER_TOKEN>";
		}
		
		body += "</BODY>";
		return body;
	}

	public static String getMD5str() {

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		// TOP
		paramMap.put("IMEI", SystemUtil.getIMEI(context));
		paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
		paramMap.put("REQUEST_TIME", date);
		//paramMap.put("SOURCE", "3");
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
		paramMap.put("TRANSFER_TOKEN", TRANSFER_TOKEN);
		paramMap.put("PIN", PIN);
		paramMap.put("OPER_TYPE", OPER_TYPE);
		// TAIL
		paramMap.put("SIGN_TYPE", "1");

		String signStr = bulidParam(paramMap);
		String sign = "";
		try {
			sign = Md5Algorithm.getInstance().md5Digest(signStr.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}

	public static String getTRANSFER_TOKEN() {
		return TRANSFER_TOKEN;
	}

	public static void setTRANSFER_TOKEN(String tRANSFER_TOKEN) {
		TRANSFER_TOKEN = tRANSFER_TOKEN;
	}

	public static String getPIN() {
		return PIN;
	}

	public static void setPIN(String pIN) {
		PIN = pIN;
	}

	public static String getOPER_TYPE() {
		return OPER_TYPE;
	}

	public static void setOPER_TYPE(String oPER_TYPE) {
		OPER_TYPE = oPER_TYPE;
	}

	
}
