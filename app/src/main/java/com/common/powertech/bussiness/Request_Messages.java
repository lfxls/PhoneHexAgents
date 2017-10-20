package com.common.powertech.bussiness;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.axis.utils.StringUtils;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;

public class Request_Messages extends ParentRequset{

//	private static String Pagenum = ""; // 当前页码

	public static String getBODY() {
		String body = "";
		body = "<BODY>";
//		if (Pagenum.length() > 0) {
//			body += "<PAGENUM>" + Pagenum + "</PAGENUM>";
//		}
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

		// TAIL
		paramMap.put("SIGN_TYPE", "1");

		String signStr = bulidParam(paramMap);
		String sign = Md5Algorithm.getInstance().md5Digest(signStr.getBytes());
		return sign;
	}

    public static String getRequsetXML() {
        return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
                + "</ROOT>";
    }

}
