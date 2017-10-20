package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Complete_Order extends ParentRequset {


	private static String VERIFY_CODE = ""; // 验证码
	private static String TRANSFER_NO = ""; // 转账订单

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}
	
	public static String getBODY() {
		String body = "";
		body = "<BODY>";
		body += "<TRANSFER_NO>" + TRANSFER_NO + "</TRANSFER_NO>";
		body += "<VERIFY_CODE>" + VERIFY_CODE + "</VERIFY_CODE>"  + "</BODY>";

		return body;
	}

	public static String getMD5str() {

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		// TOP
		paramMap.put("IMEI", SystemUtil.getIMEI(context));
		paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
		paramMap.put("REQUEST_TIME", date);
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
		paramMap.put("VERIFY_CODE", VERIFY_CODE);
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

	public static String getVERIFY_CODE() {
		return VERIFY_CODE;
	}

	public static void setVERIFY_CODE(String vERIFY_CODE) {
		VERIFY_CODE = vERIFY_CODE;
	}

	public static String getTRANSFER_NO() {
		return TRANSFER_NO;
	}

	public static void setTRANSFER_NO(String tRANSFER_NO) {
		TRANSFER_NO = tRANSFER_NO;
	}



}
