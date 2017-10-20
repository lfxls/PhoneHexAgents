package com.common.powertech.activity;

import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.common.powertech.R;
import com.common.powertech.ItemListActivity.ReadCardMessageCallBack;
import com.common.powertech.activity.ShouDianXiangQingActivity.PrinterServiceConnection;
import com.common.powertech.bussiness.Request_Account_Query;
import com.common.powertech.bussiness.Request_Complete_Order;
import com.common.powertech.bussiness.Request_Verify_Order;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
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
import printUtils.gprinter;

public class BuyTokenActivity extends Activity{
	private Context mActivity;
	private final  String TAG = "BuyTokenActivity";
	LinearLayout passwd_lin;
	LinearLayout inf_custamt_lin;//代理商信息和下单页
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
	
//	private int TYPE=0;//0:指定代理商  1:非指定代理商
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
	private String mRspTicketXML;
	private AlertDialog mQuerengoudianDialog;
	
	  ReadCardMessageCallBack mReadCardMessageCallBack;
	    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	    private static final String[] featureStrings = { "FEATURE_UNKNOWN",
	            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
	            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
	            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
	            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
	            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
	            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
	            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
	            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
	            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
	            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND" };
  private gprinter gprinter = new gprinter();
  private UsbManager mManager;
	private Reader mReader;
	private PendingIntent mPermissionIntent;
	private PrinterServiceConnection conn = null;
	private              int                       mPrinterId           = 0;
	public static final String CONNECT_STATUS = "connect.status";
	
	//初始化 
	private void init(){
		inf_custamt_lin = (LinearLayout) rootview.findViewById(R.id.inf_custamt_lin);
		inf_amt_lin = (LinearLayout) rootview.findViewById(R.id.inf_amt_lin);
		passwd_lin = (LinearLayout) rootview.findViewById(R.id.passwd_lin);
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
		
	}
	//初始化 非指定对象菜单
	private void initUser(){
//		amt.clearFocus();
		hideSoftInput();
		//隐藏代理商相关
		inf_custamt_lin.setVisibility(View.VISIBLE);
		complete.setClickable(false);
		initParams();
	}
	
	private void initParams(){
		//初始化变量
		
		TRANSFER_NO = "";
		amtstr = "";
		verifystr = "";
		phonestr = "";
		passwordstr = "";
		//输入置空
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
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mActivity = this;
		LayoutInflater inflater = LayoutInflater.from(mActivity); 
		rootview = inflater.inflate(R.layout.activity_buytoken_detail, null);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		init();
		
		if(!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1"))
			passwd_lin.setVisibility(View.VISIBLE);
		
		getverify.setOnClickListener(GetVerify);
		complete.setOnClickListener(Complete);
		inputMethodManager=(InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		time = new TimeCount(TIME, 1000);//构造CountDownTimer对象
		setContentView(rootview);
		
		  // Get USB manager
        mManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        // Initialize reader
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN
                        || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }

                if (currState < Reader.CARD_UNKNOWN
                        || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }
            }
        });
        
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        gprinter.nGpService = null;
        connection();
        Log.e(TAG, "---------onCreate");
	}
	 private void connection() {
	        conn = new PrinterServiceConnection();
	        Intent intent = new Intent(this, GpPrintService.class);
	        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	    }
	    class PrinterServiceConnection implements ServiceConnection {
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	            Log.i("ServiceConnection", "onServiceDisconnected() called");
	            gprinter.nGpService = null;
	        }

	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            gprinter.nGpService= GpService.Stub.asInterface(service);
	            getPrinterState();
	            Log.e(TAG, "onServiceConnected");
	        }
	    }
	
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
			
			request_Verify_Order.setTRANS_CUST("");
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
				mRspTicketXML = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<TICKET>", "</TICKET>");
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
					dialog.setNegativeButton(R.string.tran_print,null);
