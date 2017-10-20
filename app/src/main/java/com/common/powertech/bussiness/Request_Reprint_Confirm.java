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
public class Request_Reprint_Confirm extends ParentRequset{

    private static String PrdordNo="";   //交易单号
    private static String IC_Type="";   //IC卡类型
    private static String Json_Str="";   //写卡数据

    public static void setPrdordNo(String prdordNo) {
        PrdordNo = prdordNo;
    }

    public static void setIC_Type(String IC_Type) {
        Request_Reprint_Confirm.IC_Type = IC_Type;
    }

    public static void setJson_Str(String json_Str) {
        Json_Str = json_Str;
    }


    public static String getBODY(){
        String body="";
        body = "<BODY>" +"<PRDORDNO>"+PrdordNo+"</PRDORDNO>";
        if(IC_Type.length()>0){
            body +=  "<IC_TYPE>"+IC_Type+"</IC_TYPE>";
        }
        if(Json_Str.length()>0){
            body +=  "<IC_JSON_REQ>"+Json_Str+"</IC_JSON_REQ>";
        }

        body +="</BODY>";
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
        paramMap.put("PRDORDNO", PrdordNo);
        paramMap.put("IC_TYPE", IC_Type);
        paramMap.put("IC_JSON_REQ", Json_Str);

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
