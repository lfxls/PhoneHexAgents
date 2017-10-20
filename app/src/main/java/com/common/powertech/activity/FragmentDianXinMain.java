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

import com.common.powertech.ItemListActivity;
import com.common.powertech.PowertechApplication;
import com.common.powertech.R;
import com.common.powertech.bussiness.PULLParse_Shoudianshoufei_Query;
import com.common.powertech.bussiness.Request_ShouDianFee_Query;
import com.common.powertech.bussiness.Request_Shoudianshoufei_Query;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dbbean.PrinterTemp;
import com.common.powertech.dbbean.ServerAddress;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.ShoufeiQuery_Class;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import printUtils.gprinter;

/**
 * Created by yeqw on 2015/11/11.
 * @param <TextView>
 */
public class FragmentDianXinMain extends Fragment {
	
	 private static final String TAG = "FragmentDianXinShouFei";
	private Button btn1, btn2, btn3, btn4,btnOk,btnsms,btndata,SureImageView;
    private Button button1, button2, button3, button4, button5, button6,
    button7, button8, button9, button0, buttonClear, buttonBack;
	private ItemListActivity mActivity;
	private AutoCompleteTextView inputCond,inputCond2,inputCondd;
//	private EditText inputCond2;
	private String language="";
	private TextView mZhifuzongeTV,mDaozhangjineTV,mphoneNotitle;
	private AlertDialog huafeiquerenDialog,numberInputDialog;
	private static List<ShoufeiQuery_Class> mShouFeiList = new ArrayList<ShoufeiQuery_Class>();
	private HashMap<String, String> billBuyResult = null;// 售电收费返回结构体
	private ArrayAdapter<String> arrayAdapter;
    private List<String> numberList = new ArrayList<String>();
	private String mShoufeiZonge="";
	private String mShoufeiZonge2="";
	private Editable editable;
	private  String flag="";
	private ProgressDialog progressDialog;
	private String mRspCode = "";
	private String mRspMeg = "";
	private String mData = "";
	private String mSms = "";
	private String mAmt = "";
	private String phoneNo = "";
	private String ResourceType = "";
	private TextView  mPhoneNo;
	private String EnelName = "";
	private String EnelId = "";
	private String mTicket = "";// 打印信息
	private String mRspTicketXML = "";
    private Printer mPrinter = new Printer();
    private String mPrdordno = "";// 交易单号
    private String mIcJsonRes = "";// 写卡信息
    private String payWays="";//支付方式，默认现金
    private String parampay=""; //支付方式参数
    private String mComplexData = "";
	private String mFuwufeijisuanRspCode = "";// 服务费计算响应码
    private String mFuwufeijisuanRspMsg = "";// 服务费计算响应信息
    private String mFuwufeijisuanRspFee = "";// 服务费
    private String mFuwufeijisuanRspPrdordno = "";// 订单号
    private String mShoudianZongeRsp = "";//售电成功总额
    private String mShoudianjineRsp = "";//售电成功金额
    private String mShoudianFeeRsp = "";//售电成功服务费
    private String mZhiFuRspJine = "";   //支付总金额
    private String PhoneNo="";
    private boolean if_btn_click = false;       //防按钮双击
    private GpService mGpService = null;
    private              int                       mPrinterId           = 0;
    public static final String CONNECT_STATUS = "connect.status";
    private boolean isAutoReadCardRuned = false;// 自动读卡是否执行过而进入购电或收费
    private boolean isRunAutoReadCard = false;
    private boolean input_click = false;       //输入框标识
    private PrinterServiceConnection conn = null;
    private gprinter gprinter = new gprinter();
    PowertechApplication app ;
    
	private static HashMap<Integer, Boolean> mTotalSelectStateHM = new HashMap<Integer, Boolean>();
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
							
          View rootView = null;
		// onCreate(savedInstanceState);
	        mActivity = (ItemListActivity) getActivity();
	       // mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
	       
//	        mActivity = (ItemListActivity)getActivity();
	        
