package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.STKNXPagerAdapter;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter;
import com.sation.knxcontroller.control.KNXBlinds;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.control.KNXSwitch;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.control.KNXValueDisplay;
import com.sation.knxcontroller.knxdpt.DPT14;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDataType;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXContainer;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXPage;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;
import com.sation.knxcontroller.viewpagerindicator.LinePageIndicator;
import com.sation.knxcontroller.widget.STKNXDigitalAdjustment;
import com.sation.knxcontroller.widget.STKNXGroupBox;
import com.sation.knxcontroller.widget.STKNXPage;
import com.sation.knxcontroller.widget.STKNXSliderSwitch;
import com.sation.knxcontroller.widget.STKNXSwitch;
import com.sation.knxcontroller.widget.STKNXTimerButton;
import com.sation.knxcontroller.widget.STKNXTimerButton.TimerButtonOnClickListener;
import com.sation.knxcontroller.widget.STKNXValueDisplay;
import com.sation.knxcontroller.widget.STKNXView;
import com.sation.knxcontroller.widget.STViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RelativeLayout.LayoutParams;

public class RoomDetailsActivity extends BaseActivity implements OnPageChangeListener {
	private final String TAG = "RoomDetailsActivity";
	
	private STViewPager mViewPager;
	private LinePageIndicator linePageIndicator;
	private STKNXPage mCurrentSTKNXPage;
	
	private List<KNXPage> mKNXPages;
	private ArrayList<STKNXPage> mPages;
	private List<KNXControlBase> currentPageKNXControlBase; //当前页面所有控件集合
	private Map<KNXControlBase, String> currentPageKNXControlBaseMap;
	private List<TimingTaskListAdapter> timingTaskAdapterList;
	private SharedPreferences settings;
	private STKNXPagerAdapter mSTKNXPagerAdapter;

