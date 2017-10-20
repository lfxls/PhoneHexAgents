package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.common.powertech.ItemListActivity;
import com.common.powertech.PowertechApplication;
import com.common.powertech.R;
import com.common.powertech.SMSActivity;
import com.common.powertech.bussiness.PULLParse_Login;
import com.common.powertech.bussiness.PULLParse_UpdateAndVerifyTime;
import com.common.powertech.bussiness.Request_Upload_Setting;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.LoginError;
import com.common.powertech.dbbean.LoginNameData;
import com.common.powertech.dbbean.PhoneNumber;
import com.common.powertech.dbbean.PrinterTemp;
import com.common.powertech.dbbean.ServerAddress;
import com.common.powertech.dbbean.SystemParam;
import com.common.powertech.exception.OtherException;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.EncryptionDES;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.Login_Class;
import com.common.powertech.xml.UpdateAndVerifyTime_Class;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.impl.PrinterUtils;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.sample.MainActivity;
import com.gprinter.sample.PrinterConnectDialog;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomProgressDialog;

public class LoginActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnDismissListener {

	private Button btn_login, languageImageView;
	private EditText et_login_name, et_login_password;
	private TextView tv_app_version,tv_imei;
	private ImageView mImageView, imgclearinfoView, imgpasswordclearinfo;
	private CheckBox sms_mode_checkbox;
	private ListView mUsernameListView, mLanguageListView;
	private static CustomProgressDialog progressDialog;
	private static MyProgressDialog progressDialog2;
	private static String download_url;
	private static int ISEXIT_SEND_SMS = 0;
	private String loginName,PRDTYPEALL=null,PHONEAMOUNTCONFIG=null;
	private SmsBroadcastReceiver mSmsBroadcastReceiver;
	private IntentFilter smsIntentFilter;
	private static Timer mTimer;
	private static TimerTask mTimerTask;
	private int Laserh = 0;
	int LangHeight =0 ;
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    public static final String CONNECT_STATUS = "connect.status";
	private static final int                       INTENT_PORT_SETTINGS = 0;
	private static final int                       MAX_PRINTER_CNT = 1;
	private              PortParameters            mPortParam[]         = new PortParameters[MAX_PRINTER_CNT];
	private              int                       mPrinterId           = 0;
	
	private static int backT = 0; //次数
	private static int backTime = 5000; // 时间段
	private static runBack runBackIm = null;
	PowertechApplication app ;
    
	// 登录下拉菜单用
	private ArrayList<String> usernameList = new ArrayList<String>();
	private ArrayList<String> languageList = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter, languageAdapter;
	private PopupWindow usrnamePopup, languagePopup;
	private boolean isLoginNameShowing, isLanguageShowing, mloginPopup, mLanguagePopup;		
	
