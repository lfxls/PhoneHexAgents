package com.common.powertech.service;

import java.util.Timer;
import java.util.TimerTask;

import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.LiuLiangData;
import com.common.powertech.util.SystemUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LiuLiangTongJiService extends Service {
	
	private Timer mTimer;
	private TimerTask mTimerTask;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				BaseDao<LiuLiangData, Integer> baseDao = new BaseDao<LiuLiangData, Integer>(LiuLiangTongJiService.this, LiuLiangData.class);
				if(baseDao.isExists(1)){
					int id = (int)baseDao.queryRawValueBySQL("SELECT MAX(id) FROM liuliang_data");
					baseDao.excute("UPDATE liuliang_data SET traffic="+SystemUtil.getCurrentTraffic()+" WHERE id="+id);
				}else {
					LiuLiangData liuLiangData = new LiuLiangData(SystemUtil.getCurrentDate(),SystemUtil.getCurrentTraffic());
					baseDao.create(liuLiangData);
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, 900000);
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
	
}