	private boolean shouldDestroyActivity;
	private boolean mCycleDrag; // 循环滑动页面
	private boolean backToThis;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.room_details_layout);
		this.mViewPager = (STViewPager)findViewById(R.id.stViewPager1);
		this.linePageIndicator = (LinePageIndicator)findViewById(R.id.linePageIndicator);
		this.mPages = new ArrayList<STKNXPage>();
		this.currentPageKNXControlBase = new ArrayList<KNXControlBase>();
		this.currentPageKNXControlBaseMap = new HashMap<KNXControlBase, String>();
		this.timingTaskAdapterList = new ArrayList<TimingTaskListAdapter>();
		this.mSTKNXPagerAdapter = new STKNXPagerAdapter();

		this.shouldDestroyActivity = false;
		this.mCycleDrag = true;
		this.backToThis = false;
		
		this.settings = getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);

		KNX0X01Lib.setContext(this);
		createRoom();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();

		if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
			final String timerId = "";
			STKNXControllerApp.getInstance().setLastTimerId(timerId);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(STKNXControllerConstant.LAST_TIMER_ID, timerId);
			editor.apply();
		}
		
		this.backToThis = true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		for(TimingTaskListAdapter adapter : timingTaskAdapterList) {
			adapter.notifyDataSetChanged();
		}

	    //设备状态的广播
	    IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(STKNXControllerConstant.BROADCAST_UPDATE_DEVICE_STATUS);
		registerReceiver(this.updateDeviceStateReceiver, intentFilter);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "is main thread:"+((Looper.myLooper() != Looper.getMainLooper())?"false":"true"));
		int pageIndex = 0;
		if(this.backToThis) {
			pageIndex = this.mViewPager.getCurrentItem();
		} else {
			if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
				pageIndex = STKNXControllerApp.getInstance().getLastPageIndex();
				if(this.mPages.size() <= pageIndex) {
					pageIndex = 0;
				}
			}
		}

		if(this.mPages.size() > 0) {
			this.linePageIndicator.setCurrentItem(pageIndex);
//			this.mViewPager.setCurrentItem(this.mCycleDrag?pageIndex+1:pageIndex);
//			if(!this.mCycleDrag) {
				if(0 == pageIndex) {
					new Thread(new Runnable(){

						@Override
						public void run() {
							viewPagerSelectPage(0);
						}
					}).start();
				}
//			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		unregisterReceiver(this.updateDeviceStateReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.shouldDestroyActivity = true;
		
		this.timingTaskAdapterList = null;
		this.currentPageKNXControlBaseMap = null;
		this.currentPageKNXControlBase = null;
		
		for(int i=0; i<this.mPages.size(); i++) {
			STKNXPage page = this.mPages.get(i);
			page.onDestroy();
			page = null;
		}
		
		this.mSTKNXPagerAdapter = null;
		this.mCurrentSTKNXPage = null;
		this.mPages = null;
		this.mKNXPages = null;
		this.mViewPager = null;
		this.linePageIndicator = null;
		
		setContentView(R.layout.view_null);
	}
	
	@Override
	public final void onLowMemory(){
		super.onLowMemory();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.scale_from_center, R.anim.scale_to_center);
	}

	private void createRoom() {
		try {
			KNXRoom mKNXRoom = (KNXRoom) getIntent().getSerializableExtra(STKNXControllerConstant.REMOTE_PARAM_KEY);
			this.mKNXPages = mKNXRoom.getPages();
		
			if ((null != this.mKNXPages) && (this.mKNXPages.size() > 0 )) {
				if(this.mCycleDrag) {
					KNXPage mKNXPage = this.mKNXPages.get(this.mKNXPages.size()-1);
					STKNXPage page = createPageAndInnerControls(mKNXPage);
					this.mPages.add(page);
					
					if(this.mKNXPages.size() > 1) {
						for (int i = 0; i < this.mKNXPages.size(); i++) {
							mKNXPage = this.mKNXPages.get(i);
							page = createPageAndInnerControls(mKNXPage);
							this.mPages.add(page);
						}
					
						mKNXPage = this.mKNXPages.get(0);
						page = createPageAndInnerControls(mKNXPage);
						this.mPages.add(page);
					}
				} else {
					for (int i = 0; i < this.mKNXPages.size(); i++) {
						KNXPage mKNXPage = this.mKNXPages.get(i);
						STKNXPage page = createPageAndInnerControls(mKNXPage);
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
		pageLayoutParams.leftMargin = knxPage.Left;
		pageLayoutParams.topMargin = knxPage.Top;
		page.setLayoutParams(pageLayoutParams);
		
		//增加页面级别的控件
		createSTKNXControlAndDisplay(page, knxPage);
		
		return page;
	}
	
	private void createSTKNXControlAndDisplay(STKNXView parentView, KNXContainer knxContainer) {
		for (int x = 0; x < knxContainer.getControls().size(); x++) {
			KNXControlBase mKNXControlBase = knxContainer.getControls().get(x);
			STKNXView view = KNXControlBase.buildWithControl(this, mKNXControlBase);
			if (view != null) {
				if(view instanceof STKNXTimerButton) {
					String id = String.valueOf(mKNXControlBase.getId());
					view.setTag(id); // 当前控件的ID
					((STKNXTimerButton) view).setOnClickListener(buttonAddTimingTaskOnClickListener);
				}
				
				LayoutParams pageLayoutParams = new LayoutParams(view.getWidth(), view.getHeight()); 
				pageLayoutParams.leftMargin = view.getLeft();
				pageLayoutParams.topMargin = view.getTop();
				view.setLayoutParams(pageLayoutParams);
				parentView.addView(view);
				
				if(mKNXControlBase instanceof KNXGroupBox) {
					KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
					createSTKNXControlAndDisplay(view, mKNXGroupBox);
				}
			 }
		}
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		int i;
		
		if(this.mCycleDrag) {
			if((null != this.mPages) && (null != this.mKNXPages) && (null != this.mViewPager)) {
				if(this.mPages.size() > 1) {
					if(position < 1) {
						i = this.mKNXPages.size();
						this.mViewPager.setCurrentItem(i, false);
						return;
					} else if(position > this.mKNXPages.size()) {
						this.mViewPager.setCurrentItem(1, false);
//						i = 1;
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
//		Log.i(TAG, "position:"+position +" i:"+i);
		final int pageIndex = i;
		if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
			STKNXControllerApp.getInstance().setLastPageIndex(pageIndex);

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(STKNXControllerConstant.LAST_PAGE_INDEX, pageIndex);
			editor.apply();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				viewPagerSelectPage(pageIndex);
			}
		}).start();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private boolean isContainsTimer(KNXContainer knxContainer, String timerId) {
		boolean retval = false;
		
		for (int x = 0; x < knxContainer.getControls().size(); x++) {
			KNXControlBase mKNXControlBase = knxContainer.getControls().get(x);
			
			if (mKNXControlBase instanceof KNXTimerButton) {
				String id = String.valueOf(mKNXControlBase.getId());
				if(id.equals(timerId)) {
					retval = true;
					break;
				}
			} else if(mKNXControlBase instanceof KNXGroupBox) {
				KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
				retval = isContainsTimer(mKNXGroupBox, timerId);
				if(retval) {
					break;
				}
			}
		}
		
		return retval;
	}
	
	@SuppressLint("UseSparseArrays")
	private void viewPagerSelectPage(final int position) {
		if(this.shouldDestroyActivity) {
			return;
		}
		
		if(null != this.currentPageKNXControlBase) {
			this.currentPageKNXControlBase.clear();
		}
	
		if(null != this.currentPageKNXControlBaseMap) {
			this.currentPageKNXControlBaseMap.clear();
		}
		
		//筛选出页面中能控制的按钮
		if(null == STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap()) {
			STKNXControllerApp.getInstance().setCurrentPageKNXControlBaseMap(new HashMap<Integer, KNXControlBase>());
		} else {
			STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().clear();
		}
		
		try {
			if((null != this.mPages) && (this.mPages.size() > position)) {
				this.mCurrentSTKNXPage = this.mPages.get(this.mCycleDrag && this.mPages.size()>1? position+1:position);
				getCurrentPageKNXControl(this.mCurrentSTKNXPage.getKNXPage());
//				Log.i(TAG, "position:"+position);
			}
		
			if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
				String defaultTimerId = STKNXControllerApp.getInstance().getLastTimerId();
				if((null != this.mCurrentSTKNXPage) && (!StringUtil.isEmpty(defaultTimerId))) {
					boolean bContains = isContainsTimer(this.mCurrentSTKNXPage.getKNXPage(), defaultTimerId);
					if (bContains) {
						final String timerId = "";
						STKNXControllerApp.getInstance().setLastTimerId(timerId);

						SharedPreferences.Editor editor = settings.edit();
						editor.putString(STKNXControllerConstant.LAST_TIMER_ID, timerId);
						editor.apply();

						Intent intent = new Intent(RoomDetailsActivity.this, TimingTaskActivity.class);
						intent.putExtra(STKNXControllerConstant.CONTROL_ID, defaultTimerId);
						startActivity(intent);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getCurrentPageKNXControl(KNXContainer container) {
		if(null == container) {
			return;
		}
		
		Map<String, Integer> addressIdIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		for (int x = 0; (x < container.getControls().size()) && (!this.shouldDestroyActivity); x++) {
			final KNXControlBase mKNXControlBase = container.getControls().get(x);
			boolean readStatus = false;

			if(null == mKNXControlBase) {
				continue;
			}
			
			//增加到当前页面所有控件集合
			if(null != this.currentPageKNXControlBase) {
				this.currentPageKNXControlBase.add(mKNXControlBase);
			}
			if((mKNXControlBase.getReadAddressId() != null) && 
					(mKNXControlBase.getReadAddressId().size() > 0) && 
					(null != this.currentPageKNXControlBaseMap)) {
				this.currentPageKNXControlBaseMap.put(
						mKNXControlBase, getFirstOrNull(mKNXControlBase.getReadAddressId()).getId());
			}
			
			if(mKNXControlBase instanceof KNXBlinds) { // KNXBlinds
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(), mKNXControlBase);
			} else if(mKNXControlBase instanceof KNXSwitch) { // KNXSwitch
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(), mKNXControlBase);
				readStatus = true;
			} else if(mKNXControlBase instanceof KNXSliderSwitch) { // KNXSliderSwitch
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(), mKNXControlBase);
				readStatus = true;
			} else if (mKNXControlBase instanceof KNXTimerButton) {
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(), mKNXControlBase);
			} else if(mKNXControlBase instanceof KNXDigitalAdjustment) {
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(), mKNXControlBase);
				readStatus = true;
			} else if(mKNXControlBase instanceof KNXGroupBox) {
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(),  mKNXControlBase);
				readStatus = true;
			} else if(mKNXControlBase instanceof KNXValueDisplay) {
				STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().put(mKNXControlBase.getId(),  mKNXControlBase);
				readStatus = true;
			}

			if(readStatus) {
				KNXSelectedAddress mKNXSelectedAddress = getFirstOrNull(mKNXControlBase.getReadAddressId()); // 获取对象的读地址
				int currentIndex;
				if (null != mKNXSelectedAddress) {
					String mETSId = mKNXSelectedAddress.getId();
					if ((null != mETSId) && (null != addressIdIndexMap) && (addressIdIndexMap.containsKey(mETSId))) {
						currentIndex = STKNXControllerApp.getInstance().getGroupAddressIndexMap().get(mETSId);
						KNXGroupAddress address = STKNXControllerApp.getInstance().getGroupAddressMap().get(currentIndex);
//					if(address.getIsCommunication() && address.getIsTransmit()) {
//					Log.i(TAG, "read status "+ mKNXControlBase.getText());
						byte[] contentBytes = new byte[32];
						byte[] length = new byte[1];
						KNX0X01Lib.UTestAndCopyObject(currentIndex, contentBytes, length);
						KNX0X01Lib.USetAndRequestObject(currentIndex);

						final int value = getCallBackValue(contentBytes, address);

						UIKit.runOnMainThreadAsync(new Runnable() {

							@Override
							public void run() {
								updateKNXControlStatus(mKNXControlBase, value);
							}
						});
					}
				}
			}

			if(mKNXControlBase instanceof KNXGroupBox) {
				KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
				getCurrentPageKNXControl(mKNXGroupBox);
			}
		}
	}
	
	private void updateKNXControlStatus(KNXControlBase mKNXControlBase, int value) {
		if(this.shouldDestroyActivity || (null == mKNXControlBase) || (null == mCurrentSTKNXPage)) {
			return;
		}
		
//		Log.i(TAG, "["+mKNXControlBase.getText()+"]"+"="+"["+value+"]");
		
		try {
			if(mKNXControlBase instanceof KNXBlinds) {

			} else if(mKNXControlBase instanceof KNXSwitch) { 
				STKNXSwitch mSTKNXSwitch = (STKNXSwitch) mCurrentSTKNXPage.findViewById(mKNXControlBase.getId()); // 获取LinearLayout控件 
				if(null != mSTKNXSwitch) {
					mSTKNXSwitch.setValue(value);
				}
			} else if(mKNXControlBase instanceof KNXSliderSwitch) {
				STKNXSliderSwitch mSliderSwitch = (STKNXSliderSwitch) mCurrentSTKNXPage.findViewById(mKNXControlBase.getId()); // 获取LinearLayout控件 
				if(null != mSliderSwitch) {
					mSliderSwitch.setProgress(value&0xFF);
				}
			} else if(mKNXControlBase instanceof KNXDigitalAdjustment) {
				STKNXDigitalAdjustment mSTKNXDigitalAdjustment = (STKNXDigitalAdjustment) mCurrentSTKNXPage.findViewById(mKNXControlBase.getId());
				if(null != mSTKNXDigitalAdjustment) {
					mSTKNXDigitalAdjustment.setValue(value);
				}
			} else if(mKNXControlBase instanceof KNXGroupBox) {
				STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox) mCurrentSTKNXPage.findViewById(mKNXControlBase.getId());
				if(null != mSTKNXGroupBox) {
					mSTKNXGroupBox.setSelectedValue(value);
				}
			} else if(mKNXControlBase instanceof KNXValueDisplay) {
				STKNXValueDisplay mSTKNXValueDisplay = (STKNXValueDisplay) mCurrentSTKNXPage.findViewById(mKNXControlBase.getId());
				if(null != mSTKNXValueDisplay) {
					mSTKNXValueDisplay.setValue(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private BroadcastReceiver updateDeviceStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			
			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_UPDATE_DEVICE_STATUS)/* &&
					SystemUtil.isForeground(RoomDetailsActivity.this, "com.sation.knxcontroller.activity.RoomDetailsActivity")*/) {
				Log.i(TAG, "1");
//				Log.i(TAG, "is main thread:"+((Looper.myLooper() != Looper.getMainLooper())?"false":"true"));
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
						int index = intent.getExtras().getInt(STKNXControllerConstant.GROUP_ADDRESS_INDEX, 0);
						byte[] array = intent.getExtras().getByteArray(STKNXControllerConstant.GROUP_ADDRESS_NEW_VALUE);
						try {
							KNXGroupAddress address = STKNXControllerApp.getInstance().getGroupAddressMap().get(index);
//							Log.i(TAG, "address:" + address);
							if(null != address) {
								String knxId = address.getId();
//								Log.i(TAG, "knxId:"+knxId);
								if((null != currentPageKNXControlBaseMap) && (null != knxId)) {
									Iterator iter = currentPageKNXControlBaseMap.entrySet().iterator();
									while (iter.hasNext()) {
										Map.Entry entry = (Map.Entry) iter.next();
										Object key = entry.getKey();
										Object val = entry.getValue();
										if(val.equals(knxId)) {
											KNXControlBase mKNXControlBase = (KNXControlBase) key;
											if(null != mKNXControlBase) {
												final int value  = getCallBackValue(array, address);
												Log.i(TAG, "received status of ["+mKNXControlBase.getText() +"] ... value:"+value);
												Log.i(TAG, "["+address.getStringKnxAddress()+"]"+"="+value);
												updateKNXControlStatus(mKNXControlBase, value);
											}
										}
									}
//									final KNXControlBase mKNXControlBase = currentPageKNXControlBaseMap.get(knxId);
////									Log.i(TAG, "mKNXControlBase:"+mKNXControlBase);
//									if(null != mKNXControlBase) {
//										final int value  = getCallBackValue(array, address);
//										Log.i(TAG, "received status of ["+mKNXControlBase.getText() +"] ... value:"+value);
////										UIKit.runOnMainThreadAsync(new Runnable() {
////
////											@Override
////											public void run() {
//										Log.i(TAG, "["+address.getStringKnxAddress()+"]"+"="+value);
//												updateKNXControlStatus(mKNXControlBase, value);
////											}
////										});
//									}
								}
							}
						} catch (Exception ex) { 
							ex.printStackTrace();
						}
//					}
//				}).start();
						Log.i(TAG, "2");
			}
		}
	};
	
	private int getCallBackValue(byte[] array, KNXGroupAddress address) {
		KNXDataType type = KNXDataType.values()[address.getType()];
		int value = 0;
		switch(type){
			case Bit1:
			case Bit2:
			case Bit3:
			case Bit4:
			case Bit5:
			case Bit6:
			case Bit7:
			case Bit8:
				value = array[0];
				break;
			case Bit16:
				if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
					value = (int)DPT9.bytes2float(array);
				} else {
					value = array[0]*100+array[1];
				}
				break;
			case Bit24:
				value = array[0]*10000+array[1]*100+array[2];
				break;
			
			case Bit32:
				if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_14)) {
					value = (int)DPT14.bytes2float(array);
				} else {
					value = array[0]*1000000+array[1]*10000+array[2]*100+array[3];
				}
				
				break;
			
			case Bit48:
			
				break;
			
			case Bit64:
			
				break;
			
			case Bit80:
			
				break;
			
			case Bit112:
			
				break;
			
			default:
				break;
		}
		
		return value;
	}
	
	/**
     * 获取map中第一个数据值
     *
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @param map 数据源
     * @return 返回的值
     */
    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
        
        if(null != map) {
        	for (Entry<K, V> entry : map.entrySet()) {
        		obj = entry.getValue();
        		if (obj != null) {
        			break;
        		}
        	}
        }
        
        return obj;
    }
    
    private TimerButtonOnClickListener buttonAddTimingTaskOnClickListener = new TimerButtonOnClickListener() {
		
    	@Override
		public void onClick(STKNXTimerButton button) {
			final String id = (String)button.getTag();
			if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
				STKNXControllerApp.getInstance().setLastTimerId(id);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(STKNXControllerConstant.LAST_TIMER_ID, id);
				editor.apply();

			}

			jumpToTimingTaskActivity(id);
		}
	};

	private void jumpToTimingTaskActivity(String id) {
		Intent intent = new Intent(this, TimingTaskActivity.class);
		intent.putExtra(STKNXControllerConstant.CONTROL_ID, id);
		startActivity(intent);

		overridePendingTransition(R.anim.scale_from_center,
				R.anim.scale_to_center);
	}
	
//	private BroadcastReceiver refreshTimerTaskListReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST)) {
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							for(TimingTaskListAdapter adapter : timingTaskAdapterList) {
//								adapter.notifyDataSetChanged();
//							}
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					}
//					
//				}).start();
				
//			}
//		}
//	};
}
