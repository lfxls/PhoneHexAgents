package com.common.powertech.bussiness;

import android.content.Context;
import android.os.Build;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.EncryptionDES;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

import org.apache.axis.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by yeqw on 2015/10/24.
 */
public class ShoufeiQueryRequest extends ParentRequset {


	private static String MeterNum = ""; // 表号
	private static String UserNum = ""; // 户号
	private static String PageNum = "1"; // 当前页码
	private static String NumPerPage = "3"; // 每页数据条数
 
	public static String getRequsetXML() {
		return "<ROOT>" + getTOP() + getBODY() + getTAIL(getMD5str())
				+ "</ROOT>";
	}
	
	public static String getBODY() {
		String body = "";
		body = "<BODY>";

		if (!"".equals(StringUtil.convertStringNull(MeterNum))) {
			body += "<METER_NO>" + MeterNum + "</METER_NO>";
		}

		if (!"".equals(StringUtil.convertStringNull(UserNum))) {
			body += "<USER_NO>" + UserNum + "</USER_NO>";
		}
		
		body += "<PAGENUM>" + PageNum + "</PAGENUM>" + "<NUMPERPAG>"
				+ NumPerPage + "</NUMPERPAG>" + "</BODY>";

		return body;
	}

	public static String getMD5str() {

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		// TOP
		paramMap.put("IMEI", SystemUtil.getIMEI(context));
		paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
		paramMap.put("REQUEST_TIME", date);
		paramMap.put("LOCAL_LANGUAGE", GlobalParams.LANGUAGE);

		// BODY
		paramMap.put("METER_NO", MeterNum);
		paramMap.put("USER_NO", UserNum);
		paramMap.put("PAGENUM", PageNum);
		paramMap.put("NUMPERPAG", NumPerPage);

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
		ShoufeiQueryRequest.date = date;
	}

	public static String getMeterNum() {
		return MeterNum;
	}

	public static void setMeterNum(String meterNum) {
		MeterNum = meterNum;
	}

	public static String getUserNum() {
		return UserNum;
	}

	public static void setUserNum(String userNum) {
		UserNum = userNum;
	}

	public static String getPageNum() {
		return PageNum;
	}

	public static void setPageNum(String pageNum) {
		PageNum = pageNum;
	}

	public static String getNumPerPage() {
		return NumPerPage;
	}

	public static void setNumPerPage(String numPerPage) {
		NumPerPage = numPerPage;
	}

}
