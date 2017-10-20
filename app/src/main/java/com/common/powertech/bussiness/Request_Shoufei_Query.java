package com.common.powertech.bussiness;

import java.util.TreeMap;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;

/**
 * Created by yeqw on 2015/10/24.
 */
public class Request_Shoufei_Query extends ParentRequset {


	private static String MeterNum = ""; // 表号
	private static String UserNum = ""; // 户号
	private static String PageNum = "1"; // 当前页码
	private static String NumPerPage = "3"; // 每页数据条数
	private static String IcType = ""; // IC卡类型 1-4428卡；2-4442卡
	private static String ICJsonReq = ""; // 读卡信息
	private static String ResourceType = ""; // 资源类型
	private static String EnelName = "";  // 电力公司名称
	private static String EnelId = "";   // 电力公司ID

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
		if (!"".equals(StringUtil.convertStringNull(ResourceType))) {
			body += "<RESOURCE_TYPE>" + ResourceType + "</RESOURCE_TYPE>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(EnelId))) {
			body += "<ENEL_ID>" + EnelId + "</ENEL_ID>";
		}
		
		if (!"".equals(StringUtil.convertStringNull(EnelName))) {
			body += "<ENEL_NAME>" + EnelName + "</ENEL_NAME>";
		}

		if (!"".equals(StringUtil.convertStringNull(IcType))) {
			body += "<IC_TYPE>" + IcType + "</IC_TYPE>";
		}

		if (!"".equals(StringUtil.convertStringNull(ICJsonReq))) {
			body += "<IC_JSON_REQ>" + ICJsonReq + "</IC_JSON_REQ>";
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
		paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

		// BODY
		paramMap.put("METER_NO", MeterNum);
		paramMap.put("USER_NO", UserNum);
		paramMap.put("PAGENUM", PageNum);
		paramMap.put("NUMPERPAG", NumPerPage);
		paramMap.put("IC_TYPE", IcType);
		paramMap.put("IC_JSON_REQ", ICJsonReq);

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
		Request_Shoufei_Query.date = date;
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
	public static String getResourceType() {
		return ResourceType;
	}

	public static void setResourceType(String resourceType) {
		ResourceType = resourceType;
	}
	
	public static String getEnelName() {
		return EnelName;
	}

	public static void setEnelName(String enelName) {
		EnelName = enelName;
	}

	public static String getEnelId() {
		return EnelId;
	}
	
	public static void setEnelId(String enelId) {
		EnelId = enelId;
	}


}
