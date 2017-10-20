package com.gprinter.impl;

import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.common.powertech.R;
import com.common.powertech.activity.NetWorkSettingActivity;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import printUtils.gprinter;

public class PrinterUtils {

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
		private UsbManager mManager;
		private Reader mReader;
	    private PendingIntent mPermissionIntent;
	    private gprinter gprinter = new gprinter();
	    private PrinterServiceConnection conn = null;
		private              int                       mPrinterId           = 0;
		public static final String CONNECT_STATUS = "connect.status";
		private GpService mGpService = null;
	    
	private Context mActivity;
	
	public PrinterUtils(Activity activity){
		mActivity = activity;
		initPrinter();
	}
	public void initPrinter(){
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
        mGpService = null;
        connection();
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
//	  	   	  AlertDialog x = builder.create();
//	  	   	  x.show();
	  	   
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
	                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
	                    state[i] = true;
	                }
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        return state;
	    }
	
	
	 private void connection() {
	        conn = new PrinterServiceConnection();
	        Intent intent = new Intent(mActivity.getApplicationContext(), GpPrintService.class);
	        mActivity.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	    }
    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService= GpService.Stub.asInterface(service);
        }
    }
}
