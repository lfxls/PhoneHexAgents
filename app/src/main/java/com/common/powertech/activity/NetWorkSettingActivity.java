package com.common.powertech.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.powertech.R;
import com.common.powertech.PowertechApplication;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.PrinterTemp;
import com.common.powertech.dbbean.ServerAddress;
import com.common.powertech.hardwarelayer.ReaderMonitor;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.widget.MyProgressDialog;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.sample.ListViewAdapter;
import com.gprinter.sample.MainActivity;
import com.gprinter.sample.PortConfigurationActivity;
import com.gprinter.sample.PrinterConnectDialog;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomProgressDialog;
import com.zbar.lib.CaptureActivity;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 单位:广东天波信息技术股份有限公司 创建人:luyq 功能：自定义的用户设置工具 日期:2014-1-22
 */
public class NetWorkSettingActivity extends ListActivity {
	private final static String                    DEBUG_TAG            = "NetWorkSettingActivity";
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
	private List<Map<String, Object>> mData;
	private AlertDialog  fuWuQiDiZhiDialog ;
	//private boolean if_btn_click = false;       //防按钮双击
	private  String SCAN_ADDRESS = "";
	private static final String TAG = "NetWorkSettingActivity";
    public static final String CONNECT_STATUS = "connect.status";
	private static final int                       INTENT_PORT_SETTINGS = 0;
	private static final int                       MAX_PRINTER_CNT = 1;
	private              PortParameters            mPortParam[]         = new PortParameters[MAX_PRINTER_CNT];
	private              int                       mPrinterId           = 0;
	private boolean doubleClick=true;
	CustomProgressDialog  progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		setContentView(R.layout.setting_item_back);
		
        GlobalParams.If_CloseFlashLight = true;
        Intent intent = getIntent();
        int flag_1 = intent.getIntExtra("flag_1", 0);
		mData = getData(flag_1);
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.setting_item_back);
		
		  Resources res = getResources();  
	      Drawable drawable = res.getDrawable(R.drawable.background_corner);  
	      this.getWindow().setBackgroundDrawable(drawable);  
	      
		TextView backText = (TextView) findViewById(R.id.backText);
		TextPaint tp = backText.getPaint();
		tp.setFakeBoldText(true); 
		backText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		ImageView backView = (ImageView)findViewById(R.id.backView);
		backView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		conn = new PrinterServiceConnection();
        Intent intent1 = new Intent(this, GpPrintService.class);
        bindService(intent1, conn, Context.BIND_AUTO_CREATE); // bindService
		initPortParam();
		registerBroadcast();
        
	}
	@Override
	protected void onDestroy(){
        super.onDestroy();
		unregisterReceiver(PrinterStatusBroadcastReceiver);
	}
	private void initPortParam() {
//		boolean[] state = {false};//默认当前未连接
		Intent intent = getIntent();		
		boolean[] state = intent
				.getBooleanArrayExtra(MainActivity.CONNECT_STATUS);
		for (int i = 0; i < MAX_PRINTER_CNT; i++) {
			PortParamDataBase database = new PortParamDataBase(this);
			mPortParam[i] = new PortParameters();
			mPortParam[i] = database.queryPortParamDataBase("" + i);
			mPortParam[i].setPortOpenState(state[i]);
		}
	}
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
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GpCom.ACTION_CONNECT_STATUS);
		this.registerReceiver(PrinterStatusBroadcastReceiver, filter);
	}
	private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction())) {
				int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
				int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
				Log.d(DEBUG_TAG, "connect status " + type);
				if (type == GpDevice.STATE_CONNECTING) {
//					setProgressBarIndeterminateVisibility(true);
//					SetLinkButtonEnable(ListViewAdapter.DISABLE);
//					mPortParam[id].setPortOpenState(false);
			        progressDialog = CustomProgressDialog.createProgressDialog(
			        		NetWorkSettingActivity.this, 10000,
			                new CustomProgressDialog.OnTimeOutListener() {

			                    @Override
			                    public void onTimeOut(CustomProgressDialog dialog) {
			                        Toast.makeText(NetWorkSettingActivity.this,
			                                getString(R.string.progress_timeout),
			                                Toast.LENGTH_LONG).show();
			                        if (dialog != null
			                                && (!NetWorkSettingActivity.this.isFinishing())) {
			                            dialog.dismiss();
			                            dialog = null;
			                        }

			                    }
			                }
			        );
			        progressDialog.setTitle(getString(R.string.login_progressdialog_title));
			        progressDialog.setMessage(getString(R.string.connecting));
			        // 设置进度条是否不明确
//			        progressDialog.setIndeterminate(false);
			        // 是否可以按下退回键取消
			        progressDialog.setCancelable(false);
			        progressDialog.show();
				} else if (type == GpDevice.STATE_NONE) {
//					setProgressBarIndeterminateVisibility(false);
//					SetLinkButtonEnable(ListViewAdapter.ENABLE);
//					mPortParam[id].setPortOpenState(false);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    mPortParam[mPrinterId].setPortOpenState(false);
//                    messageBox(getString(R.string.str_open_fail));
				} else if (type == GpDevice.STATE_VALID_PRINTER) {
//					setProgressBarIndeterminateVisibility(false);
//					SetLinkButtonEnable(ListViewAdapter.ENABLE);
//					mPortParam[id].setPortOpenState(true);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
					mPortParam[mPrinterId].setPortOpenState(true);
					messageBox(getString(R.string.str_open_success));
				} else if (type == GpDevice.STATE_INVALID_PRINTER) {
//					setProgressBarIndeterminateVisibility(false);
//					SetLinkButtonEnable(ListViewAdapter.ENABLE);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
					messageBox("Please use Gprinter!");
				}
			}
		}
	};

	private List<Map<String, Object>> getData(int flag) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
        
		//wifi设置
		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.wifi_setting));
		list.add(map);
		
		//以太网设置
		if(GlobalParams.DeviceModel.equals("TPS550")){
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.ethernet_setting));
			list.add(map);
		}
		
		//移动网络设置
