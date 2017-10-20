package com.common.powertech.activity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alipay.sdk.app.PayTask;
import com.common.powertech.ItemListActivity;
import com.common.powertech.PowertechApplication;
import com.common.powertech.R;
import com.common.powertech.bussiness.Request_YaJinRecharge;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;
import com.myDialog.alipay.PayDemoActivity;
import com.myDialog.alipay.PayResult;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentYaJinChongZhiMain extends Fragment{
	private View view ;
	private EditText insecash,remark;
	private Button recharge ;
	private String TXAMT = ""; // 充值押金
	private String REMARK = ""; // 备注
	private String TAG = "FragmentYaJinChongZhiMain";
	
	private ItemListActivity mActivity;
    private CustomProgressDialog progressDialog;
    private String mRequest_YaJinRechargeCode = "";// 服务费计算响应码
    private String mRequest_YaJinRechargeMsg = "";// 服务费计算响应信息
    private String PAYINFO = "";// 完整的符合支付宝参数规范的订单信息
    private String out_trade_no = ""; //支付宝合作商户网站唯一订单号。
    private PowertechApplication app;
	private static final int SDK_PAY_FLAG = 1;
	private static final int PAY_AFT_FLAG = 2;
    
	
	/*private static boolean isfragmentRunning = false;
	public static final String ARG_ITEM_ID = "item_id";
	private DummyContent.DummyItem mItem;
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
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		Log.e(TAG, "onCreateView");
		((ItemListActivity)getActivity()).setShortCutsKeyDownCallBack(null);
        ItemListActivity.isEnterTrigger = false;
        mActivity = (ItemListActivity) getActivity();
		view = inflater.inflate(R.layout.fragment_yajinchongzhi_main, container, false);
		insecash = (EditText)view.findViewById(R.id.insecash);
		insecash.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		InputFilter[] filters = {new CashierInpustFilter()};
		insecash.setFilters(filters);
		remark = (EditText) view.findViewById(R.id.remark);
		remark.addTextChangedListener(remarkWatcher);
		
		recharge = (Button) view.findViewById(R.id.recharge);
		recharge.setOnClickListener(Recharge);
		
		TextView goTrans = (TextView) view.findViewById(R.id.goTrans);
		goTrans.setOnClickListener(GoTransfer);
		
		app = (PowertechApplication) getActivity().getApplication();
		return view;
		
	}
	OnClickListener Recharge = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String cashvalue = insecash.getText().toString();
			
			if (insecash.getText().length() == 0) {
                SystemUtil.displayToast(mActivity,
                        R.string.shoudianxiangqing_tv_je);
                setButtonEnable();
                return;
            }

            String money = insecash.getText().toString();
            if (money.length() == 0) {
                SystemUtil.displayToast(mActivity,
                        R.string.shoudianxiangqing_tv_je);
                setButtonEnable();
                return;
            }

            // 检测金额格式是否正确，如果以0开头,后面必须加.
            String moneyStr = money;
            Pattern pattern = Pattern.compile("^(-)?[0-9]*.?[0-9]*");  
            Matcher matcher = pattern.matcher(moneyStr);
            if (!matcher.matches()) {
                // 金额格式不正确
                SystemUtil.displayToast(mActivity,
                        R.string.shoudianxiangqing_jineshurubuzhengque);
                setButtonEnable();
                return;
            }
            //检查金额大小
            BigDecimal cash=new BigDecimal(moneyStr);
            BigDecimal mincash=new BigDecimal(app.getMINRECHARGE());
            if(cash.compareTo(mincash) < 0){
            	// 低于最低充值金额
                SystemUtil.displayToast(mActivity,getString(R.string.main_yajinchongzhi_input_error) + mincash);
                setButtonEnable();
                return;
            }
            TXAMT = insecash.getText().toString();
            REMARK = remark.getText().toString();
            insecash.setText("");
            remark.setText("");
            Intent intent = new Intent(mActivity,YajinChongZhiDetailActivity.class);
            Bundle bundle = new Bundle();
//            bundle.putString("payInfo", PAYINFO);
//            bundle.putString("out_trade_no", out_trade_no);
            bundle.putString("TXAMT", TXAMT);
            bundle.putString("REMARK", REMARK);
            intent.putExtras(bundle);
            startActivity(intent);
            
            
//            new RechargeTask().execute();
		}
	};
	
	OnClickListener GoTransfer = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//token转账
			Intent intent = new Intent(mActivity, YajinChongZhiToken.class);
			mActivity.startActivity(intent);
		}
		
	};
	
	TextWatcher remarkWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			String str=remark.getText().toString();
			if(str.contains("<")||
					str.contains(">")||
					str.contains("%")||
					str.contains("&")){
				str=str.replace("<", "").replace(">", "").replace("%", "").replace("&", "");
			}
			
			String str2 = "";
			  int len = str.length();
		        for (int i = 0; i < len; i++) {
		            char codePoint = str.charAt(i);
		            if (isnoEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
		            	str2=str2+codePoint;
		            }
		        }
			
			
			if(!str2.equals(s.toString())){
				remark.setText(str2);
				remark.setSelection(str2.length());
			}
		}
	 /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private boolean isnoEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
    private class RechargeTask extends AsyncTask<Void, Void, Integer> {
    	@Override
    	protected void onPreExecute() {
    		 createDialog();
             progressDialog.setTitle(getString(R.string.progress_shoufei_title));
             progressDialog.setMessage(getString(R.string.progress_conducting)); // 设置进度条是否不明确
             // 是否可以按下退回键取消 progressDialog.setCancelable(false);
             progressDialog.show();
    	};
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			 try {
	                return reCharge() ? 1 : 0;
	            } catch (Exception e) {
	                Log.e("ItemListActivity", e.toString());
	                return 0;
	            }
		}
    	
    }
    private void setButtonEnable() {
    	insecash.setEnabled(true);
    }
    
    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(
                mActivity, 35 * 1000,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        SystemUtil.displayToast(mActivity,
                                R.string.progress_timeout);
                        if (dialog != null
                                && (!mActivity.isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }
	
    private boolean reCharge() {
    	Request_YaJinRecharge.setContext(mActivity);
        // 充值参数
        Request_YaJinRecharge.setTXAMT(String.valueOf(TXAMT));
        Request_YaJinRecharge.setREMARK(REMARK);
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
        	 PAYINFO = Client.Parse_XML(reponseXML, "<PAYINFO>",
                     "</PAYINFO>");
        	 PAYINFO=PAYINFO.replace("&amp;", "&");
        	
        	String[] strlist = PAYINFO.split("&");
     		HashMap<String, String> strmap = new HashMap<String, String>();
     		for(int i=0;i<strlist.length;i++){
     			strmap.put(strlist[i].split("=")[0], strlist[i].split("=")[1].replace("\"", ""));
     		}
     		out_trade_no = strmap.get("out_trade_no");
//        	 pay();
     		
     		progressDialog.dismiss();
     		
     		Intent intent = new Intent(mActivity,YajinChongZhiDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("payInfo", PAYINFO);
            bundle.putString("out_trade_no", out_trade_no);
            bundle.putString("TXAMT", TXAMT);
            bundle.putString("REMARK", REMARK);
            intent.putExtras(bundle);
            startActivity(intent);
            
            return true;
        } else {
            // 服务器返回系统超时，返回到登录页面
            if (mRequest_YaJinRechargeCode.equals("00011")) {
                Toast.makeText(mActivity,
                        mRequest_YaJinRechargeMsg, Toast.LENGTH_LONG).show();
                SystemUtil.setGlobalParamsToNull(mActivity);
                DummyContent.ITEM_MAP.clear();
                DummyContent.ITEMS.clear();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
            }
            return false;
        }
    }
    
	public void pay() {

		final String payInfo = PAYINFO;
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mActivity);
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
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
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
						Toast.makeText(mActivity, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						progressDialog.dismiss();
						Toast.makeText(mActivity, "支付失败", Toast.LENGTH_SHORT).show();
					} 
				}
				break;
			}
			case PAY_AFT_FLAG :{
				String reponseXML = (String) msg.obj;
				mRequest_YaJinRechargeCode = Client.Parse_XML(reponseXML, "<RSPCOD>",
		                "</RSPCOD>");
		        mRequest_YaJinRechargeMsg = Client.Parse_XML(reponseXML, "<RSPMSG>",
		                "</RSPMSG>");
		        insecash.setText("");
		        remark.setText("");
				 if (mRequest_YaJinRechargeCode.equalsIgnoreCase("00000")) {
//			        	Toast.makeText(mActivity, mRequest_YaJinRechargeMsg, Toast.LENGTH_SHORT).show();
					 CustomDialog.Builder dialogMsg = new CustomDialog.Builder(mActivity);
					 dialogMsg.setTitle("提示");
					 dialogMsg.setMessage("支付成功");
					 dialogMsg.setPositiveButton(R.string.str_goset,
			  	             new DialogInterface.OnClickListener() {
			  	   	   @Override
			  	   	   public void onClick(DialogInterface dialog, int which) {
			  	   		   	dialog.dismiss();
			  	   	   }
			  	   	  });
					 dialogMsg.create().show();
					 
			        } else {
			            // 服务器返回系统超时，返回到登录页面
			            if (mRequest_YaJinRechargeCode.equals("00011")) {
			                Toast.makeText(mActivity,
			                        mRequest_YaJinRechargeMsg, Toast.LENGTH_LONG).show();
			                SystemUtil.setGlobalParamsToNull(mActivity);
			                DummyContent.ITEM_MAP.clear();
			                DummyContent.ITEMS.clear();
			                Intent intent = new Intent(mActivity, LoginActivity.class);
			                mActivity.startActivity(intent);
			            }else{
			            	Toast.makeText(mActivity, mRequest_YaJinRechargeMsg, Toast.LENGTH_SHORT).show();
			            }
			        }
				 insecash.requestFocus();//获取焦点
			}
			default:
				break;
			}
		};
	};
	
	 private void reChargeForRes() { //缴费成功提交信息充值
	    	Request_YaJinRecharge.setContext(mActivity);
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
	
	
    @Override
    public void onPause(){
    	Log.i("FragmentYaJinChongZhiMain", "onPause");
    	InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
		if ((imm.isActive(insecash) || imm.isActive(remark))) {//如果开启
//    	    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    	super.onPause();
    }
}
