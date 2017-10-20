package com.myDialog;

import java.util.List;

import com.common.powertech.activity.StartAppActivity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		 //todo 跳转之前要处理的逻辑
//        Intent newIntent = new Intent(context, notifyShow.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(newIntent);
		
		 //获取ActivityManager  
        ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);  
        //获得当前运行的task  
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);  
        for (ActivityManager.RunningTaskInfo rti : taskList) {  
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台  
            if(rti.topActivity.getPackageName().equals(context.getPackageName())) {  
                mAm.moveTaskToFront(rti.id,0);  
                return;  
            }  
        }  
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity  
        Intent resultIntent = new Intent(context, StartAppActivity.class);  
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);  
        context.startActivity(resultIntent);  
	}
}
