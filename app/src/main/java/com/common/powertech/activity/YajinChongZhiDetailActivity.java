package com.common.powertech.activity;


import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.common.powertech.R;
import com.common.powertech.bussiness.Request_YaJinRecharge;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.wxapi.Constants;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;
import com.myDialog.alipay.PayResult;
import com.myDialog.peach.PeachPayActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class YajinChongZhiDetailActivity extends Activity{
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int PAY_AFT_FLAG = 2;
    private CustomProgressDialog progressDialog;
    
    private String mRequest_YaJinRechargeCode = "";// 服务费计算响应码
    private String mRequest_YaJinRechargeMsg = "";// 服务费计算响应信息
	
    private String payInfo = "";// 完整的符合支付宝参数规范的订单信息
    private String out_trade_no = ""; //支付宝合作商户网站唯一订单号。
    
    private String PREPAY_ID = "";// 微信预支付订单信息
	private String TXAMT = ""; // 充值押金
	private String REMARK = ""; // 备注
    private LinearLayout alipayLay,weChatLay,titleLay,peachLay;
    private Activity mActivity=this;
    
    private IWXAPI api;
    public static final String CHAT_RESP = "HAND_CHAT_RESP";
    PeachPayActivity peachdialog;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_yajin_detail);
		Bundle bundle = getIntent().getExtras();
//		payInfo = (String) bundle.get("payInfo");
//		out_trade_no = (String) bundle.get("out_trade_no");
		TXAMT = (String) bundle.get("TXAMT");
		REMARK = (String) bundle.get("REMARK");
		
		alipayLay = (LinearLayout) findViewById(R.id.alipayLay);
		alipayLay.setOnClickListener(itemClick);
		weChatLay = (LinearLayout) findViewById(R.id.weChatLay);
		weChatLay.setOnClickListener(itemClick);
		titleLay = (LinearLayout) findViewById(R.id.titleLay);
		titleLay.setOnClickListener(itemClick);
		peachLay = (LinearLayout) findViewById(R.id.peachLay);
		peachLay.setOnClickListener(itemClick);
//		mastercardLay = (LinearLayout) findViewById(R.id.mastercardLay);
//		mastercardLay.setOnClickListener(itemClick);
//		jcbLay = (LinearLayout) findViewById(R.id.jcbLay);
//		jcbLay.setOnClickListener(itemClick);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText(" < "+getString(R.string.detail_yajinchongzhi_title));
		
