package com.common.powertech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.common.powertech.R;

/**
 * 上拉刷新ListView
 * 
 * @author ouyangguozhao
 * 
 */
public class PullUpListView extends ListView implements OnScrollListener {
	private final static String TAG = "PullUpListView";
	/** 底部显示正在加载的页面 */
	private View footerView = null;
	/** 存储上下文 */
	private Context context;
	/** 上拉刷新的ListView的回调监听 */
	private MyPullUpListViewCallBack myPullUpListViewCallBack;
	/** 记录第一行Item的数值 */
	private int firstVisibleItem;
	private boolean isScrollToBottom; // 是否滑动到底部
	private int footerViewHeight = 0; // 脚布局的高度
	private boolean isLoadingMore = false; // 是否正在加载更多中

	public PullUpListView(Context context) {
		super(context);
		this.context = context;
		initListView();
	}

	public PullUpListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initListView();
	}

	/**
	 * 初始化ListView
	 */
	private void initListView() {

		// 为ListView设置滑动监听
		setOnScrollListener(this);
		// 去掉底部分割线
		setFooterDividersEnabled(false);
	}

	/**
	 * 初始化话底部页面
	 */
	public void initBottomView() {
		// this.context=context;
		if (footerView == null) {
			footerView = LayoutInflater.from(this.context).inflate(
					R.layout.listview_loadbar, null);
			footerView.measure(0, 0);
			footerViewHeight = footerView.getMeasuredHeight();
			footerView.setPadding(0, -footerViewHeight, 0, 0);
		}
		addFooterView(footerView);
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.e(TAG, "onScrollStateChanged");
		if (scrollState == SCROLL_STATE_IDLE) {
			// 判断当前是否已经到了底部
			if (isScrollToBottom && !isLoadingMore) {
				isLoadingMore = true;
				// 当前到底部
				footerView.setPadding(0, footerViewHeight, 0, 0);
				this.setSelection(this.getCount());

				if (myPullUpListViewCallBack != null) {
					myPullUpListViewCallBack.scrollBottomState();
				}
			}
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.e(TAG, "onScroll");
/*		if (null != footerView && footerViewHeight != 0) {
			footerView.setPadding(0, footerViewHeight / 2, 0, 0);
		}*/
		this.firstVisibleItem = firstVisibleItem;
		if (getLastVisiblePosition() == (totalItemCount - 1)) {
			isScrollToBottom = true;
		} else {
			isScrollToBottom = false;
		}
	}

	public void removeFooterView() {
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		isLoadingMore = false;
		// footerView.setVisibility(View.GONE);//隐藏底部布局
	}

	/*
	 * public void showFooterView(){
	 * footerView.setVisibility(View.VISIBLE);//显示底部布局 }
	 */

	public void setMyPullUpListViewCallBack(
			MyPullUpListViewCallBack myPullUpListViewCallBack) {
		this.myPullUpListViewCallBack = myPullUpListViewCallBack;
	}

	/**
	 * 上拉刷新的ListView的回调监听
	 * 
	 * @author ouyangguozhao
	 * 
	 */
	public interface MyPullUpListViewCallBack {

		void scrollBottomState();
	}
}