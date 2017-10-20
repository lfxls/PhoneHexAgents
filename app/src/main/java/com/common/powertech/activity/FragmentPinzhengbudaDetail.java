package com.common.powertech.activity;

import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.common.powertech.bussiness.*;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.*;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullUpListView;

import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.hardware.usb.UsbDevice;
import android.os.SystemClock;
import android.widget.Spinner;
import com.acs.smartcard.Reader;
import com.common.powertech.ItemListActivity.ReadCardMessageCallBack;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.acs.smartcard.Features;
import com.acs.smartcard.PinProperties;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.acs.smartcard.TlvProperties;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
//蓝牙打印机
import com.gprinter.sample.MainActivity;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomProgressDialog;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import printUtils.gprinter;

public class FragmentPinzhengbudaDetail extends Fragment implements OnClickListener {

    private PullUpListView lv;
    private List<BillReprint_Class> rplist;
    String inputCond = "";
    String queryType = "";
    protected static boolean isRun = false;// 业务处理中，按取消件无效
    private String requestXML;
    // 设置下拉刷新
    private PullRefreshLayout mPullLayout;
    private TextView mActionText;
    private TextView mTimeText;
    private View mProgress;
    private View mActionImage;
    private Animation mRotateUpAnimation;
    private Animation mRotateDownAnimation;
    private boolean mInRefreshing = false;

    private boolean mInLoading = false;
    private int mCurrentPage = 1;
    CustomProgressDialog progressDialog;
    private ArrayList<HashMap<String, Object>> arrayList;
    private Printer mPrinter = new Printer();
    private boolean isbtnClick = false;
    private String mIcJsonRes = "";// 写卡信息
    private String ticket = "";//小票信息
    private Button btn;     //打印与写卡的按钮
    private String mRmg = "";
    private String mOffset = "";
    private String mValue = "";
    private int count = 0;
    private int ret;
    private ItemListActivity mActivity;
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
    private gprinter gprinter = new gprinter();
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;
    private AlertDialog mShowtokenDialog;
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

	private class TransmitProgress {

		public int controlCode;
		public byte[] command;
		public int commandLength;
		public byte[] response;
		public int responseLength;
		public Exception e;
	}
	private class verifyCardParams {

		public int slotNum;
		public int controlCode;
		public String commandString;
		public String verifyPara;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (ItemListActivity)getActivity();
        View rootView = inflater.inflate(R.layout.fragment_pinzhengbuda_detail, container, false);

        lv = (PullUpListView) rootView.findViewById(R.id.rrlist);
        lv.initBottomView();
        lv.setMyPullUpListViewCallBack(new PullUpListView.MyPullUpListViewCallBack() {

            @Override
            public void scrollBottomState() {
                // 拉到底部继续向上拉动加载
                if (!mInLoading) {

                    if (count == 3) {
                        mCurrentPage += 1;
                    }
                    Request_Reprint_Query.setContext(mActivity);
                    if ("1".equals(queryType)) {
                        Request_Reprint_Query.setUserNum(inputCond);
                    } else if ("2".equals(queryType)) {
                        // 按表号查询
                        Request_Reprint_Query.setMeterNum(inputCond);
                    }
                    Request_Reprint_Query.setPageNum(String.valueOf(mCurrentPage));
                    //Request_Reprint_Query.setNumPerPage("5");
                    String APIName = "PBillReprintQuery";
                    String data = Request_Reprint_Query.getRequsetXML();
                    Client.SendData(APIName, data, handler);
                    mInLoading = true;
                }
            }
        });


        mPullLayout = (PullRefreshLayout) rootView
                .findViewById(R.id.pull_container);
        mPullLayout.setOnActionPullListener(new PullRefreshLayout.OnPullListener() {

            @Override
            public void onSnapToTop() {
                if (!mInRefreshing) {
                    showRefreshModel();
                    // new RefreshDataTask().execute();
                    // 拖到顶部下拉刷新上一页
                    if (mCurrentPage > 1) {

                        mCurrentPage -= 1;
                        Request_Reprint_Query.setContext(mActivity);
                        if ("1".equals(queryType)) {
                            Request_Reprint_Query.setUserNum(inputCond);
                        } else if ("2".equals(queryType)) {
                            // 按表号查询
                            Request_Reprint_Query.setMeterNum(inputCond);
                        }
                        Request_Reprint_Query.setPageNum(String.valueOf(mCurrentPage));
                        //Request_Reprint_Query.setNumPerPage("5");
                        String APIName = "PBillReprintQuery";
                        String data = Request_Reprint_Query.getRequsetXML();
                        Client.SendData(APIName, data, handler);

                    } else {
                        if (mInRefreshing) {
                            removeRefreshModel();
                        }
                    }
                }
            }

            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {

            }
        });

