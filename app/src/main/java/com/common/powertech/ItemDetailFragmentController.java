package com.common.powertech;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.acs.smartcard.Features;
import com.acs.smartcard.PinProperties;
import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.acs.smartcard.TlvProperties;
import com.common.powertech.ItemListActivity.ReadCardMessageCallBack;
import com.common.powertech.ItemListActivity.ShortCutsKeyDownCallBack;
import com.common.powertech.activity.BuyTokenActivity;
import com.common.powertech.activity.FragmentChongZhengShenQingDetail;
import com.common.powertech.activity.FragmentDaoZhangQueRenDetail;
import com.common.powertech.activity.FragmentDianXinMain;
import com.common.powertech.activity.FragmentNoMenu;
import com.common.powertech.activity.FragmentPinzhengbudaDetail;
import com.common.powertech.activity.FragmentShouFeiRiJieDetail;
import com.common.powertech.activity.FragmentShouZhiMingXi;
import com.common.powertech.activity.FragmentXiTongSheZhiMain;
import com.common.powertech.activity.FragmentXiaoShouJiLu;
import com.common.powertech.activity.FragmentYaJinChongZhiMain;
import com.common.powertech.activity.FragmentZhuanZhangJiLu;
import com.common.powertech.activity.Fragmentshoufei;
import com.common.powertech.activity.NetWorkSettingActivity;
import com.common.powertech.activity.RefundTokenActivity;
import com.common.powertech.bussiness.Request_Zidongduka;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;
import com.myDialog.softinput.HexSoftInput;
import com.telpo.tps550.api.decode.Decode;
import com.zbar.lib.CaptureActivity;
import com.zxing.Capture;
import com.zxing.view.ViewfinderView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 广东天波信息技术股份有限公司
 * 功能：Fragment界面跳转控制器
 * 作者:luyq
 * 日期:2015-10-12
 */

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragmentController extends Fragment implements ReadCardMessageCallBack {
	private static final String TAG = "ItemDetailFragmentController";

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

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
    
	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;
	private AutoCompleteTextView inputCond;
	private ArrayAdapter<String> arrayAdapter;
	private List<String> numberList = new ArrayList<String>();
	private ItemListActivity mActivity;

	private String mRspMeg = "";
	PowertechApplication app ;

	// private AlertDialog mICTypeSelectDialog;
	private AsyncTask<Void, Void, Integer> mAutoReadCardTask;
	String mMeterNo;
	String mUserNo;
	String mIcFlag;
	private Button buttonByUser, buttonByElecWatch, buttonChongzhengQuery, buttonBytypeWatch;

	private boolean isRunAutoReadCard = false;
	String id;
	private Capture capture;
	private static boolean isfragmentRunning = false;

	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;
	private boolean isAutoReadCardRuned = false;// 自动读卡是否执行过而进入购电或收费
	private static CustomProgressDialog progressDialog = null;
	private ImageButton img_btn;
	private ImageButton img_read;
    private ImageButton img_btn1;
    private ImageButton img_btn2;
    private ImageButton img_btn3;
    private ImageButton img_btn4;
    String ResourceType;
    private Spinner spinner;
    private List<String> data_list;
    private String enel="";//电力公司
    private static Button msailelec,msailair,msailcomm,msailwater,msailtoken,msailrefund;
	private static Button  buttonByMain;
	private int el_select = 0;
	private int rushFlag = 0;
	private List<String> spinnerList,spinnerListid;
	private String spinneroncl;
	private Map<String,String> map =new HashMap<String, String>();
	
	private boolean if_btn_click = false; // 防按钮双击
	private String language = "";
	private UsbManager mManager;
	private Reader mReader;
    private PendingIntent mPermissionIntent;
    private Features mFeatures = new Features();
    private String mOffset = "";
    private boolean ISDEBUG=false;
    private Spinner mSlotSpinner;
    private String mValue="";
	private boolean continueClick = true; // 防按钮连续点击
    private GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private PrinterServiceConnection conn = null;
	private              int                       mPrinterId           = 0;
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

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragmentController() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "onCreate");
		isfragmentRunning = false;
		super.onCreate(savedInstanceState);
		mActivity = (ItemListActivity) getActivity();
