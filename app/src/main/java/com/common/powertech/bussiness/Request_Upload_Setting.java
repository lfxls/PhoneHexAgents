package com.common.powertech.bussiness;

import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.SystemUtil;

import java.util.TreeMap;

/**
 * Created by yeqw on 2015/11/27.
 */
public class Request_Upload_Setting extends ParentRequset {

    private static String display = ""; // 显示模式
    private static String voice = ""; // 语音
    private static String language = ""; // 语言
    private static String locktime = ""; // 锁屏时间

    public static void setDisplay(String display) {
        Request_Upload_Setting.display = display;
    }

    public static void setVoice(String voice) {
        Request_Upload_Setting.voice = voice;
    }

    public static void setLanguage(String language) {
        Request_Upload_Setting.language = language;
    }

    public static void setLocktime(String locktime) {
        Request_Upload_Setting.locktime = locktime;
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
        if (display.length() > 0) {
            body += "<DISPLAY>" + display + "</DISPLAY>";
        }
        if (voice.length() > 0) {
            body += "<VOICE>" + voice + "</VOICE>";
        }
        if (language.length() > 0) {
            body += "<LANGUAGE>" + language + "</LANGUAGE>";
        }

        if (locktime.length() > 0) {
            body += "<LOCKTIME>" + locktime + "</LOCKTIME>";
        }
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
        paramMap.put("DISPLAY", display);
        paramMap.put("VOICE", voice);
        paramMap.put("LANGUAGE", language);
        paramMap.put("LOCKTIME", locktime);

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
