package com.common.powertech;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.common.powertech.activity.FragmentChongZhengShenQingDetail;
import com.common.powertech.activity.FragmentDianXinMain;
import com.common.powertech.activity.LoginActivity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import printUtils.gprinter;

import com.common.powertech.activity.FragmentXiTongSheZhiMain;
import com.common.powertech.activity.Fragmentshoufei;
import com.common.powertech.bussiness.PULLParse_BillDaily_Apply;
import com.common.powertech.bussiness.PULLParse_Logout;
import com.common.powertech.bussiness.PULLParse_Notify_Query;
import com.common.powertech.bussiness.ParentRequset;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dbbean.PrinterTemp;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.exception.OtherException;
import com.common.powertech.hardwarelayer.ReaderMonitor;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Md5Algorithm;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MarqueeTextView;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.BillDailyApply_Class;
import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.telpo.tps550.api.reader.CardReader;
import com.zxing.Capture;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.view.ViewfinderView;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragmentController}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */

/**
 * 广东天波信息技术股份有限公司 功能：Fragment列表主框架 作者:luyq 日期:2015-10-12
 */
public class ItemListActivity extends Activity implements
        ItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final String TAG = "ItemListActivity";
    private boolean mTwoPane;
    private ItemDetailFragmentController mWeixin;
    private static CustomProgressDialog progressDialog;
    private Timer timer;
    private TimerTask timerTask, timerTask_clock;
    private TextView tv_user, tv_jinrishoudian, tv_baozhengjinyue, tv_clock;
    private MarqueeTextView tv_notify;
    private ImageView iv_battery, iv_wifi, iv_gsm, iv_ethernet;
    private Button btn_scjl;
    private StringBuilder stringBuilder;
    public static StringBuilder notifyStringBuilder;
    ShortCutsKeyDownCallBack mShortCutKeyDownCB;
    OnBackPressedListener mOnBackPressedListener;
    int mShortCutKeyCode = 0;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;

    ReadCardMessageCallBack mReadCardMessageCallBack;
    private String mOffset = "";
    private String mValue = "";
    private Spinner spinner_menu;
    private static Boolean If_Double_Click = false;
    private ListView listView;

    // 二维码
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private Capture capture;
    private ReadCardMessageCallBack mTempReadCardMessageCallBack = null;
    private String Pre_ID = "1";
    private boolean isZXClicked = false;
    public static boolean isEnterTrigger = true;
    private long exitTime = 0;
    //蓝牙打印机
    private gprinter gprinter = new gprinter();
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;

	private static int backT = 0; //次数
	private static int backTime = 5000; // 时间段
	private static runBack runBackIm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SystemUtil.setAppLanguageChange(ItemListActivity.this);
        // 设置主题样式
        if (GlobalParams.Theme == 1) {
            setTheme(R.style.VioletTheme);
        } else if (GlobalParams.Theme == 2) {
            setTheme(R.style.OrangeTheme);
        }
        DummyContent.ITEM_MAP.clear();
        DummyContent.ITEMS.clear();
        DummyContent.setItem();
        setContentView(R.layout.activity_item_twopane);

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);

        iv_battery = (ImageView) findViewById(R.id.imageBattery);
        iv_wifi = (ImageView) findViewById(R.id.imageWifi);
        iv_gsm = (ImageView) findViewById(R.id.imageSignal);
        iv_ethernet = (ImageView) findViewById(R.id.imageEthernet);
        tv_clock = (TextView) findViewById(R.id.tv_clock);
        tv_notify = (MarqueeTextView) findViewById(R.id.tv_notify);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText(GlobalParams.LAW_NAME + "/" + GlobalParams.OPER_NAME);
        tv_jinrishoudian = (TextView) findViewById(R.id.tv_jinrishoudian);

        // 从数据库获取今日收费数据，若数据库无数据则显示为0，否则显示数据库数据
        BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
                ItemListActivity.this, JinRiShouDian.class);
        if (baseDao.isExists(1)) {
            setTextViewJinRiShouDian();
        } else {
            tv_jinrishoudian.setText("0/0"
                    + getResources().getString(
                    R.string.main_welcome_jingrishoudian_unit));
        }
        tv_baozhengjinyue = (TextView) findViewById(R.id.tv_baozhengjinyue);
        tv_baozhengjinyue.setText(GlobalParams.CASH_AC_BAL);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            // ((ItemListFragment) getFragmentManager().findFragmentById(
            // R.id.item_list)).setActivateOnItemClick(true);
            // luyq 根据型号去控制菜单
                spinner_menu = (Spinner) this.findViewById(R.id.spinner_menu);
                List<String> list = new ArrayList<String>();
                if (DummyContent.ITEMS != null && DummyContent.ITEMS.size() > 0) {
                    for (int i = 0; i < DummyContent.ITEMS.size(); i++) {
                        list.add(DummyContent.ITEMS.get(i).content);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, list);
                spinner_menu.setAdapter(adapter);
                    spinner_menu.setSelection(0, true);
                spinner_menu
                        .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int arg2, long arg3) {
                            	if(!(arg2 == 6)){ //不是充值菜单
	                                try {
	                                    // 以下三行代码是解决问题所在
	                                    Field field = AdapterView.class
	                                            .getDeclaredField("mOldSelectedPosition");
	                                    field.setAccessible(true); // 设置mOldSelectedPosition可访问
	                                    field.setInt(spinner_menu,
	                                            AdapterView.INVALID_POSITION); // 设置mOldSelectedPosition的值
	                                } catch (Exception e) {
	                                    e.printStackTrace();
	                                }
                            	}

                                if (FragmentChongZhengShenQingDetail.SoftInputRunning) {
                                    return;
                                }

                                String selectId = DummyContent.ITEMS.get(arg2).id;
                                Pre_ID = selectId;
                                Bundle arguments = new Bundle();
                                arguments
                                        .putString(
                                                ItemDetailFragmentController.ARG_ITEM_ID,
                                                selectId);
                                ItemDetailFragmentController fragment = new ItemDetailFragmentController();
                                fragment.setArguments(arguments);
                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.item_detail_container,
                                                fragment).commitAllowingStateLoss();
                            }

                            public void onNothingSelected(
                                    AdapterView<?> arg0) {
                            }
                        });

        }
        // 从数据库获取打印模板参数并赋值到GlobalParams,并设置到打印机
        setGlobalPrinterParams();

        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                try {
                    getNotify();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        getNotify();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        timerTask_clock = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_clock.setText(SystemUtil.getCurrentHourAndMinute());
                    }
                });
            }
        };
        // 每隔30分钟从服务器获取公告
        timer.schedule(timerTask, 3000, 1800000);
        // 每隔1s获取系统时间并显示在主界面
        timer.schedule(timerTask_clock, 1000, 1000);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        // 使用AlertDialog显示公告详细信息
        tv_notify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	LayoutInflater layoutInflater = LayoutInflater.from(ItemListActivity.this);
            	View view = layoutInflater.inflate(R.layout.dialog_notify_main, null);
            	TextView textView = (TextView) view.findViewById(R.id.dialog_notify);
            	if(notifyStringBuilder == null){
            		textView.setText(getString(R.string.notify_dialog_message_none));
            	}else {
            		textView.setText(Html.fromHtml(notifyStringBuilder.toString()));
				}
				new AlertDialog.Builder(ItemListActivity.this)
				.setView(view)
				.setCancelable(true)
				.setTitle(R.string.notify_dialog)
				.setPositiveButton(getString(R.string.notify_dialog_button_close), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
            }
        });
        if (GlobalParams.IsSystemSettingTrigger == false) {
            setDefaultFragment();
        } else {
            setXtszFragment();
            GlobalParams.IsSystemSettingTrigger = false;
        }
        // 设置网络状态图标
        setNetWorkStatus();

        if ("TPS550".equals(GlobalParams.DeviceModel)) {
            ListFragment listFragment = ((ItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.item_list));
            listView = listFragment.getListView();
            listView.setItemChecked(0, true);
            listView.setSelection(0);
        }

        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(GlobalParams.UPDATE_JINRISHOUDIAN_ACTION);
        intentFilter.addAction(GlobalParams.UPDATE_YAJINYUER_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
//        ReaderMonitor.setContext(getApplicationContext());
//        ReaderMonitor.startMonitor();
//        UpdateTextTask updateTextTask = new UpdateTextTask();  
//        updateTextTask.execute();  
//        new UpdateTextTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        connection();
        PowertechApplication.getInstance().addActivity(this);
//        timerNotify.schedule(task, 1000, 10000);
	}

    Timer timerNotify = new Timer();
	TimerTask task = new TimerTask(){
		public void run() {
			Intent intent = new Intent();
//            intent.setAction("NOTIFY_SERVICE");
//            startService(intent);
			intent.setAction("NOTIFY_SERVICE");
			intent.setPackage(getPackageName());
			startService(intent);
		};
	};

	Handler backHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){
				backT = 0;
			}
			super.handleMessage(msg);
		}
	};
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

    protected void onStart() {
        Log.e(TAG, "onStart");
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
            if (GlobalParams.If_CloseFlashLight) {
                Log.e(TAG, "Camera!-Start!");
                Scan(handler);
                GlobalParams.If_CloseFlashLight = false;
            } else {
                Log.e(TAG, "Camera!-ReStart!");
                continuePreview(handler);
            }
            GlobalParams.If_CloseFlashLight = false;
        }
        super.onStart();

    }

    @Override
    protected void onResume() {

        Log.e(TAG, "onResume");

        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
            try {
                // 让Spinner 选择相同项时 可以触发 setOnItemSelectedListener
                Field field = AdapterView.class
                        .getDeclaredField("mOldSelectedPosition");
                field.setAccessible(true); // 设置mOldSelectedPosition可访问
                field.setInt(spinner_menu,
                        AdapterView.INVALID_POSITION); // 设置mOldSelectedPosition的值
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (capture == null) {
            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
                if (GlobalParams.If_CloseFlashLight) {
                    Log.e(TAG, "Camera!-Start!");
                    Scan(handler);
                    GlobalParams.If_CloseFlashLight = false;
                } else {
                    Log.e(TAG, "Camera!-ReStart!");
                    continuePreview(handler);
                }
                GlobalParams.If_CloseFlashLight = false;
            }
        }

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ReaderMonitor.ACTION_ICC_PRESENT);
//        filter.addAction(ReaderMonitor.ACTION_MSC);
//        registerReceiver(mCardMessageReceiver, filter);
        super.onResume();
    }

    public void setDefaultFragment() {
        Pre_ID = "1";
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Bundle arguments = new Bundle();
        	 arguments.putString(ItemDetailFragmentController.ARG_ITEM_ID,
                     DummyContent.ITEMS.get(0).id);

        mWeixin = new ItemDetailFragmentController();
        mWeixin.setArguments(arguments);
        transaction.replace(R.id.item_detail_container, mWeixin);
        transaction.commitAllowingStateLoss();
    }

    public void setDefaultFragment(String id) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragmentController.ARG_ITEM_ID, id);
        mWeixin = new ItemDetailFragmentController();
        mWeixin.setArguments(arguments);
        transaction.replace(R.id.item_detail_container, mWeixin);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (Pre_ID.equalsIgnoreCase("4") || Pre_ID.equalsIgnoreCase("5")
                || Pre_ID.equalsIgnoreCase("6")) {
            // 防止双击
            listView.setEnabled(false);
            if (If_Double_Click) {
                return;
            }
            If_Double_Click = true;
            Delay();
        }

        Pre_ID = id;

        // 切换菜单时 不关闭闪关灯

        // if( id.equalsIgnoreCase("1") || id.equalsIgnoreCase("2") ||
        // id.equalsIgnoreCase("3") ){
        // GlobalParams.If_CloseFlashLight = false;
        // }else{
        // StopScan();
        // ItemDetailFragmentController.isScanerRunning = false;
        // }

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragmentController.ARG_ITEM_ID, id);
            ItemDetailFragmentController fragment = new ItemDetailFragmentController();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment).commitAllowingStateLoss();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragmentController.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    // 系统设置
    public void xtsz(View view) {
        // StopScan();
        // ItemDetailFragmentController.isScanerRunning = false;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        FragmentXiTongSheZhiMain xiTongSheZhiMainFragment = new FragmentXiTongSheZhiMain();
        transaction.replace(R.id.item_detail_container,
                xiTongSheZhiMainFragment);
        transaction.commitAllowingStateLoss();
    }

    // 设置显示为为系统设置界面
    public void setXtszFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        FragmentXiTongSheZhiMain xiTongSheZhiMainFragment = new FragmentXiTongSheZhiMain();
        transaction.replace(R.id.item_detail_container,
                xiTongSheZhiMainFragment);
        transaction.commitAllowingStateLoss();
    }

    // 注销
    public void zx(View view) {
        if (!ItemListActivity.this.isFinishing()) {
            if (!isZXClicked) {
                isZXClicked = true;
            } else {
                return;
            }
            if (mReadCardMessageCallBack != null) {
                mTempReadCardMessageCallBack = mReadCardMessageCallBack;
            }
            setOnReadCardMessageCallBack(null);
            final Dialog dialogZx = new Dialog(ItemListActivity.this,
                    R.style.FullHeightDialog);
            LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View dialogZxView = li.inflate(R.layout.dialog_tuichudenglu, null);
            dialogZx.setContentView(dialogZxView);
            dialogZx.setCancelable(true);
            dialogZx.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    isZXClicked = false;
                    if (mTempReadCardMessageCallBack != null) {
                        setOnReadCardMessageCallBack(mTempReadCardMessageCallBack);
                    }
                }
            });
            dialogZx.show();

            btn_scjl = (Button) dialogZxView.findViewById(R.id.btn_scjl);
            btn_scjl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btn_scjl.setEnabled(false);
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(ItemListActivity.this);
                    }
                    progressDialog
                            .setTitle(R.string.login_progressdialog_title);
                    progressDialog
                            .setMessage(getResources()
                                    .getString(
                                            R.string.logout_progressdialog_message_pbilldailyapply));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                TreeMap<String, String> paramMap = new TreeMap<String, String>();
                                paramMap.put("IMEI", SystemUtil
                                        .getIMEI(ItemListActivity.this));
                                paramMap.put("REQUEST_TIME",
                                        SystemUtil.getCurrentDateTimeHH24());
                                paramMap.put("SESSION_ID",
                                        GlobalParams.SESSION_ID);
                                paramMap.put(
                                        "LOCAL_LANGUAGE",
                                        SystemUtil
                                                .getLocalLanguage(ItemListActivity.this)
                                );
                                paramMap.put("SIGN_TYPE", "1");
                                String sign = ParentRequset
                                        .bulidParam(paramMap);
                                String signature = Md5Algorithm.getInstance()
                                        .md5Digest(sign.getBytes());
                                String xmlData = "<ROOT><TOP><IMEI>"
                                        + SystemUtil
                                        .getIMEI(ItemListActivity.this)
                                        + "</IMEI><SESSION_ID>"
                                        + GlobalParams.SESSION_ID
                                        + "</SESSION_ID><SOURCE>3</SOURCE><REQUEST_TIME>"
                                        + SystemUtil.getCurrentDateTimeHH24()
                                        + "</REQUEST_TIME><LOCAL_LANGUAGE>"
                                        + SystemUtil
                                        .getLocalLanguage(ItemListActivity.this)
                                        + "</LOCAL_LANGUAGE></TOP><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>"
                                        + signature
                                        + "</SIGNATURE></TAIL></ROOT>";
                                Log.v("生成日结请求:", xmlData);
                                final String serverxmlData = Client
                                        .ConnectServer("PBillDailyApply",
                                                xmlData);
                                Log.v("生成日结响应:", serverxmlData);
                                // 服务器返回系统超时，返回到登录页面
                                if (Client.Parse_XML(serverxmlData, "<RSPCOD>",
                                        "</RSPCOD>").equals("00011")) {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(
                                                    ItemListActivity.this,
                                                    Client.Parse_XML(
                                                            serverxmlData,
                                                            "<RSPMSG>",
                                                            "</RSPMSG>"),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                            if (dialogZx != null && (dialogZx.isShowing())) {
                                                dialogZx.dismiss();
                                            }
                                            SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                            DummyContent.ITEM_MAP.clear();
                                            DummyContent.ITEMS.clear();
                                            Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                            ItemListActivity.this.startActivity(intent);
                                        }
                                    });
                                }
                                final List<BillDailyApply_Class> list = PULLParse_BillDaily_Apply
                                        .getBillDailyApplyList(new ByteArrayInputStream(
                                                serverxmlData.getBytes()));
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                            progressDialog = null;
                                        }
                                        showRiJieXiangQingDialog(
                                                list,
                                                "<TICKET>"
                                                        + Client.Parse_XML(
                                                        serverxmlData,
                                                        "<TICKET>",
                                                        "</TICKET>")
                                                        + "</TICKET>"
                                        );
                                    }
                                });
                            } catch (ConnectException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.login_message_supply_server_error)
                                );
                                message.setData(bundle);
                                ihandler.sendMessage(message);
                            } catch (SocketTimeoutException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.login_message_server_response_timeout)
                                );
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
                                        getResources()
                                                .getString(
                                                        R.string.login_message_unknow_error)
                                );
                                message.setData(bundle);
                                ihandler.sendMessage(message);
                            } finally {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        btn_scjl.setEnabled(true);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });

            ImageView btnCloseDialog = (ImageView) dialogZxView
                    .findViewById(R.id.btnCloseDialog);
            btnCloseDialog.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (dialogZx != null && (dialogZx.isShowing())) {
                        dialogZx.dismiss();
                    }
                }
            });
            Button btn_zjtc = (Button) dialogZxView.findViewById(R.id.btn_zjtc);
            btn_zjtc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(
                                ItemListActivity.this);
                    }
                    progressDialog
                            .setTitle(R.string.login_progressdialog_title);
                    progressDialog.setMessage(getResources().getString(
                            R.string.logout_progressdialog_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                TreeMap<String, String> paramMap = new TreeMap<String, String>();
                                paramMap.put("IMEI", SystemUtil
                                        .getIMEI(ItemListActivity.this));
                                paramMap.put("REQUEST_TIME",
                                        SystemUtil.getCurrentDateTimeHH24());
                                paramMap.put("SESSION_ID",
                                        GlobalParams.SESSION_ID);
                                paramMap.put(
                                        "LOCAL_LANGUAGE",
                                        SystemUtil
                                                .getLocalLanguage(ItemListActivity.this)
                                );
                                paramMap.put("SIGN_TYPE", "1");
                                String sign = ParentRequset
                                        .bulidParam(paramMap);
                                String signature = Md5Algorithm.getInstance()
                                        .md5Digest(sign.getBytes());
                                String xmlData = "<ROOT><TOP><IMEI>"
                                        + SystemUtil
                                        .getIMEI(ItemListActivity.this)
                                        + "</IMEI><SESSION_ID>"
                                        + GlobalParams.SESSION_ID
                                        + "</SESSION_ID><SOURCE>3</SOURCE><REQUEST_TIME>"
                                        + SystemUtil.getCurrentDateTimeHH24()
                                        + "</REQUEST_TIME><LOCAL_LANGUAGE>"
                                        + SystemUtil
                                        .getLocalLanguage(ItemListActivity.this)
                                        + "</LOCAL_LANGUAGE></TOP><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>"
                                        + signature
                                        + "</SIGNATURE></TAIL></ROOT>";
                                Log.v("直接退出请求:", xmlData);
                                final String serverxmlData = Client
                                        .ConnectServer("PLogout", xmlData);
                                Log.v("直接退出响应:", serverxmlData);
                                PULLParse_Logout
                                        .getLogoutList(new ByteArrayInputStream(
                                                serverxmlData.getBytes()));
                                if (dialogZx != null && (dialogZx.isShowing())) {
                                    dialogZx.dismiss();
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.logout_message_success)
                                );
                                message.setData(bundle);
                                SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                ItemListActivity.this.startActivity(intent);
                            } catch (ConnectException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.logout_message_success)
                                );
                                message.setData(bundle);
                                ihandler.sendMessage(message);

                                SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                ItemListActivity.this.startActivity(intent);
                            } catch (SocketTimeoutException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.logout_message_success)
                                );
                                message.setData(bundle);
                                ihandler.sendMessage(message);

                                SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                ItemListActivity.this.startActivity(intent);
                            } catch (OtherException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("ErrorMsg", e.getMessage());
                                message.setData(bundle);
                                ihandler.sendMessage(message);

                                SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                ItemListActivity.this.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString(
                                        "ErrorMsg",
                                        getResources()
                                                .getString(
                                                        R.string.logout_message_success)
                                );
                                message.setData(bundle);
                                ihandler.sendMessage(message);

                                SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                                ItemListActivity.this.startActivity(intent);
                            }
                        }
                    }).start();
                }
            });
        }
    }

    private static class Ihandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public Ihandler(ItemListActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            SystemUtil.displayToast(mActivity.get(),
                    msg.getData().getString("ErrorMsg"));
            super.handleMessage(msg);
        }
    }

    private Ihandler ihandler = new Ihandler(ItemListActivity.this);

    // 从服务器获取公告信息并显示到主界面
    private void getNotify() throws Exception {
        String xmlData = "<ROOT><TOP><VERSION>"
                + SystemUtil.getAppVersionName(ItemListActivity.this)
                + "</VERSION><SOURCE>3</SOURCE><IMEI>"
                + SystemUtil.getIMEI(ItemListActivity.this)
                + "</IMEI><REQUEST_TIME>" + SystemUtil.getCurrentDateTimeHH24()
                + "</REQUEST_TIME><LOCAL_LANGUAGE>"
                + SystemUtil.getLocalLanguage(ItemListActivity.this)
                + "</LOCAL_LANGUAGE></TOP></ROOT>";
        Log.v("获取公告请求:", xmlData);
        final String serverxmlData = Client.ConnectServer("PNotifyQuery",
                xmlData);
        Log.v("获取公告响应:", serverxmlData);
        // 服务器返回系统超时，返回到登录页面
        if (Client.Parse_XML(serverxmlData, "<RSPCOD>", "</RSPCOD>").equals(
                "00011")) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(
                            ItemListActivity.this,
                            Client.Parse_XML(serverxmlData, "<RSPMSG>",
                                    "</RSPMSG>"), Toast.LENGTH_LONG
                    ).show();
                    SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                    DummyContent.ITEM_MAP.clear();
                    DummyContent.ITEMS.clear();
                    Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                    ItemListActivity.this.startActivity(intent);
                }
            });
        }
        List<String> list = PULLParse_Notify_Query
                .getNotifyList(new ByteArrayInputStream(serverxmlData
                        .getBytes()));
        stringBuilder = new StringBuilder();
        notifyStringBuilder = new StringBuilder();
        for (String string : list) {
            stringBuilder.append(string + "     ");
            notifyStringBuilder.append(string + "\n");
        }
        if (stringBuilder != null && stringBuilder.length() != 0) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tv_notify.setText(Html.fromHtml(stringBuilder.toString()));
                    stringBuilder = null;
                }
            });
        }
    }

    // 从数据库获取打印模板参数并赋值到GlobalParams
    private void setGlobalPrinterParams() {
        BaseDao<PrinterTemp, Integer> baseDao = new BaseDao<PrinterTemp, Integer>(
                ItemListActivity.this, PrinterTemp.class);
        if (baseDao.isExists(1)) {
            String tempList = baseDao.findById(1).getTemp_list();
            if (tempList != null) {
                String[] temp = tempList.split(";");
                for (String string : temp) {
//                    Printer.setTemplet(string);
                    gprinter.setTemplet(string);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_1:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(1);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_2:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(2);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_3:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(3);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_4:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(4);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_5:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(5);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_6:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(6);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_7:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(7);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_8:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(8);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_9:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == keyCode) {
                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyDownCB.keyValue(9);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_PERIOD:
                    if ("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)) {
                        if (System.currentTimeMillis() - exitTime > 2000) {
                            exitTime = System.currentTimeMillis();
                        } else {
                            if (mShortCutKeyCode == KeyEvent.KEYCODE_PERIOD) {
                                spinner_menu.setSelection(0);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_POUND:
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                    } else {
                        if (mShortCutKeyCode == KeyEvent.KEYCODE_POUND) {
                            setDefaultFragment();
                            ListFragment listFragment = ((ItemListFragment) getFragmentManager()
                                    .findFragmentById(R.id.item_list));
                            ListView listView = listFragment.getListView();
                            listView.setItemChecked(0, true);
                            listView.setSelection(0);
                        }
                    }
                    break;

                case KeyEvent.KEYCODE_ENTER:
                    if (Pre_ID.equalsIgnoreCase("1")
                            || Pre_ID.equalsIgnoreCase("2")
                            || Pre_ID.equalsIgnoreCase("3")) {
                        if (!isEnterTrigger) {
                            mShortCutKeyCode = keyCode;
                            return false;
                        }
                        if ("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)) {
                            if (mShortCutKeyCode == KeyEvent.KEYCODE_PERIOD) {
                                if (System.currentTimeMillis() - exitTime > 2000) {
                                    exitTime = System.currentTimeMillis();
                                    if (mShortCutKeyDownCB != null) {
                                        mShortCutKeyCode = keyCode;
                                        mShortCutKeyDownCB.keyValue(28);
                                        return false;
                                    }// Enter
                                } else {
                                    if (mShortCutKeyDownCB != null) {
                                        mShortCutKeyCode = keyCode;
                                        mShortCutKeyDownCB.keyValue(248);
                                        return false;
                                    }
                                }
                            } else {
                                if (mShortCutKeyDownCB != null) {
                                    mShortCutKeyCode = keyCode;
                                    mShortCutKeyDownCB.keyValue(28);
                                    return false;
                                }// Enter
                            }
                        } else {
                            if (System.currentTimeMillis() - exitTime > 2000) {
                                exitTime = System.currentTimeMillis();
                            } else {
                                if (mShortCutKeyCode == KeyEvent.KEYCODE_POUND) {
                                    if (mShortCutKeyDownCB != null) {
                                        mShortCutKeyCode = keyCode;
                                        mShortCutKeyDownCB.keyValue(248);
                                    }
                                    return false;
                                }
                            }

                            if (mShortCutKeyDownCB != null) {
                                mShortCutKeyCode = keyCode;
                                mShortCutKeyDownCB.keyValue(28);
                                return false;
                            }// Enter
                        }
                    } else {
                        mShortCutKeyCode = keyCode;
                        return false;
                    }
                    break;
                default:
                    break;
            }
            mShortCutKeyCode = keyCode;
        }
        return super.dispatchKeyEvent(event);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	backT += 1;
//		if(backT > 1){
//			PowertechApplication.getInstance().AppExit();
//		}else{
//			if(runBackIm!=null){  
//				 runBackIm.interrupt();  
//				 runBackIm = null;  
//				 }  
//			 (runBackIm = new runBack()).start();
//			SystemUtil.displayToast(ItemListActivity.this, R.string.toast_back);
//		}
//        return super.onKeyDown(keyCode, event);
//    }

    public void setOnBackPressedListener(
            OnBackPressedListener mOnBackPressedListener) {
        this.mOnBackPressedListener = mOnBackPressedListener;
    }

    public interface OnBackPressedListener {
        void onPressed();
    }

    public void setShortCutsKeyDownCallBack(
            ShortCutsKeyDownCallBack mShortCutsKeyDownCallBack) {
        this.mShortCutKeyDownCB = mShortCutsKeyDownCallBack;
    }

    public interface ShortCutsKeyDownCallBack {
        void keyValue(int selectKey);
    }

    public void setOnReadCardMessageCallBack(
            ReadCardMessageCallBack mReadCardMessageCallBack) {
        this.mReadCardMessageCallBack = mReadCardMessageCallBack;
    }

    public interface ReadCardMessageCallBack {
        void readCardMessage(String offset, List<String> list);

    }

    // 注销生成记录后的日结详情对话框
    private void showRiJieXiangQingDialog(
            final List<BillDailyApply_Class> list, final String ticket) {
        final Dialog dialogZx1 = new Dialog(this, R.style.FullHeightDialog);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogZxView = li.inflate(R.layout.dialog_rijiexiangqing, null);
        dialogZx1.setContentView(dialogZxView);
        dialogZx1.show();

        TextView tv_rjxq = (TextView) dialogZxView.findViewById(R.id.tv_rjxq);
        StringBuilder stringBuilder = new StringBuilder();
        for (BillDailyApply_Class billDailyApply_Class : list) {
            String fta_status = "";
            if (billDailyApply_Class.getFTA_STATUS().equals("0")) {
                fta_status = getResources().getString(
                        R.string.logout_print_status_zaitu);
            } else if (billDailyApply_Class.getFTA_STATUS().equals("1")) {
                fta_status = getResources().getString(
                        R.string.logout_print_status_daozhang);
            }
            stringBuilder.append(getResources().getString(
                    R.string.logout_dialog_rj_tv_number)
                    + "\n"
                    + billDailyApply_Class.getTOF_NO()
                    + "\n"
                    + getResources().getString(
                    R.string.logout_dialog_rj_tv_date)
                    + billDailyApply_Class.getTOF_DATE()
                    + "\n"
                    + getResources().getString(
                    R.string.logout_dialog_rj_tv_money)
                    + billDailyApply_Class.getTOF_AMT()
                    + "\n"
                    + getResources().getString(
                    R.string.logout_dialog_rj_tv_zhuangtai)
                    + fta_status + "\n");
        }
        tv_rjxq.setText(stringBuilder);

        final Button btn_print = (Button) dialogZxView.findViewById(R.id.btn_d_dayin);
        btn_print.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				if (progressDialog == null) {
					progressDialog = CustomProgressDialog.createProgressDialog(ItemListActivity.this, 10000, new CustomProgressDialog.OnTimeOutListener() {

						@Override
						public void onTimeOut(CustomProgressDialog dialog) {
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("ErrorMsg", getString(R.string.printer_status_timeout));
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
				progressDialog.setMessage(getString(R.string.printering));
				progressDialog.setCancelable(false);
				progressDialog.show();
            	btn_print.setEnabled(false);
            	new Thread(new Runnable() {

					@Override
					public void run() {
//		                Printer printer = new Printer();
//		                printer.start();
//		                printer.reset();
//		                printer.printXML(ticket);
//		                int printResult = printer.commitOperation();
			        	gprinter.printXML(ticket);
			        	String result = gprinter.commitOperation();
						Message message = new Message();
						Bundle bundle = new Bundle();
			        	if(result==null){
			        		result="Printer Success";
			        	}
			        	bundle.putString("ErrorMsg", result);
		              /*  switch (printResult) {
		                    case 0:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_success));
		                        break;
		                    case -1001:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_nopaper));
		                        break;
		                    case -1002:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_hot));
		                        break;
		                    case -1003:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_full));
		                        break;
		                    case -1004:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_noconnect));
		                        break;
		                    case -9999:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_other_error));
		                        break;
		                    default:
		                        break;
		                }*/
						message.setData(bundle);
						ihandler.sendMessage(message);
//		                printer.stop();
					}
				}).start();
                btn_print.setEnabled(true);
            }
        });

        ImageView imageView = (ImageView) dialogZxView
                .findViewById(R.id.btnCloseDialog);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialogZx1 != null && (dialogZx1.isShowing())) {
                    dialogZx1.dismiss();
                }
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status",
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        iv_battery.setImageResource(R.drawable.battery_charge);
                        iv_battery.setImageLevel((level * 100) / scale);
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        iv_battery.setImageResource(R.drawable.battery_not_charge);
                        iv_battery.setImageLevel((level * 100) / scale);
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        iv_battery.setImageResource(R.drawable.battery_not_charge);
                        iv_battery.setImageLevel((level * 100) / scale);
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        iv_battery
                                .setImageResource(R.drawable.stat_sys_battery_unknown);
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        int plugged = intent.getIntExtra("plugged", 0);
                        if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                            iv_battery
                                    .setImageResource(R.drawable.stat_sys_battery_charge_anim100);
                        } else {
                            iv_battery
                                    .setImageResource(R.drawable.stat_sys_battery_100);
                        }
                        break;
                }
            } else if (intent.getAction().equals(
                    WifiManager.RSSI_CHANGED_ACTION)) {
                iv_wifi.setImageResource(R.drawable.wifi_level);
                iv_wifi.setImageLevel(getWifiLevel());
            } else if (intent.getAction().equals(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                setNetWorkStatus();
            } else if (intent.getAction().equals(GlobalParams.UPDATE_JINRISHOUDIAN_ACTION)) {
                setTextViewJinRiShouDian();
            } else if (intent.getAction().equals(GlobalParams.UPDATE_YAJINYUER_ACTION)) {
                tv_baozhengjinyue.setText(GlobalParams.CASH_AC_BAL);
            }
        }
    };

    // 获取wifi信号强度
    private int getWifiLevel() {
        int mRssi = -200;
        int level = 0;
        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
        mRssi = mWifiInfo.getRssi();
        level = WifiManager.calculateSignalLevel(mRssi, 4);
        return level;
    }

    // 监测手机信号强度变化，并改变相应图标
    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level;
            int asu = signalStrength.getGsmSignalStrength();
            if (asu <= 2 || asu == 99)
                level = 0;
            else if (asu >= 12) {
                level = 4;
            } else if (asu >= 8) {
                level = 3;
            } else if (asu >= 5) {
                level = 2;
            } else {
                level = 1;
            }
            iv_gsm.setImageResource(R.drawable.gsm_strength);
            iv_gsm.setImageLevel(level);
        }

    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        mOnBackPressedListener = null;
        if (capture != null) {
            StopScan();
        }
