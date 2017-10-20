package com.common.powertech.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.*;
import com.common.powertech.ItemListActivity;
import com.common.powertech.bussiness.*;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomProgressDialog;

import hdx.HdxUtil;

import com.common.powertech.R;
import com.common.powertech.activity.LoginActivity.PrinterServiceConnection;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.LiuLiangData;
import com.common.powertech.dbbean.SystemParam;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;

public class FragmentXiTongSheZhiMain extends Fragment {

	private Spinner spinner_xianshimoshi, spinner_yuyinshezhi,
			spinner_yuyanshezhi, spinner_suopingshijian;
	private TextView tv_app_version, tv_wlan_data;
	private Button bt_wifi_setting;
	private TelephonyManager telephonyManager;
    private CustomProgressDialog progressDialog;
    private String QUERY_MESSAGE_SUCC = "1";
    private String QUERY_MESSAGE_FAIL = "other";
    private String mRspCode = "";
    private String mRspMeg = "";
    private int temp;
    private String tmp;
    private StringBuilder stringBuilder;
    private GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private PrinterServiceConnection conn = null;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		((ItemListActivity)getActivity()).setShortCutsKeyDownCallBack(null);
        ItemListActivity.isEnterTrigger = false;
		View view = inflater.inflate(R.layout.fragment_xitongshezhi_main, container, false);

		telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);		
		spinner_xianshimoshi = (Spinner) view.findViewById(R.id.spinner_xianshimoshi);
		spinner_yuyinshezhi = (Spinner) view.findViewById(R.id.spinner_yuyinshezhi);
		spinner_yuyanshezhi = (Spinner) view.findViewById(R.id.spinner_yuyanshezhi);
		spinner_suopingshijian = (Spinner) view.findViewById(R.id.spinner_suopingshijian);
		bt_wifi_setting = (Button) view.findViewById(R.id.btn_wlansetting);
		bt_wifi_setting.setOnClickListener(new OnClickListenerWifiSetting());
		tv_app_version = (TextView) view.findViewById(R.id.tv_app_version);
		tv_app_version.setText("V" + SystemUtil.getAppVersionName(getActivity()));
		tv_wlan_data = (TextView) view.findViewById(R.id.tv_wlan_data);
		
		//主题
		if (GlobalParams.Theme == 1) {
			spinner_xianshimoshi.setSelection(0, true);
		} else if (GlobalParams.Theme == 2) {
			spinner_xianshimoshi.setSelection(1, true);
		}
		spinner_xianshimoshi.setOnItemSelectedListener(new OnItemSelectedListenerXianShiMoShi());
		
		//声音
//		if ("1".equals(GlobalParams.VOICE)) {
			spinner_yuyinshezhi.setSelection(0, true);