        mPullLayout.setOnPullStateChangeListener(new PullRefreshLayout.OnPullStateListener() {

            @Override
            public void onPullOut() {
                if (!mInRefreshing) {
                    mActionText.setText(R.string.note_pull_refresh);
                    mActionImage.clearAnimation();
                    mActionImage.startAnimation(mRotateUpAnimation);
                }
            }

            @Override
            public void onPullIn() {
                if (!mInRefreshing) {
                    mActionText.setText(R.string.note_pull_down);
                    mActionImage.clearAnimation();
                    mActionImage.startAnimation(mRotateDownAnimation);
                }
            }
        });

        mRotateUpAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.rotate_up);
        mRotateDownAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.rotate_down);

        mProgress = (View) rootView.findViewById(android.R.id.progress);
        mActionImage = (View) rootView.findViewById(android.R.id.icon);
        mActionText = (TextView) rootView.findViewById(R.id.pull_note);
        mTimeText = (TextView) rootView.findViewById(R.id.refresh_time);

        mTimeText.setText(R.string.note_not_update);


        inputCond = getArguments().getString("inputCond");
        queryType = getArguments().getString("queryType");

        //new ReprintQueryTask(mActivity).execute();
        progressDialog = CustomProgressDialog.createProgressDialog(
                mActivity, GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        Toast.makeText(mActivity,
                                getString(R.string.progress_timeout),
                                Toast.LENGTH_LONG).show();
                        if (dialog != null
                                && (!mActivity.isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }
                }
        );
        progressDialog.setTitle(getString(R.string.str_pingzhengbuda));
        progressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
//        progressDialog.setIndeterminate(false);

        // 是否可以按下退回键取消
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Disable_Key.EnableTestMode("true", mActivity);
        // 按用户查询
        String APIName = "PBillReprintQuery";
        Request_Reprint_Query.setContext(mActivity);

        if ("1".equals(queryType)) {
            Request_Reprint_Query.setUserNum(inputCond);
            Request_Reprint_Query.setMeterNum("");
        } else if ("2".equals(queryType)) {
            // 按表号查询
            Request_Reprint_Query.setMeterNum(inputCond);
            Request_Reprint_Query.setUserNum("");
        }
        Request_Reprint_Query.setPageNum("1");
        Request_Reprint_Query.setNumPerPage("3");
        requestXML = Request_Reprint_Query.getRequsetXML();
        Client.SendData(APIName, requestXML, handler);

//        if(!ReaderMonitor.isStarted()){
//            ReaderMonitor.setContext(mActivity);
//            ReaderMonitor.startMonitor();
//        }
	     // Get USB manager
        mManager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);

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
        
        mPermissionIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        gprinter.nGpService = null;
        connection();
