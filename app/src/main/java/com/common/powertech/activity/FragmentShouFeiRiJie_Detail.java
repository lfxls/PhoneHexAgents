package com.common.powertech.activity;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import printUtils.gprinter;

import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.bussiness.*;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullUpListView;
import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yeqw on 2015/11/11.
 */
public class FragmentShouFeiRiJie_Detail extends Fragment {

    private String APINAME = "";
    private PullUpListView lv;

    //用于判断当前连接是 连接上一页操作还是下一页操作   ifNextPage = -1 表示进行上一页    ifNextPage = 1 表示进行下一页
    private int ifNextPage = 0;
    private View rootView = null;
    private List<BillDaily_Class> list;
    ProgressDialog progressDialog;
    private Boolean ifDetail = true;
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
    private int mCurrentPage = 1,tof_pageno=1;

    private MyAdapter myAdapter;
    private int index;
    private String tempstr = GlobalParams.RETURN_DATA;
    private ArrayList<String> arlist;
    private int count = 0;
    private ItemListActivity mActivity;
    private Printer mPrinter = new Printer();
    private String printstr = "",tofno ="",tof_ticket="";
    private boolean ifBtnClick = false;
    private boolean tof_status = true;
    private String ticket_detail ="",ticket_after ="";
	private gprinter gprinter = new gprinter();
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //收费日结
        mActivity = (ItemListActivity)getActivity();
        
        APINAME = "PBillDailyQuery";
        final Button btn1, btn2;
        TextView tofno_txt;
        rootView = inflater.inflate(R.layout.fragment_shoufeirijie_detail, container, false);
        tofno = getArguments().getString("tofno");
        tof_ticket = getArguments().getString("tof_ticket");
        tof_pageno = getArguments().getInt("tof_pageno");
        tof_status = getArguments().getBoolean("tof_status");
        btn1 = (Button) rootView.findViewById(R.id.btn_detail_shoufeirijie_print);//打印日结单按钮
        btn2 = (Button) rootView.findViewById(R.id.btn_detail_shoufeirijie_return);//返回收费日结界面
        tofno_txt = (TextView) rootView.findViewById(R.id.txt_detail_tofno);
        
        tofno_txt.setText(":".concat(tofno));
        
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	printstr =tof_ticket;
                new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if(ifBtnClick){
                    return;
                }
                ifBtnClick = true;
                
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                FragmentShouFeiRiJieDetail daozhangqueren = new FragmentShouFeiRiJieDetail();
                Bundle bundle = new Bundle();
                bundle.putInt("tof_pageno", tof_pageno);
                bundle.putBoolean("tof_status", tof_status);
                daozhangqueren.setArguments(bundle);
                transaction.replace(R.id.item_detail_container, daozhangqueren);
                transaction.commit();
            }
        });
        
        
        lv = (PullUpListView) rootView.findViewById(R.id.listShoufeirijieView);
        lv.initBottomView();
        lv.setMyPullUpListViewCallBack(new PullUpListView.MyPullUpListViewCallBack() {

            @Override
            public void scrollBottomState() {
                // 拉到底部继续向上拉动加载
                if (!mInLoading) {
                    //当前页未满,重新请求当前页面
                    if (count == 5) {
                        mCurrentPage += 1;
                    }

                    Request_Daily_Query.setContext(mActivity);
                    //读取操作员
                    Request_Daily_Query.setOperID(GlobalParams.LOGIN_USER_ID);
                    if(ifDetail && tofno!=""){
                    	Request_Daily_Query.setTofNo(tofno);//表示查询日结详情
                    }else {
                        Request_Daily_Query.setStatue("0");
                    }

                    Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
//                Request_Daily_Query.setNumperpage("5");

                    String APIName = "PBillDailyQuery";
                    String data = Request_Daily_Query.getRequsetXML();
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
                        Request_Daily_Query.setContext(mActivity);
                        //读取操作员
                        Request_Daily_Query.setOperID(GlobalParams.LOGIN_USER_ID);
                        
                        if(ifDetail && tofno!=""){
                    		Request_Daily_Query.setTofNo(tofno);//表示查询日结详情
	                    }else {
	                        Request_Daily_Query.setStatue("0");
	                    }
                        Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
//                Request_Daily_Query.setNumperpage("5");
                        String APIName = "PBillDailyQuery";
                        String data = Request_Daily_Query.getRequsetXML();
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

            }

            @Override
            public void onHide() {

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ifDetail) {
                    String temp = arlist.get(i);
                    String ticket = Client.Parse_XML(temp, "<TICKET>", "</TICKET>");
                    PrintNotice("<TICKET>" + ticket + "</TICKET>",i);
                }
            }
        });


        //连接服务器

        Request_Daily_Query.setContext(mActivity);
        Request_Daily_Query.setOperID(GlobalParams.LOGIN_USER_ID);
        if(ifDetail && tofno!=""){
        	Request_Daily_Query.setTofNo(tofno);//表示查询日结详情
        }else {
            Request_Daily_Query.setStatue("0");
        }
        Request_Daily_Query.setPagenum("1");
        mCurrentPage = 1;
