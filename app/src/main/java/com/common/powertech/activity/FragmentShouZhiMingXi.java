package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import printUtils.gprinter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.common.powertech.ItemListActivity;
import com.common.powertech.R;
import com.common.powertech.bussiness.BillIncome_Class;
import com.common.powertech.bussiness.PULLParse_Income_Query;
import com.common.powertech.bussiness.Request_Income_Query;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullRefreshLayout;
import com.common.powertech.widget.PullRefreshLayout.OnPullListener;
import com.common.powertech.widget.PullRefreshLayout.OnPullStateListener;
import com.common.powertech.widget.PullUpListView;
import com.common.powertech.widget.PullUpListView.MyPullUpListViewCallBack;
import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.myDialog.CustomDialog;
import com.myDialog.CustomProgressDialog;
import com.myDialog.mySpinnerItem;

/**
 * 广东天波信息技术股份有限公司 功能：收支明细Fragment 作者:ouyangguozhao 日期:2015-11-6
 */
public class FragmentShouZhiMingXi extends Fragment {
	private static String TAG = "FragmentShouZhiMingXi";
	private static final String NUMPERPAG = "10";// 每页请求数据条
	private View rootView;
	private PullUpListView lv;
	private MyIncomeDetailAdapter myIncomeDetailAdapter;
	private Button mCurrentDayBtn, mCurrentWeekBtn, mCurrentMonthBtn,
			mSetTimeBtn,mPrintBtn;
	private Spinner balanceType;
	private List<BillIncome_Class> mCurrentPageBillList;
	private CustomProgressDialog progressDialog;
	private List<BillIncome_Class> mTotalBilist = new ArrayList<BillIncome_Class>();

