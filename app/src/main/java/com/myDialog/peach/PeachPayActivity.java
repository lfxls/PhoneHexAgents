package com.myDialog.peach;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.powertech.R;
import com.common.powertech.activity.LoginActivity;
import com.common.powertech.activity.YajinChongZhiDetailActivity;
import com.common.powertech.bussiness.PULLParse_PeachList_Query;
import com.common.powertech.bussiness.PeachList_Class;
import com.common.powertech.bussiness.PeachSavedList_Class;
import com.common.powertech.bussiness.Request_Peach_Pay;
import com.common.powertech.bussiness.Request_Peach_Query;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;

public class PeachPayActivity extends DialogFragment {

    private ImageButton headClose;
    private ProgressDialog progressDialog;
    private List<PeachList_Class> mTotalPeachList = new ArrayList<PeachList_Class>();
    private List<PeachSavedList_Class> mTotalPeachSavedList = new ArrayList<PeachSavedList_Class>();
    private Fragment peachList, payInf1, payInf2, payConfirm1, payConfirm2;
    private LinearLayout visaLay, mastercardLay, jcbLay, mainList, amexLay;
    private TextView _footerText, amountText;
    private boolean isSavedFlag = false;

    private String pay_type = "", amount = "", unit = "", regist_flag = "", pay_brand = "", cardNo = "", cardHolder = "", expiryM = "", expiryY = "", card_cvv = "", regist_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomProgressDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.connect_checkout_template, container, false);
//			requestWindowFeature(Window.FEATURE_NO_TITLE);  
//			setContentView(R.layout.connect_checkout_template);
//			Bundle bundle = getIntent().getExtras();

        this.amount = getArguments().getString("amount");
        this.unit = "ZAR";
        this.pay_type = "DB";
        visaLay = (LinearLayout) rootView.findViewById(R.id.visaLay);
        visaLay.setOnClickListener(itemClick);
        mastercardLay = (LinearLayout) rootView.findViewById(R.id.mastercardLay);
        mastercardLay.setOnClickListener(itemClick);
        jcbLay = (LinearLayout) rootView.findViewById(R.id.jcbLay);
        jcbLay.setOnClickListener(itemClick);
        amexLay = (LinearLayout) rootView.findViewById(R.id.amexLay);
        amexLay.setOnClickListener(itemClick);
        mainList = (LinearLayout) rootView.findViewById(R.id.mainList);

        headClose = (ImageButton) rootView.findViewById(R.id.connect_checkout_template_header_close);

        _footerText = (TextView) rootView.findViewById(R.id.connect_checkout_template_footer_text);
        amountText = (TextView) rootView.findViewById(R.id.connect_checkout_template_header_amount);
        amountText.setText(amount + " " + unit);

        headClose.setOnClickListener(itemClick);
        //加载支付方式菜单，调用
        callPeachList();
        return rootView;
    }