	   class PrinterServiceConnection implements ServiceConnection {
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	            Log.i("ServiceConnection", "onServiceDisconnected() called");
	            mGpService = null;
	        }

	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            mGpService = GpService.Stub.asInterface(service);
	        }
	    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//luyq add 非天波设备直接退出
		GlobalParams.DeviceModel = "TPS390";
		if (!"TPS550".equalsIgnoreCase(GlobalParams.DeviceModel)&&
				!"TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)){
			System.exit(0);
			return;
		}
        //若数据库存在主题，语言，声音，锁屏时间，则赋值到GlobalParams，否则将默认值赋值到GlobalParams并保存到数据库
		BaseDao<SystemParam, Integer> baseDao = new BaseDao<SystemParam, Integer>(LoginActivity.this, SystemParam.class);
		if(baseDao.isExists(1)){
			SystemParam systemParam = baseDao.findById(1);
			GlobalParams.Theme = systemParam.getTheme();
			GlobalParams.LANGUAGE = systemParam.getLanguage();
			GlobalParams.VOICE = systemParam.getVoice();
			GlobalParams.LOCKTIME = systemParam.getLocktime();
		}else {
			GlobalParams.Theme = 1;
			GlobalParams.LANGUAGE = "en";
			GlobalParams.VOICE = "1";
			GlobalParams.LOCKTIME = "1";
			SystemParam systemParam = new SystemParam(GlobalParams.Theme, GlobalParams.LANGUAGE, GlobalParams.VOICE, GlobalParams.LOCKTIME);
			baseDao.create(systemParam);
		}
		//若数据库存在服务器地址，则赋值到GlobalParams，否则将默认值赋值到GlobalParams并保存到数据库
		BaseDao<ServerAddress, Integer> baseDao2 = new BaseDao<ServerAddress, Integer>(LoginActivity.this, ServerAddress.class);
		if(baseDao2.isExists(1)){
			ServerAddress serverAddress = baseDao2.findById(1);
			GlobalParams.SERVER_ADDRESS = serverAddress.getAddress();
		}else {
			GlobalParams.SERVER_ADDRESS = "172.16.251.91:9091";//115.238.36.165:19091
			ServerAddress serverAddress = new ServerAddress(GlobalParams.SERVER_ADDRESS);
			baseDao2.create(serverAddress);
		}
        //更新界面
		SystemUtil.setAppLanguageChange(LoginActivity.this);
		if (GlobalParams.Theme == 1) {
			setTheme(R.style.VioletTheme);
		} else if (GlobalParams.Theme == 2) {
			setTheme(R.style.OrangeTheme);
		}

		GlobalParams.LoginTheme = GlobalParams.Theme;
		GlobalParams.LoginLanguage=GlobalParams.LANGUAGE;
			
		setContentView(R.layout.login);
		//若数据库存在短信号码，则赋值到GlobalParams.PNO,否则将默认值赋值到GlobalParams并保存到数据库
		BaseDao<PhoneNumber, Integer> phoneNumberDao = new BaseDao<PhoneNumber, Integer>(LoginActivity.this, PhoneNumber.class);
		if(phoneNumberDao.isExists(1)){
			PhoneNumber mPhoneNumber = phoneNumberDao.findById(1);
			GlobalParams.PNO = mPhoneNumber.getPhone();
		}else {
			GlobalParams.PNO = "18806508487";
			PhoneNumber phoneNumber = new PhoneNumber(GlobalParams.PNO);
			phoneNumberDao.create(phoneNumber);
		}

        tv_app_version = (TextView) findViewById(R.id.tv_app_version);
        tv_app_version.setText("V"
                + SystemUtil.getAppVersionName(LoginActivity.this));

        tv_imei = (TextView) findViewById(R.id.tv_imei);
        tv_imei.setText("IMEI: "
				+ SystemUtil.getIMEI(LoginActivity.this));

		sms_mode_checkbox = (CheckBox) findViewById(R.id.sms_mode_checkbox);
		sms_mode_checkbox.setText(getString(R.string.login_tv_duanxinmoshi));

		mImageView = (ImageView) findViewById(R.id.imgusernamedrop);
		mImageView.setOnClickListener(this);

		imgclearinfoView = (ImageView) findViewById(R.id.imgclearinfo);
		imgclearinfoView.setOnClickListener(this);

		imgpasswordclearinfo = (ImageView) findViewById(R.id.imgpasswordclearinfo);
		imgpasswordclearinfo.setOnClickListener(this);

		languageImageView = (Button) findViewById(R.id.languageImageView);
		languageImageView.setOnClickListener(this);
	
		et_login_name = (EditText) findViewById(R.id.username_edittext);
		et_login_password = (EditText) findViewById(R.id.password_edittext);
		et_login_password.setOnKeyListener(onKey);
		
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_login_name.setText("admin");
				et_login_password.setText("hx888888");
				loginName = StringUtil.convertStringNull(et_login_name.getText().toString());
				final String loginPassword = StringUtil.convertStringNull(et_login_password.getText().toString());
				// 判断用户名是否为空
				if (loginName == null || loginName.length() == 0) {
					Toast.makeText(LoginActivity.this,getString(R.string.login_username_not_null),
							Toast.LENGTH_LONG).show();
					et_login_name.requestFocus();
					return;
				}
				Pattern pattern = Pattern.compile("^\\w*$");
				Matcher matcher = pattern.matcher(loginName);
				if(!matcher.matches()){
					Toast.makeText(LoginActivity.this,getString(R.string.login_username_error),
							Toast.LENGTH_LONG).show();
					et_login_name.requestFocus();
					return;
				}
				
				// 判断密码是否为空
				if (loginPassword == null || loginPassword.length() == 0) {
					Toast.makeText(LoginActivity.this,getString(R.string.login_password_not_null),
							Toast.LENGTH_LONG).show();
					et_login_password.requestFocus();
					 return;
				}
				
				String mach = "(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%\\^&*?]+$";
				Pattern pattern2 = Pattern.compile(mach);
				Matcher matcher2 = pattern.matcher(loginName);
				if(!matcher2.matches()){
					Toast.makeText(LoginActivity.this,getString(R.string.login_password_error),
							Toast.LENGTH_LONG).show();
					et_login_password.requestFocus();
					 return;
				}

				if (GlobalParams.EnterAndroidLoginName.equals(loginName)
						&& "ls".equals(loginPassword)) {
					jumpToAndroidSystem();
					System.exit(0);
					return;
				}
				//判断该账户登录失败次数是否达到5次，若是则锁定15分钟
				try {
					if(isLoginLock(SystemUtil.getCurrentDateTimeHH24()) == 1){
						et_login_password.setText("");
						et_login_password.requestFocus();//账户锁定的情况下登陆失败也要把密码清空并且焦点聚在输入框
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}

				if (progressDialog == null) {
					progressDialog = CustomProgressDialog.createProgressDialog(LoginActivity.this, 45000, new CustomProgressDialog.OnTimeOutListener() {

						@Override
						public void onTimeOut(CustomProgressDialog dialog) {
							Message message = new Message();
							Bundle bundle = new Bundle();
							//超时提醒有问题，原来是短信发送超时的提醒
							bundle.putString("ErrorMsg", getString(R.string.login_message_server_response_timeout));
							message.setData(bundle);
							ihandler.sendMessage(message);
							if(dialog != null){
								dialog.dismiss();
								dialog = null;
							}
						}
					});
				}
				progressDialog.setTitle(R.string.login_progressdialog_title);
				progressDialog.setMessage(getString(R.string.login_progressdialog_message));
				progressDialog.setCancelable(false);
				progressDialog.show();

				if(sms_mode_checkbox.isChecked()){
					
				}else {
				 new Thread(new Runnable() {

					@Override
					public void run() {
						  String  SERVER_ADDRESS="";
						   String[] SERVER_ADDRESSS=GlobalParams.SERVER_ADDRESS.split("&"); 
					      for (int i = 0; i < SERVER_ADDRESSS.length; i++) 
					      {
					  		 boolean flag = true;
					  		SERVER_ADDRESS=SERVER_ADDRESSS[i].trim();
					  		
					  		app.setSERVERADDRESS(SERVER_ADDRESS);
						try {
							getUpdateAndVerifyTime(loginPassword);
						} catch (ConnectException e) {
							flag=false;
							if(i == SERVER_ADDRESSS.length){
							e.printStackTrace();
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString(
									"ErrorMsg",
											getString(
													R.string.login_message_supply_server_error));
							message.setData(bundle);
							ihandler.sendMessage(message);
//							startActivity(new Intent(LoginActivity.this,NetWorkSettingActivity.class).putExtra("flag_1", 0));
							Intent intent = new Intent(LoginActivity.this, NetWorkSettingActivity.class);
					        boolean[] state = getConnectState();
					        intent.putExtra(CONNECT_STATUS, state);
					        intent.putExtra("flag_1", 0);
					        startActivity(intent);
							}continue;
						} catch (SocketTimeoutException e) {
							flag=false;
							if(i == SERVER_ADDRESSS.length){
							e.printStackTrace();
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString(
									"ErrorMsg",
											getString(
													R.string.login_message_server_response_timeout));
							message.setData(bundle);
							ihandler.sendMessage(message);
							}continue;
						} catch (OtherException e) {
							e.printStackTrace();
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("ErrorMsg", e.getMessage());
							message.setData(bundle);
							ihandler.sendMessage(message);
						} catch (Exception e) {
							flag=false;

							if(i == SERVER_ADDRESSS.length){
							e.printStackTrace();
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString(
									"ErrorMsg",
											getString(
													R.string.login_message_unknow_error));
							message.setData(bundle);
							ihandler.sendMessage(message);
							}continue;
						}if(flag==true){
		  			       //GlobalParams.SERVER_ADDRESS=ASSA;
		  			      break; 
						}
					}
				 }
				}).start();
			  }
			}
		});
		//语言选择框显示当前语言
		if(GlobalParams.LANGUAGE.equals("zh")){
			languageImageView.setText(getString(R.string.main_xitongshezhi_textview_chinese));			
		}else if (GlobalParams.LANGUAGE.equals("en")) {
			languageImageView.setText(getString(R.string.main_xitongshezhi_textview_english));
		}else if (GlobalParams.LANGUAGE.equals("fr")) {
			languageImageView.setText(getString(R.string.main_xitongshezhi_textview_french));
		}		

		languageList
				.add(getString(R.string.main_xitongshezhi_textview_chinese));
		languageList
				.add(getString(R.string.main_xitongshezhi_textview_english));
		languageList
		        .add(getString(R.string.main_xitongshezhi_textview_french));
		
		smsIntentFilter = new IntentFilter();
		smsIntentFilter.addAction(GlobalParams.RECEIVER_SMS_ACTION);
		smsIntentFilter.addAction(GlobalParams.SENT_SMS_ACTION);
		
		if(GlobalParams.DeviceModel.equals("TPS390")){
			if(SystemUtil.getLaserh() != null && SystemUtil.getLaserh().equals("1")){
				Laserh = 1;
			}
		}
        connection();
        PowertechApplication.getInstance().addActivity(this);
        
        app = (PowertechApplication) getApplication();
        
        //TODO 自动登录测试