	// 缓存
	// private List<BillIncome_Class> mTodayBilist = new
	// ArrayList<BillIncome_Class>();
	// private List<BillIncome_Class> mCurrentWeekBilist = new
	// ArrayList<BillIncome_Class>();
	// private List<BillIncome_Class> mCurrentMonthBilist = new
	// ArrayList<BillIncome_Class>();
	private DatePickerDialog mDatePickerDialog;
	private int mYear = 0, mMonth = 0, mDay = 0;
	private String mMinDate = "", mIMinDate = "";
	private String mMaxDate = "", mIMaxDate = "";
	// 设置全局开始日期和结束日期
	private static String mStartDate, mEndDate;
	private int mCurrentPage = 1;
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
	private AlertDialog mSetTimeDialog;
	private String mSelectTag = "1";
	private boolean isFullContent = false;
	private ItemListActivity mActivity;
	private String mStartTimePreferenceText = "";
	private String mEndTimePreferenceText = "";
	private String ticket = "";
    private gprinter gprinter = new gprinter();
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private PrinterServiceConnection conn = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (ItemListActivity) getActivity();
		Log.e(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_shouzhimingxi_main,
				container, false);
		initUI();
        gprinter.nGpService = null;
        connection();
		return rootView;
	}
	private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(mActivity, GpPrintService.class);
        mActivity.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }
    public class PrinterServiceConnection implements ServiceConnection {
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

	public void onResume() {
		super.onResume();
		mActivity.setOnBackPressedListener(null);
	}

	public void initUI() {
		if (SystemUtil.getLocalLanguage(mActivity).equalsIgnoreCase("en")) {
			if ("TPS390".equalsIgnoreCase(GlobalParams.DeviceModel)) {
				TextView tv1 = (TextView) rootView.findViewById(R.id.tv1);
				tv1.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_jiaoyidanhao_port));
				TextView tv2 = (TextView) rootView.findViewById(R.id.tv2);
				tv2.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_jiaoyileixing_port));
				TextView tv3 = (TextView) rootView.findViewById(R.id.tv3);
				tv3.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_jiaoyishijian_port));
				TextView tv4 = (TextView) rootView.findViewById(R.id.tv4);
				tv4.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_shouru_port));
				TextView tv5 = (TextView) rootView.findViewById(R.id.tv5);
				tv5.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_zhichu_port));
				TextView tv6 = (TextView) rootView.findViewById(R.id.tv6);
				tv6.setText(mActivity
						.getString(R.string.main_shouzhimingxi_listtv_yuer_port));
			}
		}

		mCurrentDayBtn = (Button) rootView
				.findViewById(R.id.btn_main_shouzhimingxi_dangri);
		mCurrentDayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setQueryButtonUnable();
				// 收支明细查询操作 -- 本日
				mTotalBilist.clear();
				// mCurrentPageBillList.clear();
				myIncomeDetailAdapter.notifyDataSetChanged();
				mCurrentPage = 1;
				String currentDate = SystemUtil.getCurrentDate();
				mStartDate = currentDate;
				mEndDate = currentDate;
				requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG,
						mStartDate, mEndDate);
			}
		});

		mCurrentWeekBtn = (Button) rootView
				.findViewById(R.id.btn_main_shouzhimingxi_benzhou);
		mCurrentWeekBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setQueryButtonUnable();
				// 收支明细查询操作 -- 本周
				mTotalBilist.clear();
				// mCurrentPageBillList.clear();
				myIncomeDetailAdapter.notifyDataSetChanged();
				mCurrentPage = 1;
				mStartDate = SystemUtil.getCurrentMonday();
				mEndDate = SystemUtil.getPreviousSunday();
				requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG,
						mStartDate, mEndDate);
			}
		});
		mCurrentMonthBtn = (Button) rootView
				.findViewById(R.id.btn_main_shouzhimingxi_dangyue);
		mCurrentMonthBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setQueryButtonUnable();
				// 收支明细查询操作 -- 本月
				mTotalBilist.clear();
				// mCurrentPageBillList.clear();
				myIncomeDetailAdapter.notifyDataSetChanged();
				mCurrentPage = 1;
				mStartDate = SystemUtil.getMinMonthDate();
				mEndDate = SystemUtil.getMaxMonthDate();
				requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG,
						mStartDate, mEndDate);
			}
		});
		balanceType = (Spinner)rootView.findViewById(R.id.btn_main_shouzhimingxi_type);
		List<mySpinnerItem> iditem = new ArrayList<mySpinnerItem>();
		iditem.add(new mySpinnerItem("",getString(R.string.main_shouzhimingxi_spinner_type)));
		iditem.add(new mySpinnerItem("11",getString(R.string.main_shouzhimingxi_shouzhileixing_goudianxiaofei)));
		iditem.add(new mySpinnerItem("12",getString(R.string.main_shouzhimingxi_shouzhileixing_yajinchongzhi)));
		iditem.add(new mySpinnerItem("5",getString(R.string.main_shouzhimingxi_shouzhileixing_baozhengjin)));
		iditem.add(new mySpinnerItem("17",getString(R.string.main_shouzhimingxi_shouzhileixing_changhuanfuwufei)));
		iditem.add(new mySpinnerItem("18",getString(R.string.main_shouzhimingxi_shouzhileixing_chanshengfuwufei)));
		iditem.add(new mySpinnerItem("19",getString(R.string.main_shouzhimingxi_shouzhileixing_xiaofeichongzheng)));
		ArrayAdapter adapterid 
					= new ArrayAdapter<mySpinnerItem>(mActivity, android.R.layout.simple_spinner_item, iditem);
		balanceType.setAdapter(adapterid);
		
		setDateTime();
		mPrintBtn = (Button) rootView
				.findViewById(R.id.btn_main_shouzhimingxi_dayin);
		mPrintBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new PrintTask()
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});
		mSetTimeBtn = (Button) rootView
				.findViewById(R.id.btn_main_shouzhimingxi_chaxun);
		mSetTimeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDateSettingDialog(mActivity);
			}
		});
		
		lv = (PullUpListView) rootView.findViewById(R.id.listShouzhimingxiView);
		lv.initBottomView();
		myIncomeDetailAdapter = new MyIncomeDetailAdapter(mActivity);
		lv.setAdapter(myIncomeDetailAdapter);
		lv.setMyPullUpListViewCallBack(new MyPullUpListViewCallBack() {

			@Override
			public void scrollBottomState() {
				// 拉到底部继续向上拉动加载
				if (!mInLoading) {
					if (isFullContent) {
						mCurrentPage += 1;
					}
					Log.e(TAG, "scrollBottomState -- " + "mStartDate = "
							+ mStartDate + " mEndDate=" + mEndDate);
					requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG,
							mStartDate, mEndDate);
					mInLoading = true;
				}
			}
		});

		mPullLayout = (PullRefreshLayout) rootView
				.findViewById(R.id.pull_container);
		mPullLayout.setOnActionPullListener(new OnPullListener() {

			@Override
			public void onSnapToTop() {
				if (!mInRefreshing) {
					showRefreshModel();
					// new RefreshDataTask().execute();
					// 拖到顶部下拉刷新上一页
					if (mCurrentPage > 1) {
						mCurrentPage -= 1;
						requestIncomeQuery(String.valueOf(mCurrentPage),
								NUMPERPAG, mStartDate, mEndDate);
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
		mPullLayout.setOnPullStateChangeListener(new OnPullStateListener() {

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
		mActionText.setText(R.string.note_pull_down);

		// mTotalBilist.clear();
		mStartDate = SystemUtil.getCurrentDate();
		mEndDate = SystemUtil.getCurrentDate();
		requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG, mStartDate,
				mEndDate);// 进去以后需要显示当日收支明细

	}

	private void setQueryButtonUnable() {
		mCurrentDayBtn.setEnabled(false);
		mCurrentMonthBtn.setEnabled(false);
		mCurrentWeekBtn.setEnabled(false);
		mSetTimeBtn.setEnabled(false);
	}

	private void setQueryButtonEnable() {
		mCurrentDayBtn.setEnabled(true);
		mCurrentMonthBtn.setEnabled(true);
		mCurrentWeekBtn.setEnabled(true);
		mSetTimeBtn.setEnabled(true);
	}

	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		// updateDisplay();
	}

	Handler mhandler = new Handler() {

		public void handleMessage(Message msg) {
			if (progressDialog != null && (!mActivity.isFinishing())) {
				progressDialog.dismiss();
				// progressDialog = null;
			}
			setQueryButtonEnable();
			// 处理消息
			switch (msg.what) {
			case 0:
				// 联网失败
				// 没有加载到数据，页码返回到当前页
				if (isFullContent) {
					if (mInRefreshing) {
						mCurrentPage += 1;
					} else if (mInLoading) {
						mCurrentPage -= 1;
					}
				}
				isFullContent = false;
				break;
			case 1:
				// 联网成功
				try {
					InputStream in = new ByteArrayInputStream(
							GlobalParams.RETURN_DATA.getBytes("UTF-8"));
					if (GlobalParams.APINAME.equals("PBillIncomeQuery")) {
						// Log.d(TAG, GlobalParams.RETURN_DATA);
						in = new ByteArrayInputStream(
								GlobalParams.RETURN_DATA.getBytes("UTF-8"));
						String responeCode = Client.Parse_XML(
								GlobalParams.RETURN_DATA, "<RSPCOD>",
								"</RSPCOD>");
						String responeMsg = Client.Parse_XML(
								GlobalParams.RETURN_DATA, "<RSPMSG>",
								"</RSPMSG>");
						// 小票信息
	                    ticket = Client.Parse_XML(GlobalParams.RETURN_DATA, "<TICKET>", "</TICKET>");
						if (responeCode.equals("00000")) {
							// TODO 排序功能
							/*
							 * mTotalBilist.addAll(mCurrentPageBillList);
							 * mCurrentPage += 1; Log.e("mTotalBilist.size()=",
							 * mTotalBilist.size() + "");
							 */
							// 加载成功
							mCurrentPageBillList = PULLParse_Income_Query
									.getBIList(in);
							mTotalBilist = mCurrentPageBillList;
							if (mTotalBilist.size() < Integer
									.valueOf(NUMPERPAG)) {
								isFullContent = false;
							} else {
								isFullContent = true;
							}
							myIncomeDetailAdapter.notifyDataSetChanged();
							if (mInLoading) {
								lv.setSelection(mTotalBilist.size() - 1);
							}
							// 循环加载
							/*
							 * requestIncomeQuery(String.valueOf(mCurrentPage),
							 * NUMPERPAG, mStartDate, mEndDate);
							 */
						} else {
							// 服务器返回系统超时，返回到登录页面
							if (responeCode.equals("00011")) {
								Toast.makeText(mActivity, responeMsg,
										Toast.LENGTH_LONG).show();
								SystemUtil.setGlobalParamsToNull(mActivity);
						        DummyContent.ITEM_MAP.clear();
						        DummyContent.ITEMS.clear();
								Intent intent = new Intent(mActivity, LoginActivity.class);
                                mActivity.startActivity(intent);
							}
							// 没有加载到数据，页码返回到当前页
							if (mInRefreshing) {
								if (mInRefreshing) {
									mCurrentPage += 1;
								} else if (mInLoading) {
									mCurrentPage -= 1;
								}
							}
							isFullContent = false;
							myIncomeDetailAdapter.notifyDataSetChanged();
							if (responeMsg.equalsIgnoreCase("")) {
								SystemUtil
										.displayToast(
												mActivity,
												R.string.shoufeixiangqing_wangluoyichang);
							} else {
								SystemUtil.displayToast(mActivity, responeMsg);
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				// btnprev.setEnabled(true);
				// btnnext.setEnabled(true);
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
							new Date(System.currentTimeMillis()))));
		}
	}

	/**
	 * 添加收支明细数据适配器
	 * 
	 */
	private class MyIncomeDetailAdapter extends BaseAdapter {
		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
		private TextView tv1, tv2, tv3, tv4, tv5, tv6 = null;

		// private List<BillIncome_Class> mList = null;

		/** 构造函数 */
		public MyIncomeDetailAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			// this.mList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTotalBilist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = (View) mInflater.inflate(R.layout.list_shouzhimingxi,
					null);
			tv1 = (TextView) convertView.findViewById(R.id.tv1);
			tv2 = (TextView) convertView.findViewById(R.id.tv2);
			tv3 = (TextView) convertView.findViewById(R.id.tv3);
			tv4 = (TextView) convertView.findViewById(R.id.tv4);
			tv5 = (TextView) convertView.findViewById(R.id.tv5);
			tv6 = (TextView) convertView.findViewById(R.id.tv6);
			tv1.setText(mTotalBilist.get(position).Get_PRDORDNO());
			int type = Integer.valueOf(mTotalBilist.get(position).Get_TXNTYP());
			String txnTyp = "";
			switch (type) {
			case 5:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_baozhengjin);
				break;
			case 11:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_goudianxiaofei);
				break;
			case 12:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_yajinchongzhi);
				break;
			case 17:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_changhuanfuwufei);
				break;
			case 18:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_chanshengfuwufei);
				break;
			case 19:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_xiaofeichongzheng);
				break;
			case 20:
				txnTyp = mActivity
						.getString(R.string.main_shouzhimingxi_shouzhileixing_fanyongchongzheng);
				break;

			default:
				break;
			}
			tv2.setText(txnTyp);
			tv3.setText(mTotalBilist.get(position).Get_DATETIME());
			tv4.setText(mTotalBilist.get(position).Get_IN());
			tv5.setText(mTotalBilist.get(position).Get_OUT());
			tv6.setText(mTotalBilist.get(position).Get_BANLANCE());
			return convertView;
		}
	}

	public void showDateSettingDialog(Context mContext) {
		setQueryButtonUnable();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_time_select, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		// builder.setTitle(R.string.dialog_title_accordingtotime);
		builder.setView(view);
		mSetTimeDialog = builder.create();
		mSetTimeDialog.setCancelable(false);
		mSetTimeDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				setQueryButtonEnable();
			}
		});
		ImageView mCloseView = (ImageView) view
				.findViewById(R.id.btnCloseDialog);
		mCloseView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSetTimeDialog.dismiss();
			}
		});
		final EditText mBeginEt = (EditText) view.findViewById(R.id.begin_et);
		mBeginEt.setInputType(InputType.TYPE_NULL);
		if (!mStartTimePreferenceText.equalsIgnoreCase("")) {
			mBeginEt.setText(mStartTimePreferenceText);
			String timeStr[] = mStartTimePreferenceText.split("-");
			mYear = Integer.valueOf(timeStr[0]);
			mMonth = Integer.valueOf(timeStr[1]) - 1;
			mDay = Integer.valueOf(timeStr[2]);
		} else {
			mBeginEt.setText(SystemUtil.getCurrentDate());
		}
		mBeginEt.setFocusable(true);
		mBeginEt.requestFocus();
		mBeginEt.setKeyListener(null);
		mSelectTag = "1";
		final EditText mEndEt = (EditText) view.findViewById(R.id.end_et);
		mEndEt.setInputType(InputType.TYPE_NULL);
		if (!mEndTimePreferenceText.equalsIgnoreCase("")) {
			mEndEt.setText(mEndTimePreferenceText);
		} else {
			mEndEt.setText(SystemUtil.getCurrentDate());
		}
		mEndEt.setKeyListener(null);
		final DatePicker mDatePicker = (DatePicker) view
				.findViewById(R.id.date_picker);
		mDatePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		final String[] mDisplayMonths = { "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "10", "11", "12" };
		final String language = SystemUtil.getLocalLanguage(mActivity);
		Button mCommitBtn = (Button) view.findViewById(R.id.commitBtn);
		Button mtodayBtn = (Button) view.findViewById(R.id.todayBtn);
		mtodayBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mBeginEt.isFocused() && !mEndEt.isFocused()){
					mBeginEt.setText(SystemUtil.getCurrentDate());
					String timeStr[] = mBeginEt.getText().toString().split("-");
					mDatePicker.updateDate(Integer.valueOf(timeStr[0]), 
							Integer.valueOf(timeStr[1])-1, Integer.valueOf(timeStr[2]));
				}else if(!mBeginEt.isFocused() && mEndEt.isFocused()){
					mEndEt.setText(SystemUtil.getCurrentDate());
					String timeStr[] = mEndEt.getText().toString().split("-");
					mDatePicker.updateDate(Integer.valueOf(timeStr[0]), 
							Integer.valueOf(timeStr[1])-1, Integer.valueOf(timeStr[2]));
				}else{
					String today = SystemUtil.getCurrentDate();
					mBeginEt.setText(today);
					mEndEt.setText(today);
					String timeStr[] = today.split("-");
					mDatePicker.updateDate(Integer.valueOf(timeStr[0]),
							Integer.valueOf(timeStr[1])-1, Integer.valueOf(timeStr[2]));
					
				}
			}
		});
		mCommitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTotalBilist.clear();
				myIncomeDetailAdapter.notifyDataSetChanged();
				String startT = mBeginEt.getText().toString();
				String endT = mEndEt.getText().toString();
				if (startT.equalsIgnoreCase("") || endT.equalsIgnoreCase("")) {
					// if(startT.equalsIgnoreCase("")){
					// mBeginEt.setFocusable(true);
					// mBeginEt.requestFocus();
					// }else{
					mEndEt.setFocusable(true);
					mEndEt.requestFocus();
					mSelectTag = "2";
					// }
					SystemUtil.displayToast(mActivity,
							R.string.shoudianxiangqing_error1);
					return;
				}
				mStartTimePreferenceText = startT;
				mEndTimePreferenceText = endT;
				int startTime = Integer.valueOf(startT.replaceAll("-", ""));
				int endTime = Integer.valueOf(endT.replaceAll("-", ""));
				if (endTime < startTime) {
					mEndEt.setFocusable(true);
					mEndEt.requestFocus();
					mSelectTag = "2";
					int day = mDatePicker.getDayOfMonth();
					int mouth = mDatePicker.getMonth() + 1;
					int year = mDatePicker.getYear();
					String timeStr[] = mEndEt.getText().toString().split("-");
					if (year != Integer.valueOf(timeStr[0])
							|| mouth != Integer.valueOf(timeStr[1])
							|| day != Integer.valueOf(timeStr[2])) {
						mDatePicker.updateDate(Integer.valueOf(timeStr[0]),
								Integer.valueOf(timeStr[1]) - 1,
								Integer.valueOf(timeStr[2]));
					}
					SystemUtil.displayToast(mActivity,
							R.string.main_shouzhimingxi_select_warm);
					return;
				}
				mCurrentPage = 1;
				mStartDate = startT;
				mEndDate = endT;
				requestIncomeQuery(String.valueOf(mCurrentPage), NUMPERPAG,
						startT, endT);
				mSetTimeDialog.dismiss();
			}
		});
		mDatePicker.init(mYear, mMonth, mDay, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker arg0, int year, int mouth,
					int day) {
				int maxVale=((NumberPicker) ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0))
						.getChildAt(0)).getChildAt(0)).getMaxValue();
				if(maxVale<12){
					((NumberPicker) ((ViewGroup) ((ViewGroup) arg0
							.getChildAt(0)).getChildAt(0)).getChildAt(0))
							.setDisplayedValues(mDisplayMonths);
				}else{
					((NumberPicker) ((ViewGroup) ((ViewGroup) arg0
							.getChildAt(0)).getChildAt(0)).getChildAt(1))
							.setDisplayedValues(mDisplayMonths);
				}
				if (mSelectTag.equalsIgnoreCase("1")) {
					mBeginEt.setText(new StringBuilder()
							.append(year)
							.append("-")
							.append((mouth + 1) < 10 ? "0" + (mouth + 1)
									: (mouth + 1)).append("-")
							.append((day < 10) ? "0" + day : day));
				} else if (mSelectTag.equalsIgnoreCase("2")) {
					mEndEt.setText(new StringBuilder()
							.append(year)
							.append("-")
							.append((mouth + 1) < 10 ? "0" + (mouth + 1)
									: (mouth + 1)).append("-")
							.append((day < 10) ? "0" + day : day));
				}
			}
		});
		int maxVale=((NumberPicker) ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0))
				.getChildAt(0)).getChildAt(0)).getMaxValue();
		if(maxVale<12){
			((NumberPicker) ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0))
					.getChildAt(0)).getChildAt(0))
					.setDisplayedValues(mDisplayMonths);
		}else{
			((NumberPicker) ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0))
					.getChildAt(0)).getChildAt(1))
					.setDisplayedValues(mDisplayMonths);
		}
		mBeginEt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mSelectTag = "1";
				mBeginEt.setFocusable(true);
				mBeginEt.requestFocus();
				int day = mDatePicker.getDayOfMonth();
				int mouth = mDatePicker.getMonth() + 1;
				int year = mDatePicker.getYear();
				String timeStr[] = mBeginEt.getText().toString().split("-");
				if (year != Integer.valueOf(timeStr[0])
						|| mouth != Integer.valueOf(timeStr[1])
						|| day != Integer.valueOf(timeStr[2])) {
					mDatePicker.updateDate(Integer.valueOf(timeStr[0]),
							Integer.valueOf(timeStr[1]) - 1,
							Integer.valueOf(timeStr[2]));
				}
				return true;
			}
		});
		mEndEt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mSelectTag = "2";
				mEndEt.setFocusable(true);
				mEndEt.requestFocus();
				int day = mDatePicker.getDayOfMonth();
				int mouth = mDatePicker.getMonth() + 1;
				int year = mDatePicker.getYear();
				String timeStr[] = mEndEt.getText().toString().split("-");
				if (year != Integer.valueOf(timeStr[0])
						|| mouth != Integer.valueOf(timeStr[1])
						|| day != Integer.valueOf(timeStr[2])) {
					mDatePicker.updateDate(Integer.valueOf(timeStr[0]),
							Integer.valueOf(timeStr[1]) - 1,
							Integer.valueOf(timeStr[2]));
				}
				return true;
			}
		});

		mSetTimeDialog.show();
	}

	private void createDialog() {
		progressDialog = CustomProgressDialog.createProgressDialog(mActivity,
				GlobalParams.PROGRESSDIALOG_TIMEOUT,
				new CustomProgressDialog.OnTimeOutListener() {

					@Override
					public void onTimeOut(CustomProgressDialog dialog) {
						SystemUtil.displayToast(mActivity,
								R.string.progress_timeout);
						if (dialog != null && (!mActivity.isFinishing())) {
							dialog.dismiss();
							dialog = null;
						}

					}

				});
	}

	private void requestIncomeQuery(String pageNum, String numberPag,
			String minDate, String maxDate) {
		createDialog();
		progressDialog.setTitle(getString(R.string.progress_tishi_title));
		progressDialog.setMessage(getString(R.string.progress_conducting));
		// 设置进度条是否不明确
//		progressDialog.setIndeterminate(false);
		// 是否可以按下退回键取消
		progressDialog.setCancelable(false);
		progressDialog.show();

		Request_Income_Query.setContext(mActivity);
		Request_Income_Query.setPagenum(pageNum);
		Request_Income_Query.setNunperpage(numberPag);
		Request_Income_Query.setFromdate(minDate);
		Request_Income_Query.setTodate(maxDate);
		Request_Income_Query.setTXNTYPE(((mySpinnerItem) balanceType.getSelectedItem()).getID());
		String data = Request_Income_Query.getRequsetXML();
		Log.e(TAG, "require -- " + data);
		Client.SendData(GlobalParams.APINAME, data, mhandler);
	}
	
    private class PrintTask extends AsyncTask<Void, Void, String> {
	            @Override
	            protected void onPreExecute() {
	                if (mPrintBtn != null) {
	                	mPrintBtn.setEnabled(false);
	                }
	                if (progressDialog != null && progressDialog.isShowing()) {
	                    progressDialog.dismiss();
	                }
	                createDialog();
	                progressDialog.setTitle(getString(R.string.str_dayin));
	                progressDialog.setMessage(getString(R.string.progress_conducting));
//	                progressDialog.setIndeterminate(false);
	                progressDialog.setCancelable(false);
	                progressDialog.show();
	            }

	            @Override
	            protected String doInBackground(Void... params) {
	    /*            mPrinter.start();
	                mPrinter.printXML(ticket);
	                // 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
	                // -9999 其他错误
	                int printResult = mPrinter.commitOperation();
	                mPrinter.stop();
	                return printResult;*/
		            if(ticket == null || ticket==""){
		            	return "1005";
		            }
		            String ticketstr = "<TICKET>"+ ticket+ "</TICKET>";
	            	gprinter.printXML(ticketstr);
	            	String result = gprinter.commitOperation();
	                return result;
	            }

	            @Override
	            protected void onPostExecute(String result) {
	            	if(result.equals("1005")){
	            	}else{
	            		SystemUtil.displayToast(mActivity,R.string.printer_status_success);
	            	}
	                if (progressDialog != null && progressDialog.isShowing()) {
	                    progressDialog.dismiss();
	                }
	                if (mPrintBtn != null) {
	                	mPrintBtn.setEnabled(true);
	                }
	            }
	        
	    }

}
