package com.common.powertech.bussiness;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;

import java.util.TreeMap;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Refund_Token extends ParentRequset {


	private String REFUND_PIN = ""; // 退款PIN
	private String PRDORDNO = "";//订单号

	public String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}
	
	public String getBODY() {
		String body = "";
		body = "<BODY>";
		body += "<REFUND_PIN>" + REFUND_PIN + "</REFUND_PIN>";
		body += "<PRDORDNO>" + PRDORDNO + "</PRDORDNO>"  + "</BODY>";

		return body;
	}

	public String getMD5str() {

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		// TOP
		paramMap.put("IMEI", SystemUtil.getIMEI(context));
		paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
		paramMap.put("REQUEST_TIME", date);
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
		paramMap.put("REFUND_PIN", REFUND_PIN);
		paramMap.put("PRDORDNO", PRDORDNO);

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

	public String getREFUND_PIN() {
		return REFUND_PIN;
	}

	public void setREFUND_PIN(String REFUND_PIN) {
		this.REFUND_PIN = REFUND_PIN;
	}

	public String getPRDORDNO() {
		return PRDORDNO;
	}

	public void setPRDORDNO(String PRDORDNO) {
		this.PRDORDNO = PRDORDNO;
	}
}