//        new PrinterUtils(this);
//        btn_login.performClick();
	}
	Handler backHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){
				backT = 0;
			}
			super.handleMessage(msg);
		}
	};
//	Thread runBack = new Thread(new Runnable(){
	public class runBack extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			SystemClock.sleep(backTime);
			Message msg = new Message();
			msg.what = 1;
			backHandler.sendMessage(msg);
		}
		
	}
	
	  private void connection() {
	        conn = new PrinterServiceConnection();
	        Intent intent = new Intent(this, GpPrintService.class);
	        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	    }
		private void initPortParam() {
//			boolean[] state = {false};//默认当前未连接
	        boolean[] state = getConnectState();
			if(state==null){
				return;
			}
			for (int i = 0; i < MAX_PRINTER_CNT; i++) {
				PortParamDataBase database = new PortParamDataBase(this);
				mPortParam[i] = new PortParameters();
				mPortParam[i] = database.queryPortParamDataBase("" + i);
				mPortParam[i].setPortOpenState(state[i]);
			}
		}

	@Override
	protected void onResume() {
		super.onResume();
        GlobalParams.If_CloseFlashLight = true;				
		sms_mode_checkbox.setText(getString(R.string.login_tv_duanxinmoshi));
		//用户名输入框显示上一次登录的用户名
		showLoginInfo();		

		// 主题不一致时，重新加载页面
		if (GlobalParams.Theme != GlobalParams.LoginTheme) {
			recreate();
		    return;
		}
		
		if (!GlobalParams.LoginLanguage.equals(GlobalParams.LANGUAGE)) {
			recreate();
			return;
		}
		
		mSmsBroadcastReceiver = new SmsBroadcastReceiver();
		registerReceiver(mSmsBroadcastReceiver, smsIntentFilter);
	}
	@Override
	protected void onStart(){
		super.onStart();
		Log.e("LoginActivity", "onStart");
	}
	
	@Override
	protected void onRestart(){
		Log.e("LoginActivity", "onreStart");
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mSmsBroadcastReceiver != null){
			unregisterReceiver(mSmsBroadcastReceiver);
			mSmsBroadcastReceiver = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		et_login_password.setText("");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}

	private static class Ihandler extends Handler {
		private final WeakReference<Activity> mActivity;

		public Ihandler(LoginActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			Toast.makeText(mActivity.get(),
					msg.getData().getString("ErrorMsg"), Toast.LENGTH_LONG)
					.show();
			ISEXIT_SEND_SMS = 0;
			if(mTimer != null){
				mTimer.cancel();
				mTimer = null;
			}
			if(mTimerTask != null){
				mTimerTask.cancel();
				mTimerTask = null;
			}
			super.handleMessage(msg);
		}
	}
	private Ihandler ihandler = new Ihandler(LoginActivity.this);

	// 从登录接口获取数据并赋值到GlobalParams
	public void setGlobalParams(List<Login_Class> list_login_class) {
		PowertechApplication.setENELGROUP1(null); 
		PowertechApplication.setENELGROUP2(null); 
		PowertechApplication.setENELGROUP3(null); 
		PowertechApplication.setENELGROUP4(null); 
		for (Login_Class login_Class : list_login_class) {
			if (login_Class.getKEY() != null) {
				GlobalParams.KEY = login_Class.getKEY();
			}
			if (login_Class.getSESSION_ID() != null) {
				GlobalParams.SESSION_ID = login_Class.getSESSION_ID();
			}
			if (login_Class.getLAW_NAME() != null) {
				GlobalParams.LAW_NAME = login_Class.getLAW_NAME();
			}
			if (login_Class.getOPER_NAME() != null) {
				GlobalParams.OPER_NAME = login_Class.getOPER_NAME();
			}
			if (login_Class.getCASH_AC_BAL() != null) {
				GlobalParams.CASH_AC_BAL = login_Class.getCASH_AC_BAL();
			}
			if (login_Class.getBUY_ELE_WAY() != null) {
				GlobalParams.BUY_ELE_WAY = login_Class.getBUY_ELE_WAY();
			}
			if (login_Class.getPAY_PWD() != null) {
				GlobalParams.PAY_PWD = login_Class.getPAY_PWD();
			}
			if (login_Class.getOPER_LIST() != null) {
				GlobalParams.OPER_LIST = login_Class.getOPER_LIST();
			}
			if (login_Class.getMENU_ID() != null) {
				GlobalParams.MENU_ID = login_Class.getMENU_ID();
			}
			if (login_Class.getMENU_NAME() != null) {
				GlobalParams.MENU_NAME = login_Class.getMENU_NAME();
			}
			if (login_Class.getMENU_NAME_EN() != null) {
				GlobalParams.MENU_NAME_EN = login_Class.getMENU_NAME_EN();
			}
			if (login_Class.getMENU_NAME_FR() != null) {
				GlobalParams.MENU_NAME_FR = login_Class.getMENU_NAME_FR();
			}
			if (login_Class.getREAD4428() != null) {
				GlobalParams.READ4428 = login_Class.getREAD4428();
			}
			if (login_Class.getREAD4442() != null) {
				GlobalParams.READ4442 = login_Class.getREAD4442();
			}
			if (login_Class.getMCARD() != null) {
				GlobalParams.MCARD = login_Class.getMCARD();
			}
			if (login_Class.getB4() != null) {
				GlobalParams.B4 = login_Class.getB4();
			}
			if (login_Class.getDe() != null) {
				GlobalParams.De = login_Class.getDe();
			}
			if (login_Class.getB9() != null) {
				GlobalParams.B9 = login_Class.getB9();
			}
			if (login_Class.getLASERH() != null) {
				if(login_Class.getLASERH().equals("1")){
					GlobalParams.LASERH = true;
				}				
			}
			if (login_Class.getPNO() != null) {
				GlobalParams.PNO = login_Class.getPNO();
				savePhoneNumber();
			}
			if (login_Class.getMINRECHARGE() != null) {
				app.setMINRECHARGE(login_Class.getMINRECHARGE());
			}else{
				app.setMINRECHARGE("0");
			}
			if (login_Class.getENEL_GRP1() != null) {
				PowertechApplication.setENELGROUP1(login_Class.getENEL_GRP1()); 
			}
			if (login_Class.getENEL_GRP2() != null) {
				PowertechApplication.setENELGROUP2(login_Class.getENEL_GRP2());
			}
			if (login_Class.getENEL_GRP3() != null) {
				PowertechApplication.setENELGROUP3(login_Class.getENEL_GRP3());
			}
			if (login_Class.getENEL_GRP4() != null) {
				PowertechApplication.setENELGROUP4(login_Class.getENEL_GRP4());
			}
			
			if (login_Class.getPRDTYPEALL() != null) {
				PRDTYPEALL= login_Class.getPRDTYPEALL();
			}
			if (login_Class.getPHONEAMOUNTCONFIG() != null) {
				PHONEAMOUNTCONFIG= login_Class.getPHONEAMOUNTCONFIG();
			}else{
//				app.setPHONEAMOUNTCONFIG("");
				PHONEAMOUNTCONFIG="";
			}
		}
	}

	// 连接服务器进行检查更新
	private void getUpdateAndVerifyTime(final String loginPassword) throws Exception {
		String temp_version = "";
		BaseDao<PrinterTemp, Integer> baseDao = new BaseDao<PrinterTemp, Integer>(
				LoginActivity.this, PrinterTemp.class);
		if (baseDao.isExists(1)) {
			temp_version = baseDao.findById(1).getTemp_version();
		}
		String xmlData = "<ROOT><TOP><VERSION>"//组装请求服务器的报文
				+ SystemUtil.getAppVersionName(LoginActivity.this)
				+ "</VERSION><SOURCE>3</SOURCE><IMEI>"
				+ SystemUtil.getIMEI(LoginActivity.this)
				+ "</IMEI><REQUEST_TIME>" + SystemUtil.getCurrentDateTimeHH24()
				+ "</REQUEST_TIME><LOCAL_LANGUAGE>"
				+ SystemUtil.getLocalLanguage(LoginActivity.this)
				+ "</LOCAL_LANGUAGE></TOP><BODY><TEMP_VERSION>" + temp_version
				+ "</TEMP_VERSION></BODY></ROOT>";
		Log.v("更新请求:", xmlData);
		String serverxmlData = Client.ConnectServer("PUpdateAndVerifyTime",
				xmlData);
		Log.v("更新响应:", serverxmlData);
		List<UpdateAndVerifyTime_Class> list_updateAndVerifyTime_class = PULLParse_UpdateAndVerifyTime
				.getUpdateAndVerifyTimeList(new ByteArrayInputStream(
						serverxmlData.getBytes()));
		for (final UpdateAndVerifyTime_Class updateAndVerifyTime_Class : list_updateAndVerifyTime_class) {
			if (updateAndVerifyTime_Class.getSERVER_DATETIME() != null) {
				sendBroadcast(new Intent(GlobalParams.UPDATE_SYSTEM_DATE_AND_TIME_ACTION).putExtra("DATEANDTIME", updateAndVerifyTime_Class.getSERVER_DATETIME()));
			}
			if (updateAndVerifyTime_Class.getTEMP_VERSION() != null) {
				StringBuilder mStringBuilder =new StringBuilder();
				Pattern mPattern = Pattern.compile("<TEMP_LIST>(.*?)</TEMP_LIST>");
				Matcher mMatcher = mPattern.matcher(serverxmlData);
				ArrayList<String> mArrayList = new ArrayList<String>();
				while(mMatcher.find()){
					mArrayList.add(mMatcher.group());
				}
				if(mArrayList != null && mArrayList.size() > 0){
					for (String string : mArrayList) {
						mStringBuilder.append(string+";");
					}
				}
				if(baseDao.isExists(1)){
					baseDao.excute("UPDATE printer_temp SET temp_version="+updateAndVerifyTime_Class.getTEMP_VERSION()+",temp_list='"+mStringBuilder.toString()+"' WHERE id=1");
				}else {
					PrinterTemp printerTemp = new PrinterTemp(updateAndVerifyTime_Class.getTEMP_VERSION(),mStringBuilder.toString());
					baseDao.create(printerTemp);
				}
			}
			if (updateAndVerifyTime_Class.getU_VERSION() != null
					&& updateAndVerifyTime_Class.getU_URL() != null
					&& updateAndVerifyTime_Class.getU_RULE() != null) {
				download_url = updateAndVerifyTime_Class.getU_URL();
				if (updateAndVerifyTime_Class.getU_RULE().equals("3")) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showChooseUpdateDialog(loginPassword, updateAndVerifyTime_Class.getSERVER_DATETIME());
						}
					});
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if(progressDialog != null){
								progressDialog.dismiss();
								progressDialog = null;
							}
							DownTask downTask = new DownTask(LoginActivity.this);
							downTask.execute(download_url);
						}
					});
				}
			}else {
				checkTimeIsRight(loginPassword, updateAndVerifyTime_Class.getSERVER_DATETIME());
			}
		}
	}
	
	//登录
	private void getLogin(String loginPassword) {
		
		try{
			String time = SystemUtil.getCurrentDateTimeHH24();	
		    String xmlData = "<ROOT><TOP><VERSION>"
				+ SystemUtil
						.getAppVersionName(LoginActivity.this)
				+ "</VERSION><SOURCE>3</SOURCE><IMEI>"
				+ SystemUtil
						.getIMEI(LoginActivity.this)
				+ "</IMEI><REQUEST_TIME>"
				+ time
				+ "</REQUEST_TIME><LOCAL_LANGUAGE>"
				+ SystemUtil.getLocalLanguage(LoginActivity.this)
				+ "</LOCAL_LANGUAGE></TOP><BODY><OPER_ID>"
				+ loginName + "</OPER_ID><LOGIN_PWD>"
				+ EncryptionDES.DESSTR(loginPassword)
				+ "</LOGIN_PWD><SYS_LASER>"+Laserh+"</SYS_LASER></BODY></ROOT>";
		Log.v("登录请求:", xmlData);
		String serverxmlData = Client.ConnectServer(
				"PLogin", xmlData);
		Log.v("登录响应:", serverxmlData);
		//把登录失败的用户名保存到数据库
		recordErrorLoginName(serverxmlData, time);
		if(Client.Parse_XML(serverxmlData, "<RSPCOD>", "</RSPCOD>").equals("00526")){

			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			ISEXIT_SEND_SMS = 0;
			if(mTimer != null){
				mTimer.cancel();
				mTimer = null;
			}
			if(mTimerTask != null){
				mTimerTask.cancel();
				mTimerTask = null;
			}
			return;
		}
		List<Login_Class> list_login_class = PULLParse_Login
				.getLoginList(new ByteArrayInputStream(
						serverxmlData.getBytes()));
		setGlobalParams(list_login_class);
        saveParam();
        SystemClock.sleep(500);
		initPortParam();
        connectOrDisConnectToDevice(mPrinterId);//自动连接蓝牙打印机
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("ErrorMsg",getString(R.string.login_message_success));
		app.setPRDTYPELIMIT(PRDTYPEALL);
		app.setPHONEAMOUNTCONFIG(PHONEAMOUNTCONFIG);
		message.setData(bundle);
		ihandler.sendMessage(message);
		GlobalParams.LOGIN_USER_ID = loginName;
		saveLoginInfo();
		//上传主题，语言，声音，锁屏时间到服务器
        upload_parameter();
        //从数据库中删除登录失败的用户名
        deleteErrorLoginName();
		startActivity(new Intent(LoginActivity.this,ItemListActivity.class));
		} catch (ConnectException e) {
			e.printStackTrace();
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString(
					"ErrorMsg",
							getString(
									R.string.login_message_supply_server_error));
			message.setData(bundle);
			ihandler.sendMessage(message);
//			startActivity(new Intent(LoginActivity.this,NetWorkSettingActivity.class).putExtra("flag_1", 0));
			Intent intent = new Intent(LoginActivity.this, NetWorkSettingActivity.class);
	        boolean[] state = getConnectState();
	        intent.putExtra(CONNECT_STATUS, state);
	        intent.putExtra("flag_1", 0);
	        startActivity(intent);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString(
					"ErrorMsg",
							getString(
									R.string.login_message_server_response_timeout));
			message.setData(bundle);
			ihandler.sendMessage(message);
		} catch (OtherException e) {
			e.printStackTrace();
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("ErrorMsg", e.getMessage());
			message.setData(bundle);

			ihandler.sendMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString(
					"ErrorMsg",
							getString(
									R.string.login_message_unknow_error));
			message.setData(bundle);
			ihandler.sendMessage(message);
		}
	}
	void connectOrDisConnectToDevice(int PrinterId) {
		mPrinterId = PrinterId;
		int rel = 0;
		if(mPortParam[PrinterId]==null){
			return;
		}
			if (CheckPortParamters(mPortParam[PrinterId])) {
				try {
					mGpService.closePort(mPrinterId);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				switch (mPortParam[PrinterId].getPortType()) {
					case PortParameters.BLUETOOTH:
						try {
							SystemClock.sleep(500);
							rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(), mPortParam[PrinterId].getBluetoothAddr(), 0);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break;
				}
				GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
			} 
	}
	Boolean CheckPortParamters(PortParameters param) {
		boolean rel = false;
		int type = param.getPortType();
		if (type == PortParameters.BLUETOOTH) {
			if (!param.getBluetoothAddr().equals("")) {
				rel = true;
			}
		}
		return rel;
	}

	// apk升级选择
	private void showChooseUpdateDialog(final String loginPassword, final String serverDateTime) {
		new AlertDialog.Builder(LoginActivity.this)
				.setMessage(
						getString(
								R.string.update_alertdialog_message))
				.setPositiveButton(
						getString(
								R.string.update_alertdialog_positive_button),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if(progressDialog != null){
									progressDialog.dismiss();
									progressDialog = null;
								}
								DownTask downTask = new DownTask(
										LoginActivity.this);
								downTask.execute(download_url);
							}
						})
				.setNegativeButton(
						getString(
								R.string.update_alertdialog_negative_button),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								new Thread(new Runnable() {
										
									@Override
									public void run() {
										checkTimeIsRight(loginPassword, serverDateTime);
									}
								}).start();																	
							}
						}).setCancelable(false).create().show();
	}

	// apk升级下载
	class DownTask extends AsyncTask<String, Integer, Void> {

		public Context context;

		boolean flg = true;
		
		public DownTask(Context ctx) {
			context = ctx;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setConnectTimeout(10000);
				httpURLConnection.setReadTimeout(30000);
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.connect();
				int length = httpURLConnection.getContentLength();
				InputStream inputStream = httpURLConnection.getInputStream();
				File file = new File(GlobalParams.UPDATE_SAVE_PATH);
				if (file.exists()) {
					file.delete();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				byte[] buffer = new byte[20480];
				int len = 0;
				int downloading_size = 0;
				while ((len = inputStream.read(buffer)) != -1) {
					if(!flg){
						fileOutputStream.close();
						inputStream.close();
						httpURLConnection.disconnect();
						return null;
					} 
					
					fileOutputStream.write(buffer, 0, len);
					downloading_size += len;
					publishProgress(downloading_size / (length / 100));
				}
				fileOutputStream.close();
				inputStream.close();
				httpURLConnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(progressDialog2 != null){
				progressDialog2.dismiss();
				progressDialog2 = null;
			}
			SystemUtil.installApk(LoginActivity.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (progressDialog2 == null)
				progressDialog2 = MyProgressDialog.createProgressDialog(context, 600000, new MyProgressDialog.OnTimeOutListener() {
					
					@Override
					public void onTimeOut(MyProgressDialog dialog) {
						if(dialog != null){
							flg = false;
							dialog.setProgress(0);
							dialog.dismiss();
							dialog = null;
						}						
					}
				});
			progressDialog2.setTitle(getResources().getString(
					R.string.update_progressdialog_title));
			progressDialog2.setMessage(getResources().getString(
					R.string.update_progressdialog_message));
			progressDialog2.setCancelable(false);
			progressDialog2.setMax(100);
			progressDialog2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog2.setIndeterminate(false);
			progressDialog2.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressDialog2.setProgress(values[0]);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(progressDialog2 != null){
				progressDialog2.dismiss();
				progressDialog2 = null;
			}
		}

	}

	// 跳转到网络设置界面
	public void networkSetting(View view) {
        Intent intent = new Intent(this, NetWorkSettingActivity.class);
        boolean[] state = getConnectState();
        intent.putExtra(CONNECT_STATUS, state);
        intent.putExtra("flag_1", 0);
        this.startActivity(intent);
//		startActivity(new Intent(LoginActivity.this,NetWorkSettingActivity.class).putExtra("flag_1", 0));
	}	
	public boolean[] getConnectState() {
	        boolean[] state = new boolean[MAX_PRINTER_CNT];
	        for (int i = 0; i < MAX_PRINTER_CNT; i++) {
	            state[i] = false;
	        }
	        for (int i = 0; i < MAX_PRINTER_CNT; i++) {
	        	  SystemClock.sleep(300);
	            try {
	                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
	                    state[i] = true;
	                }
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        Log.v("zhuangtai",":"+state[0]);
	        return state;
	    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgusernamedrop) {
			// 登录名选择下拉对话框
			if (usernameList != null && usernameList.size() > 0 && !mloginPopup) {
				mloginPopup = true;
				initPopup();
			}
			if (usrnamePopup != null) {
				if (!isLoginNameShowing) {
					usrnamePopup.showAsDropDown(et_login_name, 0, -5);
					isLoginNameShowing = true;
				} else {
					usrnamePopup.dismiss();
				}
			}
		} else if (v.getId() == R.id.languageImageView) {
			// 语言选择下拉对话框
			if (languageList != null && languageList.size() > 0
					&& !mLanguagePopup) {
				mLanguagePopup = true;
				initLanguagePopup();
			}
			if (languagePopup != null) {
				if (!isLanguageShowing) {
					if(GlobalParams.DeviceModel.equals("TPS550")){
						languagePopup.showAsDropDown(languageImageView, 0, -5);
					}else if (GlobalParams.DeviceModel.equals("TPS390")) {
						int height=languageImageView.getHeight();
						int heightView=mLanguageListView.getHeight();
						heightView=mLanguageListView.getHeight();
						heightView=mLanguageListView.getHeight();
//						languagePopup.showAtLocation(languageImageView, Gravity.TOP, 30, 980);
//						languagePopup.showAsDropDown(languageImageView,-0,-(height+heightView));
						languagePopup.showAsDropDown(languageImageView,-0,-(height*5));
						heightView=mLanguageListView.getHeight();
					}
					isLanguageShowing = true;
				} else {
					languagePopup.dismiss();
				}
			}
		} else if (v.getId() == R.id.imgclearinfo) {
			et_login_name.setText("");
		} else if (v.getId() == R.id.imgpasswordclearinfo) {
			et_login_password.setText("");
		}
	}

	// 账号选择框初始化
	private void initPopup() {
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, usernameList);
		mUsernameListView = new ListView(this);
		mUsernameListView.setAdapter(mAdapter);
		mUsernameListView.setOnItemClickListener(this);
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		int width = et_login_name.getWidth() + mImageView.getWidth() - 11;
		usrnamePopup = new PopupWindow(mUsernameListView, width, height, true);
		usrnamePopup.setOutsideTouchable(true);
		usrnamePopup.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popup_bg));
		usrnamePopup.setOnDismissListener(this);
	}

	// 弹出语言选择框
	private void initLanguagePopup() {
		languageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, languageList);
		mLanguageListView = new ListView(this);
		mLanguageListView.setAdapter(languageAdapter);
		mLanguageListView.setOnItemClickListener(this);
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		int width = languageImageView.getWidth();
		LangHeight=height;
		languagePopup = new PopupWindow(mLanguageListView, width, height, true);
		languagePopup.setOutsideTouchable(true);
		languagePopup.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popup_bg));
		languagePopup.setOnDismissListener(this);
	}

	//保存登录用户名
	private void saveLoginInfo() {		
		String loginName = et_login_name.getText().toString();
		BaseDao<LoginNameData, Integer> baseDao = new BaseDao<LoginNameData, Integer>(LoginActivity.this, LoginNameData.class);
		int id=(int)baseDao.queryRawValueBySQL("SELECT id FROM login_name_data WHERE loginname='"+loginName+"'");
		if(id == 0){
			LoginNameData loginNameData = new LoginNameData(loginName, SystemUtil.getCurrentDateTimeHH24());
			baseDao.create(loginNameData);
		}else {
			baseDao.excute("UPDATE login_name_data SET time='"+SystemUtil.getCurrentDateTimeHH24()+"' WHERE id="+id);
		}
	}
	
	//显示用户登录名
	private void showLoginInfo(){
		BaseDao<LoginNameData, Integer> baseDao = new BaseDao<LoginNameData, Integer>(LoginActivity.this, LoginNameData.class);
		if(baseDao.isExists(1)){
			List<String[]> list = baseDao.queryRawBySQL("SELECT loginname FROM login_name_data ORDER BY time DESC");
			usernameList.clear();
			for(int i =0;i < list.size();i++){
				usernameList.add(list.get(i)[0]);
				if(i == 4){
					break;
				}
			}
			et_login_name.setText(usernameList.get(0));
		}
	}

	@Override
	public void onDismiss() {
		isLoginNameShowing = false;
		isLanguageShowing = false;
	}
	
	public void signUp(View v){
		Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
		startActivity(intent);
		
	};

	OnKeyListener onKey = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_ENTER
					&& KeyEvent.ACTION_UP == event.getAction()) {
				InputMethodManager imm = (InputMethodManager) v.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
							0);
				}
				btn_login.performClick();// 登录按钮自动按下
				return false;
			}
			return false;
		}
	};

	/**
	 * 监听Activity按键事件
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_2:
		case KeyEvent.KEYCODE_3:
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_5:
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_7:
		case KeyEvent.KEYCODE_8:
		case KeyEvent.KEYCODE_9:
		case KeyEvent.KEYCODE_DEL:
		case KeyEvent.KEYCODE_PLUS:
		case KeyEvent.KEYCODE_CLEAR:
		case KeyEvent.KEYCODE_COMMA:
		case KeyEvent.KEYCODE_SEMICOLON:
		case KeyEvent.KEYCODE_PERIOD:
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_STAR:
			requestPressKey(keyCode);
			return true;
		case KeyEvent.KEYCODE_BACK:
			KeyBackDown();
			return true;
		default:
			return false;
		}
	}
	public void KeyBackDown() {
//		timer.schedule(task, 0, backTime);
    		/*AlertDialog.Builder builder = new Builder(LoginActivity.this);
        	builder.setMessage(R.string.dialog_sureout);
        	builder.setTitle(R.string.str_note);
        	builder.setPositiveButton(R.string.dialog_yes,new DialogInterface.OnClickListener(){
        		@Override
        		public void onClick(DialogInterface dialog,int which){
        			dialog.dismiss();
        			Intent i= new Intent(Intent.ACTION_MAIN);
                	i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                	i.addCategory(Intent.CATEGORY_HOME);
                	startActivity(i);
        		}
        	});
        	builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				dialog.dismiss();
    			}
    		});
        	builder.create().show();*/
		backT += 1;
		if(backT > 1){
			PowertechApplication.getInstance().AppExit();
		}else{
			 if(runBackIm!=null){  
				 runBackIm.interrupt();  
				 runBackIm = null;  
				 }  
			 (runBackIm = new runBack()).start();
			SystemUtil.displayToast(LoginActivity.this, R.string.toast_back);
		}
		
    }
	// 按键按下，执行setPredialNumber，number由UI自己根据Key组装
	private boolean requestPressKey(int key) {
		String keyStr = "";
		if (key >= KeyEvent.KEYCODE_0 && key <= KeyEvent.KEYCODE_9) {
			keyStr = Integer.toString(key - 7);
		} else {
			return false;
		}

		// 记录下按键内容
		GlobalParams.CombinationsKeyValue = StringUtil
				.convertStringNull(GlobalParams.CombinationsKeyValue) + keyStr;
		if (GlobalParams.CombinationsKeyValue
				.indexOf(GlobalParams.EnterAndroid) != -1) {
			GlobalParams.CombinationsKeyValue = "";// 恢复
			jumpToAndroidSystem();
		}
		return true;
	}

	/**
	 * 跳转到android系统
	 */
	private void jumpToAndroidSystem() {
		Intent intent = null;
		if("TPS550".equalsIgnoreCase(GlobalParams.DeviceModel)){
		    SystemUtil.showBar();
		    intent = new Intent("android.intent.action.DeskTop");
		}else if ("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)) {
		
		intent = new Intent();
		intent.setClassName("com.android.launcher3","com.android.launcher3.Launcher");	
		}
		
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (arg0 == mUsernameListView) {
			et_login_name.setText(usernameList.get(arg2));
			usrnamePopup.dismiss();
		} else if (arg0 == mLanguageListView) {
			if(arg2 == 0){
                if(GlobalParams.LANGUAGE.equals("zh")){
                    languagePopup.dismiss();
                    return;
                }
				GlobalParams.LANGUAGE = "zh";
			}else if(arg2 == 1){
                if(GlobalParams.LANGUAGE.equals("en")){
                    languagePopup.dismiss();
                    return;
                }
				GlobalParams.LANGUAGE = "en";
			}else if(arg2 == 2){
                if(GlobalParams.LANGUAGE.equals("fr")){
                    languagePopup.dismiss();
                    return;
                }
				GlobalParams.LANGUAGE = "fr";
			}		
			// 保存主题，语言，声音，锁屏时间到数据库
			saveParam();
			languagePopup.dismiss();			
			// 刷新activity才能马上奏效
			recreate();
		}

	}

    // 保存主题，语言，声音，锁屏时间到数据库
    private void saveParam() {
    	BaseDao<SystemParam, Integer> baseDao = new BaseDao<SystemParam, Integer>(LoginActivity.this, SystemParam.class);
    	baseDao.excute("UPDATE system_param SET theme="+GlobalParams.Theme+",language='"+GlobalParams.LANGUAGE+"',voice='"+GlobalParams.VOICE+"',locktime='"+GlobalParams.LOCKTIME+"' WHERE id=1");
    }
    
    //上传主题，语言，声音，锁屏时间到服务器
    private void upload_parameter(){
        String reponseXML="";
        String APIName = "PSettingUpload";
        Request_Upload_Setting.setContext(LoginActivity.this);
        Request_Upload_Setting.setDisplay(String.valueOf(GlobalParams.Theme));
        if(GlobalParams.LANGUAGE.equals("en")){
            Request_Upload_Setting.setLanguage("2");
        }else if(GlobalParams.LANGUAGE.equals("fr")){
            Request_Upload_Setting.setLanguage("3");
        }else{
            Request_Upload_Setting.setLanguage("1");
        }
        Request_Upload_Setting.setVoice(GlobalParams.VOICE);
        Request_Upload_Setting.setLocktime(GlobalParams.LOCKTIME);

        String date = Request_Upload_Setting.getRequsetXML();
        System.out.println("上传设置请求：" + date);
        try {
            reponseXML = Client.ConnectServer(APIName, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("上传设置响应：" + reponseXML);
    }
    
    //判断该账户登录失败次数是否达到5次，若是则锁定15分钟
    private int isLoginLock(String time) throws Exception{
    	BaseDao<LoginError, Integer> baseDao = new BaseDao<LoginError, Integer>(LoginActivity.this, LoginError.class);
    	List<String[]> list = baseDao.queryRawBySQL("SELECT time FROM login_error WHERE name='"+loginName+"' ORDER BY time DESC");
    	if(list.size() >= 5){
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if((mSimpleDateFormat.parse(time).getTime()-mSimpleDateFormat.parse(list.get(0)[0]).getTime()) > 900000){
				baseDao.excute("DELETE FROM login_error WHERE name='"+loginName+"'");				
			}else {
				Toast.makeText(LoginActivity.this, getString(R.string.login_fail_lock), Toast.LENGTH_LONG).show();
				return 1;
			}
    	}
    	return 0;
    }
    
    //把登录失败的用户名保存到数据库
    private void recordErrorLoginName(String serverxmlData,String time) throws Exception{
    	boolean IS_NETWORK_OR_SMS = true;
    	if(ISEXIT_SEND_SMS == 1){
    		IS_NETWORK_OR_SMS = serverxmlData.equals("00526");
    	}else {
			IS_NETWORK_OR_SMS = Client.Parse_XML(serverxmlData, "<RSPCOD>", "</RSPCOD>").equals("00526");
		}
		if(IS_NETWORK_OR_SMS){		
			LoginError mLoginError = new LoginError(loginName, time);
			BaseDao<LoginError, Integer> baseDao = new BaseDao<LoginError, Integer>(LoginActivity.this, LoginError.class);
			baseDao.create(mLoginError);
			List<String[]> list = baseDao.queryRawBySQL("SELECT time FROM login_error WHERE name='"+loginName+"' ORDER BY time ASC");
			if(list.size() > 0){
				SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (String[] strings : list) {
					if((mSimpleDateFormat.parse(time).getTime()-mSimpleDateFormat.parse(strings[0]).getTime()) > 900000){
						baseDao.excute("DELETE FROM login_error WHERE name='"+loginName+"' AND time='"+mSimpleDateFormat.parse(strings[0]).getTime()+"'");
					}
				}
				final List<String[]> li = baseDao.queryRawBySQL("SELECT time FROM login_error WHERE name='"+loginName+"' ORDER BY time ASC");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(LoginActivity.this, getString(R.string.login_fail_times)+(5-li.size()), Toast.LENGTH_LONG).show();
						et_login_password.setText("");
					}
				});
			}
		}
    }
    
    //从数据库中删除登录失败的用户名
    private void deleteErrorLoginName(){
        BaseDao<LoginError, Integer> baseDao = new BaseDao<LoginError, Integer>(LoginActivity.this, LoginError.class);
        List<String[]> list = baseDao.queryRawBySQL("SELECT * FROM login_error WHERE name='"+loginName+"'");
        if(list.size() > 0){
        	baseDao.excute("DELETE FROM login_error WHERE name='"+loginName+"'");
        } 
    }
    //若数据库存在短信号码，则更新，否则新建
    private void savePhoneNumber(){
		BaseDao<PhoneNumber, Integer> phoneNumberDao = new BaseDao<PhoneNumber, Integer>(LoginActivity.this, PhoneNumber.class);
		if(phoneNumberDao.isExists(1)){
			phoneNumberDao.excute("UPDATE phone_number SET phone='"+GlobalParams.PNO+"' WHERE id=1");
		}else {
			PhoneNumber mPhoneNumber = new PhoneNumber(GlobalParams.PNO);
			phoneNumberDao.create(mPhoneNumber);
		}
    }
    
    //登录时判断系统时间是否正确
    private void checkTimeIsRight(final String loginPassword,final String serverDateTime){
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			int i = 0;
			
			@Override
			public void run() {
				if(serverDateTime != null && (Math.abs(Long.parseLong(SystemUtil.getCurrentDateTimeHH24Format1()) - Long.parseLong(serverDateTime)) > 3000)){																										
					if(i == 2){
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("ErrorMsg", getString(R.string.login_fail_time_bad));
						message.setData(bundle);
						ihandler.sendMessage(message);
					}
					i++;
				}else {
					getLogin(loginPassword);
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, 3000);    	
    }
    
    private class SmsBroadcastReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(ISEXIT_SEND_SMS == 1){
			if(intent.getAction().equals(GlobalParams.RECEIVER_SMS_ACTION)){
				Object[] pdus = (Object[]) intent.getExtras().get("pdus");
				SmsMessage[] mSmsMessages = new SmsMessage[pdus.length];
				for(int i = 0;i < pdus.length;i++){
					mSmsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				String smsNumber = "";
				String smsReceiverText = "";
				for (SmsMessage smsMessage : mSmsMessages) {
					smsNumber = smsMessage.getDisplayOriginatingAddress();
					Log.v("短信登录响应号码:", smsNumber);
					smsReceiverText = smsMessage.getDisplayMessageBody();
					Log.v("短信登录响应文本", smsReceiverText);
				}
				if(smsNumber.contains(GlobalParams.PNO)){
					if(smsReceiverText.startsWith("P")){
						String[] text = smsReceiverText.split("\\*");
						if(text[1].equals("00000")){
							GlobalParams.SESSION_ID = text[2];
							GlobalParams.CASH_AC_BAL = text[3];
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("ErrorMsg", getString(R.string.login_message_success));
							message.setData(bundle);
							ihandler.sendMessage(message);
							GlobalParams.LOGIN_USER_ID = loginName;
							saveLoginInfo();
							deleteErrorLoginName();
							Intent intent1 = new Intent(LoginActivity.this,
									SMSActivity.class);
							startActivity(intent1);
						}else {
							try {
								recordErrorLoginName(text[1], SystemUtil.getCurrentDateTimeHH24());
							} catch (Exception e) {
								e.printStackTrace();
							}
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("ErrorMsg", text[2]);
							message.setData(bundle);
							ihandler.sendMessage(message);
						}
					}
				}
			}else if (intent.getAction().equals(GlobalParams.SENT_SMS_ACTION)) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(LoginActivity.this, R.string.duanxingoudian_sent_success, Toast.LENGTH_LONG).show();
					break;
				default:
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("ErrorMsg", getString(R.string.duanxingoudian_sent_fail));
					message.setData(bundle);
					ihandler.sendMessage(message);
					break;
				}
			}
		  }
		}
	};
}