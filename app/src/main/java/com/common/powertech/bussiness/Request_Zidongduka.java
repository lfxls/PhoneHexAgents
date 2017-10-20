package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by ouyangguozhao on 2015/11/20.
 */
public class Request_Zidongduka extends ParentRequset {

	private static String IcType = "";// IC卡类型    1-4428卡；2-4442卡；3-……
	private static String IcJsonReq = "";// 读卡信息

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(IcType))) {
			body += "<IC_TYPE>" + IcType + "</IC_TYPE>";
		}

		if (!"".equals(StringUtil.convertStringNull(IcJsonReq))) {
			body += "<IC_JSON_REQ>" + IcJsonReq + "</IC_JSON_REQ>";
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
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
		paramMap.put("IC_TYPE", IcType);
		paramMap.put("IC_JSON_REQ", IcJsonReq);

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

//	public static String getDate() {
//		return date;
//	}
//
//	public static void setDate(String date) {
//		Request_Zidongduka.date = date;
//	}

	public static String getIcType() {
		return IcType;
	}

	public static void setIcType(String icType) {
		IcType = icType;
	}

	public static String getIcJsonReq() {
		return IcJsonReq;
	}

	public static void setIcJsonReq(String icJsonReq) {
		IcJsonReq = icJsonReq;
	}

}