//        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        return rootView;
    }
    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(mActivity, GpPrintService.class);
        mActivity.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }
    public class PrinterServiceConnection implements ServiceConnection {
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

                    Toast.makeText(mActivity, "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                	int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status != GpCom.STATE_NO_ERR) {
                        Toast.makeText(mActivity, "printer  not connect", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status != GpCom.STATE_NO_ERR) {
                        Toast.makeText(mActivity, "printer not connect", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
//		FragmentTwo fTwo = new FragmentTwo();
//		FragmentManager fm = getFragmentManager();
//		FragmentTransaction tx = fm.beginTransaction();
//		tx.replace(R.id.id_content, fTwo, "TWO");
//		tx.addToBackStack(null);
//		tx.commit();

    }

    //凭证补打查询列表
    private ArrayList<HashMap<String, Object>> RP_getData() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        /**为动态数组添加数据*/
        for (int i = 0; i < rplist.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String Temp;
            Temp = rplist.get(i).Get_PRDORDNO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv1", Temp);

            Temp = rplist.get(i).Get_ORDAMT();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv2", Temp);

            Temp = rplist.get(i).Get_ORDERTIME();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv3", Temp);

            Temp = rplist.get(i).Get_BIZ_TYPE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv4", Temp);


            Temp = rplist.get(i).Get_ELEN_ID();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv5", Temp);


            Temp = rplist.get(i).Get_USER_NO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv6", Temp);

            Temp = rplist.get(i).Get_METER_NO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv7", Temp);


            Temp = rplist.get(i).Get_TOKEN();
            if (Temp == null) {
                Temp = "";
            }
            Temp = Temp.replace('|', '\n').replaceAll("(.{4})", "$1 ");
            map.put("tv8", Temp);

            if (GlobalParams.IC_FLAG.equalsIgnoreCase("1")) {
                map.put("btn", getString(R.string.pinzhengbuda_btn_xk));
            } else {
                map.put("btn", getString(R.string.pinzhengbuda_btn_bd));
            }
            listItem.add(map);
        }
        //多加一行 用于显示 上一页 下一页按钮
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tv1", "");
        listItem.add(map);

        return listItem;
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        /**
         * 构造函数
         */
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return rplist.size();//返回数组的长度

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            TextView t1, t2, t3, t4, t5, t6, t7, tv_token, tv_num;
            if (position == rplist.size()) {
                convertView = mInflater.inflate(R.layout.list_blank_item, null);
            } else {
                if( !GlobalParams.LANGUAGE.equalsIgnoreCase("zh") && GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")){
                    convertView = mInflater.inflate(R.layout.list_reprint_en, null);
                }else{
                    convertView = mInflater.inflate(R.layout.list_reprint, null);
                }

                LinearLayout layout_token = (LinearLayout) convertView.findViewById(R.id.layout_token);
                /**得到各个控件的对象*/
                t1 = (TextView) convertView.findViewById(R.id.tv12);
                t2 = (TextView) convertView.findViewById(R.id.tv14);
                t3 = (TextView) convertView.findViewById(R.id.tv22);
                t4 = (TextView) convertView.findViewById(R.id.tv24);
                t5 = (TextView) convertView.findViewById(R.id.tv32);
                tv_num = (TextView) convertView.findViewById(R.id.tv33);
                if (queryType.equals("1")) {
                    tv_num.setText(getString(R.string.str_huhao));
                }
                t6 = (TextView) convertView.findViewById(R.id.tv34);
                tv_token = (TextView) convertView.findViewById(R.id.tv41);
                t7 = (TextView) convertView.findViewById(R.id.tv42);
                btn = (Button) convertView.findViewById(R.id.btn);

                arrayList = RP_getData();
                t1.setText(arrayList.get(position).get("tv1").toString());
                t2.setText(arrayList.get(position).get("tv2").toString());
                t3.setText(arrayList.get(position).get("tv3").toString());
                String temp = arrayList.get(position).get("tv4").toString();
                if (temp.equals("D2")) {
                    temp = getString(R.string.str_yufufei);
                } else if (temp.equals("D4")) {
                    temp = getString(R.string.str_houfufei);
                } else if (temp.equals("D5")) {
                    temp = getString(R.string.str_chongzheng);
                }
                t4.setText(temp);
                t5.setText(arrayList.get(position).get("tv5").toString());
                if (queryType.equals(1)) {
                    t6.setText(arrayList.get(position).get("tv6").toString());
                } else {
                    t6.setText(arrayList.get(position).get("tv7").toString());
                }

                String token = arrayList.get(position).get("tv8").toString();
                if (token.length() > 0) {
                    t7.setText(token);
                    int tokennum = token.length();
                    if(tokennum>=80){
                    	t7.setText(token.substring(0, 77).concat("..."));
                    	t7.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) { // 显示token
                            	String tokenshow = arrayList.get(position).get("tv8").toString();
                            	showMoretokenDialog(arrayList.get(position).get("tv8").toString());
                            }
                        });
                    }
                } else {
                    layout_token.setVisibility(View.GONE);
                }
                btn.setText(arrayList.get(position).get("btn").toString());
                //获取打印数据
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 写卡
                        if(isbtnClick){
                            return;
                        }
//                        isbtnClick = true;
                        Log.v("MyListViewBase", "你点击了按钮" + position);//打印Button的点击信息
                        if (GlobalParams.IC_FLAG.equalsIgnoreCase("1")) {//0:不写卡 1：写卡

                            String APIName = "PBillReprint";
                            Request_Reprint_Confirm.setContext(mActivity);
                            Request_Reprint_Confirm.setPrdordNo(arrayList.get(position).get("tv1").toString());
                            Request_Reprint_Confirm.setIC_Type(GlobalParams.CARD_TYPE);
                            Request_Reprint_Confirm.setJson_Str(GlobalParams.IC_JSON_REQ);
                            requestXML = Request_Reprint_Confirm.getRequsetXML();
                            Client.SendData(APIName, requestXML, handler);
                            if (progressDialog == null) {
                                createDialog();
                            }
                            progressDialog.setTitle(getString(R.string.str_pingzhengdayin));
                            progressDialog.show();
                        } else {

                            String APIName = "PBillReprint";
                            Request_Reprint_Confirm.setContext(mActivity);
                            Request_Reprint_Confirm.setPrdordNo(arrayList.get(position).get("tv1").toString());
                            Request_Reprint_Confirm.setIC_Type("");
                            Request_Reprint_Confirm.setJson_Str("");
                            requestXML = Request_Reprint_Confirm.getRequsetXML();
                            Client.SendData(APIName, requestXML, handler);
                            if (progressDialog == null) {
                                createDialog();
                            }
                            progressDialog.setTitle(getString(R.string.str_pingzhengdayin));
                            progressDialog.show();
                        }
                    }
                });
            }
            return convertView;
        }

    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            //处理消息
            switch (msg.what) {

                case 0:
                    //联网失败
                    try {
                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                            // 没有加载到数据，页码返回到当前页
                            Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mInRefreshing) {
                        mCurrentPage += 1;
                    } else if (mInLoading) {
                        if (count == 3) {
                            mCurrentPage -= 1;
                        }
                    } else {
                        turnBack();
                    }
                    break;

                case 1:     //凭证查询

//                    GlobalParams.RETURN_DATA = "<ROOT><TOP><IMEI>012813005288305</IMEI><SESSION_ID>vF3Xc8nylN0VfeFDCT8E</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2016-03-02 09:48:18</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><TOLCNT>92</TOLCNT><STUDENT><PRDORDNO>D016022600148238</PRDORDNO><ORDERTIME>2016-02-26 09:41:35</ORDERTIME><ORDAMT>17269.00</ORDAMT><BIZ_TYPE>D4</BIZ_TYPE><USER_NO>1031204430</USER_NO><METER_NO>1031204430</METER_NO><ELEN_ID>Demo Power Company</ELEN_ID></STUDENT><STUDENT><PRDORDNO>D016022600148238D016022600148238D016022600148238</PRDORDNO><ORDERTIME>2016-02-26 09:41:352016-02-26 09:41:352016-02-26 09:41:35</ORDERTIME><ORDAMT>17269.0017269.0017269.00</ORDAMT><BIZ_TYPE>D4</BIZ_TYPE><USER_NO>103120443010312044301031204430</USER_NO><METER_NO>103120443010312044301031204430</METER_NO><ELEN_ID>Demo Power CompanyDemo Power CompanyDemo Power Company</ELEN_ID><TOKEN>5448 1361 4871 8658 6278 5477 5664 7834 2575 1454 0958 0819 2582 5970 6825</TOKEN><TOKEN>5448 1361 4871 8658 6278 5477 5664 7834 2575 1454 0958 0819 2582 5970 6825</TOKEN></STUDENT></BODY></ROOT>";
                    //联网成功
                    try {

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                        }

                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        List<BillReprint_Class> tmp = PULLParse_Reprint_Query.getRPList(in);

                        String errcode = PULLParse_Reprint_Query.getRspcod();
                        if (errcode.equals("00000")) {
                            //成功后才保存户号与表号
                            String number = inputCond;
                            String regex = "(.{4})";
                            number = number.replaceAll(regex, "$1 ");
                            List<String> numberList = new ArrayList<String>();
                            if (Preferences.getComplexDataInPreference(mActivity,
                                    Preferences.KEY_MeterOrUser_No, "0") != null
                                    && !Preferences
                                    .getComplexDataInPreference(mActivity,
                                            Preferences.KEY_MeterOrUser_No, "0")
                                    .toString().equalsIgnoreCase("0")) {
                                numberList = (List<String>) Preferences
                                        .getComplexDataInPreference(mActivity,
                                                Preferences.KEY_MeterOrUser_No, "0");
                            }
                            if (numberList.size() > 0) {
                                boolean same = false;
                                for (int i = 0; i < numberList.size(); i++) {
                                    String str = numberList.get(i);
                                    if (str.equalsIgnoreCase(number)) {
                                        same = true;
                                    }
                                }
                                if (!same) {
                                    numberList.add(number);
                                }
                            } else {
                                numberList.add(number);
                            }
                            Preferences.storeComplexDataInPreference(mActivity,
                                    Preferences.KEY_MeterOrUser_No, numberList);


                            count = tmp.size();
                            if (count > 0) {
                                //当前页面更新
                                ItemListActivity activity = (ItemListActivity) mActivity;
                                activity.setShortCutsKeyDownCallBack(new ItemListActivity.ShortCutsKeyDownCallBack() {

                                    @Override
                                    public void keyValue(int selectKey) {
                                    }
                                });
                                rplist = tmp;
                                MyAdapter myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                            } else {
                                Toast.makeText(mActivity, getString(R.string.str_meiyoujilu), Toast.LENGTH_LONG).show();
                                if (mInRefreshing) {
                                    mCurrentPage += 1;
                                } else if (mInLoading) {
                                    if (count == 3) {
                                        mCurrentPage -= 1;
                                    }
                                } else {
                                    turnBack();
                                }

                            }
                        } else {

                            Toast.makeText(mActivity, PULLParse_Reprint_Query.getRspmsg(), Toast.LENGTH_LONG).show();
                            //服务器返回系统超时，返回到登录页面
                            if (errcode.equals("00011")) {
                            	SystemUtil.setGlobalParamsToNull(mActivity);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                            	Intent intent = new Intent(mActivity, LoginActivity.class);
                                mActivity.startActivity(intent);
                            }
                            if (mInRefreshing) {
                                mCurrentPage += 1;
                            } else if (mInLoading) {
                                if (count == 3) {
                                    mCurrentPage -= 1;
                                }
                            } else {
                                turnBack();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:

                    try {

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                        }

                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        String code = Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPCOD>", "</RSPCOD>");
                        if (code.equals("00000")) {
                            String type = Client.Parse_XML(GlobalParams.RETURN_DATA, "<TYPE_T>", "</TYPE_T>");
                            ticket = Client.Parse_XML(GlobalParams.RETURN_DATA, "<TICKET>", "</TICKET>");
                            mIcJsonRes = "";
                            mIcJsonRes = Client.Parse_XML(GlobalParams.RETURN_DATA, "<IC_JSON_RES>", "</IC_JSON_RES>");
                            if (ticket.length() > 0) {
                                ticket = "<TICKET>" + ticket + "</TICKET>";
                                //TODO PRINT
                                new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }

                          

                        } else {
                            Toast.makeText(mActivity, Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPMSG>", "</RSPMSG>"), Toast.LENGTH_LONG).show();
                            //服务器返回系统超时，返回到登录页面
                            if (code.equals("00011")) {
                            	SystemUtil.setGlobalParamsToNull(mActivity);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                            	Intent intent = new Intent(mActivity, LoginActivity.class);
                                mActivity.startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                default:

                    break;
            }

            if (mInLoading) {
                lv.removeFooterView();
            }
            // 若是下拉更新完
            removeRefreshModel();
            mInLoading = false;
        }
    };
    
    public void showMoretokenDialog(String token) {
    	//TODO
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
        LayoutInflater inflater = getActivity().getLayoutInflater();  
        View view = inflater.inflate(R.layout.dialog_shoudianxiangqing_moretoken, null);  
        builder.setView(view);

        mShowtokenDialog = builder.create();
        mShowtokenDialog.setCancelable(true);
        
        TextView mMoretoken = (TextView) view
                .findViewById(R.id.more_token);
        mMoretoken.setText(token);
        mShowtokenDialog.show();

    }

    private void showRefreshModel() {
        mInRefreshing = true;
        mPullLayout.setEnableStopInActionView(true);
        mActionImage.clearAnimation();
        mActionImage.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mActionText.setText(R.string.note_pull_loading);
    }

    private void removeRefreshModel() {
        if (mInRefreshing) {
            mInRefreshing = false;
            mPullLayout.setEnableStopInActionView(false);
            mPullLayout.hideActionView();
            mActionImage.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);

            if (mPullLayout.isPullOut()) {
                mActionText.setText(R.string.note_pull_refresh);
                mActionImage.clearAnimation();
                mActionImage.startAnimation(mRotateUpAnimation);
            } else {
                mActionText.setText(R.string.note_pull_down);
            }

            mTimeText.setText(getString(
                    R.string.note_update_at,
                    DateFormat.getTimeFormat(mActivity).format(
                            new Date(System.currentTimeMillis()))
            ));
        }
    }


    private void turnBack() {
        mActivity.setDefaultFragment("2");
    }

    @Override
    public void onResume() {
        super.onResume();
        if ("" == inputCond && "" == queryType) {
            turnBack();
        }
        mActivity.setOnBackPressedListener(new ItemListActivity.OnBackPressedListener() {
            @Override
            public void onPressed() {
                    turnBack();
            }
        });
    }


    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(
                mActivity,
                60*1000,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        SystemUtil.displayToast(mActivity,
                                R.string.progress_timeout);
                        if (dialog != null
                                && (!mActivity
                                .isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }

    // 写IC卡异步任务
    private class WriteICCardTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {

            createDialog();
            progressDialog
                    .setTitle(getString(R.string.progress_tishi_title));
            progressDialog.setMessage(getString(R.string.writingcard));
            // 设置进度条是否不明确
//            progressDialog.setIndeterminate(false);

            // 是否可以按下退回键取消
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e("FragmentPinzhengbudaDetail", "IC卡检测开始....");
            // 解析写卡数据
            if (mIcJsonRes.length() == 0) {
//                mRmg = getString(R.string.writecard_nonemessage);
                Log.e("FragmentPinzhengbudaDetail", "未有写卡数据");
                return 0;
            }
//            HashMap<String, List<String>> mJsonMap = parsingJsonData4Write(mIcJsonRes);
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
                // 校验比较
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
//                    //TODO TEST
//                     pw=new byte[]{(byte) 0xff,(byte) 0xff};
                    Log.e("FragmentPinzhengbudaDetail", "密码 = " + passwd.get(0));

                    readCardPassVerify(passwd.get(0));
                    if (isPass) {// 密码正确，开始写卡
                    	isPass=false;
                        String writeOffset = write.get(0);
                        String writeValue = write.get(1);
                        
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
                        //数据备份
                        int Wstart=0;
                        int Wlength=1024;
                        readCardBackup(Wstart,Wlength);
                      //最后一条为新密码数据 后写
                        String tempNewPassData = mWriteCardValueList.get(mWriteCardValueList.size()-1);
                        int tempNewPassOffset = mWriteOffsetList.get(mWriteOffsetList.size()-1);
                        int tempNewPassLen = mWriteValueLenList.get(mWriteValueLenList.size()-1);
                        mWriteCardValueList.remove(mWriteCardValueList.size()-1);
                        mWriteOffsetList.remove(mWriteOffsetList.size()-1);
                        mWriteValueLenList.remove(mWriteValueLenList.size()-1);
                        // 写卡
                        writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
                        if(!writeSucc){
                        	Log.e("FragmentPinzhengbudaDetail", "写卡失败");
                        	 return 0;
                        }
                        writeSucc=false;
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
	                                
	                                if (!writeSucc) {
	                                    Log.e("FragmentPinzhengbudaDetail", "恢复数据失败！");
	                                }
	                                writeSucc=false;
	                                backupData="";
	                        		return 0;
	                        	}
	                        	writeCardMessage(mWriteCardValueList,passwd.get(0),mWriteOffsetList,mWriteValueLenList);
	                        }else{
	                        	writeSucc=false;
	                        }
                        }
                        // 写卡成功，设置新密码
                        Log.e("FragmentPinzhengbudaDetail", "新密码 = " + newPasswd.get(0));

//                      //开始位置
//                        int Wstartpwd=Integer.parseInt(writeOffset.split(",")[1].replaceAll("[^0-9]",""));
//                        //写卡字节长度
//                        int Wlengthpwd=writeValue.split(",")[1].replaceAll("[^0-9a-fA-F]","").length()/2;
//                        String writeValue2=writeValue.split(",")[1].replaceAll("[^0-9a-fA-F]","");
                     // 写入新密码
//                        writeCardMessage(writeValue2,passwd.get(0),Wstartpwd,Wlengthpwd);
                        //开始位置
                        int Wstartpwd2=1022;
                        //写卡字节长度
                        int Wlengthpwd2=2;
                        String writeValue3=newPasswd.get(0);
//                        String ICARDSTRUPass[]={writeValue2,writeValue3};
//                        int WstartPass[]={Wstartpwd,Wstartpwd2};
//                        int WlengthPass[]={Wlengthpwd,Wlengthpwd2};
//                        writeCardPassward(ICARDSTRUPass,passwd.get(0), WstartPass,WlengthPass);
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
                            Log.e("FragmentPinzhengbudaDetail", "修改密码成功！");
                            writeSucc=false;
                            backupData="";
                            return 1;
                        } else {
                            Log.e("FragmentPinzhengbudaDetail", "修改密码失败！");
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
                            if (!writeSucc) {
                                Log.e("FragmentPinzhengbudaDetail", "恢复数据失败！");
                            }
                            writeSucc=false;
                            backupData="";
                            // 恢复数据
                            return 0;
                        }
                    } else {
                        // 密码错误
                        Log.e("FragmentPinzhengbudaDetail", "IC卡密码错误，请检查!");
                        mRmg = getString(R.string.writecard_iccardpw_fail);
                        backupData="";
                        return 0;
                    }
                } else {// 校验失败
                    Log.e("FragmentPinzhengbudaDetail", "校验失败!");
                    mRmg = getString(R.string.writecard_verify_fail);
                    backupData="";
                    return 0;
                }
            } else if (GlobalParams.CARD_TYPE.equalsIgnoreCase("2")) {
                // 4442
            	value=value.replaceAll("[^0-9a-fA-F]","");
            	int len=value.length()/2;
            	String verOffset=Integer.toHexString(Integer.parseInt(offset.replaceAll("[^0-9]","")));
            	String verValue=Integer.toHexString(len);
            	if(len<16){
            		verValue="0"+verValue;
            	}
            	readCardVerify(verOffset,verValue,value);
                if (isVerifyCard) {// 校验成功
                	isVerifyCard=false;
                    Log.e("FragmentPinzhengbudaDetail", "密码 = " + passwd.get(0));
                    byte[] pw = StringUtil.str2BCD(passwd.get(0));
                    readCardPassVerify(passwd.get(0));
                    if (isPass) {// 密码正确，开始写卡
                    	isPass=false;
                        String writeOffset = write.get(0);
                        String writeValue = write.get(1);
//                        //开始位置
//                        int Wstart=Integer.parseInt(writeOffset.split(",")[0].replaceAll("[^0-9]",""));
//                        //写卡字节长度
//                        int Wlength=writeValue.split(",")[0].replaceAll("[^0-9a-fA-F]","").length()/2;
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
                        if(writeSucc){
                        	writeSucc=false;
                        	return 1;
                        }else{
                        	return 0;
                        }
                        
                    } else {
                        // 密码错误
                        Log.e("FragmentPinzhengbudaDetail", "IC卡密码错误，请检查!");
                        mRmg = getString(R.string.writecard_iccardpw_fail);
//                        ReaderMonitor.reset();
                        return 0;
                    }
                } else {// 校验失败
                    Log.e("FragmentPinzhengbudaDetail", "校验失败!");
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

    				// If device name is found
    				if (deviceName.equals(device.getDeviceName())) {

    					if (mManager.hasPermission(device)) { // ��ȡȨ��

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
		//    							String cardInfo = "ffa400000106" + changeLine + "ffb00020E0";
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
			SystemClock.sleep(400);
			CloseTask();
            Log.e("FragmentPinzhengbudaDetail", "onPostExecute result = " + result);
            if (progressDialog != null
                    && (!mActivity.isFinishing())) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (result == 1) {
//                SystemUtil.displayToast(mActivity,
//                        R.string.shoudianxiangqing_xiekachenggong);
                Toast.makeText(mActivity, R.string.shoudianxiangqing_xiekachenggong,  Toast.LENGTH_LONG).show();
            } else {
                SystemUtil.displayToast(mActivity,
                        getString(R.string.shoudianxiangqing_xiekashibai)
                                + mRmg
                );
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
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
  						
  						int slotNum = 0;//
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
//  	            	  if(resultStr.substring(resultStr.length()-4, resultStr.length()).equals("9000")){
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

//  				logMsg1("Command:");
//  				logBuffer(progress[0].command, progress[0].commandLength);

//  				logMsg1("Response:");
//  				logBuffer(progress[0].response, progress[0].responseLength);

  				if (progress[0].response != null && progress[0].responseLength > 0) {

  					int controlCode;
  					int i;

  					// Show control codes for IOCTL_GET_FEATURE_REQUEST
  					if (progress[0].controlCode == Reader.IOCTL_GET_FEATURE_REQUEST) {

  						mFeatures.fromByteArray(progress[0].response, progress[0].responseLength);

//  						logMsg1("Features:");
  						for (i = Features.FEATURE_VERIFY_PIN_START; i <= Features.FEATURE_CCID_ESC_COMMAND; i++) {

  							controlCode = mFeatures.getControlCode(i);
  							if (controlCode >= 0) {
  								logMsg1("Control Code: " + controlCode + " (" + featureStrings[i] + ")");
  							}
  						}

  						// Enable buttons if features are supported
//  						mVerifyPinButton.setEnabled(mFeatures.getControlCode(Features.FEATURE_VERIFY_PIN_DIRECT) >= 0);
//  						mModifyPinButton.setEnabled(mFeatures.getControlCode(Features.FEATURE_MODIFY_PIN_DIRECT) >= 0);
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
//  								logMsg1(propertyStrings[i] + ": " + toHexString((Integer) property));
  							} else if (property instanceof String) {
//  								logMsg1(propertyStrings[i] + ": " + property);
  							}
  						}
  					}
  				}
  			}
  		}
          @Override  
          protected void onPostExecute(String result) {  
//              textView.setText("异步操作执行结束" + result); 
              logMsg1("doIN后:"+result);
              if(result.equals("")){
              	logMsg1("dukashibai");
              	return;
              }
              		logMsg1("读卡成功！");
              		backupData=result;
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
//						SystemClock.sleep(100);

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
    							preferredProtocols |= Reader.PROTOCOL_T0;

    							preferredProtocols |= Reader.PROTOCOL_T1;
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
//    							passWord = "b62307";
//    							ICARDSTR = "68013201101014400199991003516757121251977629542048450166221875413638800852819152802064FFFFFFFFFFFFFFFFFFFFA616FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA";
//    							String cardInfo = "ffa400000106" + changeLine + "ff20000003" + passWord
//    									+ changeLine + "ffd00020E0" + ICARDSTR;
//    							String cardInfo = "ffa400000106" + changeLine + "ffb00020E0";
    							
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
    							SystemClock.sleep(200);
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
//    							passWord = "b62307";
//    							ICARDSTR = "68013201101014400199991003516757121251977629542048450166221875413638800852819152802064FFFFFFFFFFFFFFFFFFFFA616FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA";
//    							String cardInfo = "ffa400000106" + changeLine + "ff20000003" + passWord
//    									+ changeLine + "ffd00020E0" + ICARDSTR;
//    							String cardInfo = "ffa400000106" + changeLine + "ffb00020E0";
//    							passward="b62307";
    							String cardInfo="";
    							if(GlobalParams.CARD_TYPE=="2"){
    								cardInfo = "ffa400000106" + changeLine +"ff20000003" + passward ;
    							}else if(GlobalParams.CARD_TYPE=="1"){
    								cardInfo = "ffa400000105" + changeLine +"ff20000002" + passward ;
    							}
    							paramsTransmit.commandString = cardInfo;
    							TransmitTask2(paramsTransmit);// �ύ
    							SystemClock.sleep(300);
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

    			} while (foundIndex >= 0);

    			String result = logBuffer(progress.response, progress.responseLength);

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
            Log.e("FragmentPinzhengbudaDetail", "offset = " + mOffset);
            mValue = jso1.getString("value"); // 得到["12345","332200777"]
            Log.e("FragmentPinzhengbudaDetail", "value = " + mValue);
            verifyList.add(mOffset);
            verifyList.add(mValue);
            resultMap.put("verify", verifyList);

            String passwd = jso.getString("passwd"); // 得到 "123456789"
            passwdList.add(passwd);
            resultMap.put("passwd", passwdList);

            String newPasswd = jso.getString("newpasswd"); // 得到新密码
            Log.e("FragmentPinzhengbudaDetail", "新密码 = " + newPasswd);
            newPasswdList.add(newPasswd);
            resultMap.put("newpasswd", newPasswdList);

            String write = jso.getString("write"); // 得到
            // {"offset":[15,50,100],"value":["FF00DD","0032","aabb"]}
            JSONObject jso2 = new JSONObject(write);
            mOffset = jso2.getString("offset"); // 得到 [15,50,100]
            Log.e("FragmentPinzhengbudaDetail", "offset = " + mOffset);
            mValue = jso2.getString("value"); // 得到["FF00DD","0032","aabb"]
            Log.e("FragmentPinzhengbudaDetail", "value = " + mValue);
            writeList.add(mOffset);
            writeList.add(mValue);
            resultMap.put("write", writeList);
        } catch (Exception ex) {
            Log.e("FragmentPinzhengbudaDetail", "Exception ex= " + ex.toString());
            return null;
        }
        return resultMap;
    }
    private HashMap<String, List<String>> parsingJsonData4Write4442(String jsonStr) {
        /*
         * { "verify":{"offset":[5,100],"value":["12345","332200777"]},
		 * "passwd":"123456789",
		 * "write":{"offset":[15,50,100],"value":["FF00DD","0032","aabb"]} }
		 */

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
            Log.e("FragmentPinzhengbudaDetail", "offset = " + mOffset);
            mValue = jso1.getString("value"); // 得到["12345","332200777"]
            Log.e("FragmentPinzhengbudaDetail", "value = " + mValue);
            verifyList.add(mOffset);
            verifyList.add(mValue);
            resultMap.put("verify", verifyList);

            String passwd = jso.getString("passwd"); // 得到 "123456789"
            passwdList.add(passwd);
            resultMap.put("passwd", passwdList);

            String write = jso.getString("write"); // 得到
            // {"offset":[15,50,100],"value":["FF00DD","0032","aabb"]}
            JSONObject jso2 = new JSONObject(write);
            mOffset = jso2.getString("offset"); // 得到 [15,50,100]
            Log.e("FragmentPinzhengbudaDetail", "offset = " + mOffset);
            mValue = jso2.getString("value"); // 得到["FF00DD","0032","aabb"]
            Log.e("FragmentPinzhengbudaDetail", "value = " + mValue);
            writeList.add(mOffset);
            writeList.add(mValue);
            resultMap.put("write", writeList);
        } catch (Exception ex) {
            Log.e("FragmentPinzhengbudaDetail", "Exception ex= " + ex.toString());
            return null;
        }
        return resultMap;
    }

    public static List<String> matchValue(String s) {
        List<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("\"([\\w/\\.]*)\"");
        Matcher m = p.matcher(s);
        while (!m.hitEnd() && m.find()) {
            Log.e("FragmentPinzhengbudaDetail", "Match value =" + m.group(1));
            results.add(m.group(1));
        }
        return results;
    }

    public void onDestroy() {
        //ReaderMonitor.stopMonitor();
        mActivity.setOnBackPressedListener(null);
        if (progressDialog != null
                && (!mActivity.isFinishing())) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
    }

    private class PrintTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            createDialog();
            progressDialog
                    .setTitle(getString(R.string.str_dayin));
            progressDialog.setMessage(getString(R.string.progress_conducting));
