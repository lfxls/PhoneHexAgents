package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.bussiness.PULLParse_Daily_Confrim;
import com.common.powertech.bussiness.PULLParse_Transfer_Query;
import com.common.powertech.bussiness.Request_Token_Find;
import com.common.powertech.bussiness.Request_Token_TranIn;
import com.common.powertech.bussiness.Request_Transfer_Query;
import com.common.powertech.bussiness.Transfer_Class;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullUpListView;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;

import android.app.DownloadManager.Query;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentZhuanZhangJiLu extends Fragment{
	  private View rootView = null;
	    private String sta, oper = "";
	    private Spinner status, opername;
	    private PullUpListView lv;
	    private List<Transfer_Class> list;
	    ProgressDialog progressDialog;
	    CustomProgressDialog customerProgressDialog;
	    private Button add;
	    private Button btn1;

	    // 设置下拉刷新
	    private PullRefreshLayout mPullLayout;
	    private TextView mActionText;
	    private TextView mTimeText;
	    private View mProgress;
	    private View mActionImage;
	    private Animation mRotateUpAnimation;
	    private Animation mRotateDownAnimation;
	    private boolean mInRefreshing = false;

	    private boolean mInLoading = false;
	    private int mCurrentPage = 1;
	    private String operater = "";
	    private String statue = "";
	    private MyAdapter myAdapter;
	    private int index;
	    private int count = 0;
	    private ItemListActivity mActivity;
	    private boolean ifBtnClick = false;
	    private Map<String,String> STA_MAP = new HashMap<String, String>();
	    private Map<String,String> TYPE_MAP = new HashMap<String, String>();
	    private List STA_LIST = new ArrayList<String>();
	    private List TYPE_LIST = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String all = getString(R.string.zhuanzhang_all);
	     
			STA_MAP.put(getString(R.string.zhuanzhang_status01), "01");
			STA_MAP.put(getString(R.string.zhuanzhang_status02), "02");
			STA_MAP.put(getString(R.string.zhuanzhang_status03), "03");
			 STA_MAP.put(all, "");
			for(Iterator it =  STA_MAP.keySet().iterator();it.hasNext();){
				String key = (String) it.next();
				STA_LIST.add(key);
			}
			
			TYPE_MAP.put(getString(R.string.zhuanzhang_type0), "0");
			TYPE_MAP.put(getString(R.string.zhuanzhang_type1), "1");
			TYPE_MAP.put(all, "");
			for(Iterator it =  TYPE_MAP.keySet().iterator();it.hasNext();){
				String key = (String) it.next();
				TYPE_LIST.add(key);
			}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (ItemListActivity)getActivity();
        rootView = inflater.inflate(R.layout.fragment_zhuanzhangjilu_main, container, false);
        btn1 = (Button) rootView.findViewById(R.id.btn_query);
        add = (Button) rootView.findViewById(R.id.btn_add);
        add.setOnClickListener(AddTrans);
        
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询
                //防止双击
                if (ifBtnClick){
                    return;
                }
                ifBtnClick = true;
                //清空已经查询到的列表
                if (list != null) {
                    list.clear();
                    MyAdapter myAdapter = new MyAdapter(mActivity);
                    lv.setAdapter(myAdapter);
                }

                //日结查询操作  --  在途
//                sta = STA_MAP.get(String.valueOf(status.getSelectedItemId()));
                sta = STA_MAP.get(status.getSelectedItem().toString());
                statue = sta;
                long i = opername.getSelectedItemId();
                
                oper = TYPE_MAP.get(opername.getSelectedItem().toString());
                operater = oper;
                Request_Transfer_Query.setContext(mActivity);
                Request_Transfer_Query.setTOKEN_TYPE(oper);
                Request_Transfer_Query.setORDSTATUS(sta);
                Request_Transfer_Query.setPagenum("1");
                mCurrentPage = 1;
                
                String APIName = "PTranRecords";
                String data = Request_Transfer_Query.getRequsetXML();
                progress_show(getString(R.string.trecord_progress_title));
                Client.SendData(APIName, data, handler);

            }
        });
        

        status = (Spinner) rootView.findViewById(R.id.spinner_zhuangtai);//转账状态
        opername = (Spinner) rootView.findViewById(R.id.spinner_type);//转账类型
       /* String[] mItems = GlobalParams.OPER_LIST.split("\\|");
        String[] tmp = new String[mItems.length + 1];
        tmp[0] = getString(R.string.str_alluser);
        for (int i = 0; i < mItems.length; i++) {
            tmp[i + 1] = mItems[i];
        }
        ArrayAdapter _Adapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, tmp);
        opername.setAdapter(_Adapter);*/
        ArrayAdapter sta_adapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, STA_LIST);
        status.setAdapter(sta_adapter);
        for(int i=0; i<status.getCount();i++){
        	if(status.getItemAtPosition(i).toString().equals(getString(R.string.zhuanzhang_all))){
        		status.setSelection(i, true);
        		break;
        	}
        }
        
        ArrayAdapter type_adapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, TYPE_LIST);
        opername.setAdapter(type_adapter);
        for(int i=0; i<opername.getCount();i++){
        	if(opername.getItemAtPosition(i).toString().equals(getString(R.string.zhuanzhang_all))){
        		opername.setSelection(i, true);
        		break;
        	}
        }

        lv = (PullUpListView) rootView.findViewById(R.id.listzhuanzhangjiluView);
        lv.initBottomView();
        lv.setMyPullUpListViewCallBack(new PullUpListView.MyPullUpListViewCallBack() {

            @Override
            public void scrollBottomState() {
                // 拉到底部继续向上拉动加载
                if (!mInLoading) {

                    if (count == 5) {
                        mCurrentPage += 1;
                    }

                    Request_Transfer_Query.setContext(mActivity);
                    if (operater.length() > 0) {
                        Request_Transfer_Query.setTOKEN_TYPE(operater);
                    }
                    Request_Transfer_Query.setORDSTATUS(statue);
                    Request_Transfer_Query.setPagenum(String.valueOf(mCurrentPage));
                    //Request_Transfer_Query.setNumperpage("5");
                    String APIName = "PTranRecords";
                    String data = Request_Transfer_Query.getRequsetXML();
                    Client.SendData(APIName, data, handler);
                    mInLoading = true;
                }
            }
        });

        mPullLayout = (PullRefreshLayout) rootView
                .findViewById(R.id.pull_container);
        mPullLayout.setOnActionPullListener(new PullRefreshLayout.OnPullListener() {

            @Override
            public void onSnapToTop() {
                if (!mInRefreshing) {
                    showRefreshModel();
                    // new RefreshDataTask().execute();
                    // 拖到顶部下拉刷新上一页
                    if (mCurrentPage > 1) {

                        mCurrentPage -= 1;
                        Request_Transfer_Query.setContext(mActivity);
                        if (operater.length() > 0) {
                            Request_Transfer_Query.setTOKEN_TYPE(operater);
                        }
                        Request_Transfer_Query.setORDSTATUS(statue);
                        Request_Transfer_Query.setPagenum(String.valueOf(mCurrentPage));
                        //Request_Transfer_Query.setNumperpage("5");
                        String APIName = "PTranRecords";
                        String data = Request_Transfer_Query.getRequsetXML();
                        Client.SendData(APIName, data, handler);

                    } else {
                        if (mInRefreshing) {
                            removeRefreshModel();
                        }
                    }
                }
            }

            @Override
            public void onShow() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onHide() {
                // TODO Auto-generated method stub

            }
        });
        mPullLayout.setOnPullStateChangeListener(new PullRefreshLayout.OnPullStateListener() {

            @Override
            public void onPullOut() {
                if (!mInRefreshing) {
                    mActionText.setText(R.string.note_pull_refresh);
                    mActionImage.clearAnimation();
                    mActionImage.startAnimation(mRotateUpAnimation);
                }
            }

            @Override
            public void onPullIn() {
                if (!mInRefreshing) {
                    mActionText.setText(R.string.note_pull_down);
                    mActionImage.clearAnimation();
                    mActionImage.startAnimation(mRotateDownAnimation);
                }
            }
        });

        mRotateUpAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.rotate_up);
        mRotateDownAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.rotate_down);

        mProgress = (View) rootView.findViewById(android.R.id.progress);
        mActionImage = (View) rootView.findViewById(android.R.id.icon);
        mActionText = (TextView) rootView.findViewById(R.id.pull_note);
        mTimeText = (TextView) rootView.findViewById(R.id.refresh_time);
        mTimeText.setText(R.string.note_not_update);