//		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
		}
		// if (!isScanerRunning) {
		// if (GlobalParams.If_CloseFlashLight) {
		// Log.e(TAG, "stART!");
		// ((ItemListActivity) mActivity).Scan(handler);
		// } else {
		// Log.e(TAG, "stART!");
		// ((ItemListActivity) mActivity).continuePreview(handler);
		// }
		// GlobalParams.If_CloseFlashLight = false;
		// // isScanerRunning = true;
		// }
        connection();
	}
	  private void connection() {
	        conn = new PrinterServiceConnection();
	        Intent intent = new Intent(getActivity().getApplicationContext(), GpPrintService.class);
	        getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		View rootView = null;
		String selectItemId = mItem.id;
		id = selectItemId;
		Log.e(TAG, "selectItemId = " + selectItemId);
		isRunAutoReadCard = false;
		language = SystemUtil.getLocalLanguage(mActivity);
		
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
        
		if ("1".equals(selectItemId)) {
			// ReaderMonitor.setContext(mActivity.getApplicationContext());
			// ReaderMonitor.startMonitor();
			// 收费
			ItemListActivity.isEnterTrigger = true;
//			isRunAutoReadCard = true;
			if (GlobalParams.MENU_NAME == null || GlobalParams.MENU_NAME.size() <= 0) {
				if (mActivity == null) {
					mActivity = (ItemListActivity) getActivity();
				}
				FragmentManager fm = mActivity.getFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				FragmentNoMenu fragmentNoMenu = new FragmentNoMenu();
				transaction.replace(R.id.item_detail_container, fragmentNoMenu);
				transaction.commit();
			} else {
                if(GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")){
                	
                	rootView = inflater.inflate(R.layout.fragment_shofei_sel,
                  container, false);
        }
                	msailelec = (Button) rootView.findViewById(R.id.btn_selectelec);
                	msailwater = (Button) rootView.findViewById(R.id.btn_selectwater);
           		 	msailair = (Button) rootView.findViewById(R.id.btn_selectair);
           		 	msailcomm = (Button) rootView.findViewById(R.id.btn_selectcomm);
           		 	msailtoken = (Button) rootView.findViewById(R.id.btn_selecttoken);
           		 	msailrefund = (Button)rootView.findViewById(R.id.btn_selectrefund);
           		 	buttonByMain = (Button) rootView.findViewById(R.id.buttonByMain);
           		 	spinner  = (Spinner) rootView.findViewById(R.id.spinner_choosecompany);
           		    buttonByMain.setTextSize(20);
           		 	
           		    
           	   String PRDTYPEALL = app.getPRDTYPELIMIT();
           		  if(PRDTYPEALL== null){
           		 	   PRDTYPEALL ="1";	
           		 	}
           		String[] temp= PRDTYPEALL.split(";");   
           		for (int i = 0; i < temp.length; i++) 
			      {
           			String PRDTYPE=temp[i].trim();
           			if(PRDTYPE.equals("1")){
        				msailelec.setVisibility(View.VISIBLE);
        			 }
           			if(PRDTYPE.equals("2")){
           				msailwater.setVisibility(View.VISIBLE);
        			 }
           			if(PRDTYPE.equals("3")){
           				msailair.setVisibility(View.VISIBLE);
        			 }
           			if(PRDTYPE.equals("4")){
           			 msailcomm.setVisibility(View.VISIBLE);
        			 }
           			if(PRDTYPE.equals("5")){
           				msailtoken.setVisibility(View.VISIBLE);
           			 }
           			if(PRDTYPE.equals("6")){
           				msailrefund.setVisibility(View.VISIBLE);
           			 }
           		}
           		
           		
           	
           		if(temp.length>0){
    		 		el_select=Integer.valueOf(temp[0].trim());
    		 		rushFlag=Integer.valueOf(temp[0].trim());
    		 	}else el_select = 1;
           		      rushFlag = 1;
	        	loadDataForSpinner();
//		         msailelec.setBackgroundResource(R.drawable.electryactive);
           		 OnClickListener mOnClickListeners = new OnClickListener() {
           			boolean spinnerFirst = true;
         			@Override
         			public void onClick(View v) {
         				spinner.setVisibility(View.VISIBLE);
         		         switch (v.getId()) {
         		         case R.id.btn_selectelec:
         		        	 el_select=1;
         		        	 if(el_select!=rushFlag){
         		        		rushFlag=1;
             		        	setButtonUnable();
             		        	loadDataForSpinner();
         		        	 }
         		        	 
//         		        	 msailelec.setBackgroundResource(R.drawable.electryactive);
         		        	 break;
         		         case R.id.btn_selectwater:
         		        	 el_select=2;
         		        	if(el_select!=rushFlag){
         		        	 rushFlag=2;
         		        	 setButtonUnable();
         		        	 loadDataForSpinner();
         		        	}
//         		        	msailwater.setBackgroundResource(R.drawable.wateractive);
         		        	 break;
         		         case R.id.btn_selectair:
         		        	 el_select=3;
         		        	if(el_select!=rushFlag){
         		        	 rushFlag=3;
         		        	 setButtonUnable();
         		        	 loadDataForSpinner();
         		        	}
//         		        	msailair.setBackgroundResource(R.drawable.gasactive);
         		        	 break;
         		         case R.id.btn_selectcomm:
         		        	 el_select=4;
         		        	if(el_select!=rushFlag){
         		        	 rushFlag=4;
         		        	 setButtonUnable();
         		        	 loadDataForSpinner();
         		        	}
//         		        	msailcomm.setBackgroundResource(R.drawable.phoneactive);
         		        	 break;
         		        case R.id.btn_selecttoken:
         		        	spinner.setVisibility(View.GONE);
        		        	 el_select=5;
        		        	if(el_select!=rushFlag){
        		        	 rushFlag=5;
        		        	 setButtonUnable();
        		        	 msailtoken.setBackgroundResource(R.drawable.payfee_active);
//        		        	 loadDataForSpinner();
        		        	}
//        		        	msailcomm.setBackgroundResource(R.drawable.phoneactive);
        		        	 break;
         		       case R.id.btn_selectrefund:
        		        	spinner.setVisibility(View.GONE);
       		        	 el_select=6;
       		        	if(el_select!=rushFlag){
       		        	 rushFlag=6;
       		        	 setButtonUnable();
       		        	msailrefund.setBackgroundResource(R.drawable.refunds_fee_active);
//       		        	 loadDataForSpinner();
       		        	}
//       		        	msailcomm.setBackgroundResource(R.drawable.phoneactive);
       		        	 break;
         		            
         		         }
         			}
         			
    				private void setButtonUnable() {
    					msailelec.setBackgroundResource(R.drawable.electryfee);
    					msailwater.setBackgroundResource(R.drawable.waterfee);
    					msailair.setBackgroundResource(R.drawable.gasfee);
    					msailcomm.setBackgroundResource(R.drawable.phonefee);
    					msailtoken.setBackgroundResource(R.drawable.payfee);
    					msailrefund.setBackgroundResource(R.drawable.refunds_fee);
    				}
         		};
         		
         		msailelec.setOnClickListener(mOnClickListeners);
            	msailwater.setOnClickListener(mOnClickListeners);
       		 	msailair.setOnClickListener(mOnClickListeners);
       		 	msailcomm.setOnClickListener(mOnClickListeners);
       		 	msailtoken.setOnClickListener(mOnClickListeners);
       		 	msailrefund.setOnClickListener(mOnClickListeners);

         		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

    				@SuppressLint("ResourceAsColor")
    				@Override
    				public void onItemSelected(AdapterView<?> parent, View view,
    						int position, long id) {
    					// TODO Auto-generated method stub
    					if(spinnerList.get(position).equals(mActivity.getString(R.string.shoufei_spinner_sel))){
    						TextView tv = (TextView) view;
    						tv.setTextColor(R.color.grey);
    						
    					}
    				}

    				@Override
    				public void onNothingSelected(AdapterView<?> parent) {
    					// TODO Auto-generated method stub
    					
    				}
         			
         		});
       		 	
//         		final LayoutInflater inflater2=inflater;
//         		final ViewGroup container2=container;
         		buttonByMain.setOnClickListener(new OnClickListener() {
         			
         			@Override
         			public void onClick(View widget) {
         				spinneroncl = (String) spinner.getSelectedItem(); 
         				if(el_select == 5){
         					//购买TOKEN
            				Intent intent = new Intent(mActivity, BuyTokenActivity.class);
            				mActivity.startActivity(intent);
         					
         				}else if(el_select == 6){
         					//购买TOKEN
            				Intent intent = new Intent(mActivity, RefundTokenActivity.class);
            				mActivity.startActivity(intent);
         					
         				}else if(el_select!=4){
         					if(spinneroncl==null||spinneroncl.equals(getString(R.string.shoufei_spinner_sel))){
             					SystemUtil.displayToast(mActivity, getString(R.string.shoufei_spinner_sel));
             					return;
             				}
         					
         					if(el_select==1){
            					String selectEnel1= getKeyByValue(map, spinneroncl);
            					app.setSELECTENEL1(selectEnel1);
            				}
            				if(el_select==2){
            					String selectEnel2= getKeyByValue(map, spinneroncl);
            					app.setSELECTENEL2(selectEnel2);
            				}
            				if(el_select==3){
        						String selectEnel3= getKeyByValue(map, spinneroncl);
            					app.setSELECTENEL3(selectEnel3);
            				}
         					
            				FragmentManager fm = mActivity.getFragmentManager();
                    	    FragmentTransaction transaction = fm.beginTransaction();
                    	    Fragmentshoufei shoufei = new Fragmentshoufei();
                    	    Bundle bundle = new Bundle();
            				bundle.putInt("ResourceType", el_select);
            				bundle.putString("enel_name", spinneroncl);
            				bundle.putString("enel_id",getKeyByValue(map, spinneroncl));
                    	    shoufei.setArguments(bundle);
                    	    transaction.replace(R.id.item_detail_container, shoufei);
                    	    transaction.addToBackStack(null);
                    	    transaction.commit();	

         				}else{
         					if(spinneroncl==null||spinneroncl.equals(getString(R.string.shoufei_spinner_sel))){
             					SystemUtil.displayToast(mActivity, getString(R.string.shoufei_spinner_sel));
             					return;
             				}
         					
         					String selectEnel4= getKeyByValue(map, spinneroncl);
        					app.setSELECTENEL4(selectEnel4);
            				Bundle bundle = new Bundle();
            				bundle.putInt("ResourceType", el_select);
                            bundle.putString("enel_name", spinneroncl);
                            bundle.putString("enel_id",getKeyByValue(map, spinneroncl));           				
            				FragmentManager fm = mActivity.getFragmentManager();
                    	    FragmentTransaction transaction = fm.beginTransaction();
                    	    FragmentDianXinMain dianxinshoufei = new FragmentDianXinMain();
                    	    dianxinshoufei.setArguments(bundle);
                    	    transaction.replace(R.id.item_detail_container, dianxinshoufei);
                    	    transaction.addToBackStack(null);
                    	    transaction.commit();	
         				}	
         			}
		
               });
            }


		} else {
            if ("2".equals(selectItemId)) {
                // ReaderMonitor.setContext(mActivity.getApplicationContext());
                // ReaderMonitor.startMonitor();
                // 凭证补打
                // 每次进来先清空
                ItemListActivity.isEnterTrigger = true;
                GlobalParams.IC_FLAG = "0";
                isRunAutoReadCard = true;
                rootView = inflater.inflate(R.layout.fragment_pinzhengbuda_main, container, false);

                if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                    img_btn = (ImageButton) rootView.findViewById(R.id.btn_scan);
                    img_btn.setOnClickListener(cameraListener);
                    img_read = (ImageButton) rootView.findViewById(R.id.btn_read);
                    img_read.setOnClickListener(icCardQuery);
                }

                buttonByUser = (Button) rootView.findViewById(R.id.buttonByUser);
                if ("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)) {
                    buttonByUser.setText(mActivity.getString(R.string.main_button_anyonghu_390));
                }
                buttonByElecWatch = (Button) rootView.findViewById(R.id.buttonByElecWatch);
                inputCond = (AutoCompleteTextView) rootView.findViewById(R.id.inputCond);
                if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {

                    if (language.equalsIgnoreCase("en")) {
                        inputCond.setTextSize(18);
                    } else if (language.equalsIgnoreCase("fr")) {
                        inputCond.setTextSize(16);
                    }
                }
                hideSoftInputMethod(inputCond);
                if (Preferences.getComplexDataInPreference(mActivity, Preferences.KEY_MeterOrUser_No, "0") != null
                        && !Preferences.getComplexDataInPreference(mActivity, Preferences.KEY_MeterOrUser_No, "0")
                        .toString().equalsIgnoreCase("0")) {
                    numberList = (List<String>) Preferences.getComplexDataInPreference(mActivity,
                            Preferences.KEY_MeterOrUser_No, "0");
                    List<String> tempList = new ArrayList<String>();
                    for (String s : numberList) {
                        boolean isSame = false;
                        for (String str : tempList) {
                            if (s.equalsIgnoreCase(str)) {
                                isSame = true;
                            }
                        }
                        if (!isSame) {
                            tempList.add(s);
                        }
                    }
                    numberList = tempList;
                }
                inputCond.setThreshold(6);
                arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, numberList);
                inputCond.setAdapter(arrayAdapter);

                inputCond.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s == null || s.length() == 0) {
                            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {

                                if (language.equalsIgnoreCase("en")) {
                                    inputCond.setTextSize(18);
                                } else if (language.equalsIgnoreCase("fr")) {
                                    inputCond.setTextSize(16);
                                }
                            }
                            return;
                        }

                        if (start == 0 && count == 1) {
                            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                                if (language.equalsIgnoreCase("en") || language.equalsIgnoreCase("fr")) {
                                    inputCond.setTextSize(22);
                                }
                            }
                        }

                        if (s.toString().contains("#") || s.toString().contains(".") || s.toString().contains("*")) {
                            s = s.toString().replace("#", "");
                            s = s.toString().replace("*", "");
                            s = s.toString().replace(".", "");
                            inputCond.setText(s.toString());
                            inputCond.setSelection(s.length());
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < s.length(); i++) {
                            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                                continue;
                            } else {
                                sb.append(s.charAt(i));
                                if ((sb.length() == 5 || sb.length() == 10 || sb.length() == 15)
                                        && sb.charAt(sb.length() - 1) != ' ') {
                                    sb.insert(sb.length() - 1, ' ');
                                }
                            }
                        }
                        if (!sb.toString().equals(s.toString())) {
                            int index = start + 1;
                            if (sb.length() <= start) {
                                return;
                            }
                            if (sb.charAt(start) == ' ') {
                                if (before == 0) {
                                    index++;
                                } else {
                                    index--;
                                }
                            } else {
                                if (before == 1) {
                                    index--;
                                }
                            }
                            inputCond.setText(sb.toString());
                            inputCond.setSelection(index);
                        }
                        Editable editable = inputCond.getText();
                        int len = editable.length();

                        if (len > 19) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            // 截取新字符串
                            String newStr = str.substring(0, 19);
                            inputCond.setText(newStr);
                            editable = inputCond.getText();

                            // 新字符串的长度
                            int newLen = editable.length();
                            // 旧光标位置超过字符串长度
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            // 设置新光标所在的位置
                            Selection.setSelection(editable, selEndIndex);

                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {

                    }
                });