//            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
/*            mPrinter.start();
            mPrinter.printXML(ticket);
            // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
            // -9999 其他错误
            int printResult = mPrinter.commitOperation();
            mPrinter.stop();
            return printResult;*/
        	ticket = ticket.replace("&amp;caret;","^");
//        	ticket = ticket.replace("&quot;", "@quot;").replace("&apos;", "@apos;").replace("&lt;", "@lt;").replace("&gt;", "@gt;");
//        	ticket = ticket.replace("&","&amp;");
//        	ticket = ticket.replace("@quot;", "&quot;").replace("@apos;", "&apos;").replace("@lt;", "&lt;").replace("@gt;", "&gt;");
        	/*ticket = StringEscapeUtils.unescapeXml(ticket).replace("&","&amp;");
        	ticket = ticket.replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");*/
        	gprinter.printXML(ticket);
        	String result = gprinter.commitOperation();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
        	if(result==null){
        		result="Printer Success";
        	}
            SystemUtil.displayToast(mActivity,result);
            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            isbtnClick=false;
            if (GlobalParams.IC_FLAG.equalsIgnoreCase("1") && mIcJsonRes.length() > 0) {
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
        								 SystemUtil.displayToast(mActivity, R.string.writecard_check_card);
        								 CloseTask();
        								 return;
        					            }
        							 CloseTask();
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
            }
        }
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
		    	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
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
	                mFeatures.clear();

	            }
	    }
	    private void CloseTask(){
	            mReader.close();
	    }
	    //判断是否插卡
	    public int getState() {

	        // Get slot number
	        int slotNum = 0;

	        // If slot is selected
	        if (slotNum != Spinner.INVALID_POSITION) {

	            try {

	                // Get state
//	                logMsg1("Slot " + slotNum + ": Getting state...");
	                int state = mReader.getState(slotNum);

	                if (state < Reader.CARD_UNKNOWN
	                        || state > Reader.CARD_SPECIFIC) {
	                    state = Reader.CARD_UNKNOWN;
	                }
	                return state;
//	                logMsg1("State: " + stateStrings[state]);

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
	                    return logBuffer(atr, atr.length);

	                } else {

	                   return "";
	                }

	            } catch (IllegalArgumentException e) {

	                logMsg1(e.toString());
	            }
	        }
	        return "";
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