//        Request_Daily_Query.setNumperpage("5");
        String APIName = "PBillDailyQuery";
        String data = Request_Daily_Query.getRequsetXML();

        Client.SendData(APIName, data, handler);
        progress_show(getString(R.string.str_rijiechaxun));
        return rootView;
    }
    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(mActivity, GpPrintService.class);
        mActivity.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
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
        }
    }


    public void onResume(){
        super.onResume();
        mActivity.setOnBackPressedListener(null);
    }


    //日结查询返回列表
    private ArrayList<HashMap<String, Object>> BD_getDate() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        /**为动态数组添加数据*/
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String Temp;
            Temp = list.get(i).Get_TOF_NO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv1", Temp);
            
            Temp = list.get(i).Get_TOF_DATE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv2", Temp);
            
            Temp = list.get(i).Get_TOF_AMT();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv3", Temp);


            Temp = list.get(i).Get_TOF_OPR();
            if (Temp == null) {
                Temp = "";
            }
            if (Temp.equals("D4")) {
                map.put("tv4", getString(R.string.detail_shoufeirijie_listtv_postpaid));
            } else if (Temp.equals("D5")) {
                map.put("tv4", getString(R.string.detail_shoufeirijie_listtv_reversal));
            } else if (Temp.equals("D2")) {
                map.put("tv4", getString(R.string.detail_shoufeirijie_listtv_prepaid));
            }

            Temp = list.get(i).Get_FTA_DEAL_OPR();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv5", Temp);

            /*Temp = list.get(i).Get_FTA_DEAL_DATE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv6", Temp);*/

            listItem.add(map);
        }
