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
public class Request_Income_Query extends ParentRequset{

	private static String Pagenum = ""; // 当前页码
    private static String Nunperpage = ""; // 每页条数
    private static String Fromdate = ""; // 开始时间
    private static String Todate = ""; // 结束时间
    private static String TXNTYPE = "";//收支类型 

    public static void setPagenum(String pagenum) {
        Pagenum = pagenum;
    }

    public static void setNunperpage(String nunperpage) {
        Nunperpage = nunperpage;
    }

    public static void setFromdate(String fromdate) {
        Fromdate = fromdate;
    }

    public static void setTodate(String todate) {
        Todate = todate;
    }
    
    public static void setTXNTYPE(String tXNTYPE) {
    	TXNTYPE = tXNTYPE;
    }


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
		if (Pagenum.length() > 0) {
			body += "<PAGENUM>" + Pagenum + "</PAGENUM>";
		}
		if (Nunperpage.length() > 0) {
			body += "<NUMPERPAG>" + Nunperpage + "</NUMPERPAG>";
		}
		if (Fromdate.length() > 0) {
			body += "<FROMDATE>" + Fromdate + "</FROMDATE>";
		}

		if (Todate.length() > 0) {
			body += "<TODATE>" + Todate + "</TODATE>";
		}
		
		body += "<TXNTYPE>" + TXNTYPE +"</TXNTYPE>";
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
		paramMap.put("PAGENUM", Pagenum);
		paramMap.put("NUMPERPAG", Nunperpage);
		paramMap.put("FROMDATE", Fromdate);
		paramMap.put("TODATE", Todate);
		paramMap.put("TXNTYPE", TXNTYPE);

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