//	@Override
//	public void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);  
//		setContentView(R.layout.connect_checkout_template);
//		Bundle bundle = getIntent().getExtras();
//		
//		visaLay = (LinearLayout) findViewById(R.id.visaLay);
//		visaLay.setOnClickListener(itemClick);
//		mastercardLay = (LinearLayout) findViewById(R.id.mastercardLay);
//		mastercardLay.setOnClickListener(itemClick);
//		jcbLay = (LinearLayout) findViewById(R.id.jcbLay);
//		jcbLay.setOnClickListener(itemClick);
//		
//		headClose = (ImageButton)findViewById(R.id.connect_checkout_template_header_close);
//		_footerText = (TextView)findViewById(R.id.connect_checkout_template_footer_text);
//		peachList =(Fragment) getFragmentManager().findFragmentById(R.layout.connect_checkout_insert_cc_payment_data);
//		payInf1 =(Fragment) getFragmentManager().findFragmentById(R.layout.connect_checkout_insert_cc_payment_data);
//		
//		headClose.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onDestroy();
//			}
//		});
//		//加载支付方式菜单，调用
//		callPeachList();
//		
//		
//	}


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((YajinChongZhiDetailActivity) getActivity()).dismissPeachDialog();
    }


    OnClickListener itemClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.visaLay) {
                //切换到1信息输入框
                paymentPayInf1();
                pay_brand = "VISA";
            } else if (v.getId() == R.id.mastercardLay) {
                //切换到1信息输入框
                pay_brand = "MASTER";
                paymentPayInf1();
            } else if (v.getId() == R.id.amexLay) {
                //切换到1信息输入框

                pay_brand = "AMEX";
                paymentPayInf1();

            } else if (v.getId() == R.id.jcbLay) {//标题栏
                //切换到2信息输入框
                pay_brand = "DB";
//				paymentPayInf2();
            } else if (v.getId() == R.id.connect_checkout_template_header_close) {
                onDestroy();
            }
        }
    };

    Handler mhandler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (progressDialog != null /*&& (!mActivity.isFinishing())*/) {
                progressDialog.dismiss();
                // progressDialog = null;
            }

            switch (msg.what) {
                case 0:
                    // 联网失败
                case 1:
                    InputStream in;
                    try {
                        in = new ByteArrayInputStream(
                                GlobalParams.RETURN_DATA.getBytes("UTF-8"));

                        String rspCode = Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPCOD>", "</RSPCOD>");
                        String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPMSG>", "</RSPMSG>");
                        if (rspCode.equals("00000")) {
                            //成功
                            Toast.makeText(getActivity(), getString(R.string.main_xiaoshoudingdan_select_chenggong),
                                    Toast.LENGTH_LONG).show();
                            createSuccessDialog(mRspMeg);


                        } else {
                            if (rspCode.equals("99999") || rspCode.equals("900001")) {
                                createRetryDialog(mRspMeg);
                            } else if (rspCode.equals("00011")) {
                                Activity mactivity = getActivity();
                                SystemUtil.setGlobalParamsToNull(mactivity);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(mactivity, LoginActivity.class);
                                mactivity.startActivity(intent);
                            } else {
                                createRetryDialog(mRspMeg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

        }


    };

    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            if (progressDialog != null /*&& (!mActivity.isFinishing())*/) {
                progressDialog.dismiss();
                // progressDialog = null;
            }
            switch (msg.what) {
                case 0:
                    // 联网失败
                case 1:
                    InputStream in;
                    try {
                        in = new ByteArrayInputStream(
                                GlobalParams.RETURN_DATA.getBytes("UTF-8"));

                        String rspCode = Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPCOD>", "</RSPCOD>");
                        String mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA, "<RSPMSG>", "</RSPMSG>");
                        if (rspCode.equals("00000")) {
                            PULLParse_PeachList_Query.getPeach(in);
                            mTotalPeachList = PULLParse_PeachList_Query.getPeachList();
                            mTotalPeachSavedList = PULLParse_PeachList_Query.getPeachSavedList();
                            //加载列表.
                            updateList();
                        } else {
                            if (rspCode.equals("00011")) {
                                Activity mactivity = getActivity();
                                SystemUtil.setGlobalParamsToNull(mactivity);
                                DummyContent.ITEM_MAP.clear();
                                DummyContent.ITEMS.clear();
                                Intent intent = new Intent(mactivity, LoginActivity.class);
                                mactivity.startActivity(intent);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }

    };

    private void createSuccessDialog(String mRspMeg) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(mRspMeg);
        builder.setTitle(getString(R.string.connect_checkout_layout_title_success));
        builder.setPositiveButton(R.string.connect_checkout_layout_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        onDestroy();
                    }

                });
        builder.show();
    }

    private void createRetryDialog(String rspmsg) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(rspmsg);
        builder.setTitle(R.string.connect_checkout_layout_title_faild);
        builder.setPositiveButton(R.string.connect_checkout_layout_retry,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }

                });

        // builder.setPositiveButton(R.string.query, new OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // dialog.dismiss();
        // Main.this.finish();
        // 　　 }
        // 　　 });
        builder.setNegativeButton(R.string.connect_checkout_layout_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onDestroy();
            }
        });
        builder.create().show();
    }

    private void updateList() {
//		Fragment newFragment = new ChoosePaymentMethodFragment(this, _supportedMethods,
//				_storedAccounts);
//		LayoutInflater inflater;
//		View rootView = inflater.inflate(R.layout.fragment_xiaoshoudingdan_main,
//				rootView, false);
//		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//		transaction.replace(R.id.connect_checkout_template_content, peachList);
//		transaction.commit();
        for (PeachList_Class peach : mTotalPeachList) {
            if (peach.getEnm_Dat_Opt().equals("01") && peach.getEnm_Dat_Des().equals("VISA")) {
                visaLay.setVisibility(View.VISIBLE);
            }
            if (peach.getEnm_Dat_Opt().equals("02") && peach.getEnm_Dat_Des().equals("MASTER")) {
                mastercardLay.setVisibility(View.VISIBLE);
            }
            if (peach.getEnm_Dat_Opt().equals("03") && peach.getEnm_Dat_Des().equals("AMEX")) {
                amexLay.setVisibility(View.VISIBLE);
            }
            if (peach.getEnm_Dat_Opt().equals("04") && peach.getEnm_Dat_Des().equals("Direct Debit")) {
                jcbLay.setVisibility(View.VISIBLE);
            }

        }

        for (PeachSavedList_Class peachS : mTotalPeachSavedList) {

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setBackground(getResources().getDrawable(R.drawable.background_boder));

            layout.setLayoutParams(new LinearLayout.LayoutParams(mainList.getWidth(), 52));
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
            linearParams.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, getResources().getDisplayMetrics()));

            layout.setLayoutParams(linearParams);
            layout.setPadding(linearParams.width / 12, linearParams.height / 8, 0, linearParams.height / 8);