//		api = WXAPIFactory.createWXAPI(this,Constants.APP_ID);
//		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID,false);
		api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(Constants.APP_ID);
		
		mbroadCastReceiver receiver = new mbroadCastReceiver();
		IntentFilter filter = new IntentFilter(CHAT_RESP);
		registerReceiver(receiver, filter);
	}
	
	private class mbroadCastReceiver  extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(CHAT_RESP)){
				String errorCode = intent.getStringExtra("msg");
				switch(Integer.parseInt(errorCode)){
					case 0://微信支付成功
						weChatResp chatTask = new weChatResp();
						chatTask.execute();
						break;
					case -2://用户取消支付
						 CustomDialog.Builder dialogMsg = new CustomDialog.Builder(YajinChongZhiDetailActivity.this);
						 dialogMsg.setTitle(getString(R.string.str_note));
						 dialogMsg.setMessage(R.string.pay_result_error2);
						 dialogMsg.setPositiveButton(R.string.dialog_comfirm,
				  	             new DialogInterface.OnClickListener() {
				  	   	   @Override
				  	   	   public void onClick(DialogInterface dialog, int which) {
				  	   		   	dialog.dismiss();
				  	   		   	finish();
				  	   	   }
				  	   	  });
//						 dialogMsg.setNegativeButton(negativeButtonText, listener)
						 dialogMsg.create().show();
						 
						break;
					default://支付失败 需要请求服务器验证支付结果
						 CustomDialog.Builder dialogMsg2 = new CustomDialog.Builder(YajinChongZhiDetailActivity.this);
						 dialogMsg2.setTitle(getString(R.string.str_note));
						 dialogMsg2.setMessage(R.string.pay_result_error);
						 dialogMsg2.setPositiveButton(R.string.dialog_comfirm,
				  	             new DialogInterface.OnClickListener() {
				  	   	   @Override
				  	   	   public void onClick(DialogInterface dialog, int which) {
				  	   		   	dialog.dismiss();
				  	   		   	finish();
				  	   	   }
				  	   	  });
						 dialogMsg2.create().show();
						break;
				}
				
			}
		}
		
	}
	
	private class weChatResp extends AsyncTask<Void, Void, Void>{
		@Override
    	protected void onPreExecute() {
    		 createDialog();
             progressDialog.setTitle(getString(R.string.progress_shoufei_title));
             progressDialog.setMessage(getString(R.string.progress_conducting)); // 设置进度条是否不明确
             // 是否可以按下退回键取消 progressDialog.setCancelable(false);
             progressDialog.show();
    	};
		
		@Override
		protected Void doInBackground(Void... params) {
			Request_YaJinRecharge.setContext(YajinChongZhiDetailActivity.this);
	        // 充值参数
	        Request_YaJinRecharge.setTXAMT(String.valueOf(TXAMT));
	        Request_YaJinRecharge.setREMARK(REMARK);
	        Request_YaJinRecharge.setFLAG("2");
	        Request_YaJinRecharge.setCHATORD(PREPAY_ID);
	        Request_YaJinRecharge.setCHATORD_STA("1");
	        String requestXML = Request_YaJinRecharge.getRequsetXML();
	        String reponseXML = "";
	        try {
	            reponseXML = Client.ConnectServer("PAgentRecharge", requestXML);
	            progressDialog.dismiss();
	            System.out.println("押金充值响应：" + reponseXML);
	        } catch (Exception ex) {
	            System.out.print(ex.toString());
	        }
	        mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
	                "</RSPCOD>");
	        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
	                "</RSPMSG>");
	        Message msg = new Message();
			msg.what = PAY_AFT_FLAG;
			msg.obj = reponseXML;
			mHandler.sendMessage(msg);
			return null;
		}
		
	};
	
	OnClickListener itemClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.alipayLay){
				new RechargeTask().execute(1);
			}else if(v.getId() == R.id.weChatLay){
				new RechargeTask().execute(2);
			}else if(v.getId() == R.id.titleLay){//标题栏
				finish();
			}else if(v.getId() == R.id.peachLay){
				createPeachDialog();
//				DialogFragment
			}	
