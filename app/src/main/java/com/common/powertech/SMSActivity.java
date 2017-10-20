package com.common.powertech;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.powertech.activity.LoginActivity;
import com.common.powertech.dao.BaseDao;
import com.common.powertech.dbbean.JinRiShouDian;
import com.common.powertech.hardwarelayer.Printer;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.widget.MyProgressDialog;

/**
 * 广东天波信息技术股份有限公司 功能：短信售电Activity 作者:ouyangguozhao 日期:2015-12-25
 */

public class SMSActivity extends Activity {
	private static final String TAG = "SMSActivity";
	private EditText mElecMeterNoEt, mAmtEt;
	private Button mSmsBtn, btn_zjtc;
	private TextView tv_user1, tv_jinrishoudian1,
			tv_baozhengjinyue1, tv_clock1, mZX;
	private ImageView imageBattery1, imageWifi1, imageSignal1, imageEthernet1;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private String mElecMeterNo, mAmt, mSms, mEnergyNum, mToken,
			mElecBillBuyOrderNo, mPrintXml;
	private AlertDialog mQuerengoudianDialog, mGoudianchaoshiDialog,
			mGoudianchenggongDialog, mGoudianshibaiDialog;
	private static ProgressDialog progressDialog;
	private String mFailResponeMsg;
	private WifiManager wifiManager;
	private ConnectivityManager connectivityManager;
	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
	private int ISEXIT_SEND_SMS_2 = 0;
	private boolean IS_LOGOUT_SUCCESS = true;
	private String LOGOUT_NUMBER;
	OnBackPressedListener mOnBackPressedListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUtil.setAppLanguageChange(SMSActivity.this);
		// 设置主题样式
		if (GlobalParams.Theme == 1) {
			setTheme(R.style.VioletTheme);
		} else if (GlobalParams.Theme == 2) {
			setTheme(R.style.OrangeTheme);
		}
		setContentView(R.layout.activity_sms_main);
		// 设置打印模板
		String mTemplate = "<TEMP_LIST><TYPE_T>1</TYPE_T><TITLE_T>FT_1|WM_2|HM_2|DQ_AM|JJ_100</TITLE_T><CONTENT_T>DQ_AL|JJ_50</CONTENT_T><TAIL_T>FT_1|WM_2|HM_2|DQ_AM|JJ_200</TAIL_T><TOKEN_T>FT_1|WM_2|HM_2|DQ_AM|JJ_10|FB_T</TOKEN_T><TM2>DQ_AM|TM2</TM2><TM>TM|FP_100</TM><FP>FP_30</FP><FPSPACE>FP_25</FPSPACE></TEMP_LIST>";
		Printer.setTemplet(mTemplate);