//		map = new HashMap<String, Object>();
//		map.put("title", getString(R.string.mobile_setting));
//		list.add(map);
		
		//蓝牙设置
		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.bluetooth));
		list.add(map);
		
		//服务器地址设置
		if(flag == 0){
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.network_setting_server_address));
			list.add(map);
		}
		
		
		//返回
//		map = new HashMap<String, Object>();
//		map.put("title", getString(R.string.networksetting_back));
//		list.add(map);
		return list;
	}

	// ListView 中某项被选中后的逻辑
	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		ViewHolder vHolder = (ViewHolder) view.getTag();
		
		// 处理列表按下面情况
		if(getString(R.string.wifi_setting).equals(vHolder.title.getText())){
			//wifi设置
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		}else if (getString(R.string.ethernet_setting).equals(vHolder.title.getText())) {
			//以太网设置
			startActivity(new Intent("android.net.ethernet.ETHERNET_SETTINGS"));
		}else if (getString(R.string.mobile_setting).equals(vHolder.title.getText())) {
			//移动网络设置
			startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
		}else if (getString(R.string.bluetooth).equals(vHolder.title.getText())) {
			//蓝牙连接
			if(doubleClick){
				openPortDialogueClicked();
			}
		}else if (getString(R.string.network_setting_server_address).equals(vHolder.title.getText())) {
			//服务器地址设置
			showFuWuQiDiZhiDialog(NetWorkSettingActivity.this);
			
		}else if (getString(R.string.networksetting_back).equals(vHolder.title.getText())) {
			//返回
			finish();
		}
	}
	
	public void showFuWuQiDiZhiDialog(Context mContext) {
			
		 if (fuWuQiDiZhiDialog != null && fuWuQiDiZhiDialog.isShowing()) {
			 fuWuQiDiZhiDialog.dismiss();
         }
		    LayoutInflater mInflater = LayoutInflater.from(mContext);
	        View view = mInflater.inflate(R.layout.dialog_fuwuqidizhishezhi, null);

	        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	        builder.setView(view);
	        fuWuQiDiZhiDialog = builder.create();
	        fuWuQiDiZhiDialog.setCancelable(false);
	        final TextView network_address = (TextView) view
	                .findViewById(R.id.network_address);
	        final TextView network_address1 = (TextView) view
	                .findViewById(R.id.network_address1);
	        final TextView network_address2 = (TextView) view
	                .findViewById(R.id.network_address2);
		     
 String  SERVER_ADDRESS="";
	        
	        if( SCAN_ADDRESS!=""){
	        	String[] SERVER_ADDRESSS=SCAN_ADDRESS.split("&"); 
			      for (int i = 0; i < SERVER_ADDRESSS.length; i++) 
			      {
			  		 SERVER_ADDRESS=SERVER_ADDRESSS[i].trim();
			  		if(i==0){
			  			network_address.setText(SERVER_ADDRESS); 
			  		 }
			  		if(i==1){
			  			 network_address1.setText(SERVER_ADDRESS); 
			  		 }
			  		if(i==2){
			  			network_address2.setText(SERVER_ADDRESS); 
			  		 }
			      }
	        }
	        else{
	        	String[] SERVER_ADDRESSS=GlobalParams.SERVER_ADDRESS.split("&"); 
			      for (int i = 0; i < SERVER_ADDRESSS.length; i++) 
			      {
			  		 SERVER_ADDRESS=SERVER_ADDRESSS[i].trim();
			  		if(i==0){
			  			network_address.setText(SERVER_ADDRESS); 
			  		 }
			  		if(i==1){
			  			 network_address1.setText(SERVER_ADDRESS); 
			  		 }
			  		if(i==2){
			  			network_address2.setText(SERVER_ADDRESS); 
			  		 }
			      }
	        }
	                       
		      ImageView CloseImageView = (ImageView) view
	                .findViewById(R.id.btn_cancel);
	        
	        CloseImageView.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		SCAN_ADDRESS="";    //关闭时清空二维码扫描结果
//					SystemUtil.closeAlertDialog(dialog);
	        		fuWuQiDiZhiDialog.dismiss();
				}
	        });
	        
              Button ScanImageView = (Button) view.findViewById(R.id.btn_scan);
	        
	        ScanImageView.setOnClickListener(new OnClickListener() {

	        	@Override
				public void onClick(View v) {					
//	        		if(if_btn_click){
//                    return;
//                }
//                if_btn_click = true;
        		
            	Intent openCameraIntent = new Intent(NetWorkSettingActivity.this, CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
				
            	fuWuQiDiZhiDialog.dismiss();
				} 	
	        });
	      
	        Button SureImageView = (Button) view
	                .findViewById(R.id.btn_ensure);

	                SureImageView.setOnClickListener(new OnClickListener() {
	                	@Override
	                	public void onClick(View v) { 
							if(network_address.getText().toString() != null && network_address.getText().toString().length() > 0){
								GlobalParams.SERVER_ADDRESS = network_address.getText().toString()+"&"+network_address1.getText().toString()+"&"+network_address2.getText().toString();
								BaseDao<ServerAddress, Integer> baseDao = new BaseDao<ServerAddress, Integer>(NetWorkSettingActivity.this, ServerAddress.class);
								if(baseDao.isExists(1)){
									baseDao.excute("UPDATE server_address SET address='"+GlobalParams.SERVER_ADDRESS+"' WHERE id=1");
								}else {
									ServerAddress serverAddress = new ServerAddress(GlobalParams.SERVER_ADDRESS);
									baseDao.create(serverAddress);
								}
								BaseDao<PrinterTemp, Integer> baseDao2 = new BaseDao<PrinterTemp, Integer>(NetWorkSettingActivity.this, PrinterTemp.class);
								if(baseDao2.isExists(1)){
									baseDao2.excute("UPDATE printer_temp SET temp_version='',temp_list='' WHERE id=1");
								}
								SCAN_ADDRESS ="";//保存后清空二维码扫描结果
								//SystemUtil.closeAlertDialog(dialog);
								fuWuQiDiZhiDialog.dismiss();
							}else {
								//SystemUtil.showAlertDialog(dialog);
								Toast.makeText(NetWorkSettingActivity.this, R.string.network_setting_server_address_message_not_null, Toast.LENGTH_LONG).show();
							}
						}

					});     
	                fuWuQiDiZhiDialog.show();
	                
	                Window window = fuWuQiDiZhiDialog.getWindow(); 
	                WindowManager.LayoutParams lp = window.getAttributes();   
	                lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕  
	                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;  
	                window.setAttributes(lp);  
	}

	   public void openPortDialogueClicked() {
	        if (mGpService == null) {
	            Toast.makeText(this, "Print Service is not start, please check it", Toast.LENGTH_SHORT).show();
	            return;
	        }
	        doubleClick=false;
//	        Log.d("NetWorkSettingActivity", "openPortConfigurationDialog ");
//	        Intent intent = new Intent(this, PrinterConnectDialog.class);
//	        boolean[] state = getConnectState();
//	        intent.putExtra(CONNECT_STATUS, state);
//	        this.startActivity(intent);
			Intent intent = new Intent(NetWorkSettingActivity.this,
					PortConfigurationActivity.class);
			startActivityForResult(intent, INTENT_PORT_SETTINGS);
	    }
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			Log.d(DEBUG_TAG, "requestCode" + requestCode + '\n' + "resultCode"
					+ resultCode);
			super.onActivityResult(requestCode, resultCode, data);
