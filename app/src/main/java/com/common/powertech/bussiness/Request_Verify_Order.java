package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Verify_Order extends ParentRequset {


	private String TRANS_CUST = ""; // 转账人
	private String PIN_PHONE = ""; // 转账手机号
	private String TRANSFER_AMT = ""; // 转账金额
	private String PAY_PWD = ""; // 转账金额

	public String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}
	
	public String getBODY() {
		String body = "";
		body = "<BODY>";
		body += "<TRANS_CUST>" + TRANS_CUST + "</TRANS_CUST>";
		body += "<TRANSFER_AMT>" + TRANSFER_AMT + "</TRANSFER_AMT>";
		if(PAY_PWD != null && !"".equals(PAY_PWD))
			body += "<PAY_PWD>" + PAY_PWD + "</PAY_PWD>";
		body += "<PIN_PHONE>" + PIN_PHONE + "</PIN_PHONE>"  + "</BODY>";

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
		paramMap.put("TRANS_CUST", TRANS_CUST);
		paramMap.put("PIN_PHONE", PIN_PHONE);
		paramMap.put("ATRANSFER_AMT", TRANSFER_AMT);
		paramMap.put("PAY_PWD", PAY_PWD);

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

	public String getTRANS_CUST() {
		return TRANS_CUST;
	}

	public void setTRANS_CUST(String tRANS_CUST) {
		TRANS_CUST = tRANS_CUST;
	}

	public String getPIN_PHONE() {
		return PIN_PHONE;
	}

	public void setPIN_PHONE(String pIN_PHONE) {
		PIN_PHONE = pIN_PHONE;
	}

	public String getTRANSFER_AMT() {
		return TRANSFER_AMT;
	}

	public void setTRANSFER_AMT(String tRANSFER_AMT) {
		TRANSFER_AMT = tRANSFER_AMT;
	}

	public String getPAY_PWD() {
		return PAY_PWD;
	}

	public void setPAY_PWD(String pAY_PWD) {
		PAY_PWD = pAY_PWD;
	}



}
