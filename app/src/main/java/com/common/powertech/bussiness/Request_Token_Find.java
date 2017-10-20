package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by ouyangguozhao on 2015/10/24.
 */
public class Request_Token_Find extends ParentRequset {

	private static String TRANSFER_NO = ""; // 订单号

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(TRANSFER_NO))) {
			body += "<TRANSFER_NO>" + TRANSFER_NO + "</TRANSFER_NO>";
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
		paramMap.put("TRANSFER_NO", TRANSFER_NO);
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

	public static String getTRANSFER_NO() {
		return TRANSFER_NO;
	}

	public static void setTRANSFER_NO(String tRANSFER_NO) {
		TRANSFER_NO = tRANSFER_NO;
	}


	
}
