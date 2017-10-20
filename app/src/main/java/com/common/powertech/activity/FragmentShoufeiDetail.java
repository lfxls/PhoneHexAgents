package com.common.powertech.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.common.powertech.ItemListActivity;
import com.common.powertech.ItemListActivity.OnBackPressedListener;
import com.common.powertech.ItemListActivity.ShortCutsKeyDownCallBack;
import com.common.powertech.R;
import com.common.powertech.bussiness.PULLParse_ShoufeiQueryRequest;
import com.common.powertech.bussiness.Request_Shoufei_Query;
import com.common.powertech.dummy.DummyContent;
import com.common.powertech.param.GlobalParams;
import com.common.powertech.util.MathUtil;
import com.common.powertech.util.Preferences;
import com.common.powertech.util.StringUtil;
import com.common.powertech.util.SystemUtil;
import com.common.powertech.webservice.Client;
import com.common.powertech.widget.MyProgressDialog;
import com.common.powertech.widget.PullUpListView;
import com.common.powertech.widget.PullUpListView.MyPullUpListViewCallBack;
import com.common.powertech.xml.ShoufeiQuery_Class;
import com.myDialog.CustomProgressDialog;

/**
 * 广东天波信息技术股份有限公司 功能：收费详情Fragment 作者:ouyangguozhao 日期:2015-11-6
 */

public class FragmentShoufeiDetail extends Fragment {
	private static final String TAG = "FragmentShoufeiDetail";
	private CustomProgressDialog progressDialog;
	private View mView;
	private PullUpListView mShouFeiDetailList;
	private ShouFeiAdapter mShouFeiAdapter;
	private String bizType = "";

	private Button mMergeBtn;
	private String inputCond = "";
	private String queryType = "";
	private String CARD_TYPE = "";
	private String IC_JSON_REQ = "";
	private String mRspCode = "";
	private String mRspMeg = "";
	private String ResourceType = "";
	private String EnelName="";
	private String EnelId="";
	private String FirstAmt="";
	private String PayMethod="";
	private String prdordnocon="";
	private int mCurrentPageNum = 1;// 默认当前页码

	private static List<ShoufeiQuery_Class> mShouFeiList = new ArrayList<ShoufeiQuery_Class>();
	private static List<ShoufeiQuery_Class> mTempSelectList = new ArrayList<ShoufeiQuery_Class>();
	private static HashMap<Integer, Boolean> mTotalSelectStateHM = new HashMap<Integer, Boolean>();