	        rootView = inflater.inflate(R.layout.fragment_multi_dianxinshoufei_main, container, false);
	        
	        
	    	ResourceType = String.valueOf(getArguments().getInt("ResourceType"));
	    	EnelName = getArguments().getString("enel_name");
	    	EnelId = getArguments().getString("enel_id");

	       String PHONEAMOUNT = app.getPHONEAMOUNTCONFIG();

	        btn1 = (Button)rootView.findViewById(R.id.button1);
        	btn2 = (Button)rootView.findViewById(R.id.button2);
        	btn3 = (Button)rootView.findViewById(R.id.button3);
        	btn4 = (Button)rootView.findViewById(R.id.button4);
        	btnOk = (Button)rootView.findViewById(R.id.buttonByUser);
        	btnsms=(Button)rootView.findViewById(R.id.buttonSMS); 
        	btndata=(Button)rootView.findViewById(R.id.buttonDATA);   
        	btnOk.setTextSize(20);
        	btnsms.setTextSize(20);
        	btndata.setTextSize(20);
        	    
        	
        	if(PHONEAMOUNT!= null && PHONEAMOUNT.length()!=0 && PHONEAMOUNT.split("\\|").length >= 3){
        		
        		String phoneAmount[]=PHONEAMOUNT.split("\\|");
            	btn1.setText(phoneAmount[0]);
            	btn2.setText(phoneAmount[1]);
            	btn3.setText(phoneAmount[2]);
        	}
        	
        	
//        	btn4.setText(phoneAmount[3]);
        	
//        	inputCond2 = (EditText) rootView
//                    .findViewById(R.id.inputCond2);
        	
	        inputCond2 = (AutoCompleteTextView) rootView
                    .findViewById(R.id.inputCond2);
        	
        	flag="1";
        	
        	
        	btn1.setOnClickListener(new OnClickListener() {
        		@Override
              public void onClick(View view) {
        			
        			//btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn1.setBackgroundResource(R.color.orange);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setVisibility(View.VISIBLE);
        			inputCond2.setVisibility(View.GONE);
        			flag="1";
        			inputCond2.setText("");
        			mShoufeiZonge=(String) btn1.getText(); 
        		}
        	});
               
        	btn2.setOnClickListener(new OnClickListener() {
        		@Override
              public void onClick(View view) {
        			
        			btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.color.orange);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setVisibility(View.VISIBLE);
        			inputCond2.setVisibility(View.GONE);
        			flag="1";
        			inputCond2.setText("");
        			mShoufeiZonge=(String) btn2.getText(); 

        		}
        	});
        	btn3.setOnClickListener(new OnClickListener() {
        		@Override
              public void onClick(View view) {
        			
        			btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.color.orange);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setVisibility(View.VISIBLE);
        			inputCond2.setVisibility(View.GONE);
        			flag="1";
        			inputCond2.setText("");
        			mShoufeiZonge=(String) btn3.getText(); 
        		}
        	});
        	
        	btn4.setOnClickListener(new OnClickListener() {
        		@SuppressWarnings("unchecked")
				@Override
              public void onClick(View view) {
        			
        			btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			inputCond2.setBackgroundResource(R.color.orange);
        			btn4.setVisibility(View.GONE);
        			hideSoftInputMethod(inputCond2);
//        			mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
//        			mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
        			inputCond2.setText("");
        			inputCond.clearFocus();
        			inputCond2.setVisibility(View.VISIBLE);
        			inputCond2.setFocusable(true);
        			inputCond2.setFocusableInTouchMode(true);
        			
        			input_click=true;      
        			
        			inputCond2.requestFocus();
        			inputCondd=inputCond2;
        			showNumberDialog(mActivity);
        		    flag="2";
        		}
        	});
	        

        	inputCond2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					input_click=true;
					inputCondd=inputCond2;
					showNumberDialog(mActivity);
				}
			});

        	
