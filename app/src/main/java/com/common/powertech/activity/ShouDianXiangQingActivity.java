package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telpo.tps550.api.decode.Decode;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.hardware.usb.UsbDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acs.smartcard.Reader;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.ItemListActivity.ReadCardMessageCallBack;
import com.common.powertech.bussiness.PULLParse_Shoudianshoufei_Query;
import com.common.powertech.bussiness.Request_Bangka;
import com.common.powertech.bussiness.Request_ShouDianFee_Query;
import com.common.powertech.bussiness.Request_Shoudianshoufei_Query;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.hardwarelayer.ReaderMonitor;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.EncryptionDES;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.ShoufeiQuery_Class;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.telpo.tps550.api.reader.CardReader;
import com.zbar.lib.CaptureActivity;
import com.zxing.Capture;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.view.ViewfinderView;

import android.app.PendingIntent;
import android.hardware.usb.UsbManager;

import com.acs.smartcard.Features;
import com.acs.smartcard.PinProperties;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.acs.smartcard.TlvProperties;

//蓝牙打印机
import com.gprinter.sample.MainActivity;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;
import com.myDialog.CustomDialog.Builder;
import com.myDialog.CustomProgressDialog;
import com.myDialog.notifyShow;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import printUtils.gprinter;


/**
 * 广东天波信息技术股份有限公司 功能：售电详情Activity 作者:ouyangguozhao 日期:2015-11-6
 */
public class ShouDianXiangQingActivity extends Activity {
    private static final String TAG = "ShouDianXiangQingActivity";
    private CustomProgressDialog progressDialog;
    private TextView mUserNameAndNum, mUserAddr, mUserTel, mElecCompany,
            mMeterNo,mSureCashRspMsg;
    private EditText mZidingshoudianjineEt, mPasswordET,mSurecashzhanghuET,mSurecashcelenumET,phonenum,Token,PinCode;
    private ImageView mCloseImageView;
    private static Button m500Btn, m1000Btn, m1500Btn, m2000Btn, m2500Btn,
            mConfirmChargeBtn, bt_querengoudian,bt_surecashqueren, mBangkaBtn, mPrintAgainBtn,mSendSMSBtn;
    private LinearLayout Amtbuttons,tokenPayLin;
    private String userAmount[];
    private String mComplexData = "";
    private ShoufeiQuery_Class mShouDianXiangQingItem;
    private String mAmt = "";
//    private String mResultAmt = "";
    private String mToken = "";
    private int mTokenlen = 0;
    private String mFuwufeijisuanRspCode = "";// 服务费计算响应码
    private String mFuwufeijisuanRspMsg = "";// 服务费计算响应信息
    private String mFuwufeijisuanRspFee = "";// 服务费
    private String mZhiFuRspJine="";        //支付总金额
    private String mFuwufeijisuanRspPrdordno = "";// 订单号
    private String mShoudianZongeRsp = "";//售电成功总额
    private String mShoudianjineRsp = "";//售电成功金额
    private String mShoudianFeeRsp = "";//售电成功服务费
    private String mTokenAmt = "";//非限制购买Token金额

    private String mTicket = "";// 打印信息
    private HashMap<String, String> billBuyResult = null;// 售电收费返回结构体
    private Printer mPrinter = new Printer();
    private gprinter gprinter = new gprinter();
    private AlertDialog mQuerengoudianDialog,mQuerengoudiansurecashDialog,mGoudianchenggongDialog,mShowtokenDialog,
            mBangkaDialog;
    private String mRspCode = "";
    private String mRspMeg = "";
    private String mRspTicketXML = "";
    private int mInputPassWordTimes = 3;

    private String mPrdordno = "";// 交易单号
    private String mIcJsonRes = "";// 写卡信息
    private String mRmg = "";
    private String mOffset = "";
    private String mValue = "";
    private View mPasswordView;
    private ReadCardNoCallBack mReadCardNoCallBack = null;

    private Capture capture;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private String mBandCardCardType = "1";// 绑卡类型，默认IC卡
    private boolean isCloseScaner = false;
    private String TracData = "";
    private boolean isConfirmChargeCommit=false;
    private Button btn_mag;
    private Button btn_img;
    private String payWays="";//支付方式，默认现金
    private String parampay=""; //支付方式参数
	private String FirstAmt = "";
	private String PayMethod = "";
	private String prdordnocon="";
    private Spinner spinner;
    private String ResourceType=""; //资源类型
    private String EnelName=""; //资源类型
    private String EnelId=""; //资源类型
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    
    private String payAccount="";//支付账号
    private String teleNO="";//手机号码
    private String MyCode;//自定义返回码 标识检查接口成功
    private Activity mActivity = this;
    private String tokenCode ;
    private String pinCode ;

    
    
    		
    
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
	private UsbManager mManager;
	private Reader mReader;
    private PendingIntent mPermissionIntent;
    private Features mFeatures = new Features();
    private String mOffset1 = "";
    private boolean isOpened = true;
    private boolean ISDEBUG=false;
    private boolean isContinue=false;
    private boolean isVerifyCard=false;
    boolean isPass =false;
    boolean writeSucc=false;
    String backupData="";
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;
    private boolean rewrite=false;//重新写卡
    private boolean rewriteagain=false;//重新写卡后重新写卡
    private long currenttime;
    private String ICRspCode = "";//表号查询返回 ic卡是否可以写卡 （卡内token处理）
    private String ICRspMeg = "";
    
    private String TOKENPAY = "";
    
	private class PowerParams {

		public int slotNum;
		public int action;
	}

	private class PowerResult {

		public byte[] atr;
		public Exception e;
	}

	private class SetProtocolParams {

		public int slotNum;
		public int preferredProtocols;
	}

	private class SetProtocolResult {

		public int activeProtocol;
		public Exception e;
	}

	private class TransmitParams {

		public int slotNum;
		public int controlCode;
		public String commandString;
	}
	private class verifyCardParams {

		public int slotNum;
		public int controlCode;
		public String commandString;
		public String verifyPara;
	}
	
	

	private class TransmitProgress {

		public int controlCode;
		public byte[] command;
		public int commandLength;
		public byte[] response;
		public int responseLength;
		public Exception e;
	}

    private static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GlobalParams.If_CloseFlashLight = true;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (GlobalParams.Theme == 1) {
            setTheme(R.style.VioletTheme);
        } else if (GlobalParams.Theme == 1) {
            setTheme(R.style.OrangeTheme);
        }
        setContentView(R.layout.activity_shoudianxiangqing);
        mComplexData = getIntent().getExtras().getString("ShouDianQuery_Class");
        ICRspCode = getIntent().getExtras().getString("ICRspCode");
        ICRspMeg = getIntent().getExtras().getString("ICRspMeg");
        Log.e(TAG, ICRspCode);
        Log.e(TAG, ICRspMeg);
        mShouDianXiangQingItem = (ShoufeiQuery_Class) StringUtil
                .string2ComplexData(mComplexData);
        ResourceType=getIntent().getStringExtra("ResourceType");//区分短信还是流量
        EnelName=getIntent().getStringExtra("EnelName");
        EnelId=getIntent().getStringExtra("EnelId");
		FirstAmt = getIntent().getStringExtra("FirstAmt");
		PayMethod = getIntent().getStringExtra("PayMethod");
		prdordnocon = getIntent().getStringExtra("prdordno");
		 initUI();
        parampay =mShouDianXiangQingItem.getPAY_WEYS(); 
        TOKENPAY = mShouDianXiangQingItem.getTOKEN_WAY();
        if (parampay == null || parampay=="" ) {
      	   
            TextView  tv=(TextView)findViewById(R.id.spinner_paywayss);
      	  tv.setVisibility(View.GONE);
      	  Spinner  tvv=(Spinner)findViewById(R.id.spinner_payways);
      	  tvv.setVisibility(View.GONE); 
    
          }else {
          	 spinner = (Spinner) findViewById(R.id.spinner_payways);
               //数据
               List<String> data_list = new ArrayList<String>();
       		String[] strarray=mShouDianXiangQingItem.getPAY_WEYS().split(";"); 
       	      for (int i = 0; i < strarray.length; i++) 
       	      {
       	    	  data_list.add(strarray[i]);
       	      }
       	     
               //适配器
               arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
               //设置样式
               arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               //加载适配器
               spinner.setAdapter(arr_adapter);   
               
               spinner.setOnItemSelectedListener(new OnItemSelectedListener() {        	
       			@Override
       			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {				
       				payWays =spinner.getItemAtPosition(position).toString();
                            TextView tv = (TextView)view;  
                            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550") ) {
                          	  tv.setTextSize(25.0f);    //设置大小  	
                            }else{
                          	  tv.setTextSize(18.0f); 
                            }
                            TokenPayView();
       				}
       			@Override
       			public void onNothingSelected(AdapterView<?> parent) {
       				
       			} 
               	});    	
               
             //继续支付功能下设置spinner默认值
       		if(PayMethod!=null&&PayMethod.length()>0){
       			for(int i=0;i<strarray.length;i++){
       				if(strarray[i].equals(PayMethod)){
       					spinner.setSelection(i,true);
       					break;
       				}
       			}
       		}
       		TokenPayView();
       		
       		
          }
      
       
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
        //继续支付功能设置默认金额，并下单
        if(FirstAmt!=null&&!FirstAmt.equals("")){
			mZidingshoudianjineEt.setText(FirstAmt);
			
//			spinner.setSelection(Integer.parseInt(PayMethod)-1);
			mConfirmChargeBtn.performClick();
		}
//        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        
    }
    
    private void TokenPayView(){
    	if(spinner.getSelectedItem().toString().equals(TOKENPAY)  && !TOKENPAY.equals("")){
   			Amtbuttons.setVisibility(View.GONE);
   			mZidingshoudianjineEt.setVisibility(View.GONE);
   			tokenPayLin.setVisibility(View.VISIBLE);
   			
   		}else{
   			Amtbuttons.setVisibility(View.VISIBLE);
   			mZidingshoudianjineEt.setVisibility(View.VISIBLE);
   			tokenPayLin.setVisibility(View.GONE);
   		}
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
        }
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }

                    Toast.makeText(getApplicationContext(), "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                	int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status != GpCom.STATE_NO_ERR) {
                        Toast.makeText(ShouDianXiangQingActivity.this, "printer  not connect", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status != GpCom.STATE_NO_ERR) {
                        Toast.makeText(ShouDianXiangQingActivity.this, "printer not connect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    public void printReceiptClicked() {
        try {
            int type = gprinter.nGpService.getPrinterCommandType(mPrinterIndex);
            if (type == GpCom.ESC_COMMAND) {
            	gprinter.nGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_RECEIPT);
            } else {
//                Toast.makeText(this, "Printer is not receipt mode", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }
    
    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550") && isCloseScaner) {
            capture = new Capture(ShouDianXiangQingActivity.this, surfaceView,
                    viewfinderView);
            capture.Scan(handler, true);
            Handler mhdl = capture.getHandler();
            if (mhdl != null) {
                ((CaptureActivityHandler) mhdl).restartPreviewAndDecode();
                isCloseScaner = false;
            }
        }

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ReaderMonitor.ACTION_ICC_PRESENT);
//        filter.addAction(ReaderMonitor.ACTION_MSC);
//        registerReceiver(mCardMessageReceiver, filter);
        super.onResume();
    }

    private void initUI() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        CameraManager.init(ShouDianXiangQingActivity.this);
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
            capture = new Capture(ShouDianXiangQingActivity.this, surfaceView,
                    viewfinderView);
        }
        mUserNameAndNum = (TextView) findViewById(R.id.shoudianxiangqing_nameandnumber);
        mUserNameAndNum.setText("Elec Bill of "
                + mShouDianXiangQingItem.getUSER_NAME() + "/"
                + mShouDianXiangQingItem.getUSER_NO());

        mMeterNo = (TextView) findViewById(R.id.shoudianxiangqing_dianbiaobianhao);
        mMeterNo.setText(mShouDianXiangQingItem.getMETER_NO());

        mUserAddr = (TextView) findViewById(R.id.shoudianxiangqing_address);
        mUserAddr.setText(mShouDianXiangQingItem.getUSER_ADDR());

        mUserTel = (TextView) findViewById(R.id.shoudianxiangqing_lianxidianhua);
        mUserTel.setText(mShouDianXiangQingItem.getTEL());

        mElecCompany = (TextView) findViewById(R.id.shoudianxiangqing_elecdComplany);
        mElecCompany.setText(mShouDianXiangQingItem.getENEL_NAME());

        mZidingshoudianjineEt = (EditText) findViewById(R.id.et_zdsdje);
