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
public class Request_Recovery_Confirm extends ParentRequset {

    private static String Number="";   //冲正的交易单号
    private static String Rtype="";   //冲正类型
    private static String Rreason="";   //冲正原因

    public static void setNumber(String number) {
        Number = number;
    }

    public static void setRtype(String rtype) {
        Rtype = rtype;
    }

    public static void setRreason(String rreason) {
        Rreason = rreason;
    }

    private static String getBODY(){
        String body="";
        body = "<BODY>" +
                "<PRDORDNO>"+Number+"</PRDORDNO>" +
                "<TYPE>"+Rtype+"</TYPE>" +
                "<REASON>"+Rreason+"</REASON>" +
                "</BODY>";

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
        paramMap.put("PRDORDNO", Number);
        paramMap.put("TYPE", Rtype);
        paramMap.put("REASON", Rreason);

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
