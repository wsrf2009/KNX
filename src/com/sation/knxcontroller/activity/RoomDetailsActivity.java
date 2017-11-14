package com.sation.knxcontroller.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RelativeLayout.LayoutParams;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.STKNXPagerAdapter;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXPage;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.viewpagerindicator.LinePageIndicator;
import com.sation.knxcontroller.widget.STKNXPage;
import com.sation.knxcontroller.widget.STViewPager;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailsActivity extends BaseActivity implements OnPageChangeListener {
	final private String TAG = "RoomDetailsActivity";

	final private List<Integer> mUpdatedList = new ArrayList<Integer>();
	private STViewPager mViewPager;
	private LinePageIndicator linePageIndicator;
	private STKNXPage mCurrentSTKNXPage;
	private List<KNXPage> mKNXPages;
	private ArrayList<STKNXPage> mPages;
	private List<TimingTaskListAdapter> timingTaskAdapterList;
	private SharedPreferences settings;
	private STKNXPagerAdapter mSTKNXPagerAdapter;
	private boolean mFlagForThreadRun;

	private boolean shouldDestroyActivity;
	private boolean mCycleDrag = false; // 循环滑动页面
    private int mCurrentPageIndex = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_details_layout);

		this.mViewPager = (STViewPager)findViewById(R.id.stViewPager1);
		this.linePageIndicator = (LinePageIndicator)findViewById(R.id.linePageIndicator);
		this.mPages = new ArrayList<STKNXPage>();
		this.timingTaskAdapterList = new ArrayList<TimingTaskListAdapter>();
		this.mSTKNXPagerAdapter = new STKNXPagerAdapter();

		this.shouldDestroyActivity = false;
		
		this.settings = getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);

		createRoom(); // 创建房间

		startThreadUpdateAddressStatus(); // 开启获取对象状态的线程
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();

		if(STKNXControllerApp.getInstance().getRememberLastInterface()) { // 是否记住最后一个界面
			final String timerId = ""; // 从定时器界面返回时，清楚记住的定时器
			STKNXControllerApp.getInstance().setLastTimerId(timerId);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(STKNXControllerConstant.LAST_TIMER_ID, timerId);
			editor.apply();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();

		for(TimingTaskListAdapter adapter : timingTaskAdapterList) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

        if (this.mCurrentPageIndex >= 0) {
            final int pageIndex = this.mCurrentPageIndex;
			viewPagerSelectPage(pageIndex); // 从其他页面返回，自动进入之前的页面
		} else {
			if (STKNXControllerApp.getInstance().getRememberLastInterface()) {
				final int pageIndex = STKNXControllerApp.getInstance().getLastPageIndex();
				if (this.mPages.size() > 0) {
					if (this.mPages.size() > pageIndex) {
						this.linePageIndicator.setCurrentItem(pageIndex);
						viewPagerSelectPage(pageIndex); // 自动进入到已记住的页面
					} else {
						this.linePageIndicator.setCurrentItem(0);
						viewPagerSelectPage(0);
					}
				}
			} else {
				if (this.mPages.size() > 0) {
					this.linePageIndicator.setCurrentItem(0);
					viewPagerSelectPage(0);
				}
			}
		}

		KNX0X01Lib.setContext(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		super.onPause();

		KNX0X01Lib.setContext(null);

		if (null != this.mViewPager) {
			this.mCurrentPageIndex = this.mViewPager.getCurrentItem();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "");

		stopThreadUpdateAddressStatus();

		this.shouldDestroyActivity = true;

		for(int i=0; i<this.mPages.size(); i++) {
			STKNXPage page = this.mPages.get(i);
			page.onDestroy();
		}
	}
	
	@Override
	public final void onLowMemory(){
		super.onLowMemory();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.scale_from_center_600, R.anim.scale_to_center_600);
	}

	private void createRoom() {
		try {
			KNXRoom mKNXRoom = (KNXRoom) getIntent().getSerializableExtra(STKNXControllerConstant.REMOTE_PARAM_KEY);
			this.mKNXPages = mKNXRoom.getPages(); // 获取房间的页面
		
			if ((null != this.mKNXPages) && (this.mKNXPages.size() > 0 )) {
				if(this.mCycleDrag) { // 循环切换页面？
					KNXPage mKNXPage = this.mKNXPages.get(this.mKNXPages.size()-1);
					STKNXPage page = createPageAndInnerControls(mKNXPage); // 创建最后一个页面
					this.mPages.add(page);
					
					if(this.mKNXPages.size() > 1) {
						for (int i = 0; i < this.mKNXPages.size(); i++) {
							mKNXPage = this.mKNXPages.get(i);
							page = createPageAndInnerControls(mKNXPage); // 创建页面
							this.mPages.add(page);
						}
					
						mKNXPage = this.mKNXPages.get(0);
						page = createPageAndInnerControls(mKNXPage); // 创建第一个页面
						this.mPages.add(page);
					}
				} else {
					for (int i = 0; i < this.mKNXPages.size(); i++) {
						KNXPage mKNXPage = this.mKNXPages.get(i);
						STKNXPage page = createPageAndInnerControls(mKNXPage); // 创建页面
						this.mPages.add(page);
					}
				}
			}
		
			this.mSTKNXPagerAdapter.setAdapterSource(this.mPages);
		
			this.mViewPager.setAdapter(this.mSTKNXPagerAdapter);
			this.mViewPager.setCycleDrag(this.mCycleDrag);
			this.mViewPager.addOnPageChangeListener(this);

			this.linePageIndicator.setViewPager(this.mViewPager);
			this.linePageIndicator.setOnPageChangeListener(this);
			this.linePageIndicator.setCycleDrag(this.mCycleDrag);
			this.linePageIndicator.setPageCount(this.mKNXPages.size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private STKNXPage createPageAndInnerControls(KNXPage knxPage) {
		STKNXPage page = new STKNXPage(this, knxPage);
		LayoutParams pageLayoutParams = new LayoutParams(knxPage.Width, knxPage.Height);
		page.setLayoutParams(pageLayoutParams);
		
		return page;
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		int i;

		if(this.mCycleDrag) { // 循环？
			if((null != this.mPages) && (null != this.mKNXPages) && (null != this.mViewPager)) {
				if(this.mPages.size() > 1) {
					if(position < 1) { // 第0个页面也即是最后一个页面
						i = this.mKNXPages.size();
						this.mViewPager.setCurrentItem(i, false);
						return;
					} else if(position > this.mKNXPages.size()) {
						this.mViewPager.setCurrentItem(1, false); // 最后一个页面也即是第一个页面
						return;
					} else {
						i = position;
					}
					
					i -= 1;
				} else {
					i = position;
				}
			} else {
				return;
			}
		} else {
			i = position;
		}

		final int pageIndex = i;
		if(STKNXControllerApp.getInstance().getRememberLastInterface()) { // 记住页面？
			STKNXControllerApp.getInstance().setLastPageIndex(pageIndex);

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(STKNXControllerConstant.LAST_PAGE_INDEX, pageIndex);
			editor.apply();
		}

		viewPagerSelectPage(pageIndex); // 刷新页面内容
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
	
	@SuppressLint("UseSparseArrays")
	private void viewPagerSelectPage(final int position) {

		try {
			synchronized (this.mPages) {
				if(this.shouldDestroyActivity) {
					return;
				}

				if (null != this.mCurrentSTKNXPage) {
					this.mCurrentSTKNXPage.onSuspend(); // 挂起当前页面
				}

				if ((null != this.mPages) && (this.mPages.size() > position)) { // 恢复新的页面
					this.mCurrentSTKNXPage = this.mPages.get(this.mCycleDrag && this.mPages.size() > 1 ? position + 1 : position);
					this.mCurrentSTKNXPage.onResume();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addUpdatedAddress(int asp) {
		synchronized (this.mUpdatedList) {
			this.mUpdatedList.add(asp); // 将状态已更新的对象添加到列表
			this.mUpdatedList.notify();
		}
	}

	final private Thread mThreadUpdateAddressStatus = new Thread() {
		public void run() {
			do {
				int asp = -1;

				synchronized (mUpdatedList) {
					if (mUpdatedList.size() <= 0) {
						try {
							mUpdatedList.notify();
							mUpdatedList.wait();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					if (mUpdatedList.size() > 0) {
						asp = mUpdatedList.get(0);
						mUpdatedList.remove(0);
					}
				}

				if (asp >= 0) {
					try {
						KNXGroupAddress address = STKNXControllerApp.getInstance().getGroupAddressMap().get(asp);
						if (null != address) {
							mCurrentSTKNXPage.addressStatusUpdate(asp, address); // 在当前页面查找并更新该对象
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} while (mFlagForThreadRun);
		}
	};

	private void startThreadUpdateAddressStatus() {
		this.mFlagForThreadRun = true;
		this.mThreadUpdateAddressStatus.start();
	}

	private void stopThreadUpdateAddressStatus() {
		this.mFlagForThreadRun = false;
		synchronized (this.mUpdatedList) {
			this.mUpdatedList.notify();
		}
	}
}