//			initNumberButton(rootView);
                HexSoftInput hexSoftInput = new HexSoftInput(mActivity, rootView);
                hexSoftInput.setInputCond(inputCond);

                mActivity.setShortCutsKeyDownCallBack(new ShortCutsKeyDownCallBack() {

                    @Override
                    public void keyValue(int selectKey) {
                        if (selectKey == 28) {
                            // 按表号查询快捷键
                            buttonByElecWatch.performClick();
                        }
                        if (selectKey == 248) {
                            // / 按用户查询快捷键
                            buttonByUser.performClick();
                        }
                    }
                });

                buttonByUser.setOnClickListener((new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //连接打印机先
                        boolean[] state = getConnectState();
                        if (state[mPrinterId] != true) {
                            CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
                            builder.setMessage(R.string.str_noopen);
                            builder.setTitle(R.string.str_note);
                            builder.setPositiveButton(R.string.str_goset,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(getActivity(), NetWorkSettingActivity.class);
                                            boolean[] state = getConnectState();
                                            intent.putExtra(CONNECT_STATUS, state);
                                            intent.putExtra("flag_1", 1);
                                            getActivity().startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton(R.string.str_cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if (inputCond.getText().length() == 0) {
                                                SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
                                                return;
                                            }
                                            isRunAutoReadCard = false;
                                            if (mActivity == null) {
                                                mActivity = (ItemListActivity) getActivity();
                                            }
                                            FragmentManager fm = mActivity.getFragmentManager();
                                            FragmentTransaction transaction = fm.beginTransaction();
                                            ItemListActivity.isEnterTrigger = false;
                                            FragmentPinzhengbudaDetail fragmentPinzhengbudaDetail = new FragmentPinzhengbudaDetail();
                                            Bundle bundle = new Bundle();
                                            String queryType = "1";
                                            bundle.putString("inputCond",
                                                    StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
                                            bundle.putString("queryType", queryType);
                                            fragmentPinzhengbudaDetail.setArguments(bundle);
                                            transaction.replace(R.id.item_detail_container, fragmentPinzhengbudaDetail);
                                            transaction.commit();

                                        }
                                    });
                            builder.create().show();
                        } else {
                            if (inputCond.getText().length() == 0) {
                                SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
                                return;
                            }
                            isRunAutoReadCard = false;
                            if (mActivity == null) {
                                mActivity = (ItemListActivity) getActivity();
                            }
                            FragmentManager fm = mActivity.getFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            ItemListActivity.isEnterTrigger = false;
                            FragmentPinzhengbudaDetail fragmentPinzhengbudaDetail = new FragmentPinzhengbudaDetail();
                            Bundle bundle = new Bundle();
                            String queryType = "1";
                            bundle.putString("inputCond",
                                    StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
                            bundle.putString("queryType", queryType);
                            fragmentPinzhengbudaDetail.setArguments(bundle);
                            transaction.replace(R.id.item_detail_container, fragmentPinzhengbudaDetail);
                            transaction.commit();
                        }
                    }
                }));

                buttonByElecWatch.setOnClickListener((new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //连接打印机先
                        boolean[] state = getConnectState();
                        if (state[mPrinterId] != true) {
                            CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
                            builder.setMessage(R.string.str_noopen);
                            builder.setTitle(R.string.str_note);
                            builder.setPositiveButton(R.string.str_goset,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(getActivity(), NetWorkSettingActivity.class);
                                            boolean[] state = getConnectState();
                                            intent.putExtra(CONNECT_STATUS, state);
                                            intent.putExtra("flag_1", 1);
                                            getActivity().startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton(R.string.str_cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if (!isAutoReadCardRuned) {
                                                GlobalParams.IC_JSON_REQ = "";
                                                GlobalParams.CARD_TYPE = "";// 若是手动输入表号，则卡类型清空
                                            }
                                            if (inputCond.getText().length() == 0) {
                                                SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
                                                return;
                                            }
                                            isRunAutoReadCard = false;
                                            if (mActivity == null) {
                                                mActivity = (ItemListActivity) getActivity();
                                            }
                                            FragmentManager fm = mActivity.getFragmentManager();
                                            FragmentTransaction transaction = fm.beginTransaction();
                                            ItemListActivity.isEnterTrigger = false;
                                            FragmentPinzhengbudaDetail fragmentPinzhengbudaDetail = new FragmentPinzhengbudaDetail();
                                            Bundle bundle = new Bundle();
                                            String queryType = "2";
                                            bundle.putString("inputCond",
                                                    StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
                                            bundle.putString("queryType", queryType);
                                            fragmentPinzhengbudaDetail.setArguments(bundle);
                                            transaction.replace(R.id.item_detail_container, fragmentPinzhengbudaDetail);
                                            transaction.commit();
                                        }
                                    });
                            builder.create().show();
                        } else {
                            if (!isAutoReadCardRuned) {
                                GlobalParams.IC_JSON_REQ = "";
                            }
                            if (inputCond.getText().length() == 0) {
                                SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
                                return;
                            }
                            isRunAutoReadCard = false;
                            if (mActivity == null) {
                                mActivity = (ItemListActivity) getActivity();
                            }
                            FragmentManager fm = mActivity.getFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            ItemListActivity.isEnterTrigger = false;
                            FragmentPinzhengbudaDetail fragmentPinzhengbudaDetail = new FragmentPinzhengbudaDetail();
                            Bundle bundle = new Bundle();
                            String queryType = "2";
                            bundle.putString("inputCond",
                                    StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
                            bundle.putString("queryType", queryType);
                            fragmentPinzhengbudaDetail.setArguments(bundle);
                            transaction.replace(R.id.item_detail_container, fragmentPinzhengbudaDetail);
                            transaction.commit();
                        }

                    }
                }));

            } else if ("3".equals(selectItemId)) {
                // ReaderMonitor.setContext(mActivity.getApplicationContext());
                // ReaderMonitor.startMonitor();
                // 冲正申请
                ItemListActivity.isEnterTrigger = true;
                isRunAutoReadCard = true;
                rootView = inflater.inflate(R.layout.fragment_chongzhengshenqing_main, container, false);

                if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                    img_btn = (ImageButton) rootView.findViewById(R.id.btn_scan);
                    img_btn.setOnClickListener(cameraListener);
//				img_btn.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View view) {
//
//						if (if_btn_click) {
//							return;
//						}
//						if_btn_click = true;
//						if (GlobalParams.LASERH) {
//							// 激光扫描二维码
//							new GetDataAndTypeTask().execute();
//						} else {
//							// Zxing扫描二维码
//							Intent openCameraIntent = new Intent(mActivity, CaptureActivity.class);
//							startActivityForResult(openCameraIntent, 0);
//						}
//					}
//				});
                }
                buttonChongzhengQuery = (Button) rootView.findViewById(R.id.buttonChongzhengQuery);

                inputCond = (AutoCompleteTextView) rootView.findViewById(R.id.inputCond);
                if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                    if (language.equalsIgnoreCase("en") || language.equalsIgnoreCase("fr")) {
                        inputCond.setTextSize(22);
                    }
                }
                hideSoftInputMethod(inputCond);

                if (Preferences.getComplexDataInPreference(mActivity, Preferences.KEY_MeterOrUser_No, "0") != null
                        && !Preferences.getComplexDataInPreference(mActivity, Preferences.KEY_MeterOrUser_No, "0")
                        .toString().equalsIgnoreCase("0")) {
                    numberList = (List<String>) Preferences.getComplexDataInPreference(mActivity,
                            Preferences.KEY_MeterOrUser_No, "0");
                    List<String> tempList = new ArrayList<String>();
                    for (String s : numberList) {
                        boolean isSame = false;
                        for (String str : tempList) {
                            if (s.equalsIgnoreCase(str)) {
                                isSame = true;
                            }
                        }
                        if (!isSame) {
                            tempList.add(s);
                        }
                    }
                    numberList = tempList;
                }
                inputCond.setThreshold(6);
                arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, numberList);
                inputCond.setAdapter(arrayAdapter);

                inputCond.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s == null || s.length() == 0) {
                            return;
                        }
                        if (s.toString().contains("#") || s.toString().contains(".") || s.toString().contains("*")) {
                            s = s.toString().replace("#", "");
                            s = s.toString().replace("*", "");
                            s = s.toString().replace(".", "");
                            inputCond.setText(s.toString());
                            inputCond.setSelection(s.length());
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < s.length(); i++) {
                            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                                continue;
                            } else {
                                sb.append(s.charAt(i));
                                if ((sb.length() == 5 || sb.length() == 10 || sb.length() == 15)
                                        && sb.charAt(sb.length() - 1) != ' ') {
                                    sb.insert(sb.length() - 1, ' ');
                                }
                            }
                        }
                        if (!sb.toString().equals(s.toString())) {
                            int index = start + 1;
                            if (sb.length() <= start) {
                                return;
                            }
                            if (sb.charAt(start) == ' ') {
                                if (before == 0) {
                                    index++;
                                } else {
                                    index--;
                                }
                            } else {
                                if (before == 1) {
                                    index--;
                                }
                            }
                            inputCond.setText(sb.toString());
                            inputCond.setSelection(index);
                        }
                        Editable editable = inputCond.getText();
                        int len = editable.length();

                        if (len > 19) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            // 截取新字符串
                            String newStr = str.substring(0, 19);
                            inputCond.setText(newStr);
                            editable = inputCond.getText();

                            // 新字符串的长度
                            int newLen = editable.length();
                            // 旧光标位置超过字符串长度
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            // 设置新光标所在的位置
                            Selection.setSelection(editable, selEndIndex);

                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {

                    }
                });
