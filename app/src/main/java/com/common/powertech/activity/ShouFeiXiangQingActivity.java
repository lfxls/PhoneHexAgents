package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.telpo.tps550.api.decode.Decode;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import printUtils.gprinter;

import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Bangka;
import com.common.powertech.bussiness.Request_ShouDianFee_Query;
import com.common.powertech.bussiness.Request_Zhangdanshoufei_Query;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.hardwarelayer.ReaderMonitor;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.EncryptionDES;
import com.common.powertech.util.MathUtil;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.util.TRACE;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.ShoufeiQuery_Class;
import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;
import com.telpo.tps550.api.reader.CardReader;
import com.zxing.Capture;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.view.ViewfinderView;

/**
 * 广东天波信息技术股份有限公司 功能：收费详情Activity 作者:ouyangguozhao 日期:2015-11-6
 */
public class ShouFeiXiangQingActivity extends Activity {
    private static final String TAG = "SFeiDetailActivity";
    private String mReponseXML = "";
    private String mRspCode = "";
    private String mRspMeg = "";
    private ImageView mCloseImageView;
    private TextView mUserNameAndNum, mUserAddr, mUserTel, mElecCompany,
            mBillNum, mElecMon, mBillAmt, mFeeAmt, mPayAmt, mEnergyAmt,
            mDeditAmt, mTotalAmt;
    private Button mConfirmChargeBtnBtn, mZaicidayinBtn, mBangkaBtn;
    private EditText mPasswordET;
    private View mPasswordView;
    private String mComplexData = "";
    private List<ShoufeiQuery_Class> mShoufeiXiangQingItemList;
    private float totalAmt = 0;
    private float mTotalBillAmt = 0;
    private String receID = "";
    private String requestXML = "";
    private String reponseXML = "";
    private CustomProgressDialog progressDialog;
    String prdornNo = "";// 交易单号
    String banlance = "";// 当时余额
    String ticket = "";// 小票信息
    private Printer mPrinter = new Printer();
    private CustomDialog mBangkaDialog;
    private int TimeoutInms = 10 * 1000;
    private int mInputPassWordTimes = 3;

    private String mFuwufeijisuanRspCode = "";// 服务费计算响应码
    private String mFuwufeijisuanRspMsg = "";// 服务费计算响应信息
    private String mFuwufeijisuanRspFee = "";// 服务费
    private String mRmg = "";
    private Capture capture;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private String mBandCardCardType = "1";// 绑卡类型，默认IC卡
    private String ResourceType=""; //资源类型
    private String EnelName=""; //资源类型
    private String EnelId=""; //资源类型
    