//        sta = status.getSelectedItem().toString();
//        if(sta.equals( getString(R.string.str_zaitu) ) ){
//            sta = "0";
//        }else{
//            sta = "1";
//        }
        return rootView;
    }


    public void onResume(){
        super.onResume();
        mActivity.setOnBackPressedListener(null);
        Request_Transfer_Query.setContext(mActivity);
        Request_Transfer_Query.setORDSTATUS("");
        Request_Transfer_Query.setTOKEN_TYPE("");
        Request_Transfer_Query.setPagenum("1");
        mCurrentPage = 1;
        //Request_Transfer_Query.setNumperpage("5");
        String APIName = "PTranRecords";
        String data = Request_Transfer_Query.getRequsetXML();
        Client.SendData(APIName, data, handler);
        progress_show(getString(R.string.update_progressdialog_message));
    }
    
    
   private android.view.View.OnClickListener AddTrans = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(mActivity,CustTransferActivity.class);
			mActivity.startActivity(intent);
		}
	};

    //日结查询返回列表
    private ArrayList<HashMap<String, Object>> BD_getDate() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        /**为动态数组添加数据*/
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String Temp;
            boolean btnVi=false;
            Temp = list.get(i).getPRDORDNO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv1", Temp);

            Temp = list.get(i).getTOKEN_TYPE();
            if (Temp == null) {
                Temp = "";
            }else if(Temp.equals("0")){
            	Temp = getString(R.string.zhuanzhang_type0);
            	btnVi=false;
            }else if(Temp.equals("1")){
            	Temp = getString(R.string.zhuanzhang_type1);
            	btnVi=true;
            }else{
            	Temp = "";
            }
            //手机号
            Temp = list.get(i).getPIN_PHONE();
            map.put("tv2", Temp);

            Temp = list.get(i).getTRANSFER_AMT();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv3", Temp);
            
            Temp = list.get(i).getTOKEN_USER();
            if (Temp == null) {
                Temp = "-";
            }
            map.put("tv4", Temp);

            Temp = list.get(i).getORDSTATUS();
            if (Temp == null) {
                Temp = "";
                Temp = getString(R.string.zhuanzhang_all);
            }
            if (Temp.equals("01")) {
                map.put("tv5", getString(R.string.zhuanzhang_status01));
                if(btnVi){
                	map.put("btn", true);// 未使用且指定代理商的记录可找回
                }else{
                	map.put("btn", false);
                }
            } else if(Temp.equals("02")) {
            	map.put("tv5", getString(R.string.zhuanzhang_status02));
            	map.put("btn", false);
            }
            else if(Temp.equals("03")) {
            	map.put("tv5", getString(R.string.zhuanzhang_status03));
            	map.put("btn", false);
            }else{
            	map.put("btn", false);
            }
            
            Temp = list.get(i).getORDERTIME();
            if (Temp == null) {
                Temp = "-";
            }
            map.put("tv6", Temp);

            listItem.add(map);
        }
        //多加一行 用于显示 上一页 下一页按钮
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tv1", "");
        listItem.add(map);

        return listItem;
    }


    /**
     * 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        /**
         * 构造函数
         */
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //日结
            return list.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            TextView tv1, tv2, tv3, tv4, tv5 ,tv6= null;
            Button btn;
