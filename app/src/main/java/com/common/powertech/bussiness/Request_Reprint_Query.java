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
 * Created by yeqw on 2015/10/24.
 */
public class Request_Reprint_Query extends ParentRequset{

    private static String MeterNum="";   //表号
    private static String UserNum="";   //户号
    private static String PageNum="";   //当前页码
    private static String NumPerPage="3";   //每页数据条数

    public static void setMeterNum(String meterNum) {
        MeterNum = meterNum;
    }

    public static void setUserNum(String userNum) {
        UserNum = userNum;
    }

    public static void setPageNum(String pageNum) {
        PageNum = pageNum;
    }

    public static void setNumPerPage(String numPerPage) {
        NumPerPage = numPerPage;
    }

    public static String getBODY(){
        String body="";

        //电表号与用户号 只能存在其中一个
        if(MeterNum.length()>0){

            body = "<BODY><METER_NO>"+MeterNum+"</METER_NO>";
            if (PageNum.length()>0){
                body += "<PAGENUM>"+PageNum+"</PAGENUM>";
            }
            if(NumPerPage.length()>0){
                body += "<NUMPERPAG>"+NumPerPage+"</NUMPERPAG>";
            }
            body += "</BODY>";

        }else{

            body = "<BODY><USER_NO>"+UserNum+"</USER_NO>";
            if (PageNum.length()>0){
                body += "<PAGENUM>"+PageNum+"</PAGENUM>";
            }
            if(NumPerPage.length()>0){
                body += "<NUMPERPAG>"+NumPerPage+"</NUMPERPAG>";
            }
            body += "</BODY>";
        }


        return body;
    }

    public static String getMD5str(){

        TreeMap<String, String> paramMap = new TreeMap<String, String>();
        //TOP
        paramMap.put("IMEI", SystemUtil.getIMEI(context));
        paramMap.put("SESSION_ID", GlobalParams.SESSION_ID);
        paramMap.put("REQUEST_TIME", date);
        paramMap.put("LOCAL_LANGUAGE", SystemUtil.getLocalLanguage(getContext()));

        //BODY
        if(MeterNum.length()>0){
            paramMap.put("METER_NO", MeterNum);
            //paramMap.put("USER_NO", UserNum);
            paramMap.put("PAGENUM", PageNum);
            paramMap.put("NUMPERPAG",NumPerPage );
        }else{
            //paramMap.put("METER_NO", MeterNum);
            paramMap.put("USER_NO", UserNum);
            paramMap.put("PAGENUM", PageNum);
            paramMap.put("NUMPERPAG",NumPerPage );
        }

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
