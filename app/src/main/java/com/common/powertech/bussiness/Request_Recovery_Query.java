package com.common.powertech.bussiness;

import android.os.Build;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;
import org.apache.axis.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by yeqw on 2015/10/23.
 */
public class Request_Recovery_Query extends ParentRequset{

    private static String usrno="";   //查询输入的号码
    private static String pagenum="";   //查询输入的号码
    private static String numperpage="";   //查询输入的号码

    public static void setUsrno(String usrno) {
        Request_Recovery_Query.usrno = usrno;
    }

    public static void setPagenum(String pagenum) {
        Request_Recovery_Query.pagenum = pagenum;
    }

    public static void setNumperpage(String numperpage) {
        Request_Recovery_Query.numperpage = numperpage;
    }

    private static String getBODY(){
        String body="";

        body = "<BODY>";
        if(usrno.length()>0){
            body += "<USER_NO>"+usrno+"</USER_NO>";
        }
        if (pagenum.length()>0){
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
        paramMap.put("USER_NO", usrno);
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
