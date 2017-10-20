package com.common.powertech.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import printUtils.gprinter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.powertech.R;
import com.common.powertech.bussiness.PULLParse_Shoudianshoufei_Query;
import com.common.powertech.bussiness.PULLParse_ShoufeiQueryRequest;
import com.common.powertech.bussiness.Request_ShouDianFee_Query;
import com.common.powertech.bussiness.Request_Shoudianshoufei_Query;
import com.common.powertech.bussiness.Request_Shoufei_Query;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.xml.ShoufeiQuery_Class;
import com.zxing.view.ViewfinderView;



/**
 *  功能：duanxinActivity 作者:hq 日期:2017-2-9
 */
public class DianXinDuanXinXiangQingActivity extends Activity {
	  private static final String TAG = "DianXinXiaoShouXiangQingActivity";
	  
	  private Button SureImageView,SureImageVieww;
	  
	  private ProgressDialog progressDialog;
	  private AlertDialog MultiquerenDialog ;
	  private ImageView mCloseImageView;
	  private ViewfinderView viewfinderView;
	  private SurfaceView surfaceView;
	  private TextView  mPhoneNo,mContent,mphoneNotitle;
	  private String userAmount[];
	  private String mAmt = "";
	  private String mDescription = "";//产品描述
	  private String mProductCode = "";//产品代码
	  private  DianXinDuanXinXiangQingActivity mActivity;
	  private String mRspCode = "";
	  private String mRspMeg = "";
	  private String mData = "";
	  private String mSms = "";
	  private String phoneNo="";
	  
	  private String EnelName="";
	  private String EnelId="";
	  
	  private String PowerType="";
	  private ShoufeiQuery_Class mShouDianXiangQingItem;
	    private String mFuwufeijisuanRspCode = "";// 服务费计算响应码
	    private String mFuwufeijisuanRspMsg = "";// 服务费计算响应信息
	    private String mFuwufeijisuanRspFee = "";// 服务费
	    private String mZhiFuRspJine = "";       // 总金额
	    private String mFuwufeijisuanRspPrdordno = "";// 订单号
	    private String mShoudianZongeRsp = "";//售电成功总额
	    private String mShoudianjineRsp = "";//售电成功金额
	    private String mShoudianFeeRsp = "";//售电成功服务费
	    private String mTicket = "";// 打印信息
	    private HashMap<String, String> billBuyResult = null;// 售电收费返回结构体
	    private Printer mPrinter = new Printer();
	    private String mPrdordno = "";// 交易单号
	    private String mIcJsonRes = "";// 写卡信息
	    private String payWays="";//支付方式，默认现金
	    private String parampay=""; //支付方式参数
	    private String mComplexData = "";
	    private Spinner spinner;
	    private ArrayAdapter<String> arr_adapter;
	    private gprinter gprinter = new gprinter();
	    private String mRspTicketXML = "";
//	    private View mPasswordView;
	    private String ResourceType ="";
	
		private static HashMap<Integer, Boolean> mTotalSelectStateHM = new HashMap<Integer, Boolean>();
		View view;
		
		
	  @SuppressWarnings("unchecked")
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        Log.e(TAG, "onCreate");
	        GlobalParams.If_CloseFlashLight = true;
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        
	        ResourceType="4";
	        Intent  aa= getIntent();
	        phoneNo = aa.getStringExtra("key");//电话号码
	        PowerType=aa.getStringExtra("PowerType");//区分短信还是流量
	        EnelName=aa.getStringExtra("EnelName");
	        EnelId=aa.getStringExtra("EnelId");
	        
	        if (GlobalParams.Theme == 1) {
	            setTheme(R.style.VioletTheme);
	        } else if (GlobalParams.Theme == 1) {
	            setTheme(R.style.OrangeTheme);
	        }
	        mActivity = DianXinDuanXinXiangQingActivity.this;
	        
