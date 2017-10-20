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
public class Request_Daily_Query extends ParentRequset{

    private static String operID="";   //操作员ID
    private static String statue="";   //日结状态   0 -- 在途（默认）   1 --- 到账
    private static String tofNo="";   //日结记录id
    private static String PrdNo=""; 
    private static String pagenum="";   //当前页数
    private static String numperpage="";   //每页数据条数


    public static String getPrdNo() {
		return PrdNo;
	}
	public static void setPrdNo(String prdNo) {
		Request_Daily_Query.PrdNo = prdNo;
	}
	public static void setTofNo(String tofNo) {
		Request_Daily_Query.tofNo = tofNo;
	}
    public static void setDate(String date) {
        Request_Daily_Query.date = date;
    }

    public static void setOperID(String operID) {
        Request_Daily_Query.operID = operID;
    }

    public static void setStatue(String statue) {
        Request_Daily_Query.statue = statue;
    }

    public static void setPagenum(String pagenum) {
        Request_Daily_Query.pagenum = pagenum;
    }

    public static void setNumperpage(String numperpage) {
        Request_Daily_Query.numperpage = numperpage;
    }


    private static String getBODY(){
        String body="";

        body = "<BODY>";
        if(operID.length()>0){
            body += "<OPER_ID>"+operID+"</OPER_ID>";
        }
        if(tofNo.length()>0){
            body += "<TOFNO>"+tofNo+"</TOFNO>";
        }
        if(PrdNo.length()>0){
            body += "<PRDNO>"+PrdNo+"</PRDNO>";
        }
        body += "<STATUS>"+statue+"</STATUS>";
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
        paramMap.put("OPER_ID", operID);
        paramMap.put("STATUS", statue);
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