//					dialog.setNegativeButton("dayin", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							 new PrintTask()
//                             .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							
//						}
//					});
					
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
					
					AlertDialog alertDialog = dialog.create();
					alertDialog.show();
					
					alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
						 new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    }
				    });
				        
					
					
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
		Log.e(TAG, "---------onResume");
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e(TAG, "----------onStart");
	}
	/**
	 * 获取当前蓝牙表连接状态
	 */
	public void getPrinterState(){
		//连接打印机先
        boolean[] state = getConnectState();
        if(state[mPrinterId]!=true){
        	CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
//	        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
	  	   	  builder.setMessage(R.string.str_noopen);
	  	   	  builder.setTitle(R.string.str_note);
	  	   	 builder.setPositiveButton(R.string.str_goset,
	  	             new DialogInterface.OnClickListener() {
	  	   	   @Override
	  	   	   public void onClick(DialogInterface dialog, int which) {
	  	   		   	dialog.dismiss();
	  		        Intent intent = new Intent(mActivity, NetWorkSettingActivity.class);
	  		        boolean[] state = getConnectState();
	  		        intent.putExtra(CONNECT_STATUS, state);
	  		        intent.putExtra("flag_1", 1);
	  		        mActivity.startActivity(intent);
	  	   	   }
	  	   	  });
	  	   	 builder.setNegativeButton(R.string.str_cancel,
	  	             new DialogInterface.OnClickListener() {
	  	   	   @Override
	  	   	   public void onClick(DialogInterface dialog, int which) {
	  	   		   	dialog.dismiss();
	  	   	   }
	  	   	  });
//	  	   	  AlertDialog x = builder.create();
//	  	   	  x.show();
	  	   
	  	   builder.create().show();
		
        }
	}
	 public boolean[] getConnectState() {
	        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
	        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
	            state[i] = false;
	        }
	        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
	            try {
	                if (gprinter.nGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
	                    state[i] = true;
	                }
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        return state;
	    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	  private class PrintTask extends AsyncTask<Void, Void, String> {
	    	HashMap a;
	        @Override
	        protected void onPreExecute() {
	            if (mQuerengoudianDialog != null
	                    && mQuerengoudianDialog.isShowing()) {
	                mQuerengoudianDialog.dismiss();
	            }
	            if (progressDialog != null && progressDialog.isShowing()) {
	                progressDialog.dismiss();
	            }
	            createDialog();
	            progressDialog.setTitle(getString(R.string.str_dayin));
	            progressDialog.setMessage(getString(R.string.progress_conducting));
//	            progressDialog.setIndeterminate(false);
	            progressDialog.setCancelable(false);
	            progressDialog.show();
	        }

	        @Override
	        protected String doInBackground(Void... params) {
	/*            mPrinter.start();
	            mPrinter.printXML("<TICKET>" + mRspTicketXML + "</TICKET>");
	            // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
	            // -9999 其他错误
	            int printResult = mPrinter.commitOperation();
	            mPrinter.stop();*/
//	        	printReceiptClicked();
	            mRspTicketXML = mRspTicketXML.replace("&amp;caret;","^");
//	            mRspTicketXML = mRspTicketXML.replace("&quot;", "@quot;").replace("&apos;", "@apos;").replace("&lt;", "@lt;").replace("&gt;", "@gt;");
//	            mRspTicketXML = mRspTicketXML.replace("&","&amp;");
//	            mRspTicketXML = mRspTicketXML.replace("@quot;", "&quot;").replace("@apos;", "&apos;").replace("@lt;", "&lt;").replace("@gt;", "&gt;");
	        	gprinter.printXML("<TICKET>" + mRspTicketXML + "</TICKET>");
	        	String result = gprinter.commitOperation();
	            return result;
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	if(result==null){
	        		result="Printer Success";
	        	}
	            SystemUtil.displayToast(mActivity,result);
	            if (progressDialog != null && progressDialog.isShowing()) {
	                progressDialog.dismiss();
	            }
	        }
	    }

	
}
