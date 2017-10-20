package com.common.powertech.wxapi;






import com.common.powertech.R;
import com.common.powertech.activity.YajinChongZhiDetailActivity;
import com.common.powertech.wxapi.Constants;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
    
    private TextView resultText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        setContentView(R.layout.pay_result);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.pay_result_title);
    	
//        resultText = (TextView) findViewById(R.id.resultText);
//        TextView backText = (TextView) findViewById(R.id.backText);
//		backText.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//			
//		});
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("app_tip");
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
			/*if(resp.errCode == 0){
				resultText.setText(R.string.pay_result_success);
			}else if(resp.errCode == -2){
				resultText.setText(R.string.pay_result_error2);
			}else{
				resultText.setText(R.string.pay_result_error);
			}*/
			
			Intent intent = new Intent(YajinChongZhiDetailActivity.CHAT_RESP);
			intent.putExtra("msg", ""+resp.errCode);
			sendBroadcast(intent);
			finish();
			
		}
	}
}