/*			if (requestCode == INTENT_PORT_SETTINGS) {
				// getIP settings info from IP settings dialog
				if (resultCode == RESULT_OK) {
					Bundle bundle = new Bundle();
					bundle = data.getExtras();
					Log.d(DEBUG_TAG, "PrinterId " + mPrinterId);
					int param = bundle.getInt(GpPrintService.PORT_TYPE);
					mPortParam[mPrinterId].setPortType(param);
					Log.d(DEBUG_TAG, "PortType " + param);
					String str = bundle.getString(GpPrintService.IP_ADDR);
					mPortParam[mPrinterId].setIpAddr(str);
					Log.d(DEBUG_TAG, "IP addr " + str);
					str = bundle.getString(GpPrintService.BLUETOOT_ADDR);
					mPortParam[mPrinterId].setBluetoothAddr(str);
					Log.d(DEBUG_TAG, "BluetoothAddr " + str);
					PortParamDataBase database = new PortParamDataBase(this);
					String lastAddress = database.queryPortParamDataBase("0").getBluetoothAddr();
					boolean reconn=lastAddress!=str;
					connectOrDisConnectToDevice(mPrinterId,reconn);
				}
			}*/		
			
			//扫描二维码
			if (resultCode == -1) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				Log.e(TAG, scanResult);
				if (scanResult.length() > 0) {
					GlobalParams.CARD_TYPE = "5";// 条形码
					GlobalParams.QR_Info = scanResult;
					List<String> list = new ArrayList<String>();
					list.add(GlobalParams.QR_Info);
					SCAN_ADDRESS=scanResult;
				}
				showFuWuQiDiZhiDialog(NetWorkSettingActivity.this); 