//        mZidingshoudianjineEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mZidingshoudianjineEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        InputFilter[] filters = {new CashierInpustFilter()};
        mZidingshoudianjineEt.setFilters(filters);
        mZidingshoudianjineEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
				m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
            	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
            	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
            	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
			}
        	
        });
        Token = (EditText)findViewById(R.id.TokenCode);
        Amtbuttons = (LinearLayout)findViewById(R.id.Amtbuttons);
        tokenPayLin = (LinearLayout)findViewById(R.id.tokenPayLin);
        m500Btn = (Button) findViewById(R.id.btn500);
        m1000Btn = (Button) findViewById(R.id.btn1000);
        m1500Btn = (Button) findViewById(R.id.btn1500);
        m2000Btn = (Button) findViewById(R.id.btn2000);
        m2500Btn = (Button) findViewById(R.id.btn2500);
        userAmount = mShouDianXiangQingItem.getUSER_AMOUNT().split("\\|");
        m500Btn.setText(userAmount[0]);
        m1000Btn.setText(userAmount[1]);
        m1500Btn.setText(userAmount[2]);
        m2000Btn.setText(userAmount[3]);
        m2500Btn.setText(userAmount[4]);

        mConfirmChargeBtn = (Button) findViewById(R.id.btn_qrsf);
        if(parampay!=null){ 
      	  m500Btn.setOnClickListener(mOnClickListeners);
            m1000Btn.setOnClickListener(mOnClickListeners);
            m1500Btn.setOnClickListener(mOnClickListeners);
            m2000Btn.setOnClickListener(mOnClickListeners);
            m2500Btn.setOnClickListener(mOnClickListeners);
      }else{
      	 m500Btn.setOnClickListener(mOnClickListener);
           m1000Btn.setOnClickListener(mOnClickListener);
           m1500Btn.setOnClickListener(mOnClickListener);
           m2000Btn.setOnClickListener(mOnClickListener);
           m2500Btn.setOnClickListener(mOnClickListener);	
      }
        mConfirmChargeBtn.setOnClickListener(mOnClickListener);

        mCloseImageView = (ImageView) findViewById(R.id.btnCloseActivity);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        
        if(ICRspCode.equals("11111") || ICRspCode.equals("11112")){
        	final Builder customDialog = new CustomDialog.Builder(ShouDianXiangQingActivity.this);
        	customDialog.setTitle(R.string.shoudianxiangqing_titletip);
        	customDialog.setMessage(ICRspMeg);
        	customDialog.setPositiveButton(R.string.shoudianxiangqing_sure, new DialogInterface.OnClickListener(){
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				dialog.dismiss();
//    				finish();
    			}
    		});
        	setButtonUnable();
        	CustomDialog dialog = customDialog.create();
			dialog.show();
        }
    }

    public void showQuerengoudianDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(
                R.layout.dialog_shoudianxiangqing_querengoudian, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
        builder.setView(view);
        if (mQuerengoudianDialog != null && mQuerengoudianDialog.isShowing()) {
            return;
        }
        mQuerengoudianDialog = builder.create();
        mQuerengoudianDialog.setCancelable(false);
        TextView mZhifuzongeTV = (TextView) view
                .findViewById(R.id.zhifujine_Tv);
        /*
		 * TextView mGoudianliangTV = (TextView) view
		 * .findViewById(R.id.goudianliang_Tv);
		 */
        TextView tokenamt_label = (TextView) view.findViewById(R.id.tokenamt_label);
        TextView dialogtitle = (TextView) view.findViewById(R.id.dialogtitle);
        dialogtitle.setText(R.string.shoufeixiangqing_tv_querengoudian);
        TextView tokenamt_Tv = (TextView) view.findViewById(R.id.tokenamt_Tv);
        TextView mGoudianjineTV = (TextView) view
                .findViewById(R.id.goudianjine_Tv);
        TextView mShouxufeiTV = (TextView) view.findViewById(R.id.shouxufei_Tv);
        LinearLayout pin_layout = (LinearLayout) view.findViewById(R.id.pin_layout);
        PinCode = (EditText) view.findViewById(R.id.PinCode);

        mShouxufeiTV.setText(keepDecimalPlaces(mFuwufeijisuanRspFee));
       
        BigDecimal b1=new BigDecimal(mZhiFuRspJine);
        BigDecimal b2=new BigDecimal(mFuwufeijisuanRspFee
                .equalsIgnoreCase("") ? "0"
                : mFuwufeijisuanRspFee);
        if(spinner.getSelectedItem().toString().equals(TOKENPAY) && !TOKENPAY.equals("")) {//token支付
            tokenamt_Tv.setText(mTokenAmt);

            tokenamt_label.setVisibility(View.VISIBLE);
            tokenamt_Tv.setVisibility(View.VISIBLE);
            pin_layout.setVisibility(View.VISIBLE);
            PinCode.setVisibility(View.VISIBLE);
        }else{
            tokenamt_label.setVisibility(View.GONE);
            tokenamt_Tv.setVisibility(View.GONE);
            pin_layout.setVisibility(View.GONE);
            PinCode.setVisibility(View.GONE);
        }

        mGoudianjineTV
                .setText(keepDecimalPlaces(b1.subtract(b2).toString()));
        mZhifuzongeTV.setText(keepDecimalPlaces(String.valueOf(mZhiFuRspJine)));//
		/* mGoudianliangTV.setText(billBuyResult.get("ENERGY_NUM") + "KW.H"); */

        ImageView mCloseImageView = (ImageView) view
                .findViewById(R.id.btnCloseDialog);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mQuerengoudianDialog != null
                        && mQuerengoudianDialog.isShowing()) {
                    mQuerengoudianDialog.dismiss();
                    prdordnocon=null;//清除继续购电功能的订单号
                }
            }
        });

        mPasswordET = (EditText) view.findViewById(R.id.password_ET);
        // mPasswordET.setText("xls888888");
        mPasswordET.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (mQuerengoudianDialog != null
                                && mQuerengoudianDialog.isShowing()) {
                            if (bt_querengoudian != null
                                    && bt_querengoudian.isEnabled()) {
                                bt_querengoudian.performClick();
                            }
                        }
                    }
                }
                return false;
            }
        });


        mPasswordView = (View) view.findViewById(R.id.pw_layout);
        bt_querengoudian = (Button) view.findViewById(R.id.btn_qrgd);
        if (GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1")) {// 不需要支付密码
            mPasswordView.setVisibility(View.GONE);
//            bt_querengoudian.setFocusableInTouchMode(true);
//            bt_querengoudian.setFocusable(true);
//            bt_querengoudian.requestFocus();
        } else {
            mPasswordET.requestFocus();
        }
        bt_querengoudian
                .setOnClickListener(new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        bt_querengoudian.setEnabled(false);
                        if (!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1")) {
                            boolean isLocked = false;
                            List<String> currentLockTime = new ArrayList<String>();
                            HashMap<String, List<String>> mLockMap = (HashMap<String, List<String>>) Preferences
                                    .getComplexDataInPreference(
                                            ShouDianXiangQingActivity.this,
                                            Preferences.KEY_LOCKMAP, null);
                            if (mLockMap != null) {
                                currentLockTime = mLockMap
                                        .get(GlobalParams.LOGIN_USER_ID);
                                if (currentLockTime == null) {
                                    currentLockTime = new ArrayList<String>();
                                }
                                if (currentLockTime.size() == 0) {
                                    isLocked = false;
                                } else if (currentLockTime.size() >= 3) {
                                    long lockHour = SystemUtil.dateDiff(
                                            currentLockTime.get(0),
                                            SystemUtil.getCurrentDateTimeHH24());
                                    if (lockHour >= 2) {
                                        // 解锁
                                        currentLockTime.clear();
                                        mLockMap.put(
                                                GlobalParams.LOGIN_USER_ID,
                                                currentLockTime);
                                        Preferences
                                                .storeComplexDataInPreference(
                                                        ShouDianXiangQingActivity.this,
                                                        Preferences.KEY_LOCKMAP,
                                                        mLockMap);
                                        isLocked = false;
                                    } else {
                                        isLocked = true;
                                    }
                                }
                                if (isLocked) {
                                    SystemUtil.displayToast(
                                            ShouDianXiangQingActivity.this,
                                            R.string.warm_password_lockWarm);
                                    bt_querengoudian.setEnabled(true);
                                    mPasswordET.setSelection(mPasswordET.getText().length());
                                    return;
                                }
                            }
                            if (mLockMap == null) {
                                mLockMap = new HashMap<String, List<String>>();
                            }
                            String desPW = "";
                            try {
                                if (mPasswordET.getText().toString().length() == 0) {
                                    SystemUtil.displayToast(
                                            ShouDianXiangQingActivity.this,
                                            R.string.login_password_not_null);
                                    bt_querengoudian.setEnabled(true);
                                    mPasswordET.setSelection(mPasswordET.getText().length());
                                    return;
                                }
                                desPW = EncryptionDES.DESSTR(mPasswordET
                                        .getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                bt_querengoudian.setEnabled(true);
                                mPasswordET.setSelection(mPasswordET.getText().length());
                                return;
                            }
                            if (GlobalParams.PAY_PWD.equalsIgnoreCase(desPW)) {
                                currentLockTime.clear();
                                mLockMap.put(GlobalParams.LOGIN_USER_ID,
                                        currentLockTime);
                                Preferences.storeComplexDataInPreference(
                                        ShouDianXiangQingActivity.this,
                                        Preferences.KEY_LOCKMAP, mLockMap);
                                billBuyQuery();
                            } else {
                                currentLockTime.add(SystemUtil
                                        .getCurrentDateTimeHH24());
                                mLockMap.put(GlobalParams.LOGIN_USER_ID,
                                        currentLockTime);
                                Preferences.storeComplexDataInPreference(
                                        ShouDianXiangQingActivity.this,
                                        Preferences.KEY_LOCKMAP, mLockMap);
                                int lessTimes = 3 - currentLockTime.size();
                                if (lessTimes == 0) {
                                    SystemUtil.displayToast(
                                            ShouDianXiangQingActivity.this,
                                            R.string.warm_password_lockWarm);
                                } else {
                                    SystemUtil
                                            .displayToast(
                                                    ShouDianXiangQingActivity.this,
                                                    getString(R.string.warm_password_inputPer)
                                                            + lessTimes
                                                            + getString(R.string.warm_password_inputLeft)
                                            );
                                }
                                bt_querengoudian.setEnabled(true);
                                mPasswordET.setSelection(mPasswordET.getText().length());
                            }
                        } else {
                            billBuyQuery();
                        }
                    }
                });
        mQuerengoudianDialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1")) {// 需要支付密码
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mPasswordET, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        mQuerengoudianDialog.show();
    }
    
    
    public void showQuerengoudiansurecashDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_shoudianxiangqing_querengoudian_surecash, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
        builder.setView(view);
        if (mQuerengoudiansurecashDialog != null && mQuerengoudiansurecashDialog.isShowing()) {
            return;
        }
        mQuerengoudiansurecashDialog = builder.create();
        mQuerengoudiansurecashDialog.setCancelable(false);       
        mSureCashRspMsg = (TextView) view.findViewById(R.id.SureCashRspMsg);//显示surecash返回的明确错误
        mSurecashzhanghuET=(EditText) view.findViewById(R.id.surecash_Pay);//surecash账号
        mSurecashcelenumET=(EditText) view.findViewById(R.id.surecash_tele);//手机号码
         
        ImageView mCloseImageView = (ImageView) view.findViewById(R.id.btnClosesurecashDialog);//关闭按钮
        mCloseImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) { 
                if (mQuerengoudiansurecashDialog != null
                        && mQuerengoudiansurecashDialog.isShowing()) {
                	mQuerengoudiansurecashDialog.dismiss();
                	
                	bt_querengoudian.setClickable(true); //返回按钮可继续发起购电请求 
               	    bt_querengoudian.setEnabled(true);//返回按钮可继续发起购电请求 
                	
                	
                }
            }
        });
       
       //验证账号、手机号是否存在
        if(!payAccount.equals("") && !teleNO.equals("")){
     	   mSurecashzhanghuET.setText(payAccount);
     	   mSurecashcelenumET.setText(teleNO);
     	   mSureCashRspMsg.setVisibility(View.VISIBLE);//view处于可见状态
     	   
     	   if(mRspCode.equals("00156")){//前台
     		   mSureCashRspMsg.setText("");
     		   mSureCashRspMsg.setVisibility(View.GONE);//view处于不可见状态
     	     }
     	      else{
		     	if(!mRspMeg.equals("")){
		     	   mSureCashRspMsg.setText(mRspMeg);  
		        }else{
		     	   mSureCashRspMsg.setText(R.string.SureCash_request_fail); //返回信息为空,默认显示提示信息
		        }
     	     }
     	   //光标重新定位
     	//   mSurecashcelenumET.setSelection(mSurecashcelenumET.getText().length());   
        }
        
  
        
        mSurecashzhanghuET.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (mQuerengoudiansurecashDialog != null
                                && mQuerengoudiansurecashDialog.isShowing()) {
                            if (bt_surecashqueren != null
                                    && bt_surecashqueren.isEnabled()) {
                            	bt_surecashqueren.performClick();
                            }
                        }
                    }
                }
                return false;
            }
        });
        
        
        mSurecashcelenumET.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (mQuerengoudiansurecashDialog != null
                                && mQuerengoudiansurecashDialog.isShowing()) {
                            if (bt_surecashqueren != null
                                    && bt_surecashqueren.isEnabled()) {
                            	bt_surecashqueren.performClick();
                            }
                        }
                    }
                }
                return false;
            }
        });
 
        bt_surecashqueren = (Button) view.findViewById(R.id.btn_surecashqueding);
      
       
        // if (TextUtils.isEmpty(mobiles)) return false;  
       // else return mobiles.matches(telRegex); 
 
        bt_surecashqueren
                .setOnClickListener(new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	bt_surecashqueren.setEnabled(false);
                    	 Pattern pattern = Pattern.compile("01\\d{9}");//手机号码01开头，后面跟任意9位
                         Matcher matcher = pattern.matcher(mSurecashcelenumET.getText().toString());
                            
                         try {
                                if (mSurecashzhanghuET.getText().toString().length() == 0) {
			                        SystemUtil.displayToast( ShouDianXiangQingActivity.this,R.string.surecash_account_not_null);
			                        bt_surecashqueren.setEnabled(true);
			                        mSurecashzhanghuET.setSelection(mSurecashzhanghuET.getText().length());//定位用的，设置到光标的最尾端
                                    return;
                                }
                             
                                else if (mSurecashzhanghuET.getText().toString().length()!=12) {//账号长度为12为纯数字组成
                                    SystemUtil.displayToast( ShouDianXiangQingActivity.this,R.string.surecash_account_jiaoyan);
                                    bt_surecashqueren.setEnabled(true);
                                    mSurecashzhanghuET.setSelection(mSurecashzhanghuET.getText().length());//定位用的，设置到光标的最尾端
                                    return;
                                }
                                
                                if (mSurecashcelenumET.getText().toString().length() == 0) {
                        
                                	SystemUtil.displayToast( ShouDianXiangQingActivity.this,R.string.surecash_telenum_not_null);
                                    bt_surecashqueren.setEnabled(true);
                                    mSurecashcelenumET.setSelection(mSurecashcelenumET.getText().length());//定位用的，设置到光标的最尾端
                                    return;
                                }
                                else if(!matcher.matches()){
                                	
                                	SystemUtil.displayToast( ShouDianXiangQingActivity.this,R.string.surecash_telenum_jiaoyan);
                                    bt_surecashqueren.setEnabled(true);
                                    mSurecashcelenumET.setSelection(mSurecashcelenumET.getText().length());//定位用的，设置到光标的最尾端
                                    return;
                                }
                                payAccount = mSurecashzhanghuET.getText().toString();//账号信息
                                teleNO = mSurecashcelenumET.getText().toString();//手机号码
                                mQuerengoudiansurecashDialog.dismiss();
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                                bt_surecashqueren.setEnabled(true);
                                mSurecashzhanghuET.setSelection(mSurecashzhanghuET.getText().length());
                                mSurecashcelenumET.setSelection(mSurecashcelenumET.getText().length());
                                return;
                            }
                         billBuyQuery();//购电
                    }
                });
       mQuerengoudiansurecashDialog.show();
      
            //  bt_querengoudian.setClickable(true); //返回按钮可继续发起购电请求 
   	        //  bt_querengoudian.setEnabled(true);//返回按钮可继续发起购电请求 
    }
 
    public void showShoudianchenggongDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(
                R.layout.dialog_shoudianxiangqing_goudianchenggong, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        mGoudianchenggongDialog = builder.create();
        mGoudianchenggongDialog.setCancelable(false);
        TextView mZhifuzongeTV = (TextView) view
                .findViewById(R.id.zhifujine_Tv);
        TextView mGoudianliangTV = (TextView) view
                .findViewById(R.id.goudianliang_Tv);
        TextView mGoudianjineTV = (TextView) view
                .findViewById(R.id.goudianjine_Tv);
        TextView mShouxufeiTV = (TextView) view.findViewById(R.id.shouxufei_Tv);
        TextView mTokenTV = (TextView) view.findViewById(R.id.token_TV);

        ImageView mCloseImageView = (ImageView) view
                .findViewById(R.id.btnGdcgCloseDialog);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mGoudianchenggongDialog != null) {
                    mGoudianchenggongDialog.dismiss();
                }
                ShouDianXiangQingActivity.this.finish();
            }
        });

        mShouxufeiTV.setText(mShoudianFeeRsp);

        mGoudianjineTV
                .setText(mShoudianjineRsp);
        mZhifuzongeTV.setText(mShoudianZongeRsp);
        if (billBuyResult.size() != 0) {
            mGoudianliangTV.setText(billBuyResult.get("ENERGY_NUM") + "KW.H");
//            String tokenStr = billBuyResult.get("TOKEN").replace("|", "\n");
           /* int tokennum = tokenStr.length();
            mTokenTV.setText(tokenStr);
            if(tokennum>=75){
            	mTokenTV.setText(tokenStr.substring(0, 75).concat("..."));
            	mTokenTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) { // 显示token
                    	showMoretokenDialog(ShouDianXiangQingActivity.this);
                    }
                });
            }*/
            String [] tokenArr = billBuyResult.get("TOKEN").split("\\|");
            StringBuilder tokenStr=new StringBuilder();
            String tokenStr2="";
            String regex = "(.{4})";
            mTokenlen = tokenArr.length;
            for(int i=0;i<mTokenlen;i++){
            	tokenStr.append(tokenArr[i].replaceAll(regex, "$1 ")).append("\n");
            	
            }
            mToken = tokenStr.delete(tokenStr.length()-2,tokenStr.length()).toString();
            if(tokenStr.length()>=80){
            	tokenStr2=tokenStr.substring(0, 77).concat("...");
            }
            mTokenTV.setText(mTokenlen>3?tokenStr2:mToken);//只显示前3串token，
        }
        
        mPrintAgainBtn = (Button) view.findViewById(R.id.btn_print_again);
        mPrintAgainBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) { // 再次打印
                mPrintAgainBtn.setEnabled(false);
                new PrintTask()
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        
        mSendSMSBtn = (Button) view.findViewById(R.id.btn_send_sms);
        mSendSMSBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) { // 发送短信
    			// TODO Auto-generated method stub
            	final Builder customDialog = new CustomDialog.Builder(ShouDianXiangQingActivity.this);
            	customDialog.setTitle(R.string.shoudianxiangqing_sendsms_title);
            	customDialog.setPositiveButton(R.string.network_setting_server_address_button_ensure, new DialogInterface.OnClickListener() {
						/* (non-Javadoc)
						 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
						 */
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String phone = customDialog.getEditStr();
							String mach="^[0-9-.]*$";
							if("".equals(phone) || !phone.matches(mach)){
								SystemUtil.displayToast(ShouDianXiangQingActivity.this, R.string.shoufeidetail_phone_error);
								return;
							}
							
							String  meter = getString(R.string.str_biaohao) + mShouDianXiangQingItem.getMETER_NO()+"\n";
							String totalamt = getString(R.string.str_jiaofeijine) +  mShoudianZongeRsp+"\n";
							String poweramt = getString(R.string.shoufeixiangqing_tv_goudianliang) +  billBuyResult.get("ENERGY_NUM") + "KW.H"+"\n";
			    			String tokenStr = getString(R.string.str_token) + billBuyResult.get("TOKEN").replace("|", "\n");
			    			String message = meter+totalamt+poweramt+tokenStr;
			    			 PendingIntent pi = PendingIntent.getActivity(ShouDianXiangQingActivity.this, 0, new Intent(ShouDianXiangQingActivity.this, ShouDianXiangQingActivity.class), 0);
			    			  SmsManager sms = SmsManager.getDefault();
			    			 sms.sendTextMessage(phone, null, message, pi, null);
			    			 dialog.dismiss();
						}
					});
            	customDialog.setNegativeButton(R.string.network_setting_server_address_button_cancel, new DialogInterface.OnClickListener(){
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				// TODO Auto-generated method stub
        				dialog.dismiss();
        			}
        		});
            	CustomDialog dialog = customDialog.create();
            	EditText phonenum = customDialog.getEditView();
				phonenum.setInputType(InputType.TYPE_CLASS_PHONE);