//        //多加一行 用于显示 上一页 下一页按钮
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("tv1", "");
//        listItem.add(map);

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

            Button btn;
            TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7 = null;
            if (position == list.size()) {
                convertView = mInflater.inflate(R.layout.list_blank_item, null);
            } else {

                convertView = mInflater.inflate(R.layout.list_shoudianrijie_detail, null);
                tv1 = (TextView) convertView.findViewById(R.id.tv1);
                tv2 = (TextView) convertView.findViewById(R.id.tv2);
                tv3 = (TextView) convertView.findViewById(R.id.tv3);
                tv4 = (TextView) convertView.findViewById(R.id.tv4);
                tv5 = (TextView) convertView.findViewById(R.id.tv5);
                ArrayList<HashMap<String, Object>> arrayList = BD_getDate();
                tv1.setText(arrayList.get(position).get("tv1").toString());
                tv2.setText(arrayList.get(position).get("tv2").toString());
                tv3.setText(arrayList.get(position).get("tv3").toString());
                tv4.setText(arrayList.get(position).get("tv4").toString());
                tv5.setText(arrayList.get(position).get("tv5").toString());
            }

            return convertView;
        }

    }


    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {

                case 0:

                    try {
                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                            progressDialog = null;
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
                    //联网失败
                    break;

                case 1:     //日结查询

                    //联网成功
                    try {

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        //GlobalParams.RETURN_DATA = "<ROOT><TOP><IMEI>862845024199122</IMEI><SESSION_ID>aqUovRAGbgoaCwrrKo3X</SESSION_ID><LOCAL_LANGUAGE>en</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-28 02:13:27</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>Success!</RSPMSG><TOLCNT>2</TOLCNT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000001</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000002</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000003</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000004</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000005</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000006</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000007</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000008</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000009</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T01510280000010</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT></BODY></ROOT>";
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        List<BillDaily_Class> tmplist = PULLParse_Daily_Query.getBDList(in);
                        
                        
                        if(Request_Daily_Query.getPrdNo()!=null && Request_Daily_Query.getPrdNo()!=""){
                        	Request_Daily_Query.setPrdNo("");
                        	ticket_after=Client.Parse_XML(
    								GlobalParams.RETURN_DATA, "<TICKET_DETAIL>",
    								"</TICKET_DETAIL>");
                        	if(ticket_after.length()>5){
                        		printstr = "<TICKET>"+ticket_after+"</TICKET>";
        		                new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        	}else {
                        		Toast.makeText(mActivity, R.string.str_meiyoujilu, Toast.LENGTH_LONG).show();
                        		
                        	}
                        	
                        }else{
                        	ticket_detail=Client.Parse_XML(
							GlobalParams.RETURN_DATA, "<TICKET_DETAIL>",
							"</TICKET_DETAIL>");
                        }
                        
                        if (PULLParse_Daily_Query.getRspcod().equals("00000")) {
                            count = tmplist.size();
                            if (count > 0) {
                                //当前页面更新
                                list = tmplist;
                                //获取列表用于打印
                                arlist = new ArrayList<String>(list.size());
                                tempstr = GlobalParams.RETURN_DATA;
                                for (int i = 0; i < list.size(); i++) {

                                    int st = tempstr.indexOf("<STUDENT>");
                                    int ed = tempstr.indexOf("</STUDENT>");
                                    arlist.add(tempstr.substring(st, ed + 10));
                                    tempstr = tempstr.substring(ed + 10);
                                }

                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);

                            } else {
                                // 没有加载到数据，页码返回到当前页
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
                            // 没有加载到数据，页码返回到当前页
                            Toast.makeText(mActivity, PULLParse_Daily_Query.getRspmsg(), Toast.LENGTH_LONG).show();
                            //服务器返回系统超时，返回到登录页面
                            if (PULLParse_Daily_Query.getRspcod().equals("00011")) {
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
                //取消日结记录返回
                case 2:

                    try {

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        PULLParse_Daily_Confrim.parse(in);
                        String tmp = PULLParse_Daily_Confrim.getRspcod();
                        //更新列表
                        if (tmp.equals("00000")) {
                            list.remove(index);
                            if (list.size() > 0) {

                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                            } else {    //列表已经全部删除   则获取联网获取当前页码的数据
                                lv.setAdapter(null);
                                if(mCurrentPage <= 1){
                                    return;
                                }
                                Request_Daily_Query.setContext(mActivity);
                                //读取操作员
                                Request_Daily_Query.setOperID(GlobalParams.LOGIN_USER_ID);
                                if(ifDetail){
                                	Request_Daily_Query.setStatue("2");//表示查询日结详情
                                }else {
                                    Request_Daily_Query.setStatue("0");
                                }

                                mCurrentPage -= 1;
                                Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
                                //                Request_Daily_Query.setNumperpage("5");

                                String APIName = "PBillDailyQuery";
                                progress_show(getString(R.string.str_rijiechaxun));
                                String data = Request_Daily_Query.getRequsetXML();
                                Client.SendData(APIName, data, handler);
                                mInRefreshing = true;
                                return;
                            }

                        }
                        Toast.makeText(mActivity, PULLParse_Daily_Confrim.getRspmsg(), Toast.LENGTH_LONG).show();
                        //服务器返回系统超时，返回到登录页面
                        if (tmp.equals("00011")) {
                        	SystemUtil.setGlobalParamsToNull(mActivity);
                            DummyContent.ITEM_MAP.clear();
                            DummyContent.ITEMS.clear();
                        	Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    if (progressDialog != null
                            && (!mActivity.isFinishing())) {
                        progressDialog.dismiss();
                        progressDialog = null;
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
    
    private void PrintNotice(final String ticket,final int position){
    	new AlertDialog.Builder(mActivity).setTitle(getString(R.string.str_dayintishi))
		        .setPositiveButton(getString(R.string.detail_shoufeirijie_listtv_printafter), new DialogInterface.OnClickListener() {//添加确定按钮
		    		
		            @Override
		            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
		                //TODO PRINT
		            	if(ifBtnClick){
		                    return;
		                }
		                ifBtnClick = true;

		                Request_Daily_Query.setContext(mActivity);
		                //读取操作员
		                Request_Daily_Query.setOperID(GlobalParams.LOGIN_USER_ID);
		                /*Request_Daily_Query.setStatue("0");*/
		                if(ifDetail && tofno!=""){
	                    	Request_Daily_Query.setTofNo(tofno);//表示查询日结详情
	                    }
		                Request_Daily_Query.setPrdNo(list.get(position).Get_TOF_NO());
		                if (count == 0) {
		                	mCurrentPage = mCurrentPage-1;
	                    }
		                Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
		                if (list != null) {
		                    list.clear();
		                    myAdapter = new MyAdapter(mActivity);
		                    lv.setAdapter(myAdapter);
		                }
		                String APIName = "PBillDailyQuery";
		                String data = Request_Daily_Query.getRequsetXML();
		                
		                progress_show(getString(R.string.str_rijiechaxun));
		                Client.SendData(APIName, data, handler);
		            }
		        }).setNegativeButton(getString(R.string.detail_shoufeirijie_listtv_printall), new DialogInterface.OnClickListener() {//添加返回按钮
				    @Override
				    public void onClick(DialogInterface dialog, int which) {//响应事件
				    	printstr = "<TICKET>"+ticket_detail+"</TICKET>";
                        new PrintTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        dialog.dismiss();
		                
				    }
		
		}).show();//在按键响应事件中显示此对话框
    }






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
//        mItemListActivity.setDefaultFragment("4");
    }


    private void progress_show(String title) {

        progressDialog = MyProgressDialog.createProgressDialog(
                mActivity, GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new MyProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(MyProgressDialog dialog) {

                        try {
                            if (dialog != null
                                    && (!mActivity.isFinishing())) {
                                dialog.dismiss();
                                dialog = null;
                            }
                            Toast.makeText(mActivity,
                                    getString(R.string.progress_timeout),
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        progressDialog.setTitle(title);
        progressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 是否可以按下退回键取消
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null
                && (!mActivity.isFinishing())) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void createDialog() {
        progressDialog = MyProgressDialog.createProgressDialog(
                mActivity,
                60*1000,
                new MyProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(MyProgressDialog dialog) {
                        SystemUtil.displayToast(mActivity,
                                R.string.progress_timeout);
                        if (dialog != null
                                && (!mActivity
                                .isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }

                    }

                }
        );
    }

    private class PrintTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            createDialog();
            progressDialog
                    .setTitle(getString(R.string.str_dayin));
            progressDialog.setMessage(getString(R.string.progress_conducting));
//            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
/*            mPrinter.start();
            mPrinter.printXML(printstr);
            // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
            // -9999 其他错误
            int printResult = mPrinter.commitOperation();
            mPrinter.stop();
            return printResult;*/
        	gprinter.printXML(printstr);
        	String result = gprinter.commitOperation();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
        	if(result==null){
        		result="Printer Success";
        	}
            SystemUtil.displayToast(mActivity,result);
/*            switch (result) {
                case 0:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_success);
                    break;
                case -1001:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_nopaper);
                    break;
                case -1002:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_hot);
                    break;
                case -1003:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_full);
                    break;
                case -1004:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_noconnect);
                    break;
                case -9999:
                    SystemUtil.displayToast(mActivity,
                            R.string.printer_status_other_error);
                    break;
                default:
                    break;
            }*/
            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