		initUI();
	}

	private void initUI() {
		tv_user1 = (TextView) findViewById(R.id.tv_user1);
		tv_user1.setText(GlobalParams.LOGIN_USER_ID);
		tv_clock1 = (TextView) findViewById(R.id.tv_clock1);
		tv_jinrishoudian1 = (TextView) findViewById(R.id.tv_jinrishoudian1);
		tv_baozhengjinyue1 = (TextView) findViewById(R.id.tv_baozhengjinyue1);
		tv_baozhengjinyue1.setText(GlobalParams.CASH_AC_BAL);


		imageBattery1 = (ImageView) findViewById(R.id.imageBattery1);
		imageWifi1 = (ImageView) findViewById(R.id.imageWifi1);

		imageSignal1 = (ImageView) findViewById(R.id.imageSignal1);
		imageEthernet1 = (ImageView) findViewById(R.id.imageEthernet1);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter1.addAction(WifiManager.RSSI_CHANGED_ACTION);
		intentFilter1.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter1.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(broadcastReceiver, intentFilter1);

		mZX = (TextView) findViewById(R.id.zx_tv);
		mZX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Dialog dialogZx = new Dialog(SMSActivity.this,
						R.style.FullHeightDialog);
				LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View dialogZxView = li.inflate(R.layout.dialog_tuichudenglu,
						null);
				dialogZx.setContentView(dialogZxView);
				dialogZx.setCancelable(true);
				dialogZx.show();

				Button btn_scjl = (Button) dialogZxView
						.findViewById(R.id.btn_scjl);
				btn_scjl.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (progressDialog == null) {
							progressDialog = MyProgressDialog
									.createProgressDialog(
											SMSActivity.this,
											180000,
											new MyProgressDialog.OnTimeOutListener() {

												@Override
												public void onTimeOut(
														MyProgressDialog dialog) {
													if (dialog != null) {
														dialog.dismiss();
														dialog = null;
													}
													Toast.makeText(SMSActivity.this, getString(R.string.duanxingoudian_tv_timeout), Toast.LENGTH_LONG).show();
													ISEXIT_SEND_SMS_2 = 0;
													IS_LOGOUT_SUCCESS = false;
												}
											});
						}
						progressDialog
								.setTitle(R.string.login_progressdialog_title);
						progressDialog
								.setMessage(getString(R.string.logout_progressdialog_message_pbilldailyapply));
						progressDialog.setCancelable(false);
						progressDialog.show();
						if (IS_LOGOUT_SUCCESS) {
							LOGOUT_NUMBER = SystemUtil.getElecBillBuyOrderNo();
						}
						String smsSendText = "T*" + GlobalParams.SESSION_ID
								+ "*" + LOGOUT_NUMBER;
						Log.v("短信日结请求内容:", smsSendText);
						Log.v("短信日结请求号码:", GlobalParams.PNO);
						sendMessage(SMSActivity.this, smsSendText,
								GlobalParams.PNO);
						ISEXIT_SEND_SMS_2 = 1;
					}
				});

				ImageView btnCloseDialog = (ImageView) dialogZxView
						.findViewById(R.id.btnCloseDialog);
				btnCloseDialog.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (dialogZx != null && (dialogZx.isShowing())) {
							dialogZx.dismiss();
						}
					}
				});

				btn_zjtc = (Button) dialogZxView.findViewById(R.id.btn_zjtc);
				btn_zjtc.setEnabled(false);
				btn_zjtc.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dialogZx != null && (dialogZx.isShowing())) {
							dialogZx.dismiss();
						}
						Intent mIntent = new Intent(SMSActivity.this,
								LoginActivity.class);
						startActivity(mIntent);
					}

				});
			}
		});

		mSmsBtn = (Button) findViewById(R.id.command);
		mSmsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mElecMeterNo = mElecMeterNoEt.getText().toString();
				mAmt = mAmtEt.getText().toString();
				if (mElecMeterNo.length() == 0) {
					SystemUtil.displayToast(getApplicationContext(),
							R.string.duanxingoudian_inputNo);
					return;
				}
				if (mAmt.length() == 0) {
					SystemUtil.displayToast(getApplicationContext(),
							R.string.shoudianxiangqing_tv_je);
					return;
				}
				// 检测金额格式是否正确，如果以0开头,后面必须加.
				Pattern pattern = Pattern
						.compile("^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");
				Matcher matcher = pattern.matcher(mAmt);
				if (!matcher.matches()) {
					// 金额格式不正确
					SystemUtil.displayToast(getApplicationContext(),
							R.string.shoudianxiangqing_jineshurubuzhengque);
					return;
				}
				showQuerengoudianDialog();
			}
		});

		mTimer = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_clock1.setText(SystemUtil.getCurrentHourAndMinute());
					}
				});
			}
		};
		// 每隔1s获取系统时间并显示在主界面
		mTimer.schedule(mTimerTask, 0, 1000);

		// 从数据库获取今日收费数据，若数据库无数据则显示为0，否则显示数据库数据
		BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
				getApplicationContext(), JinRiShouDian.class);
		if (baseDao.isExists(1)) {
			setTextViewJinRiShouDian();
		} else {
			tv_jinrishoudian1.setText("0/0"
					+ getResources().getString(
							R.string.main_welcome_jingrishoudian_unit));
		}
	}

	public void showQuerengoudianDialog() {
		LayoutInflater inflater = LayoutInflater.from(SMSActivity.this);
		View view = inflater.inflate(R.layout.dialog_sms_querengoudian, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
		builder.setView(view);

		mQuerengoudianDialog = builder.create();
		TextView mDianbiaobianhao = (TextView) view
				.findViewById(R.id.dianbiaobianhao_Tv);
		mDianbiaobianhao.setText(mElecMeterNo);
		TextView mFukuanshu = (TextView) view.findViewById(R.id.fukuanshu_Tv);
		mFukuanshu.setText(mAmt);

		ImageView mCloseImageView = (ImageView) view
				.findViewById(R.id.btnGdcgCloseDialog);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mQuerengoudianDialog != null
						&& mQuerengoudianDialog.isShowing()) {
					mQuerengoudianDialog.dismiss();
				}
			}
		});

		Button mCommitBtn = (Button) view.findViewById(R.id.command_Btn);
		mCommitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mElecBillBuyOrderNo = SystemUtil.getElecBillBuyOrderNo();
				mSms = getBillBuySMS(mElecMeterNoEt.getText().toString(),
						mAmtEt.getText().toString(), GlobalParams.SESSION_ID,
						mElecBillBuyOrderNo);
				// for test
				// mSms="S*007042464888*100*imawfWFHJsaIvx5D9yNj*10213501";
				Log.e(TAG, "mSms = " + mSms);
				sendMessage(SMSActivity.this, mSms, GlobalParams.PNO);
				ISEXIT_SEND_SMS_2 = 1;

				if (progressDialog == null) {
					createDialog();
				}
				progressDialog
						.setTitle(getString(R.string.shoufeixiangqing_tv_querengoudian));
				progressDialog
						.setMessage(getString(R.string.progress_conducting));
				// 设置进度条是否不明确
				progressDialog.setIndeterminate(false);
				// 是否可以按下退回键取消
				progressDialog.setCancelable(false);
				progressDialog.show();

				// Receive Test Message
				// 购电成功：S*00000*10213501*100*0*71302768603504002013
				// 购电失败：S*00001*10213501*系统错误
				// sendMessage(SMSActivity.this,
				// "S*00000*10213501*100*0*71302768603504002013",
				// "18566015760");
			}
		});
		mQuerengoudianDialog.show();
	}

	private void showGoudianchaoshiDialog() {
		LayoutInflater inflater = LayoutInflater.from(SMSActivity.this);
		View view = inflater.inflate(R.layout.dialog_sms_goudianchaoshi, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
		builder.setView(view);

		mGoudianchaoshiDialog = builder.create();
		TextView mDianbiaobianhao = (TextView) view
				.findViewById(R.id.dianbiaobianhao_Tv);
		mDianbiaobianhao.setText(mElecMeterNo);
		TextView mFukuanshu = (TextView) view.findViewById(R.id.fukuanshu_Tv);
		mFukuanshu.setText(mAmt);

		ImageView mCloseImageView = (ImageView) view
				.findViewById(R.id.btnGdcgCloseDialog);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mGoudianchaoshiDialog != null
						&& mGoudianchaoshiDialog.isShowing()) {
					mGoudianchaoshiDialog.dismiss();
				}
			}
		});

		Button mCommitBtn = (Button) view.findViewById(R.id.command_Btn);
		mCommitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 超时重发
				sendMessage(SMSActivity.this, mSms, GlobalParams.PNO);
				ISEXIT_SEND_SMS_2 = 1;
				if (progressDialog == null) {
					createDialog();
				}
				progressDialog
						.setTitle(getString(R.string.shoufeixiangqing_tv_querengoudian));
				progressDialog
						.setMessage(getString(R.string.progress_conducting));
				// 设置进度条是否不明确
				progressDialog.setIndeterminate(false);
				// 是否可以按下退回键取消
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
		});
		mGoudianchaoshiDialog.show();
	}

	private void showGoudianchenggong() {
		LayoutInflater inflater = LayoutInflater.from(SMSActivity.this);
		View view = inflater
				.inflate(R.layout.dialog_sms_goudianchenggong, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
		builder.setView(view);
		mGoudianchenggongDialog = builder.create();

		TextView mDianbiaobianhao = (TextView) view
				.findViewById(R.id.dianbiaobianhao_Tv);
		mDianbiaobianhao.setText(mElecMeterNo);
		TextView mFukuanshu = (TextView) view.findViewById(R.id.fukuanshu_Tv);
		mFukuanshu.setText(mAmt);
		TextView mGoudianliang = (TextView) view
				.findViewById(R.id.goudianliang_Tv);
		mGoudianliang.setText(mEnergyNum);
		TextView mTokenTv = (TextView) view.findViewById(R.id.info_Tv);
		mTokenTv.setText(mToken);

		ImageView mCloseImageView = (ImageView) view
				.findViewById(R.id.btnGdcgCloseDialog);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mGoudianchenggongDialog != null
						&& mGoudianchenggongDialog.isShowing()) {
					mGoudianchenggongDialog.dismiss();
				}
			}
		});

		Button mCommitBtn = (Button) view.findViewById(R.id.command_Btn);
		mCommitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 再次打印
				Printer mPrinter = new Printer();
				mPrinter.start();
				mPrinter.printXML(mPrintXml);
				// 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
				// -9999 其他错误
				int printResult = mPrinter.commitOperation();
				switch (printResult) {
				case 0:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_success);
					break;
				case -1001:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_nopaper);
					break;
				case -1002:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_hot);
					break;
				case -1003:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_full);
					break;
				case -1004:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_noconnect);
					break;
				case -9999:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_other_error);
					break;
				default:
					break;
				}
				mPrinter.stop();
			}
		});
		mGoudianchenggongDialog.show();
	}

	private void showGoudianshibaiDialog() {
		LayoutInflater inflater = LayoutInflater.from(SMSActivity.this);
		View view = inflater.inflate(R.layout.dialog_sms_goudianshibai, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
		builder.setView(view);

		mGoudianshibaiDialog = builder.create();
		TextView mDianbiaobianhao = (TextView) view
				.findViewById(R.id.dianbiaobianhao_Tv);
		mDianbiaobianhao.setText(mElecMeterNo);
		TextView mFukuanshu = (TextView) view.findViewById(R.id.fukuanshu_Tv);
		mFukuanshu.setText(mAmt);

		ImageView mCloseImageView = (ImageView) view
				.findViewById(R.id.btnGdcgCloseDialog);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mGoudianshibaiDialog != null
						&& mGoudianshibaiDialog.isShowing()) {
					mGoudianshibaiDialog.dismiss();
				}
			}
		});

		TextView mCommitBtn = (TextView) view.findViewById(R.id.command_Btn);
		// mCommitBtn.setBackgroundColor(R.color.red);
		mCommitBtn.setText(mFailResponeMsg);
		// mCommitBtn.setEnabled(false);
		// mCommitBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// mGoudianshibaiDialog.dismiss();
		// }
		// });
		mGoudianshibaiDialog.show();
	}

	private String getPrintXML() {
		String xml = "";
		String tk[] = mToken.split("\\|");
		String token = "";
		for (String t : tk) {
			token += "<TOKEN_T>" + t + "</TOKEN_T>";
		}
		String mTM2 = "METERNO" + mElecMeterNo;
		xml = "<TICKET><TYPE_T>1</TYPE_T><TITLE_T>"
				+ getString(R.string.duanxingoudian_ticket_title)
				+ "</TITLE_T><CONTENT_T>"
				+ getString(R.string.duanxingoudian_tv_dianbiaobianhao)
				+ mElecMeterNo + "</CONTENT_T><CONTENT_T>"
				+ getString(R.string.duanxingoudian_fukuanjine) + mAmt
				+ "</CONTENT_T><CONTENT_T>"
				+ getString(R.string.shoufeixiangqing_tv_goudianliang)
				+ mEnergyNum + "</CONTENT_T><CONTENT_T>TOKEN:</CONTENT_T>"
				+ token + "<TM2>" + mTM2 + "</TM2><FPSPACE/><FP/></TICKET>";
		Log.e(TAG, "print xml = " + xml);
		return xml;
	}

	private String getBillBuySMS(String elecmeterNo, String amt,
			String sessionID, String billBuyNo) {
		return "S*" + elecmeterNo + "*" + amt + "*" + sessionID + "*"
				+ billBuyNo;
	}

	private void parseSMS(String sms) {
		String mSMS[] = sms.split("\\*");
		String responeCode;
		String billBuyNo;
		if (mSMS[0].equalsIgnoreCase("S")) {// 购电返回
			if (mQuerengoudianDialog != null
					&& mQuerengoudianDialog.isShowing()) {
				mQuerengoudianDialog.dismiss();
			}
			responeCode = mSMS[1];// 响应码
			billBuyNo = mSMS[2];// 购电序号
			if (responeCode.equalsIgnoreCase("00000")) {// 购电成功
				String payAmt = mSMS[3];// 付款金额
				// 售电记录存数据库并更新主界面
				JinRiShouDian mJinRiShouDian = new JinRiShouDian(
						SystemUtil.getCurrentDate(), payAmt);
				BaseDao<JinRiShouDian, Integer> mJinRiShouDianDao = new BaseDao<JinRiShouDian, Integer>(
						SMSActivity.this, JinRiShouDian.class);
				mJinRiShouDianDao.create(mJinRiShouDian);
				setTextViewJinRiShouDian();

				mEnergyNum = mSMS[4];// 购电量
				mToken = mSMS[5];// token
				mPrintXml = getPrintXML();
				Printer mPrinter = new Printer();
				mPrinter.start();
				mPrinter.printXML(mPrintXml);
				// 0 打印成功 -1001 打印机缺纸 -1002 打印机过热 -1003 打印机接收缓存满 -1004 打印机未连接
				// -9999 其他错误
				int printResult = mPrinter.commitOperation();
				switch (printResult) {
				case 0:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_success);
					break;
				case -1001:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_nopaper);
					break;
				case -1002:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_hot);
					break;
				case -1003:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_full);
					break;
				case -1004:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_noconnect);
					break;
				case -9999:
					SystemUtil.displayToast(SMSActivity.this,
							R.string.printer_status_other_error);
					break;
				default:
					break;
				}
				mPrinter.stop();
				showGoudianchenggong();
			} else {
				mFailResponeMsg = mSMS[3];// 响应消息 失败
				// SystemUtil.displayToast(SMSActivity.this,
				// mFailResponeMsg);
				showGoudianshibaiDialog();
			}
		}
	}

	private BroadcastReceiver sendMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 判断短信是否发送成功
			if (intent.getAction().equals(GlobalParams.SENT_SMS_ACTION)) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					SystemUtil.displayToast(context,
							R.string.duanxingoudian_sent_success);
					break;
				default:
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
						SystemUtil.displayToast(context,
								R.string.duanxingoudian_sent_fail);
					}
					ISEXIT_SEND_SMS_2 = 0;
					break;
				}
			}
		}
	};

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				int level = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				int status = intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);
				switch (status) {
				case BatteryManager.BATTERY_STATUS_CHARGING:
					imageBattery1.setImageResource(R.drawable.battery_charge);
					imageBattery1.setImageLevel((level * 100) / scale);
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					imageBattery1
							.setImageResource(R.drawable.battery_not_charge);
					imageBattery1.setImageLevel((level * 100) / scale);
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					imageBattery1
							.setImageResource(R.drawable.battery_not_charge);
					imageBattery1.setImageLevel((level * 100) / scale);
					break;
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					imageBattery1
							.setImageResource(R.drawable.stat_sys_battery_unknown);
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					int plugged = intent.getIntExtra("plugged", 0);
					if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
						imageBattery1
								.setImageResource(R.drawable.stat_sys_battery_charge_anim100);
					} else {
						imageBattery1
								.setImageResource(R.drawable.stat_sys_battery_100);
					}
					break;
				}
			} else if (intent.getAction().equals(
					WifiManager.RSSI_CHANGED_ACTION)) {
				imageWifi1.setImageResource(R.drawable.wifi_level);
				imageWifi1.setImageLevel(getWifiLevel());
			} else if (intent.getAction().equals(
					ConnectivityManager.CONNECTIVITY_ACTION)) {
				setNetWorkStatus();
			} else if (intent.getAction().equals(
					GlobalParams.UPDATE_JINRISHOUDIAN_ACTION)) {
				setTextViewJinRiShouDian();
			} else if (intent.getAction().equals(
					GlobalParams.UPDATE_YAJINYUER_ACTION)) {
				tv_baozhengjinyue1.setText(GlobalParams.CASH_AC_BAL);
			}
		}
	};

	private void createDialog() {
		progressDialog = MyProgressDialog.createProgressDialog(
				SMSActivity.this, 3 * 60 * 1000,
				new MyProgressDialog.OnTimeOutListener() {

					@Override
					public void onTimeOut(MyProgressDialog dialog) {
						SystemUtil.displayToast(SMSActivity.this,
								R.string.progress_timeout);
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
						ISEXIT_SEND_SMS_2 = 0;
						showGoudianchaoshiDialog();
					}
				});
	}

	/**
	 * 实现发送短信
	 * 
	 * @param context
	 * @param text
	 *            短信的内容
	 * @param phoneNumber
	 *            手机号码
	 */
	private void sendMessage(Context context, String text, String phoneNumber) {
		// create the sentIntent parameter
		Intent sentIntent = new Intent(GlobalParams.SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
				sentIntent, 0);
		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(GlobalParams.DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
				deliverIntent, 0);

		SmsManager smsManager = SmsManager.getDefault();
		// 如果字数超过5,需拆分成多条短信发送
		if (text.length() > 70) {
			ArrayList<String> msgs = smsManager.divideMessage(text);
			for (String msg : msgs) {
				smsManager.sendTextMessage(phoneNumber, null, msg, sentPI,
						deliverPI);
			}
		} else {
			smsManager.sendTextMessage(phoneNumber, null, text, sentPI,
					deliverPI);
		}
	}

	@Override
	public void onResume() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalParams.RECEIVER_SMS_ACTION);
		intentFilter.addAction(GlobalParams.SENT_SMS_ACTION);
		intentFilter.addAction(GlobalParams.DELIVERED_SMS_ACTION);
		registerReceiver(mBroadcastReceiver, intentFilter);
		registerReceiver(sendMessageReceiver, intentFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		unregisterReceiver(mBroadcastReceiver);
		unregisterReceiver(sendMessageReceiver);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	// 从数据库获取今日收费数据并显示到主界面
	private void setTextViewJinRiShouDian() {
		BaseDao<JinRiShouDian, Integer> baseDao = new BaseDao<JinRiShouDian, Integer>(
				getApplicationContext(), JinRiShouDian.class);
		List<JinRiShouDian> li = baseDao.findAllByField("time",
				SystemUtil.getCurrentDate());
		float money = 0.00F;
		String mMoney = "0";
		if (baseDao.isExists(1)) {
			if (li.size() > 0) {
				for (JinRiShouDian jinRiShouDian : li) {
					money += Float.valueOf(jinRiShouDian.getMoney());
				}
				DecimalFormat mDecimalFormat = new DecimalFormat("#.00");
				mMoney = mDecimalFormat.format(money);
			}
		}
		tv_jinrishoudian1.setText(mMoney
				+ "/"
				+ li.size()
				+ getResources().getString(
						R.string.main_welcome_jingrishoudian_unit));
	}

	// 获取wifi信号强度
	private int getWifiLevel() {
		int mRssi = -200;
		int level = 0;
		WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
		mRssi = mWifiInfo.getRssi();
		level = WifiManager.calculateSignalLevel(mRssi, 4);
		return level;
	}

	// 监测手机信号强度变化，并改变相应图标
	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			int level;
			int asu = signalStrength.getGsmSignalStrength();
			if (asu <= 2 || asu == 99)
				level = 0;
			else if (asu >= 12) {
				level = 4;
			} else if (asu >= 8) {
				level = 3;
			} else if (asu >= 5) {
				level = 2;
			} else {
				level = 1;
			}
			imageSignal1.setImageResource(R.drawable.gsm_strength);
			imageSignal1.setImageLevel(level);
		}

	}

	// 设置网络状态图标
	public void setNetWorkStatus() {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable())
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				imageWifi1.setVisibility(View.VISIBLE);
				imageSignal1.setVisibility(View.GONE);
				imageEthernet1.setVisibility(View.GONE);
				imageWifi1.setImageResource(R.drawable.wifi_level);
				imageWifi1.setImageLevel(getWifiLevel());
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				imageWifi1.setVisibility(View.GONE);
				imageSignal1.setVisibility(View.VISIBLE);
				imageEthernet1.setVisibility(View.GONE);
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
				imageWifi1.setVisibility(View.GONE);
				imageSignal1.setVisibility(View.GONE);
				imageEthernet1.setVisibility(View.VISIBLE);
			}
	}

	// 注销生成记录后的日结详情对话框
	private void showRiJieXiangQingDialog(final String TOF_NO,
			final String TOF_NAME, final String TOF_AMT,
			final String TOF_NUMBER, final String TOF_DATE) {
		final Dialog dialogZx1 = new Dialog(this, R.style.FullHeightDialog);
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View dialogZxView = li.inflate(R.layout.dialog_rijiexiangqing, null);
		dialogZx1.setContentView(dialogZxView);
		dialogZx1.show();

		TextView tv_rjxq = (TextView) dialogZxView.findViewById(R.id.tv_rjxq);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(
				TOF_NAME 
				+ "\n"
				+ getString(R.string.logout_dialog_sms_rj_date) 
				+ "\n"
				+ TOF_DATE 
				+ "\n"
				+ getString(R.string.logout_dialog_sms_rj_number) 
				+ TOF_NUMBER
				+ "\n" 
				+ getString(R.string.logout_dialog_sms_rj_money)
				+ TOF_AMT 
				+ "\n");
		tv_rjxq.setText(stringBuilder);

		final Button btn_print = (Button) dialogZxView.findViewById(R.id.btn_d_dayin);
		btn_print.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (progressDialog == null) {
					progressDialog = MyProgressDialog.createProgressDialog(SMSActivity.this, 10000, new MyProgressDialog.OnTimeOutListener() {
						
						@Override
						public void onTimeOut(MyProgressDialog dialog) {
							SystemUtil.displayToast(SMSActivity.this,
									R.string.printer_status_timeout);
							if(dialog != null){
								dialog.dismiss();
								dialog = null;
							}							
						}
					});
				}
				progressDialog.setTitle(R.string.login_progressdialog_title);
				progressDialog.setMessage(getString(R.string.printering));
				progressDialog.setCancelable(false);
				progressDialog.show();
				btn_print.setEnabled(false);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Printer printer = new Printer();
						printer.start();
						printer.reset();
						printer.printXML(
								"<TICKET>" +
								"<TYPE_T>1</TYPE_T>" +
								"<TITLE_T>" + getString(R.string.logout_dialog_sms_rj) + "</TITLE_T>" +
								"<CONTENT_T>" + TOF_NAME + "</CONTENT_T>" +
								"<CONTENT_T>" + getString(R.string.logout_dialog_sms_rj_date) + TOF_DATE + "</CONTENT_T>" +
								"<CONTENT_T>" + getString(R.string.logout_dialog_sms_rj_number) + TOF_NUMBER + "</CONTENT_T>" +
								"<CONTENT_T>" + getString(R.string.logout_dialog_sms_rj_money) + TOF_AMT + "</CONTENT_T>" +
								"<FP/>" +
								"</TICKET>");
						int printResult = printer.commitOperation();
						Message message = new Message();
						Bundle bundle = new Bundle();
		                switch (printResult) {
		                    case 0:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_success));
		                        break;
		                    case -1001:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_nopaper));
		                        break;
		                    case -1002:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_hot));
		                        break;
		                    case -1003:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_full));
		                        break;
		                    case -1004:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_noconnect));
		                        break;
		                    case -9999:
		                    	bundle.putString("ErrorMsg", getString(R.string.printer_status_other_error));
		                        break;
		                    default:
		                        break;
		                }
						message.setData(bundle);
						ihandler.sendMessage(message);
						printer.stop();
					}
				}).start();
				btn_print.setEnabled(true);
			}
		});

		ImageView imageView = (ImageView) dialogZxView
				.findViewById(R.id.btnCloseDialog);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialogZx1 != null && (dialogZx1.isShowing())) {
					dialogZx1.dismiss();
				}
			}
		});
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ISEXIT_SEND_SMS_2 == 1) {
				if (intent.getAction().equals(GlobalParams.RECEIVER_SMS_ACTION)) {
					Object[] pdus = (Object[]) intent.getExtras().get("pdus");
					SmsMessage[] mSmsMessages = new SmsMessage[pdus.length];
					for (int i = 0; i < pdus.length; i++) {
						mSmsMessages[i] = SmsMessage
								.createFromPdu((byte[]) pdus[i]);
					}
					String smsNumber = "";
					String smsReceiverText = "";
					for (SmsMessage smsMessage : mSmsMessages) {
						smsNumber = smsMessage.getDisplayOriginatingAddress();
						Log.v("响应号码:", smsNumber);
						smsReceiverText = smsMessage.getDisplayMessageBody();
						Log.v("响应文本", smsReceiverText);
					}
					if (smsNumber.contains(GlobalParams.PNO)) {
						if (smsReceiverText.startsWith("T")) {
							String[] text = smsReceiverText.split("\\*");
							if (text[2].equals(LOGOUT_NUMBER)) {
								IS_LOGOUT_SUCCESS = true;
								btn_zjtc.setEnabled(true);
								if (text[1].equals("00000")) {
									String TOF_NO = text[3];
									String TOF_NAME = text[4];
									String TOF_AMT = text[5];
									String TOF_NUMBER = text[6];
									String TOF_DATE = SystemUtil
											.getCurrentDateTimeHH24();
									if (progressDialog != null) {
										progressDialog.dismiss();
										progressDialog = null;
									}
									showRiJieXiangQingDialog(TOF_NO, TOF_NAME,
											TOF_AMT, TOF_NUMBER, TOF_DATE);
								} else {
									Toast.makeText(SMSActivity.this, text[3],
											Toast.LENGTH_LONG).show();
									ISEXIT_SEND_SMS_2 = 0;
									if (progressDialog != null) {
										progressDialog.dismiss();
										progressDialog = null;
									}
								}
							}
						} else if (smsReceiverText.startsWith("S")) {
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;
							}
							parseSMS(smsReceiverText);
						}
					}
				}
			}
		}
	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnBackPressedListener != null
                    && keyCode == KeyEvent.KEYCODE_BACK) {
                mOnBackPressedListener.onPressed();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
            return false;
        }
		return super.dispatchKeyEvent(event);
	}
	
    public void setOnBackPressedListener(
            OnBackPressedListener mOnBackPressedListener) {
        this.mOnBackPressedListener = mOnBackPressedListener;
    }

    public interface OnBackPressedListener {
        void onPressed();
    }
    
    private static class Ihandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public Ihandler(SMSActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            SystemUtil.displayToast(mActivity.get(),
                    msg.getData().getString("ErrorMsg"));
            super.handleMessage(msg);
        }
    }
    private Ihandler ihandler = new Ihandler(SMSActivity.this);
	
}
