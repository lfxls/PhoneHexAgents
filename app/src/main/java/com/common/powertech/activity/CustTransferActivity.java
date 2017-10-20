package com.common.powertech.activity;

import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Account_Query;
import com.common.powertech.bussiness.Request_Complete_Order;
import com.common.powertech.bussiness.Request_Verify_Order;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustTransferActivity extends Activity{
	private Context mActivity;
	LinearLayout tran_cust;//指定菜单
	LinearLayout tran_user;//非指定菜单
	LinearLayout input_cust_lin;//指定代理商输入页
	LinearLayout passwd_lin;
	private EditText account;
	private Button next;
	private String accountstr = "";
	LinearLayout inf_custamt_lin;//代理商信息和下单页
	LinearLayout inf_cust_lin;//指定代理商信息
	private TextView custname;
	private TextView custaddress;
	LinearLayout inf_amt_lin;//下单页
	private EditText phone;
	private EditText verify;
	private Button getverify;
	private EditText amt;
	private EditText password ;
	private TableLayout table_inf;
	TextView transfer_amt,fee,out_amt;
	private Button complete;
	
	private String phonestr = "";
	private String verifystr = "";
	private String amtstr = "";
	private String passwordstr = "";
	
	private int TYPE=0;//0:指定代理商  1:非指定代理商
	private String TRANSFER_NO = "";//转账订单号
	
	private final int CHOOSEN = Color.parseColor("#A9A9A9");//color.greyb;
	private final int NOCHOOSEN = Color.parseColor("#FFFFFF");
	private final int BUTTON_ACTIVE = Color.parseColor("#B9C2FB");
	private final int BUTTON_NEGATIVE = Color.parseColor("#B5B5B5");
	private final int TIME = 60000;
	private TimeCount time;
	
	private ProgressDialog progressDialog;
	private InputMethodManager inputMethodManager; //软键盘
	private View rootview;
	//#bbbbbb
	private Request_Verify_Order request_Verify_Order;
	
	//初始化 
	private void init(){
		input_cust_lin = (LinearLayout) rootview.findViewById(R.id.input_cust_lin);
		inf_custamt_lin = (LinearLayout) rootview.findViewById(R.id.inf_custamt_lin);
		inf_cust_lin = (LinearLayout) rootview.findViewById(R.id.inf_cust_lin);
		inf_amt_lin = (LinearLayout) rootview.findViewById(R.id.inf_amt_lin);
		passwd_lin = (LinearLayout) rootview.findViewById(R.id.passwd_lin);
		account = (EditText) rootview.findViewById(R.id.account);
		next = (Button) rootview.findViewById(R.id.next);
		custname = (TextView) rootview.findViewById(R.id.custname);
		custaddress = (TextView) rootview.findViewById(R.id.custaddress);
		phone = (EditText) rootview.findViewById(R.id.phone);
		verify = (EditText) rootview.findViewById(R.id.verify);
		getverify = (Button) rootview.findViewById(R.id.getverify);
		amt = (EditText) rootview.findViewById(R.id.amt);
		password = (EditText)rootview.findViewById(R.id.passwd);
		table_inf = (TableLayout)rootview.findViewById(R.id.table_inf);
		transfer_amt = (TextView)rootview.findViewById(R.id.transfer_amt);
		fee = (TextView)rootview.findViewById(R.id.fee);
		out_amt = (TextView)rootview.findViewById(R.id.out_amt); 
		complete = (Button) rootview.findViewById(R.id.complete);
		
		tran_cust = (LinearLayout) rootview.findViewById(R.id.tran_cust);
		tran_user = (LinearLayout) rootview.findViewById(R.id.tran_user);
		tran_cust.setBackgroundColor(CHOOSEN);
		tran_user.setBackgroundColor(NOCHOOSEN);
	}
	//初始化 指定对象菜单
	private void initCust(){
		hideSoftInput();
//		account.clearFocus();
		conflictf();
		//颜色
		tran_cust.setBackgroundColor(CHOOSEN);
		tran_user.setBackgroundColor(NOCHOOSEN);
		//显示代理商信息
		input_cust_lin.setVisibility(View.VISIBLE);
		inf_custamt_lin.setVisibility(View.GONE);
		inf_cust_lin.setVisibility(View.GONE);
		inf_amt_lin.setVisibility(View.VISIBLE);
		complete.setClickable(false);
		TYPE=0;
		initParams();
		conflictt();
	}
	//初始化 非指定对象菜单
	private void initUser(){
//		amt.clearFocus();
		hideSoftInput();
		conflictf();
		tran_cust.setBackgroundColor(NOCHOOSEN);
		tran_user.setBackgroundColor(CHOOSEN);
		//隐藏代理商相关
		input_cust_lin.setVisibility(View.GONE);
		inf_cust_lin.setVisibility(View.GONE);
		inf_custamt_lin.setVisibility(View.VISIBLE);
		complete.setClickable(false);
		TYPE=1;
		initParams();
		conflictt();
	}
	
	private void initParams(){
		//初始化变量
		
		TRANSFER_NO = "";
		accountstr = "";
		amtstr = "";
		verifystr = "";
		phonestr = "";
		passwordstr = "";
		//输入置空
		account.setText("");
		amt.setText(amtstr);
		verify.setText(verifystr);
		phone.setText(phonestr);
		password.setText("");
		complete.setBackgroundColor(BUTTON_NEGATIVE);
		if(time!=null){
			time.cancel();
			getverify.setText(R.string.tran_btn_verify);
		}
		table_inf.setVisibility(View.GONE);
		transfer_amt.setText("");
		fee.setText("");
		out_amt.setText("");
		getverify.setClickable(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mActivity = this;
		LayoutInflater inflater = LayoutInflater.from(mActivity); 
		rootview = inflater.inflate(R.layout.activity_transfer_main, null);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		init();
		tran_cust.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				initCust();
			}
		});
		tran_user.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				initUser();
			}
		});
		
		if(!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1"))
			passwd_lin.setVisibility(View.VISIBLE);
		
		account.addTextChangedListener(watcher);
		next.setOnClickListener(NEXT);
		getverify.setOnClickListener(GetVerify);
		complete.setOnClickListener(Complete);
		inputMethodManager=(InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE); 
//		View rootview = this.getWindow().getDecorView(); //获取当前Activity根View
		time = new TimeCount(TIME, 1000);//构造CountDownTimer对象
		setContentView(rootview);
	}
	//防止冲突
	private void conflictf(){
		tran_cust.setClickable(false);
		tran_user.setClickable(false);
	}
	//防止冲突
	private void conflictt(){
		tran_cust.setClickable(true);
		tran_user.setClickable(true);
	}
	
	TextWatcher watcher = new TextWatcher() {
		String origin="";
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			String compile = "\\w+";
			if (s == null || s.length() == 0) {
				return;
			}
			String str = s.toString();
			Pattern pattern = Pattern.compile(compile);
			if(str!=null && !pattern.matcher(str).matches()){
				if(!str.equals("")){
					int len = origin.length();
					account.setText(origin);
					account.setSelection(len);
					return;
				}
			}
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			origin = s.toString();
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	};

	OnClickListener NEXT = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hideSoftInput();
			// 查询代理商信息
			accountstr = account.getText().toString().trim();
			if("".equals(accountstr)){
				Toast.makeText(mActivity,R.string.tran_toast_error1, Toast.LENGTH_SHORT).show();
				return;
			}
			String requestXML = "";
			Request_Account_Query.setContext(mActivity);
			Request_Account_Query.setTrans_Cust(accountstr);
			requestXML = Request_Account_Query.getRequsetXML();
			 HttpSend(requestXML,"PAccountInf",mhandler);
		}
	};
	OnClickListener GetVerify = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			table_inf.setVisibility(View.GONE);
			complete.setBackgroundColor(BUTTON_NEGATIVE);
			transfer_amt.setText("");
			fee.setText("");
			out_amt.setText("");
			request_Verify_Order = new Request_Verify_Order();
			// 获取验证码及下单
			TRANSFER_NO = "";//先清空订单
			phonestr = phone.getText().toString().trim();
			if("".equals(phonestr)){
				Toast.makeText(mActivity, R.string.tran_toast_error2, Toast.LENGTH_SHORT).show();
				return;
			}
			passwordstr = password.getText().toString().trim();
			if(!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1") ){
				if("".equals(passwordstr)){
					Toast.makeText(mActivity,R.string.tran_toast_error3, Toast.LENGTH_SHORT).show();
					return;
				}else{
					request_Verify_Order.setPAY_PWD(passwordstr);
				}
			}
			amtstr = amt.getText().toString().trim();
			if("".equals(amtstr)){
				Toast.makeText(mActivity, R.string.tran_toast_error4, Toast.LENGTH_SHORT).show();
				return;
			}
			 Pattern pattern = Pattern.compile("^(-)?[0-9]*.?[0-9]*");  
            Matcher matcher = pattern.matcher(amtstr);
            if (!matcher.matches()) {
                // 金额格式不正确
                SystemUtil.displayToast(mActivity,
                        R.string.shoudianxiangqing_jineshurubuzhengque);
                return;
            }else if(amtstr.contains(".")){
            	 SystemUtil.displayToast(mActivity,R.string.tran_toast_error5);
                 return;
            }
			
			if(TYPE==0){
				if("".equals(accountstr)){
					Toast.makeText(mActivity, R.string.tran_toast_error6, Toast.LENGTH_SHORT).show();
					tran_cust.performClick();
					return;
				}
				request_Verify_Order.setTRANS_CUST(accountstr);
			}else{
				request_Verify_Order.setTRANS_CUST("");
			}
			getverify.setClickable(false);
			request_Verify_Order.setPIN_PHONE(phonestr);
			request_Verify_Order.setTRANSFER_AMT(amtstr);
			String requestXML = "";
			request_Verify_Order.setContext(mActivity);
			requestXML = request_Verify_Order.getRequsetXML();
			HttpSend(requestXML,"PBalanceTransout",orderHandler);
		}
	};
	
	OnClickListener Complete = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//生成TOKEN
			hideSoftInput();
			if("".equals(TRANSFER_NO)){
				Toast.makeText(mActivity, R.string.tran_toast_error7, Toast.LENGTH_SHORT).show();
				return;
			}
			verifystr = verify.getText().toString().trim();
			if("".equals(verifystr)){
				Toast.makeText(mActivity, R.string.tran_toast_error8, Toast.LENGTH_SHORT).show();
				return;
			}
			Request_Complete_Order.setContext(mActivity);
			Request_Complete_Order.setTRANSFER_NO(TRANSFER_NO);
			Request_Complete_Order.setVERIFY_CODE(verifystr);
			String requestXML = "";
			requestXML = Request_Complete_Order.getRequsetXML();
			HttpSend(requestXML,"PBalTranOutToken",completeHandler);
			
		}
	};
	
	public void HttpSend(String requestXML,String tran,Handler handler){
		createDialog();
		progressDialog.setTitle(getString(R.string.dialog_check));
		progressDialog.setMessage(getString(R.string.progress_conducting));
		// 设置进度条是否不明确
		progressDialog.setIndeterminate(false);
		// 是否可以按下退回键取消
		progressDialog.setCancelable(false);
		progressDialog.show();
		System.out.println("请求：" + requestXML);
		Client.SendData(tran, requestXML, handler);
		
	}
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			switch(msg.what){
			case 0:
				// 联网失败
				// 没有加载到数据，页码返回到当前页
				 try {
                     if (progressDialog != null) {
                         progressDialog.dismiss();
                         // 没有加载到数据，页码返回到当前页
                         Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                         onBackPressed();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
				 
				break;
			case 1:
				// 联网成功
				System.out.println("代理商信息查询响应：" + GlobalParams.RETURN_DATA);
				String mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPCOD>", "</RSPCOD>");
				String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPMSG>", "</RSPMSG>");
				if("00000".equals(mRspCode)){
					//成功
					String Tran_Cust_Name = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<TRAN_CUST_NAME>", "</TRAN_CUST_NAME>");
					String Tran_Cust_Address = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<TRAN_CUST_ADDRESS>", "</TRAN_CUST_ADDRESS>");
					
					//显示代理商信息
					custname.setText(Tran_Cust_Name);
					custaddress.setText(Tran_Cust_Address);
					input_cust_lin.setVisibility(View.GONE);
					inf_cust_lin.setVisibility(View.VISIBLE);
					inf_custamt_lin.setVisibility(View.VISIBLE);
					
				}else{
					if (mRspMeg.equalsIgnoreCase("")) {
						SystemUtil.displayToast(mActivity,
								R.string.shoufeixiangqing_wangluoyichang);
					} else {
						SystemUtil.displayToast(mActivity, mRspMeg);
						if(mRspCode.equalsIgnoreCase("00011")){
							SystemUtil.setGlobalParamsToNull(mActivity);
						    DummyContent.ITEM_MAP.clear();
						    DummyContent.ITEMS.clear();
							Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
							break;
						}						
					}	
				}
				
				break;
			
			}
		}
		
	};
	
	Handler orderHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			switch(msg.what){
			case 0:
				// 联网失败
				// 没有加载到数据，页码返回到当前页
				 try {
                     if (progressDialog != null) {
                         progressDialog.dismiss();
                         // 没有加载到数据，页码返回到当前页
                         Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                         onBackPressed();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
				 getverify.setClickable(true);
				break;
			case 1:
				// 联网成功
				System.out.println("下单：" + GlobalParams.RETURN_DATA);
				String mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPCOD>", "</RSPCOD>");
				String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPMSG>", "</RSPMSG>");
				if("00000".equals(mRspCode)){
					//验证码发送成功
					TRANSFER_NO =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<TRANSFER_NO>", "</TRANSFER_NO>");
					String FEE =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<FEE>", "</FEE>");
					String TRANSFER_AMT =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<TRANSFER_AMT>", "</TRANSFER_AMT>");
					
					String OUT_AMT  =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<OUT_AMT>", "</OUT_AMT>");
					
					String CCY =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<CCY>", "</CCY>");
					
					SystemUtil.displayToast(mActivity,R.string.tran_toast_error9);
					//显示完成按钮
					table_inf.setVisibility(View.VISIBLE);
					transfer_amt.setText(TRANSFER_AMT + " "+CCY);
					fee.setText(FEE + " " +CCY);
					out_amt.setText(OUT_AMT + " " +CCY);
					complete.setClickable(true);
					complete.setBackgroundColor(BUTTON_ACTIVE);
					//验证码倒计时
					time.start();

				}else{
					if (mRspMeg.equalsIgnoreCase("")) {
						SystemUtil.displayToast(mActivity,
								R.string.shoufeixiangqing_wangluoyichang);
					} else {
						SystemUtil.displayToast(mActivity, mRspMeg);
						if(mRspCode.equalsIgnoreCase("00011")){
							SystemUtil.setGlobalParamsToNull(mActivity);
						    DummyContent.ITEM_MAP.clear();
						    DummyContent.ITEMS.clear();
							Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
							break;
						}						
					}	
				}
				getverify.setClickable(true);
				break;
			
			}
			
		}
		
	};
	
	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getverify.setText(R.string.tran_btn_retry);
			getverify.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getverify.setClickable(false);
			getverify.setText(millisUntilFinished / 1000+"");
		}
	}
	
	Handler completeHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			switch(msg.what){
			case 0:
				// 联网失败
				// 没有加载到数据，页码返回到当前页
				 try {
                     if (progressDialog != null) {
                         progressDialog.dismiss();
                         // 没有加载到数据，页码返回到当前页
                         Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                         onBackPressed();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
				 
				break;
			case 1:
				// 联网成功
				System.out.println("TOKEN：" + GlobalParams.RETURN_DATA);
				String mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPCOD>", "</RSPCOD>");
				String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPMSG>", "</RSPMSG>");
				if("00000".equals(mRspCode)){
					//验证码发送成功
//					SystemUtil.displayToast(mActivity,"转账成功,请注意查收转账码");
					//返回到初始页
					
//					AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
//					LayoutInflater inflater = LayoutInflater.from(mActivity);
//					View view = inflater.inflate(R.layout.activity_transfer_main,null );
//					dialog.setView(view);
//					dialog.create().show();
					AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
					dialog.setMessage(R.string.tran_toast_error10);
					dialog.setCancelable(false);
					dialog.setPositiveButton(R.string.tran_toast_error11, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//跳转到记录页面
//							Intent intent = new Intent(mActivity,ItemListActivity.class);
//							intent.putExtra("", value);
//							startActivity(intent);
							finish();
						}
					});
					
					dialog.setNegativeButton(R.string.tran_toast_error12, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(TYPE == 0){
								tran_cust.performClick();
							}else{
								tran_user.performClick();
							}
						}
					});
					dialog.create().show();
						
					
				}else{
					if (mRspMeg.equalsIgnoreCase("")) {
						SystemUtil.displayToast(mActivity,
								R.string.shoufeixiangqing_wangluoyichang);
					} else {
						SystemUtil.displayToast(mActivity, mRspMeg);
						if(mRspCode.equalsIgnoreCase("00011")){
							SystemUtil.setGlobalParamsToNull(mActivity);
						    DummyContent.ITEM_MAP.clear();
						    DummyContent.ITEMS.clear();
							Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
							break;
						}						
					}	
				}
				
				break;
			
			}
			GlobalParams.RETURN_DATA="";
		}
		
	};
	
	
	private void createDialog() {
		progressDialog = MyProgressDialog.createProgressDialog(mActivity,
				GlobalParams.PROGRESSDIALOG_TIMEOUT,
				new MyProgressDialog.OnTimeOutListener() {
					@Override
					public void onTimeOut(MyProgressDialog dialog) {
						SystemUtil.displayToast(mActivity,
								R.string.progress_timeout);
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
					}
				});
	}
	
	private void hideSoftInput(){
//		if(inputMethodManager.isAcceptingText()){
//			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
//			
//		}
//		inputMethodManager.hideSoftInputFromWindow(account.getWindowToken(),0);
		
//		int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; 
//		((Activity) mActivity).getWindow().addFlags(flags); 
		try{
			View v = ((Activity) mActivity).getCurrentFocus();
			inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(Exception e){
			Log.i("CustTransfer", e.toString());
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