//                initNumberButton(rootView);
                HexSoftInput hexSoftInput = new HexSoftInput(mActivity,rootView);
                hexSoftInput.setInputCond(inputCond);

                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(new ShortCutsKeyDownCallBack() {

                    @Override
                    public void keyValue(int selectKey) {
                        if (selectKey == 28) {
                            // 按用户查询快捷键
                            buttonChongzhengQuery.performClick();
                        }
                    }
                });

                buttonChongzhengQuery.setOnClickListener((new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 防止双击导致崩溃
                        if (isfragmentRunning) {
                            return;
                        }
                        isfragmentRunning = true;
                        isRunAutoReadCard = false;
                        if (mActivity == null) {
                            mActivity = (ItemListActivity) getActivity();
                        }
                        FragmentManager fm = mActivity.getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        ItemListActivity.isEnterTrigger = false;
                        FragmentChongZhengShenQingDetail czsqDetailFragment = new FragmentChongZhengShenQingDetail();
                        Bundle bundle = new Bundle();
                        bundle.putString("inputCond",
                                StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
                        czsqDetailFragment.setArguments(bundle);
                        transaction.replace(R.id.item_detail_container, czsqDetailFragment);
                        transaction.commit();
                    }
                }));

            } else if ("4".equals(selectItemId)) {
                // 收费日结
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentShouFeiRiJieDetail shoufeirijie = new FragmentShouFeiRiJieDetail();
                transaction.replace(R.id.item_detail_container, shoufeirijie);
                transaction.commit();

            } else if ("5".equals(selectItemId)) {
                // 到帐确认
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentDaoZhangQueRenDetail daozhangqueren = new FragmentDaoZhangQueRenDetail();
                transaction.replace(R.id.item_detail_container, daozhangqueren);
                transaction.commit();

            } else if ("6".equals(selectItemId)) {
                // 收支明细
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);

                rootView = inflater.inflate(R.layout.list_zhuanzhangjilu, container, false);
                GlobalParams.APINAME = "PBillIncomeQuery";
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentShouZhiMingXi fragmentIncomeDetail = new FragmentShouZhiMingXi();
                transaction.replace(R.id.item_detail_container, fragmentIncomeDetail);
                transaction.commit();
            } else if ("7".equals(selectItemId)) {
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentXiTongSheZhiMain mFragmentXiTongSheZhiMain = new FragmentXiTongSheZhiMain();
                transaction.replace(R.id.item_detail_container, mFragmentXiTongSheZhiMain);
                transaction.commit();
            } else if ("8".equals(selectItemId)) {
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentYaJinChongZhiMain mFragmentYaJinChongZhiMain = new FragmentYaJinChongZhiMain();
                transaction.replace(R.id.item_detail_container, mFragmentYaJinChongZhiMain);
                transaction.commit();

            } else if ("9".equals(selectItemId)) {
                // 销售订单
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);

                rootView = inflater.inflate(R.layout.fragment_xiaoshoudingdan_main,
                        container, false);
                GlobalParams.APINAME = "POrderQuery";
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }

                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentXiaoShouJiLu fragmentIncomeDetail = new FragmentXiaoShouJiLu();
                transaction.replace(R.id.item_detail_container,
                        fragmentIncomeDetail);
                transaction.commit();
            } else if ("10".equals(selectItemId)) {
                // 到帐确认
                ItemListActivity activity = (ItemListActivity) mActivity;
                activity.setShortCutsKeyDownCallBack(null);
                if (mActivity == null) {
                    mActivity = (ItemListActivity) getActivity();
                }
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentZhuanZhangJiLu zhuanZhangJiLu = new FragmentZhuanZhangJiLu();
                transaction.replace(R.id.item_detail_container, zhuanZhangJiLu);
                transaction.commit();

            }
        }

		mActivity.setOnReadCardMessageCallBack(this);
    
		return rootView;
	}
	
	private OnClickListener cameraListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//连接打印机先