	        LayoutInflater inflater=LayoutInflater.from(DianXinDuanXinXiangQingActivity.this);
	        view = inflater.inflate(R.layout.zctivity_duoshangpin_taocanxiangqing, null);
	        
	        shoufeiQuery("1");
	        

	  }
	  @Override
		public void onResume() {
			super.onResume();
			Log.e(TAG, "onResume");
		
		}
	     private void initUI() {
	        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
	        surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
	        mCloseImageView = (ImageView) view.findViewById(R.id.btnCloseDialog);
	        mCloseImageView.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View arg0) {
	                onBackPressed();
	            }
	        });
	           

	          //支付方式、暂时默认现金，界面不显示

//	        if (parampay == null || parampay=="" ) {   
//	            TextView  tv=(TextView)findViewById(R.id.spinner_paywayss);
//	      	  tv.setVisibility(View.GONE);
//	      	  Spinner  tvv=(Spinner)findViewById(R.id.spinner_payways);
//	      	  tvv.setVisibility(View.GONE); 
//	    
//	          }else {
//	          	 spinner = (Spinner) view.findViewById(R.id.spinner_payways);
//	               //数据
//	               List<String> data_list = new ArrayList<String>();
//	       		String[] strarray=parampay.split(";"); 
//	       	      for (int i = 0; i < strarray.length; i++) 
//	       	      {
//	       	    	  data_list.add(strarray[i]);
//	       	      }
//	               //适配器
//	               arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
//	               //设置样式
//	               arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	               //加载适配器
//	               spinner.setAdapter(arr_adapter);   
//	               
//	               spinner.setOnItemSelectedListener(new OnItemSelectedListener() {        	
//	       			@Override
//	       			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {				
//	       				payWays =spinner.getItemAtPosition(position).toString();
//	                            TextView tv = (TextView)view;  
//	                            if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS550") ) {
//	                          	  tv.setTextSize(25.0f);    //设置大小  	
//	                            }else{
//	                          	  tv.setTextSize(18.0f); 
//	                            }
//	       				}
//	       			@Override
//	       			public void onNothingSelected(AdapterView<?> parent) {
//	       				
//	       			} 
//	               	});    	
//	          }
	        

	        final JSONArray myJsonArray;
	        
	        try {
	        	//
	        	if (PowerType.equals("3")){
	        		myJsonArray = new JSONArray(mSms);
	        		mContent = (TextView) view.findViewById(R.id.Content_TV);
	    	        mContent.setText(R.string.dianxin_duanxinchongzhi);
	        	}
	        	else{	
	        		myJsonArray = new JSONArray(mData);	
	        		mContent = (TextView) view.findViewById(R.id.Content_TV);
	    	        mContent.setText(R.string.dianxin_liuliangchongzhi);
	        	}
				
				final Button Btn[] = new Button[myJsonArray.length()];
				 for(int i=0 ; i < myJsonArray.length() ;i++)
				    {
				     //获取每一个JsonObject对象
				    JSONObject myjObject = myJsonArray.getJSONObject(i);
				     
				     //获取每一个对象中的值
				    String numString = myjObject.getString("amount");
				    String englishScore = myjObject.getString("productCode");
				    String historyScore = myjObject.getString("description");
				    String geographyScore = myjObject.getString("top5Seller");
				    
				    LinearLayout layoutBtn = (LinearLayout) view.findViewById(R.id.layoutBtn);
			        DisplayMetrics dm = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(dm);
			        int width = dm.widthPixels;
			        int height = dm.heightPixels;
			        
		              Btn[i]=new Button(this);
		              Btn[i].setId(2000+i); 
		              Btn[i].setText(historyScore);
		              Btn[i].setTextColor(Color.rgb(255, 255, 255));
		              RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams ((width-50)/1,LayoutParams.WRAP_CONTENT);  //设置按钮的宽度和高度
		              
		              btParams.topMargin = 5;
		              layoutBtn.addView(Btn[i],btParams);   //将按钮放入layout组件

				    }
				 
				   for (int k = 0; k <= Btn.length-1; k++) { 
		        	   //这里不需要findId，因为创建的时候已经确定哪个按钮对应哪个Id
		        	   Btn[k].setTag(k);                //为按钮设置一个标记，来确认是按下了哪一个按钮
		        	   Btn[k].setBackgroundResource(R.drawable.button_number_violet_port_shape);
		        	   Btn[k].setOnClickListener(new Button.OnClickListener() {
		        	    @Override
		        	        public void onClick(View v) {
		        	    	
		        	    	for (int k = 0; k <= Btn.length-1; k++) {
		        	    		Btn[k].setBackgroundResource(R.drawable.button_number_violet_port_shape);
		        	    	}
		        	            int i = (Integer) v.getTag();   //这里的i不能在外部定义，因为内部类的关系，内部类好多繁琐的东西，要好好研究一番
		        	           
		        	             
		        	            try {
									mAmt =myJsonArray.getJSONObject(i).getString("amount");
									mProductCode =myJsonArray.getJSONObject(i).getString("productCode");
									mDescription =myJsonArray.getJSONObject(i).getString("description");
									//Toast.makeText(getApplicationContext(),mAmt,Toast.LENGTH_SHORT).show();	
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

		        	            Btn[i].setBackgroundResource(R.color.orange);   
		        	       }
		        	     });
		        	   }

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
          Button SureImageView = (Button)  view.findViewById(R.id.btn_qrgd);
	        
	        
	        mPhoneNo = (TextView) view.findViewById(R.id.phoneNo_Tv);
	        mPhoneNo.setText(phoneNo);
	        
	        //点击确认按钮
	        SureImageView.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		
	        		if(mAmt == null || mAmt.length() <= 0){
	        		SystemUtil.displayToast(mActivity, R.string.dianxin_please_chooseProduct);
//	        		Toast.makeText(getApplicationContext(),"请选择商品",Toast.LENGTH_SHORT).show();
	   		       //Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
	   		        	return;
	        		}
	        		
	        		new FeeQueryTask(DianXinDuanXinXiangQingActivity.this).execute();
//	        		showMultiQueRenDialog(DianXinDuanXinXiangQingActivity.this);
				}
	        });  
	  }
	  

	  Handler mhandler = new Handler() {
			public void handleMessage(Message msg) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				// 处理消息
				switch (msg.what) {
				case 0:
					// 联网失败
					// 没有加载到数据，页码返回到当前页
					 try {
	                     if (progressDialog != null
	                             && (!mActivity.isFinishing())) {
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
					System.out.println("收费查询响应：" + GlobalParams.RETURN_DATA);
					mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<RSPCOD>", "</RSPCOD>");
					mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<RSPMSG>", "</RSPMSG>");
					
					mData = Client.Parse_XML(GlobalParams.RETURN_DATA,
					"<DATALIST>", "</DATALIST>");
			      
					mSms = Client.Parse_XML(GlobalParams.RETURN_DATA,
					"<SMSLIST>", "</SMSLIST>");
					
					parampay = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<PAY_WEYS>", "</PAY_WEYS>");
					
					if (mRspCode.equals("00000") || mRspCode.equals("11111") || mRspCode.equals("11112")) {
						
						InputStream in;
						List<ShoufeiQuery_Class> mQuestList = new ArrayList<ShoufeiQuery_Class>();
						try {
							in = new ByteArrayInputStream(
									GlobalParams.RETURN_DATA.getBytes("UTF-8"));
							mQuestList = PULLParse_ShoufeiQueryRequest
									.getBDList(in);
							int oldListSize = mTotalSelectStateHM.size();
							
							for (ShoufeiQuery_Class sc : mQuestList) {
								boolean isSame = false;
								if (!isSame) {
									mTotalSelectStateHM.put(oldListSize++, false);
								}
							}
							initUI();
					        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					        setContentView(view);
					        
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					} else {
						if (mRspMeg.equalsIgnoreCase("")) {
							SystemUtil.displayToast(mActivity,
									R.string.shoufeixiangqing_wangluoyichang);
							onBackPressed();
						} else {
							SystemUtil.displayToast(mActivity, mRspMeg);
							if(mRspCode.equalsIgnoreCase("00011")){
								SystemUtil.setGlobalParamsToNull(DianXinDuanXinXiangQingActivity.this);
							    DummyContent.ITEM_MAP.clear();
							    DummyContent.ITEMS.clear();
								Intent intent = new Intent(mActivity, LoginActivity.class);
	                            mActivity.startActivity(intent);
								break;
							}else{
								onBackPressed();
							}
							
						}	
					}
					break;
				default:
					break;
				}
			}
		};
		
		 @Override  
		    public void onBackPressed() {  
		        Log.d(TAG, "onBackPressed()");  
		        super.onBackPressed();  
		    }  

	  	  
		private void shoufeiQuery(String pageNum) {
			createDialog();
			progressDialog.setTitle(getString(R.string.dialog_check));
			progressDialog.setMessage(getString(R.string.progress_conducting));
			// 设置进度条是否不明确
			progressDialog.setIndeterminate(false);
			// 是否可以按下退回键取消
			progressDialog.setCancelable(false);
			progressDialog.show();
			String requestXML = "";
			Request_Shoufei_Query.setContext(mActivity);
			Request_Shoufei_Query.setPageNum(pageNum);
			Request_Shoufei_Query.setIcType(GlobalParams.CARD_TYPE);
			Request_Shoufei_Query.setICJsonReq(GlobalParams.IC_JSON_REQ);
			Request_Shoufei_Query.setMeterNum(phoneNo);
			Request_Shoufei_Query.setResourceType(ResourceType);
			Request_Shoufei_Query.setEnelName(EnelName);	
			Request_Shoufei_Query.setEnelId(EnelId);
			Request_Shoufei_Query.setUserNum("");
			

			requestXML = Request_Shoufei_Query.getRequsetXML();
			System.out.println("收费查询请求：" + requestXML);
			Client.SendData("PBillQuery", requestXML, mhandler);
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
		

		
		private boolean feeQuery() {// 购电服务费查询 成功返回true，失败返回false
	        Request_ShouDianFee_Query.setContext(mActivity);
	        // 售电只有AMT
	        if(mAmt.substring(mAmt.length()-1).equals("."))//如果购买金额末尾为'.',删除掉'.'
	        {
	      	  mAmt=mAmt.substring(0,mAmt.length()-1);
	        }
	        
	        Request_ShouDianFee_Query.setAmt(String.valueOf(mAmt));
	        Request_ShouDianFee_Query.setReceID("");
//	        Request_ShouDianFee_Query.setIcType(GlobalParams.CARD_TYPE);
//	        Request_ShouDianFee_Query.setICJsonReq(GlobalParams.IC_JSON_REQ);
	        Request_ShouDianFee_Query.setMeterNo(phoneNo);
	        Request_ShouDianFee_Query.setPrdType(ResourceType);//资源类型
	        Request_ShouDianFee_Query.setPayWays(payWays);// 支付方式
	        Request_ShouDianFee_Query.setProductCode(mProductCode+"|"+mDescription.trim());// 产品代码+描述
	        Request_ShouDianFee_Query.setPowerType(PowerType);// 套餐类型
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
//	            setButtonEnable();
//	            isConfirmChargeCommit=false;
	            if (progressDialog != null && progressDialog.isShowing()) {
	                progressDialog.dismiss();
	            }
	            if (result != 0) {
	            	showMultiQueRenDialog(DianXinDuanXinXiangQingActivity.this);
	            } else {
	                if (mFuwufeijisuanRspMsg.equalsIgnoreCase("")) {
	                    SystemUtil
	                            .displayToast(
	                            		DianXinDuanXinXiangQingActivity.this,
	                                    getString(R.string.shoufeixiangqing_fuwufeijisuanshibai));
	                } else {
	                    SystemUtil.displayToast(DianXinDuanXinXiangQingActivity.this,
	                            mFuwufeijisuanRspMsg);
	                }
	            }
	        }
	        @Override
	        protected void onCancelled() {
	            super.onCancelled();
	        }
	    }
		
	    Handler mShouDianHandler = new Handler() {

	        @Override
	        public void handleMessage(Message msg) {
	        	SureImageVieww.setEnabled(true);
	            switch (msg.what) {
	                case 0:
	                    try {
	                        if (progressDialog != null && progressDialog.isShowing()) {
	                            progressDialog.dismiss();
	                            SystemUtil
	                                    .displayToast(
	                                            DianXinDuanXinXiangQingActivity.this,
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
	                            Toast.makeText(DianXinDuanXinXiangQingActivity.this, mRspMeg,
	                                    Toast.LENGTH_LONG).show();
	                            SystemUtil.setGlobalParamsToNull(DianXinDuanXinXiangQingActivity.this);
	                            DummyContent.ITEM_MAP.clear();
	                            DummyContent.ITEMS.clear();
	                            Intent intent = new Intent(DianXinDuanXinXiangQingActivity.this, LoginActivity.class);
	                            DianXinDuanXinXiangQingActivity.this.startActivity(intent);
	                        } else {
	                            if (!mRspMeg.equalsIgnoreCase("")) {
	                                SystemUtil.displayToast(
	                                		DianXinDuanXinXiangQingActivity.this, mRspMeg);
	                            } else {
	                                SystemUtil
	                                        .displayToast(
	                                        		DianXinDuanXinXiangQingActivity.this,
	                                                getString(R.string.dianxin_failed));
	                            }
//	                            if (bt_querengoudian != null) {
//	                                bt_querengoudian.setEnabled(true);
//	                            }
	                        }
	                    } else {
	                        SystemUtil.displayToast(DianXinDuanXinXiangQingActivity.this,
	                                getString(R.string.dianxin_success));
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
	                                        keepDecimalPlaces(String.valueOf(mAmt)));
	                                BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
	                                        getApplicationContext(),
	                                        JinRiShouDian.class);
	                                baseDao.create(mJinRiShouDian);
	                                Intent it = new Intent(
	                                        GlobalParams.UPDATE_JINRISHOUDIAN_ACTION);
	                                sendBroadcast(it);
	                                // 购电成功
	                                mTicket = mRspTicketXML;
	                                new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	                            } else {
	                                SystemUtil
	                                        .displayToast(
	                                                DianXinDuanXinXiangQingActivity.this,
	                                                getString(R.string.dianxin_failed)
	                                                        + mRspMeg
	                                        );
	                             }
	                                mphoneNotitle.setText(R.string.dianxin_success);
		                            SureImageVieww.setText(R.string.pinzhengbuda_btn_bd);
		                            SureImageVieww.setOnClickListener(new OnClickListener() {
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
	    
	    private void billBuyQuery() {
	        String mRspFee = mFuwufeijisuanRspFee.equalsIgnoreCase("") ? "0"
	                        : mFuwufeijisuanRspFee;
//	        mResultAmt = MathUtil.subtract4Long(mAmt,
//	                mRspFee);
//	        BigDecimal b1=new BigDecimal(mAmt);
//	        BigDecimal b2=new BigDecimal(mRspFee);
//	        mResultAmt=b1.subtract(b2).toString();
	        mIcJsonRes = "";

	        mRspMeg = "";
	        mRspTicketXML = "";
	        mTicket = "";
	        Request_Shoudianshoufei_Query
	                .setContext(DianXinDuanXinXiangQingActivity.this);
	        Request_Shoudianshoufei_Query.setMeterNo(phoneNo);// 表号
	        
//	        String amtParm = String.valueOf(mAmt);
//	        int positionLength = amtParm.length() - amtParm.indexOf(".") - 1;
//	        // Log.e(TAG,"positionLength = "+positionLength);
//	        if (positionLength < 3) {
//	            for (int i = 0; i < (3 - positionLength); i++) {
//	                amtParm += "0";
//	            }
//	        }
	        
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
	    
	   
	    //异步打印
	    private class PrintTask extends AsyncTask<Void, Void, String> {
	    	HashMap a;
	        @Override
	        protected void onPreExecute() {
//	            if (MultiquerenDialog != null
//	                    && MultiquerenDialog.isShowing()) {
//	                MultiquerenDialog.dismiss();
//	                //showShoudianchenggongDialog(ShouDianXiangQingActivity.this);
//	            }
	            //再次打印
//	            if (mPrintAgainBtn != null) {
//	                mPrintAgainBtn.setEnabled(false);
//	            }
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
//            mPasswordView.setVisibility(View.GONE);
	            if (progressDialog != null && progressDialog.isShowing()) {
	                progressDialog.dismiss();
	            }
	     
//                if (bt_querengoudian != null) {
//                    bt_querengoudian.setEnabled(true);
//                }
	            
//	            if (mPrintAgainBtn != null) {
//	                mPrintAgainBtn.setEnabled(true);
//	            }
	        }
	    }
	    
	    
	    
	//购买结果弹框
		 public void showMultiQueRenDialog(Context mContext) {
			 
			  LayoutInflater inflater = LayoutInflater.from(mContext);
		        View view = inflater.inflate(R.layout.dialog_duoshangpin_taocanqueren, null);

		        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		        // builder.setTitle(R.string.shoufeixiangqing_tv_querengoudian);
		        builder.setView(view);

		        MultiquerenDialog = builder.create();
		        MultiquerenDialog.show();	
		        
		        ImageView CloseImageView = (ImageView) view.findViewById(R.id.btnCloseDialog);

		        TextView mshangpingxinxiTV = (TextView) view.findViewById(R.id.shangpingxinxi_Tv);
		        TextView mzhifuTV = (TextView) view.findViewById(R.id.zhifujine_Tv);
		        TextView mchognzhiTV = (TextView) view.findViewById(R.id.chongzhijine_Tv);
		        mphoneNotitle = (TextView) view.findViewById(R.id.phoneNo_title);
		        mPhoneNo = (TextView) view.findViewById(R.id.phoneNo_Tv);
		        mPhoneNo.setText(phoneNo);
		          
		        mshangpingxinxiTV.setText(mDescription);

		        BigDecimal b1=new BigDecimal(mAmt);//购买金额
		        BigDecimal b2=new BigDecimal(mFuwufeijisuanRspFee
		                .equalsIgnoreCase("") ? "0"
		                : mFuwufeijisuanRspFee);
		        
		        BigDecimal b3=new BigDecimal(mZhiFuRspJine);//支付金额
		            
		        mzhifuTV.setText(keepDecimalPlaces(b3.toString()));
		        mchognzhiTV.setText(keepDecimalPlaces(b3.subtract(b2).toString()));
		        
		        CloseImageView.setOnClickListener(new OnClickListener() {
		        	@Override
					public void onClick(View v) {
		        		SureImageVieww.setEnabled(true);
		        		MultiquerenDialog.dismiss();
		        		
					}
		        });
		       
		        SureImageVieww = (Button) view.findViewById(R.id.btn_qrgdd);
		        SureImageVieww.setOnClickListener(new OnClickListener() {
		        	@Override
					public void onClick(View v) {
		        		 billBuyQuery();
		        		 SureImageVieww.setEnabled(false);
//		        		MultiquerenDialog.dismiss();
					}
		        });  
		 }
}