package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Shoudianshoufei_Query extends ParentRequset {

	private static String MeterNo = ""; // 表号
	private static String Amt = ""; // 金额
	private static String IcType = ""; // IC卡类型 1-4428卡；2-4442卡
	private static String ICJsonReq = ""; // 读卡信息
	private static String Prdordno = ""; //订单号
	private static String PayWays = ""; //支付方式
	private static String PayAccount="";//账户
    private static String teleNO="";//手机号码
    private static String PINCODE="";//token支付Pin码


	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}

	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(PayAccount))) {
			body += "<PAY_ACCOUNT>" + PayAccount + "</PAY_ACCOUNT>";
		}

		if (!"".equals(StringUtil.convertStringNull(teleNO))) {
			body += "<PHONE_NO>" + teleNO + "</PHONE_NO>";
		}
		
		
		if (!"".equals(StringUtil.convertStringNull(MeterNo))) {
			body += "<METER_NO>" + MeterNo + "</METER_NO>";
		}

		if (!"".equals(StringUtil.convertStringNull(Amt))) {
			body += "<AMT>" + Amt + "</AMT>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(PayWays))) {
			body += "<PAYWAYS>" + PayWays + "</PAYWAYS>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(Prdordno))) {
			body += "<PRDORDNO>" + Prdordno + "</PRDORDNO>";
		}

		if (!"".equals(StringUtil.convertStringNull(IcType))) {
			body += "<IC_TYPE>" + IcType + "</IC_TYPE>";
		}

		if (!"".equals(StringUtil.convertStringNull(ICJsonReq))) {
			body += "<IC_JSON_REQ>" + ICJsonReq + "</IC_JSON_REQ>";
		}

		if (!"".equals(StringUtil.convertStringNull(PINCODE))) {
			body += "<PIN>" + PINCODE + "</PIN>";
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
		paramMap.put("METER_NO", MeterNo);
		paramMap.put("PAYWAYS", PayWays);
		paramMap.put("PRDORDNO", Prdordno);
		paramMap.put("AMT", Amt);
		paramMap.put("IC_TYPE", IcType);
		paramMap.put("IC_JSON_REQ", ICJsonReq);
		paramMap.put("B_ACCOUNT", PayAccount);
        paramMap.put("PHONE_NO", teleNO);
        paramMap.put("PIN", PINCODE);

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
		Request_Shoudianshoufei_Query.date = date;
	}

	public static String getAmt() {
		return Amt;
	}

	public static void setAmt(String amt) {
		Amt = amt;
	}
	
	public static String getPayWays() {
		return PayWays;
	}

	public static void setPayWays(String payWays) {
		PayWays = payWays;
	}
	
	public static String getPrdordno() {
		return Prdordno;
	}

	public static void setPrdordno(String prdordno) {
		Prdordno = prdordno;
	}

	public static String getMeterNo() {
		return MeterNo;
	}

	public static void setMeterNo(String meterNo) {
		MeterNo = meterNo;
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

	public static String getPayAccount() {
		return PayAccount;
	}

	public static void setPayAccount(String payAccount) {
		PayAccount = payAccount;
	}

	public static String getTeleNO() {
		return teleNO;
	}

	public static void setTeleNO(String teleNO) {
		Request_Shoudianshoufei_Query.teleNO = teleNO;
	}

	public static String getPINCODE() {
		return PINCODE;
	}

	public static void setPINCODE(String PINCODE) {
		Request_Shoudianshoufei_Query.PINCODE = PINCODE;
	}
}