//			= (LinearLayout) inflater.inflate(  
//			        R.layout.peach_saved, null).findViewById(R.id.newSavedLay);  
            // 将布局加入到当前布局中
            ImageView iv = new ImageView(getActivity());
            layout.addView(iv);
//			iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            iv.setImageResource(R.drawable.banklogo);
            TextView tx = new TextView(getActivity());
            layout.addView(tx);
            tx.setText(peachS.getHOLDER_NAME() + "  " + peachS.getTYPE_NAME() + "\n*-" + peachS.getCARD_NO());
//			tx.setTypeface(tf,ConnectCheckout.Text.Tax );
//	        android:layout_marginLeft="5dp"
//	        android:gravity="center_vertical"
//	        android:textSize="18dp"
            tx.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

            tx.setGravity(Gravity.CENTER);

//			((TextView)layout.findViewById(R.id.newSavedText)).setText(peachS.getHOLDER_NAME()+"  "+peachS.getPEACH_TYPE());
            final PeachSavedList_Class fPeachS = peachS;
            mainList.addView(layout);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    mainList.setVisibility(View.GONE);
                    paymentCreditCardDataProvided(fPeachS.getHOLDER_NAME(), fPeachS.getCARD_NO(), fPeachS.getEXPIRY_DATE().substring(0, 4),
                            fPeachS.getEXPIRY_DATE().substring(4, fPeachS.getEXPIRY_DATE().length()), "", false);
                    regist_id = fPeachS.getREGIST_ID();
                    pay_brand = fPeachS.getTYPE_NAME();
                }
            });
        }
    }


    private void callPeachList() {
        String data = null;
        data = Request_Peach_Query.getRequsetXML();
        Client.SendData("PPeachPayList", data, mhandler);
    }


    public void paymentCreditCardDataProvided(String name, String ccNumber, String expiryMonth,
                                              String expiryYear, String cvv, boolean flag) {
        cleanmeters();
        this.cardHolder = name;
        this.cardNo = ccNumber;
        this.expiryM = expiryMonth;
        this.expiryY = expiryYear;
        this.card_cvv = cvv;

        regist_flag = (flag == true ? "1" : "0");

        String strippedNumber = ccNumber.substring(ccNumber.length() > 4 ? ccNumber.length() - 4
                : 0);
        ReviewCreditCardPaymentDataFragment newFragment = new ReviewCreditCardPaymentDataFragment(name, ccNumber, expiryMonth, expiryYear, cvv, flag);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.connect_checkout_template_content, newFragment);
        transaction.commit();
        _footerText.setText(getString(R.string.connect_checkout_layout_text_footer_step2));
    }