//	        boolean[] state = getConnectState();
//	        if(state[mPrinterId]!=true && "1".equals(mItem.id)){
//		        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
//		  	   	  builder.setMessage(R.string.str_noopen);
//		  	   	  builder.setTitle(R.string.str_note);
//		  	   	 builder.setPositiveButton(R.string.str_goset,
//		  	             new DialogInterface.OnClickListener() {
//		  	   	   @Override
//		  	   	   public void onClick(DialogInterface dialog, int which) {
//		  	   		   	dialog.dismiss();
//		  		        Intent intent = new Intent(getActivity(), NetWorkSettingActivity.class);
//		  		        boolean[] state = getConnectState();
//		  		        intent.putExtra(CONNECT_STATUS, state);
//		  		        intent.putExtra("flag_1", 1);
//		  		        getActivity().startActivity(intent);
//		  	   	   }
//		  	   	  });
//		  	   	 builder.setNegativeButton(R.string.str_cancel,
//		  	             new DialogInterface.OnClickListener() {
//		  	   	   @Override
//		  	   	   public void onClick(DialogInterface dialog, int which) {
//		  	   		   	dialog.dismiss();
//						if(!continueClick){
//							return;
//						}
//						if (if_btn_click) {
//							return;
//						}
//						if_btn_click = true;
//			//			ReaderMonitor.stopMonitor();
//						if (GlobalParams.LASERH) {
//							// 激光扫描二维码
//							new GetDataAndTypeTask().execute();
//						} else {
//							// Zxing扫描二维码
//							Intent openCameraIntent = new Intent(mActivity, CaptureActivity.class);
//							startActivityForResult(openCameraIntent, 0);
//			//				startActivity(openCameraIntent);
//							
//						}
//		  	   	   }
//		  	   	  });
//		  	   	  AlertDialog x = builder.create();
//		  	   	  x.show();
//	        }else{
				if(!continueClick){
					return;
				}
				if (if_btn_click) {
					return;
				}
				if_btn_click = true;
	//			ReaderMonitor.stopMonitor();
				if (GlobalParams.LASERH) {
					// 激光扫描二维码
//					new GetDataAndTypeTask().execute();
				} else {
					// Zxing扫描二维码
					Intent openCameraIntent = new Intent(mActivity, CaptureActivity.class);
					startActivityForResult(openCameraIntent, 0);
	//				startActivity(openCameraIntent);
					
				}
