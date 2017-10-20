package com.myDialog;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.common.powertech.ItemDetailFragmentController;
import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Income_Query;
import com.common.powertech.bussiness.Request_Messages;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.webservice.Client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {

    // 获取消息线程
    private MessageThread messageThread = null;

    // 点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;

    // 通知栏消息
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 初始化
        messageNotification = new Notification();
        messageNotification.icon = R.drawable.applogo;
        messageNotification.tickerText = "新消息";
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        messageIntent = new Intent(this, notifyShow.class);
//        messageIntent.putExtra("new", "news coming");
        messagePendingIntent = PendingIntent.getActivity(this, 0,messageIntent,0);
//        messagePendingIntent.getBroadcast(this,1, messageIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        
        // 开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();

        return super.onStartCommand(intent, flags, startId);
    }
    
    public void shownotification(String msg)  
    {  
    	 /*NotificationManager barmanager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);  
         Notification notice = new Notification(android.R.drawable.stat_notify_chat,"服务器发来信息了",System.currentTimeMillis());  
         notice.flags=Notification.FLAG_AUTO_CANCEL;  
         Intent appIntent = new Intent(Intent.ACTION_MAIN);  
         //appIntent.setAction(Intent.ACTION_MAIN);  
         appIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
         appIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + "." + "NotificationService"));   
         appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式  
         PendingIntent contentIntent =PendingIntent.getActivity(this, 0,appIntent,0);  
         notice.setLatestEventInfo(this,"通知","信息:"+msg, contentIntent);  
         barmanager.notify(messageNotificationID,notice);   */
    	
    	/*Intent intent = new Intent(this,notifyShow.class);
//    	intent.putExtra("new", "1");
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 1000) + 1,intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	// 构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
    	Notification notification = new Notification.Builder(
    							this).setContentTitle("标题")
    							.setContentText("内容")
    							.setSmallIcon(R.drawable.ic_launcher)
    							.setAutoCancel(true)
    							.setDefaults(Notification.DEFAULT_SOUND)
    							.setContentIntent(pendingIntent).build();
    	messageNotificatioManager.notify((int) (Math.random() * 1000) + 1, notification);*/
    	
    	// 定义Notification的各种属性
        /*Notification notification = new Notification(R.drawable.ic_launcher, "新消息", System.currentTimeMillis());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker("ticker");
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.icon = R.drawable.applogo;
        notification.tickerText = "ticker";
        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.launcher)).getBitmap();
        notification.largeIcon = largeIcon;

        // 设置通知的事件消息
        CharSequence contentTitle = "Title"; // 通知栏标题
        CharSequence contentText = "Text"; // 通知栏内容

        Intent clickIntent = new Intent(this, NotificationClickReceiver.class); //点击通知之后要发送的广播
        int id = (int) (System.currentTimeMillis() / 1000);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        messageNotificatioManager.notify(id, notification);*/
        
        
        Intent clickIntent = new Intent(this, NotificationClickReceiver.class); 
        int id = (int) (System.currentTimeMillis() / 1000);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//      builder.setSmallIcon(R.drawable.ic_launcher);
      Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.applogo)).getBitmap();
      String info = msg;
      builder.setLargeIcon(largeIcon)
		      .setSmallIcon(R.drawable.applogo)
		      .setContentTitle(getString(R.string.push_msg_title))
		      .setContentText(info)
		      .setTicker(msg)
		      .setContentIntent(PendingIntent.getBroadcast(this, id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));
      Notification notification = builder.build();
      notification.flags|= Notification.FLAG_AUTO_CANCEL; 
        
        messageNotificatioManager.notify(id, notification);
         
    }  
    
    
    Handler handler = new Handler(){
    	public void handleMessage(Message Msg){
    		switch(Msg.what){
    		case 0:
    			break;
    		case 1:
    			InputStream in;
				try {
						in = new ByteArrayInputStream(
								GlobalParams.RETURN_DATA.getBytes("UTF-8"));
						String responeCode = Client.Parse_XML(
								GlobalParams.RETURN_DATA, "<RSPCOD>",
								"</RSPCOD>");
						String responeMsg = Client.Parse_XML(
								GlobalParams.RETURN_DATA, "<RSPMSG>",
								"</RSPMSG>");
						if (responeCode.equals("00000")) {
							String pushMsg = Client.Parse_XML(
									GlobalParams.RETURN_DATA, "<PUSHMSG>",
									"</PUSHMSG>");
							String serverMessage = pushMsg;
			                if (serverMessage != null && !"".equals(serverMessage)) {
			                    // 更新通知栏
//			                    messageNotification.setLatestEventInfo(
//			                            getApplicationContext(), "新消息",serverMessage, messagePendingIntent);
//			                    messageNotificatioManager.notify(messageNotificationID,
//			                            messageNotification);
			                	shownotification(serverMessage); 
			                    // 每次通知完，通知ID递增一下，避免消息覆盖掉
			                    messageNotificationID++;
			                }
						}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    };

    /**
     * 从服务器端获取消息
     * 
     */
    class MessageThread extends Thread {
        // 设置是否循环推送
        public boolean isRunning = true;

        public void run() {
            // while (isRunning) {
            try {
                // 间隔时间
                Thread.sleep(1000);
                // 获取服务器消息
                String data = Request_Messages.getRequsetXML();
                Client.SendDataNoThread("PMsgPush", data, handler);
                
//                messageNotification.setLatestEventInfo(
//                        getApplicationContext(), "新消息","news come", messagePendingIntent);
//                messageNotificatioManager.notify(messageNotificationID,
//                        messageNotification);
//                messageNotificationID++;
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // }
        }
    }

    @Override
    public void onDestroy() {
        // System.exit(0);
        messageThread.isRunning = false;
        super.onDestroy();
    }

    /**
     * 模拟发送消息
     * 
     * @return 返回服务器要推送的消息，否则如果为空的话，不推送
     */
    public String getServerMessage() {
        return "NEWS!";
    }
}