//			    if_btn_click = false;
			  } else{
			
			// getIP settings info from IP settings dialog
			if (requestCode == INTENT_PORT_SETTINGS) {
				if (resultCode == 1) {
					Bundle bundle = new Bundle();
					bundle = data.getExtras();
					Log.d(DEBUG_TAG, "PrinterId " + mPrinterId);
					int param = bundle.getInt(GpPrintService.PORT_TYPE);
					mPortParam[mPrinterId].setPortType(param);
					Log.d(DEBUG_TAG, "PortType " + param);
					String str = bundle.getString(GpPrintService.BLUETOOT_ADDR);
					mPortParam[mPrinterId].setBluetoothAddr(str);
					Log.d(DEBUG_TAG, "BluetoothAddr " + str);
					PortParamDataBase database = new PortParamDataBase(this);
					String lastAddress = database.queryPortParamDataBase(""+mPrinterId).getBluetoothAddr();
					boolean reconn=(!lastAddress.equalsIgnoreCase(str));
					if (CheckPortParamters(mPortParam[mPrinterId])) {
	//					PortParamDataBase database = new PortParamDataBase(this);
						database.deleteDataBase("" + mPrinterId);
						database.insertPortParam(mPrinterId, mPortParam[mPrinterId]);
					} else {
						messageBox(getString(R.string.port_parameters_wrong));
					}
					connectOrDisConnectToDevice(mPrinterId,reconn);
				} else {
					messageBox(getString(R.string.port_parameters_is_not_save));
				}
			}
			doubleClick=true;
		  }
	   }
				void connectOrDisConnectToDevice(int PrinterId,boolean reconn) {
					mPrinterId = PrinterId;
					int rel = 0;
					Log.e(DEBUG_TAG, String.valueOf(mPortParam[PrinterId].getPortOpenState()));
//					if (mPortParam[PrinterId].getPortOpenState() == false || (reconn&&mPortParam[PrinterId].getPortOpenState() == true)) {
//						mPortParam[PrinterId].setPortOpenState(false);
						if (CheckPortParamters(mPortParam[PrinterId])) {
							try {
								mGpService.closePort(mPrinterId);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							switch (mPortParam[PrinterId].getPortType()) {
								case PortParameters.USB:
									try {

										rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(), mPortParam[PrinterId].getUsbDeviceName(), 0);
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
								case PortParameters.ETHERNET:
									try {
										rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(), mPortParam[PrinterId].getIpAddr(), mPortParam[PrinterId].getPortNumber());
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
								case PortParameters.BLUETOOTH:
									try {
										rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(), mPortParam[PrinterId].getBluetoothAddr(), 0);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									break;
							}
							GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
							Log.e(DEBUG_TAG, "result :" + String.valueOf(r));
							if (r != GpCom.ERROR_CODE.SUCCESS) {
								if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
//									mPortParam[PrinterId].setPortOpenState(true);
								} else {
									messageBox(GpCom.getErrorText(r));
								}
							}
						} else {
							messageBox(getString(R.string.port_parameters_wrong));
						}
//					} else {
						/*Log.d(DEBUG_TAG, "DisconnectToDevice ");
						setProgressBarIndeterminateVisibility(true);
						try {
							mGpService.closePort(PrinterId);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
//						messageBox("Device has been connected!");
//					}
				}
				private void messageBox(String err) {
					Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
				}
				Boolean CheckPortParamters(PortParameters param) {
					boolean rel = false;
					int type = param.getPortType();
					if (type == PortParameters.BLUETOOTH) {
						if (!param.getBluetoothAddr().equals("")) {
							rel = true;
						}
					}
					return rel;
				}
	   public boolean[] getConnectState() {
	        boolean[] state = new boolean[MAX_PRINTER_CNT];
	        for (int i = 0; i < MAX_PRINTER_CNT; i++) {
	            state[i] = false;
	        }
	        for (int i = 0; i < MAX_PRINTER_CNT; i++) {
	            try {
	                if (mGpService.getPrinterConnectStatus(i) == MAX_PRINTER_CNT) {
	                    state[i] = true;
	                }
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        return state;
	    }
	public final class ViewHolder {
		public TextView title;
		public TextView title2;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.setting_item, null);
				holder.title = (TextView) convertView.findViewById(R.id.AppLabel);
				holder.title2 = (TextView) convertView.findViewById(R.id.AppLabel2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText((String) mData.get(position).get("title"));

			/*if(holder.title.getText().equals(getString(R.string.networksetting_back))){
				holder.title2.setText("<");
			}else{
				holder.title2.setText(">");
			}*/
			return convertView;
		}

	}	


}