package com.common.powertech.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.common.powertech.PowertechApplication;
import com.common.powertech.R;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.service.LiuLiangTongJiService;
import com.common.powertech.util.SystemUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 单位:广东天波信息技术股份有限公司
 * 创建人:luyq
 * 功能：爱电影启动页面
 * 日期:2014-7-1
 */
public class StartAppActivity extends Activity {

    private static final int NOTIFY_START_APP = 0x00000001;
    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if(Build.MODEL.equalsIgnoreCase("TPS390")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			SystemUtil.closeBar();
//        }
            Settings.System.putInt(StartAppActivity.this.getContentResolver(), Settings.System.AUTO_TIME, 0);
            Settings.System.putInt(StartAppActivity.this.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0); 
        setContentView(R.layout.layout_activity_main);
        PowertechApplication.getInstance().addActivity(this);
    }
    
    

    @Override
    public void onResume() {
        super.onResume();

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(NOTIFY_START_APP);
            }
        };
        int delayTime=1000;
        if(GlobalParams.IsSystemExceptionOccur){
        	GlobalParams.IsSystemExceptionOccur=false;
        	delayTime=0;
        }
        mTimer.schedule(mTimerTask, delayTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		if(mTimerTask != null){
			mTimerTask.cancel();
			mTimerTask = null;
		}
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case StartAppActivity.NOTIFY_START_APP:
                	startService(new Intent(StartAppActivity.this, LiuLiangTongJiService.class));
                    startActivity(new Intent(StartAppActivity.this, LoginActivity.class));
                    break;

                default:
                    break;
            }
            super.handleMessage(message);
        }
    };
}