package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by ouyangguozhao on 2015/10/24.
 */
public class Request_ShouDianFee_Query extends ParentRequset {

	private static String TokenCode = "";//Token支付码
//	private static  String PinCode = "";//Pin 支付码
	private static String Amt = ""; // 购电金额
	private static String MeterNo = ""; // 购电表号
	private static String PayWays = ""; // 支付方式
	private static String ReceID = ""; // 应收账单ID 多条以“|”分割
	private static String PrdType = ""; // 资源类型
	private static String ProductCode = ""; // 产品代码
	private static String PowerType = ""; // 套餐类型（短信、流量）
	private static String EnelId = ""; // 电力公司
	private static String PrdOrdNo = "";

	private static String IcType = ""; // IC卡类型 1-4428卡；2-4442卡

	private static String ICJsonReq = ""; // 读卡信息
	
	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(Amt))) {
			body += "<AMT>" + Amt + "</AMT>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(MeterNo))) {
			body += "<METER_NO>" + MeterNo + "</METER_NO>";
		}
		if (!"".equals(StringUtil.convertStringNull(PrdType))) {
			body += "<RESOURCE_TYPE>" + PrdType + "</RESOURCE_TYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(ProductCode))) {
			body += "<PRODUCT_CODE>" + ProductCode + "</PRODUCT_CODE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(PowerType))) {
			body += "<POWER_TYPE>" + PowerType + "</POWER_TYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(EnelId))) {
			body += "<ENEL_ID>" + EnelId + "</ENEL_ID>";
		}
		if (!"".equals(StringUtil.convertStringNull(PayWays))) {
			body += "<PAYWAYS>" + PayWays + "</PAYWAYS>";
		}
		if (!"".equals(StringUtil.convertStringNull(ReceID))) {
			body += "<RECE_ID>" + ReceID + "</RECE_ID>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(IcType))) {
			body += "<IC_TYPE>" + IcType + "</IC_TYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(ICJsonReq))) {
			body += "<IC_JSON_REQ>" + ICJsonReq + "</IC_JSON_REQ>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(PrdOrdNo))) {
			body += "<PRDORDNO>" + PrdOrdNo + "</PRDORDNO>";
		}
		if (!"".equals(StringUtil.convertStringNull(TokenCode))) {
			body += "<TOKENCODE>" + TokenCode + "</TOKENCODE>";
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
		paramMap.put("AMT", Amt);
		paramMap.put("METER_NO", MeterNo);
		paramMap.put("PAYWAYS", PayWays);
		paramMap.put("RECE_ID", ReceID);
		paramMap.put("IC_TYPE",IcType);
		paramMap.put("IC_JSON_REQ",ICJsonReq);
		paramMap.put("PRDORDNO", PrdOrdNo);
		paramMap.put("TOKENCODE", TokenCode);
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
	public static String getPrdType() {
		return PrdType;
	}

	public static void setPrdType(String prdType) {
		PrdType = prdType;
	}
	
	public static String getProductCode() {
		return ProductCode;
	}

	public static void setProductCode(String productCode) {
		ProductCode = productCode;
	}
	
	public static String getPowerType() {
		return PowerType;
	}

	public static void setPowerType(String powerType) {
		PowerType = powerType;
	}
	
	public static String getEnelId() {
		return EnelId;
	}

	public static void setEnelId(String enelId) {
		EnelId = enelId;
	}

	public static String getDate() {
		return date;
	}

	public static void setDate(String date) {
		Request_ShouDianFee_Query.date = date;
	}

	public static String getAmt() {
		return Amt;
	}

	public static void setAmt(String amt) {
		Amt = amt;
	}
	
	public static String getMeterNo() {
		return MeterNo;
	}

	public static void setMeterNo(String meterno) {
		MeterNo = meterno;
	}
	public static String getPayWays() {
		return PayWays;
	}

	public static void setPayWays(String payWays) {
		PayWays = payWays;
	}
	
	public static String getIcType() {
		return IcType;
	}

	public static void setIcType(String icType) {
		IcType = icType;
	}

	public static String getICJsonReq() {
		return ICJsonReq;
	}

	public static void setICJsonReq(String iCJsonReq) {
		ICJsonReq = iCJsonReq;
	}
	public static String getReceID() {
		return ReceID;
	}

	public static void setReceID(String receID) {
		ReceID = receID;
	}
	
	public static String getPrdOrdNo() {
		return PrdOrdNo;
	}

	public static void setPrdOrdNo(String prdOrdNo) {
		PrdOrdNo = prdOrdNo;
	}

	public static String getTokenCode() {
		return TokenCode;
	}

	public static void setTokenCode(String tokenCode) {
		TokenCode = tokenCode;
	}



}