//				String digits = "0123456789-";
//				phonenum.setKeyListener(DigitsKeyListener.getInstance(digits));   
				phonenum.setHint(R.string.shoudianxiangqing_sendsms_num);
				dialog.show();
            	
            	
            	/*phonenum = new EditText(ShouDianXiangQingActivity.this);
            	new AlertDialog.Builder(ShouDianXiangQingActivity.this).setTitle(R.string.shoudianxiangqing_sendsms_title)
            		.setIcon(android.R.drawable.ic_dialog_info)
            		.setView(phonenum)
            		.setPositiveButton(R.string.network_setting_server_address_button_ensure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String phone = phonenum.getText().toString();
							String  meter = getString(R.string.str_biaohao) + mShouDianXiangQingItem.getMETER_NO()+"\n";
							String totalamt = getString(R.string.str_jiaofeijine) +  mShoudianZongeRsp+"\n";
							String poweramt = getString(R.string.shoufeixiangqing_tv_goudianliang) +  billBuyResult.get("ENERGY_NUM") + "KW.H"+"\n";
			    			String tokenStr = getString(R.string.str_token) + billBuyResult.get("TOKEN").replace("|", "\n");
			    			String message = meter+totalamt+poweramt+tokenStr;
			    			 PendingIntent pi = PendingIntent.getActivity(ShouDianXiangQingActivity.this, 0, new Intent(ShouDianXiangQingActivity.this, ShouDianXiangQingActivity.class), 0);
			    			  SmsManager sms = SmsManager.getDefault();
			    			 sms.sendTextMessage(phone, null, message, pi, null);
						}
					})
            		.setNegativeButton(R.string.network_setting_server_address_button_cancel, new DialogInterface.OnClickListener(){
            			@Override
            			public void onClick(DialogInterface dialog, int which) {
            				// TODO Auto-generated method stub
            				dialog.dismiss();
            			}
            		}).show();*/
            		
            }
        });
        if(mTokenlen>3){
            mTokenTV.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View arg0) {
            		
    				showMoretokenDialog(ShouDianXiangQingActivity.this);
            	}
            });
            }
        mBangkaBtn = (Button) view.findViewById(R.id.btn_bangka);
        mBangkaBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) { // 绑卡
                mBangkaBtn.setEnabled(false);
                showQuerenbangkaDialog(ShouDianXiangQingActivity.this);
            }
        });

        if (mGoudianchenggongDialog != null
                && !mGoudianchenggongDialog.isShowing()) {
            mGoudianchenggongDialog.show();
        }

    }
    
    public void showMoretokenDialog(Context mContext) {

    	// TODO Auto-generated method stub
    			LayoutInflater inflater = LayoutInflater.from(mContext);
    	        View view = inflater.inflate(
    	                R.layout.dialog_shoudianxiangqing_moretoken, null);
    	        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    	        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
    	        builder.setView(view);
    	        mShowtokenDialog = builder.create();
    	        mShowtokenDialog.setCancelable(true);
    	        TextView tokens = (TextView) view.findViewById(R.id.more_token);
    	        tokens.setText(mToken);
    	       
    	    mShowtokenDialog.setOnDismissListener(new OnDismissListener() {

    			@Override
    			public void onDismiss(DialogInterface dialog) {

    		        }
    	    	
    	    });
    	    mShowtokenDialog.show();

    }

    public void showQuerenbangkaDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_querenbangka, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
        builder.setView(view);

        mBangkaDialog = builder.create();
        mBangkaDialog.setCancelable(false);
        TextView mHuhaokahaoTV = (TextView) view
                .findViewById(R.id.huhaobiaohao_TV);
        TextView mHumingTV = (TextView) view.findViewById(R.id.huming_TV);
        // TextView mKahaoTV = (TextView) view.findViewById(R.id.kahao_TV);

        mHuhaokahaoTV.setText(mShouDianXiangQingItem.getMETER_NO());
        mHumingTV.setText(mShouDianXiangQingItem.getUSER_NAME());
        // mKahaoTV.setText(getString(R.string.shoudianxiangqing_kahao));

        ImageView mCloseImageView = (ImageView) view
                .findViewById(R.id.btnCloseDialog);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mBangkaDialog != null) {
                    mBangkaDialog.dismiss();
                }
            }
        });


        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
           btn_mag = (Button) view.findViewById(R.id.btn_d_qrbk_mag);
           btn_img = (Button) view.findViewById(R.id.btn_d_qrbk_img);
           btn_mag.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	btn_mag.setEnabled(false);
                    // 绑卡操作
                    TracData = "";
                    createDialog();
                    progressDialog
                            .setTitle(getString(R.string.shoufeixiangqing_btn_bangka));
                    progressDialog.setMessage(getString(R.string.warm_bandcard));
                    // 设置进度条是否不明确
//                    progressDialog.setIndeterminate(false);
                    // 是否可以按下退回键取消
                    progressDialog.setCancelable(true);
                    progressDialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							if(btn_mag!=null){
				        		btn_mag.setEnabled(true);
				        	}
						}
					});
                    progressDialog.show();

                    setOnReadCardNoCallBack(new ReadCardNoCallBack() {
                        @Override
                        public void readCardMessage(String cardNo) {
                            setOnReadCardNoCallBack(null);
                            new BandCardTask(cardNo)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                }

            });

            btn_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_img.setEnabled(false);
                    //TPS390 开启一二维码扫描功能
                    if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
//                        ReaderMonitor.stopMonitor();
                        if (GlobalParams.LASERH) {
                            //激光扫描二维码
//                            new GetDataAndTypeTask().execute();
                        } else {
                            //Zxing扫描二维码
//                            Intent openCameraIntent = new Intent(ShouDianXiangQingActivity.this,CaptureActivity.class);
//                            startActivityForResult(openCameraIntent, 0);
                        	
                        	Intent intent = new Intent();
                        	intent.setClass(ShouDianXiangQingActivity.this, CaptureActivity.class);
                        	startActivityForResult(intent,0);
                        }
                    }
                }
            });

        }


        mBangkaDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mBangkaBtn != null) {
                    mBangkaBtn.setEnabled(true);
                }
            }
        });
        mBangkaDialog.show();

    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            setButtonUnable();
            switch (arg0.getId()) {
                case R.id.btn_qrsf:
                	isConfirmChargeCommit=true;
                	if(spinner.getSelectedItem().toString().equals(TOKENPAY) && !TOKENPAY.equals("")){//token支付
                		if(Token.getText().toString().length() == 0){
                			SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error_token);
                			return;
                		}
//                		if(PinCode.getText().toString().length() == 0){
//                			SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error_pin);
//                			return;
//                		}
                		tokenCode = Token.getText().toString();
//                		pinCode = PinCode.getText().toString();
                		break;
                	}
                	
                    if (mZidingshoudianjineEt.getText().length() == 0) {
                        SystemUtil.displayToast(getApplicationContext(),
                                R.string.shoudianxiangqing_tv_je);
                        setButtonEnable();
                        return;
                    }

                    String money = mZidingshoudianjineEt.getText().toString();
                    if (money.length() == 0) {
                        SystemUtil.displayToast(getApplicationContext(),
                                R.string.shoudianxiangqing_tv_je);
                        setButtonEnable();
                        return;
                    }

                    // 检测金额格式是否正确，如果以0开头,后面必须加.
                    String moneyStr = money;