//        unregisterReceiver(mCardMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        GlobalParams.If_CloseFlashLight = true;
        if (capture != null) {
            StopScan();
        }
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timerTask_clock != null) {
            timerTask_clock.cancel();
        }
        unregisterReceiver(broadcastReceiver);
    }

    // 从数据库获取今日收费数据并显示到主界面
    public void setTextViewJinRiShouDian() {
        BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
        		ItemListActivity.this, JinRiShouDian.class);
        List<JinRiShouDian> li = baseDao.findAllByField("time",
                SystemUtil.getCurrentDate());
        float money = 0.00F;
        String mMoney = "0";
        if (baseDao.isExists(1)) {
            if (li.size() > 0) {
                for (JinRiShouDian jinRiShouDian : li) {
                    money += Float.valueOf(jinRiShouDian.getMoney());
                }
                DecimalFormat mDecimalFormat = new DecimalFormat("#.00");
                mMoney = mDecimalFormat.format(money);
            }
        }
        tv_jinrishoudian.setText(mMoney
                + "/"
                + li.size()
                + getResources().getString(
                R.string.main_welcome_jingrishoudian_unit));
    }

    // 设置网络状态图标
    public void setNetWorkStatus() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable())
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                iv_wifi.setVisibility(View.GONE);
                iv_gsm.setVisibility(View.GONE);
                iv_ethernet.setVisibility(View.GONE);
                iv_wifi.setImageResource(R.drawable.wifi_level);
                iv_wifi.setImageLevel(getWifiLevel());
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                iv_wifi.setVisibility(View.GONE);
                iv_gsm.setVisibility(View.GONE);
                iv_ethernet.setVisibility(View.GONE);
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                iv_wifi.setVisibility(View.GONE);
                iv_gsm.setVisibility(View.GONE);
                iv_ethernet.setVisibility(View.GONE);
            }
    }

    private final BroadcastReceiver mCardMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "icc present Broadcast Receiver");
            int dataNum = 0;
            List<Integer> numList = new ArrayList<Integer>();
            List<String> dataList = new ArrayList<String>();
            if (intent.getAction() == ReaderMonitor.ACTION_ICC_PRESENT) {
                if (intent.getExtras().getBoolean(
                        ReaderMonitor.EXTRA_IS_PRESENT)) {
                    int cardType = intent.getExtras().getInt(
                            ReaderMonitor.EXTRA_CARD_TYPE);
                    if (cardType == CardReader.CARD_TYPE_SLE4428) {
                        GlobalParams.CARD_TYPE = "1";
//                        byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF};
//                        ReaderMonitor.pscVerify(psc);
                        if (mReadCardMessageCallBack != null) {
                            Log.d(TAG, "mReadCardMessageCallBack!=null");
                            numList = parsingJsonData(GlobalParams.READ4428);
                            if (numList != null) {
                                dataNum = numList.size() / 2;
                            }
                            Log.e(TAG, "dataNum = " + dataNum);
                            for (int i = 0; i < dataNum; i++) {
                                byte[] temp = ReaderMonitor.readMainMemory(
                                        numList.get(i),
                                        numList.get(i + dataNum));
                                if (temp != null) {
                                    dataList.add(StringUtil.BCD2Str(temp));
                                } else {
                                    Log.e(TAG,
                                            "mSLE4428Reader.readMainMemory = null");
                                }

                            }
                            mReadCardMessageCallBack.readCardMessage(mOffset,
                                    dataList);
                        }
                    } else if (cardType == CardReader.CARD_TYPE_SLE4442) {
                        GlobalParams.CARD_TYPE = "2";
//                        byte[] psc = new byte[]{(byte) 0xFF, (byte) 0xFF,
//                                (byte) 0xFF};
//                        ReaderMonitor.pscVerify(psc);
                        if (mReadCardMessageCallBack != null) {
                            numList = parsingJsonData(GlobalParams.READ4442);
                            if (numList != null) {
                                dataNum = numList.size() / 2;
                            }
                            Log.e(TAG, "dataNum = " + dataNum);
                            for (int i = 0; i < dataNum; i++) {
                                byte[] temp = ReaderMonitor.readMainMemory(
                                        numList.get(i),
                                        numList.get(i + dataNum));
                                if (temp != null) {
                                    dataList.add(StringUtil.BCD2Str(temp));
                                } else {
                                    Log.e(TAG,
                                            "mSLE4442Reader.readMainMemory = null");
                                }
                            }
                            mReadCardMessageCallBack.readCardMessage(mOffset,
                                    dataList);
                        }
                    } else {
                        Log.e(TAG, "Card Type Unknow!");
                    }
                } else {
                    Log.e(TAG, "NO Card");
                }
            } else if (intent.getAction() == ReaderMonitor.ACTION_MSC) {
                GlobalParams.CARD_TYPE = "3";
                String[] trackData = intent.getExtras().getStringArray(
                        ReaderMonitor.EXTRA_MSC_TRACK);
                StringBuilder builder = new StringBuilder();
                // String t1=trackData[0];
                // String t2=trackData[1];
                // String t3=trackData[2];
                // for(int i=0;i<(79-trackData[0].length());i++){
                // t1+="0";
                // }
                // for(int i=0;i<(40-trackData[0].length());i++){
                // t2+="0";
                // }
                // for(int i=0;i<(107-trackData[0].length());i++){
                // t3+="0";
                // }
                // Log.e(TAG,"t1 = "+t1);
                // Log.e(TAG,"t2 = "+t2);
                // Log.e(TAG,"t3 = "+t3);
                // builder.append(t1+t2+t3);
                builder.append(trackData[0] + "|" + trackData[1] + "|"
                        + trackData[2]);
                if (mReadCardMessageCallBack != null) {
                    // numList = parsingJsonData(GlobalParams.MCARD);
                    // if (numList != null) {
                    // dataNum = numList.size() / 2;
                    // }
                    // Log.e(TAG, "dataNum = " + dataNum);
                    // for (int i = 0; i < dataNum; i++) {
                    // String temp=builder.subSequence(numList.get(i),
                    // numList.get(i)+numList.get(i + dataNum)).toString();
                    // if (!temp.equalsIgnoreCase("")) {
                    // dataList.add(temp);
                    // } else {
                    // Log.e(TAG, "Track Reader.readMainMemory = null");
                    // }
                    // }
                    dataList.clear();
                    dataList.add(builder.toString());
                    mReadCardMessageCallBack.readCardMessage(mOffset, dataList);
                }

            }
        }

    };

    private List<Integer> parsingJsonData(String jsonStr) {
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

    private void Delay() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            public void run() {
                If_Double_Click = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setEnabled(true);
                    }
                });
            }
        };
        timer.schedule(task, 1 * 500);
    }

    public void Scan(final Handler handler) {
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
            CameraManager.init(ItemListActivity.this.getApplication());
            capture = new Capture(ItemListActivity.this, surfaceView,
                    viewfinderView);
            capture.Scan(handler);
        }
    }

    public void continuePreview(Handler handler) {
        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550")) {
            CameraManager.init(ItemListActivity.this.getApplication());
            capture = new Capture(ItemListActivity.this, surfaceView,
                    viewfinderView);
            capture.Scan(handler, true);
            Handler mhdl = capture.getHandler();
            if (mhdl != null) {
                ((CaptureActivityHandler) mhdl).restartPreviewAndDecode();
            }
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 扫描成功
                case 0x00:
                    if (GlobalParams.BARCODE_FORMAT.equalsIgnoreCase("QR_CODE")) {
                        GlobalParams.CARD_TYPE = "4";// 二维码
                    } else {
                        GlobalParams.CARD_TYPE = "5";// 条形码
                    }
                    if (mReadCardMessageCallBack != null) {
                        List<String> list = new ArrayList<String>();
                        list.add(GlobalParams.QR_Info);
                        mReadCardMessageCallBack.readCardMessage(mOffset, list);
                    }
                    // mAutoReadCardTask = new
                    // AutoReadCardTask(GlobalParams.QR_Info)
                    // .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;

                case 0x01:
                    // if (!isScanerRunning) {
                    // ((ItemListActivity) getActivity()).continuePreview(handler);
                    // isScanerRunning = true;
                    // }
                    break;
            }
        }
    };

    public void StopScan() {
        if (capture != null) {
            Log.e(TAG, "Stop Camera!");
            capture.Stop();
            capture = null;
        } else {
            Log.e(TAG, "NON-ST Camera!");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                  && event.getRepeatCount() == 0) {
        	FragmentManager fragmentManager = ItemListActivity.this.getFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.item_detail_container);
            if(fragment instanceof Fragmentshoufei || fragment instanceof FragmentDianXinMain){
            	setDefaultFragment();
	            spinner_menu.setSelection(0, true);
	            return true;
            }
        	CustomDialog.Builder dialog = new CustomDialog.Builder(ItemListActivity.this);
        	dialog.setTitle("");
        	dialog.setMessage(R.string.btn_keyback_title);
        	dialog.setPositiveButton(R.string.btn_keyback_yes, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					SystemUtil.setGlobalParamsToNull(ItemListActivity.this);
                    DummyContent.ITEM_MAP.clear();
                    DummyContent.ITEMS.clear();
                    Intent intent = new Intent(ItemListActivity.this, LoginActivity.class);
                    ItemListActivity.this.startActivity(intent);
				}
        	});
        	dialog.setNegativeButton(R.string.btn_keyback_no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
        	dialog.create().show();

              return true;
          }
          return super.onKeyDown(keyCode, event);
      }

}