//	        }
		}
		
	};
	
	private OnClickListener icCardQuery=new OnClickListener() {
		@Override
		/*
		 * public void onClick(View view) { AlertDialog.Builder
		 * builder = new Builder(mActivity);
		 * builder.setTitle("确认" ) ; builder.setMessage("读卡") ;
		 * builder.setPositiveButton("是" , null );
		 * builder.show(); }
		 */
		public void onClick(View v) {
			//连接打印机先
//	        boolean[] state = getConnectState();
//	        if(state[mPrinterId]!=true && "1".equals(mItem.id)){
//		        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
//		  	   	  builder.setMessage(R.string.str_noopen);
//		  	   	  builder.setTitle(R.string.str_note);
//		  	   	 builder.setPositiveButton(R.string.str_goset,
//		  	             new DialogInterface.OnClickListener() {
//		  	   	   @Override
//		  	   	   public void onClick(DialogInterface dialog, int which) {
//		  	   		   	dialog.dismiss();
//		  		        Intent intent = new Intent(getActivity(), NetWorkSettingActivity.class);
//		  		        boolean[] state = getConnectState();
//		  		        intent.putExtra(CONNECT_STATUS, state);
//		  		        intent.putExtra("flag_1", 1);
//		  		        getActivity().startActivity(intent);
//		  	   	   }
//		  	   	  });
//		  	   	 builder.setNegativeButton(R.string.str_cancel,
//		  	             new DialogInterface.OnClickListener() {
//		  	   	   @Override
//		  	   	   public void onClick(DialogInterface dialog, int which) {
//		  	   		   	dialog.dismiss();
//		  	   		icCardQueryMain();
//		  	   	   }
//		  	   	  });
//		  	   	  AlertDialog x = builder.create();
//		  	   	  x.show();
//	        }else{
	  	   		icCardQueryMain();
//	        }
		}
	};
	private void icCardQueryMain(){

		// list
		if(!continueClick){
			return;
		}
		continueClick=false;
//		img_btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}});
		String deviceName = null;
//		logMsg1("devie1");
		for (UsbDevice device : mManager.getDeviceList().values()) {// ���usb
			if (mReader.isSupported(device)) {
				deviceName = device.getDeviceName();
				break;
			}
		}
		logMsg1("devie2"+deviceName);
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
								 continueClick = true; 
								 return;
					            }
							
						/////// DoAll////////
						// power
						// Get slot number
						int slotNum = 0;//
						// Get action number
						int actionNum = Reader.CARD_WARM_RESET;// Э��0
						// Set parameters
						PowerParams params = new PowerParams();
						params.slotNum = slotNum;
						params.action = actionNum;
						
						PowerTask(params);
						logMsg1("statepower"+getState());
//						SystemClock.sleep(100);
						if (getATR().contains("A2131091"))
		                {
		            		GlobalParams.CARD_TYPE = "2";//4442
		                }
		            	else if (getATR().contains("92231091"))
		                {
		                	 GlobalParams.CARD_TYPE = "1";//4428
		                }else{
		                	 logMsg1("IC Type Error!");
		                	 continueClick=true;
							 break;
		                }
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
							logMsg1("stateproto"+getState());
						}
//						SystemClock.sleep(100);

						// Transmit
						// If slot is selected
						if (slotNum != Spinner.INVALID_POSITION) {
							// Set parameters
							List<Integer> numList = new ArrayList<Integer>();
							if(GlobalParams.CARD_TYPE.equals("2")){
								numList = parsingJsonData(GlobalParams.READ4442);
							}else if(GlobalParams.CARD_TYPE.equals("1")){
								numList = parsingJsonData(GlobalParams.READ4428);
							}
							String startread="";
							String lenread="";
							  if (numList.size() == 2) {
								  startread=Integer.toHexString(numList.get(0));
								  lenread=Integer.toHexString(numList.get(1));
								  if(startread.length()==1){
									  startread="0"+startread;
								  }
								  if(lenread.length()==1){
									  lenread="0"+startread;
								  }
	                            }else{
	                            	continueClick=true;
	                            	break;
	                            }
							
							TransmitParams paramsTransmit = new TransmitParams();
							paramsTransmit.slotNum = slotNum;
							paramsTransmit.controlCode = -1;
							String changeLine = "\n";
//							String passWord = "b62307";
//							String card = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
//							String cardInfo = "ffa400000106" + changeLine + "ff20000003" + passWord
//									+ changeLine + "ffd00020E0" + card;
							String cardInfo="";
							logMsg1("startread/lenread"+startread+"/"+lenread);
							if(GlobalParams.CARD_TYPE.equals("2")){
								cardInfo = "ffa400000106" + changeLine + "ffb000"+startread+lenread;
								paramsTransmit.commandString = cardInfo;
								ReadCardTask(slotNum,cardInfo);
//								new TransmitTask().execute(paramsTransmit);// �ύ
							}else if(GlobalParams.CARD_TYPE.equals("1")){//4428
								int address=numList.get(0);
								int len=numList.get(1);
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
									logMsg1("地址长度:"+startread+";"+lenread);
									cardInfo = cardInfo + changeLine + "ffb0"+startread+lenread;
								}
								logMsg1("命令："+cardInfo);
								ReadCardTask(slotNum,cardInfo);
//								paramsTransmit.commandString = cardInfo;
//								new TransmitTask().execute(paramsTransmit);
							}
