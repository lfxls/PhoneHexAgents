package com.common.powertech.bussiness;

import android.os.Build;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;
import org.apache.axis.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by yeqw on 2015/10/23.
 */
public class Request_Transfer_Query extends ParentRequset{

    private static String ORDSTATUS="";   //转账状态
    private static String TOKEN_TYPE=""; // 转账类型
    private static String pagenum="";   //当前页数
    private static String numperpage="";   //每页数据条数


	public static String getORDSTATUS() {
		return ORDSTATUS;
	}

	public static void setORDSTATUS(String oRDSTATUS) {
		ORDSTATUS = oRDSTATUS;
	}

	public static String getTOKEN_TYPE() {
		return TOKEN_TYPE;
	}

	public static void setTOKEN_TYPE(String tOKEN_TYPE) {
		TOKEN_TYPE = tOKEN_TYPE;
	}

	public static void setPagenum(String pagenum) {
        Request_Transfer_Query.pagenum = pagenum;
    }

    public static void setNumperpage(String numperpage) {
        Request_Transfer_Query.numperpage = numperpage;
    }


    private static String getBODY(){
        String body="";

        body = "<BODY>";
        if(TOKEN_TYPE.length()>0){
            body += "<TOKEN_TYPE>"+TOKEN_TYPE+"</TOKEN_TYPE>";
        }
        if(ORDSTATUS.length()>0){
            body += "<ORDSTATUS>"+ORDSTATUS+"</ORDSTATUS>";
        }
        if(pagenum.length()>0){
            body += "<PAGENUM>"+pagenum+"</PAGENUM>";
        }
        if(numperpage.length()>0){
            body += "<NUMPERPAG>"+numperpage+"</NUMPERPAG>";
        }
        body += "</BODY>";

        return body;
    }

    private static String getMD5str(){

        TreeMap<String, String> paramMap = new TreeMap<String, String>();
        //TOP
        paramMap.put("IMEI", SystemUtil.getIMEI(context));
        paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
        paramMap.put("REQUEST_TIME", date);
        paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

        //BODY
        paramMap.put("ORDSTATUS", ORDSTATUS);
        paramMap.put("TOKEN_TYPE", TOKEN_TYPE);
        paramMap.put("PAGENUM", pagenum);
        paramMap.put("NUMPERPAG", numperpage);

        //TAIL
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