    private ReadCardNoCallBack mReadCardNoCallBack = null;
    private boolean isCloseScaner = false;
    private String TracData = "";
    private Button btn_mag;
    private Button btn_img;
    private gprinter gprinter = new gprinter();
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        GlobalParams.If_CloseFlashLight = true;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (GlobalParams.Theme == 1) {
            setTheme(R.style.VioletTheme);
        } else if (GlobalParams.Theme == 1) {
            setTheme(R.style.OrangeTheme);
        }
        setContentView(R.layout.activity_shoufeixiangqing);
        mComplexData = getIntent().getExtras().getString(
                "ShoufeiQueryItem_List");
        mShoufeiXiangQingItemList = (List<ShoufeiQuery_Class>) StringUtil
                .string2ComplexData(mComplexData);
        ResourceType=getIntent().getStringExtra("ResourceType");//区分短信还是流量
        EnelName=getIntent().getStringExtra("EnelName");
        EnelId=getIntent().getStringExtra("EnelId");
        createDialog();
        initUI();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        connection();
//      registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
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

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550") && isCloseScaner) {
            CameraManager.init(ShouFeiXiangQingActivity.this.getApplication());
            capture = new Capture(ShouFeiXiangQingActivity.this, surfaceView,
                    viewfinderView);
            capture.Scan(handler, true);
            Handler mhdl = capture.getHandler();
            if (mhdl != null) {
                ((CaptureActivityHandler) mhdl).restartPreviewAndDecode();
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
        CameraManager.init(ShouFeiXiangQingActivity.this);
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
            capture = new Capture(ShouFeiXiangQingActivity.this, surfaceView,
                    viewfinderView);
        }
        if (!mComplexData.equalsIgnoreCase("")
                && mShoufeiXiangQingItemList.size() > 0) {
            mUserNameAndNum = (TextView) findViewById(R.id.shoufeixiangqing_nameandnumber);
            mUserNameAndNum.setText("Elec Bill of "
                    + mShoufeiXiangQingItemList.get(0).getUSER_NAME() + "/"
                    + mShoufeiXiangQingItemList.get(0).getUSER_NO());

            mUserAddr = (TextView) findViewById(R.id.shoufeixiangqing_address);
            mUserAddr.setText(mShoufeiXiangQingItemList.get(0).getUSER_ADDR());

            mUserTel = (TextView) findViewById(R.id.shoufeixiangqing_phone);
            mUserTel.setText(mShoufeiXiangQingItemList.get(0).getTEL());

            mElecCompany = (TextView) findViewById(R.id.shoufeixiangqing_elecdComplany);
            mElecCompany.setText(mShoufeiXiangQingItemList.get(0).getELEN_ID());

			/*
             * mElecMon = (TextView)
			 * findViewById(R.id.shoufeixiangqing_dianfeiyuefen);
			 * mElecMon.setText(mShoufeiXiangQingItemList.getCALC_MON());
			 */
            // 账单条数
            mBillNum = (TextView) findViewById(R.id.shoufeixiangqing_zhangdantiaoshu);
            mBillNum.setText("" + mShoufeiXiangQingItemList.size());

            mBillAmt = (TextView) findViewById(R.id.shoufeixiangqing_shoudianjine);
            float totalBillAmt = 0.0f;
            float totalFeeAmt = 0.0f;
            float totalPayAmt = 0.0f;
            float totalEnergyAmt = 0.0f;
            float totalOtherAmt = 0.0f;
            for (ShoufeiQuery_Class sc : mShoufeiXiangQingItemList) {
                if (null != sc.getBILL_AMT()) {
                    totalBillAmt = MathUtil.add4Float(totalBillAmt,
                            Float.parseFloat(sc.getBILL_AMT()));
                    // totalBillAmt += Float.parseFloat(sc.getBILL_AMT());
                }
				/*
				 * if (null != sc.getFEE_AMT()) { totalFeeAmt +=
				 * Float.parseFloat(sc.getFEE_AMT()); }
				 */
                if (null != sc.getENERGY_AMT()) {
                    totalEnergyAmt = MathUtil.add4Float(totalEnergyAmt,
                            Float.parseFloat(sc.getENERGY_AMT()));
                    // totalEnergyAmt += Float.parseFloat(sc.getENERGY_AMT());
                }
                if (null != sc.getOTHER_AMT()) {
                    totalOtherAmt = MathUtil.add4Float(totalOtherAmt,
                            Float.parseFloat(sc.getOTHER_AMT()));
                    // totalOtherAmt += Float.parseFloat(sc.getOTHER_AMT());
                }
            }
            mBillAmt.setText(keepDecimalPlaces(String.valueOf(totalBillAmt)));
            mTotalBillAmt = totalBillAmt;
            mTotalAmt = (TextView) findViewById(R.id.shoufeixiangqing_gongjishoufei);
            mPayAmt = (TextView) findViewById(R.id.shoufeixiangqing_yingfujine);
            mFeeAmt = (TextView) findViewById(R.id.shoufeixiangqing_fuwufei);
            if (mShoufeiXiangQingItemList.size() == 1) {
                mFuwufeijisuanRspFee = mShoufeiXiangQingItemList.get(0)
                        .getFEE_AMT();
                mFeeAmt.setText(keepDecimalPlaces(mFuwufeijisuanRspFee));
                mPayAmt.setText(keepDecimalPlaces(String.valueOf(MathUtil
                        .add4Float(mTotalBillAmt,
                                Float.valueOf(mFuwufeijisuanRspFee)))));
                mTotalAmt
                        .setText(getString(R.string.shoufeixiangqing_tv_gongjishoufei)
                                + keepDecimalPlaces(String.valueOf(MathUtil
                                .add4Float(mTotalBillAmt, Float
                                        .valueOf(mFuwufeijisuanRspFee)))));
            } else {
                new FeeQueryTask(ShouFeiXiangQingActivity.this).execute();
            }
            mEnergyAmt = (TextView) findViewById(R.id.shoufeixiangqing_qizhongdianfei);
            mEnergyAmt
                    .setText(keepDecimalPlaces(String.valueOf(totalEnergyAmt)));

            mDeditAmt = (TextView) findViewById(R.id.shoufeixiangqing_qizhongweiyuejin);
            mDeditAmt.setText(keepDecimalPlaces(keepDecimalPlaces(String
                    .valueOf(totalOtherAmt))));
			/*
			 * mTotalAmt
			 * .setText(getString(R.string.shoufeixiangqing_tv_gongjishoufei) +
			 * totalPayAmt);
			 */

        }