//                    Pattern pattern = Pattern
//                            .compile("^(-)?(([1-9]{1}\\d*)|([0]{1}))((\\.(\\d){1,2})?$");
                    Pattern pattern = Pattern.compile("^(-)?[0-9]*.?[0-9]*");  
                    Matcher matcher = pattern.matcher(moneyStr);
                    if (!matcher.matches()) {
                        // 金额格式不正确
                        SystemUtil.displayToast(getApplicationContext(),
                                R.string.shoudianxiangqing_jineshurubuzhengque);
                        setButtonEnable();
                        return;
                    }
                    mAmt = moneyStr;
                    break;
                case R.id.btn500:
                	mZidingshoudianjineEt.setText("");
                	mAmt =userAmount[0];
                	m500Btn.setBackgroundResource(R.color.orange);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn1000:
                	mZidingshoudianjineEt.setText("");
                	mAmt = userAmount[1];
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.color.orange);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn1500:
                	mZidingshoudianjineEt.setText("");
                	mAmt = userAmount[2];
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.color.orange);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn2000:
                	mZidingshoudianjineEt.setText("");
                	mAmt = userAmount[3];
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.color.orange);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn2500:
                	mZidingshoudianjineEt.setText("");
                	mAmt = userAmount[4];
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.color.orange);
                    break;
                default:
                    break;
            }
            new FeeQueryTask(ShouDianXiangQingActivity.this).execute();
        }
    };

    private OnClickListener mOnClickListeners = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            //setButtonUnable();
            switch (arg0.getId()) {
                case R.id.btn500:
                	mAmt =userAmount[0];
                	mZidingshoudianjineEt.clearComposingText();
                	mZidingshoudianjineEt.setText(mAmt);
                	mZidingshoudianjineEt.setSelection(mAmt.length());
                	m500Btn.setBackgroundResource(R.color.orange);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn1000:
                	mAmt = userAmount[1];
                	mZidingshoudianjineEt.clearComposingText();
                	mZidingshoudianjineEt.setText(mAmt);
                	mZidingshoudianjineEt.setSelection(mAmt.length());
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.color.orange);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn1500:
                	mAmt = userAmount[2];
                	mZidingshoudianjineEt.clearComposingText();
                	mZidingshoudianjineEt.setText(mAmt);
                	mZidingshoudianjineEt.setSelection(mAmt.length());
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.color.orange);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn2000:
                	mAmt = userAmount[3];
                	mZidingshoudianjineEt.clearComposingText();
                	mZidingshoudianjineEt.setText(mAmt);
                	mZidingshoudianjineEt.setSelection(mAmt.length());
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.color.orange);
                	m2500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                    break;
                case R.id.btn2500:
                	mAmt = userAmount[4];
                	mZidingshoudianjineEt.clearComposingText();
                	mZidingshoudianjineEt.setText(mAmt);
                	mZidingshoudianjineEt.setSelection(mAmt.length());
                	m500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m1500Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2000Btn.setBackgroundResource(R.drawable.button_number_violet_port_shape);
                	m2500Btn.setBackgroundResource(R.color.orange);
                    break;
               
                default:
                    break;
            }   
        }
    };
    

    private void setButtonUnable() {
        m500Btn.setEnabled(false);
        m1000Btn.setEnabled(false);
        m1500Btn.setEnabled(false);
        m2000Btn.setEnabled(false);
        m2500Btn.setEnabled(false);
        mConfirmChargeBtn.setEnabled(false);
    }

    private void setButtonEnable() {
        m500Btn.setEnabled(true);
        m1000Btn.setEnabled(true);
        m1500Btn.setEnabled(true);
        m2000Btn.setEnabled(true);
        m2500Btn.setEnabled(true);
        mConfirmChargeBtn.setEnabled(true);
    }

    @SuppressLint("LongLogTag")
    private boolean feeQuery() {// 购电服务费查询 成功返回true，失败返回false
        Request_ShouDianFee_Query.setContext(ShouDianXiangQingActivity.this);
        // 售电只有AMT
        if(payWays.equals(TOKENPAY)  && !TOKENPAY.equals("")){
        	Request_ShouDianFee_Query.setAmt("");
            Request_ShouDianFee_Query.setTokenCode(tokenCode);
        }else{
        	 if(mAmt.substring(mAmt.length()-1).equals("."))//如果购买金额末尾为'.',删除掉'.'
             {
           	  mAmt=mAmt.substring(0,mAmt.length()-1);
             }
             Request_ShouDianFee_Query.setAmt(String.valueOf(mAmt));
        }
       
        Request_ShouDianFee_Query.setReceID("");
        Request_ShouDianFee_Query.setIcType(GlobalParams.CARD_TYPE);
        Request_ShouDianFee_Query.setICJsonReq(GlobalParams.IC_JSON_REQ);
        Request_ShouDianFee_Query.setMeterNo(mShouDianXiangQingItem.getMETER_NO());
        Request_ShouDianFee_Query.setPayWays(payWays);// 支付方式
        Request_ShouDianFee_Query.setPrdType(ResourceType);//资源类型
        Request_ShouDianFee_Query.setEnelId(EnelId);// 电力公司
        Request_ShouDianFee_Query.setPrdOrdNo(prdordnocon);
//        Request_ShouDianFee_Query.setPinCode(pinCode);
        
        payAccount="";//重置账户
        teleNO="";//重置手机号
        
        String requestXML = Request_ShouDianFee_Query.getRequsetXML();//请求数据
        // 模拟数据
        // requestXML="<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE></TOP><BODY><AMT>2000</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>ef1fc3307410b1e0f7a15ed0e51f9e17</SIGNATURE></TAIL></ROOT>";
        String reponseXML = "";
        try {
            reponseXML = Client.ConnectServer("PBillFeeCount", requestXML);//响应数据
            // 模拟数据
            // reponseXML =
            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
            System.out.println("服务费计算查询响应：" + reponseXML);
        } catch (Exception ex) {
            System.out.print(ex.toString());
            return false;
        }
        mFuwufeijisuanRspCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
                "</RSPCOD>");
        MyCode = Client.Parse_XML(reponseXML, "<MYCODE>", "</MYCODE>");//自定义check接口
        mFuwufeijisuanRspMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
                "</RSPMSG>");
        mFuwufeijisuanRspFee = Client.Parse_XML(reponseXML, "<FEE>", "</FEE>");
        mZhiFuRspJine = Client.Parse_XML(reponseXML, "<ORDAMT>", "</ORDAMT>");
        mTokenAmt = Client.Parse_XML(reponseXML,"<TOKENAMT>","</TOKENAMT>");
        String payWayTemp=Client.Parse_XML(reponseXML, "<PAYWAYS>", "</PAYWAYS>");//该标签和后台打包标签一致
        mFuwufeijisuanRspPrdordno = Client.Parse_XML(reponseXML, "<PRDORDNO>", "</PRDORDNO>");//订单号
        if(null!=payWayTemp &&!payWayTemp.equals("") ){
     	   payWays=payWayTemp;
        } 
        if (mFuwufeijisuanRspCode.equalsIgnoreCase("00000")) {
            Log.e(TAG, "mFuwufeijisuanRspFee = " + mFuwufeijisuanRspFee);
            return true;
        } 
        
        if(MyCode.equalsIgnoreCase("00605")){
        	 
       	 return true;
       } 
              
       if(mFuwufeijisuanRspCode.equalsIgnoreCase("91001")){
   	  
    	 return true;
       }
        else {
            // 服务器返回系统超时，返回到登录页面
            if (mFuwufeijisuanRspCode.equals("00011")) {
                Toast.makeText(ShouDianXiangQingActivity.this,
                        mFuwufeijisuanRspMsg, Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(ShouDianXiangQingActivity.this);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(ShouDianXiangQingActivity.this, LoginActivity.class);
                ShouDianXiangQingActivity.this.startActivity(intent);
            }
            return false;
        }
    }

    /**
     * @param CardNo   用户绑定卡号
     * @param CardType 用户卡类型
     * @param PrdordNo 交易单号
     * @return
     */
    private boolean bandCard(String CardNo, String CardType, String PrdordNo) {
        mRspCode = "";
        mRspMeg = "";
        Request_Bangka.setContext(ShouDianXiangQingActivity.this);
        Request_Bangka.setCardNo(CardNo);
        Request_Bangka.setCardType(CardType);
        Request_Bangka.setPrdordNo(PrdordNo);
        String requestXML = Request_Bangka.getRequsetXML();
        Log.e(TAG, "requestXML=" + requestXML);
        String reponseXML = "";
        try {
            reponseXML = Client.ConnectServer("PCardBanding", requestXML);
            // 模拟数据
            // reponseXML = "";
            System.out.println("绑卡查询响应：" + reponseXML);
        } catch (Exception ex) {
            System.out.print(ex.toString());
            return false;
        }
        mRspCode = Client.Parse_XML(reponseXML, "<RSPCOD>", "</RSPCOD>");
        mRspMeg = Client.Parse_XML(reponseXML, "<RSPMSG>", "</RSPMSG>");
        if (mRspCode.equalsIgnoreCase("00000")) {
            Log.e(TAG, "绑卡成功！");
            return true;
        } else {
            // 服务器返回系统超时，返回到登录页面
            if (mRspCode.equals("00011")) {
                Toast.makeText(ShouDianXiangQingActivity.this, mRspMeg,
                        Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(ShouDianXiangQingActivity.this);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(ShouDianXiangQingActivity.this, LoginActivity.class);
                ShouDianXiangQingActivity.this.startActivity(intent);
            }
            return false;
        }
    }

    private void billBuyQuery() {
        String mRspFee = mFuwufeijisuanRspFee.equalsIgnoreCase("") ? "0"
                        : mFuwufeijisuanRspFee;
//        mResultAmt = MathUtil.subtract4Long(mAmt,
//                mRspFee);
//        BigDecimal b1=new BigDecimal(mAmt);
//        BigDecimal b2=new BigDecimal(mRspFee);
//        mResultAmt=b1.subtract(b2).toString();
        mIcJsonRes = "";

        mRspMeg = "";
        mRspTicketXML = "";
        mTicket = "";
        Request_Shoudianshoufei_Query
                .setContext(ShouDianXiangQingActivity.this);
        Request_Shoudianshoufei_Query.setMeterNo(mShouDianXiangQingItem
                .getMETER_NO());// 表号

//        String amtParm = String.valueOf(mAmt);
//        int positionLength = amtParm.length() - amtParm.indexOf(".") - 1;
//        // Log.e(TAG,"positionLength = "+positionLength);
//        if (positionLength < 3) {
//            for (int i = 0; i < (3 - positionLength); i++) {
//                amtParm += "0";
//            }
//        }
        String mPin = "";
            if(spinner.getSelectedItem().toString().equals(TOKENPAY) && !TOKENPAY.equals("")) {//token支付
                //获取pin码
                mPin = PinCode.getText().toString();
                if(mPin.equals("")){
                    SystemUtil.displayToast(mActivity,R.string.shoudianxiangqing_pay_pin);
                    bt_querengoudian.setEnabled(true);
                    return;
                }
                Request_Shoudianshoufei_Query.setPINCODE(mPin);
            }

        Request_Shoudianshoufei_Query.setAmt(keepDecimalPlaces(mZhiFuRspJine));// 总金额
        Request_Shoudianshoufei_Query.setPayWays(payWays);// 支付方式
        Request_Shoudianshoufei_Query.setPrdordno(mFuwufeijisuanRspPrdordno);// 订单号
        Request_Shoudianshoufei_Query.setIcType(GlobalParams.CARD_TYPE);// IC类型
        Request_Shoudianshoufei_Query.setICJsonReq(GlobalParams.IC_JSON_REQ);// 读卡信息
        Request_Shoudianshoufei_Query.setPayAccount(payAccount);//支付账号
        Request_Shoudianshoufei_Query.setTeleNO(teleNO);//电话号码
        
        
        String requestXML = Request_Shoudianshoufei_Query.getRequsetXML();
        Log.e(TAG, "requestXML = " + requestXML);
        try {
            Client.SendData("PBillBuy", requestXML, mShouDianHandler);
        } catch (Exception ex) {
            Log.e(TAG, "Exception = " + ex.toString());
        }
        if (progressDialog == null) {
            createDialog();
        }
        progressDialog
                .setTitle(getString(R.string.shoufeixiangqing_tv_querengoudian));
        progressDialog.setMessage(getString(R.string.progress_conducting));
//        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @SuppressLint("NewApi")
    public void loginon() {
        Intent intent = new Intent(ShouDianXiangQingActivity.this,
                ItemListActivity.class);
        startActivity(intent);
    }

    // 服务费查询异步任务
    private class FeeQueryTask extends AsyncTask<Void, Void, Integer> {

        public FeeQueryTask(Context context) {
            createDialog();
            progressDialog.setTitle(getString(R.string.progress_shoufei_title));
            progressDialog.setMessage(getString(R.string.progress_conducting)); // 设置进度条是否不明确
            // 是否可以按下退回键取消 progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e(TAG, "--doinbackground");
            // Disable_Key.EnableTestMode("true",
            // ShouDianXiangQingActivity.this);
            try {
                return feeQuery() ? 1 : 0;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return 0;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            setButtonEnable();
            isConfirmChargeCommit=false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result != 0) {
            	
            	//购电下单（服务费计算）时，调用surecash接口返回成功，标识 00605
	            if( MyCode.equalsIgnoreCase("00605")&&mFuwufeijisuanRspCode.equalsIgnoreCase("00000")){//00000
	                 	AlertDialog.Builder builder = new AlertDialog.Builder(ShouDianXiangQingActivity.this);
	                     builder.setTitle(" ");
	                     builder.setMessage(mFuwufeijisuanRspMsg);
	                     //    设置一个PositiveButton
	                     builder.setPositiveButton(R.string.shoufeixiangqing_btn_surecashcheckqueren, new DialogInterface.OnClickListener()
	                     {
	                         @Override
	                         public void onClick(DialogInterface dialog, int which)
	                         {
	                        	 //billBuyQuery();
	                        	 showQuerengoudianDialog(ShouDianXiangQingActivity.this);
	                         }
	                     }); 
	                     builder.show();
	                 } 
            	
	             //91001未输入PIN
		          else if(mFuwufeijisuanRspCode.equalsIgnoreCase("91001")){
		          	AlertDialog.Builder builder = new AlertDialog.Builder(ShouDianXiangQingActivity.this);
		              builder.setTitle("");
		              builder.setMessage(mFuwufeijisuanRspMsg);
		              //    设置一个PositiveButton
		              builder.setPositiveButton(R.string.shoufeixiangqing_btn_surecashcheckqueren, new DialogInterface.OnClickListener()
		              {
		                  @Override
		                  public void onClick(DialogInterface dialog, int which)
		                  { 
		                	  //未输入PIN码返回91001，不超过10分钟，该订单可再次支付 ，若超过10分钟，订单自动作废
		                	  new FeeQueryTask(ShouDianXiangQingActivity.this).execute();
		                  }
		              }); 
		              
		              builder.show();
		          } 
                 	
		          else{
                showQuerengoudianDialog(ShouDianXiangQingActivity.this);
		          }
		          
		          } else {////pos请求hexpay超时
                if (mFuwufeijisuanRspMsg.equalsIgnoreCase("")) {
                    SystemUtil
                            .displayToast(
                                    ShouDianXiangQingActivity.this,
                                    getString(R.string.shoufeixiangqing_fuwufeijisuanshibai));
                } else {
                    SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                            mFuwufeijisuanRspMsg);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(
                ShouDianXiangQingActivity.this, 35 * 1000,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                                R.string.progress_timeout);
                        setOnReadCardNoCallBack(null);
                        if (mBangkaBtn != null) {
                            mBangkaBtn.setEnabled(true);
                        }
                        if (bt_querengoudian != null) {
                            bt_querengoudian.setEnabled(true);
                        }
                        if (dialog != null
                                && (!ShouDianXiangQingActivity.this
                                .isFinishing())) {
                            if (capture != null) {
                                capture.Stop();
                                capture = null;
                            }
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }

    private class PrintTask extends AsyncTask<Void, Void, String> {
    	HashMap a;
        @Override
        protected void onPreExecute() {
            if (mQuerengoudianDialog != null
                    && mQuerengoudianDialog.isShowing()) {
                mQuerengoudianDialog.dismiss();
                showShoudianchenggongDialog(ShouDianXiangQingActivity.this);
            }
            if (mPrintAgainBtn != null) {
                mPrintAgainBtn.setEnabled(false);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            createDialog();
            progressDialog.setTitle(getString(R.string.str_dayin));
            progressDialog.setMessage(getString(R.string.progress_conducting));
//            progressDialog.setIndeterminate(false);
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
//        	printReceiptClicked();
            mRspTicketXML = mRspTicketXML.replace("&amp;caret;","^");
//            mRspTicketXML = mRspTicketXML.replace("&quot;", "@quot;").replace("&apos;", "@apos;").replace("&lt;", "@lt;").replace("&gt;", "@gt;");
//            mRspTicketXML = mRspTicketXML.replace("&","&amp;");
//            mRspTicketXML = mRspTicketXML.replace("@quot;", "&quot;").replace("@apos;", "&apos;").replace("@lt;", "&lt;").replace("@gt;", "&gt;");
        	gprinter.printXML("<TICKET>" + mRspTicketXML + "</TICKET>");
        	String result = gprinter.commitOperation();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        	if(result==null){
        		result="Printer Success";
        	}
            SystemUtil.displayToast(ShouDianXiangQingActivity.this,result);
            mPasswordView.setVisibility(View.GONE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            // 写卡
            if (GlobalParams.IC_FLAG.equalsIgnoreCase("1")) {// 0:不写卡  1：写卡
        		String deviceName = null;
        		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
        			if (mReader.isSupported(device)) {
        				deviceName = device.getDeviceName();
        				break;
        			}
        		}
        		// open
        		if (deviceName != null) {

        			// For each device
        			for (UsbDevice device : mManager.getDeviceList().values()) {

        				// If device name is found
        				if (deviceName.equals(device.getDeviceName())) {

        					if (mManager.hasPermission(device)) { // ��ȡȨ��
        						
        							OpenTask(device);
        							SystemClock.sleep(200);
        							int temstate=getState();
        							SystemClock.sleep(200);
        							logMsg1("stateopen"+temstate);
        							 if(temstate==1||temstate==0){
        								 logMsg1("请插入IC卡！");
        								 SystemUtil.displayToast(ShouDianXiangQingActivity.this, R.string.writecard_check_card);
        						                if (bt_querengoudian != null) {
        						                    bt_querengoudian.setEnabled(true);
        						                }
        						            if (mPrintAgainBtn != null) {
        						                mPrintAgainBtn.setEnabled(true);
        						            }
//        								 new CloseTask();
        						         mReader.close();
        								 return;
        					            }
//        							 new CloseTask();
        							 mReader.close();
        					}else {
        						// Request permission Ȩ��
        						mManager.requestPermission(device, mPermissionIntent);
        						break;
        					}
        				}
        			}
        		}
                new WriteICCardTask()
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                if (bt_querengoudian != null) {
                    bt_querengoudian.setEnabled(true);
                }
            }
            if (mPrintAgainBtn != null) {
                mPrintAgainBtn.setEnabled(true);
            }
        }
    }
  //判断是否插卡
    public int getState() {

        // Get slot number
        int slotNum = 0;

        // If slot is selected
        if (slotNum != Spinner.INVALID_POSITION) {

            try {

                // Get state
//                logMsg1("Slot " + slotNum + ": Getting state...");
                int state = mReader.getState(slotNum);

                if (state < Reader.CARD_UNKNOWN
                        || state > Reader.CARD_SPECIFIC) {
                    state = Reader.CARD_UNKNOWN;
                }
                return state;
//                logMsg1("State: " + stateStrings[state]);

            } catch (IllegalArgumentException e) {

                logMsg1(e.toString());
            }
        }
        return -1;
    }

//判断IC卡类型
    public String getATR() {

        // Get slot number
        int slotNum = 0;

        // If slot is selected
        if (slotNum != Spinner.INVALID_POSITION) {

            try {

                // Get ATR
                logMsg1("Slot " + slotNum + ": Getting ATR...");
                byte[] atr = mReader.getAtr(slotNum);

                // Show ATR
                if (atr != null) {

                    logMsg1("ATR:");
                    return getStrNew(atr, atr.length);

                } else {

                   return "";
                }

            } catch (IllegalArgumentException e) {

                logMsg1(e.toString());
            }
        }
        return "";
    }

    // 绑卡异步任务
    private class BandCardTask extends AsyncTask<Void, Void, Integer> {
        private String mCardNo = "";

        BandCardTask(String cardNo) {
            this.mCardNo = cardNo;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                Log.e(TAG, "mPrdornNo = " + mPrdordno);
                boolean isOK = bandCard(mCardNo, mBandCardCardType, mPrdordno);
                if (isOK) {
                    return 1;
                } else {
                    if (mRmg.length() == 0) {
                        mRmg = getString(R.string.shoudianxiangqing_bangkashibai);
                    }
                    Log.e(TAG, "绑卡失败!");
                    return 0;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                mRmg = getString(R.string.shoudianxiangqing_bangkashibai)
                        + e.toString();
                return 0;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
        	if(btn_mag!=null){
        		btn_mag.setEnabled(true);
        	}
            if (capture != null) {
                capture.Stop();
                capture = null;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                // progressDialog = null;
            }
            if (mBangkaBtn != null) {
                mBangkaBtn.setEnabled(true);
            }
            if (mBangkaDialog != null && mBangkaDialog.isShowing()) {
                mBangkaDialog.dismiss();
            }
            if (result == 1) {
                SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                        getString(R.string.shoudianxiangqing_bangkachenggong));
            } else {
                SystemUtil.displayToast(ShouDianXiangQingActivity.this, mRmg);
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
	private String getStrNew(byte[] buffer, int bufferLength) {

		String bufferString = "";

		for (int i = 0; i < bufferLength; i++) {

			String hexChar = Integer.toHexString(buffer[i] & 0xFF);
			if (hexChar.length() == 1) {
				hexChar = "0" + hexChar;
			}
			bufferString += hexChar.toUpperCase();
		}

		if (bufferString != "") {
			return bufferString;
		}
		return "";
	}

    // 写IC卡异步任务
    private class WriteICCardTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            createDialog();
            progressDialog.setTitle(getString(R.string.progress_tishi_title));
            progressDialog.setMessage(getString(R.string.writingcard));
            // 设置进度条是否不明确
//            progressDialog.setIndeterminate(false);

            // 是否可以按下退回键取消
            progressDialog.setCancelable(false);
            Log.e(TAG, "--------WriteICCardTask Dialog Showing");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e(TAG, "IC卡检测开始....");
            // 解析写卡数据
            if (mIcJsonRes.length() == 0) {
//                mRmg = getString(R.string.writecard_nonemessage);
                Log.e(TAG, "未有写卡数据");
                return 0;
            }
            HashMap<String, List<String>> mJsonMap=null;
            if (GlobalParams.CARD_TYPE.equalsIgnoreCase("1")) {
            	 mJsonMap = parsingJsonData4Write(mIcJsonRes);
            }else  if (GlobalParams.CARD_TYPE.equalsIgnoreCase("2")) {
            	mJsonMap = parsingJsonData4Write4442(mIcJsonRes);
            }
            List<String> verify = mJsonMap.get("verify");
            List<String> passwd = mJsonMap.get("passwd");
            List<String> write = mJsonMap.get("write");
            List<String> newPasswd = mJsonMap.get("newpasswd");
            String offset = verify.get(0);
            String value = verify.get(1);
            // 偏移位置
            List<Integer> mOffsetList = new ArrayList<Integer>();
            for (String ss : offset.replaceAll("[^0-9]", ",").split(",")) {
                if (ss.length() > 0) {
                    int s = Integer.valueOf(ss);
                    mOffsetList.add(s);
                }
            }
            // 偏移长度
            List<Integer> mValueLengthList = new ArrayList<Integer>();
            List<String> mValue = matchValue(value);
            for (String str : mValue) {
                mValueLengthList.add(str.length() / 2);
            }
            boolean isVerify = true;

            if (GlobalParams.CARD_TYPE.equalsIgnoreCase("1")) {
            	value=value.replaceAll("[^0-9a-fA-F]","");
            	int len=value.length()/2;
            	String verOffset=Integer.toHexString(Integer.parseInt(offset.replaceAll("[^0-9]","")));
            	String verValue=Integer.toHexString(len);
            	 for(int i=verOffset.length();i<4;i++){
            		 verOffset="0"+verOffset;
	    		  }
            	if(len<16){
            		verValue="0"+verValue;
            	}
            	readCardVerify(verOffset,verValue,value);
            	isVerifyCard=true;
                if (isVerifyCard) {// 校验成功
                	isVerifyCard=false;
                    byte[] pw = StringUtil.str2BCD(passwd.get(0));
                    // 默认
                    // pw=new byte[]{(byte) 0xff,(byte) 0xff};
                    if(rewriteagain){//是否重打
                    	passwd.set(0, newPasswd.get(0));
                    }
                    Log.e(TAG, "密码 = " + passwd.get(0));
                    readCardPassVerify(passwd.get(0));
                    if (isPass) {// 密码正确，开始写卡
                    	isPass=false;
                        String writeOffset = write.get(0);
                        String writeValue = write.get(1);
//                        //开始位置
//                        int Wstart=Integer.parseInt(writeOffset.split(",")[0].replaceAll("[^0-9]",""));
//                        //写卡字节长度
//                        int Wlength=writeValue.split(",")[0].replaceAll("[^0-9a-fA-F]","").length()/2;
//                        String writeValue1=writeValue.split(",")[0].replaceAll("[^0-9a-fA-F]","");
                        List<Integer> mWriteOffsetList = new ArrayList<Integer>();
                        for (String ss : writeOffset.replaceAll("[^0-9]", ",")
                                .split(",")) {
                            if (ss.length() > 0) {
                                int s = Integer.valueOf(ss);
                                mWriteOffsetList.add(s);
                            }
                        }
                        // 数据
                        List<String> mWriteCardValueList = matchValue(writeValue);
                        List<Integer> mWriteValueLenList = new ArrayList<Integer>();
                        for (String str : mWriteCardValueList) {
                        	mWriteValueLenList.add(str.length() / 2);
                        }
                        // 写卡数据偏移位置
                        //数据备份
                        int Wstart=0;
                        int Wlength=1024;
                        readCardBackup(Wstart,Wlength);
                        // 写卡卡串
                        //最后一条为新密码数据 后写
                        String tempNewPassData = mWriteCardValueList.get(mWriteCardValueList.size()-1);
                        int tempNewPassOffset = mWriteOffsetList.get(mWriteOffsetList.size()-1);
                        int tempNewPassLen = mWriteValueLenList.get(mWriteValueLenList.size()-1);
                        mWriteCardValueList.remove(mWriteCardValueList.size()-1);
                        mWriteOffsetList.remove(mWriteOffsetList.size()-1);
                        mWriteValueLenList.remove(mWriteValueLenList.size()-1);
                        writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
                        if (writeSucc) {
                        	writeSucc=false;
                        }
                        //读卡校验是否成功 不成功的重写
                        for(int i = 0;i<2;i++){
	                        if(!VerifyReadEqualWrite(mWriteCardValueList,mWriteOffsetList,mWriteValueLenList)){
	                        	if(i==1){
	                        		 //写卡字节长度
	                                int Wlengthback=backupData.replaceAll("[^0-9a-fA-F]","").length()/2;
	                                List<String> mWriteCardValueList2 = new ArrayList<String>();
	                                List<Integer> mWriteOffsetList2 = new ArrayList<Integer>();
	                                List<Integer> mValueLengthList2 = new ArrayList<Integer>();
	                                mWriteCardValueList2.add(backupData);
	                                mWriteOffsetList2.add(0);
	                                mValueLengthList2.add(Wlengthback);
	                             // 写卡
	                                writeCardMessage(mWriteCardValueList2,passwd.get(0),mWriteOffsetList2,mValueLengthList2);
	                                SystemClock.sleep(300);
	                                if (!writeSucc) {
	                                    Log.e(TAG, "恢复数据失败！");
	                                }
	                                writeSucc=false;
	                                backupData="";
	                        		return 0;
	                        	}
	                        	writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
	                        }else{
	                        	writeSucc=false;
	                        	break;
	                        }
                        }
//                     // 写入新密码
//                        //开始位置
                        int Wstartpwd2=1022;
                        //写卡字节长度
                        int Wlengthpwd2=2;
                        String writeValue3=newPasswd.get(0);
                        List<String> mWriteCardValueList1 = new ArrayList<String>();
                        List<Integer> mWriteOffsetList1 = new ArrayList<Integer>();
                        List<Integer> mValueLengthList1 = new ArrayList<Integer>();
                        mWriteCardValueList1.add(writeValue3);
                        mWriteOffsetList1.add(Wstartpwd2);
                        mValueLengthList1.add(Wlengthpwd2);
                        
                        mWriteCardValueList1.add(tempNewPassData);
                        mWriteOffsetList1.add(tempNewPassOffset);
                        mValueLengthList1.add(tempNewPassLen);
                        writeCardMessage(mWriteCardValueList1,passwd.get(0),mWriteOffsetList1,mValueLengthList1);

                        if (writeSucc) {
                            writeSucc=false;
                            Log.e(TAG, "修改密码成功！");
                            writeSucc=false;
                            backupData="";
                            //密码修改成功，修改写卡串密码
                            rewrite=true;
                            rewriteagain=true;
                            return 1;
                        } else {
                        	rewrite=false;
                            Log.e(TAG, "修改密码失败！");
                            mRmg = getString(R.string.writecard_changePW_fail);
                            
                            //写卡字节长度
                            int Wlengthback=backupData.replaceAll("[^0-9a-fA-F]","").length()/2;
                            List<String> mWriteCardValueList2 = new ArrayList<String>();
                            List<Integer> mWriteOffsetList2 = new ArrayList<Integer>();
                            List<Integer> mValueLengthList2 = new ArrayList<Integer>();
                            mWriteCardValueList2.add(backupData);
                            mWriteOffsetList2.add(0);
                            mValueLengthList2.add(Wlengthback);
                         // 写卡
                            writeCardMessage(mWriteCardValueList2,passwd.get(0),mWriteOffsetList2,mValueLengthList2);
                            SystemClock.sleep(200);
                            if (!writeSucc) {
                                Log.e(TAG, "恢复数据失败！");
                            }
                            writeSucc=false;
                            backupData="";
                            // 恢复数据
                            return 0;
                        }
                    } else {
                        // 密码错误
                        Log.e(TAG, "IC卡密码错误，请检查!");
                        mRmg = getString(R.string.writecard_iccardpw_fail);
                        backupData="";
                    	rewrite=false;
                        return 0;
                    }
                } else {// 校验失败
                    Log.e(TAG, "校验失败!");
                    mRmg = getString(R.string.writecard_verify_fail);
                    backupData="";
                	rewrite=false;
                    return 0;
                }
            } else if (GlobalParams.CARD_TYPE.equalsIgnoreCase("2")) {
                // 4442
                // 校验比较
            	//表号
            	value=value.replaceAll("[^0-9a-fA-F]","");
            	int len=value.length()/2;
            	String verOffset=Integer.toHexString(Integer.parseInt(offset.replaceAll("[^0-9]","")));
            	String verValue=Integer.toHexString(len);
            	if(len<16){
            		verValue="0"+verValue;
            	}
            	readCardVerify(verOffset,verValue,value);
            	SystemClock.sleep(300);
                if (isVerifyCard) {// 校验成功
                	isVerifyCard=false;
                    Log.e(TAG, "密码 = " + passwd.get(0));
                    byte[] pw = StringUtil.str2BCD(passwd.get(0));
                    readCardPassVerify(passwd.get(0));
                    SystemClock.sleep(300);
                    if (isPass) {// 密码正确，开始写卡
                    	isPass=false;
                        String writeOffset = write.get(0);
                        String writeValue = write.get(1);
//                        //开始位置
//                        int Wstart=Integer.parseInt(writeOffset.replaceAll("[^0-9]",""));
//                        //写卡字节长度
//                        int Wlength=writeValue.replaceAll("[^0-9a-fA-F]","").length()/2;
                        // 写卡数据偏移位置
                        List<Integer> mWriteOffsetList = new ArrayList<Integer>();
                        for (String ss : writeOffset.replaceAll("[^0-9]", ",")
                                .split(",")) {
                            if (ss.length() > 0) {
                                int s = Integer.valueOf(ss);
                                mWriteOffsetList.add(s);
                            }
                        }
                        // 数据
                        List<String> mWriteCardValueList = matchValue(writeValue);
                        List<Integer> mWriteValueLenList = new ArrayList<Integer>();
                        for (String str : mWriteCardValueList) {
                        	mWriteValueLenList.add(str.length() / 2);
                        }
                        // 写卡
                        writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
                        SystemClock.sleep(300);
                        if(writeSucc){
                        	writeSucc=false;
                        }
                        //读卡校验是否成功 不成功的重写
                        for(int i = 0;i<2;i++){
	                        if(!VerifyReadEqualWrite(mWriteCardValueList,mWriteOffsetList,mWriteValueLenList)){
	                        	writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
	                        }else{
	                        	writeSucc=false;
	                            return 1;
	                        }
                        }
                        // 写卡失败
                         return 0;
                    } 
                    else 
                    {
                        // 密码错误
                        Log.e(TAG, "IC卡密码错误，请检查!");
                        mRmg = getString(R.string.writecard_iccardpw_fail);
//                        ReaderMonitor.reset();
                        return 0;
                    }
                } else {// 校验失败
                    Log.e(TAG, "校验失败!");
                    mRmg = getString(R.string.writecard_verify_fail);
//                    ReaderMonitor.reset();
                    return 0;
                }
            }
            return 0;
        }
       
//you写卡
    	private void writeCardMessage(List<String> mWriteCardValueList,String passWord,List<Integer> mWriteOffsetList,List<Integer> mValueLengthList){

    		// list
    		String deviceName = null;
    		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
    			if (mReader.isSupported(device)) {
    				deviceName = device.getDeviceName();
    				break;
    			}
    		}
    		// open
    		if (deviceName != null) {

    			// For each device
    			for (UsbDevice device : mManager.getDeviceList().values()) {
    				if (deviceName.equals(device.getDeviceName())) {

    					if (mManager.hasPermission(device)) { // ��ȡȨ��
    						int slotNum = 0;// ״̬0
    						// Transmit
    						// If slot is selected
    						if (slotNum != Spinner.INVALID_POSITION) {

    							// Set parameters
    							TransmitParams paramsTransmit = new TransmitParams();
    							paramsTransmit.slotNum = slotNum;
    							paramsTransmit.controlCode = -1;
    							String changeLine = "\n";
//    							passWord = "b62307";
//    							ICARDSTR = "68013201101014400199991003516757121251977629542048450166221875413638800852819152802064FFFFFFFFFFFFFFFFFFFFA616FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA";
    							if(GlobalParams.CARD_TYPE=="2"){
    								if(mWriteOffsetList.size()!=mWriteCardValueList.size()){
    									return;
    								}
    								String cardInfo="ffa400000106" + changeLine + "ff20000003" + passWord;
    								for(int i=0;i<mWriteOffsetList.size();i++){
	    								String start =Integer.toHexString(mWriteOffsetList.get(i));//20
	    								String len =Integer.toHexString(mValueLengthList.get(i));//E0
	    								String ICARDSTR=mWriteCardValueList.get(i);
	    								if(start.length()==1){
	    									start="0"+start;
	    								}
	    								if(len.length()==1){
	    									len="0"+len;
	    								}
	    								cardInfo += changeLine + "ffd000"+start+len+ ICARDSTR;
    								}
        							paramsTransmit.commandString = cardInfo;
        							TransmitTask(paramsTransmit);// �ύ
    							}else if(GlobalParams.CARD_TYPE=="1"){
    								if(mWriteOffsetList.size()!=mValueLengthList.size() || mWriteOffsetList.size()!=mWriteCardValueList.size()){
    									return;
    								}
    								String cardInfo = "ffa400000105"+ changeLine + "ff20000002" + passWord;
    								for(int j=0;j<mWriteOffsetList.size();j++){
	    								int address=mWriteOffsetList.get(j);
										int len=mValueLengthList.get(j);
										String ICARDSTR=mWriteCardValueList.get(j);
										int unit=255;
										int MaxL=len/unit;
										int lastLen=0;
										if(len%unit>0){
											MaxL+=1;
										}
										String start="";
										String leng="";
										String tempStr="";
										for(int i=0;i<MaxL;i++){
											if(i==MaxL-1&&(len%unit>0)){
												address+=lastLen;
												start=getHex(address,false);
												leng=getHex(len%unit,true);
												tempStr=ICARDSTR;						
											}else{
												address+=lastLen;
												start=getHex(address,false);
												leng=getHex(unit,true);
												tempStr=ICARDSTR.substring(0, unit*2);
												ICARDSTR=ICARDSTR.substring(unit*2);
												lastLen=unit;
											}
		    								cardInfo = cardInfo + changeLine + "ffd0"+start+leng + tempStr;
										}
    								}
        							paramsTransmit.commandString = cardInfo;
        							TransmitTask(paramsTransmit);// �ύ
    							}
    							SystemClock.sleep(400);
    						}
    					} else {
    						// Request permission Ȩ��
    						mManager.requestPermission(device, mPermissionIntent);
    						break;
    					}
    				}
    			}
    		}

    	
    	}
        @Override
        protected void onPostExecute(Integer result) {
        	logMsg1("打完："+(System.currentTimeMillis()-currenttime)/1000);
			SystemClock.sleep(400);
			new CloseTask().execute();
        	logMsg1("chuanchuan 返回:"+result);
            Log.e(TAG, "onPostExecute result = " + result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result == 1) {
                SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                        R.string.shoudianxiangqing_xiekachenggong);
            } else {
                if (mRmg.equalsIgnoreCase("")) {
                    SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                            getString(R.string.shoudianxiangqing_xiekashibai));
                } else {
                    SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                            mRmg);
                }
            }
            SystemClock.sleep(300);
            if (bt_querengoudian != null) {
                bt_querengoudian.setEnabled(true);
            }
            if (mQuerengoudianDialog != null
                    && mQuerengoudianDialog.isShowing()) {
                mQuerengoudianDialog.dismiss();
            }
            if (mGoudianchenggongDialog != null
                    && !mGoudianchenggongDialog.isShowing()) {
                showShoudianchenggongDialog(ShouDianXiangQingActivity.this);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
    //写卡完成后读卡校验是否与写卡串相同
    private boolean VerifyReadEqualWrite(List<String> mWriteCardValueList,List<Integer> mWriteOffsetList,List<Integer> mValueLengthList){
		// list
		String deviceName = null;
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		// open
		if (deviceName != null) {

			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {

				// If device name is found
				if (deviceName.equals(device.getDeviceName())) {

					if (mManager.hasPermission(device)) { // ��ȡȨ��

//							OpenTask(device);
//							SystemClock.sleep(200);
//							int temstate=getState();
//							SystemClock.sleep(200);
						/////// DoAll////////
						// power
						// Get slot number
						int slotNum = 0;// ״̬0

						// Get action number
//						int actionNum = Reader.CARD_WARM_RESET;// Э��0
						// Set parameters
//						PowerParams params = new PowerParams();
//						params.slotNum = slotNum;
//						params.action = actionNum;
						
//						PowerTask(params);
//						SystemClock.sleep(100);

						// Set protocol
						// If slot is selected
//						if (slotNum != Spinner.INVALID_POSITION) {
//
////							int preferredProtocols = Reader.PROTOCOL_UNDEFINED;
////							String preferredProtocolsString = "";
////
////							preferredProtocols |= Reader.PROTOCOL_T0;
////							preferredProtocolsString = "T=0";
////
////							preferredProtocols |= Reader.PROTOCOL_T1;
////							if (preferredProtocolsString != "") {
////								preferredProtocolsString += "/";
////							}
////
////							preferredProtocolsString += "T=1";
////
////							if (preferredProtocolsString == "") {
////								preferredProtocolsString = "None";
////							}
//
//							// Set Parameters
////							SetProtocolParams paramsProtoco = new SetProtocolParams();
////							paramsProtoco.slotNum = slotNum;
////							paramsProtoco.preferredProtocols = preferredProtocols;
//							
////							SetProtocolTask(paramsProtoco);
//						}
//						SystemClock.sleep(100);

						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {

							// Set parameters
							verifyCardParams paramsTransmit = new verifyCardParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
							String cardInfo ="";
							String writecardInfo ="";
							if(GlobalParams.CARD_TYPE=="2"){

								if(mWriteOffsetList.size()!=mWriteCardValueList.size()){
									return false;
								}
								cardInfo="ffa400000106";
								for(int i=0;i<mWriteOffsetList.size();i++){
    								String start =Integer.toHexString(mWriteOffsetList.get(i));//20
    								String len =Integer.toHexString(mValueLengthList.get(i));//E0
    								String ICARDSTR=mWriteCardValueList.get(i);
    								if(start.length()==1){
    									start="0"+start;
    								}
    								if(len.length()==1){
    									len="0"+len;
    								}
    								cardInfo += changeLine + "ffb000"+start+len ;
    								writecardInfo+=ICARDSTR;
								}
    							paramsTransmit.commandString = cardInfo;
//    							TransmitTask(paramsTransmit);// �ύ
							
//								cardInfo = "ffa400000106" + changeLine + "ffb000"+start+length;
							}else if(GlobalParams.CARD_TYPE=="1"){
								if(mWriteOffsetList.size()!=mValueLengthList.size() || mWriteOffsetList.size()!=mWriteCardValueList.size()){
									return false;
								}
								cardInfo = "ffa400000105";
								for(int j=0;j<mWriteOffsetList.size();j++){
    								int address=mWriteOffsetList.get(j);
									int len=mValueLengthList.get(j);
									String ICARDSTR=mWriteCardValueList.get(j);
									int unit=255;
									int MaxL=len/unit;
									int lastLen=0;
									if(len%unit>0){
										MaxL+=1;
									}
									String start="";
									String leng="";
									String tempStr="";
									for(int i=0;i<MaxL;i++){
										if(i==MaxL-1&&(len%unit>0)){
											address+=lastLen;
											start=getHex(address,false);
											leng=getHex(len%unit,true);
											tempStr=ICARDSTR;						
										}else{
											address+=lastLen;
											start=getHex(address,false);
											leng=getHex(unit,true);
											tempStr=ICARDSTR.substring(0, unit*2);
											ICARDSTR=ICARDSTR.substring(unit*2);
											lastLen=unit;
										}
	    								cardInfo = cardInfo + changeLine + "ffb0"+start+leng ;
	    								writecardInfo += tempStr;
									}
								}
    							paramsTransmit.commandString = cardInfo;
//    							TransmitTask(paramsTransmit);// �ύ
//								cardInfo = "ffa400000105" + changeLine + "ffb0"+start+length;
							}
							paramsTransmit.commandString = cardInfo;
							paramsTransmit.verifyPara = writecardInfo;
							int param = 2;
							Boolean isEqual = verifyCardTask(paramsTransmit,param);// �ύ
							SystemClock.sleep(200);
							 return isEqual;
						}
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						break;
					}
				}
			}
		}
		return false;
	}
    
   	private void writeCardPassward(String ICARDSTRU[],String passWord,int Wstart[],int Wlength[]){

		// list
		String deviceName = null;
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		// open
		if (deviceName != null) {

			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {

				// If device name is found
				if (deviceName.equals(device.getDeviceName())) {

					if (mManager.hasPermission(device)) { // ��ȡȨ��

//							new OpenTask().execute(device);
//							SystemClock.sleep(200);
						/////// DoAll////////
						// power
						// Get slot number
						int slotNum = 0;// ״̬0

						// Get action number
						int actionNum = Reader.CARD_WARM_RESET;// Э��0
						// Set parameters
						PowerParams params = new PowerParams();
						params.slotNum = slotNum;
						params.action = actionNum;
						
						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {

							// Set parameters
							TransmitParams paramsTransmit = new TransmitParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
							String cardInfo = "ffa400000105"+ changeLine + "ff20000002" + passWord;
							int unit=255;
							for(int j=0;j<Wstart.length;j++){
								String ICARDSTR=ICARDSTRU[j];
								int address=Wstart[j];
								int len=Wlength[j];
								int MaxL=len/unit;
								int lastLen=0;
								if(len%unit>0){
									MaxL+=1;
								}
								String start="";
								String leng="";
								String tempStr="";
								for(int i=0;i<MaxL;i++){
									if(i==MaxL-1&&(len%unit>0)){
										address+=lastLen;
										start=getHex(address,false);
										leng=getHex(len%unit,true);
										tempStr=ICARDSTR;						
									}else{
										address+=lastLen;
										start=getHex(address,false);
										leng=getHex(unit,true);
										tempStr=ICARDSTR.substring(0, unit*2);
										ICARDSTR=ICARDSTR.substring(unit*2);
										lastLen=unit;
									}
    								cardInfo = cardInfo + changeLine + "ffd0"+start+leng + tempStr;
								}
							}
    							paramsTransmit.commandString = cardInfo;
    							TransmitTask(paramsTransmit);// �ύ
//							SystemClock.sleep(400);
//							new CloseTask().execute();
						}
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						break;
					}
				}
			}
		}

	
	}
    //4428读卡备份数据
	private void readCardBackup(int start,int length){
		String deviceName = null;
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		// open
		if (deviceName != null) {

			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {

				// If device name is found
				if (deviceName.equals(device.getDeviceName())) {

					if (mManager.hasPermission(device)) { // ��ȡȨ��
						// power
						// Get slot number
						int slotNum = 0;//
						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {
							// Set parameters
							String startread="";
							String lenread="";
							TransmitParams paramsTransmit = new TransmitParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
							String cardInfo="";
								int address=start;
								int len=length;
								int unit=255;
								int MaxL=len/unit;
								int lastLen=0;
								if(len%unit>0){
									MaxL+=1;
								}
								cardInfo = "ffa400000105" ;
								for(int i=0;i<MaxL;i++){
									if(i==MaxL-1&&(len%unit>0)){
										address+=lastLen;
										startread=getHex(address,false);
										lenread=getHex(len%unit,true);
																
									}else{
										address+=lastLen;
										startread=getHex(address,false);
										lenread=getHex(unit,true);
										lastLen=unit;
									}
									cardInfo = cardInfo + changeLine + "ffb0"+startread+lenread;
								}
								paramsTransmit.commandString = cardInfo;
								new TransmitTaskBackup().execute(paramsTransmit);
							SystemClock.sleep(300);
						}
						/////// *****////////
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						break;
					}
				}
			}
		}
	}
	private class TransmitTaskBackup extends AsyncTask<TransmitParams, TransmitProgress, String> {

		@Override
		protected String doInBackground(TransmitParams... params) {

			TransmitProgress progress = null;

			byte[] command = null;
			byte[] response = null;
			int responseLength = 0;
			int foundIndex = 0;
			int startIndex = 0;
			String resultStr="";
			do {

				// Find carriage return
				foundIndex = params[0].commandString.indexOf('\n', startIndex);
				if (foundIndex >= 0) {
					command = toByteArray(params[0].commandString.substring(startIndex, foundIndex));
				} else {
					command = toByteArray(params[0].commandString.substring(startIndex));
				}

				// Set next start index
				startIndex = foundIndex + 1;

				response = new byte[300];
				progress = new TransmitProgress();
				progress.controlCode = params[0].controlCode;
				try {

					if (params[0].controlCode < 0) {

						// Transmit APDU
						responseLength = mReader.transmit(params[0].slotNum, command, command.length, response,
								response.length);

					} else {

						// Transmit control command
						responseLength = mReader.control(params[0].slotNum, params[0].controlCode, command,
								command.length, response, response.length);
					}

					progress.command = command;
					progress.commandLength = command.length;
					progress.response = response;
					progress.responseLength = responseLength;
					progress.e = null;

				} catch (Exception e) {

					progress.command = null;
					progress.commandLength = 0;
					progress.response = null;
					progress.responseLength = 0;
					progress.e = e;
				}

				publishProgress(progress);
				resultStr+=getStrNew(progress.response, progress.responseLength);
				 if(resultStr.length()>=4){
//	            	  if(resultStr.substring(resultStr.length()-4, resultStr.length()).equals("9000")){
	            	  if(resultStr.endsWith("9000")){
	            		  if(resultStr.length()==4){
	            			  resultStr="";
	            		  }else{
	            			  resultStr=resultStr.substring(0,resultStr.length()-4);
	            		  }
	            	  }else{
	 					 resultStr="";
	 					 break;
	 				 }
				 }else{
					 resultStr="";
					 break;
				 }
			} while (foundIndex >= 0);

			return resultStr;
		}

		@Override
		protected void onProgressUpdate(TransmitProgress... progress) {

			if (progress[0].e != null) {

				logMsg1(progress[0].e.toString());

			} else {

//				logMsg1("Command:");
//				logBuffer(progress[0].command, progress[0].commandLength);

//				logMsg1("Response:");
//				logBuffer(progress[0].response, progress[0].responseLength);

				if (progress[0].response != null && progress[0].responseLength > 0) {

					int controlCode;
					int i;

					// Show control codes for IOCTL_GET_FEATURE_REQUEST
					if (progress[0].controlCode == Reader.IOCTL_GET_FEATURE_REQUEST) {

						mFeatures.fromByteArray(progress[0].response, progress[0].responseLength);

//						logMsg1("Features:");
						for (i = Features.FEATURE_VERIFY_PIN_START; i <= Features.FEATURE_CCID_ESC_COMMAND; i++) {

							controlCode = mFeatures.getControlCode(i);
							if (controlCode >= 0) {
								logMsg1("Control Code: " + controlCode + " (" + featureStrings[i] + ")");
							}
						}

						// Enable buttons if features are supported
//						mVerifyPinButton.setEnabled(mFeatures.getControlCode(Features.FEATURE_VERIFY_PIN_DIRECT) >= 0);
//						mModifyPinButton.setEnabled(mFeatures.getControlCode(Features.FEATURE_MODIFY_PIN_DIRECT) >= 0);
					}

					controlCode = mFeatures.getControlCode(Features.FEATURE_IFD_PIN_PROPERTIES);
					if (controlCode >= 0 && progress[0].controlCode == controlCode) {

						PinProperties pinProperties = new PinProperties(progress[0].response,
								progress[0].responseLength);

						logMsg1("PIN Properties:");
						logMsg1("LCD Layout: " + toHexString(pinProperties.getLcdLayout()));
						logMsg1("Entry Validation Condition: "
								+ toHexString(pinProperties.getEntryValidationCondition()));
						logMsg1("Timeout 2: " + toHexString(pinProperties.getTimeOut2()));
					}

					controlCode = mFeatures.getControlCode(Features.FEATURE_GET_TLV_PROPERTIES);
					if (controlCode >= 0 && progress[0].controlCode == controlCode) {

						TlvProperties readerProperties = new TlvProperties(progress[0].response,
								progress[0].responseLength);

						Object property;
						logMsg1("TLV Properties:");
						for (i = TlvProperties.PROPERTY_wLcdLayout; i <= TlvProperties.PROPERTY_wIdProduct; i++) {

							property = readerProperties.getProperty(i);
							if (property instanceof Integer) {
//								logMsg1(propertyStrings[i] + ": " + toHexString((Integer) property));
							} else if (property instanceof String) {
//								logMsg1(propertyStrings[i] + ": " + property);
							}
						}
					}
				}
			}
		}
        @Override  
        protected void onPostExecute(String result) {  
//            textView.setText("异步操作执行结束" + result); 
            logMsg1("doIN后:"+result);
            if(result.equals("")){
            	logMsg1("dukashibai");
//            	new CloseTask().execute();
            	return;
            }
            		logMsg1("读卡成功！");
            		backupData=result;
        }  
	}
   	private void readCardVerify(String start,String length,String verifyPara){
		// list
		String deviceName = null;
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		// open
		if (deviceName != null) {

			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {

				// If device name is found
				if (deviceName.equals(device.getDeviceName())) {

					if (mManager.hasPermission(device)) { // ��ȡȨ��

							OpenTask(device);
							SystemClock.sleep(200);
							int temstate=getState();
							SystemClock.sleep(200);
						/////// DoAll////////
						// power
						// Get slot number
						int slotNum = 0;// ״̬0

						// Get action number
						int actionNum = Reader.CARD_WARM_RESET;// Э��0
						// Set parameters
						PowerParams params = new PowerParams();
						params.slotNum = slotNum;
						params.action = actionNum;
						
						PowerTask(params);


						// Set protocol
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {

							int preferredProtocols = Reader.PROTOCOL_UNDEFINED;
							String preferredProtocolsString = "";

							preferredProtocols |= Reader.PROTOCOL_T0;
							preferredProtocolsString = "T=0";

							preferredProtocols |= Reader.PROTOCOL_T1;
							if (preferredProtocolsString != "") {
								preferredProtocolsString += "/";
							}

							preferredProtocolsString += "T=1";

							if (preferredProtocolsString == "") {
								preferredProtocolsString = "None";
							}

							// Set Parameters
							SetProtocolParams paramsProtoco = new SetProtocolParams();
							paramsProtoco.slotNum = slotNum;
							paramsProtoco.preferredProtocols = preferredProtocols;
							
							SetProtocolTask(paramsProtoco);
						}

						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {

							// Set parameters
							verifyCardParams paramsTransmit = new verifyCardParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
//							passWord = "b62307";
//							ICARDSTR = "68013201101014400199991003516757121251977629542048450166221875413638800852819152802064FFFFFFFFFFFFFFFFFFFFA616FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA";
//							String cardInfo = "ffa400000106" + changeLine + "ff20000003" + passWord
//									+ changeLine + "ffd00020E0" + ICARDSTR;
//							String cardInfo = "ffa400000106" + changeLine + "ffb00020E0";
							String cardInfo ="";
							if(GlobalParams.CARD_TYPE=="2"){
								cardInfo = "ffa400000106" + changeLine + "ffb000"+start+length;
							}else if(GlobalParams.CARD_TYPE=="1"){
								cardInfo = "ffa400000105" + changeLine + "ffb0"+start+length;
							}
							paramsTransmit.commandString = cardInfo;
							paramsTransmit.verifyPara = verifyPara;
							int param = 1;
							verifyCardTask(paramsTransmit,param);// �ύ
//							new CloseTask().execute();
							SystemClock.sleep(200);
//							 mReader.close();
						}
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						break;
					}
				}
			}
		}
	}
	private void readCardPassVerify(String passward){

		// list
		String deviceName = null;
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		// open
		if (deviceName != null) {

			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {

				// If device name is found
				if (deviceName.equals(device.getDeviceName())) {

					if (mManager.hasPermission(device)) { // ��ȡȨ��
						int slotNum = 0;// ״̬0

						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {

							// Set parameters
							TransmitParams paramsTransmit = new TransmitParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
//							passWord = "b62307";
//							ICARDSTR = "68013201101014400199991003516757121251977629542048450166221875413638800852819152802064FFFFFFFFFFFFFFFFFFFFA616FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA";
//							String cardInfo = "ffa400000106" + changeLine + "ff20000003" + passWord
//									+ changeLine + "ffd00020E0" + ICARDSTR;
//							String cardInfo = "ffa400000106" + changeLine + "ffb00020E0";
//							passward="b62307";
							String cardInfo="";
							if(GlobalParams.CARD_TYPE=="2"){
								cardInfo = "ffa400000106" + changeLine +"ff20000003" + passward ;
							}else if(GlobalParams.CARD_TYPE=="1"){
								cardInfo = "ffa400000105" + changeLine +"ff20000002" + passward ;
							}
							paramsTransmit.commandString = cardInfo;
							TransmitTask2(paramsTransmit);// �ύ
							SystemClock.sleep(400);
						}
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						break;
					}
				}
			}
		}
	}
	private void TransmitTask2(TransmitParams paramsTransmit){
			TransmitParams[] params = new TransmitParams[1];
			params[0] = paramsTransmit;
			TransmitProgress progress = null;
			byte[] command = null;
			byte[] response = null;
			int responseLength = 0;
			int foundIndex = 0;
			int startIndex = 0;

			do {
				foundIndex = params[0].commandString.indexOf('\n', startIndex);
				if (foundIndex >= 0) {
					command = toByteArray(params[0].commandString.substring(startIndex, foundIndex));
				} else {
					command = toByteArray(params[0].commandString.substring(startIndex));
				}

				// Set next start index
				startIndex = foundIndex + 1;

				response = new byte[300];
				progress = new TransmitProgress();
				progress.controlCode = params[0].controlCode;
				try {

					if (params[0].controlCode < 0) {

						// Transmit APDU
						responseLength = mReader.transmit(params[0].slotNum, command, command.length, response,
								response.length);

					} else {

						// Transmit control command
						responseLength = mReader.control(params[0].slotNum, params[0].controlCode, command,
								command.length, response, response.length);
					}

					progress.command = command;
					progress.commandLength = command.length;
					progress.response = response;
					progress.responseLength = responseLength;
					progress.e = null;

				} catch (Exception e) {

					progress.command = null;
					progress.commandLength = 0;
					progress.response = null;
					progress.responseLength = 0;
					progress.e = e;
				}
			} while (foundIndex >= 0);

			String result = logBuffer(progress.response, progress.responseLength);
			SystemClock.sleep(100);
            if(GlobalParams.CARD_TYPE=="2"){
	            if(result!=null){
	          	  if(result.equals("9007")){
	          		  isPass=true;
	                }else if(result.equals("9000")){
	                	 isPass=false;
	              }else{
	            	  isPass=false;
	              }
	          }else{
	        	  isPass=false;
	          }
	        }else if(GlobalParams.CARD_TYPE=="1"){
		            if(result!=null){
		          	  if(result.equalsIgnoreCase("90FF")){
		          		  isPass=true;
		                }else if(result.equals("9000")){
		                	 isPass=false;
		              }else{
		            	  isPass=false;
		              }
		          }else{
		        	  isPass=false;
		          }
		}
}
    //非异步任务校验        
    private boolean verifyCardTask(verifyCardParams paramsTransmit, int param) {
    	verifyCardParams[]params=new verifyCardParams[1];
    	params[0] = paramsTransmit;
			TransmitProgress progress = null;
			byte[] command = null;
			byte[] response = null;
			int responseLength = 0;
			int foundIndex = 0;
			int startIndex = 0;
			String veriftStr = "";
			do {
				// Find carriage return
				foundIndex = params[0].commandString.indexOf('\n', startIndex);
				if (foundIndex >= 0) {
					command = toByteArray(params[0].commandString.substring(startIndex, foundIndex));
				} else {
					command = toByteArray(params[0].commandString.substring(startIndex));
				}

				// Set next start index
				startIndex = foundIndex + 1;
				response = new byte[300];
				progress = new TransmitProgress();
				progress.controlCode = params[0].controlCode;
				try {

					if (params[0].controlCode < 0) {

						// Transmit APDU
						responseLength = mReader.transmit(params[0].slotNum, command, command.length, response,
								response.length);

					} else {

						// Transmit control command
						responseLength = mReader.control(params[0].slotNum, params[0].controlCode, command,
								command.length, response, response.length);
					}

					progress.command = command;
					progress.commandLength = command.length;
					progress.response = response;
					progress.responseLength = responseLength;
					progress.e = null;
					SystemClock.sleep(50);
					String temp = logBuffer(progress.response, progress.responseLength);
					if(temp.length()>=4){
			          	  if(temp.substring(temp.length()-4, temp.length()).equals("9000")){
			          		temp=temp.substring(0,temp.length()-4);
			          	  }else{
			          			if(param==2){
			        				  return false;
			        			  }else{
			        			  	isVerifyCard=false;
			        			  	return false;
			        			  }
			               }
					}else{
	          			if(param==2){
	        				  return false;
	        			  }else{
	        			  	isVerifyCard=false;
	        			  	return false;
	        			  }
	                }
					veriftStr += temp;

				} catch (Exception e) {

					progress.command = null;
					progress.commandLength = 0;
					progress.response = null;
					progress.responseLength = 0;
					progress.e = e;
				}
			} while (foundIndex >= 0);
			 if((params[0].verifyPara).equalsIgnoreCase(veriftStr)){
     			  if(param==2){
     				  return true;
     			  }else{
     			  	isVerifyCard=true;
     			  	return true;
     			  }
     		  }else{
     			if(param==2){
   				  return false;
   			  	}else{
   			  	isVerifyCard=false;
   			  	return false;
   			  	}
             }
	}

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_PERIOD) {
            if (GlobalParams.De.equalsIgnoreCase("0")) {
                return false;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (mConfirmChargeBtn != null
                        && mZidingshoudianjineEt.getText().toString().length() > 0&&!isConfirmChargeCommit) {
                    mConfirmChargeBtn.performClick();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private HashMap<String, List<String>> parsingJsonData4Write(String jsonStr) {
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
        List<String> verifyList = new ArrayList<String>();
        List<String> passwdList = new ArrayList<String>();
        List<String> newPasswdList = new ArrayList<String>();
        List<String> writeList = new ArrayList<String>();
        try {
            JSONObject jso = new JSONObject(jsonStr);
            String verify = jso.getString("verify"); // 得到
            // {"offset":[5,100],"value":["12345","332200777"]}
            JSONObject jso1 = new JSONObject(verify);
            mOffset = jso1.getString("offset"); // 得到 [5,100]
            Log.e(TAG, "offset = " + mOffset);
            mValue = jso1.getString("value"); // 得到["12345","332200777"]
            Log.e(TAG, "value = " + mValue);
            verifyList.add(mOffset);
            verifyList.add(mValue);
            resultMap.put("verify", verifyList);

            String passwd = jso.getString("passwd"); // 得到 "123456789"
            passwdList.add(passwd);
            resultMap.put("passwd", passwdList);

            String newPasswd = jso.getString("newpasswd"); // 得到新密码
            Log.e(TAG, "新密码 = " + newPasswd);
            newPasswdList.add(newPasswd);
            resultMap.put("newpasswd", newPasswdList);

            String write = jso.getString("write"); // 得到
            // {"offset":[15,50,100],"value":["FF00DD","0032","aabb"]}
            JSONObject jso2 = new JSONObject(write);
            mOffset = jso2.getString("offset"); // 得到 [15,50,100]
            Log.e(TAG, "offset = " + mOffset);
            mValue = jso2.getString("value"); // 得到["FF00DD","0032","aabb"]
            Log.e(TAG, "value = " + mValue);
            writeList.add(mOffset);
            writeList.add(mValue);
            resultMap.put("write", writeList);
        } catch (Exception ex) {
            Log.e(TAG, "Exception ex= " + ex.toString());
            return null;
        }
        return resultMap;
    }
    private HashMap<String, List<String>> parsingJsonData4Write4442(String jsonStr) {
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
        List<String> verifyList = new ArrayList<String>();
        List<String> passwdList = new ArrayList<String>();
        List<String> writeList = new ArrayList<String>();
        try {
            JSONObject jso = new JSONObject(jsonStr);
            String verify = jso.getString("verify"); // 得到
            // {"offset":[5,100],"value":["12345","332200777"]}
            JSONObject jso1 = new JSONObject(verify);
            mOffset = jso1.getString("offset"); // 得到 [5,100]
            Log.e(TAG, "offset = " + mOffset);
            mValue = jso1.getString("value"); // 得到["12345","332200777"]
            Log.e(TAG, "value = " + mValue);
            verifyList.add(mOffset);
            verifyList.add(mValue);
            resultMap.put("verify", verifyList);

            String passwd = jso.getString("passwd"); // 得到 "123456789"
            passwdList.add(passwd);
            resultMap.put("passwd", passwdList);

            String write = jso.getString("write"); // 得到
            JSONObject jso2 = new JSONObject(write);
            mOffset = jso2.getString("offset"); // 得到 [15,50,100]
            Log.e(TAG, "offset = " + mOffset);
            mValue = jso2.getString("value"); // 得到["FF00DD","0032","aabb"]
            Log.e(TAG, "value = " + mValue);
            writeList.add(mOffset);
            writeList.add(mValue);
            resultMap.put("write", writeList);
        } catch (Exception ex) {
            Log.e(TAG, "Exception ex= " + ex.toString());
            return null;
        }
        return resultMap;
    }

    private List<Integer> parsingJsonData4Read(String jsonStr) {
        // READ4428:{"read":{"offset":[5,20],"value":[10,8]}}
        List<Integer> offsetAndValue = new ArrayList<Integer>();
        try {
            JSONObject jso = new JSONObject(jsonStr);
            String read = jso.getString("read"); // 得到
            // {“offset”:[5,100],”value”:[5,4]}
            JSONObject jso1 = new JSONObject(read);
            mOffset = jso1.getString("offset"); // 得到 [5,100]
            Log.e(TAG, "offset = " + mOffset);
            mValue = jso1.getString("value"); // 得到[5,4]
            Log.e(TAG, "value = " + mValue);
            String total = mOffset + mValue;
            for (String ss : total.replaceAll("[^0-9]", ",").split(",")) {
                if (ss.length() > 0) {
                    int s = Integer.valueOf(ss);
                    Log.e(TAG, "s = " + s);
                    offsetAndValue.add(s);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception ex= " + ex.toString());
            return null;
        }
        return offsetAndValue;
    }

    public static List<String> matchValue(String s) {
        List<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("\"([\\w/\\.]*)\"");
        Matcher m = p.matcher(s);
        while (!m.hitEnd() && m.find()) {
            Log.e(TAG, "Match value =" + m.group(1));
            results.add(m.group(1));
        }
        return results;
    }

    private String keepDecimalPlaces(String number) {
		if (number == null) {
			return "";
		}
		if (GlobalParams.De == null || GlobalParams.De.equalsIgnoreCase("")) {
			GlobalParams.De = "2";
		}
		if(GlobalParams.De.equalsIgnoreCase("0")){
			if(number.contains(".")){
				number=String.valueOf(Math.round(Float.valueOf(number)));
			}
			return number;
		}
		if (!number.contains(".")) {
			number += ".0";
		}
		int positionLength = number.length() - number.indexOf(".") - 1;
		if (positionLength < Integer.valueOf(GlobalParams.De)) {
			for (int i = 0; i < (Integer.valueOf(GlobalParams.De) - positionLength); i++) {
				number += "0";
			}
		} else if (positionLength > Integer.valueOf(GlobalParams.De)) {
			DecimalFormat df1 = new DecimalFormat("#.0");
			DecimalFormat df2 = new DecimalFormat("#.00");
			DecimalFormat df3 = new DecimalFormat("#.000");
			if (GlobalParams.De.equalsIgnoreCase("1")) {
				number = df1.format(Float.valueOf(number));
			} else if(GlobalParams.De.equalsIgnoreCase("2")){
				number = df2.format(Float.valueOf(number));
			}else if(GlobalParams.De.equalsIgnoreCase("3")){
				number = df3.format(Float.valueOf(number));
			}else{
				number = df2.format(Float.valueOf(number));
			}
		}
		return number;
	}

    private void setOnReadCardNoCallBack(ReadCardNoCallBack mReadCardNoCallBack) {
        this.mReadCardNoCallBack = mReadCardNoCallBack;
    }

    private interface ReadCardNoCallBack {
        void readCardMessage(String cardNo);
    }

    // 隐藏系统键盘
    public void hideSoftInputMethod(EditText ed) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private final BroadcastReceiver mCardMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "icc present Broadcast Receiver");
            int dataNum = 0;
            List<Integer> numList = new ArrayList<Integer>();
            List<String> dataList = new ArrayList<String>();
            if (intent.getAction() == ReaderMonitor.ACTION_ICC_PRESENT) {
                if (intent.getExtras().getBoolean(
                        ReaderMonitor.EXTRA_IS_PRESENT)) {
                    int cardType = intent.getExtras().getInt(
                            ReaderMonitor.EXTRA_CARD_TYPE);
                    if (cardType == CardReader.CARD_TYPE_SLE4428) {
                        mBandCardCardType = "1";
                        GlobalParams.CARD_TYPE = "1";
                        byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF};
                        ReaderMonitor.pscVerify(psc);
                        // 读IC卡卡号
                        byte userCode[] = ReaderMonitor.getUserCode();
                        String cardNo = StringUtil.BCD2Str(userCode);
                        Log.e(TAG, "cardNo = " + cardNo);
                        GlobalParams.ICCARD_NO = cardNo;
                        // 取消IC卡綁卡
                        // if (mReadCardNoCallBack != null) {
                        // // 读IC卡卡号
                        // byte userCode[] = ReaderMonitor.getUserCode();
                        // String cardNo = StringUtil.BCD2Str(userCode);
                        // Log.e(TAG, "cardNo = " + cardNo);
                        // GlobalParams.ICCARD_NO = cardNo;
                        // mReadCardNoCallBack.readCardMessage(cardNo);
                        // }

                        // 购电前若是检测到插入4428的卡，那必须先再读一次卡
                        numList = parsingJsonData4Read(GlobalParams.READ4428);
                        if (numList != null) {
                            dataNum = numList.size() / 2;
                        }
                        Log.e(TAG, "dataNum = " + dataNum);
                        for (int i = 0; i < dataNum; i++) {
                            byte[] temp = ReaderMonitor.readMainMemory(
                                    numList.get(i), numList.get(i + dataNum));
                            if (temp != null) {
                                dataList.add(StringUtil.BCD2Str(temp));
                            } else {
                                Log.e(TAG,
                                        "mSLE4428Reader.readMainMemory = null");
                            }

                        }
                        String ic_json_req = "";
                        for (int i = 0; i < dataList.size(); i++) {
                            if (i == dataList.size() - 1) {// 最后一段
                                ic_json_req += "\"" + dataList.get(i) + "\"";
                            } else {
                                ic_json_req += "\"" + dataList.get(i) + "\""
                                        + ",";
                            }
                        }
                        String IC_JSON_REQ = "{   \"read\" : {\"offset\" : "
                                + mOffset + ",   \"value\" : [" + ic_json_req
                                + "] }}";
                        Log.e(TAG, "IC_JSON_REQ = " + IC_JSON_REQ);
                        GlobalParams.IC_JSON_REQ = IC_JSON_REQ;
                    } else if (cardType == CardReader.CARD_TYPE_SLE4442) {
                        mBandCardCardType = "1";
                        GlobalParams.CARD_TYPE = "2";
                        byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF,
                                (byte) 0xFF};
                        ReaderMonitor.pscVerify(psc);
                        byte userCode[] = ReaderMonitor.getUserCode();
                        String cardNo = StringUtil.BCD2Str(userCode);
                        Log.e(TAG, "cardNo = " + cardNo);
                        GlobalParams.ICCARD_NO = cardNo;

                        // 购电前若是检测到插入4442的卡，那必须先再读一次卡
                        numList = parsingJsonData4Read(GlobalParams.READ4442);
                        if (numList != null) {
                            dataNum = numList.size() / 2;
                        }
                        Log.e(TAG, "dataNum = " + dataNum);
                        for (int i = 0; i < dataNum; i++) {
                            byte[] temp = ReaderMonitor.readMainMemory(
                                    numList.get(i), numList.get(i + dataNum));
                            if (temp != null) {
                                dataList.add(StringUtil.BCD2Str(temp));
                            } else {
                                Log.e(TAG,
                                        "mSLE4442Reader.readMainMemory = null");
                            }

                        }
                        String ic_json_req = "";
                        for (int i = 0; i < dataList.size(); i++) {
                            if (i == dataList.size() - 1) {// 最后一段
                                ic_json_req += "\"" + dataList.get(i) + "\"";
                            } else {
                                ic_json_req += "\"" + dataList.get(i) + "\""
                                        + ",";
                            }
                        }
                        String IC_JSON_REQ = "{   \"read\" : {\"offset\" : "
                                + mOffset + ",   \"value\" : [" + ic_json_req
                                + "] }}";
                        Log.e(TAG, "IC_JSON_REQ = " + IC_JSON_REQ);
                        GlobalParams.IC_JSON_REQ = IC_JSON_REQ;
                        // 取消IC卡綁卡
                        // if (mReadCardNoCallBack != null) {
                        // // 读IC卡卡号
                        // byte userCode[] = ReaderMonitor.getUserCode();
                        // String cardNo = StringUtil.BCD2Str(userCode);
                        // Log.e(TAG, "cardNo = " + cardNo);
                        // GlobalParams.ICCARD_NO = cardNo;
                        // mReadCardNoCallBack.readCardMessage(cardNo);
                        // }
                    } else {
                        Log.e(TAG, "Card Type Unknow!");
                    }
                } else {
                    Log.e(TAG, "NO Card");
                }
            } else if (intent.getAction() == ReaderMonitor.ACTION_MSC) {
                mBandCardCardType = "2";
                GlobalParams.CARD_TYPE = "3";
                String[] trackData = intent.getExtras().getStringArray(
                        ReaderMonitor.EXTRA_MSC_TRACK);
                StringBuilder builder = new StringBuilder();
                // builder.append("Track1:\n" + trackData[0] + "\nTrack2:\n" +
                // trackData[1] + "\nTrack3:\n" + trackData[2]);
                // try{
                // Log.e(TAG, "磁條卡数据内容 =" +builder.toString());
                // GlobalParams.TRACCARD_NO = trackData[1].substring(
                // trackData[1].indexOf(";") + 1,
                // trackData[1].indexOf("="));
                // Log.e(TAG, "磁條卡卡号 =" + GlobalParams.TRACCARD_NO);
                // }catch(Exception ex){
                // SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                // R.string.trac_data_error);
                // }

                builder.append(trackData[0] + "|" + trackData[1] + "|"
                        + trackData[2]);
                Log.e(TAG, "TRAC = " + builder.toString());
                if (mReadCardNoCallBack != null) {// &&!GlobalParams.TRACCARD_NO.equalsIgnoreCase("")
                    if (TracData.equalsIgnoreCase("")) {
                        TracData = builder.toString();
                        SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                                getString(R.string.warm_swipcard_again));
                    } else {
                        if (!TracData.equalsIgnoreCase(builder.toString())) {
                            SystemUtil.displayToast(
                                    ShouDianXiangQingActivity.this,
                                    getString(R.string.warm_swipcard_dataError));
                            TracData = builder.toString();
                        } else {
                            TracData = "";
                            mReadCardNoCallBack.readCardMessage(builder
                                    .toString());// GlobalParams.TRACCARD_NO
                        }
                    }
                } else {
                    // SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                    // R.string.shoudianxiangqing_bangkashibai);
                    if (progressDialog != null
                            && (!ShouDianXiangQingActivity.this.isFinishing())) {
                        progressDialog.dismiss();
                        // progressDialog = null;
                    }
                }
            }
        }
    };

    protected void sendMsgInner(int what) {
        Message msg = new Message();
        msg.what = what;
        mShouDianHandler.sendMessage(msg);
    }

    Handler mShouDianHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            SystemUtil
                                    .displayToast(
                                            ShouDianXiangQingActivity.this,
                                            getString(R.string.shoufeixiangqing_goudianshibai));
                        }
                        if (bt_querengoudian != null) {
                            bt_querengoudian.setEnabled(true);
                        }
                        

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    String rspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
                            "<RSPCOD>", "</RSPCOD>");
                    mPrdordno = Client.Parse_XML(GlobalParams.RETURN_DATA,
                            "<PRDORDNO>", "</PRDORDNO>");
                    mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
                            "<RSPMSG>", "</RSPMSG>");
                    mShoudianZongeRsp = Client.Parse_XML(GlobalParams.RETURN_DATA, "<AMT>",
                            "</AMT>");
                    mShoudianjineRsp = Client.Parse_XML(GlobalParams.RETURN_DATA, "<PAY_AMT>",
                            "</PAY_AMT>");
                    mShoudianFeeRsp = Client.Parse_XML(GlobalParams.RETURN_DATA, "<FEE_AMT>",
                            "</FEE_AMT>");
                    mIcJsonRes = Client.Parse_XML(GlobalParams.RETURN_DATA,
                            "<IC_JSON_RES>", "</IC_JSON_RES>");
                    Log.e(TAG, "mIcJsonRes = " + mIcJsonRes);
                    if (!rspCode.equals("00000")) {// 请求失败
                        // 服务器返回系统超时，返回到登录页面
                        if (rspCode.equals("00011")) {
                            Toast.makeText(ShouDianXiangQingActivity.this, mRspMeg,
                                    Toast.LENGTH_LONG).show();
                            SystemUtil.setGlobalParamsToNull(ShouDianXiangQingActivity.this);
                            DummyContent.ITEM_MAP.clear();
                            DummyContent.ITEMS.clear();
                            Intent intent = new Intent(ShouDianXiangQingActivity.this, LoginActivity.class);
                            ShouDianXiangQingActivity.this.startActivity(intent);
                        } 
                        
                        //00156：调用surecash接口前判断账户和手机号是否为空
                        else if(rspCode.equals("00156")){
                        	showQuerengoudiansurecashDialog(ShouDianXiangQingActivity.this);	
                        }
                        
                        
	                    //22
	                   //00022：调用surecash支付接口，返回码标识支付接口的所有错误
	                   //00604:调用surecash检查接口，返回码标识：系统超时，请重试
	                   //91001:调用surecash检查接口，返回码标识： 未输入PIN码，一段时间后作废当前订单     
                      else if(rspCode.equals("00022")||rspCode.equals("00604")||rspCode.equals("91001")){
                    	 bt_querengoudian.setClickable(true); //返回按钮可继续发起购电请求 
                   	     bt_querengoudian.setEnabled(true);//返回按钮可继续发起购电请求 
                    	 AlertDialog.Builder builder = new AlertDialog.Builder(ShouDianXiangQingActivity.this);
                         builder.setTitle("");
                       if(mRspMeg.equals("")){
                    	   builder.setMessage(R.string.SureCash_pay);
                       }else{
                    	   builder.setMessage(mRspMeg);
                       }
                         builder.setPositiveButton(R.string.shoufeixiangqing_btn_surecashcheckqueren, new DialogInterface.OnClickListener()
                         {
                             @Override
                             public void onClick(DialogInterface dialog, int which)
                             {
                            	 billBuyQuery();
                             }
                         }); 
                         builder.show();
       
                    }
                    //00606：调用surecash支付后的检查接口，返回已知错误，修改订单状态未支付，弹框显示错误原因，并重新核对账户和手机号 
                    else if(rspCode.equals("00606")){
                       showQuerengoudiansurecashDialog(ShouDianXiangQingActivity.this);
                    }
                        
                      
                        else {
                            if (!mRspMeg.equalsIgnoreCase("")) {
                                SystemUtil.displayToast(
                                        ShouDianXiangQingActivity.this, mRspMeg);
                            } else {
                                SystemUtil
                                        .displayToast(
                                                ShouDianXiangQingActivity.this,
                                                getString(R.string.shoufeixiangqing_goudianshibai));
                            }
                            if (bt_querengoudian != null) {
                                bt_querengoudian.setEnabled(true);
                            }
                        }
                    } else {
                    	
                    	
                        SystemUtil.displayToast(ShouDianXiangQingActivity.this, getString(R.string.dlg_goudianchenggong_title));
                        try {
                            // 获取押金余额并更新主界面
                            String banlance = Client.Parse_XML(
                                    GlobalParams.RETURN_DATA, "<BANLANCE>",
                                    "</BANLANCE>");
                            GlobalParams.CASH_AC_BAL = banlance;
                            Intent intentYaJinYuEr = new Intent(
                                    GlobalParams.UPDATE_YAJINYUER_ACTION);
                            sendBroadcast(intentYaJinYuEr);

                            InputStream in = new ByteArrayInputStream(
                                    GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                            mRspTicketXML = Client.Parse_XML(
                                    GlobalParams.RETURN_DATA, "<TICKET>",
                                    "</TICKET>");

                            billBuyResult = PULLParse_Shoudianshoufei_Query
                                    .getBillPayMap(in);
                            if (billBuyResult != null && billBuyResult.size() != 0) {
                                // 交易成功售电记录存数据库并更新主界面
                                JinRiShouDian mJinRiShouDian = new JinRiShouDian(
                                        SystemUtil.getCurrentDate(),
                                        keepDecimalPlaces(String.valueOf(mZhiFuRspJine)));
                                BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
                                        getApplicationContext(),
                                        JinRiShouDian.class);
                                baseDao.create(mJinRiShouDian);
                                Intent it = new Intent(
                                        GlobalParams.UPDATE_JINRISHOUDIAN_ACTION);
                                sendBroadcast(it);
                                // 购电成功
                                mTicket = mRspTicketXML;
                                currenttime = System.currentTimeMillis();
                                logMsg1("开打：");
                                new PrintTask()
                                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                SystemUtil
                                        .displayToast(
                                                ShouDianXiangQingActivity.this,
                                                getString(R.string.shoufeixiangqing_goudianshibai)
                                                        + mRspMeg
                                        );
                                if (bt_querengoudian != null) {
                                    bt_querengoudian.setEnabled(true);
                                }
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Exception = " + ex.toString());
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 扫描成功
                case 0x00:
                    if (mReadCardNoCallBack != null) {
                        if (GlobalParams.BARCODE_FORMAT.equalsIgnoreCase("QR_CODE")) {
                            mBandCardCardType = "4";// 二维码
                        } else {
                            mBandCardCardType = "5";// 条形码
                        }
                        mReadCardNoCallBack.readCardMessage(GlobalParams.QR_Info);
                    }
                    break;
                case 0x01: // 扫描失败
                    SystemUtil.displayToast(ShouDianXiangQingActivity.this,
                            R.string.scan_fail);
                    break;
            }
        }
    };

    private class GetDataAndTypeTask extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        String[] data = new String[2];
        String msg="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(dialog == null){
                dialog = new ProgressDialog(ShouDianXiangQingActivity.this);
            }
            dialog.setMessage(getString(R.string.update_progressdialog_message));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                Decode.open();
                data = Decode.readDataAndType(10000);
                if(data[1].equals("1")){
                    GlobalParams.CARD_TYPE = "5";// 条形码
                }else if (data[1].equals("2")) {
                    GlobalParams.CARD_TYPE = "4";// 二维码
                }
                msg = data[0];      //扫描到的数据
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                Decode.disconnect();
            }
            return  msg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(btn_img!=null){
            	btn_img.setEnabled(true);
            }
            dialog.dismiss();
            ReaderMonitor.startMonitor();
            if (result.length() > 0) {
                GlobalParams.QR_Info = result;
                Log.e(TAG, result);
                new BandCardTask(GlobalParams.QR_Info)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (btn_img != null) {
			btn_img.setEnabled(true);
		}
        ReaderMonitor.startMonitor();
        if (resultCode == -1) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Log.e(TAG, scanResult);

            if (scanResult.length() > 0) {

                GlobalParams.QR_Info = scanResult;
                Log.e(TAG, scanResult);
                new BandCardTask(GlobalParams.QR_Info)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }


    @Override
    protected void onPause() {
    	Log.e("ShouDianXiangQingActivity", "onPause");
        if (capture != null) {
            capture.Stop();
            isCloseScaner = true;
            capture = null;
            // GlobalParams.If_CloseFlashLight = true;
        }
        // try {
        // Thread.sleep(500);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
//        unregisterReceiver(mCardMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
    	Log.e("ShouDianXiangQingActivity", "onDestroy");
        super.onDestroy();
    }
	private void PowerTask(PowerParams powerParams){
		PowerParams []params = new PowerParams[1];
		params[0] = powerParams;
			PowerResult result = new PowerResult();
			try {

				result.atr = mReader.power(params[0].slotNum, params[0].action);

			} catch (Exception e) {

				result.e = e;
			}
	}

	private void SetProtocolTask(SetProtocolParams setProtocolParams) {
		SetProtocolParams[] params= new SetProtocolParams[1];
		params[0] = setProtocolParams;
			SetProtocolResult result = new SetProtocolResult();

			try {

				result.activeProtocol = mReader.setProtocol(params[0].slotNum, params[0].preferredProtocols);

			} catch (Exception e) {

				result.e = e;
			}
	}

	private void TransmitTask(TransmitParams paramsTransmit){
		TransmitParams []params = new TransmitParams[1];
		params[0] = paramsTransmit;
			TransmitProgress progress = null;
			byte[] command = null;
			byte[] response = null;
			int responseLength = 0;
			int foundIndex = 0;
			int startIndex = 0;

			do {

				// Find carriage return
				foundIndex = params[0].commandString.indexOf('\n', startIndex);
				if (foundIndex >= 0) {
					command = toByteArray(params[0].commandString.substring(startIndex, foundIndex));
				} else {
					command = toByteArray(params[0].commandString.substring(startIndex));
				}

				// Set next start index
				startIndex = foundIndex + 1;

				response = new byte[300];
				progress = new TransmitProgress();
				progress.controlCode = params[0].controlCode;
				try {

					if (params[0].controlCode < 0) {

						// Transmit APDU
						responseLength = mReader.transmit(params[0].slotNum, command, command.length, response,
								response.length);

					} else {

						// Transmit control command
						responseLength = mReader.control(params[0].slotNum, params[0].controlCode, command,
								command.length, response, response.length);
					}

					progress.command = command;
					progress.commandLength = command.length;
					progress.response = response;
					progress.responseLength = responseLength;
					progress.e = null;

				} catch (Exception e) {

					progress.command = null;
					progress.commandLength = 0;
					progress.response = null;
					progress.responseLength = 0;
					progress.e = e;
				}

				String Final=logBuffer(progress.response, progress.responseLength);
				 if(!Final.equals("9000") && !Final.equals("90FF")&& !Final.equals("9007")){
					 return;
				 }
			} while (foundIndex >= 0);

			String result = logBuffer(progress.response, progress.responseLength);
            if(result!=null){
          	  if(result.equals("9000")){
                  writeSucc=true;
                }else{
            	   writeSucc=false;
              }
          }else{
            writeSucc=false;
          }
        
	}

	private String logBuffer(byte[] buffer, int bufferLength) {

		String bufferString = "";

		for (int i = 0; i < bufferLength; i++) {

			String hexChar = Integer.toHexString(buffer[i] & 0xFF);
			if (hexChar.length() == 1) {
				hexChar = "0" + hexChar;
			}

			if (i % 16 == 0) {

				if (bufferString != "") {

					// logMsg1(bufferString);
					//bufferString = "";
				}
			}

			bufferString += hexChar.toUpperCase();
		}

		if (bufferString != "") {
//			 logMsg1("读卡"+bufferString);
			String ss = bufferString.substring(0, bufferString.length()-4);
			
			
//			return "{   \"read\" : {\"offset\" : [32],   \"value\" : [\""+ss+"\"] }}";
			return bufferString;
		}
		return "";
	}
	
	  private byte[] toByteArray(String hexString) {

	        int hexStringLength = hexString.length();
	        byte[] byteArray = null;
	        int count = 0;
	        char c;
	        int i;

	        // Count number of hex characters
	        for (i = 0; i < hexStringLength; i++) {

	            c = hexString.charAt(i);
	            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
	                    && c <= 'f') {
	                count++;
	            }
	        }

	        byteArray = new byte[(count + 1) / 2];
	        boolean first = true;
	        int len = 0;
	        int value;
	        for (i = 0; i < hexStringLength; i++) {

	            c = hexString.charAt(i);
	            if (c >= '0' && c <= '9') {
	                value = c - '0';
	            } else if (c >= 'A' && c <= 'F') {
	                value = c - 'A' + 10;
	            } else if (c >= 'a' && c <= 'f') {
	                value = c - 'a' + 10;
	            } else {
	                value = -1;
	            }

	            if (value >= 0) {

	                if (first) {

	                    byteArray[len] = (byte) (value << 4);

	                } else {

	                    byteArray[len] |= value;
	                    len++;
	                }

	                first = !first;
	            }
	        }

	        return byteArray;
	    }
	    private void logMsg1(String msg) {
	    	if(ISDEBUG){
		    	//AlertDialog.Builder builder;
		    	AlertDialog.Builder  builder = new AlertDialog.Builder (this);
		   	  builder.setMessage(msg);
		   	  builder.setTitle("提示");
		   	 builder.setPositiveButton("OK",
		             new DialogInterface.OnClickListener() {
		   	   @Override
		   	   public void onClick(DialogInterface dialog, int which) {
		   		   	dialog.dismiss();
		   	   }
		   	  });
		   	  AlertDialog x = builder.create();
		   	  x.show();
	    	}
	  }
	    
	    private String toHexString(int i) {

	        String hexString = Integer.toHexString(i);
	        if (hexString.length() % 2 != 0) {
	            hexString = "0" + hexString;
	        }

	        return hexString.toUpperCase();
	    }
	    
	    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

	        public void onReceive(Context context, Intent intent) {

	            String action = intent.getAction();

	            if (ACTION_USB_PERMISSION.equals(action)) {

	                synchronized (this) {

	                    UsbDevice device = (UsbDevice) intent
	                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);

	                    if (intent.getBooleanExtra(
	                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

	                        if (device != null) {

	                            // Open reader
	                            logMsg1("Opening reader: " + device.getDeviceName()
	                                    + "...");
	                            OpenTask(device);
	                        }

	                    } else {

	                        logMsg1("Permission denied for device "
	                                + device.getDeviceName());
	                    }
	                }

	            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

	                synchronized (this) {

	                    UsbDevice device = (UsbDevice) intent
	                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);

	                    if (device != null && device.equals(mReader.getDevice())) {

	                        // Close reader
	                        logMsg1("Closing reader...");
//	                        new CloseTask().execute();
	                    }
	                }
	            }
	        }
	    };
	    private void OpenTask(UsbDevice device) {
	    	UsbDevice params[]=new UsbDevice[1];
	    	params[0] = device;

	            Exception result = null;

	            try {

	                mReader.open(params[0]);

	            } catch (Exception e) {

	                result = e;
	            }
	            if (result != null) {

	            } else {

	                int numSlots = mReader.getNumSlots();
	                // Remove all control codes
	                mFeatures.clear();

	            }
	    }
	    private class CloseTask extends AsyncTask<Void, Void, Void> {

	        @Override
	        protected Void doInBackground(Void... params) {

	            mReader.close();
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	         
	        }

	    }
	    public String getHex(int a,boolean b){
	    	String result="";
	    	if(b){
	    		result=Integer.toHexString(a);
	    		  if(result.length()==1){
	    			  result="0"+result;
	    		  }
	    	}else{
	    		result=Integer.toHexString(a);
	    		  for(int i=result.length();i<4;i++){
	    			  result="0"+result;
	    		  }
	    	}
	    	return result;
	    	
	    }
}
