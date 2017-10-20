package com.common.powertech.activity;

import com.common.powertech.R;
import com.common.powertech.bussiness.Request_Token_TranIn;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class YajinChongZhiToken extends Activity {
	private Activity mActivity=this;
	
	private EditText token,pincode;
	private Button query,complete;
	TableLayout table_inf,table_success;//查询信息显示
	TextView transfer_amt,fee,mobile_number,in_amt
	,transfer_amt_suc,fee_suc,commission_suc,balance_suc;
	LinearLayout lin_pin,titleLay;//pin输入
	ScrollView scroll;
	
	
	private String transfer_tokenstr;//服务端返回Token 转账使用
	
	private ProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_token_in);
		token = (EditText) findViewById(R.id.token);
		query = (Button) findViewById(R.id.query);
		table_inf = (TableLayout) findViewById(R.id.table_inf);
		transfer_amt = (TextView) findViewById(R.id.transfer_amt);
		fee = (TextView) findViewById(R.id.fee);
		mobile_number = (TextView) findViewById(R.id.mobile_number);
		in_amt = (TextView) findViewById(R.id.in_amt);
		lin_pin = (LinearLayout) findViewById(R.id.lin_pin);
		titleLay = (LinearLayout) findViewById(R.id.titleLay);
		pincode = (EditText) findViewById(R.id.pincode);
		complete = (Button) findViewById(R.id.complete);
		query.setOnClickListener(QueryTokenInf);
		complete.setOnClickListener(TranTokenIn);
		titleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		scroll = (ScrollView) findViewById(R.id.scroll);
		table_success = (TableLayout) findViewById(R.id.table_success);
		transfer_amt_suc = (TextView) findViewById(R.id.transfer_amt_suc);
		fee_suc = (TextView) findViewById(R.id.fee_suc);
		commission_suc = (TextView) findViewById(R.id.commission_suc);
		balance_suc = (TextView) findViewById(R.id.balance_suc);
	
		table_success.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	android.view.View.OnClickListener TranTokenIn = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 转账充值
			new TranTask().execute();
		}
	};
	
	android.view.View.OnClickListener QueryTokenInf = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// 查询 转账记录信息
			initParam();
			new QryTask().execute();
		}
	};
	
	public void initParam(){
		table_inf.setVisibility(View.GONE);
		lin_pin.setVisibility(View.GONE);
		transfer_amt.setText("");
		fee.setText("");
		mobile_number.setText("");
		in_amt.setText("");
		pincode.setText("");
		transfer_tokenstr = "";
		hideSoftInput();
	}
	
	private class QryTask extends AsyncTask<Void, Void, String>{
		String RspMsg = "";
		String RspCode = "";
		String ErrorMsg = "";
		
		String FEE = "";//此次服务费
		String TRANSFER_AMT = "";//转账金额
		String TRANSFER_TOKEN = "";//转账Token
		String TRANSFER_NO = "";//订单号
		String MOBILE_NUMBER = "";//手机号
		String IN_AMT = "";//入账金额
		String CCY =""; //单位
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			createDialog();
			progressDialog.setTitle(getString(R.string.dialog_check));
			progressDialog.setMessage(getString(R.string.progress_conducting));
			// 设置进度条是否不明确
			progressDialog.setIndeterminate(false);
			// 是否可以按下退回键取消
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String tokenstr = token.getText().toString();
			if(TextUtils.isEmpty(tokenstr)){
				ErrorMsg = getString(R.string.tranin_error1);
				return "1";
			}
			
			Request_Token_TranIn.setContext(mActivity);
			Request_Token_TranIn.setTRANSFER_TOKEN(tokenstr);
			Request_Token_TranIn.setOPER_TYPE("1");
			
			String requestXML = Request_Token_TranIn.getRequsetXML();
			String reponseXML = "";
			 try {
		            reponseXML = Client.ConnectServer("PBalanceTransin", requestXML);
		            // 模拟数据
		            // reponseXML =
		            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
		            System.out.println("查询响应：" + reponseXML);
		        } catch (Exception ex) {
		            System.out.print(ex.toString());
		            return "1";
		        }
		        RspCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
		                "</RSPCOD>");
		        RspMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
		                "</RSPMSG>");
		        if (RspCode.equalsIgnoreCase("00000")) {
		        	CCY = Client.Parse_XML(reponseXML, "<CCY>", "</CCY>");
		        	FEE = Client.Parse_XML(reponseXML, "<FEE>", "</FEE>");
		        	TRANSFER_AMT = Client.Parse_XML(reponseXML, "<TOKEN_AMT>", "</TOKEN_AMT>");
		        	TRANSFER_TOKEN = Client.Parse_XML(reponseXML, "<TRANSFER_TOKEN>", "</TRANSFER_TOKEN>");
		        	transfer_tokenstr = TRANSFER_TOKEN;
		        	TRANSFER_NO = Client.Parse_XML(reponseXML, "<TRANSFER_NO>", "</TRANSFER_NO>");
		        	MOBILE_NUMBER = Client.Parse_XML(reponseXML, "<MOBILE_NUMBER>", "</MOBILE_NUMBER>");
		        	IN_AMT = Client.Parse_XML(reponseXML, "<TRANSFER_AMT>", "</TRANSFER_AMT>");

		            return "0";
		        } else {
		            // 服务器返回系统超时，返回到登录页面
		            if (RspCode.equals("00011")) {
		                return "2";
		            }
		            if (RspMsg.equalsIgnoreCase("")) {
		            	ErrorMsg = getString(R.string.tranin_error2);
						return "1";
	                } else {
	                	ErrorMsg = RspMsg;
	    				return "1";
	                }
		        }
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			hideSoftInput();
			if (result.equals("1")) {
				SystemUtil.displayToast(mActivity, ErrorMsg);
				return;
			}else if(result.equals("2")){
				Toast.makeText(mActivity, RspMsg, Toast.LENGTH_LONG).show();
	            SystemUtil.setGlobalParamsToNull(mActivity);
	            DummyContent.ITEM_MAP.clear();
	            DummyContent.ITEMS.clear();
	            Intent intent = new Intent(mActivity, LoginActivity.class);
	            mActivity.startActivity(intent);
			}else{
				//成功 查询 信息显示
				if(TextUtils.isEmpty(FEE) || TextUtils.isEmpty(TRANSFER_AMT) || TextUtils.isEmpty(TRANSFER_TOKEN) ||
						TextUtils.isEmpty(TRANSFER_NO) || TextUtils.isEmpty(MOBILE_NUMBER)){
					 SystemUtil.displayToast(mActivity, getString(R.string.tranin_error2));
					 return ;
				}else{
					lin_pin.setVisibility(View.VISIBLE);
					table_inf.setVisibility(View.VISIBLE);
					transfer_amt.setText(TRANSFER_AMT+" "+CCY);
					fee.setText(FEE+" "+CCY);
					in_amt.setText(IN_AMT+" "+CCY);
					mobile_number.setText(MOBILE_NUMBER);
				}
				
			}
			
		}
		
	};
	
	private class TranTask extends AsyncTask<Void, Void, String>{
		String RspMsg = "";
		String RspCode = "";
		String ErrorMsg = "";
		
		String FEE = "";//此次服务费
		String RECHARGE_AMT = "";//转账金额
		String CCY = "";//单位
		String COMMISSION = "";//返佣金额
		String CASH_AC_BAL = "";//押金余额
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			createDialog();
			progressDialog.setTitle(getString(R.string.dialog_check));
			progressDialog.setMessage(getString(R.string.progress_conducting));
			// 设置进度条是否不明确
			progressDialog.setIndeterminate(false);
			// 是否可以按下退回键取消
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String pincodestr = pincode.getText().toString();
			if(TextUtils.isEmpty(pincodestr)){
				ErrorMsg = getString(R.string.tranin_error3);
				return "1";
			}
			
			Request_Token_TranIn.setContext(mActivity);
			Request_Token_TranIn.setTRANSFER_TOKEN(transfer_tokenstr);
			Request_Token_TranIn.setPIN(pincodestr);
			Request_Token_TranIn.setOPER_TYPE("2");
			
			String requestXML = Request_Token_TranIn.getRequsetXML();
			String reponseXML = "";
			 try {
		            reponseXML = Client.ConnectServer("PBalanceTransin", requestXML);
		            // 模拟数据
		            // reponseXML =
		            // "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:37:34</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><FEE>181.81</FEE></BODY></ROOT>";
		            System.out.println("查询响应：" + reponseXML);
		        } catch (Exception ex) {
		            System.out.print(ex.toString());
		            return "1";
		        }
		        RspCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
		                "</RSPCOD>");
		        RspMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
		                "</RSPMSG>");
		        if (RspCode.equalsIgnoreCase("00000")) {
		        	FEE = Client.Parse_XML(reponseXML, "<FEE>", "</FEE>");
		        	RECHARGE_AMT = Client.Parse_XML(reponseXML, "<RECHARGE_AMT>", "</RECHARGE_AMT>");
		        	COMMISSION = Client.Parse_XML(reponseXML, "<COMMISSION>", "</COMMISSION>");
		        	CASH_AC_BAL = Client.Parse_XML(reponseXML, "<CASH_AC_BAL>", "</CASH_AC_BAL>");
		        	CCY = Client.Parse_XML(reponseXML, "<CCY>", "</CCY>");

		            return "0";
		        } else {
		            // 服务器返回系统超时，返回到登录页面
		            if (RspCode.equals("00011")) {
		                return "2";
		            }
		            if (RspMsg.equalsIgnoreCase("")) {
		            	ErrorMsg = getString(R.string.tranin_error4);
						return "1";
	                } else {
	                	ErrorMsg = RspMsg;
	    				return "1";
	                }
		        }
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			hideSoftInput();
			if (result.equals("1")) {
				SystemUtil.displayToast(mActivity, ErrorMsg);
				return;
			}else if(result.equals("2")){
				Toast.makeText(mActivity, RspMsg, Toast.LENGTH_LONG).show();
	            SystemUtil.setGlobalParamsToNull(mActivity);
	            DummyContent.ITEM_MAP.clear();
	            DummyContent.ITEMS.clear();
	            Intent intent = new Intent(mActivity, LoginActivity.class);
	            mActivity.startActivity(intent);
			}else{
				//成功 查询 信息显示
				scroll.setVisibility(View.GONE);
				table_success.setVisibility(View.VISIBLE);

				transfer_amt_suc.setText(RECHARGE_AMT+" "+CCY);
				fee_suc.setText(FEE+" "+CCY);
				commission_suc.setText(COMMISSION+" "+CCY);
				balance_suc.setText(CASH_AC_BAL+" "+CCY);
				
			}
			
		}
		
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
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
	private void hideSoftInput(){
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
		try{
			View v = ((Activity) mActivity).getCurrentFocus();
			inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(Exception e){
			Log.i("CustTransfer", e.toString());
		}
	}

}