//			}else if(v.getId() == R.id.mastercardLay){
//				
//			}else if(v.getId() == R.id.jcbLay){
//				
//			}
			
		}
	};
	 private class RechargeTask extends AsyncTask<Integer, Void, Integer> {
	    	@Override
	    	protected void onPreExecute() {
	    		 createDialog();
	             progressDialog.setTitle(getString(R.string.progress_shoufei_title));
	             progressDialog.setMessage(getString(R.string.progress_conducting)); // 设置进度条是否不明确
	             // 是否可以按下退回键取消 progressDialog.setCancelable(false);
	             progressDialog.show();
	    	};
			@Override
			protected Integer doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				 try {
					 	if(params[0] == 1){
					 		return reCharge() ? 1 : 0;
					 	}else if(params[0] == 2){
					 		return weChatCharge() ? 1 : 0;
					 	}else if(params[0] == 3){
					 		return 0;
					 	}else{
					 		return null;
					 	}
		                
		            } catch (Exception e) {
		                Log.e("ItemListActivity", e.toString());
		                return 0;
		            }
			}
	    	
	    }
	 
	 private boolean reCharge() {
	    	Request_YaJinRecharge.setContext(YajinChongZhiDetailActivity.this);
	        // 充值参数
	        Request_YaJinRecharge.setTXAMT(String.valueOf(TXAMT));
	        Request_YaJinRecharge.setREMARK(REMARK);
	        Request_YaJinRecharge.setPAYTYPE("1");
	        Request_YaJinRecharge.setFLAG("1");
	        Request_YaJinRecharge.setALPORD("");
	        Request_YaJinRecharge.setALPORD_STA("");
	        String requestXML = Request_YaJinRecharge.getRequsetXML();
	        // 模拟数据
	        // requestXML="<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE></TOP><BODY><AMT>2000</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>ef1fc3307410b1e0f7a15ed0e51f9e17</SIGNATURE></TAIL></ROOT>";
	        String reponseXML = "";
	        try {
	            reponseXML = Client.ConnectServer("PAgentRecharge", requestXML);
	            // 模拟数据
	            // reponseXML =
	            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
	            System.out.println("押金充值响应：" + reponseXML);
	        } catch (Exception ex) {
	            System.out.print(ex.toString());
	            return false;
	        }
	        mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
	                "</RSPCOD>");
	        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
	                "</RSPMSG>");
	        if (mRequest_YaJinRechargeCode.equalsIgnoreCase("00000")) {
	        	payInfo = Client.Parse_XML(reponseXML, "<PAYINFO>",
	                     "</PAYINFO>");
	        	payInfo=payInfo.replace("&amp;", "&");
	        	
	        	String[] strlist = payInfo.split("&");
	     		HashMap<String, String> strmap = new HashMap<String, String>();
	     		for(int i=0;i<strlist.length;i++){
	     			strmap.put(strlist[i].split("=")[0], strlist[i].split("=")[1].replace("\"", ""));
	     		}
	     		out_trade_no = strmap.get("out_trade_no");
	     		
	     		progressDialog.dismiss();
	     		
	     		pay();
	            return true;
	        } else {
	        	if(progressDialog.isShowing()){
		        	progressDialog.dismiss();
		        }
	            // 服务器返回系统超时，返回到登录页面
	            if (mRequest_YaJinRechargeCode.equals("00011")) {
	                Toast.makeText(YajinChongZhiDetailActivity.this,
	                        mRequest_YaJinRechargeMsg, Toast.LENGTH_LONG).show();
	                SystemUtil.setGlobalParamsToNull(YajinChongZhiDetailActivity.this);
	                DummyContent.ITEM_MAP.clear();
	                DummyContent.ITEMS.clear();
	                Intent intent = new Intent(YajinChongZhiDetailActivity.this, LoginActivity.class);
	                startActivity(intent);
	            }
	            return false;
	        }
	    }
	 private boolean weChatCharge() {
	    	Request_YaJinRecharge.setContext(YajinChongZhiDetailActivity.this);
	        // 充值参数
	        Request_YaJinRecharge.setTXAMT(String.valueOf(TXAMT));
	        Request_YaJinRecharge.setREMARK(REMARK);
	        Request_YaJinRecharge.setPAYTYPE("2");
	        Request_YaJinRecharge.setFLAG("1");
	        String requestXML = Request_YaJinRecharge.getRequsetXML();
	        // 模拟数据
	        // requestXML="<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE></TOP><BODY><AMT>2000</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>ef1fc3307410b1e0f7a15ed0e51f9e17</SIGNATURE></TAIL></ROOT>";
	        String reponseXML = "";
	        try {
	            reponseXML = Client.ConnectServer("PAgentRecharge", requestXML);
	            // 模拟数据
	            // reponseXML =
	            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
	            System.out.println("押金充值响应：" + reponseXML);
	        } catch (Exception ex) {
	            System.out.print(ex.toString());
	            return false;
	        }
	        mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
	                "</RSPCOD>");
	        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
	                "</RSPMSG>");
	        if (mRequest_YaJinRechargeCode.equalsIgnoreCase("00000")) {
//	        	payInfo = Client.Parse_XML(reponseXML, "<PAYINFO>",
//	                     "</PAYINFO>");
//	        	payInfo=payInfo.replace("&amp;", "&");
//	        	
//	        	String[] strlist = payInfo.split("&");
//	     		HashMap<String, String> strmap = new HashMap<String, String>();
//	     		for(int i=0;i<strlist.length;i++){
//	     			strmap.put(strlist[i].split("=")[0], strlist[i].split("=")[1].replace("\"", ""));
//	     		}
//	     		out_trade_no = strmap.get("out_trade_no");
	        	
	        	String appId = Client.Parse_XML(reponseXML, "<APPID>",
	                     "</APPID>");
	        	String prepayId = Client.Parse_XML(reponseXML, "<PREPAY_ID>",
	                     "</PREPAY_ID>");
	        	PREPAY_ID = prepayId;
	        	String partnerId = Client.Parse_XML(reponseXML, "<PARTNERID>",
	                     "</PARTNERID>");
	        	String nonceStr = Client.Parse_XML(reponseXML, "<NONCESTR>",
	                     "</NONCESTR>");
	        	String timeStamp = Client.Parse_XML(reponseXML, "<TIMESTAMP>",
	                     "</TIMESTAMP>");
	        	String sign = Client.Parse_XML(reponseXML, "<SIGN>",
	                     "</SIGN>");
	        	String packageValue	= Client.Parse_XML(reponseXML, "<PACKAGEVALUE>",
	                     "</PACKAGEVALUE>");
//	        	String extData			= Client.Parse_XML(reponseXML, "<EXTDATA>",
//	                     "</EXTDATA>");
	     		
	     		progressDialog.dismiss();
	     		
	     		try{
	     			PayReq req = new PayReq();
					req.appId			= appId;
					req.partnerId		= partnerId;
					req.prepayId		= prepayId;
					req.nonceStr		= nonceStr;
					req.timeStamp		= timeStamp;
					req.packageValue	= packageValue;
					req.sign			= sign;
//					req.extData			= extData; // optional
					// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//					Log.d(YajinChongZhiDetailActivity.this, "checkArgs=" + req.checkArgs());
					api.sendReq(req);
//					finish();
					
		        }catch(Exception e){
		        	Log.e("PAY_GET", "异常："+e.getMessage());
		        	Toast.makeText(YajinChongZhiDetailActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
		        	return false;
		        }
	            return true;
	        } else {
	        	if(progressDialog.isShowing()){
		        	progressDialog.dismiss();
		        }
	            // 服务器返回系统超时，返回到登录页面
	            if (mRequest_YaJinRechargeCode.equals("00011")) {
	                Toast.makeText(YajinChongZhiDetailActivity.this,
	                        mRequest_YaJinRechargeMsg, Toast.LENGTH_LONG).show();
	                SystemUtil.setGlobalParamsToNull(YajinChongZhiDetailActivity.this);
	                DummyContent.ITEM_MAP.clear();
	                DummyContent.ITEMS.clear();
	                Intent intent = new Intent(YajinChongZhiDetailActivity.this, LoginActivity.class);
	                startActivity(intent);
	            }
	            return false;
	        }
	    }
	
	//支付宝付款
	public void pay() {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(YajinChongZhiDetailActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
//			progressDialog.dismiss();
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				
				createDialog();
	             progressDialog.setTitle(getString(R.string.progress_shoufei_title));
	             progressDialog.setMessage(getString(R.string.progress_conducting)); // 设置进度条是否不明确
	             // 是否可以按下退回键取消 progressDialog.setCancelable(false);
	             progressDialog.show();
	             
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				String resultMessage = "";
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Runnable payRunnable = new Runnable() {
						@Override
						public void run() {
							reChargeForRes();
						}
					};
					// 必须异步调用
					Thread payThread = new Thread(payRunnable);
					payThread.start();
					
					
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						progressDialog.dismiss();
						resultMessage = "支付结果确认中";
						Toast.makeText(YajinChongZhiDetailActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else if(TextUtils.equals(resultStatus, "4000")){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "订单支付失败";
						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					} else if(TextUtils.equals(resultStatus, "5000")){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "重复请求";
//						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					} else if(TextUtils.equals(resultStatus, "6001")){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "用户中途取消";
//						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}else if(TextUtils.equals(resultStatus, "6002")){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "网络连接出错";
//						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}else if(TextUtils.equals(resultStatus, "6004")){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "支付结果未知，请查询支付账单列表中订单的支付状态，若成功请联系客服";
//						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						resultMessage = "订单支付失败";
//						Toast.makeText(YajinChongZhiDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}
					
//					CustomDialog.Builder dialogMsg = new CustomDialog.Builder(YajinChongZhiDetailActivity.this);
//					 dialogMsg.setTitle("提示");
//					 dialogMsg.setMessage(resultMessage);
//					 dialogMsg.setPositiveButton(R.string.dialog_comfirm,
//			  	             new DialogInterface.OnClickListener() {
//			  	   	   @Override
//			  	   	   public void onClick(DialogInterface dialog, int which) {
//			  	   		   	dialog.dismiss();
//			  	   	   }
//			  	   	  });
//					 dialogMsg.create().show();
				}
				break;
			}
			case PAY_AFT_FLAG :{
				String reponseXML = (String) msg.obj;
				mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
		                "</RSPCOD>");
		        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
		                "</RSPMSG>");
				 if (mRequest_YaJinRechargeCode.equalsIgnoreCase("00000")) {
//			        	Toast.makeText(YajinChongZhiDetailActivity.this, mRequest_YaJinRechargeMsg, Toast.LENGTH_SHORT).show();
					 CustomDialog.Builder dialogMsg = new CustomDialog.Builder(YajinChongZhiDetailActivity.this);
					 dialogMsg.setTitle(getString(R.string.str_note));
					 dialogMsg.setMessage(mRequest_YaJinRechargeMsg);
					 dialogMsg.setPositiveButton(R.string.dialog_comfirm,
			  	             new DialogInterface.OnClickListener() {
			  	   	   @Override
			  	   	   public void onClick(DialogInterface dialog, int which) {
			  	   		   	dialog.dismiss();
			  	   		   	finish();
			  	   	   }
			  	   	  });
					 dialogMsg.create().show();
					 
			        } else {
			            // 服务器返回系统超时，返回到登录页面
			            if (mRequest_YaJinRechargeCode.equals("00011")) {
			                Toast.makeText(YajinChongZhiDetailActivity.this,
			                        mRequest_YaJinRechargeMsg, Toast.LENGTH_LONG).show();
			                SystemUtil.setGlobalParamsToNull(YajinChongZhiDetailActivity.this);
			                DummyContent.ITEM_MAP.clear();
			                DummyContent.ITEMS.clear();
			                Intent intent = new Intent(YajinChongZhiDetailActivity.this, LoginActivity.class);
			                YajinChongZhiDetailActivity.this.startActivity(intent);
			            }else{
//			            	Toast.makeText(YajinChongZhiDetailActivity.this, mRequest_YaJinRechargeMsg, Toast.LENGTH_SHORT).show();
			            	 CustomDialog.Builder dialogMsg = new CustomDialog.Builder(YajinChongZhiDetailActivity.this);
							 dialogMsg.setTitle(getString(R.string.str_note));
							 dialogMsg.setMessage(mRequest_YaJinRechargeMsg);
							 dialogMsg.setPositiveButton(R.string.dialog_comfirm,
					  	             new DialogInterface.OnClickListener() {
					  	   	   @Override
					  	   	   public void onClick(DialogInterface dialog, int which) {
					  	   		   	dialog.dismiss();
					  	   		   	finish();
					  	   	   }
					  	   	  });
							 dialogMsg.create().show();
			            }
			        }
			}
			default:
				break;
			}
		};
	};
	
	
	 private void reChargeForRes() { //缴费成功提交信息充值
	    	Request_YaJinRecharge.setContext(YajinChongZhiDetailActivity.this);
	        // 充值参数
	        Request_YaJinRecharge.setTXAMT(String.valueOf(TXAMT));
	        Request_YaJinRecharge.setREMARK(REMARK);
	        Request_YaJinRecharge.setFLAG("2");
	        Request_YaJinRecharge.setALPORD(out_trade_no);
	        Request_YaJinRecharge.setALPORD_STA("0");
	        String requestXML = Request_YaJinRecharge.getRequsetXML();
	        // 模拟数据
	        // requestXML="<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE></TOP><BODY><AMT>2000</AMT></BODY><TAIL><SIGN_TYPE>1</SIGN_TYPE><SIGNATURE>ef1fc3307410b1e0f7a15ed0e51f9e17</SIGNATURE></TAIL></ROOT>";
	        String reponseXML = "";
	        try {
	            reponseXML = Client.ConnectServer("PAgentRecharge", requestXML);
	            progressDialog.dismiss();
	            // 模拟数据
	            // reponseXML =
	            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
	            System.out.println("押金充值响应：" + reponseXML);
	        } catch (Exception ex) {
	            System.out.print(ex.toString());
	        }
	        mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
	                "</RSPCOD>");
	        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
	                "</RSPMSG>");
	        Message msg = new Message();
			msg.what = PAY_AFT_FLAG;
			msg.obj = reponseXML;
			mHandler.sendMessage(msg);
	        
	    }
	
    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(
                YajinChongZhiDetailActivity.this, 35 * 1000,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        SystemUtil.displayToast(YajinChongZhiDetailActivity.this,
                                R.string.progress_timeout);
                        if (dialog != null
                                && (!YajinChongZhiDetailActivity.this.isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }
    
    private void createPeachDialog(){

//    	Intent i = new Intent(this, PeachPayActivity.class);
////
//			startActivityForResult(i,0);		
//	        startActivity(i);  
			peachdialog = new PeachPayActivity();
			Bundle bundle = new Bundle();
			bundle.putString("amount", TXAMT);

			peachdialog.setArguments(bundle);
			
			
			peachdialog.show(getFragmentManager(), "PeachPayActivity");  
			
//		
	
    }
    
    public void dismissPeachDialog(){
    	peachdialog.dismiss();
    }
    
    

}
