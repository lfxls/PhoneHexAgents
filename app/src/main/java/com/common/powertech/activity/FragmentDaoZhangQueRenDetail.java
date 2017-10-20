package com.common.powertech.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.bussiness.*;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullUpListView;
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
public class FragmentDaoZhangQueRenDetail extends Fragment {

    private View rootView = null;
    private String sta, oper = "";
    private Spinner status, opername;
    private PullUpListView lv;
    private List<BillDaily_Class> list;
    CustomProgressDialog progressDialog;

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
    private String statue = "0";
    private MyAdapter myAdapter;
    private int index;
    private int count = 0;
    private ItemListActivity mActivity;
    private boolean ifBtnClick = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Button btn1;
        mActivity = (ItemListActivity)getActivity();
        rootView = inflater.inflate(R.layout.fragment_daozhangqueren_main, container, false);
        btn1 = (Button) rootView.findViewById(R.id.btn_dzqr_quiry);
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
                sta = String.valueOf(status.getSelectedItemId());
                //sta = status.getSelectedItem().toString();
//                if(sta.equals( getString(R.string.str_zaitu) ) ){
//                    sta = "0";
//                }else{
//                    sta = "1";
//                }
                statue = sta;
                long i = opername.getSelectedItemId();
                if (i == 0) {
                    oper = "";
                } else {
                    oper = opername.getSelectedItem().toString();
                }
                operater = oper;
                Request_Daily_Query.setContext(mActivity);
                Request_Daily_Query.setOperID(oper);
                Request_Daily_Query.setStatue(sta);
                Request_Daily_Query.setPagenum("1");
                mCurrentPage = 1;
                //Request_Daily_Query.setNumperpage("5");
                String APIName = "PBillDailyQuery";
                String data = Request_Daily_Query.getRequsetXML();
                progress_show(getString(R.string.str_rijiechaxun));
                Client.SendData(APIName, data, handler);

            }
        });

        status = (Spinner) rootView.findViewById(R.id.spinner_daozhangqueren);
        opername = (Spinner) rootView.findViewById(R.id.spinner_jiekuangren);
        String[] mItems = GlobalParams.OPER_LIST.split("\\|");
        String[] tmp = new String[mItems.length + 1];
        tmp[0] = getString(R.string.str_alluser);
        for (int i = 0; i < mItems.length; i++) {
            tmp[i + 1] = mItems[i];
        }
        ArrayAdapter _Adapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, tmp);
        opername.setAdapter(_Adapter);

        lv = (PullUpListView) rootView.findViewById(R.id.listdaozhangquerenView);
        lv.initBottomView();
        lv.setMyPullUpListViewCallBack(new PullUpListView.MyPullUpListViewCallBack() {

            @Override
            public void scrollBottomState() {
                // 拉到底部继续向上拉动加载
                if (!mInLoading) {

                    if (count == 5) {
                        mCurrentPage += 1;
                    }

                    Request_Daily_Query.setContext(mActivity);
                    if (operater.length() > 0) {
                        Request_Daily_Query.setOperID(operater);
                    }
                    Request_Daily_Query.setStatue(statue);
                    Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
                    //Request_Daily_Query.setNumperpage("5");
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
                        if (operater.length() > 0) {
                            Request_Daily_Query.setOperID(operater);
                        }
                        Request_Daily_Query.setStatue(statue);
                        Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
                        //Request_Daily_Query.setNumperpage("5");
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

        Request_Daily_Query.setTofNo("");
        Request_Daily_Query.setContext(mActivity);
        Request_Daily_Query.setStatue("0");
        Request_Daily_Query.setOperID("");
        Request_Daily_Query.setPagenum("1");
        mCurrentPage = 1;
        //Request_Daily_Query.setNumperpage("5");
        String APIName = "PBillDailyQuery";
        String data = Request_Daily_Query.getRequsetXML();
        Client.SendData(APIName, data, handler);
        progress_show(getString(R.string.str_rijiechaxun));

        return rootView;
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

            Temp = list.get(i).Get_TOF_AMT();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv2", Temp);

            Temp = list.get(i).Get_TOF_DATE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv3", Temp);

            Temp = list.get(i).Get_FTA_STATUS();
            if (Temp == null) {
                Temp = "";
            }
            if (Temp.equals("0")) {
                map.put("tv4", getString(R.string.str_zaitu));
            } else {
                map.put("tv4", getString(R.string.str_daozhang));
            }

            Temp = list.get(i).Get_FTA_DEAL_OPR();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv5", Temp);

            Temp = list.get(i).Get_FTA_DEAL_DATE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv6", Temp);

            Temp = list.get(i).Get_TOF_OPR();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv7", Temp);
            map.put("btn", getString(R.string.str_qurendaozhang));
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

            Button btn;
            TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7 = null;
            View btnborder=null;
            if (position == list.size()) {
                convertView = mInflater.inflate(R.layout.list_blank_item, null);
            } else {

                //convertView = mInflater.inflate(R.layout.list_daily_check,null);
                convertView = mInflater.inflate(R.layout.list_daily_confirm, null);
                tv1 = (TextView) convertView.findViewById(R.id.tv1);
                tv2 = (TextView) convertView.findViewById(R.id.tv2);
                tv3 = (TextView) convertView.findViewById(R.id.tv3);
                tv4 = (TextView) convertView.findViewById(R.id.tv4);
                tv5 = (TextView) convertView.findViewById(R.id.tv5);
                tv6 = (TextView) convertView.findViewById(R.id.tv6);
                tv7 = (TextView) convertView.findViewById(R.id.tv7);
                //btn = (Button) convertView.findViewById(R.id.btn);
                btn = (Button) convertView.findViewById(R.id.btn);
                if("TPS350".equalsIgnoreCase(GlobalParams.DeviceModel)){
                    btnborder= (View) convertView.findViewById(R.id.btnborder);
                }
                tv1.setText(BD_getDate().get(position).get("tv1").toString());
                tv2.setText(BD_getDate().get(position).get("tv2").toString());
                tv3.setText(BD_getDate().get(position).get("tv3").toString());
                tv4.setText(BD_getDate().get(position).get("tv4").toString());
                tv5.setText(BD_getDate().get(position).get("tv5").toString());
                tv6.setText(BD_getDate().get(position).get("tv6").toString());
                tv7.setText(BD_getDate().get(position).get("tv7").toString());

                if (statue.equals("0")) {
                    btn.setText(BD_getDate().get(position).get("btn").toString());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfirmNotice(list.get(position).Get_TOF_NO());
                        }
                    });
                } else {
                    btn.setVisibility(View.GONE);
                    if(btnborder != null){
                        btnborder.setVisibility(View.GONE);
                    }
                }

            }

            return convertView;
        }

    }


    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {


                case 0:
                    //联网失败
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

                    break;

                case 1:     //日结查询

                    //联网成功
                    try {

                        //GlobalParams.RETURN_DATA = "<ROOT><TOP><IMEI>862845024199122</IMEI><SESSION_ID>aqUovRAGbgoaCwrrKo3X</SESSION_ID><LOCAL_LANGUAGE>en</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-28 02:13:27</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>Success!</RSPMSG><TOLCNT>2</TOLCNT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000001</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000002</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000003</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000004</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000005</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000006</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000007</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000008</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T0151028000009</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4000.00</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT><STUDENT><TOF_DATE>20151028</TOF_DATE><TOF_NO>T01510280000010</TOF_NO><TOF_OPR>aaa</TOF_OPR><TOF_AMT>4.30</TOF_AMT><FTA_STATUS>0</FTA_STATUS><FTA_DEAL_OPR>\\</FTA_DEAL_OPR></STUDENT></BODY></ROOT>";


                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        List<BillDaily_Class> tmplist = PULLParse_Daily_Query.getBDList(in);

                        if (PULLParse_Daily_Query.getRspcod().equals("00000")) {
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

                //到账确认
                case 2:

                    try {

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.setTitle(getString(R.string.str_qurendaozhang));
                            progressDialog.dismiss();
                            progressDialog = null;
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
                                Request_Daily_Query.setContext(mActivity);
                                if (operater.length() > 0) {
                                    Request_Daily_Query.setOperID(operater);
                                }
                                Request_Daily_Query.setStatue(statue);
                                mCurrentPage -= 1;
                                Request_Daily_Query.setPagenum(String.valueOf(mCurrentPage));
                                //Request_Daily_Query.setNumperpage("5");
                                String APIName = "PBillDailyQuery";
                                String data = Request_Daily_Query.getRequsetXML();
                                Client.SendData(APIName, data, handler);
                                mInRefreshing = true;
                                lv.setAdapter(null);
                                return;
                            }
                            Toast.makeText(mActivity, PULLParse_Daily_Confrim.getRspmsg(), Toast.LENGTH_LONG).show();

                        } else if (tmp.equals("00011")) {
                            //服务器返回系统超时，返回到登录页面
                            Toast.makeText(mActivity, PULLParse_Daily_Query.getRspmsg(), Toast.LENGTH_LONG).show();
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


    private void ConfirmNotice(final String tofno) {

        new CustomDialog.Builder(mActivity).setTitle(getString(R.string.str_qurendaozhang))
                .setMessage(getString(R.string.str_querendaozhang))
                .setPositiveButton(getString(R.string.str_queding), new DialogInterface.OnClickListener() {//添加确定按钮

                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        //发起确认到账
                        Request_Daily_Confirm.setContext(mActivity);
                        Request_Daily_Confirm.setTofno(tofno);
                        Request_Daily_Confirm.setStatue("0");

                        String APIName = "PBillDailyConfirm";
                        String data = Request_Daily_Confirm.getRequsetXML();
                        progress_show(getString(R.string.str_qurendaozhang));
                        Client.SendData(APIName, data, handler);
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.str_quxiao), new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
            	dialog.dismiss();
            }

        }).create().show();//在按键响应事件中显示此对话框
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
//        mItemListActivity.setDefaultFragment("5");
    }

    private void progress_show(String title) {

        progressDialog = CustomProgressDialog.createProgressDialog(
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

        progressDialog.setTitle(getString(R.string.str_rijiechaxun));
        progressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
//        progressDialog.setIndeterminate(false);
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
}