//							SystemClock.sleep(300);
//							new CloseTask().execute();
							 mReader.close();
						}
						/////// *****////////
					} else {
						// Request permission Ȩ��
						mManager.requestPermission(device, mPermissionIntent);
						continueClick=true;
						break;
					}
				}else{
					continueClick=true;
				}
			}
		}else{
			continueClick=true;
		}
	
	}
	
	//非异步任务读卡
		private void ReadCardTask (int slotNum,String cardInfo) {
			logMsg1("读卡非异步任务");
			int controlCode=-1;

				TransmitProgress progress = null;

				byte[] command = null;
				byte[] response = null;
				int responseLength = 0;
				int foundIndex = 0;
				int startIndex = 0;
				String resultStr="";
				do {

					// Find carriage return
					foundIndex = cardInfo.indexOf('\n', startIndex);
					if (foundIndex >= 0) {
						command = toByteArray(cardInfo.substring(startIndex, foundIndex));
					} else {
						command = toByteArray(cardInfo.substring(startIndex));
					}

					// Set next start index
					startIndex = foundIndex + 1;

					response = new byte[300];
					progress = new TransmitProgress();
					progress.controlCode = controlCode;
					try {

						if (controlCode < 0) {

							// Transmit APDU
							responseLength = mReader.transmit(slotNum, command, command.length, response,
									response.length);

						} else {

							// Transmit control command
							responseLength = mReader.control(slotNum, controlCode, command,
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

					resultStr+=getStrNew(progress.response, progress.responseLength);
					 if(resultStr.length()>=4){
//		            	  if(resultStr.substring(resultStr.length()-4, resultStr.length()).equals("9000")){
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


				String result=resultStr;
	            if(result.equals("")){
	            	continueClick=true;
	            	logMsg1("dukashibai");
	            	return;
	            }
	            SystemClock.sleep(100);
	            if(GlobalParams.CARD_TYPE.equals("1")){
	            		logMsg1("读卡成功！");
		            	continueClick=true;
		            	mOffset=GlobalParams.READ4428;
		 	            List<String> dataList = new ArrayList<String>();
		 	            dataList.add(result);
		 	            readCardMessage(mOffset,dataList);
	            }
	            if(GlobalParams.CARD_TYPE.equals("2")){
	            		continueClick=true;
		            	logMsg1("读卡成功！");
		            	List<String> dataList = new ArrayList<String>();
		            	dataList.add(result);
		            	mOffset=GlobalParams.READ4442;//"{\"read\":{\"offset\":[32],\"value\":[224]}}";
		            	readCardMessage(mOffset,dataList);
	            }
		}
	
private String getHex(int a,boolean b){
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
	@Override
	public void readCardMessage(String offset, List<String> list) {
		// 检查到插入卡后，停止扫描二维码
		Log.d(TAG, "ReadCardMessageCallBack");
		logMsg1("jiexi:"+list.get(0));
		if (isRunAutoReadCard) {
			mAutoReadCardTask = new AutoReadCardTask(offset, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	// 自动读卡异步任务
	private class AutoReadCardTask extends AsyncTask<Void, Void, Integer> {
		List<String> valueList = null;
		String mOffset = "";
		String IC_JSON_REQ;

		public AutoReadCardTask(String offset, List<String> list) {
			valueList = list;
			mOffset = offset;
		}

		public AutoReadCardTask(String content) {
			IC_JSON_REQ = content;
		}

		@Override
		protected void onPreExecute() {
			isRunAutoReadCard = false;
			sendMsgInner(2001);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Log.e(TAG, "GlobalParams.CARD_TYPE = " + GlobalParams.CARD_TYPE);
			if (valueList == null || valueList.size() == 0) { 
				return 0;
			}
			Request_Zidongduka.setContext(mActivity);
			Request_Zidongduka.setIcType(GlobalParams.CARD_TYPE);

			if (GlobalParams.CARD_TYPE.equalsIgnoreCase("3")) {
				IC_JSON_REQ = valueList.get(0);
			} else if (GlobalParams.CARD_TYPE.equalsIgnoreCase("1") || GlobalParams.CARD_TYPE.equalsIgnoreCase("2")) {
				String ic_json_req = "";
				for (int i = 0; i < valueList.size(); i++) {
					if (i == valueList.size() - 1) {// 最后一段
						ic_json_req += "\"" + valueList.get(i) + "\"";
					} else {
						ic_json_req += "\"" + valueList.get(i) + "\"" + ",";
					}
				}
				IC_JSON_REQ = "{   \"read\" : {\"offset\" : " + mOffset + ",   \"value\" : [" + ic_json_req + "] }}";
				Log.e(TAG, "IC_JSON_REQ = " + IC_JSON_REQ);
				GlobalParams.IC_JSON_REQ = IC_JSON_REQ;
			} else {
				IC_JSON_REQ = valueList.get(0);
			}

			Request_Zidongduka.setIcJsonReq(IC_JSON_REQ);

			String requestXML = Request_Zidongduka.getRequsetXML();
			Log.e(TAG, "requestXML = " + requestXML);
			String reponseXML = "";
			try {
				reponseXML = Client.ConnectServer("ReadCard", requestXML);
				Log.e(TAG, "reponseXML = " + reponseXML);
				String rspCode = Client.Parse_XML(reponseXML, "<RSPCOD>", "</RSPCOD>");
				mRspMeg = Client.Parse_XML(reponseXML, "<RSPMSG>", "</RSPMSG>");
				if (!rspCode.equals("00000")) {// 请求失败
					return 0;
				} else {
					mMeterNo = Client.Parse_XML(reponseXML, "<METER_NO>", "</METER_NO>");// 表号
					Log.e(TAG, "mMeterNo = " + mMeterNo);
					mUserNo = Client.Parse_XML(reponseXML, "<USER_NO>", "</USER_NO>");// 户号
					Log.e(TAG, "mUserNo = " + mUserNo);
					mIcFlag = Client.Parse_XML(reponseXML, "<IC_FLAG>", "</IC_FLAG>");// IC是否写卡
																						// 0:不写卡
																						// 1：写卡
					Log.e(TAG, "mIcFlag = " + mIcFlag);
					GlobalParams.IC_FLAG = mIcFlag;
					return 1;
				}
			} catch (Exception ex) {
				Log.e(TAG, "Exception = " + ex.toString());
				return 0;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			isRunAutoReadCard = true;
			isAutoReadCardRuned = true;
			sendMsgInner(2002);
			if (result == 1) {
				if (mMeterNo.length() != 0) {
					// TODO TEST
					// mMeterNo = "014237831814";
					GlobalParams.USERNO_OR_METERNO = mMeterNo;
				} else if (mUserNo.length() != 0) {
					// TODO TEST
					// mUserNo = "014237831814";
					GlobalParams.USERNO_OR_METERNO = mUserNo;
				}
				if (inputCond != null) {
					String number = GlobalParams.USERNO_OR_METERNO;
					String regex = "(.{4})";
					number = number.replaceAll(regex, "$1 ");
					inputCond.setText(number);
					inputCond.setSelection(inputCond.getText().length());
				}
				SystemUtil.displayToast(mActivity, R.string.auto_readCard_success);
				if (mMeterNo.length() != 0) {

					if (id.equals("3")) {
						if (buttonChongzhengQuery != null) {
							buttonChongzhengQuery.performClick();
						}
					} else {
						if (buttonByElecWatch != null) {
							buttonByElecWatch.performClick();
						}
					}
				} else if (mUserNo.length() != 0) {

					if (id.equals("3")) {
						if (buttonChongzhengQuery != null) {
							buttonChongzhengQuery.performClick();
						}
					} else {
						if (buttonByUser != null) {
							buttonByUser.performClick();
						}
					}
				}
			} else {
				if (mRspMeg.equalsIgnoreCase("")) {
					SystemUtil.displayToast(mActivity, getString(R.string.auto_readCard_fail));
				} else {
					SystemUtil.displayToast(mActivity, mRspMeg);
				}

				Message msg = handler.obtainMessage(0x01);
				handler.sendMessage(msg);
			}
			continueClick=true;
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		// if (GlobalParams.If_CloseFlashLight) {
		// ((ItemListActivity) mActivity).continuePreview(handler);
		// GlobalParams.If_CloseFlashLight = false;
		// }
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");

		// ReaderMonitor.stopMonitor();
		mActivity = (ItemListActivity) mActivity;
		mActivity.setOnReadCardMessageCallBack(null);
		mActivity.setShortCutsKeyDownCallBack(null);
		if (mAutoReadCardTask != null && mAutoReadCardTask.isCancelled()) {
			mAutoReadCardTask.cancel(true);
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onDestroy();
	}

	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText ed) {
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
				setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(ed, false);
			} catch (NoSuchMethodException e) {
				ed.setInputType(InputType.TYPE_NULL);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	protected void sendMsgInner(int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2001:
				progressDialog = CustomProgressDialog.createProgressDialog(mActivity, GlobalParams.PROGRESSDIALOG_TIMEOUT,
						new CustomProgressDialog.OnTimeOutListener() {

							@Override
							public void onTimeOut(CustomProgressDialog dialog) {
								SystemUtil.displayToast(mActivity, R.string.progress_timeout);
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
									dialog = null;
								}

							}

						});
				progressDialog.setTitle(mActivity.getString(R.string.progress_zidongduka_title));
				progressDialog.setMessage(mActivity.getString(R.string.progress_conducting));
				// 设置进度条是否不明确
//				progressDialog.setIndeterminate(false);

				// 是否可以按下退回键取消
				progressDialog.setCancelable(false);
				progressDialog.show();
				break;
			case 2002:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == -1) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			Log.e(TAG, scanResult);

			if (scanResult.length() > 0) {

//				if (GlobalParams.BARCODE_FORMAT.equalsIgnoreCase("QR_CODE")) {
//					GlobalParams.CARD_TYPE = "4";// 二维码
//				} else {
					GlobalParams.CARD_TYPE = "5";// 条形码
//				}
				GlobalParams.QR_Info = scanResult;
				List<String> list = new ArrayList<String>();
				list.add(GlobalParams.QR_Info);
				readCardMessage("", list);
			}
		}
		if_btn_click = false;
	}
    public static String getKeyByValue(Map map, String value) {  
        String keys="";  
        Iterator it = map.entrySet().iterator();  
        while (it.hasNext()) {  
            Entry entry = (Entry) it.next();
            String obj = (String) entry.getValue();  
            if (obj != null && obj.equals(value)) {  
                keys=(String) entry.getKey();  
            }  
  
  
        }  
        return keys;  
        }  

	
	private class GetDataAndTypeTask extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		String[] data = new String[2];
		String msg = "";

		@Override
		protected String doInBackground(Void... params) {
			try {
				Decode.open();
				data = Decode.readDataAndType(10000);
				if (data[1].equals("1")) {
					GlobalParams.CARD_TYPE = "5";// 条形码
				} else if (data[1].equals("2")) {
					GlobalParams.CARD_TYPE = "4";// 二维码
				}
				msg = data[0]; // 扫描到的数据
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Decode.disconnect();
			}

			return msg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null) {
				dialog = new ProgressDialog(mActivity);
			}
			dialog.setMessage(getString(R.string.update_progressdialog_message));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
//			ReaderMonitor.startMonitor();
			if (result.length() > 0) {
				// TODO 获取扫描的图片类型
				GlobalParams.QR_Info = result;
				Log.e(TAG, result);
				List<String> list = new ArrayList<String>();
				list.add(GlobalParams.QR_Info);
				readCardMessage("", list);
			}
			if_btn_click = false;

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

	
	private class TransmitTask extends AsyncTask<TransmitParams, TransmitProgress, String> {

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
            	continueClick=true;
            	logMsg1("dukashibai");
            	return;
            }
            if(GlobalParams.CARD_TYPE.equals("1")){
            		logMsg1("读卡成功！");
	            	continueClick=true;
	            	mOffset=GlobalParams.READ4428;
	 	            List<String> dataList = new ArrayList<String>();
	 	            dataList.add(result);
	 	            readCardMessage(mOffset,dataList);
            }
            if(GlobalParams.CARD_TYPE.equals("2")){
            		continueClick=true;
	            	logMsg1("读卡成功！");
	            	List<String> dataList = new ArrayList<String>();
	            	dataList.add(result);
	            	mOffset=GlobalParams.READ4442;//"{\"read\":{\"offset\":[32],\"value\":[224]}}";
	            	readCardMessage(mOffset,dataList);
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
//                logMsg1("Slot " + slotNum + ": Getting ATR...");
                byte[] atr = mReader.getAtr(slotNum);

                // Show ATR
                if (atr != null) {

//                    logMsg1("ATR:");
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
			String ss = bufferString.substring(0, bufferString.length()-4);
			return ss;
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

	                logMsg1(result.toString());

	            } else {

	                logMsg1("Reader name: " + mReader.getReaderName());

	                int numSlots = mReader.getNumSlots();
	                logMsg1("Number of slots: " + numSlots);

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
	    public boolean[] getConnectState() {
	        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
	        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
	            state[i] = false;
	        }
	        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
	            try {
	                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
	                    state[i] = true;
	                }
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        return state;
	    }
	    public void loadDataForSpinner(){
			
			spinnerList = new ArrayList<String>();
			spinnerListid = new ArrayList<String>();
//			spinnerList.add(mActivity.getString(R.string.shoufei_spinner_sel));
			
			if(el_select==1){
				msailelec.setBackgroundResource(R.drawable.electryactive);
				map=PowertechApplication.getENELGROUP1();
			}else if(el_select==2){
				msailwater.setBackgroundResource(R.drawable.wateractive);
				map=PowertechApplication.getENELGROUP2();
			}else if(el_select==3){
				msailair.setBackgroundResource(R.drawable.gasactive);
				map=PowertechApplication.getENELGROUP3();
			}else if(el_select==4){
				msailcomm.setBackgroundResource(R.drawable.phoneactive);
				map=PowertechApplication.getENELGROUP4();
			}
			if(map != null) {  
				
			Set<String> keys = map.keySet();  
			
			Iterator<String> iter = keys.iterator(); 
			
			//Iterator iter = map.keySet().iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					String val =  map.get(key);
					spinnerList.add(val);
					spinnerListid.add(key);
				}
			}
				ArrayAdapter<String> myAdapter =  new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, spinnerList);
				
				myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				spinner.setAdapter(myAdapter);
				
				
				if(el_select==1){
					for(int i = 0 ; i < spinnerListid.size() ; i++) {
						 if(spinnerListid.get(i).equals(app.getSELECTENEL1())){
							 spinner.setSelection(i);
						 }
					}	
				}
				if(el_select==2){
					for(int i = 0 ; i < spinnerListid.size() ; i++) {
						 if(spinnerListid.get(i).equals(app.getSELECTENEL2())){
							 spinner.setSelection(i);
						 }
					}	
				}
				if(el_select==3){
					for(int i = 0 ; i < spinnerListid.size() ; i++) {
						 if(spinnerListid.get(i).equals(app.getSELECTENEL3())){
							 spinner.setSelection(i);
						 }
					}	
				}
				if(el_select==4){
					for(int i = 0 ; i < spinnerListid.size() ; i++) {
						 if(spinnerListid.get(i).equals(app.getSELECTENEL4())){
							 spinner.setSelection(i);
						 }
					}	
				}
				
	    } 
}
