package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter;
import com.sation.knxcontroller.control.KNXBlinds;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.control.KNXSwitch;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.control.KNXValueDisplay;
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
import com.sation.knxcontroller.widget.STKNXDigitalAdjustment;
import com.sation.knxcontroller.widget.STKNXGroupBox;
import com.sation.knxcontroller.widget.STKNXPage;
import com.sation.knxcontroller.widget.STKNXSliderSwitch;
import com.sation.knxcontroller.widget.STKNXSwitch;
import com.sation.knxcontroller.widget.STKNXTimerButton;
import com.sation.knxcontroller.widget.STKNXTimerButton.TimerButtonOnClickListener;
import com.sation.knxcontroller.widget.STKNXValueDisplay;
import com.sation.knxcontroller.widget.STKNXView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class RoomDetailsActivity extends BaseActivity {
	//当前页面所有控件集合
	List<KNXControlBase> currentPageKNXControlBase = new ArrayList<KNXControlBase>();
	Map<String, KNXControlBase> currentPageKNXControlBaseMap = new HashMap<String, KNXControlBase>(); 
	private RefreshTimerTaskListReceiver mRefreshTimerTaskListReceiver;
	
	private List<TimingTaskListAdapter> timingTaskAdapterList = new ArrayList<TimingTaskListAdapter>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.room_details_layout);

		KNX0X01Lib.setContext(this);
		IntentFilter intentFilter = new IntentFilter();
		//设备状态的广播
		intentFilter.addAction(STKNXControllerConstant.BROADCAST_UPDATE_DEVICE_STATUS);
		registerReceiver(updateDeviceStateReceiver, intentFilter);
		
		initialComponent();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		for(TimingTaskListAdapter adapter : timingTaskAdapterList) {
			adapter.notifyDataSetChanged();
		}
		
		mRefreshTimerTaskListReceiver = new RefreshTimerTaskListReceiver();
	    IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
	    registerReceiver(mRefreshTimerTaskListReceiver, filter);  
	}
	
	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(mRefreshTimerTaskListReceiver);
	}
	
	private void initialComponent() {
		KNXRoom mKNXRoom = (KNXRoom) getIntent().getSerializableExtra(STKNXControllerConstant.REMOTE_PARAM_KEY);
		RelativeLayout layContent = (RelativeLayout) findViewById(R.id.layContent);

		if (mKNXRoom.getPages() != null && mKNXRoom.getPages().size() > 0 ) { 
				
			for (int i = 0; i < mKNXRoom.getPages().size(); i++) {

				KNXPage mKNXPage = mKNXRoom.getPages().get(i);
				
				STKNXPage page = new STKNXPage(this, mKNXPage);
				LayoutParams pageLayoutParams = new LayoutParams(mKNXPage.Width, mKNXPage.Height); 
				pageLayoutParams.leftMargin = mKNXPage.Left;
				pageLayoutParams.topMargin = mKNXPage.Top;
				page.setLayoutParams(pageLayoutParams);
				layContent.addView(page, pageLayoutParams);
				
				//增加页面级别的控件
				createSTKNXControlAndDisplay(page, mKNXPage);
			}
		}

		//筛选出场景按钮能控制的按钮
		Map<Integer,KNXControlBase> mKNXControls = getSceneButtonControl(currentPageKNXControlBase);
		STKNXControllerApp.getInstance().setCurrentPageKNXControlBaseMap(mKNXControls);
	}
	
	private void createSTKNXControlAndDisplay(STKNXView parentView, KNXContainer knxContainer) {
		for (int x = 0; x < knxContainer.getControls().size(); x++) {
			KNXControlBase mKNXControlBase = knxContainer.getControls().get(x);
			//增加到当前页面所有控件集合
			currentPageKNXControlBase.add(mKNXControlBase);
			if(mKNXControlBase.getReadAddressId() != null && mKNXControlBase.getReadAddressId().size() > 0) {
				currentPageKNXControlBaseMap.put(getFirstOrNull(mKNXControlBase.getReadAddressId()).getId(), mKNXControlBase);
			}
			
			STKNXView view = KNXControlBase.buildWithControl(this, mKNXControlBase);
			if (view != null) {
				if(view instanceof STKNXTimerButton) {
					String id = String.valueOf(mKNXControlBase.getId());
					view.setTag(id); // 当前控件的ID
					((STKNXTimerButton) view).setOnClickListener(buttonAddTimingTaskOnClickListener);
				}
				
				parentView.addView(view);
				
				if(mKNXControlBase instanceof KNXGroupBox) {
					KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
					createSTKNXControlAndDisplay(view, mKNXGroupBox);
				}
			 }
		}
	}
	
	private Map<Integer,KNXControlBase> getSceneButtonControl(List<KNXControlBase> currentPageKNXControlBase) {
		Map<Integer, KNXControlBase> allBaseControl = new HashMap<Integer, KNXControlBase>();
		Map<String, Integer> groupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		
		for (int i = 0; i < currentPageKNXControlBase.size(); i++) {
			KNXControlBase knxControl = currentPageKNXControlBase.get(i);
//			Log.e("RoomDetailsActivity", "Name:"+knxControl.getText());
			KNXSelectedAddress mKNXSelectedAddress = getFirstOrNull(knxControl.getReadAddressId()); // 获取对象的读地址
			int currentIndex = 0;
			int currentValue = -1;
			if(mKNXSelectedAddress != null) {
				String mETSId = mKNXSelectedAddress.getId();
				if(groupAddressIndexMap.containsKey(mETSId)) {
					currentIndex = STKNXControllerApp.getInstance().getGroupAddressIndexMap().get(mETSId);
					KNXGroupAddress address = STKNXControllerApp.getInstance().getGroupAddressMap().get(currentIndex);
					if(address.getIsCommunication() && address.getIsRead()) {
						byte[] contentBytes = new byte[32];
						byte[] length  = new byte[1];
						boolean b1 = KNX0X01Lib.UTestAndCopyObject(currentIndex, contentBytes, length);
//					currentValue = contentBytes[0]*1000000+contentBytes[1]*10000+contentBytes[2]*100+contentBytes[0];
						boolean b2 = KNX0X01Lib.USetAndRequestObject(currentIndex);
					
//					Log.e("RoomDetailsActivity", "b1:"+b1+" b2:"+b2);
					
						int type = mKNXSelectedAddress.getType();
						currentValue = getCallBackValue(contentBytes, address);
					}
//					Log.e("RoomDetailsActivity", "add name:"+mKNXSelectedAddress.getName());
//					Log.e("RoomDetailsActivity", "currentValue:"+currentValue);
				}  
			}
			 
			if(knxControl instanceof KNXBlinds) { // KNXBlinds
//				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
				allBaseControl.put(knxControl.getId(), knxControl);
			} else if(knxControl instanceof KNXSwitch) { // KNXSwitch
				allBaseControl.put(knxControl.getId(), knxControl);
				
				if(currentIndex >0) {
					STKNXSwitch mSTKNXSwitch = (STKNXSwitch) findViewById(knxControl.getId()); // 获取LinearLayout控件 
					mSTKNXSwitch.setValue(currentValue);
				}
			} else if(knxControl instanceof KNXSliderSwitch) { // KNXSliderSwitch
				allBaseControl.put(knxControl.getId(), knxControl);
			
				if(currentIndex >0) {
					STKNXSliderSwitch mSTKNXSliderSwitch = (STKNXSliderSwitch) findViewById(knxControl.getId()); // 获取LinearLayout控件 
					mSTKNXSliderSwitch.setProgress(currentValue&0xFF);
				}
			} else if (knxControl instanceof KNXTimerButton) {
				allBaseControl.put(knxControl.getId(), knxControl);
			} else if(knxControl instanceof KNXDigitalAdjustment) {
				allBaseControl.put(knxControl.getId(), knxControl);
				STKNXDigitalAdjustment mSTKNXDigitalAdjustment = (STKNXDigitalAdjustment) findViewById(knxControl.getId());
				mSTKNXDigitalAdjustment.setValue(currentValue);
			} else if(knxControl instanceof KNXGroupBox) {
				allBaseControl.put(knxControl.getId(),  knxControl);
				STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox) findViewById(knxControl.getId());
				mSTKNXGroupBox.setSelectedValue(currentValue);
			} else if(knxControl instanceof KNXValueDisplay) {
				allBaseControl.put(knxControl.getId(),  knxControl);
				STKNXValueDisplay mSTKNXValueDisplay = (STKNXValueDisplay) findViewById(knxControl.getId());
				mSTKNXValueDisplay.setValue(currentValue);
			}
		}
		return allBaseControl;
	}

	private BroadcastReceiver updateDeviceStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_UPDATE_DEVICE_STATUS)){ 
				int index = intent.getExtras().getInt(STKNXControllerConstant.GROUP_ADDRESS_INDEX, 0);
				int valLength = intent.getExtras().getInt(STKNXControllerConstant.GROUP_ADDRESS_NEW_VALUE_LENGTH, 0);
				byte[] array = intent.getExtras().getByteArray(STKNXControllerConstant.GROUP_ADDRESS_NEW_VALUE);
				try {
					KNXGroupAddress address = STKNXControllerApp.getInstance().getGroupAddressMap().get(index);
					String knxId = address.getId();
					int type = address.getType();
					int value  = getCallBackValue(array, address);

					Log.i(STKNXControllerConstant.CALLBACK, "当前的knxId："+ knxId +"");
					
					KNXControlBase mKNXControlBase = currentPageKNXControlBaseMap.get(knxId);
					int controlId = mKNXControlBase.getId(); 
					if(mKNXControlBase instanceof KNXBlinds) {
						 
					} else if(mKNXControlBase instanceof KNXSwitch) { 
						STKNXSwitch mSTKNXSwitch = (STKNXSwitch) findViewById(controlId); // 获取LinearLayout控件 
						mSTKNXSwitch.setValue(value);
					
					} else if(mKNXControlBase instanceof KNXSliderSwitch) {
						STKNXSliderSwitch mSliderSwitch = (STKNXSliderSwitch) findViewById(controlId); // 获取LinearLayout控件 
						mSliderSwitch.setProgress(value&0xFF);
					} else if(mKNXControlBase instanceof KNXDigitalAdjustment) {
						STKNXDigitalAdjustment mSTKNXDigitalAdjustment = (STKNXDigitalAdjustment) findViewById(controlId);
						mSTKNXDigitalAdjustment.setValue(value);
					} else if(mKNXControlBase instanceof KNXGroupBox) {
						Log.e("KNXGroupBox", "value===>"+(value));
						STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox) findViewById(controlId);
						mSTKNXGroupBox.setSelectedValue(value);
					} else if(mKNXControlBase instanceof KNXValueDisplay) {
						STKNXValueDisplay mSTKNXValueDisplay = (STKNXValueDisplay) findViewById(controlId);
						mSTKNXValueDisplay.setValue(value);
					}
				} catch (Exception e) { 
					
				}
				 
			}
		}
	};
	
	private int getCallBackValue(byte[] array, KNXGroupAddress address){
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
				if(KNXDatapointType.DPT_9 ==  address.getKnxMainNumber()) {
					value = (int)DPT9.getFloat(array);
				} else {
					value = array[0]*100+array[1];
				}
				break;
			case Bit24:
				value = array[0]*10000+array[1]*100+array[2];
				break;
			
			case Bit32:
				value = array[0]*1000000+array[1]*10000+array[2]*100+array[3];
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
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy(); 
		unregisterReceiver(updateDeviceStateReceiver); 
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
        for (Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }
    
    TimerButtonOnClickListener buttonAddTimingTaskOnClickListener = new TimerButtonOnClickListener() {
		
    	@Override
		public void onClick(STKNXTimerButton button) {
			String id = (String)button.getTag();
			Intent intent = new Intent(RoomDetailsActivity.this, TimingTaskActivity.class);
			intent.putExtra(STKNXControllerConstant.CONTROL_ID, id);
			startActivity(intent);
		}
	};
	
	private class RefreshTimerTaskListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST)) {
				for(TimingTaskListAdapter adapter : timingTaskAdapterList) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}