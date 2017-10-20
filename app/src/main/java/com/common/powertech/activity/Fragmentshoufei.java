package com.common.powertech.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.acs.smartcard.Features;
import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.ItemListActivity.ReadCardMessageCallBack;
import com.common.powertech.ItemListActivity.ShortCutsKeyDownCallBack;
import com.common.powertech.bussiness.Request_Zidongduka;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.ReaderMonitor;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myDialog.CustomDialog;
import com.telpo.tps550.api.decode.Decode;
import com.zbar.lib.CaptureActivity;

/**
 * Created by yeqw on 2015/11/11.
 */
public class Fragmentshoufei extends Fragment implements ReadCardMessageCallBack {
	
	private static final String TAG = "Fragmentshoufei";
	 private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	 
	private ImageButton img_btn;
	private ImageButton img_read;
	private boolean if_btn_click = false;       //防按钮双击
	private ItemListActivity mActivity;
	private Button buttonByUser, buttonByElecWatch, buttonBytypeWatch, buttonChongzhengQuery;
	private DummyContent.DummyItem mItem;
    private AutoCompleteTextView inputCond;
    private Button button1, button2, button3, button4, button5, button6,
            button7, button8, button9, button0, switchtoABC;
    private Button buttonq ,buttonw ,buttone ,buttonr ,buttont ,buttony ,buttonu ,buttoni ,buttono ,buttonp ,
            buttona ,buttons ,buttond ,buttonf ,buttong ,buttonh ,buttonj ,buttonk ,buttonl ,buttonz ,buttonx ,
            buttonc ,buttonv ,buttonb ,buttonn ,buttonm ,switchto123  ;
    private TableLayout input_model123,input_modelABC;
    private LinearLayout buttonBack123,buttonBackABC;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> numberList = new ArrayList<String>();
    private String language="";
    private boolean isRunAutoReadCard = false;
    private AsyncTask<Void, Void, Integer> mAutoReadCardTask;
    private String mRspMeg = "";
    private boolean isAutoReadCardRuned = false;// 自动读卡是否执行过而进入购电或收费
    String mMeterNo;
    String mUserNo;
    String mIcFlag;
    private static ProgressDialog progressDialog = null;
    private String ResourceType ="";
	private String EnelName = "";
	private String EnelId = "";
	private GpService mGpService = null;
	private boolean continueClick = true; // 防按钮连续点击
	private boolean ISDEBUG=false;
	private String mOffset = "";
	private String mValue="";
	private Features mFeatures = new Features();
	private              int                       mPrinterId           = 0;
	public static final String CONNECT_STATUS = "connect.status";
	private UsbManager mManager;
	private Reader mReader;
	private PendingIntent mPermissionIntent;
	private PrinterServiceConnection conn = null;
	
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
		
		
	 @Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			connection();
		}

		  private void connection() {
		        conn = new PrinterServiceConnection();
		        Intent intent = new Intent(getActivity().getApplicationContext(), GpPrintService.class);
		        getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
		    }
		
	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        
		 Log.e(TAG, "onCreateView");
	

		 super.onCreate(savedInstanceState);
	        mActivity = (ItemListActivity) getActivity();
	        mActivity.getWindow().setSoftInputMode(
	                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	   	 View rootView = null;
	   	 
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
		 
	       ResourceType = String.valueOf(getArguments().getInt("ResourceType"));
	       EnelName = getArguments().getString("enel_name");
	    	EnelId = getArguments().getString("enel_id");
	    	
	    	
	    	
	       rootView = inflater.inflate(R.layout.fragment_shoufei_main, container, false);
	        
		          if(GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")){
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
					initNumberButton(rootView);

					mActivity.setShortCutsKeyDownCallBack(new ShortCutsKeyDownCallBack() {

						@Override
						public void keyValue(int selectKey) {
							if (selectKey == 28) {
								// 按表号查询快捷键
								buttonByElecWatch.performClick();
							}
							if (selectKey == 248) {
								// 按用户查询快捷键
								buttonByUser.performClick();
							}
							if (selectKey == 18) {
								// 双击"#"跳转收费模块
								mActivity.setDefaultFragment();
								/*
								 * ItemListFragment mif=(ItemListFragment) mActivity
								 * .getFragmentManager().findFragmentById(
								 * R.id.item_list);
								 * mif.setActivateOnItemClick(true);
								 * mif.setItemSelected(0);
								 */
							}
						}
					});
    		
					buttonByUser.setOnClickListener((new OnClickListener() {
						@Override
						public void onClick(View v) {
							//连接打印机先
					        boolean[] state = getConnectState();
					        if(state[mPrinterId]!=true){
					        	CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
//						        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
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
										ItemListActivity.isEnterTrigger = false;
										if (mActivity == null) {
											mActivity = (ItemListActivity) getActivity();
										}
										FragmentManager fm = mActivity.getFragmentManager();
										FragmentTransaction transaction = fm.beginTransaction();
										FragmentShoufeiDetail fragmentShoufeiDetail = new FragmentShoufeiDetail();
				
										Bundle bundle = new Bundle();
										String queryType = "1";
										bundle.putString("inputCond",
												StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
										bundle.putString("queryType", queryType);
	    		                        bundle.putString("ResourceType", ResourceType);
	    		                        bundle.putString("EnelName", EnelName);
	    		                        bundle.putString("EnelId", EnelId);
										fragmentShoufeiDetail.setArguments(bundle);
										transaction.replace(R.id.item_detail_container, fragmentShoufeiDetail);
										transaction.addToBackStack(null);
										inputCond.setText("");
										transaction.commit();
						  	   	   }
						  	   	  });
//						  	   	  AlertDialog x = builder.create();
//						  	   	  x.show();
						  	   
						  	   builder.create().show();
					        }else{
								if (inputCond.getText().length() == 0) {
									SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
									return;
								}
		
								isRunAutoReadCard = false;
								ItemListActivity.isEnterTrigger = false;
								if (mActivity == null) {
									mActivity = (ItemListActivity) getActivity();
								}
								FragmentManager fm = mActivity.getFragmentManager();
								FragmentTransaction transaction = fm.beginTransaction();
								FragmentShoufeiDetail fragmentShoufeiDetail = new FragmentShoufeiDetail();
		
								Bundle bundle = new Bundle();
								String queryType = "1";
								bundle.putString("inputCond",
										StringUtil.convertStringNull(inputCond.getText().toString().replaceAll(" ", "")));
								bundle.putString("queryType", queryType);
								bundle.putString("ResourceType", ResourceType);
  		                        bundle.putString("EnelName", EnelName);
  		                        bundle.putString("EnelId", EnelId);
								fragmentShoufeiDetail.setArguments(bundle);
								transaction.replace(R.id.item_detail_container, fragmentShoufeiDetail);
								transaction.addToBackStack(null);
								inputCond.setText("");
								transaction.commit();
					        }
					        
						}
						
					}));
    		
					buttonByElecWatch.setOnClickListener((new OnClickListener() {
						@Override
						public void onClick(View v) {
							//连接打印机先
					        boolean[] state = getConnectState();
					        if(state[mPrinterId]!=true){
//						        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
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
										GlobalParams.IC_JSON_REQ = "";// 若是手动输入表号，则卡串信息清空
										GlobalParams.CARD_TYPE= "";// 若是手动输入表号，则卡类型清空
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
									FragmentShoufeiDetail fragmentShoufeiDetail = new FragmentShoufeiDetail();
									Bundle bundle = new Bundle();
									String queryType = "2";
									bundle.putString("inputCond",
											StringUtil.convertStringNull(inputCond.getText().toString()).replaceAll(" ", ""));
									bundle.putString("queryType", queryType);
									bundle.putString("CARD_TYPE", GlobalParams.CARD_TYPE);
									bundle.putString("IC_JSON_REQ", GlobalParams.IC_JSON_REQ);
								    bundle.putString("ResourceType", ResourceType);
    		                        bundle.putString("EnelName", EnelName);
    		                        bundle.putString("EnelId", EnelId);
									fragmentShoufeiDetail.setArguments(bundle);
									transaction.replace(R.id.item_detail_container, fragmentShoufeiDetail);
									transaction.addToBackStack(null);
									inputCond.setText("");
									transaction.commit();
						  	   	   }
						  	   	  });
//						  	   	  AlertDialog x = builder.create();
//						  	   	  x.show();
						  	   	 builder.create().show();
					        }else{
					        	if (!isAutoReadCardRuned) {
									GlobalParams.IC_JSON_REQ = "";// 若是手动输入表号，则卡串信息清空
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
								FragmentShoufeiDetail fragmentShoufeiDetail = new FragmentShoufeiDetail();
								Bundle bundle = new Bundle();
								String queryType = "2";
								bundle.putString("inputCond",
										StringUtil.convertStringNull(inputCond.getText().toString()).replaceAll(" ", ""));
								bundle.putString("queryType", queryType);
								bundle.putString("ResourceType", ResourceType);
  		                        bundle.putString("EnelName", EnelName);
  		                        bundle.putString("EnelId", EnelId);
								fragmentShoufeiDetail.setArguments(bundle);
								transaction.replace(R.id.item_detail_container, fragmentShoufeiDetail);
								transaction.addToBackStack(null);
								inputCond.setText("");
								transaction.commit();
					        }						        
						}
					}));  
    		                return rootView;
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

		private OnClickListener cameraListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//连接打印机先
					if(!continueClick){
						return;
					}
					if (if_btn_click) {
						return;
					}
					if_btn_click = true;
					if (GlobalParams.LASERH) {
					} else {
						Intent openCameraIntent = new Intent(mActivity, CaptureActivity.class);
						startActivityForResult(openCameraIntent, 0);
					}
			}
		};
	 
		private OnClickListener icCardQuery=new OnClickListener() {
			@Override

			public void onClick(View v) {

		  	   		icCardQueryMain();
			}
		}; 
		private void icCardQueryMain(){

			// list
			if(!continueClick){
				return;
			}
			continueClick=false;
//			img_btn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//				}});
			String deviceName = null;
//			logMsg1("devie1");
			for (UsbDevice device : mManager.getDeviceList().values()) {
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

						if (mManager.hasPermission(device)) { 
							
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
								
							int slotNum = 0;//
							int actionNum = Reader.CARD_WARM_RESET;
							// Set parameters
							PowerParams params = new PowerParams();
							params.slotNum = slotNum;
							params.action = actionNum;
							
							PowerTask(params);
							logMsg1("statepower"+getState());
//							SystemClock.sleep(100);
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

								SetProtocolParams paramsProtoco = new SetProtocolParams();
								paramsProtoco.slotNum = slotNum;
								paramsProtoco.preferredProtocols = preferredProtocols;
								
								SetProtocolTask(paramsProtoco);
								logMsg1("stateproto"+getState());
							}
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
								String cardInfo="";
								logMsg1("startread/lenread"+startread+"/"+lenread);
								if(GlobalParams.CARD_TYPE.equals("2")){
									cardInfo = "ffa400000106" + changeLine + "ffb000"+startread+lenread;
									paramsTransmit.commandString = cardInfo;
									ReadCardTask(slotNum,cardInfo);
//									new TransmitTask().execute(paramsTransmit);// �ύ
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
								}
								 mReader.close();
							}
						} else {
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
//				ReaderMonitor.startMonitor();
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
	 
		//判断IC卡类型
	    public String getATR() {

	        // Get slot number
	        int slotNum = 0;

	        // If slot is selected
	        if (slotNum != Spinner.INVALID_POSITION) {

	            try {

	                // Get ATR
//	                logMsg1("Slot " + slotNum + ": Getting ATR...");
	                byte[] atr = mReader.getAtr(slotNum);

	                // Show ATR
	                if (atr != null) {

//	                    logMsg1("ATR:");
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
	            isRunAutoReadCard = true;
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
		
		  
	 protected void sendMsgInner(int what) {
	        Message msg = new Message();
	        msg.what = what;
	        handler.sendMessage(msg);
	    }
	    
	    Handler handler = new Handler() {

	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case 2001:
	                    progressDialog = MyProgressDialog.createProgressDialog(
	                            mActivity, GlobalParams.PROGRESSDIALOG_TIMEOUT,
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

	                            }
	                    );
	                    progressDialog.setTitle(mActivity
	                            .getString(R.string.progress_zidongduka_title));
	                    progressDialog.setMessage(mActivity
	                            .getString(R.string.progress_conducting));
	                    // 设置进度条是否不明确
	                    progressDialog.setIndeterminate(false);

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

	    
//隐藏系统键盘
public void hideSoftInputMethod(EditText ed) {
    mActivity.getWindow().setSoftInputMode(
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
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

public void readCardMessage(String offset, List<String> list) {
    // 检查到插入卡后，停止扫描二维码
    Log.d(TAG, "ReadCardMessageCallBack");
    if (isRunAutoReadCard) {
        mAutoReadCardTask = new AutoReadCardTask(offset, list)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        } else if (GlobalParams.CARD_TYPE.equalsIgnoreCase("1")
                || GlobalParams.CARD_TYPE.equalsIgnoreCase("2")) {
            String ic_json_req = "";
            for (int i = 0; i < valueList.size(); i++) {
                if (i == valueList.size() - 1) {// 最后一段
                    ic_json_req += "\"" + valueList.get(i) + "\"";
                } else {
                    ic_json_req += "\"" + valueList.get(i) + "\"" + ",";
                }
            }
            IC_JSON_REQ = "{   \"read\" : {\"offset\" : " + mOffset
                    + ",   \"value\" : [" + ic_json_req + "] }}";
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
            String rspCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
                    "</RSPCOD>");
            mRspMeg = Client.Parse_XML(reponseXML, "<RSPMSG>", "</RSPMSG>");
            if (!rspCode.equals("00000")) {// 请求失败
                return 0;
            } else {
                mMeterNo = Client.Parse_XML(reponseXML, "<METER_NO>",
                        "</METER_NO>");// 表号
                Log.e(TAG, "mMeterNo = " + mMeterNo);
                mUserNo = Client.Parse_XML(reponseXML, "<USER_NO>",
                        "</USER_NO>");// 户号
                Log.e(TAG, "mUserNo = " + mUserNo);
                mIcFlag = Client.Parse_XML(reponseXML, "<IC_FLAG>",
                        "</IC_FLAG>");// IC是否写卡 0:不写卡 1：写卡
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
				if (buttonByElecWatch != null) {
					buttonByElecWatch.performClick();
				}
			} else if (mUserNo.length() != 0) {

				if (buttonByUser != null) {
					buttonByUser.performClick();
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
		super.onPostExecute(result);
	}
}


private void initNumberButton(View view) {

    button1 = (Button) view.findViewById(R.id.button1);
    button2 = (Button) view.findViewById(R.id.button2);
    button3 = (Button) view.findViewById(R.id.button3);
    button4 = (Button) view.findViewById(R.id.button4);
    button5 = (Button) view.findViewById(R.id.button5);
    button6 = (Button) view.findViewById(R.id.button6);
    button7 = (Button) view.findViewById(R.id.button7);
    button8 = (Button) view.findViewById(R.id.button8);
    button9 = (Button) view.findViewById(R.id.button9);
    button0 = (Button) view.findViewById(R.id.button0);
    switchtoABC = (Button) view.findViewById(R.id.switchtoABC);
    buttonBack123 = (LinearLayout) view.findViewById(R.id.buttonBack123);
    button1.setOnClickListener(numberOnclickListener);
    button2.setOnClickListener(numberOnclickListener);
    button3.setOnClickListener(numberOnclickListener);
    button4.setOnClickListener(numberOnclickListener);
    button5.setOnClickListener(numberOnclickListener);
    button6.setOnClickListener(numberOnclickListener);
    button7.setOnClickListener(numberOnclickListener);
    button8.setOnClickListener(numberOnclickListener);
    button9.setOnClickListener(numberOnclickListener);
    button0.setOnClickListener(numberOnclickListener);
    switchtoABC.setOnClickListener(numberOnclickListener);
    buttonBack123.setOnClickListener(numberOnclickListener);

    input_model123 = (TableLayout) view.findViewById(R.id.input_model123);
    input_modelABC = (TableLayout) view.findViewById(R.id.input_modelABC);

    buttonq = (Button) view.findViewById(R.id.buttonq );
    buttonw = (Button) view.findViewById(R.id.buttonw );
    buttone = (Button) view.findViewById(R.id.buttone );
    buttonr = (Button) view.findViewById(R.id.buttonr );
    buttont = (Button) view.findViewById(R.id.buttont );
    buttony = (Button) view.findViewById(R.id.buttony );
    buttonu = (Button) view.findViewById(R.id.buttonu );
    buttoni = (Button) view.findViewById(R.id.buttoni );
    buttono = (Button) view.findViewById(R.id.buttono );
    buttonp = (Button) view.findViewById(R.id.buttonp );
    buttona = (Button) view.findViewById(R.id.buttona );
    buttons = (Button) view.findViewById(R.id.buttons );
    buttond = (Button) view.findViewById(R.id.buttond );
    buttonf = (Button) view.findViewById(R.id.buttonf );
    buttong = (Button) view.findViewById(R.id.buttong );
    buttonh = (Button) view.findViewById(R.id.buttonh );
    buttonj = (Button) view.findViewById(R.id.buttonj );
    buttonk = (Button) view.findViewById(R.id.buttonk );
    buttonl = (Button) view.findViewById(R.id.buttonl );
    buttonz = (Button) view.findViewById(R.id.buttonz );
    buttonx = (Button) view.findViewById(R.id.buttonx );
    buttonc = (Button) view.findViewById(R.id.buttonc );
    buttonv = (Button) view.findViewById(R.id.buttonv );
    buttonb = (Button) view.findViewById(R.id.buttonb );
    buttonn = (Button) view.findViewById(R.id.buttonn );
    buttonm = (Button) view.findViewById(R.id.buttonm );
    switchto123 = (Button) view.findViewById(R.id.switchto123 );
    buttonBackABC = (LinearLayout)view.findViewById(R.id.buttonBackABC );

    buttonq.setOnClickListener(letterOnclickListener);
    buttonw.setOnClickListener(letterOnclickListener);
    buttone.setOnClickListener(letterOnclickListener);
    buttonr.setOnClickListener(letterOnclickListener);
    buttont.setOnClickListener(letterOnclickListener);
    buttony.setOnClickListener(letterOnclickListener);
    buttonu.setOnClickListener(letterOnclickListener);
    buttoni.setOnClickListener(letterOnclickListener);
    buttono.setOnClickListener(letterOnclickListener);
    buttonp.setOnClickListener(letterOnclickListener);
    buttona.setOnClickListener(letterOnclickListener);
    buttons.setOnClickListener(letterOnclickListener);
    buttond.setOnClickListener(letterOnclickListener);
    buttonf.setOnClickListener(letterOnclickListener);
    buttong.setOnClickListener(letterOnclickListener);
    buttonh.setOnClickListener(letterOnclickListener);
    buttonj.setOnClickListener(letterOnclickListener);
    buttonk.setOnClickListener(letterOnclickListener);
    buttonl.setOnClickListener(letterOnclickListener);
    buttonz.setOnClickListener(letterOnclickListener);
    buttonx.setOnClickListener(letterOnclickListener);
    buttonc.setOnClickListener(letterOnclickListener);
    buttonv.setOnClickListener(letterOnclickListener);
    buttonb.setOnClickListener(letterOnclickListener);
    buttonn.setOnClickListener(letterOnclickListener);
    buttonm.setOnClickListener(letterOnclickListener);
    switchto123.setOnClickListener(letterOnclickListener);
    buttonBackABC.setOnClickListener(letterOnclickListener);

}


/**
 * 按键监听器
 */
OnClickListener numberOnclickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
        String temp = StringUtil.convertStringNull(inputCond.getText()
                .toString());
        String tempPre = temp.substring(0, inputCond.getSelectionEnd());
        String tempLes = temp.substring(inputCond.getSelectionEnd(),
                temp.length());
        switch (v.getId()) {
            case R.id.button1:
                if (tempPre.length() < temp.length()) {
                    tempPre += "1";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "1";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button2:
                if (tempPre.length() < temp.length()) {
                    tempPre += "2";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "2";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button3:
                if (tempPre.length() < temp.length()) {
                    tempPre += "3";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "3";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button4:
                if (tempPre.length() < temp.length()) {
                    tempPre += "4";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "4";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button5:
                if (tempPre.length() < temp.length()) {
                    tempPre += "5";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "5";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button6:
                if (tempPre.length() < temp.length()) {
                    tempPre += "6";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "6";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button7:
                if (tempPre.length() < temp.length()) {
                    tempPre += "7";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "7";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button8:
                if (tempPre.length() < temp.length()) {
                    tempPre += "8";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "8";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button9:
                if (tempPre.length() < temp.length()) {
                    tempPre += "9";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "9";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.button0:
                if (tempPre.length() < temp.length()) {
                    tempPre += "0";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "0";
                    inputCond.setText(temp);
                    break;
                }
            case R.id.buttonBack123:
                if (tempPre.length() > 1) {
                    tempPre = tempPre.substring(0, tempPre.length() - 1);
                } else {
                    tempPre = "";
                }
                inputCond.setText(tempPre + tempLes);
                if (tempPre.length() <= inputCond.getText().length()) {
                    inputCond.setSelection(tempPre.length());
                } else {
                    inputCond.setSelection(inputCond.length()); // 设置光标在最后
                }
                return;
            // break;
            case R.id.switchtoABC:
                input_model123.setVisibility(View.GONE);
                input_modelABC.setVisibility(View.VISIBLE);
                break;
        }
        inputCond.setSelection(inputCond.length()); // 设置光标在最后
    }
};


OnClickListener letterOnclickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
        String temp = StringUtil.convertStringNull(inputCond.getText()
                .toString());
        String tempPre = temp.substring(0, inputCond.getSelectionEnd());
        String tempLes = temp.substring(inputCond.getSelectionEnd(),
                temp.length());
        switch (v.getId()) {
            case R.id.buttona:
                if (tempPre.length() < temp.length()) {
                    tempPre += "A";
                    temp = tempPre + tempLes;
                    inputCond.setText(temp);
                    inputCond.setSelection(tempPre.length());
                    return;
                } else {
                    temp += "A";
                    inputCond.setText(temp);
                    break;
                }
			case R.id.buttonb:
				if (tempPre.length() < temp.length()) {
					tempPre += "B";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "B";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonc:
				if (tempPre.length() < temp.length()) {
					tempPre += "C";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "C";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttond:
				if (tempPre.length() < temp.length()) {
					tempPre += "D";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "D";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttone:
				if (tempPre.length() < temp.length()) {
					tempPre += "E";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "E";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonf:
				if (tempPre.length() < temp.length()) {
					tempPre += "F";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "F";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttong:
				if (tempPre.length() < temp.length()) {
					tempPre += "G";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "G";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonh:
				if (tempPre.length() < temp.length()) {
					tempPre += "H";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "H";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttoni:
				if (tempPre.length() < temp.length()) {
					tempPre += "I";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "I";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonj:
				if (tempPre.length() < temp.length()) {
					tempPre += "J";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "J";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonk:
				if (tempPre.length() < temp.length()) {
					tempPre += "K";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "K";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonl:
				if (tempPre.length() < temp.length()) {
					tempPre += "L";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "L";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonm:
				if (tempPre.length() < temp.length()) {
					tempPre += "M";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "M";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonn:
				if (tempPre.length() < temp.length()) {
					tempPre += "N";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "N";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttono:
				if (tempPre.length() < temp.length()) {
					tempPre += "O";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "O";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonp:
				if (tempPre.length() < temp.length()) {
					tempPre += "P";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "P";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonq:
				if (tempPre.length() < temp.length()) {
					tempPre += "Q";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "Q";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonr:
				if (tempPre.length() < temp.length()) {
					tempPre += "R";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "R";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttons:
				if (tempPre.length() < temp.length()) {
					tempPre += "S";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "S";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttont:
				if (tempPre.length() < temp.length()) {
					tempPre += "T";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "T";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonu:
				if (tempPre.length() < temp.length()) {
					tempPre += "U";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "U";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonv:
				if (tempPre.length() < temp.length()) {
					tempPre += "V";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "V";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonw:
				if (tempPre.length() < temp.length()) {
					tempPre += "W";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "W";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonx:
				if (tempPre.length() < temp.length()) {
					tempPre += "X";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "X";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttony:
				if (tempPre.length() < temp.length()) {
					tempPre += "Y";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "Y";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonz:
				if (tempPre.length() < temp.length()) {
					tempPre += "Z";
					temp = tempPre + tempLes;
					inputCond.setText(temp);
					inputCond.setSelection(tempPre.length());
					return;
				} else {
					temp += "Z";
					inputCond.setText(temp);
					break;
				}
			case R.id.buttonBackABC:
                if (tempPre.length() > 1) {
                    tempPre = tempPre.substring(0, tempPre.length() - 1);
                } else {
                    tempPre = "";
                }
                inputCond.setText(tempPre + tempLes);
                if (tempPre.length() <= inputCond.getText().length()) {
                    inputCond.setSelection(tempPre.length());
                } else {
                    inputCond.setSelection(inputCond.length()); // 设置光标在最后
                }
                return;
            case R.id.switchto123:
                input_model123.setVisibility(View.VISIBLE);
                input_modelABC.setVisibility(View.GONE);
                break;
        }
        inputCond.setSelection(inputCond.length()); // 设置光标在最后

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

//			if (GlobalParams.BARCODE_FORMAT.equalsIgnoreCase("QR_CODE")) {
//				GlobalParams.CARD_TYPE = "4";// 二维码
//			} else {
				GlobalParams.CARD_TYPE = "5";// 条形码
//			}
			GlobalParams.QR_Info = scanResult;
			List<String> list = new ArrayList<String>();
			list.add(GlobalParams.QR_Info);
			isRunAutoReadCard=true;
			readCardMessage("", list);
		}
	}
	if_btn_click = false;
}


}