//		    inputCond2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
//	        InputFilter[] filters = {new CashierInpustFilter()};
//	        inputCond2.setFilters(filters);
 
        	 
        	btnOk.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("unchecked")
				@Override
              public void onClick(View view) {
					
					
					 if(inputCond2.getText().length()!=0 ){
						   mShoufeiZonge =inputCond2.getText().toString().trim().replaceAll(" ", "");
						}
						mAmt=mShoufeiZonge;
						
	                    String moneyStr = mShoufeiZonge;

	                    Pattern pattern = Pattern.compile("^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");

	                    Matcher matcher = pattern.matcher(moneyStr);
	                    
	                    
						if (inputCond.getText().length() == 0) {
							SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
							return;
						}
					    PhoneNo=inputCond.getText().toString().trim().replaceAll(" ", "");
						

						if(flag.equals("1") &&  ("".equals(mShoufeiZonge) || mShoufeiZonge == null)){
							SystemUtil.displayToast(mActivity, R.string.dianxinxiangqing_error1);

							return;
						}
						if(flag.equals("2") && inputCond2.getText().length()==0){
							SystemUtil.displayToast(mActivity, R.string.dianxinxiangqing_error2);
							return;
						}
						
						if (!matcher.matches()) {
	                        // 金额格式不正确
	                        SystemUtil.displayToast(mActivity,
	                                R.string.shoudianxiangqing_jineshurubuzhengque);
	                        return;
	                    }
						
						if(flag.equals("2") && Float.parseFloat(inputCond2.getText().toString())<1){
							 SystemUtil.displayToast(mActivity,
		                               R.string.shoudianxiangqing_error2);
		                       return;
						}
						
						
					
					
				boolean[] state = getConnectState();
						
				 if(state[mPrinterId]!=true){
			
				        	
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

					isRunAutoReadCard = false;
					if (mActivity == null) {
						mActivity = (ItemListActivity) getActivity();
					}
					
					new FeeQueryTask(mActivity).execute();
					btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
					mShoufeiZonge="";
					inputCond.setText("");   
			  	   	   }
			  	   	  });
			  	   builder.create().show();
			   }else{
		        	
				  new FeeQueryTask(mActivity).execute();
				  btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
       			  btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
       			  btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
       			  btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
				  mShoufeiZonge="";
				  inputCond.setText("");
		         }
        		}
				
        	});
        	
        	//短信选择
        	btnsms.setOnClickListener(new OnClickListener() {

				@Override
              public void onClick(View view) {
					
					
					if (inputCond.getText().length() == 0) {
						SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
						return;
					}					
					 PhoneNo=inputCond.getText().toString().trim().replaceAll(" ", "");
	
//					PhoneNo="27791231234";
					btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setVisibility(View.VISIBLE);
        			inputCond2.setText("");
        			inputCond2.setVisibility(View.GONE);
					
					 boolean[] state = getConnectState();
					 if(state[mPrinterId]!=true){
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
						isRunAutoReadCard = false;
						if (mActivity == null) {
							mActivity = (ItemListActivity) getActivity();
						}

					    Bundle bundle = new Bundle();
						Intent mDianXinDuanXinXiangQingIntent = new Intent(
								mActivity, DianXinDuanXinXiangQingActivity.class);
						mDianXinDuanXinXiangQingIntent.putExtra("key", PhoneNo);
						mDianXinDuanXinXiangQingIntent.putExtra("PowerType", "3");//用来区分是短息还是流量				
						mDianXinDuanXinXiangQingIntent.putExtra("EnelName", EnelName);
						mDianXinDuanXinXiangQingIntent.putExtra("EnelId", EnelId);
						startActivity(mDianXinDuanXinXiangQingIntent,bundle);
						inputCond.setText("");
				  	   	   
				  	   	   }
				  	   	  });
				  	   builder.create().show();
				   }else{

				    Bundle bundle = new Bundle();
					Intent mDianXinDuanXinXiangQingIntent = new Intent(
							mActivity, DianXinDuanXinXiangQingActivity.class);
					mDianXinDuanXinXiangQingIntent.putExtra("key", PhoneNo);
					mDianXinDuanXinXiangQingIntent.putExtra("PowerType", "3");//用来区分是短息还是流量				
					mDianXinDuanXinXiangQingIntent.putExtra("EnelName", EnelName);
					mDianXinDuanXinXiangQingIntent.putExtra("EnelId", EnelId);
					startActivity(mDianXinDuanXinXiangQingIntent,bundle);
					inputCond.setText("");
				  }
        		}
        	});
        	
        	

        	
        	//流量选择
        	btndata.setOnClickListener(new OnClickListener() {
				@Override
              public void onClick(View view) {
					
					if (inputCond.getText().length() == 0) {
					SystemUtil.displayToast(mActivity, R.string.shoudianxiangqing_error1);
					return;
				}
				
//				 PhoneNo="27791231234";
				   PhoneNo=inputCond.getText().toString().trim().replaceAll(" ", "");
					
					btn1.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn2.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn3.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setBackgroundResource(R.drawable.button_number_violet_port_shape);
        			btn4.setVisibility(View.VISIBLE);
        			inputCond2.setText("");
        			inputCond2.setVisibility(View.GONE);
					 boolean[] state = getConnectState();
					 if(state[mPrinterId]!=true){
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

						isRunAutoReadCard = false;
						if (mActivity == null) {
							mActivity = (ItemListActivity) getActivity();
						}
					    Bundle bundle = new Bundle();

						Intent mDianXinDuanXinXiangQingIntent = new Intent(
								mActivity, DianXinDuanXinXiangQingActivity.class);
						mDianXinDuanXinXiangQingIntent.putExtra("key", PhoneNo);
						mDianXinDuanXinXiangQingIntent.putExtra("PowerType", "4");
						mDianXinDuanXinXiangQingIntent.putExtra("EnelName", EnelName);
						mDianXinDuanXinXiangQingIntent.putExtra("EnelId", EnelId);
						startActivity(mDianXinDuanXinXiangQingIntent,bundle);
						inputCond.setText("");
				  	   	   }
				  	   	  });
				  	   builder.create().show();
				   }else{
										    		
				    Bundle bundle = new Bundle();
					Intent mDianXinDuanXinXiangQingIntent = new Intent(
							mActivity, DianXinDuanXinXiangQingActivity.class);
					mDianXinDuanXinXiangQingIntent.putExtra("key", PhoneNo);
					mDianXinDuanXinXiangQingIntent.putExtra("PowerType", "4");
					mDianXinDuanXinXiangQingIntent.putExtra("EnelName", EnelName);
					mDianXinDuanXinXiangQingIntent.putExtra("EnelId", EnelId);
					startActivity(mDianXinDuanXinXiangQingIntent,bundle);
					inputCond.setText("");
        		 }
				}
        	});
        	
        	
	        inputCond = (AutoCompleteTextView) rootView
                    .findViewById(R.id.inputCond);
            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                if (language.equalsIgnoreCase("en")) {
                    inputCond.setTextSize(18);
                }else if(language.equalsIgnoreCase("fr")){
                    inputCond.setTextSize(16);
                }
            }
            
           	 inputCond.setOnClickListener(new OnClickListener() {
				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					inputCondd=inputCond;
    					input_click=false;
    					showNumberDialog(mActivity);
    					
    				}
    			});
            hideSoftInputMethod(inputCond);
            if (Preferences.getComplexDataInPreference(mActivity,
                    Preferences.KEY_MeterOrUser_No, "0") != null
                    && !Preferences
                    .getComplexDataInPreference(mActivity,
                            Preferences.KEY_MeterOrUser_No, "0")
                    .toString().equalsIgnoreCase("0")) {
                numberList = (List<String>) Preferences
                        .getComplexDataInPreference(mActivity,
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
            arrayAdapter = new ArrayAdapter<String>(mActivity,
                    android.R.layout.simple_list_item_1, numberList);
            inputCond.setAdapter(arrayAdapter);                
            inputCond.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s == null || s.length() == 0) {
                        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                            if (language.equalsIgnoreCase("en")) {
                                inputCond.setTextSize(18);
                            }else if(language.equalsIgnoreCase("fr")){
                                inputCond.setTextSize(16);
                            }
                        }
                        return;
                    }
                    if(start == 0 && count == 1){

                        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
                            if (language.equalsIgnoreCase("en")
                                    || language.equalsIgnoreCase("fr")) {
                                inputCond.setTextSize(22);
                            }
                        }
                    }
                    if (s.toString().contains("#")
                            || s.toString().contains(".")
                            || s.toString().contains("*")) {
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
                            if ((sb.length() == 5 || sb.length() == 10 || sb
                                    .length() == 15)
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
                        int selEndIndex = Selection
                                .getSelectionEnd(editable);
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
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });
             
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
	
    
  //数字弹框	 
  	public void showNumberDialog(Context mContext) {
  		    LayoutInflater inflater = LayoutInflater.from(mContext);
  	        View view = inflater.inflate(R.layout.dialog_duoshangpin_number, null);
     
  	        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
  	        builder.setView(view);
  	        numberInputDialog = builder.create();
  	        
  	        initNumberButton(view);
  	        
  	        
  	      Window window = numberInputDialog.getWindow(); 
  	        window.setGravity(Gravity.BOTTOM);   //window.setGravity(Gravity.BOTTOM);  
  	        
  	        
//  	     WindowManager.LayoutParams lp = window.getAttributes();   
//  	      // 设置透明度为0.3   
//  	      lp.alpha = 0.6f;   
//  	       window.setAttributes(lp); 
  	        
//  	      
//  	    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,   
//  	    		        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
  	      
  	       
	        
	        numberInputDialog.show();
//	        WindowManager.LayoutParams lp = window.getAttributes();   
//            lp.width = WindowManager.LayoutParams.FILL_PARENT;   //设置宽度充满屏幕  
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;  
//            window.setAttributes(lp);
            
            
            
        ImageView CloseImageView = (ImageView) view
           .findViewById(R.id.btnCloseActivity);
        CloseImageView.setOnClickListener(new OnClickListener() {
    	@Override
		public void onClick(View v) {
    		//if_btn_click = false;
    		numberInputDialog.dismiss();
		}
    });
            
            
  	 }
    
  	
  	//actionsheepDialog
  	
  	
	//确认话费弹框	 
	public void showHuafeiquerenDialog(Context mContext) {
		 LayoutInflater inflater = LayoutInflater.from(mContext);
	        View view = inflater.inflate(
	                R.layout.dialog_duoshangpin_huafeiqueren, null);
	        
	      
	        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	       
	        builder.setView(view);
	        huafeiquerenDialog = builder.create();
	        TextView mfuwuTV= (TextView) view.findViewById(R.id.fuwufei_Tv);
	        TextView mzhifuTV = (TextView) view.findViewById(R.id.zhifujine_Tv);
	        TextView mchognzhiTV = (TextView) view.findViewById(R.id.chongzhijine_Tv);
	        mPhoneNo = (TextView) view.findViewById(R.id.phoneNo_Tv);
	        mphoneNotitle = (TextView) view.findViewById(R.id.phoneNo_title);
	        mPhoneNo.setText(PhoneNo);
//	        mPhoneNo.setText("27791231234");
	       
	        BigDecimal b1=new BigDecimal(mAmt);//购买金额
	        BigDecimal b2=new BigDecimal(mFuwufeijisuanRspFee
	                .equalsIgnoreCase("") ? "0"
	                : mFuwufeijisuanRspFee);

	        BigDecimal b3=new BigDecimal(mZhiFuRspJine);//支付金额
	            
	        mzhifuTV.setText(keepDecimalPlaces(b3.toString()));
	       
	        mfuwuTV.setText(keepDecimalPlaces(b2.toString()));
	        
	        mchognzhiTV.setText(keepDecimalPlaces(b3.subtract(b2).toString()));
	        
	        huafeiquerenDialog.show();	
	        
	        
	        ImageView CloseImageView = (ImageView) view
	                .findViewById(R.id.btnCloseDialog);
	        CloseImageView.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		//if_btn_click = false;
	        		SureImageView.setEnabled(true);
	        		huafeiquerenDialog.dismiss();
				}
	        });
	        
             SureImageView = (Button) view.findViewById(R.id.btn_qrgd);
	         SureImageView.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		
	        		SureImageView.setEnabled(false);
	        	
	        		billBuyQuery();
				}
	        });
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
	    buttonClear = (Button) view.findViewById(R.id.buttonClear);
	    buttonBack = (Button) view.findViewById(R.id.buttonBack);
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
	    buttonClear.setOnClickListener(numberOnclickListener);
	    buttonBack.setOnClickListener(numberOnclickListener);
	  
	    if(input_click){
	    	buttonClear.setText(".");
	    }
	    if(!input_click){
	    	buttonClear.setText(R.string.main_button_shoufei_qingkong);
	    }
	}


	/**
	 * 按键监听器
	 */
	OnClickListener numberOnclickListener = new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	        String temp = StringUtil.convertStringNull(inputCondd.getText()
	                .toString());
	        String tempPre = temp.substring(0, inputCondd.getSelectionEnd());
	        String tempLes = temp.substring(inputCondd.getSelectionEnd(),
	                temp.length());
	        switch (v.getId()) {
	            case R.id.button1:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "1";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "1";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button2:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "2";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "2";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button3:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "3";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "3";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button4:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "4";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "4";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button5:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "5";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "5";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button6:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "6";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "6";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button7:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "7";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "7";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button8:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "8";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "8";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button9:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "9";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "9";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.button0:
	                if (tempPre.length() < temp.length()) {
	                    tempPre += "0";
	                    temp = tempPre + tempLes;
	                    inputCondd.setText(temp);
	                    inputCondd.setSelection(tempPre.length());
	                    return;
	                } else {
	                    temp += "0";
	                    inputCondd.setText(temp);
	                    break;
	                }
	            case R.id.buttonBack:
	                if (tempPre.length() > 1) {
	                    tempPre = tempPre.substring(0, tempPre.length() - 1);
	                } else {
	                    tempPre = "";
	                }
	                inputCondd.setText(tempPre + tempLes);
	                if (tempPre.length() <= inputCondd.getText().length()) {
	                    inputCondd.setSelection(tempPre.length());
	                } else {
	                    inputCondd.setSelection(inputCondd.length()); // 设置光标在最后
	                }
	                return;
	            // break;
	            case R.id.buttonClear:
	            	if(!input_click){
	            		 temp = "";
	 	                inputCondd.setText(temp);
	 	                break;
	            		
	            	}
	          	    if(input_click){
	          	    	if (tempPre.length() < temp.length()) {
		                    tempPre += ".";
		                    temp = tempPre + tempLes;
		                    inputCondd.setText(temp);
		                    inputCondd.setSelection(tempPre.length());
		                    return;
		                } else {
		                    temp += ".";
		                    inputCondd.setText(temp);
		                    break;
		                }
	          	    }
	               
	        }
	        inputCondd.setSelection(inputCondd.length()); // 设置光标在最后
	    }
	};
	
   
    
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
                .setContext(mActivity);
        Request_Shoudianshoufei_Query.setMeterNo(PhoneNo);// 表号
        
