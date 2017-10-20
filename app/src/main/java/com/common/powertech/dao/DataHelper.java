package com.common.powertech.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

 
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dbbean.LiuLiangData;
import com.common.powertech.dbbean.LoginError;
import com.common.powertech.dbbean.LoginNameData;
import com.common.powertech.dbbean.PhoneNumber;
import com.common.powertech.dbbean.PrinterTemp;
import com.common.powertech.dbbean.ServerAddress;
import com.common.powertech.dbbean.SystemParam;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by luyq on 2014/3/26.
 */
public class DataHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "hxpower.db";
	private static final int DATABASE_VERSION = 1;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {
		try {
			Log.e(DataHelper.class.getName(), "开始创建数据库");

			TableUtils.createTable(connectionSource, LiuLiangData.class);
			TableUtils.createTable(connectionSource, PrinterTemp.class);
			TableUtils.createTable(connectionSource, SystemParam.class);
			TableUtils.createTable(connectionSource, JinRiShouDian.class);
			TableUtils.createTable(connectionSource, LoginNameData.class);
			TableUtils.createTable(connectionSource, LoginError.class);
			TableUtils.createTable(connectionSource, PhoneNumber.class);
			TableUtils.createTable(connectionSource, ServerAddress.class);
			
			Log.e(DataHelper.class.getName(), "创建数据库成功");

		} catch (SQLException e) {

			Log.e(DataHelper.class.getName(), "创建数据库失败", e);

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int i, int i2) {
		try {

			TableUtils.dropTable(connectionSource, LiuLiangData.class, true);
			TableUtils.dropTable(connectionSource, PrinterTemp.class, true);
			TableUtils.dropTable(connectionSource, SystemParam.class, true);
			TableUtils.dropTable(connectionSource, JinRiShouDian.class, true);
			TableUtils.dropTable(connectionSource, LoginNameData.class, true);
			TableUtils.dropTable(connectionSource, LoginError.class, true);
			TableUtils.dropTable(connectionSource, PhoneNumber.class, true);
			TableUtils.dropTable(connectionSource, ServerAddress.class, true);
			
			onCreate(sqLiteDatabase, connectionSource);

			Log.e(DataHelper.class.getName(), "更新数据库成功");

		} catch (SQLException e) {

			Log.e(DataHelper.class.getName(), "更新数据库失败", e);

		}
	}

	@Override
	public void close() {
		super.close();
	}
}