//            View btnborder=null;
            if (position == list.size()) {
                convertView = mInflater.inflate(R.layout.list_blank_item, null);
            } else {

                convertView = mInflater.inflate(R.layout.list_zhuanzhangjilu, null);
                tv1 = (TextView) convertView.findViewById(R.id.tv1);
                tv2 = (TextView) convertView.findViewById(R.id.tv2);
                tv3 = (TextView) convertView.findViewById(R.id.tv3);
                tv4 = (TextView) convertView.findViewById(R.id.tv4);
                tv5 = (TextView) convertView.findViewById(R.id.tv5);
                tv6 = (TextView) convertView.findViewById(R.id.tv6);
                btn = (Button) convertView.findViewById(R.id.btn);
//                if("TPS350".equalsIgnoreCase(GlobalParams.DeviceModel)){
//                    btnborder= (View) convertView.findViewById(R.id.btnborder);
//                }
                HashMap<String, Object> map = BD_getDate().get(position);
                tv1.setText(map.get("tv1")==null?"":map.get("tv1").toString());
                tv1.setVisibility(View.GONE);
                tv2.setText(map.get("tv2")==null?"":map.get("tv2").toString());
                tv3.setText(map.get("tv3")==null?"":map.get("tv3").toString());
                tv4.setText(map.get("tv4")==null?"":map.get("tv4").toString());
                tv5.setText(map.get("tv5")==null?"":map.get("tv5").toString());
                tv6.setText(map.get("tv6")==null?"":map.get("tv6").toString());
                if((Boolean) map.get("btn")){
                	final String phone = map.get("tv2").toString();
                	final String PRDORDNO = map.get("tv1").toString();
                	//未使用状态的可找回
                	btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							CustomDialog.Builder dialog = new CustomDialog.Builder(mActivity);
							dialog.setMessage(getString(R.string.trecord_btndialog_msg).replace("#{phone}", phone));
							dialog.setPositiveButton(R.string.tranin_dialog_pbtn, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// 找回
									dialog.dismiss();
									new FindTokenTask().execute(PRDORDNO);
								}
							});
							dialog.setNegativeButton(R.string.tranin_dialog_nbtn, new  OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							dialog.create().show();
						}
					});
                }else{
                	btn.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

    }
    
	private class FindTokenTask extends AsyncTask<String, Void, String>{
		String ErrorMsg;
		String RspMsg = "";
		String RspCode = "";
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			createDialog();
			progressDialog.setTitle(getString(R.string.dialog_check));
			progressDialog.setMessage(getString(R.string.progress_conducting));
			// 设置进度条是否不明确
//			progressDialog.setIndeterminate(false);
			// 是否可以按下退回键取消
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String PRDORDNO = params[0];
			if(TextUtils.isEmpty(PRDORDNO)){
				ErrorMsg = getString(R.string.tranord_error1);
				return "1";
			}
			
			Request_Token_Find.setContext(mActivity);
			Request_Token_Find.setTRANSFER_NO(PRDORDNO);
			
			String requestXML = Request_Token_Find.getRequsetXML();
			String reponseXML = "";
			 try {
		            reponseXML = Client.ConnectServer("PFindTranToken", requestXML);
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

		            return "0";
		        } else {
		            // 服务器返回系统超时，返回到登录页面
		            if (RspCode.equals("00011")) {
		                return "2";
		            }
		            if (RspMsg.equalsIgnoreCase("")) {
		            	ErrorMsg = getString(R.string.tranord_error2);
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
				//成功 跳转到成功页面
				CustomDialog.Builder dialog = new CustomDialog.Builder(mActivity);
				dialog.setMessage(R.string.tranord_find_successmsg);
				dialog.setNegativeButton(R.string.trecord_btndialog_negative, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						btn1.performClick();
						dialog.dismiss();
					}
				});
				dialog.create().show();
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


    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {


                case 0:
                    //联网失败
                    try {
                        if (customerProgressDialog != null
                                && (!mActivity.isFinishing())) {
                        	customerProgressDialog.dismiss();
                        	customerProgressDialog = null;
                        }
                        // 没有加载到数据，页码返回到当前页
                        Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mInRefreshing) {
                        mCurrentPage += 1;
                    } else if (mInLoading) {
                        if (count == 5) {
                            mCurrentPage -= 1;
                        }
                    } else {
                        turnBack();
                    }

                    break;

                case 1:     //日结查询

                    //联网成功
                    try {

                        //GlobalParams.RETURN_DATA = "<ROOT><TOP><IMEI>862845024199122</IMEI><SESSION_ID>aqUovRAGbgoaCwrrKo3X</SESSION_ID><LOCAL_LANGUAGE>en</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-28 02:13:27</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>Success!</RSPMSG><TOLCNT>2</TOLCNT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000001</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000002</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000003</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000004</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000005</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000006</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000007</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000008</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000009</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T01510280000010</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT></BODY></ROOT>";


                        if (customerProgressDialog != null
                                && (!mActivity.isFinishing())) {
                        	customerProgressDialog.dismiss();
                        	customerProgressDialog = null;
                        }
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        List<Transfer_Class> tmplist = PULLParse_Transfer_Query.getBDList(in);

                        if (PULLParse_Transfer_Query.getRspcod().equals("00000")) {
                            count = tmplist.size();
                            if (count > 0) {
                                //当前页面更新
                                list = tmplist;
                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                            } else {

                                Toast.makeText(mActivity, getString(R.string.str_meiyoujilu), Toast.LENGTH_LONG).show();
                                if (mInRefreshing) {
                                    mCurrentPage += 1;
                                } else if (mInLoading) {
                                    if (count == 5) {
                                        mCurrentPage -= 1;
                                    }
                                } else {
                                    turnBack();
                                }
                            }
                        } else {
                            Toast.makeText(mActivity, PULLParse_Transfer_Query.getRspmsg(), Toast.LENGTH_LONG).show();
                            //服务器返回系统超时，返回到登录页面
                            if (PULLParse_Transfer_Query.getRspcod().equals("00011")) {
                            	SystemUtil.setGlobalParamsToNull(mActivity);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                            	Intent intent = new Intent(mActivity, LoginActivity.class);
                                mActivity.startActivity(intent);
                            }
                            if (mInRefreshing) {
                                mCurrentPage += 1;
                            } else if (mInLoading) {
                                if (count == 5) {
                                    mCurrentPage -= 1;
                                }
                            } else {
                                turnBack();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //到账确认
                case 2:

                    try {

                        if (customerProgressDialog != null
                                && (!mActivity.isFinishing())) {
                        	customerProgressDialog.setTitle(getString(R.string.str_qurendaozhang));
                        	customerProgressDialog.dismiss();
                        	customerProgressDialog = null;
                        }
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        PULLParse_Daily_Confrim.parse(in);
                        String tmp = PULLParse_Daily_Confrim.getRspcod();
                        if (tmp.equals("00000")) {
                            list.remove(index);

                            if (list.size() > 0) {
                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                            } else {    //列表已经全部删除   则获取联网获取当前页码的数据

                                if(mCurrentPage <= 1){
                                    lv.setAdapter(null);
                                    return;
                                }
                                Request_Transfer_Query.setContext(mActivity);
                                if (operater.length() > 0) {
                                    Request_Transfer_Query.setTOKEN_TYPE(operater);
                                }
                                Request_Transfer_Query.setORDSTATUS(statue);
                                mCurrentPage -= 1;
                                Request_Transfer_Query.setPagenum(String.valueOf(mCurrentPage));
                                //Request_Transfer_Query.setNumperpage("5");
                                String APIName = "PTranRecords";
                                String data = Request_Transfer_Query.getRequsetXML();
                                Client.SendData(APIName, data, handler);
                                mInRefreshing = true;
                                lv.setAdapter(null);
                                return;
                            }
                            Toast.makeText(mActivity, PULLParse_Daily_Confrim.getRspmsg(), Toast.LENGTH_LONG).show();

                        } else if (tmp.equals("00011")) {
                            //服务器返回系统超时，返回到登录页面
                            Toast.makeText(mActivity, PULLParse_Transfer_Query.getRspmsg(), Toast.LENGTH_LONG).show();
                            SystemUtil.setGlobalParamsToNull(mActivity);
                            DummyContent.ITEM_MAP.clear();
                            DummyContent.ITEMS.clear();
                            Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
                        }
                        
                        else if (tmp.equals("00003"))
                        { 	
                       Toast.makeText(mActivity, PULLParse_Daily_Confrim.getRspmsg(), Toast.LENGTH_LONG).show();
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:

                    if (customerProgressDialog != null
                            && (!mActivity.isFinishing())) {
                    	customerProgressDialog.dismiss();
                    	customerProgressDialog = null;
                    }
                    break;
            }


            if (mInLoading) {
                lv.removeFooterView();
            }
            // 若是下拉更新完
            removeRefreshModel();
            mInLoading = false;
            ifBtnClick = false;
        }
    };


    private void showRefreshModel() {
        mInRefreshing = true;
        mPullLayout.setEnableStopInActionView(true);
        mActionImage.clearAnimation();
        mActionImage.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mActionText.setText(R.string.note_pull_loading);
    }

    private void removeRefreshModel() {
        if (mInRefreshing) {
            mInRefreshing = false;
            mPullLayout.setEnableStopInActionView(false);
            mPullLayout.hideActionView();
            mActionImage.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);

            if (mPullLayout.isPullOut()) {
                mActionText.setText(R.string.note_pull_refresh);
                mActionImage.clearAnimation();
                mActionImage.startAnimation(mRotateUpAnimation);
            } else {
                mActionText.setText(R.string.note_pull_down);
            }

            mTimeText.setText(getString(
                    R.string.note_update_at,
                    DateFormat.getTimeFormat(mActivity).format(
                            new Date(System.currentTimeMillis()))
            ));
        }
    }

    private void turnBack() {
//        ItemListActivity mItemListActivity = (ItemListActivity) mActivity;
//        mItemListActivity.setDefaultFragment("5");
    }

    private void progress_show(String title) {

        customerProgressDialog = CustomProgressDialog.createProgressDialog(
                mActivity, GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new CustomProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(CustomProgressDialog dialog) {
                        Toast.makeText(mActivity,
                                getString(R.string.progress_timeout),
                                Toast.LENGTH_LONG).show();
                        if (dialog != null
                                && (!mActivity.isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }
                }
        );

        customerProgressDialog.setTitle(getString(R.string.update_progressdialog_message));
        customerProgressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
//        progressDialog.setIndeterminate(false);
        // 是否可以按下退回键取消
        customerProgressDialog.setCancelable(false);
        customerProgressDialog.show();

    }

    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null
                && (!mActivity.isFinishing())) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
