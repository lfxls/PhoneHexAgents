package com.common.powertech.broadcast;

import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.LiuLiangData;
import com.common.powertech.util.SystemUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			LiuLiangData liuLiangData = new LiuLiangData(SystemUtil.getCurrentDate(),SystemUtil.getCurrentTraffic());
			BaseDao<LiuLiangData, Integer> baseDao = new BaseDao<LiuLiangData, Integer>(context, LiuLiangData.class);
			baseDao.create(liuLiangData);
		}
	}

}
