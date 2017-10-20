package com.common.powertech.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.acs.smartcard.Reader;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;

import java.util.HashMap;

import printUtils.gprinter;

/**
 * Created by HEX144 on 2017/10/9.
 */

public class RefundTokenSuccessActivity extends Activity {
    private TextView prdordno,tokenamt,poweramt,fee,recharge,discount,balance;

    private String TAG = "RefundTokenSuccessActivity";
    private Activity mActivity = this;

    ItemListActivity.ReadCardMessageCallBack mReadCardMessageCallBack;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
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
    private gprinter gprinter = new gprinter();
    private UsbManager mManager;
    private Reader mReader;
    private PendingIntent mPermissionIntent;
    private PrinterServiceConnection conn = null;
    private              int                       mPrinterId           = 0;
    public static final String CONNECT_STATUS = "connect.status";

    private ProgressDialog progressDialog;
    private String mRspTicketXML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_refund_success);

        prdordno = (TextView)findViewById(R.id.prdordno) ;
        tokenamt = (TextView)findViewById(R.id.tokenamt) ;
        poweramt = (TextView)findViewById(R.id.poweramt) ;
        fee = (TextView)findViewById(R.id.fee) ;
        recharge = (TextView)findViewById(R.id.recharge) ;
        discount = (TextView)findViewById(R.id.discount) ;
        balance = (TextView)findViewById(R.id.balance) ;

        String prdordno_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_PRDORDNO>", "</RES_PRDORDNO>");
        String tokenamt_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_TOKENAMT>", "</RES_TOKENAMT>");
        String poweramt_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_REFUND>", "</RES_REFUND>");
        String recharge_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_REFUND>", "</RES_REFUND>");
        String fee_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_FEE>", "</RES_FEE>");
         String discount_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_EARNINGS>", "</RES_EARNINGS>");
        String balance_text = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<RES_BALANCE>", "</RES_BALANCE>");
        String CCY =  Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<CCY>", "</CCY>");
        mRspTicketXML = Client.Parse_XML(GlobalParams.RETURN_DATA,
                "<TICKET>", "</TICKET>");


        prdordno.setText(prdordno_text);
        tokenamt.setText(tokenamt_text + " " + CCY);
        poweramt.setText(poweramt_text + " " + CCY);
        recharge.setText(recharge_text + " " + CCY);
        fee.setText(fee_text + " " + CCY);
        discount.setText(discount_text + " " + CCY);
        balance.setText(balance_text + " " + CCY);

        // Get USB manager
        mManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        // Initialize reader
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

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

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        gprinter.nGpService = null;
        connection();

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
            getPrinterState();
        }
    }

    /**
     * 获取当前蓝牙表连接状态
     */
    public void getPrinterState(){
        //连接打印机先
        boolean[] state = getConnectState();
        if(state[mPrinterId]!=true){
            CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
//	        	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
            builder.setMessage(R.string.str_noopen);
            builder.setTitle(R.string.str_note);
            builder.setPositiveButton(R.string.str_goset,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(mActivity, NetWorkSettingActivity.class);
                            boolean[] state = getConnectState();
                            intent.putExtra(CONNECT_STATUS, state);
                            intent.putExtra("flag_1", 1);
                            mActivity.startActivity(intent);
                        }
                    });
            builder.setNegativeButton(R.string.str_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();

        }
    }
    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (gprinter.nGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    public void close(View v){
        finish();
    }

    public void print(View v){
        new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }
    private class PrintTask extends AsyncTask<Void, Void, String> {
        HashMap a;
        @Override
        protected void onPreExecute() {
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
	/*            mPrinter.start();
	            mPrinter.printXML("<TICKET>" + mRspTicketXML + "</TICKET>");
	            // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
	            // -9999 其他错误
	            int printResult = mPrinter.commitOperation();
	            mPrinter.stop();*/
//	        	printReceiptClicked();
            mRspTicketXML = mRspTicketXML.replace("&amp;caret;","^");
//	            mRspTicketXML = mRspTicketXML.replace("&quot;", "@quot;").replace("&apos;", "@apos;").replace("&lt;", "@lt;").replace("&gt;", "@gt;");
//	            mRspTicketXML = mRspTicketXML.replace("&","&amp;");
//	            mRspTicketXML = mRspTicketXML.replace("@quot;", "&quot;").replace("@apos;", "&apos;").replace("@lt;", "&lt;").replace("@gt;", "&gt;");
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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
