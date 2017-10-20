package com.myDialog;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.common.powertech.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class notifyShow extends Activity {
	private Button button1;
	private static final int TIME_OUT = 1000000;   //超时时间
	private static final String CHARSET = "utf-8"; //设置编码
	public static final String SUCCESS="1";
	public static final String FAILURE="0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifycation);
//		TextView notify = (TextView) findViewById(R.id.notify);
//		Intent intent = getIntent();
//		String news = intent.getStringExtra("new");
//		notify.setText(news);
		
	}
	
}