        mConfirmChargeBtnBtn = (Button) findViewById(R.id.btn_qrsf);
        mZaicidayinBtn = (Button) findViewById(R.id.btn_zcdy);
        mBangkaBtn = (Button) findViewById(R.id.btn_bangka);
        mConfirmChargeBtnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mConfirmChargeBtnBtn.setEnabled(false);
                if (!GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1")) {
                    boolean isLocked = false;
                    List<String> currentLockTime = new ArrayList<String>();
                    HashMap<String, List<String>> mLockMap = (HashMap<String, List<String>>) Preferences
                            .getComplexDataInPreference(
                                    ShouFeiXiangQingActivity.this,
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
                                                ShouFeiXiangQingActivity.this,
                                                Preferences.KEY_LOCKMAP,
                                                mLockMap);
                                isLocked = false;
                            } else {
                                isLocked = true;
                            }
                        }
                        if (isLocked) {
                            SystemUtil.displayToast(
                                    ShouFeiXiangQingActivity.this,
                                    R.string.warm_password_lockWarm);
                            mConfirmChargeBtnBtn.setEnabled(true);
                            mPasswordET.requestFocus();
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
                                    ShouFeiXiangQingActivity.this,
                                    R.string.login_password_not_null);
                            mConfirmChargeBtnBtn.setEnabled(true);
                            mPasswordET.requestFocus();
                            return;
                        }
                        desPW = EncryptionDES.DESSTR(mPasswordET
                                .getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        mConfirmChargeBtnBtn.setEnabled(true);
                        mPasswordET.requestFocus();
                        return;
                    }
                    if (GlobalParams.PAY_PWD.equalsIgnoreCase(desPW)) {
                        currentLockTime.clear();
                        mLockMap.put(GlobalParams.LOGIN_USER_ID,
                                currentLockTime);
                        Preferences.storeComplexDataInPreference(
                                ShouFeiXiangQingActivity.this,
                                Preferences.KEY_LOCKMAP, mLockMap);
                        new QuerenshoufeiTask(getApplicationContext()).execute();
                    } else {
                        currentLockTime.add(SystemUtil
                                .getCurrentDateTimeHH24());
                        mLockMap.put(GlobalParams.LOGIN_USER_ID,
                                currentLockTime);
                        Preferences.storeComplexDataInPreference(
                                ShouFeiXiangQingActivity.this,
                                Preferences.KEY_LOCKMAP, mLockMap);
                        int lessTimes = 3 - currentLockTime.size();
                        if (lessTimes == 0) {
                            SystemUtil.displayToast(
                                    ShouFeiXiangQingActivity.this,
                                    R.string.warm_password_lockWarm);
                        } else {
                            SystemUtil
                                    .displayToast(
                                            ShouFeiXiangQingActivity.this,
                                            getString(R.string.warm_password_inputPer)
                                                    + lessTimes
                                                    + getString(R.string.warm_password_inputLeft)
                                    );
                        }
                        mConfirmChargeBtnBtn.setEnabled(true);
                        mPasswordET.requestFocus();
                    }
                } else {
                    new QuerenshoufeiTask(getApplicationContext()).execute();
                }
            }
        });
        mCloseImageView = (ImageView) findViewById(R.id.btnCloseActivity);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        mPasswordView = (View) findViewById(R.id.zhifumima_layout);
        if (GlobalParams.BUY_ELE_WAY.equalsIgnoreCase("1")) {
            // 不需要支付密码
            mPasswordView.setVisibility(View.GONE);
        }

        mPasswordET = (EditText) findViewById(R.id.password_ET);
        // mPasswordET.setInputType(InputType.TYPE_NULL);
        mPasswordET.setFocusable(true);
        mPasswordET.requestFocus();

    }

    private int Shoufei() {
        TRACE.v(TAG, "收费业务");
        int result = 0;
        totalAmt = 0;
        receID = "";
        for (ShoufeiQuery_Class sc : mShoufeiXiangQingItemList) {
            if (receID.equalsIgnoreCase("")) {
                receID += sc.getRECE_ID();
            } else {
                receID += ("|" + sc.getRECE_ID());
            }
            totalAmt = MathUtil.add4Float(totalAmt,
                    Float.valueOf(sc.getENERGY_AMT())); // 共计金额
        }
        Request_Zhangdanshoufei_Query.setContext(ShouFeiXiangQingActivity.this);
        Request_Zhangdanshoufei_Query.setReceID(receID);
        // 要求保留小数点后三位
        String amtParm = String.valueOf(totalAmt);
        // Log.e(TAG,"amtParm = "+amtParm);
        int positionLength = amtParm.length() - amtParm.indexOf(".") - 1;
        // Log.e(TAG,"positionLength = "+positionLength);
        if (positionLength < 3) {
            for (int i = 0; i < (3 - positionLength); i++) {
                amtParm += "0";
            }
        }
        Request_Zhangdanshoufei_Query.setAmt(amtParm);
//        Request_Zhangdanshoufei_Query.setAmt(keepDecimalPlaces(amtParm));
        requestXML = Request_Zhangdanshoufei_Query.getRequsetXML();
        // 模拟数据
        // requestXML =
        // "<ROOT><TOP><IMEI>762845024199122</IMEI><REQUEST_TIME>2015-10-30 12:51:15</REQUEST_TIME><LOCAL_LANGUAGE>en</LOCAL_LANGUAGE><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID></TOP><BODY><RECE_ID>32015102823551033</RECE_ID><AMT>428.700</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>65cca7adf924d06e27f8dee7f7d41395</SIGNATURE></TAIL></ROOT>";
        System.out.println("账单收费请求：" + requestXML);
        reponseXML = "";
        try {
            reponseXML = Client.ConnectServer("PBillPay", requestXML);
            // 模拟数据
            // reponseXML =
            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>en</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:51:15</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>Success!</RSPMSG><PRDORDNO>D015103000021526</PRDORDNO><BANLANCE>999999988104.56</BANLANCE><TICKET><TYPE_T>1</TYPE_T><TITLE_T>Title</TITLE_T><CONTENT_T>Content</CONTENT_T><TAIL_T>Tail</TAIL_T><TOKEN_T>Token</TOKEN_T></TICKET></BODY></ROOT>";
            System.out.println("收费查询响应：" + reponseXML);
            mRspCode = Client.Parse_XML(reponseXML, "<RSPCOD>", "</RSPCOD>");
            mRspMeg = Client.Parse_XML(reponseXML, "<RSPMSG>", "</RSPMSG>");
            if (!mRspCode.equals("00000")) {// 收费失败
                // 服务器返回系统超时，返回到登录页面
                if (mRspCode.equals("00011")) {
                    Toast.makeText(ShouFeiXiangQingActivity.this, mRspMeg,
                            Toast.LENGTH_LONG).show();
                    SystemUtil.setGlobalParamsToNull(ShouFeiXiangQingActivity.this);
                    DummyContent.ITEM_MAP.clear();
                    DummyContent.ITEMS.clear();
                    Intent intent = new Intent(ShouFeiXiangQingActivity.this, LoginActivity.class);
                    ShouFeiXiangQingActivity.this.startActivity(intent);
                }
                result = 0;
            } else {
                // 收费成功
                result = 1;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = GlobalParams.ERROR_NETWORK;
        }
        return result;
    }

    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(
                ShouFeiXiangQingActivity.this,
                GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        setOnReadCardNoCallBack(null);
                        SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                                R.string.progress_timeout);
                        if (dialog != null
                                && (!ShouFeiXiangQingActivity.this
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

    private List<Map<String, String>> getTicketContentList(String ticketMessage) {
        HashMap<String, String> contentMap = null;
        List<Map<String, String>> list = null;
        try {
            InputStream in = new ByteArrayInputStream(
                    ticketMessage.getBytes("UTF-8"));
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(in, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<Map<String, String>>();
                        break;

                    case XmlPullParser.START_TAG:
                        if ("TITLE_T".equals(pullParser.getName())) {
                            contentMap = new HashMap<String, String>();
                            contentMap.put("TITLE_T", pullParser.nextText() + "\n");
                            list.add(contentMap);
                        }
                        if ("CONTENT_T".equals(pullParser.getName())) {
                            contentMap = new HashMap<String, String>();
                            contentMap.put("CONTENT_T", pullParser.nextText()
                                    + "\n");
                            list.add(contentMap);
                        }
                        if ("TAIL_T".equals(pullParser.getName())) {
                            contentMap = new HashMap<String, String>();
                            contentMap.put("TAIL_T", pullParser.nextText() + "\n");
                            list.add(contentMap);
                        }
                        if ("PIC_T".equals(pullParser.getName())) {
                            contentMap = new HashMap<String, String>();
                            contentMap.put("PIC_T", pullParser.nextText() + "\n");
                            list.add(contentMap);
                        }
                        if ("TOKEN_T".equals(pullParser.getName())) {
                            contentMap = new HashMap<String, String>();
                            contentMap.put("TOKEN_T", pullParser.nextText() + "\n");
                            list.add(contentMap);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                event = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @SuppressLint("NewApi")
    public void loginon() {
        Intent intent = new Intent(ShouFeiXiangQingActivity.this,
                ItemListActivity.class);
        startActivity(intent);
    }

    // 收费查询异步任务
    private class QuerenshoufeiTask extends AsyncTask<Void, Void, Integer> {

        public QuerenshoufeiTask(Context context) {
            createDialog();
            progressDialog.setTitle(getString(R.string.progress_shoufei_title));
            progressDialog.setMessage(getString(R.string.progress_conducting));
            // 设置进度条是否不明确
//            progressDialog.setIndeterminate(false);

            // 是否可以按下退回键取消
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            int returnResult = 0;
            Log.e(TAG, "--doinbackground");
            // Disable_Key.EnableTestMode("true", getActivity());
            try {
                returnResult = Shoufei();
            } catch (Exception e) {
                returnResult = GlobalParams.ERROR_NETWORK;
            }
            return returnResult;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mConfirmChargeBtnBtn.setEnabled(true);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (result) {
                case GlobalParams.ERROR_NETWORK:
                    SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                            R.string.shoufeixiangqing_wangluoyichang);
                    break;
                case 0:
                    // 收费失败
                    if (mRspMeg.equalsIgnoreCase("")) {
                        SystemUtil
                                .displayToast(
                                        ShouFeiXiangQingActivity.this,
                                        ShouFeiXiangQingActivity.this
                                                .getString(R.string.shoufeixiangqing_shoufeishibai)
                                );
                    } else {
                        SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                                mRspMeg);
                    }
                    break;
                case 1:
                    // 交易成功售电记录存数据库并更新主界面
                    JinRiShouDian mJinRiShouDian = new JinRiShouDian(
                            SystemUtil.getCurrentDate(),
                            keepDecimalPlaces(String.valueOf(MathUtil.add4Float(
                                    mTotalBillAmt,
                                    Float.valueOf(mFuwufeijisuanRspFee))))
                    );
                    BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
                            getApplicationContext(), JinRiShouDian.class);
                    baseDao.create(mJinRiShouDian);
                    Intent it = new Intent(
                            GlobalParams.UPDATE_JINRISHOUDIAN_ACTION);
                    sendBroadcast(it);
                    // 收费成功
                    // 交易单号
                    prdornNo = Client.Parse_XML(reponseXML, "<PRDORDNO>",
                            "</PRDORDNO>");
                    // 当时余额
                    banlance = Client.Parse_XML(reponseXML, "<BANLANCE>",
                            "</BANLANCE>");
                    // 小票信息
                    ticket = "<TICKET>"
                            + Client.Parse_XML(reponseXML, "<TICKET>", "</TICKET>")
                            + "</TICKET>";

                    // 获取押金余额并更新主界面
                    GlobalParams.CASH_AC_BAL = banlance;
                    Intent intentYaJinYuEr = new Intent(
                            GlobalParams.UPDATE_YAJINYUER_ACTION);
                    sendBroadcast(intentYaJinYuEr);
                    new PrintTask()
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    mPasswordView.setVisibility(View.GONE);
                    mConfirmChargeBtnBtn.setEnabled(false);
                    mConfirmChargeBtnBtn.setVisibility(View.GONE);
                    mZaicidayinBtn.setVisibility(View.VISIBLE);
                    mZaicidayinBtn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // 再次打印
                            mZaicidayinBtn.setEnabled(false);
                            new PrintTask()
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
//                    mBangkaBtn.setVisibility(View.VISIBLE);
                    mBangkaBtn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // 绑卡
                            mBangkaBtn.setEnabled(false);
                            showQuerenbangkaDialog(ShouFeiXiangQingActivity.this);
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void showQuerenbangkaDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_querenbangka, null);

        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
        builder.setContentView(view);
        mBangkaDialog = builder.create();
        mBangkaDialog.setCancelable(false);
        TextView mHuhaokahaoTV = (TextView) view
                .findViewById(R.id.huhaobiaohao_TV);
        TextView mHumingTV = (TextView) view.findViewById(R.id.huming_TV);
        // TextView mKahaoTV = (TextView) view.findViewById(R.id.kahao_TV);

        mHuhaokahaoTV.setText(mShoufeiXiangQingItemList.get(0).getMETER_NO());
        mHumingTV.setText(mShoufeiXiangQingItemList.get(0).getUSER_NAME());
        // mKahaoTV.setText(ShouFeiXiangQingActivity.this
        // .getString(R.string.shoudianxiangqing_kahao));

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
//                            Intent openCameraIntent = new Intent(ShouFeiXiangQingActivity.this, CaptureActivity.class);
//                            startActivityForResult(openCameraIntent, 0);
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

    private class PrintTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            if (mZaicidayinBtn != null) {
                mZaicidayinBtn.setEnabled(false);
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
            mPrinter.printXML(ticket);
            // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
            // -9999 其他错误
            int printResult = mPrinter.commitOperation();
            mPrinter.stop();
            return printResult;*/
        	ticket = ticket.replace("&amp;caret;","^");
        	gprinter.printXML(ticket);
        	String result = gprinter.commitOperation();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        	if(result==null){
        		result="Printer Success";
        	}
            SystemUtil.displayToast(ShouFeiXiangQingActivity.this,result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (mZaicidayinBtn != null) {
                mZaicidayinBtn.setEnabled(true);
            }
        }
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
                boolean isOK = bandCard(mCardNo, mBandCardCardType, prdornNo);
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
            if (progressDialog != null
                    && (!ShouFeiXiangQingActivity.this.isFinishing())) {
                progressDialog.dismiss();
                // progressDialog = null;
            }
            if (mBangkaBtn != null) {
                mBangkaBtn.setClickable(true);
            }
            if (result == 1) {
                SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                        getString(R.string.shoudianxiangqing_bangkachenggong));
                if (mBangkaDialog != null && mBangkaDialog.isShowing()) {
                    mBangkaDialog.dismiss();
                }
            } else {
                SystemUtil.displayToast(ShouFeiXiangQingActivity.this, mRmg);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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
        Request_Bangka.setContext(ShouFeiXiangQingActivity.this);
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
                Toast.makeText(ShouFeiXiangQingActivity.this, mRspMeg,
                        Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(ShouFeiXiangQingActivity.this);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(ShouFeiXiangQingActivity.this, LoginActivity.class);
                ShouFeiXiangQingActivity.this.startActivity(intent);
            }
            return false;
        }
    }

    // 服务费查询异步任务
    private class FeeQueryTask extends AsyncTask<Void, Void, Integer> {

        public FeeQueryTask(Context context) {
            createDialog();
            progressDialog.setTitle(getString(R.string.progress_shoufei_title));
            progressDialog.setMessage(getString(R.string.progress_conducting));
            // 设置进度条是否不明确
//            progressDialog.setIndeterminate(false);

            // 是否可以按下退回键取消
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e(TAG, "--doinbackground");
            // Disable_Key.EnableTestMode("true",
            // ShouFeiXiangQingActivity.this);
            try {
                return feeQuery(mTotalBillAmt) ? 1 : 0;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return 0;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != 0) {
                // showQuerengoudianDialog(ShouFeiXiangQingActivity.this);
                mFeeAmt.setText(keepDecimalPlaces(mFuwufeijisuanRspFee));
                mPayAmt.setText(keepDecimalPlaces(String.valueOf(MathUtil
                        .add4Float(mTotalBillAmt,
                                Float.valueOf(mFuwufeijisuanRspFee)))));
                mTotalAmt
                        .setText(getString(R.string.shoufeixiangqing_tv_gongjishoufei)
                                + keepDecimalPlaces(String.valueOf(MathUtil
                                .add4Float(mTotalBillAmt, Float
                                        .valueOf(mFuwufeijisuanRspFee)))));
            } else {
                SystemUtil
                        .displayToast(
                                ShouFeiXiangQingActivity.this,
                                getString(R.string.shoufeixiangqing_fuwufeijisuanshibai)
                                        + " " + mFuwufeijisuanRspMsg
                        );
            }
            if (progressDialog != null
                    && (!ShouFeiXiangQingActivity.this.isFinishing())) {
                progressDialog.dismiss();
                // progressDialog = null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private boolean feeQuery(float amt) {// 购电服务费查询 成功返回true，失败返回false
        Request_ShouDianFee_Query.setContext(ShouFeiXiangQingActivity.this);
        // 收费只有RECEID
        Request_ShouDianFee_Query.setAmt("");
        receID = "";
        for (ShoufeiQuery_Class sc : mShoufeiXiangQingItemList) {
            if (receID.equalsIgnoreCase("")) {
                receID += sc.getRECE_ID();
            } else {
                receID += ("|" + sc.getRECE_ID());
            }
        }
        Request_ShouDianFee_Query.setReceID(receID);
        Request_ShouDianFee_Query.setPrdType(ResourceType);//资源类型
        Request_ShouDianFee_Query.setEnelId(EnelId);// 电力公司
        String requestXML = Request_ShouDianFee_Query.getRequsetXML();
        Log.e(TAG, "requestXML = " + requestXML);
        String reponseXML = "";
        try {
            reponseXML = Client.ConnectServer("PBillFeeCount", requestXML);
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
        mFuwufeijisuanRspMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
                "</RSPMSG>");
        mFuwufeijisuanRspFee = Client.Parse_XML(reponseXML, "<FEE>", "</FEE>");
        if (mFuwufeijisuanRspCode.equalsIgnoreCase("00000")) {
            Log.e(TAG, "mFuwufeijisuanRspFee = " + mFuwufeijisuanRspFee);
            return true;
        } else {
            // 服务器返回系统超时，返回到登录页面
            if (mFuwufeijisuanRspCode.equals("00011")) {
                Toast.makeText(ShouFeiXiangQingActivity.this,
                        mFuwufeijisuanRspMsg, Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(ShouFeiXiangQingActivity.this);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(ShouFeiXiangQingActivity.this, LoginActivity.class);
                ShouFeiXiangQingActivity.this.startActivity(intent);
            }
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (mConfirmChargeBtnBtn != null
                        && mConfirmChargeBtnBtn.isEnabled()) {
                    mConfirmChargeBtnBtn.performClick();
                }
            }
        }
        return super.dispatchKeyEvent(event);
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

    private final BroadcastReceiver mCardMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "icc present Broadcast Receiver");
            if (intent.getAction() == ReaderMonitor.ACTION_ICC_PRESENT) {
                if (intent.getExtras().getBoolean(
                        ReaderMonitor.EXTRA_IS_PRESENT)) {
                    int cardType = intent.getExtras().getInt(
                            ReaderMonitor.EXTRA_CARD_TYPE);
                    if (cardType == CardReader.CARD_TYPE_SLE4428) {
                        mBandCardCardType = "1";
                        // IC卡类型
                        GlobalParams.CARD_TYPE = "1";
                        // byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF};
                        // ReaderMonitor.pscVerify(psc);
                        // if (mReadCardNoCallBack != null) {
                        // // 读IC卡卡号
                        // byte userCode[] = ReaderMonitor.getUserCode();
                        // String cardNo = StringUtil.BCD2Str(userCode);
                        // Log.e(TAG, "cardNo = " + cardNo);
                        // GlobalParams.ICCARD_NO = cardNo;
                        // mReadCardNoCallBack.readCardMessage(cardNo);
                        // }
                    } else if (cardType == CardReader.CARD_TYPE_SLE4442) {
                        mBandCardCardType = "1";
                        // IC卡类型
                        GlobalParams.CARD_TYPE = "2";
                        // byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF,
                        // (byte) 0xFF};
                        // ReaderMonitor.pscVerify(psc);
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
                // SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                // R.string.trac_data_error);
                // }
                builder.append(trackData[0] + "|" + trackData[1] + "|"
                        + trackData[2]);
                Log.e(TAG, "TRAC = " + builder.toString());
                if (mReadCardNoCallBack != null) {
                    if (TracData.equalsIgnoreCase("")) {
                        TracData = builder.toString();
                        SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                                getString(R.string.warm_swipcard_again));
                    } else {
                        if (!TracData.equalsIgnoreCase(builder.toString())) {
                            SystemUtil.displayToast(
                                    ShouFeiXiangQingActivity.this,
                                    getString(R.string.warm_swipcard_dataError));
                            TracData = builder.toString();
                        } else {
                            TracData = "";
                            mReadCardNoCallBack.readCardMessage(builder
                                    .toString());
                        }
                    }
                } else {
                    // SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
                    // R.string.shoudianxiangqing_bangkashibai);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        // progressDialog = null;
                    }
                    if (mBangkaBtn != null) {
                        mBangkaBtn.setClickable(true);
                    }
                }
            }
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
                    SystemUtil.displayToast(ShouFeiXiangQingActivity.this,
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
                dialog = new ProgressDialog(ShouFeiXiangQingActivity.this);
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
			if (btn_img != null) {
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
        Log.e(TAG, "onPause");
        isCloseScaner = true;
        if (capture != null) {
            capture.Stop();
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
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

}