//		} else if ("2".equals(GlobalParams.VOICE)) {
//			spinner_yuyinshezhi.setSelection(1, true);
//		}
//		spinner_yuyinshezhi.setOnItemSelectedListener(new OnItemSelectedListenerYuYinSheZhi());
		
		//语言
		if ("zh".equals(GlobalParams.LANGUAGE)) {
			spinner_yuyanshezhi.setSelection(0, true);
		} else if ("en".equals(GlobalParams.LANGUAGE)) {
			spinner_yuyanshezhi.setSelection(1, true);
		} else if ("fr".equals(GlobalParams.LANGUAGE)) {
			spinner_yuyanshezhi.setSelection(2, true);
		}
		spinner_yuyanshezhi.setOnItemSelectedListener(new OnItemSelectedListenerYuYanSheZhi());
		
		//锁屏时间
		if ("0".equals(GlobalParams.LOCKTIME)) {
			spinner_suopingshijian.setSelection(0, true);
		} else if ("1".equals(GlobalParams.LOCKTIME)) {
			spinner_suopingshijian.setSelection(1, true);
		} else if ("2".equals(GlobalParams.LOCKTIME)) {
			spinner_suopingshijian.setSelection(2, true);
		} else if ("3".equals(GlobalParams.LOCKTIME)) {
			spinner_suopingshijian.setSelection(3, true);
		} else if ("4".equals(GlobalParams.LOCKTIME)) {
			spinner_suopingshijian.setSelection(4, true);
		}
		spinner_suopingshijian.setOnItemSelectedListener(new OnItemSelectedListenerSuoPingShiJian());
        connection();
		return view;
	}
	  private void connection() {
	        conn = new PrinterServiceConnection();
	        Intent intent = new Intent(getActivity().getApplicationContext(), GpPrintService.class);
	        getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	    }

    @Override
	public void onResume() {
		super.onResume();
		setTextViewLiuLiangTongJi();
	}
    
    @Override
	public void onPause() {
		super.onPause();
	}

	public void onDestroy(){
        super.onDestroy();
        ItemListActivity.isEnterTrigger = true;
    }

	private class OnItemSelectedListenerXianShiMoShi implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               temp = 1;
               if (position == 0) {
                   temp = 1;
               } else if (position == 1) {
                   temp = 2;
               }
               new UploadSettingTask(String.valueOf(temp), 0).execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

	private class OnItemSelectedListenerYuYinSheZhi implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			tmp = "1";
			if (position == 0) {
                tmp = "1";
			} else if (position == 1) {
                tmp = "2";
			}
            new UploadSettingTask(tmp,1).execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private class OnItemSelectedListenerYuYanSheZhi implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tmp ="zh";
			if (position == 0) {
                tmp = "zh";
			} else if (position == 1) {
                tmp = "en";
			} else if (position == 2) {
                tmp = "fr";
			}
            new UploadSettingTask(tmp,2).execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private class OnItemSelectedListenerSuoPingShiJian implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            temp = position;
            tmp = "0";
			tmp = String.valueOf(position);
            new UploadSettingTask(tmp,3).execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private class OnClickListenerWifiSetting implements OnClickListener {

		@Override
		public void onClick(View v) {
	        Intent intent = new Intent(getActivity(), NetWorkSettingActivity.class);
	        boolean[] state = getConnectState();
	        intent.putExtra(CONNECT_STATUS, state);
	        intent.putExtra("flag_1", 1);
	        getActivity().startActivity(intent);
//			startActivity(new Intent(getActivity(),NetWorkSettingActivity.class).putExtra("flag_1", 1));
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

	// 保存界面参数
	private void saveParam() {
    	BaseDao<SystemParam, Integer> baseDao = new BaseDao<SystemParam, Integer>(getActivity(), SystemParam.class);
    	baseDao.excute("UPDATE system_param SET theme="+GlobalParams.Theme+",language='"+GlobalParams.LANGUAGE+"',voice='"+GlobalParams.VOICE+"',locktime='"+GlobalParams.LOCKTIME+"' WHERE id=1");		
	}		

    // 收费查询异步任务
    private void createDialog() {
        progressDialog = CustomProgressDialog.createProgressDialog(getActivity(),
                GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        SystemUtil.displayToast(getActivity(),
                                R.string.progress_timeout);
                        if (dialog != null && (!getActivity().isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }

    private class UploadSettingTask extends AsyncTask<Void, Void, String> {

        String param;
        int index;
        //api    0 -- 改变显示模式   1-- 改变语言设置     2 -- 改变语音设置   3 -- 改变锁屏时间
        public UploadSettingTask(String str, int api) {

            param = str;
            index = api;

            createDialog();
            progressDialog
                    .setTitle(getString(R.string.progress_upload));
            progressDialog
                    .setMessage(getString(R.string.progress_conducting));
            // 设置进度条是否不明确
//            progressDialog.setIndeterminate(false);

            // 是否可以按下退回键取消
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String reponseXML;
            try {
                mRspCode = "";
                mRspMeg="";
                String APIName = "PSettingUpload";
                Request_Upload_Setting.setContext(getActivity());

                switch (index){
                    case 0:
                        Request_Upload_Setting.setDisplay(param);
                        Request_Upload_Setting.setVoice(GlobalParams.VOICE);
                        if(GlobalParams.LANGUAGE.equals("en")){
                            Request_Upload_Setting.setLanguage("2");
                        }else if(GlobalParams.LANGUAGE.equals("fr")){
                            Request_Upload_Setting.setLanguage("3");
                        }else{
                            Request_Upload_Setting.setLanguage("1");
                        }
                        Request_Upload_Setting.setLocktime(GlobalParams.LOCKTIME);
                        break;

                    case 1:
                        Request_Upload_Setting.setDisplay(String.valueOf(GlobalParams.Theme));
                        Request_Upload_Setting.setVoice(param);
                        if(GlobalParams.LANGUAGE.equals("en")){
                            Request_Upload_Setting.setLanguage("2");
                        }else if(GlobalParams.LANGUAGE.equals("fr")){
                            Request_Upload_Setting.setLanguage("3");
                        }else{
                            Request_Upload_Setting.setLanguage("1");
                        }
                        Request_Upload_Setting.setLocktime(GlobalParams.LOCKTIME);
                        break;

                    case 2:
                        Request_Upload_Setting.setDisplay(String.valueOf(GlobalParams.Theme));
                        if(param.equals("en")){
                            Request_Upload_Setting.setLanguage("2");
                        }else if(param.equals("fr")){
                            Request_Upload_Setting.setLanguage("3");
                        }else{
                            Request_Upload_Setting.setLanguage("1");
                        }
                        Request_Upload_Setting.setVoice(GlobalParams.VOICE);
                        Request_Upload_Setting.setLocktime(GlobalParams.LOCKTIME);
                        break;

                    case 3:
                        Request_Upload_Setting.setDisplay(String.valueOf(GlobalParams.Theme));
                        Request_Upload_Setting.setVoice(GlobalParams.VOICE);
                        if(GlobalParams.LANGUAGE.equals("en")){
                            Request_Upload_Setting.setLanguage("2");
                        }else if(GlobalParams.LANGUAGE.equals("fr")){
                            Request_Upload_Setting.setLanguage("3");
                        }else{
                            Request_Upload_Setting.setLanguage("1");
                        }
                        Request_Upload_Setting.setLocktime(param);
                        break;
                }
                String date = Request_Upload_Setting.getRequsetXML();
                System.out.println("上传设置请求：" + date);
                reponseXML = Client.ConnectServer(APIName, date);
                System.out.println("上传设置响应：" + reponseXML);
                mRspCode = Client.Parse_XML(reponseXML, "<RSPCOD>", "</RSPCOD>");
                mRspMeg = Client.Parse_XML(reponseXML, "<RSPMSG>", "</RSPMSG>");

            } catch (Exception e) {
                e.printStackTrace();
                return QUERY_MESSAGE_FAIL;
            }
            return QUERY_MESSAGE_SUCC;
        }

        @Override
        protected void onPostExecute(String result) {

            if (progressDialog != null && (!getActivity().isFinishing())) {
                progressDialog.dismiss();
                progressDialog = null;
            }


            Message msg = handler.obtainMessage(index);
            handler.sendMessage(msg);
//            if (result.equalsIgnoreCase(QUERY_MESSAGE_FAIL)) {
//
//                Toast.makeText(getActivity(), getString(R.string.main_uploadfailed),Toast.LENGTH_LONG).show();
//
//            } else if (result.equalsIgnoreCase(QUERY_MESSAGE_SUCC)) {
//
//            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    Handler handler = new Handler(){

        public void handleMessage(Message msg){
            //处理消息
            switch (msg.what){

                case 0x00:
                    if(mRspCode.equals("00000")){
                        GlobalParams.Theme = temp;
                        saveParam();
                        GlobalParams.IsSystemSettingTrigger = true;
                        getActivity().recreate();
                    }else{
                    	//服务器返回系统超时，返回到登录页面
                    	if(mRspCode.equals("00011")){
                    		Toast.makeText(getActivity(), mRspMeg, Toast.LENGTH_LONG).show();
                    		SystemUtil.setGlobalParamsToNull(getActivity());
                    	    DummyContent.ITEM_MAP.clear();
                    	    DummyContent.ITEMS.clear();
                    		Intent intent = new Intent(getActivity(), LoginActivity.class);
                    		getActivity().startActivity(intent);
                    		return;
                    	}
                        //上传参数不成功 设置不变更
                        setXtszFragment();
                        Toast.makeText(getActivity(), getString(R.string.main_uploadfailed),Toast.LENGTH_LONG).show();
                    }
                    break;

                case 0x01:
                	setTextViewLiuLiangTongJi();
                    if(mRspCode.equals("00000")){
                        GlobalParams.VOICE = tmp;
                        saveParam();
                    }else{
                    	//服务器返回系统超时，返回到登录页面
                    	if(mRspCode.equals("00011")){
                    		Toast.makeText(getActivity(), mRspMeg, Toast.LENGTH_LONG).show();
                    		SystemUtil.setGlobalParamsToNull(getActivity());
                    	      DummyContent.ITEM_MAP.clear();
                    	        DummyContent.ITEMS.clear();
                    		Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                    		return;
                    	}
                        setXtszFragment();
                        Toast.makeText(getActivity(), getString(R.string.main_uploadfailed),Toast.LENGTH_LONG).show();
                    }
                    break;

                case 0x02:
                    if(mRspCode.equals("00000")){
                        GlobalParams.LANGUAGE = tmp;
                        saveParam();
                        SystemUtil.setAppLanguageChange(getActivity());
                        GlobalParams.IsSystemSettingTrigger = true;
                        getActivity().recreate();
                    }else{
                    	//服务器返回系统超时，返回到登录页面
                    	if(mRspCode.equals("00011")){
                    		Toast.makeText(getActivity(), mRspMeg, Toast.LENGTH_LONG).show();
                    		SystemUtil.setGlobalParamsToNull(getActivity());
                    	      DummyContent.ITEM_MAP.clear();
                    	        DummyContent.ITEMS.clear();
                    		Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                    		return;
                    	}
                        setXtszFragment();
                        Toast.makeText(getActivity(), getString(R.string.main_uploadfailed),Toast.LENGTH_LONG).show();
                    }
                    break;

                case 0x03:
                	setTextViewLiuLiangTongJi();
                    if(mRspCode.equals("00000")){
                        GlobalParams.LOCKTIME = tmp;
                        if (temp == 0) {
                            SystemUtil.setSystemSleepTime(getActivity()
                                    .getContentResolver(), 2138400000);
                        } else if (temp == 1) {
                            SystemUtil.setSystemSleepTime(getActivity()
                                    .getContentResolver(), 600000);
                        } else if (temp == 2) {
                            SystemUtil.setSystemSleepTime(getActivity()
                                    .getContentResolver(), 1800000);
                        } else if (temp == 3) {
                            SystemUtil.setSystemSleepTime(getActivity()
                                    .getContentResolver(), 14400000);
                        } else if (temp == 4) {
                            SystemUtil.setSystemSleepTime(getActivity()
                                    .getContentResolver(), 28800000);
                        }
                        saveParam();
                    }else{
                    	//服务器返回系统超时，返回到登录页面
                    	if(mRspCode.equals("00011")){
                    		Toast.makeText(getActivity(), mRspMeg, Toast.LENGTH_LONG).show();
                    		SystemUtil.setGlobalParamsToNull(getActivity());
                    	      DummyContent.ITEM_MAP.clear();
                    	        DummyContent.ITEMS.clear();
                    		Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                    		return;
                    	}
                        setXtszFragment();
                        Toast.makeText(getActivity(), getString(R.string.main_uploadfailed),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    // 设置显示为为系统设置界面
    public void setXtszFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        FragmentXiTongSheZhiMain xiTongSheZhiMainFragment = new FragmentXiTongSheZhiMain();
        transaction.replace(R.id.item_detail_container,
                xiTongSheZhiMainFragment);
        transaction.commit();
    }
    
    private void setTextViewLiuLiangTongJi(){
    	if(stringBuilder != null){
    		stringBuilder = null;
    	}
		stringBuilder = new StringBuilder();
		BaseDao<LiuLiangData, Integer> baseDao = new BaseDao<LiuLiangData, Integer>(
				getActivity(), LiuLiangData.class);
		long allTraffic = (baseDao
				.queryRawValueBySQL("SELECT SUM(traffic) FROM liuliang_data")) / 1024;
		stringBuilder.append(getResources().getString(
				R.string.main_xitongshezhi_textview_all_traffic)
				+ allTraffic + " MB\n");
		long todayTraffic = (baseDao
				.queryRawValueBySQL("SELECT SUM(traffic) FROM liuliang_data WHERE time="
						+ "'" + SystemUtil.getCurrentDate() + "'")) / 1024;
		stringBuilder.append(getResources().getString(
				R.string.main_xitongshezhi_textview_today_traffic)
				+ todayTraffic + " MB\n");
		stringBuilder.append(getResources().getString(
				R.string.main_xitongshezhi_textview_current_traffic)
				+ SystemUtil.getCurrentTraffic() + " KB\n");
		int current_sim = 1;
		if(GlobalParams.DeviceModel.equals("TPS550")){
			current_sim = HdxUtil.GetCurrentSim();
		}
		int simStatus = telephonyManager.getSimState();
		if (current_sim == 1 && (!(simStatus == TelephonyManager.SIM_STATE_ABSENT || simStatus == TelephonyManager.SIM_STATE_UNKNOWN))) {
			stringBuilder.append(getResources().getText(
					R.string.main_xitongshezhi_textview_sim1exist)
					+ "     "
					+ getResources().getText(
							R.string.main_xitongshezhi_textview_sim2notexist));
		} else if (current_sim == 1 && (simStatus == TelephonyManager.SIM_STATE_ABSENT || simStatus == TelephonyManager.SIM_STATE_UNKNOWN)) {
			stringBuilder.append(getResources().getText(
					R.string.main_xitongshezhi_textview_sim1notexist)
					+ "     "
					+ getResources().getText(
							R.string.main_xitongshezhi_textview_sim2notexist));
		} else if (current_sim == 2 && (!(simStatus == TelephonyManager.SIM_STATE_ABSENT || simStatus == TelephonyManager.SIM_STATE_UNKNOWN))) {
			stringBuilder.append(getResources().getText(
					R.string.main_xitongshezhi_textview_sim1notexist)
					+ "     "
					+ getResources().getText(
							R.string.main_xitongshezhi_textview_sim2exist));
		} else if (current_sim == 2 && (simStatus == TelephonyManager.SIM_STATE_ABSENT || simStatus == TelephonyManager.SIM_STATE_UNKNOWN)) {
			stringBuilder.append(getResources().getText(
					R.string.main_xitongshezhi_textview_sim1notexist)
					+ "     "
					+ getResources().getText(
							R.string.main_xitongshezhi_textview_sim2notexist));
		}
		tv_wlan_data.setText(stringBuilder);
    }
       
}
