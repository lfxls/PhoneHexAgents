package com.common.powertech;

import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Application;

import java.util.Stack;

import com.common.powertech.exception.CrashHandler;

public class PowertechApplication extends Application {
    private static Stack<Activity> activityStack;
    private static PowertechApplication singleton;
    private static String MINRECHARGE; //全局变量 最小充值金额
    private static String PRDTYPELIMIT; //全局变量 售卖权限
    private static String PHONEAMOUNTCONFIG; //全局变量 充值金额
    
    private static String SELECTENEL1; //全局变量 默认选择运营商
	private static String SELECTENEL2; //全局变量 默认选择运营商
    private static String SELECTENEL3; //全局变量 默认选择运营商
    private static String SELECTENEL4; //全局变量 默认选择运营商


	private static Map<String, String> ENELGROUP1; //电力公司列表
    private static Map<String, String> ENELGROUP2; //水公司列表
    private static Map<String, String> ENELGROUP3; //气公司列表
    private static Map<String, String> ENELGROUP4; //电信公司列表

    private static String SERVERADDRESS; //全局变量 服务地址
    
   	public static String getSERVERADDRESS() {
   		return SERVERADDRESS;
   	}

   	public static void setSERVERADDRESS(String sERVERADDRESS) {
   		SERVERADDRESS = sERVERADDRESS;
   	}
    public static String getPRDTYPELIMIT() {
		return PRDTYPELIMIT;
	}

	public static void setPRDTYPELIMIT(String pRDTYPELIMIT) {
		PRDTYPELIMIT = pRDTYPELIMIT;
	}
	

	public static String getPHONEAMOUNTCONFIG() {
		return PHONEAMOUNTCONFIG;
	}

	public static void setPHONEAMOUNTCONFIG(String pHONEAMOUNTCONFIG) {
		PHONEAMOUNTCONFIG = pHONEAMOUNTCONFIG;
	}
	
    public static String getSELECTENEL1() {
		return SELECTENEL1;
	}

	public static void setSELECTENEL1(String sELECTENEL1) {
		SELECTENEL1 = sELECTENEL1;
	}

	public static String getSELECTENEL2() {
		return SELECTENEL2;
	}

	public static void setSELECTENEL2(String sELECTENEL2) {
		SELECTENEL2 = sELECTENEL2;
	}

	public static String getSELECTENEL3() {
		return SELECTENEL3;
	}

	public static void setSELECTENEL3(String sELECTENEL3) {
		SELECTENEL3 = sELECTENEL3;
	}

	public static String getSELECTENEL4() {
		return SELECTENEL4;
	}

	public static void setSELECTENEL4(String sELECTENEL4) {
		SELECTENEL4 = sELECTENEL4;
	}

	
	public static void addEnelGroup1(String id,String name){
		ENELGROUP1.put(id, name);
	}
	
	
	//
	public static void addEnelGroup2(String id,String name){
		ENELGROUP2.put(id, name);
	}
	
	
	
	public static void addEnelGroup3(String id,String name){
		ENELGROUP3.put(id, name);
	}
	
	
	
	public static void addEnelGroup4(String id,String name){
		ENELGROUP4.put(id, name);
	}
		
	public static Map<String, String> getENELGROUP1() {
		return ENELGROUP1;
	}

	public static void setENELGROUP1(Map<String, String> eNELGROUP1) {
		ENELGROUP1 = eNELGROUP1;
	}

	public static Map<String, String> getENELGROUP2() {
		return ENELGROUP2;
	}

	public static void setENELGROUP2(Map<String, String> eNELGROUP2) {
		ENELGROUP2 = eNELGROUP2;
	}

	public static Map<String, String> getENELGROUP3() {
		return ENELGROUP3;
	}

	public static void setENELGROUP3(Map<String, String> eNELGROUP3) {
		ENELGROUP3 = eNELGROUP3;
	}

	public static Map<String, String> getENELGROUP4() {
		return ENELGROUP4;
	}

	public static void setENELGROUP4(Map<String, String> eNELGROUP4) {
		ENELGROUP4 = eNELGROUP4;
	}
	public String getMINRECHARGE() {
		return MINRECHARGE;
	}
	public void setMINRECHARGE(String mINRECHARGE) {
		MINRECHARGE = mINRECHARGE;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler catchHandler = CrashHandler.getInstance();
		catchHandler.init(getApplicationContext());
		singleton=this;
	}
	  // Returns the application instance
    public static PowertechApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

}