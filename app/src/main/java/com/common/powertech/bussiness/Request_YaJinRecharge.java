package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 */
public class Request_YaJinRecharge extends ParentRequset {

	private static String TXAMT = ""; // 充值押金
	private static String REMARK = ""; // 备注
	private static String PAYTYPE ="";//支付宝：1，微信：2，银行卡：3

	//支付宝
	private static String FLAG = "1";  //支付宝：2:支付成功提交， 1:校验生成订单号  || 微信：1:请求获取预订单、2:支付成功提交、3:支付失败确认请求 
	private static String ALPORD = ""; // 支付宝订单号 
	private static String ALPORD_STA = ""; // 支付状态 成功1
	
	//微信
	private static String CHATORD = ""; // 微信订单号 
	private static String CHATORD_STA = ""; // 支付状态 成功1

	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(TXAMT))) {
			body += "<TXAMT>" + TXAMT + "</TXAMT>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(REMARK))) {
			body += "<REMARK>" + REMARK + "</REMARK>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(PAYTYPE))) {
			body += "<PAYTYPE>" + PAYTYPE + "</PAYTYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(FLAG))) {
			body += "<FLAG>" + FLAG + "</FLAG>";
		}

		if (!"".equals(StringUtil.convertStringNull(ALPORD))) {
			body += "<ALPORD>" + ALPORD + "</ALPORD>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(ALPORD_STA))) {
			body += "<ALPORD_STA>" + ALPORD_STA + "</ALPORD_STA>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(CHATORD))) {
			body += "<CHATORD>" + CHATORD + "</CHATORD>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(CHATORD_STA))) {
			body += "<CHATORD_STA>" + CHATORD_STA + "</CHATORD_STA>";
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
		paramMap.put("TXAMT", TXAMT);
		paramMap.put("REMARK", REMARK);
		paramMap.put("PAYTYPE", PAYTYPE);
		paramMap.put("FLAG", FLAG);
		paramMap.put("ALPORD", ALPORD);
		paramMap.put("ALPORD_STA", ALPORD_STA);
		paramMap.put("CHATORD", CHATORD);
		paramMap.put("CHATORD_STA", CHATORD_STA);

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
		Request_YaJinRecharge.date = date;
	}

	public static String getTXAMT() {
		return TXAMT;
	}

	public static void setTXAMT(String tXAMT) {
		TXAMT = tXAMT;
	}

	public static String getREMARK() {
		return REMARK;
	}

	public static void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}
	public static String getFLAG() {
		return FLAG;
	}

	public static void setFLAG(String fLAG) {
		FLAG = fLAG;
	}
	
	public static String getALPORD() {
		return ALPORD;
	}

	public static void setALPORD(String aLPORD) {
		ALPORD = aLPORD;
	}

	public static String getALPORD_STA() {
		return ALPORD_STA;
	}

	public static void setALPORD_STA(String aLPORD_STA) {
		ALPORD_STA = aLPORD_STA;
	}
	
	public static String getPAYTYPE() {
		return PAYTYPE;
	}

	public static void setPAYTYPE(String pAYTYPE) {
		PAYTYPE = pAYTYPE;
	}
	
	public static String getCHATORD() {
		return CHATORD;
	}

	public static void setCHATORD(String cHATORD) {
		CHATORD = cHATORD;
	}

	public static String getCHATORD_STA() {
		return CHATORD_STA;
	}

	public static void setCHATORD_STA(String cHATORD_STA) {
		CHATORD_STA = cHATORD_STA;
	}
}
