package com.common.powertech.bussiness;

import android.content.Context;
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
 *
 */
public class Request_Daily_Confirm extends ParentRequset{

    private static String statue="";   //处理方式
    private static String tofno="";   //日结编号

    public static void setStatue(String st) {
        statue = st;
    }

    public static void setTofno(String tfn) {
        tofno = tfn;
    }

    private static String getBODY(){
        String body="";
        body = "<BODY>" +
                "<TOF_NO>"+tofno+"</TOF_NO>" +
                "<STATUS>"+statue+"</STATUS>" +
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
        paramMap.put("TOF_NO", tofno);
        paramMap.put("STATUS", statue);

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
