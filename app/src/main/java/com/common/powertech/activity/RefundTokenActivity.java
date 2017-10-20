package com.common.powertech.activity;

import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Query_Token;
import com.common.powertech.bussiness.Request_Refund_Token;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RefundTokenActivity extends Activity {
	private EditText refundToken,refundpin;
	private Button qryToken,confirm;
	private Activity mActivity;
	private TextView ordamt,poweramt,fee;
	private LinearLayout layoutinf;
	
	private String PRDORDNO="";
	
	private ProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_refundtoken_detail);
		
		mActivity = this;
		refundToken = (EditText)findViewById(R.id.refundToken);
		qryToken = (Button)findViewById(R.id.qryToken);
		ordamt = (TextView)findViewById(R.id.ordamt);
		poweramt = (TextView)findViewById(R.id.poweramt);
		fee = (TextView)findViewById(R.id.fee);
		refundpin = (EditText)findViewById(R.id.refundpin);
		confirm = (Button)findViewById(R.id.confirm);
		layoutinf = (LinearLayout)findViewById(R.id.layoutinf);
		
		qryToken.setOnClickListener(queryToken);
		confirm.setOnClickListener(refundFun);
	}
	
	//退款token查询并下单
	OnClickListener queryToken = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			layoutinf.setVisibility(View.GONE);
			refundpin.setText("");
			PRDORDNO="";
			
			String token = refundToken.getText().toString();
			if ("".equals(token)) {
				SystemUtil.displayToast(mActivity, R.string.refund_token_error1);
				return;
			}
			Request_Query_Token request_Query_Token = new Request_Query_Token();
			String requestXML = "";
			request_Query_Token.setContext(mActivity);
			request_Query_Token.setREFUND_TOKEN(token);
			requestXML = request_Query_Token.getRequsetXML();
			HttpSend(requestXML,"PRefundTokenOrd",orderHandler);
		}
	};

	OnClickListener refundFun = new OnClickListener() {
		@Override
		public void onClick(View view) {
			final String pin = refundpin.getText().toString();
			if("".equals(pin)){
				SystemUtil.displayToast(mActivity, R.string.refund_token_error2);
				return;
			}
			if("".equals(PRDORDNO)){
				SystemUtil.displayToast(mActivity, R.string.refund_token_error3);
				layoutinf.setVisibility(View.GONE);
				refundpin.setText("");
				PRDORDNO="";
				return;
			}
			AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
			dialog.setMessage(R.string.refund_tips);
			dialog.setNegativeButton(R.string.refund_dialog_calcle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
				}
			});
			dialog.setPositiveButton(R.string.refund_dialog_confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
					//退款
					Request_Refund_Token request_refund_token = new Request_Refund_Token();
					String requestXML = "";
					request_refund_token.setContext(mActivity);
					request_refund_token.setPRDORDNO(PRDORDNO);
					request_refund_token.setREFUND_PIN(pin);
					requestXML = request_refund_token.getRequsetXML();
					HttpSend(requestXML,"PRefundToken",refundHandler);

				}
			});
			dialog.create().show();

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
				System.out.println("下单：" + GlobalParams.RETURN_DATA);
				String mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPCOD>", "</RSPCOD>");
				String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPMSG>", "</RSPMSG>");
				if("00000".equals(mRspCode)){
					PRDORDNO =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<PRDORDNO>", "</PRDORDNO>");
					String ORD_AMT =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<ORD_AMT>", "</ORD_AMT>");//token金额
					String POWER_AMT =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<POWER_AMT>", "</POWER_AMT>");//退款金额
					
					String FEE  =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<FEE>", "</FEE>");//服务费
					
					String CCY =  Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<CCY>", "</CCY>");
					String temp1 = "<font  color=\"#8e8c8c\">"+getString(R.string.refund_token_msg1)+"</font >" + ORD_AMT + " "+CCY;
					String temp2 = "<font  color=\"#8e8c8c\">"+getString(R.string.refund_token_msg2)+"</font >" + POWER_AMT + " " +CCY;
					String temp3 = "<font  color=\"#8e8c8c\">"+getString(R.string.refund_token_msg3)+"</font >" + FEE + " " +CCY;
					ordamt.setText(Html.fromHtml(temp1));
					poweramt.setText(Html.fromHtml(temp2));
					fee.setText(Html.fromHtml(temp3));
					layoutinf.setVisibility(View.VISIBLE);

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
			
		}
		
	};

	Handler refundHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
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
					System.out.println("下单：" + GlobalParams.RETURN_DATA);
					String mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<RSPCOD>", "</RSPCOD>");
					String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
							"<RSPMSG>", "</RSPMSG>");
					if("00000".equals(mRspCode)){
						SystemUtil.displayToast(mActivity,R.string.refund_success);
						Intent intent = new Intent(mActivity,RefundTokenSuccessActivity.class);
						startActivity(intent);
						finish();

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
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	
	
	
	
	
	
}
