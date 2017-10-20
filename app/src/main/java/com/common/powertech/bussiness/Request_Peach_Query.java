package com.common.powertech.bussiness;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.axis.utils.StringUtils;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/23.
 */
public class Request_Peach_Query extends ParentRequset{



    




	/****
	 * String PageNum --- 当前页码 默认1
	 * 
	 * String NumperPag --- 每页数据条 默认10
	 * 
	 * String FromDate --- 开始时间 时间格式：2014-12-27
	 * 
	 * String ToDate --- 结束时间 时间格式：2014-12-27
	 * 
	 * ***/
	public static String getBODY() {
		String body = "";
		body = "<BODY>";

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
