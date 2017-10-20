package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Query_Token extends ParentRequset {


	private String REFUND_TOKEN = ""; // 退款TOKEN

	public String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}
	
	public String getBODY() {
		String body = "";
		body = "<BODY>";
		body += "<REFUND_TOKEN>" + REFUND_TOKEN + "</REFUND_TOKEN>"  + "</BODY>";

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
		paramMap.put("REFUND_TOKEN", REFUND_TOKEN);

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

	public String getREFUND_TOKEN() {
		return REFUND_TOKEN;
	}

	public void setREFUND_TOKEN(String rEFUND_TOKEN) {
		REFUND_TOKEN = rEFUND_TOKEN;
	}

}
