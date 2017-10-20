package com.common.powertech.activity;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.*;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import android.os.Bundle;
import android.view.View.OnClickListener;
import com.common.powertech.bussiness.*;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.ByteLimitWatcher;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullUpListView;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public class FragmentChongZhengShenQingDetail extends Fragment implements OnClickListener {
    private PullUpListView lv;
    //用于判断当前连接是 连接上一页操作还是下一页操作   ifNextPage = -1 表示进行上一页    ifNextPage = 1 表示进行下一页
    private int ifNextPage = 0;
    private List<BillRecovery_Class> brlist;
    String inputCond = "";
    protected static boolean isRun = false;// 业务处理中，按取消件无效
    CustomProgressDialog progressDialog;
    private AlertDialog ConfrimDialog;
    private TextView tv_danhao;
    private EditText tv_reason;
    private Spinner spinner_yuanyin;
    private Context ctx;

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
    private Boolean RecConfirm;
    private int index;
    private MyAdapter myAdapter;
    private int count = 0;
    private ItemListActivity mActivity;
    private boolean ifBtnClick = false;
    public static boolean SoftInputRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("FragmentChongZhengShenQingDetail", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_chongzhengshenqing_detail, container, false);
//		mBtn = (Button) view.findViewById(R.id.id_fragment_one_btn);
        mActivity = (ItemListActivity) getActivity();
        inputCond = getArguments().getString("inputCond");
        lv = (PullUpListView) rootView.findViewById(R.id.listChongzhengShenQingView);
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
                    Request_Recovery_Query.setContext(mActivity);
                    Request_Recovery_Query.setUsrno(inputCond);
                    Request_Recovery_Query.setPagenum(String.valueOf(mCurrentPage));
                    //Request_Recovery_Query.setNumperpage("5");
                    String APIName = "PBillRecoveryQuery";
                    String data = Request_Recovery_Query.getRequsetXML();
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
                        Request_Recovery_Query.setContext(mActivity);
                        Request_Recovery_Query.setUsrno(inputCond);
                        Request_Recovery_Query.setPagenum(String.valueOf(mCurrentPage));
                        //Request_Recovery_Query.setNumperpage("5");
                        String APIName = "PBillRecoveryQuery";
                        String data = Request_Recovery_Query.getRequsetXML();
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
        progressDialog.setTitle(getString(R.string.str_chognzhengchaxun));
        progressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
//        progressDialog.setIndeterminate(false);
        // 是否可以按下退回键取消
        progressDialog.setCancelable(false);

        mCurrentPage = 1;

        Request_Recovery_Query.setContext(mActivity);
        Request_Recovery_Query.setUsrno(inputCond);

        Request_Recovery_Query.setPagenum(String.valueOf(mCurrentPage));
        //Request_Recovery_Query.setNumperpage("5");
        String APIName = "PBillRecoveryQuery";
        String data = Request_Recovery_Query.getRequsetXML();
        Client.SendData(APIName, data, handler);

        progressDialog.show();
        RecConfirm = false;
        return rootView;
    }

    public void onResume() {
        Log.e("FragmentChongZhengShenQingDetail", "onResume");
        super.onResume();

        mActivity.setOnBackPressedListener(new ItemListActivity.OnBackPressedListener() {
            @Override
            public void onPressed() {
                turnBack();
            }
        });
    }

    @Override
    public void onClick(View v) {
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

            return brlist.size();//返回数组的长度

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

            convertView = mInflater.inflate(R.layout.list_recovery, null);
            tv1 = (TextView) convertView.findViewById(R.id.tv1);
            tv2 = (TextView) convertView.findViewById(R.id.tv2);
            tv3 = (TextView) convertView.findViewById(R.id.tv3);
            tv4 = (TextView) convertView.findViewById(R.id.tv4);
            tv5 = (TextView) convertView.findViewById(R.id.tv5);
            tv6 = (TextView) convertView.findViewById(R.id.tv6);
            tv7 = (TextView) convertView.findViewById(R.id.tv7);
            btn = (Button) convertView.findViewById(R.id.btn);

            if (position == brlist.size()) {
                btn.setVisibility(View.GONE);
            } else {
                tv1.setText(BR_getData().get(position).get("tv1").toString());
                String temp = BR_getData().get(position).get("tv2").toString();
                if (temp.equals("D2")) {
                    temp = getString(R.string.str_yufufei);
                } else if (temp.equals("D4")) {
                    temp = getString(R.string.str_houfufei);
                }
                tv2.setText(temp);
                tv3.setText(BR_getData().get(position).get("tv3").toString());
                tv4.setText(BR_getData().get(position).get("tv4").toString());
                tv5.setText(BR_getData().get(position).get("tv5").toString());
                tv6.setText(BR_getData().get(position).get("tv6").toString());
                tv7.setText(BR_getData().get(position).get("tv7").toString());
            }

            btn.setText(BR_getData().get(position).get("btn").toString());

            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO
                    if (ifBtnClick) {
                        return;
                    }
                    ifBtnClick = true;
                    RecConfirm = true;
                    index = position;
                    showRecovertConfirmDialog(mActivity, position);
                }
            });

            if (BR_getData().get(position).get("tv7").toString().equals(getString(R.string.str_jujue))
                    || BR_getData().get(position).get("tv7").toString().equals("-")) {
                btn.setVisibility(View.VISIBLE);
            } else {
                btn.setVisibility(View.GONE);
                tv7.setTextColor(Color.rgb(255, 0, 0));
            }
            return convertView;
        }

    }

    //冲正查询返回列表
    private ArrayList<HashMap<String, Object>> BR_getData() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        /**为动态数组添加数据*/
        for (int i = 0; i < brlist.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String Temp;
            Temp = brlist.get(i).Get_PRDORDNO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv1", Temp);

            Temp = brlist.get(i).Get_BIZ_TYPE();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv2", Temp);

            Temp = brlist.get(i).Get_ORDAMT();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv3", Temp);

            Temp = brlist.get(i).Get_ORDERTIME();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv4", Temp);

            Temp = brlist.get(i).Get_ELEN_ID();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv5", Temp);

            Temp = brlist.get(i).Get_USER_NO();
            if (Temp == null) {
                Temp = "";
            }
            map.put("tv6", Temp);

            Temp = brlist.get(i).Get_R_STATUS();
            if (Temp == null) {
                Temp = "";
            }
            if (Temp.equals("0")) {
                map.put("tv7", getString(R.string.str_shenhe));
            } else if (Temp.equals("1")) {
                map.put("tv7", getString(R.string.str_yichongzheng));
            } else if (Temp.equals("2")) {
                map.put("tv7", getString(R.string.str_jujue));
            } else {
                map.put("tv7", "-");
            }

            map.put("btn", getString(R.string.str_chongzheng));
            listItem.add(map);
        }
        //多加一行 用于显示 上一页 下一页按钮
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tv1", "");
        map.put("tv2", "");
        map.put("tv3", "");
        map.put("tv4", "");
        map.put("tv5", "");
        map.put("tv6", "");
        map.put("tv7", "");
        map.put("btn", "");
        listItem.add(map);

        return listItem;
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
                        if (!RecConfirm) {
                            turnBack();
                        }
                    }
                    //联网失败
                    break;

                case 1:     //冲正查询


                    //联网成功
                    try {
//                        GlobalParams.RETURN_DATA = "<ROOT><TOP><IMEI>762845024199122</IMEI><SESSION_ID>E4ZbMmX7TngsEywlvT3g</SESSION_ID><LOCAL_LANGUAGE>zh</LOCAL_LANGUAGE><REQUEST_TIME>2015-10-30 12:41:33</REQUEST_TIME></TOP><BODY><RSPCOD>00000</RSPCOD><RSPMSG>成功!</RSPMSG><TOLCNT>6</TOLCNT>" +
//                                "<STUDENT><PRDORDNO>D015103000021498</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力1234 56788 SDFSDF SDFSFDS</ELEN_ID><R_STATUS>0</R_STATUS></STUDENT>" +
//                                "<STUDENT><PRDORDNO>D015103000021499</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力</ELEN_ID><R_STATUS>2</R_STATUS></STUDENT>" +
//                                "<STUDENT><PRDORDNO>D015103000021499</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力</ELEN_ID><R_STATUS>0</R_STATUS></STUDENT>" +
//                                "<STUDENT><PRDORDNO>D015103000021499</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力</ELEN_ID><R_STATUS>0</R_STATUS></STUDENT>" +
//                                "<STUDENT><PRDORDNO>D015103000021499</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力</ELEN_ID><R_STATUS>0</R_STATUS></STUDENT>" +
//                                "<STUDENT><PRDORDNO>D015103000021499</PRDORDNO><ORDERTIME>2015-09-30</ORDERTIME><ORDAMT>34155</ORDAMT><BIZ_TYPE>D2</BIZ_TYPE><USER_NO>1031204462</USER_NO><ELEN_ID>海兴电力</ELEN_ID><R_STATUS>0</R_STATUS></STUDENT>" +
//                                "</BODY></ROOT>";

                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                        }

                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));

                        List<BillRecovery_Class> tmp = PULLParse_Recovery_Query.getBRList(in);
                        String errcode = PULLParse_Recovery_Query.getRspcod();

                        //交易成功
                        if (errcode.equals("00000")) {

                            String number = inputCond;
                            String regex = "(.{4})";
                            number = number.replaceAll(regex, "$1 ");
                            List<String> numberList = new ArrayList<String>();
                            if (Preferences.getComplexDataInPreference(mActivity,
                                    Preferences.KEY_MeterOrUser_No, "0") != null
                                    && !Preferences
                                    .getComplexDataInPreference(mActivity,
                                            Preferences.KEY_MeterOrUser_No, "0")
                                    .toString().equalsIgnoreCase("0")) {
                                numberList = (List<String>) Preferences
                                        .getComplexDataInPreference(mActivity,
                                                Preferences.KEY_MeterOrUser_No, "0");
                            }
                            if (numberList.size() > 0) {
                                boolean same = false;
                                for (int i = 0; i < numberList.size(); i++) {
                                    String str = numberList.get(i);
                                    if (str.equalsIgnoreCase(number)) {
                                        same = true;
                                    }
                                }
                                if (!same) {
                                    numberList.add(number);
                                }
                            } else {
                                numberList.add(number);
                            }
                            Preferences.storeComplexDataInPreference(mActivity,
                                    Preferences.KEY_MeterOrUser_No, numberList);

                            //有记录
                            count = tmp.size();
                            if (count > 0) {
                                //当前页面更新
                                ItemListActivity activity = (ItemListActivity) mActivity;
                                activity.setShortCutsKeyDownCallBack(new ItemListActivity.ShortCutsKeyDownCallBack() {

                                    @Override
                                    public void keyValue(int selectKey) {
                                    }
                                });

                                brlist = tmp;
                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                                //没有记录
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
                            //交易失败
                        } else {
                            Toast.makeText(mActivity, PULLParse_Recovery_Query.getRspmsg(), Toast.LENGTH_LONG).show();
                            //服务器返回系统超时，返回到登录页面
                            if (errcode.equals("00011")) {
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

                //冲正确认成功返回
                case 2:


                    try {
                        if (progressDialog != null
                                && (!mActivity.isFinishing())) {
                            progressDialog.dismiss();
                        }
                        InputStream in = new ByteArrayInputStream(GlobalParams.RETURN_DATA.getBytes("UTF-8"));
                        PULLParse_Recovery_Confrim.parse(in);
                        String temp = PULLParse_Recovery_Confrim.getRspcod();
                        if (temp.equals("00000")) {

                            ConfrimDialog.dismiss();
                            BillRecovery_Class recovery_class = brlist.get(index);
                            recovery_class.Set_R_STATUS("0");
                            //brlist.remove(index);
                            if (brlist.size() > 0) {

                                myAdapter = new MyAdapter(mActivity);
                                lv.setAdapter(myAdapter);
                            } else {
                                //列表已经全部删除   则获取联网获取当前页码的数据
                                Request_Recovery_Query.setContext(mActivity);
                                Request_Recovery_Query.setUsrno(inputCond);
                                Request_Recovery_Query.setPagenum(String.valueOf(mCurrentPage));
                                //Request_Recovery_Query.setNumperpage("5");
                                String APIName = "PBillRecoveryQuery";
                                String data = Request_Recovery_Query.getRequsetXML();
                                Client.SendData(APIName, data, handler);
                                mInLoading = true;

                            }
                        } else if (temp.equals("00011")) {
                            //服务器返回系统超时，返回到登录页面
                            SystemUtil.setGlobalParamsToNull(mActivity);
                            DummyContent.ITEM_MAP.clear();
                            DummyContent.ITEMS.clear();
                            Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
                        }
                        SoftInputRunning = false;
                        Toast.makeText(mActivity, PULLParse_Recovery_Confrim.getRspmsg(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
//                    if (progressDialog != null
//                            && (!mActivity.isFinishing())) {
//                        progressDialog.dismiss();
//                    }
//                    btnprev.setEnabled(true);
//                    btnnext.setEnabled(true);
                    break;
            }
            if (mInLoading) {
                lv.removeFooterView();
            }
            // 若是下拉更新完
            removeRefreshModel();
            mInLoading = false;
        }
    };


    public void showRecovertConfirmDialog(Context mContext, int position) {

        ctx = mContext;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if (GlobalParams.DeviceModel.equalsIgnoreCase("TPS390")) {
            view = inflater.inflate(R.layout.dialog_recovery_confrim_en, null);
        } else {
            view = inflater.inflate(R.layout.dialog_recovery_confirm, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
        }
        tv_danhao = (TextView) view.findViewById(R.id.tv_danhao);
        TextView tv_jine = (TextView) view.findViewById(R.id.tv_jine);
        TextView tv_leixing = (TextView) view.findViewById(R.id.tv_leixing);
        TextView tv_shijian = (TextView) view.findViewById(R.id.tv_shijian);
        TextView tv_gongsi = (TextView) view.findViewById(R.id.tv_gongsi);
        TextView tv_huhao = (TextView) view.findViewById(R.id.tv_huhao);

        tv_reason = (EditText) view.findViewById(R.id.tv_reason);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            	if (charSequence.toString().contains("<")
                        || charSequence.toString().contains("&")
                        || charSequence.toString().contains("%")
                        || charSequence.toString().contains(">")) {
                    charSequence = charSequence.toString().replace("<", "");
                    charSequence = charSequence.toString().replace(">", "");
                    charSequence = charSequence.toString().replace("&", "");
                    charSequence = charSequence.toString().replace("%", "");
                    charSequence = charSequence.toString().trim();
                    tv_reason.setText(charSequence.toString());
                    tv_reason.setSelection(charSequence.length());
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        tv_reason.addTextChangedListener( new ByteLimitWatcher(tv_reason,textWatcher,256));
        tv_danhao.setText(brlist.get(position).Get_PRDORDNO());
        tv_jine.setText(brlist.get(position).Get_ORDAMT());
        String tmp = brlist.get(position).Get_BIZ_TYPE();
        if (tmp.equals("D2")) {
            tmp = getString(R.string.str_yufufei);
        } else {
            tmp = getString(R.string.str_houfufei);
        }
        tv_leixing.setText(tmp);
        tv_shijian.setText(brlist.get(position).Get_ORDERTIME());
        tv_gongsi.setText(brlist.get(position).Get_ELEN_ID());
        tv_huhao.setText(brlist.get(position).Get_USER_NO());

        spinner_yuanyin = (Spinner) view.findViewById(R.id.spinner_yuanyin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item);
        //原因列表
        adapter.add(getString(R.string.str_dianfeicuoshou));
        adapter.add(getString(R.string.str_qitayuanyin));

        spinner_yuanyin.setAdapter(adapter);
        spinner_yuanyin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //tv_reason.setText( adapterView.getSelectedItem().toString());
                if (i == 0) {
                    tv_reason.setText("");
                    tv_reason.setEnabled(false);
                } else {
                    tv_reason.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Button btn_qrgd = (Button) view.findViewById(R.id.btn_qrgd);
        btn_qrgd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交冲正请求
                Request_Recovery_Confirm.setContext(mActivity);
                String top = Request_Recovery_Confirm.getTOP();
                long index = spinner_yuanyin.getSelectedItemId();
                String reason = spinner_yuanyin.getSelectedItem().toString();
                String type = "0";
                if (index != 0) {
                    reason = tv_reason.getText().toString();
                    if (reason.length() == 0) {
                        Toast.makeText(mActivity, getString(R.string.str_inputresason), Toast.LENGTH_LONG).show();
                        return;
                    }
                    type = "1";
                }

                //Request_Recovery_Confirm.setContext(mActivity);
                Request_Recovery_Confirm.setNumber(tv_danhao.getText().toString());
                Request_Recovery_Confirm.setRtype(type);
                Request_Recovery_Confirm.setRreason(reason);
                String APIName = "PBillReversalApp";
                String data = Request_Recovery_Confirm.getRequsetXML();
                Client.SendData(APIName, data, handler);
                progressDialog.setTitle(getString(R.string.str_chongzheng));
                progressDialog.show();

                ifBtnClick = false;
            }
        });


        tv_reason.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && KeyEvent.ACTION_UP == keyEvent.getAction()) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                                0);
                    }
                    //btn_qrgd.performClick();// 登录按钮自动按下
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    return true;
                }
                return false;
            }
        });

        tv_reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lines = tv_reason.getLineCount();
                if (lines > 3) {
                    lines = 3;
                }
                tv_reason.setLines(lines);
            }
        });

