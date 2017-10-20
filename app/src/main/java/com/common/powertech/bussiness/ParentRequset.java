package com.common.powertech.bussiness;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.axis.utils.StringUtils;

import android.content.Context;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;

/**
 * 广东天波信息技术股份有限公司 功能：请求类父类 作者:luyq 日期:2015-10-12
 */
public abstract class ParentRequset {
	protected static String date = ""; // 日期
	protected static Context context;// 上下文数据

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context ct) {
		context = ct;
	}

	public static String getTOP() {
		String top = "";
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		date = sDateFormat.format(new java.util.Date());
		top = "<TOP>" + "<IMEI>" + SystemUtil.getIMEI(context) + "</IMEI>"
				+ "<SESSION_ID>" + GlobalParams.SESSION_ID + "</SESSION_ID>"
				+ "<REQUEST_TIME>" + date + "</REQUEST_TIME>"
				+ "<SOURCE>3</SOURCE>"
				+ "<LOCAL_LANGUAGE>" + SystemUtil.getLocalLanguage(getContext())
				+ "</LOCAL_LANGUAGE>" + "</TOP>";
		return top;
	}

	public static String getTAIL(String md5) {

		String tail = "";
		tail = "<TAIL>" + "<SIGN_TYPE>1</SIGN_TYPE>" + "<SIGNATURE>" + md5
				+ "</SIGNATURE>" + "</TAIL>";
		return tail;
	}

	public static String bulidParam(TreeMap paramMap) {
		String str = "";
		Set<Map.Entry<String, String>> set = paramMap.entrySet();
		for (Map.Entry<String, String> entry : set) {
			if (!StringUtils.isEmpty(entry.getValue())) {
				str += (entry.getKey().toUpperCase() + "=" + entry.getValue() + "&");
			}
		}
		// str += "KEY=751EA43CBFEB4E0336FB46CA95BE60B3";//IMkXpbUVhvTI
		str += "KEY=" + GlobalParams.KEY + "";
		System.out.println("Sign str:" + str);
		return str;
	}
}