//        String amtParm = String.valueOf(mAmt);

        Request_Shoudianshoufei_Query.setAmt(keepDecimalPlaces(mZhiFuRspJine));// 总金额
       // Request_Shoudianshoufei_Query.setPayWays(payWays);// 支付方式
        Request_Shoudianshoufei_Query.setPrdordno(mFuwufeijisuanRspPrdordno);// 订单号
        
        Request_Shoudianshoufei_Query.setIcType("");// IC类型
        Request_Shoudianshoufei_Query.setICJsonReq("");// 读卡信息
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
                .setTitle(getString(R.string.dianxin_querenchongzhi));
        progressDialog.setMessage(getString(R.string.progress_conducting));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }   
	
    //异步打印
    private class PrintTask extends AsyncTask<Void, Void, String> {
    	HashMap a;
        @Override
        protected void onPreExecute() {
//            if (MultiquerenDialog != null
//                    && MultiquerenDialog.isShowing()) {
//                MultiquerenDialog.dismiss();
//                //showShoudianchenggongDialog(ShouDianXiangQingActivity.this);
//            }
            //再次打印
//            if (mPrintAgainBtn != null) {
//                mPrintAgainBtn.setEnabled(false);
//            }
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
        	mRspTicketXML = mRspTicketXML.replace("&amp;caret;","^");
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
//        mPasswordView.setVisibility(View.GONE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
     
//            if (bt_querengoudian != null) {
//                bt_querengoudian.setEnabled(true);
//            }
            
//            if (mPrintAgainBtn != null) {
//                mPrintAgainBtn.setEnabled(true);
//            }
        }
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
	
	
    private boolean feeQuery() {// 购电服务费查询 成功返回true，失败返回false
        Request_ShouDianFee_Query.setContext(mActivity);
        // 售电只有AMT
        if(mAmt.substring(mAmt.length()-1).equals("."))//如果购买金额末尾为'.',删除掉'.'
        {
      	  mAmt=mAmt.substring(0,mAmt.length()-1);
        }
        Request_ShouDianFee_Query.setAmt(String.valueOf(mAmt));
        Request_ShouDianFee_Query.setReceID("");
//        Request_ShouDianFee_Query.setIcType(GlobalParams.CARD_TYPE);
//        Request_ShouDianFee_Query.setICJsonReq(GlobalParams.IC_JSON_REQ);
        
        Request_ShouDianFee_Query.setMeterNo(PhoneNo);
//        Request_ShouDianFee_Query.setMeterNo("27791231234");
        Request_ShouDianFee_Query.setPrdType(ResourceType);//资源类型
        Request_ShouDianFee_Query.setPayWays(payWays);// 支付方式
        Request_ShouDianFee_Query.setProductCode("");// 产品代码
        Request_ShouDianFee_Query.setPowerType("");// 套餐类型
        Request_ShouDianFee_Query.setEnelId(EnelId);// 电力公司
        
        
        String requestXML = Request_ShouDianFee_Query.getRequsetXML();
        // 模拟数据
        // requestXML="<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE></TOP><BODY><AMT>2000</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>ef1fc3307410b1e0f7a15ed0e51f9e17</SIGNATURE></TAIL></ROOT>";
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
        mZhiFuRspJine = Client.Parse_XML(reponseXML, "<ORDAMT>", "</ORDAMT>");
        
        
        mFuwufeijisuanRspPrdordno = Client.Parse_XML(reponseXML, "<PRDORDNO>", "</PRDORDNO>");//订单号
        if (mFuwufeijisuanRspCode.equalsIgnoreCase("00000")) {
            Log.e(TAG, "mFuwufeijisuanRspFee = " + mFuwufeijisuanRspFee);
            return true;
        } else {
            // 服务器返回系统超时，返回到登录页面
            if (mFuwufeijisuanRspCode.equals("00011")) {
                Toast.makeText(mActivity,
                        mFuwufeijisuanRspMsg, Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(mActivity);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
            }
            return false;
        }
    }
	
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
//            setButtonEnable();
//            isConfirmChargeCommit=false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result != 0) {
            	showHuafeiquerenDialog(mActivity);
            } else {
                if (mFuwufeijisuanRspMsg.equalsIgnoreCase("")) {
                    SystemUtil
                            .displayToast(
                            		mActivity,
                                    getString(R.string.shoufeixiangqing_fuwufeijisuanshibai));
                } else {
                    SystemUtil.displayToast(mActivity,
                            mFuwufeijisuanRspMsg);
                }
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
	
    
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
	  Handler mShouDianHandler = new Handler() {

	        @Override
	        public void handleMessage(Message msg) {
	        	SureImageView.setEnabled(true);
	            switch (msg.what) {
	                case 0:
	                    try {
	                        if (progressDialog != null && progressDialog.isShowing()) {
	                            progressDialog.dismiss();
	                            SystemUtil
	                                    .displayToast(
	                                            mActivity,
	                                            getString(R.string.dianxin_failed));
	                        }
//	                        if (bt_querengoudian != null) {
//	                            bt_querengoudian.setEnabled(true);
//	                        }
	                        

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
	                            Toast.makeText(mActivity, mRspMeg,
	                                    Toast.LENGTH_LONG).show();
	                            SystemUtil.setGlobalParamsToNull(mActivity);
	                            DummyContent.ITEM_MAP.clear();
	                            DummyContent.ITEMS.clear();
	                            Intent intent = new Intent(mActivity, LoginActivity.class);
	                            mActivity.startActivity(intent);
	                        } else {
	                            if (!mRspMeg.equalsIgnoreCase("")) {
	                                SystemUtil.displayToast(
	                                		mActivity, mRspMeg);
	                            } else {
	                                SystemUtil
	                                        .displayToast(
	                                        		mActivity,
	                                                getString(R.string.dianxin_failed));
	                            }
//	                            if (bt_querengoudian != null) {
//	                                bt_querengoudian.setEnabled(true);
//	                            }
	                        }
	                    } else {
	                        SystemUtil.displayToast(mActivity,
	                                getString(R.string.dianxin_success));
	                        try {
	                            // 获取押金余额并更新主界面
	                            String banlance = Client.Parse_XML(
	                                    GlobalParams.RETURN_DATA, "<BANLANCE>",
	                                    "</BANLANCE>");
	                            GlobalParams.CASH_AC_BAL = banlance;
	                            Intent intentYaJinYuEr = new Intent(
	                                    GlobalParams.UPDATE_YAJINYUER_ACTION);
	                            mActivity.sendBroadcast(intentYaJinYuEr);

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
	                                        keepDecimalPlaces(String.valueOf(mAmt)));
	                                BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
	                                		mActivity,
	                                        JinRiShouDian.class);
	                                baseDao.create(mJinRiShouDian);
	                                Intent it = new Intent(
	                                        GlobalParams.UPDATE_JINRISHOUDIAN_ACTION);
	                                mActivity.sendBroadcast(it);
	                                // 购电成功
	                                mTicket = mRspTicketXML;
	                                new PrintTask()
	                                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	                            } else {
	                                SystemUtil
	                                        .displayToast(
	                                                mActivity,
	                                                getString(R.string.dianxin_failed)
	                                                        + mRspMeg
	                                        );
	                            }
	                            mphoneNotitle.setText(R.string.dianxin_success);
	                            SureImageView.setText(R.string.pinzhengbuda_btn_bd);
	                            SureImageView.setOnClickListener(new OnClickListener() {
	                	        	@Override
	                				public void onClick(View v) {
	                	        		new PrintTask()
                                      .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	                				}
	                	        });
	                        } catch (Exception ex) {
	                            Log.e(TAG, "Exception = " + ex.toString());
	                        }
	                    }
	                    break;
	            }
	            super.handleMessage(msg);
	        }

	    };

}