//        tv_reason.setOnClickListener( new OnClickListener() {
//            Boolean flag = true;
//            @Override
//            public void onClick(View view) {
//                if(flag){
//                    flag = false;
//                    tv_reason.setEllipsize(null); // 展开
//                    tv_reason.setSingleLine(flag);
//                    CharSequence text = tv_reason.getText();
//                    if (text instanceof Spannable) {
//                        Spannable spanText = (Spannable)text;
//                        Selection.setSelection(spanText, text.length());
//                    }
//                }else{
//                    flag = true;
//                    tv_reason.setEllipsize(TextUtils.TruncateAt.START); // 收缩
//                    tv_reason.setSingleLine(flag);
//                    CharSequence text = tv_reason.getText();
//                    if (text instanceof Spannable) {
//                        Spannable spanText = (Spannable)text;
//                        Selection.setSelection(spanText, text.length());
//                    }
//                }
//            }
//        });


        ImageView mCloseImageView = (ImageView) view
                .findViewById(R.id.btnCloseDialog);
        mCloseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (ConfrimDialog != null) {
                    ConfrimDialog.dismiss();
                }
                Delay(1);
                ifBtnClick = false;
            }
        });

//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);

        ConfrimDialog = builder.create();
        ConfrimDialog.setCanceledOnTouchOutside(false);
        ConfrimDialog.setCancelable(true);
        ConfrimDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                ifBtnClick = false;
                SoftInputRunning = false;
            }
        });
        ConfrimDialog.show();
        SoftInputRunning = true;
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
        mActivity.setDefaultFragment("3");
    }

    public void onDestroy() {

        Log.e("FragmentChongZhengShenQingDetail", "onDestroy");
        mActivity.setOnBackPressedListener(null);
        SoftInputRunning = false;
        super.onDestroy();
        if (progressDialog != null
                && (!mActivity.isFinishing())) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void Delay(int second) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            public void run() {
                SoftInputRunning = false;
            }
        };
        timer.schedule(task, second * 1000);
    }

}