	private boolean mInLoading = false;
	private boolean isOnePieceTurnBack = false;
	private AsyncTask<Void, Void, String> mShouFeiTask;
	private boolean isFullContent = false;
	private boolean isPreOnePiece = true;
	private ItemListActivity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		mActivity = (ItemListActivity) getActivity();
		mActivity.isEnterTrigger = false;
		mView = inflater.inflate(R.layout.fragment_shoufei_detail, container,
				false);
		// 获取输入条件
		inputCond = getArguments().getString("inputCond");
		queryType = getArguments().getString("queryType");
		CARD_TYPE = getArguments().getString("CARD_TYPE");
		IC_JSON_REQ = getArguments().getString("IC_JSON_REQ");
		ResourceType = getArguments().getString("ResourceType");
		EnelName = getArguments().getString("EnelName");
		EnelId = getArguments().getString("EnelId");
		FirstAmt = getArguments().getString("FirstAmt");
		PayMethod = getArguments().getString("PayMethod");
		prdordnocon = getArguments().getString("prdordno");
		Log.e(TAG, "CARD_TYPE:"+CARD_TYPE);
		Log.e(TAG, "IC_JSON_REQ:"+IC_JSON_REQ);
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		iniUI();
		mShouFeiList.clear();
		mTotalSelectStateHM.clear();
		mShouFeiAdapter.notifyDataSetChanged();
		if (bizType.equals("D2")) {
			// 售电
			turnBack();
			return;
		}
		if (isOnePieceTurnBack && mCurrentPageNum == 1) {
			// 收费并且当前页只有一条数据
			turnBack();
			return;
		} else if (isOnePieceTurnBack && mCurrentPageNum > 1) {
			// mCurrentPageNum--;
			mCurrentPageNum = 1;
		}
		mCurrentPageNum = 1;
		shoufeiQuery(String.valueOf(mCurrentPageNum));
		mActivity.setOnBackPressedListener(new OnBackPressedListener() {

			@Override
			public void onPressed() {
				turnBack();
			}
		});
	}

	private void iniUI() {
		mActivity.setShortCutsKeyDownCallBack(new ShortCutsKeyDownCallBack() {

			@Override
			public void keyValue(int selectKey) {
				if (selectKey == 28 && mMergeBtn != null
						&& mMergeBtn.isEnabled()) {
					mMergeBtn.performClick();
				}
				if (mShouFeiList.size() >= selectKey && selectKey > 0) {
					turnToShouFeiXiangQingActivity(mShouFeiList, selectKey - 1);
				}
			}
		});

		mShouFeiDetailList = (PullUpListView) mView
				.findViewById(R.id.shoufeiDetailList);
		mShouFeiDetailList.initBottomView();
		mShouFeiAdapter = new ShouFeiAdapter(mActivity, mShouFeiList);
		mShouFeiDetailList.setAdapter(mShouFeiAdapter);
		mShouFeiDetailList
				.setMyPullUpListViewCallBack(new MyPullUpListViewCallBack() {

					@Override
					public void scrollBottomState() {
						if (!mInLoading) {
							mInLoading = true;
							if (isFullContent) {
								// 加载下一页
								mCurrentPageNum += 1;
							}
							shoufeiQuery(String.valueOf(mCurrentPageNum));
						}
					}
				});

		mShouFeiDetailList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean itemSelectState = mTotalSelectStateHM.get(position);
				mTotalSelectStateHM.put(position, !itemSelectState);
				updateTotalResult(false);
			}
		});

		mMergeBtn = (Button) mView.findViewById(R.id.btn_zdhbsf);
		mActivity.isEnterTrigger = false;
		mMergeBtn.setEnabled(false);
		mMergeBtn.setVisibility(View.GONE);
	}

	private class ShouFeiAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		// private List<ShoufeiQuery_Class> mList;
		// private HashMap<Integer, Boolean> isSelected;

		/** 构造函数 */
		public ShouFeiAdapter(Context context, List<ShoufeiQuery_Class> list) {
			this.mInflater = LayoutInflater.from(context);
			// this.isSelected = new HashMap<Integer, Boolean>();
			initState();
		}

		private void initState() {
			if (mShouFeiList.size() == mTotalSelectStateHM.size()) {
				return;
			} else {
				for (int i = 0; i < mShouFeiList.size(); i++) {
					mTotalSelectStateHM.put(i, false);
				}
			}
		}

		@Override
		public int getCount() {
			// return 0;
			return mShouFeiList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mShouFeiList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_shoufei,
						null);
				holder = new ViewHolder();
				holder.cView = (View) convertView
						.findViewById(R.id.totalSelectViewLayout);
				holder.cUserMessage4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_nameandNumber);
				holder.cUserAddress4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_address);
				holder.cElecCompany4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_elecdComplany);
				holder.cUserPhone4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_phone);
				holder.cElecIn4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_dianfeiyuefen);
				holder.cSellElecAmount4Charge = (TextView) convertView
						.findViewById(R.id.shoufei_shoudianjine);
				holder.cShortCutkey = (Button) convertView
						.findViewById(R.id.shortCutKey_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (mTotalSelectStateHM.get(position)) {
				holder.cView.setBackgroundColor(mActivity.getResources()
						.getColor(R.color.deepskyblue));
			} else {
				holder.cView.setBackgroundColor(Color.WHITE);
			}
			if (!mInLoading) {
//				if (position < 9) {
//					holder.cShortCutkey.setText(mActivity
//							.getString(R.string.shoudianxiangqing_btn_sf)
//							+ "("
//							+ (position + 1) + (position + 1) + ")");
//				} else {
					holder.cShortCutkey.setText(mActivity
							.getString(R.string.shoudianxiangqing_btn_sf));
//				}
			}

			holder.cUserMessage4Charge.setText("Elec Bill of "
					+ mShouFeiList.get(position).getUSER_NAME() + "/"
					+ mShouFeiList.get(0).getUSER_NO());
			holder.cUserAddress4Charge.setText(mShouFeiList.get(position)
					.getUSER_ADDR());
			holder.cElecCompany4Charge.setText(mShouFeiList.get(position)
					.getELEN_ID());
			holder.cUserPhone4Charge.setText(mShouFeiList.get(position)
					.getTEL());
			holder.cElecIn4Charge.setText(mShouFeiList.get(position)
					.getCALC_MON());
			holder.cSellElecAmount4Charge
					.setText((mShouFeiList.get(position)
							.getENERGY_AMT()));
			holder.cShortCutkey.setEnabled(true);
			holder.cShortCutkey.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					holder.cShortCutkey.setEnabled(false);
					// 关闭摄像头
					mActivity.StopScan();
					GlobalParams.If_CloseFlashLight = true;
					turnToShouFeiXiangQingActivity(mShouFeiList, position);
				}
			});
			return convertView;
		}

		private final class ViewHolder {
			private View cView;
			private TextView cUserMessage4Charge, cUserAddress4Charge,
					cElecCompany4Charge, cUserPhone4Charge, cElecIn4Charge,
					cSellElecAmount4Charge;
			private Button cShortCutkey;
		}
	}

	private void updateTotalResult(boolean isUpdate) {
		mTempSelectList.clear();
		if (mShouFeiList.size() == 0) {
			mTotalSelectStateHM.clear();
			return;
		}
		for (int i = 0; i < mTotalSelectStateHM.size(); i++) {
			if (mTotalSelectStateHM.get(i)) {
				mTempSelectList.add(mShouFeiList.get(i));
			}
		}

		if (mTempSelectList.size() > 1) {
			float totalAmt = 0.0f;
			for (ShoufeiQuery_Class sc : mTempSelectList) {
				if (null != sc.getENERGY_AMT()) {
					totalAmt = MathUtil.add4Float(totalAmt,
							Float.parseFloat(sc.getENERGY_AMT()));// 共计金额
				}
			}
			mActivity.isEnterTrigger = true;
			mMergeBtn.setVisibility(View.VISIBLE);
			mMergeBtn.setEnabled(true);
			mMergeBtn
					.setText(mActivity
							.getString(R.string.shoudianxiangqing_btn_zdhbsf)
							+ keepDecimalPlaces(String.valueOf(totalAmt))
							+ mActivity
									.getString(R.string.shoudianxiangqing_btn_huichequeren));
			mMergeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 账单合并收费
					Intent mShouFeiXiangQingIntent = new Intent(mActivity,
							ShouFeiXiangQingActivity.class);
					mShouFeiXiangQingIntent.putExtra("ShoufeiQueryItem_List",
							StringUtil.complexData2String(mTempSelectList));
					// startActivity(mShouFeiXiangQingIntent);
					startActivity(mShouFeiXiangQingIntent);
				}
			});
		} else {
			mActivity.isEnterTrigger = false;
			mMergeBtn.setVisibility(View.GONE);
			mMergeBtn.setEnabled(false);
		}
		mShouFeiAdapter.notifyDataSetChanged();
		if(mShouFeiList.size()>1&&isUpdate){
			if(mCurrentPageNum==1){
				mShouFeiDetailList.setSelection(0);
			}else{
				mShouFeiDetailList.setSelection(mShouFeiList.size() - 1);
			}
		}
	}
	 private void logMsg1(String msg) {
		    	//AlertDialog.Builder builder;
		    	AlertDialog.Builder  builder = new AlertDialog.Builder (mActivity);
		   	  builder.setMessage(msg);
		   	  builder.setTitle("提示");
		   	 builder.setPositiveButton("OK",
		             new DialogInterface.OnClickListener() {
		   	   @Override
		   	   public void onClick(DialogInterface dialog, int which) {
		   		   	dialog.dismiss();
		   	   }
		   	  });
		   	  AlertDialog x = builder.create();
		   	  x.show();
	  }
	Handler mhandler = new Handler() {

		public void handleMessage(Message msg) {
			isOnePieceTurnBack = false;

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				// progressDialog = null;
			}
			// 处理消息
			switch (msg.what) {
			case 0:
				// 联网失败
				// 没有加载到数据，页码返回到当前页
				 try {
                     if (progressDialog != null
                             && (!mActivity.isFinishing())) {
                         progressDialog.dismiss();
                         // 没有加载到数据，页码返回到当前页
                         Toast.makeText(mActivity, getString(R.string.str_lianwangshibai), Toast.LENGTH_LONG).show();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
				 
				if (isFullContent) {
					if (mInLoading) {
						mCurrentPageNum -= 1;
					}
				}
				isFullContent = false;
				turnBack();
				break;
			case 1:
				// 联网成功
				System.out.println("收费查询响应：" + GlobalParams.RETURN_DATA);
				mRspCode = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPCOD>", "</RSPCOD>");
				mRspMeg = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<RSPMSG>", "</RSPMSG>");
			 
				EnelName =  Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<ENEL_NAME>", "</ENEL_NAME>");
				 
				EnelId = Client.Parse_XML(GlobalParams.RETURN_DATA,
						"<ENEL_ID>", "</ENEL_ID>");
				
				
				if (mRspCode.equals("00000") || mRspCode.equals("11111") || mRspCode.equals("11112")) {
//					logMsg1(mRspCode);
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
							numberList.add(0, number);
						}
					} else {
						numberList.add(number);
					}
					Preferences.storeComplexDataInPreference(mActivity,
							Preferences.KEY_MeterOrUser_No, numberList);

					InputStream in;
					List<ShoufeiQuery_Class> mQuestList = new ArrayList<ShoufeiQuery_Class>();
					try {
						in = new ByteArrayInputStream(
								GlobalParams.RETURN_DATA.getBytes("UTF-8"));
						mQuestList = PULLParse_ShoufeiQueryRequest
								.getBDList(in);
						int oldListSize = mTotalSelectStateHM.size();
						// removeDuplicateWithOrder(mShouFeiList);
						for (ShoufeiQuery_Class sc : mQuestList) {
							boolean isSame = false;
							for (ShoufeiQuery_Class s : mShouFeiList) {
								if (s.getRECE_ID().equalsIgnoreCase(
										sc.getRECE_ID())) {
									isSame = true;
								}
							}
							if (!isSame) {
								mTotalSelectStateHM.put(oldListSize++, false);
								mShouFeiList.add(sc);
							}
						}

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (mShouFeiList.size() == 1) {
						// 如查询结果只有一条，直接弹出收费窗口，收费进入收费详情界面，售电直接进入售电详情界面
						bizType = mShouFeiList.get(0).getBIZ_TYPE();// 业务类型
						inputCond = "";
						queryType = "";
						isOnePieceTurnBack = true;
						if (bizType.equals("D2")) {// 售电

							mActivity.StopScan();
							Intent mShouDianXiangQingIntent = new Intent(
									mActivity, ShouDianXiangQingActivity.class);
							mShouDianXiangQingIntent.putExtra(
									"ShouDianQuery_Class", StringUtil
											.complexData2String(mShouFeiList
													.get(0)));
							mShouDianXiangQingIntent.putExtra("ICRspCode", mRspCode);
							mShouDianXiangQingIntent.putExtra("ICRspMeg", mRspMeg);
							mShouDianXiangQingIntent.putExtra("ResourceType", ResourceType);
							mShouDianXiangQingIntent.putExtra("EnelName", EnelName);
							mShouDianXiangQingIntent.putExtra("EnelId", EnelId);
							//通过订单继续支付功能，设定金额，支付方式。
							mShouDianXiangQingIntent.putExtra(
									"FirstAmt", FirstAmt);
							mShouDianXiangQingIntent.putExtra(
									"PayMethod", PayMethod);
							mShouDianXiangQingIntent.putExtra(
									"prdordno", prdordnocon);
							mShouFeiList.clear();
							mShouFeiAdapter.notifyDataSetChanged();
							startActivity(mShouDianXiangQingIntent);
						} else if (bizType.equals("D4")) {// 收费
							if (mCurrentPageNum == 1 && isPreOnePiece) {// 当前第一页，有且只有一条数据
                                mActivity.StopScan();
								isPreOnePiece = true;
								Intent mShouFeiXiangQingIntent = new Intent(
										mActivity,
										ShouFeiXiangQingActivity.class);
								mShouFeiXiangQingIntent
										.putExtra(
												"ShoufeiQueryItem_List",
												StringUtil
														.complexData2String(mShouFeiList));
								mShouFeiList.clear();
								mShouFeiAdapter.notifyDataSetChanged();
								startActivity(mShouFeiXiangQingIntent);
							} else {
								isPreOnePiece = false;
								// 多条数据，说明是后付费
								if (mQuestList.size() < 3) {// 每页加载3条
									isFullContent = false;
								} else {
									isFullContent = true;
								}
								iniUI();
								if (mInLoading) {
									mInLoading = false;
									mShouFeiDetailList.removeFooterView();
									// update
									mShouFeiDetailList
											.setSelection(mShouFeiList.size() - 1);
									mShouFeiAdapter.notifyDataSetChanged();
								}
							}

						}
					} else {
						// 多条数据，说明是后付费
						mActivity
								.setShortCutsKeyDownCallBack(new ItemListActivity.ShortCutsKeyDownCallBack() {

									@Override
									public void keyValue(int selectKey) {
										if (selectKey == 28
												&& mMergeBtn.isEnabled()) {
											mMergeBtn.performClick();
										}
										if (mShouFeiList.size() >= selectKey
												&& selectKey > 0) {
											turnToShouFeiXiangQingActivity(
													mShouFeiList, selectKey - 1);
										}
									}
								});

						isPreOnePiece = false;
						if (mQuestList.size() < 3) {// 每页加载3条
							isFullContent = false;
						} else {
							isFullContent = true;
						}
						iniUI();
						if (mInLoading) {
							mInLoading = false;
							mShouFeiDetailList.removeFooterView();
							// update
							mShouFeiDetailList
									.setSelection(mShouFeiList.size() - 1);
							mShouFeiAdapter.notifyDataSetChanged();
						}
					}
				} else {
					if (mRspMeg.equalsIgnoreCase("")) {
						SystemUtil.displayToast(mActivity,
								R.string.shoufeixiangqing_wangluoyichang);
					} else {
						SystemUtil.displayToast(mActivity, mRspMeg);
						if(mRspCode.equalsIgnoreCase("00011")){
							SystemUtil.setGlobalParamsToNull(getActivity());
						    DummyContent.ITEM_MAP.clear();
						    DummyContent.ITEMS.clear();
							Intent intent = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent);
							break;
						}
					}
					if (isFullContent) {
						if (mInLoading) {
							mInLoading = false;
							mCurrentPageNum -= 1;
							mShouFeiDetailList.removeFooterView();
							// update
							mShouFeiDetailList
									.setSelection(mShouFeiList.size() - 1);
							mShouFeiAdapter.notifyDataSetChanged();
						}
					}
					if (mCurrentPageNum == 1) {
						if (!isFullContent) {
							turnBack();
						}
					}
				}
				break;
			default:
				break;
			}
			if (mInLoading) {
				mShouFeiDetailList.removeFooterView();
				// update
				mShouFeiDetailList.setSelection(mShouFeiList.size() - 1);
				mShouFeiAdapter.notifyDataSetChanged();
			}
			mInLoading = false;
			if (!bizType.equals("D2")) {
				updateTotalResult(true);
			}
		}
	};

	private void shoufeiQuery(String pageNum) {
		createDialog();
		progressDialog.setTitle(getString(R.string.dialog_check));
		progressDialog.setMessage(getString(R.string.progress_conducting));
		// 设置进度条是否不明确
//		progressDialog.setIndeterminate(false);
		// 是否可以按下退回键取消
		progressDialog.setCancelable(false);
		progressDialog.show();
		String requestXML = "";
		Request_Shoufei_Query.setContext(mActivity);
		Request_Shoufei_Query.setPageNum(pageNum);
		Request_Shoufei_Query.setResourceType(ResourceType);
		Request_Shoufei_Query.setEnelName(EnelName);
		Request_Shoufei_Query.setEnelId(EnelId);
		// 按用户查询--查出后付费可能有多条
		if ("1".equals(queryType)) {
			Request_Shoufei_Query.setUserNum(inputCond);
			Request_Shoufei_Query.setMeterNum("");
		} else if ("2".equals(queryType)) {
			// 按表号查询--查出预付费只有一条
			Request_Shoufei_Query.setMeterNum(inputCond);
			Request_Shoufei_Query.setUserNum("");
			Request_Shoufei_Query.setIcType(CARD_TYPE);
			Request_Shoufei_Query.setICJsonReq(IC_JSON_REQ);
		}
		requestXML = Request_Shoufei_Query.getRequsetXML();
		System.out.println("收费查询请求：" + requestXML);
		Client.SendData("PBillQuery", requestXML, mhandler);
	}

	private void createDialog() {
		progressDialog = CustomProgressDialog.createProgressDialog(mActivity,
				GlobalParams.PROGRESSDIALOG_TIMEOUT,
				new CustomProgressDialog.OnTimeOutListener() {

					@Override
					public void onTimeOut(CustomProgressDialog dialog) {
						SystemUtil.displayToast(mActivity,
								R.string.progress_timeout);
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
//							turnBack();
						}
					}

				});
	}

	private void turnToShouFeiXiangQingActivity(List<ShoufeiQuery_Class> list,
			int position) {
		Intent mShouFeiXiangQingIntent = new Intent(mActivity,
				ShouFeiXiangQingActivity.class);
		List<ShoufeiQuery_Class> l = new ArrayList<ShoufeiQuery_Class>();
		l.add(list.get(position));
		mShouFeiXiangQingIntent.putExtra("ShoufeiQueryItem_List",
				StringUtil.complexData2String(l));
		startActivity(mShouFeiXiangQingIntent);
		// startActivity(mShouFeiXiangQingIntent);
	}

	private void turnBack() {
	
//	if (mActivity != null) {
//		mActivity.setDefaultFragment();
//	}
	 getFragmentManager().popBackStack();
	}

	@Override
	public void onPause() {
		mShouFeiList.clear();
		mTotalSelectStateHM.clear();
		mShouFeiAdapter.notifyDataSetChanged();
		
		mActivity.setOnBackPressedListener(null);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mActivity.isEnterTrigger = false;
		if (mShouFeiTask != null && mShouFeiTask.isCancelled()) {
			mShouFeiTask.cancel(true);
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

	private String keepDecimalPlaces(String number) {
		if (number == null) {
			return "";
		}
		if (GlobalParams.De == null || GlobalParams.De.equalsIgnoreCase("")) {
			GlobalParams.De = "2";
		}
		if(GlobalParams.De.equalsIgnoreCase("0")){
			if(number.contains(".")){
				number=String.valueOf(Math.round(Float.valueOf(number)));
			}
			return number;
		}
		if (!number.contains(".")) {
			number += ".0";
		}
		int positionLength = number.length() - number.indexOf(".") - 1;
		if (positionLength < Integer.valueOf(GlobalParams.De)) {
			for (int i = 0; i < (Integer.valueOf(GlobalParams.De) - positionLength); i++) {
				number += "0";
			}
		} else if (positionLength > Integer.valueOf(GlobalParams.De)) {
			DecimalFormat df1 = new DecimalFormat("#.0");
			DecimalFormat df2 = new DecimalFormat("#.00");
			DecimalFormat df3 = new DecimalFormat("#.000");
			if (GlobalParams.De.equalsIgnoreCase("1")) {
				number = df1.format(Float.valueOf(number));
			} else if(GlobalParams.De.equalsIgnoreCase("2")){
				number = df2.format(Float.valueOf(number));
			}else if(GlobalParams.De.equalsIgnoreCase("3")){
				number = df3.format(Float.valueOf(number));
			}else{
				number = df2.format(Float.valueOf(number));
			}
		}
		return number;
	}
}
