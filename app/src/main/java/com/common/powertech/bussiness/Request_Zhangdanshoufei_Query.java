package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Zhangdanshoufei_Query extends ParentRequset {

	private static String ReceID = ""; // 应收账单ID 多条以“|”分割
	private static String Amt = ""; // 应收金额

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(ReceID))) {
			body += "<RECE_ID>" + ReceID + "</RECE_ID>";
		}

		if (!"".equals(StringUtil.convertStringNull(Amt))) {
			body += "<AMT>" + Amt + "</AMT>";
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
		paramMap.put("RECE_ID", ReceID);
		paramMap.put("AMT", Amt);

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

	public static String getDate() {
		return date;
	}

	public static void setDate(String date) {
		Request_Zhangdanshoufei_Query.date = date;
	}

	public static String getReceID() {
		return ReceID;
	}

	public static void setReceID(String receID) {
		ReceID = receID;
	}

	public static String getAmt() {
		return Amt;
	}

	public static void setAmt(String amt) {
		Amt = amt;
	}

}
