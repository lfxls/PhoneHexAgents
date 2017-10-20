package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by ouyangguozhao on 2015/11/20.
 */
public class Request_Bangka extends ParentRequset {

	private static String CardNo = "";// 用户绑卡卡号
	private static String CardType = "";// 用户卡类型
	private static String PrdordNo = "";// 交易单号

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(CardNo))) {
			body += "<CARD_NO>" + CardNo + "</CARD_NO>";
		}

		if (!"".equals(StringUtil.convertStringNull(CardType))) {
			body += "<CARD_TYPE>" + CardType + "</CARD_TYPE>";
		}

		if (!"".equals(StringUtil.convertStringNull(PrdordNo))) {
			body += "<PRDORDNO>" + PrdordNo + "</PRDORDNO>";
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
		paramMap.put("CARD_NO", CardNo);
		paramMap.put("CARD_TYPE", CardType);
		paramMap.put("PRDORDNO", PrdordNo);

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
		Request_Bangka.date = date;
	}

	public static String getCardNo() {
		return CardNo;
	}

	public static void setCardNo(String cardNo) {
		CardNo = cardNo;
	}

	public static String getCardType() {
		return CardType;
	}

	public static void setCardType(String cardType) {
		CardType = cardType;
	}

	public static String getPrdordNo() {
		return PrdordNo;
	}

	public static void setPrdordNo(String prdordNo) {
		PrdordNo = prdordNo;
	}

}