//	public void ReviewDirectDebitPaymentDataFragment( String name, String accountNumberStripped, String bankNumber,
//			String bankCountry,boolean flag){
//		cleanmeters();
//		this.cardHolder = name;
//		this.cardNo = accountNumberStripped;
//		regist_flag = (flag==true?"1":"0");
//		
//
//		ReviewDirectDebitPaymentDataFragment newFragment =new ReviewDirectDebitPaymentDataFragment(name, accountNumberStripped, bankNumber, bankCountry);
//		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//		transaction.addToBackStack(null);
//		transaction.replace(R.id.connect_checkout_template_content, newFragment);
//		transaction.commit();
//		_footerText.setText(getString(R.string.connect_checkout_layout_text_footer_step2));
//		
//		
//	}

    public void paymentPayInf1() {
        PayInfFragment1 newFragment = new PayInfFragment1();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
//				R.anim.slide_in_left, R.anim.slide_out_right);
//		transaction.replace(R.id.connect_checkout_template_content, newFragment);
        transaction.addToBackStack(null);
        transaction.replace(R.id.connect_checkout_template_content, newFragment);
        transaction.commit();
        mainList.setVisibility(View.GONE);
        _footerText.setText(getString(R.string.connect_checkout_layout_text_footer_step1));
    }

//	public void paymentPayInf2() {
//
//
//		PayInfFragment2 newFragment =new PayInfFragment2();
//		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
////		transaction.replace(R.id.connect_checkout_template_content, newFragment);
//		transaction.addToBackStack(null);
//		transaction.replace(R.id.connect_checkout_template_content, newFragment);
//		transaction.commit();
//		mainList.setVisibility(View.GONE);
//		_footerText.setText(getString(R.string.connect_checkout_layout_text_footer_step1));
//	}


    public void setRequestPeachPay(boolean isSavedFlag) {
        //String amount,String unit,String regist_flag,String pay_brand,String cardNo,String cardHolder,String expiryM,String expiryY,String card_cvv ,
        createDialog();
        progressDialog.setTitle(getString(R.string.progress_tishi_title));
        progressDialog.setMessage(getString(R.string.progress_conducting));
        // 设置进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 是否可以按下退回键取消
        progressDialog.setCancelable(false);
        progressDialog.show();

        regist_flag = isSavedFlag ? "1" : "0";
        //amount,unit,regist_flag,pay_brand,cardNo,cardHolder,expiryM,expiryY,card_cvv;
        Request_Peach_Pay.setAmount(amount);
        Request_Peach_Pay.setCard_cvv(card_cvv);
        Request_Peach_Pay.setCardHolder(cardHolder);
        Request_Peach_Pay.setCardNo(cardNo);
        Request_Peach_Pay.setExpiryM(expiryM);
        Request_Peach_Pay.setExpiryY(expiryY);
        Request_Peach_Pay.setPay_brand(pay_brand);
        Request_Peach_Pay.setRegist_flag(regist_flag);
        Request_Peach_Pay.setRegist_id(regist_id);

        Request_Peach_Pay.setUnit(unit);
//		Request_Peach_Pay.setIsSaveFlag(String.valueOf(isSavedFlag));

        String data = Request_Peach_Pay.getRequsetXML();
        Client.SendData("PPeachPay", data, mhandler2);
//		requestPeachPay.setContext(ct);
    }

    private void cleanmeters() {
        this.cardHolder = "";
        this.cardNo = "";
        this.expiryM = "";
        this.expiryY = "";
        this.card_cvv = "";
        this.regist_flag = "";
        this.regist_id = "";
    }

    private void createDialog() {
        progressDialog = MyProgressDialog.createProgressDialog(getActivity(),
                GlobalParams.PROGRESSDIALOG_TIMEOUT,
                new MyProgressDialog.OnTimeOutListener() {

                    @Override
                    public void onTimeOut(MyProgressDialog dialog) {
                        SystemUtil.displayToast(getActivity(),
                                R.string.progress_timeout);
                        if (dialog != null && (!getActivity().isFinishing())) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }

                });
    }


    public String getPay_brand() {
        return pay_brand;
    }

}
