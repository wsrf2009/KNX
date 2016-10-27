package com.sation.knxcontroller;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXControlBaseDeserializerAdapter;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.services.RestartService;
import com.sation.knxcontroller.util.FileUtils;
import com.sation.knxcontroller.util.Log;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class STKNXControllerApp extends Application {
	private final String TAG = "STKNXControllerApp"; 
	private static STKNXControllerApp app = null;
	
	public final static synchronized STKNXControllerApp getInstance() {
		if (app == null) {
			app = new STKNXControllerApp();
		}
		return app;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() { 
		super.onCreate();

//		Fresco.initialize(this);
		app = this;  

		if (null == timerTaskMap) {
			/* 从文件中读取已保存的定时器任务 */
			try {
				timerTaskMap = (Map<String, List<TimingTaskItem>>)FileUtils.readObjectFromFile(this, STKNXControllerConstant.FILE_TIMERTASK/*+"_"+SystemUtil.getVersionCode(this)+".dat"*/);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			if (null == timerTaskMap) {      // 读取不成功
				Log.i(TAG, "create timing task list");
				timerTaskMap = new HashMap<String, List<TimingTaskItem>>(); // 创建定时器任务列表
			} else {
				Log.i(TAG, "Read timing task successful!"+ " count = "+ timerTaskMap.size());
			}
		}
		
		SharedPreferences settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		boolean reboot = settings.getBoolean(STKNXControllerConstant.SYSTEM_REBOOT_FLAG, 
				STKNXControllerConstant.SYSTEM_REBOOT_FLAG_VALUE);
		this.setAutoRebootFlag(reboot);
		int hour = settings.getInt(STKNXControllerConstant.SYSTEM_REBOOT_HOUR, 
				STKNXControllerConstant.SYSTEM_REBOOT_HOUR_VALUE);
		this.setHourOfReboot(hour);
		int minute = settings.getInt(STKNXControllerConstant.SYSTEM_REBOOT_MINUTE, 
				STKNXControllerConstant.SYSTEM_REBOOT_MINUTE_VALUE);
		this.setMinuteOfReboot(minute);
		
		boolean display = settings.getBoolean(STKNXControllerConstant.DISPLAY_SYSTEM_TIME_FLAG,
				STKNXControllerConstant.DISPLAY_SYSTEM_TIME_FLAG_VALUE);
		this.setDisplayTimeFlag(display);

		boolean remember = settings.getBoolean(STKNXControllerConstant.REMEMBER_LAST_INTERFACE,
	    		STKNXControllerConstant.REMEMBER_LAST_INTERFACE_VALUE);
		this.setRememberLastInterface(remember);
		int roomIndex = settings.getInt(STKNXControllerConstant.LAST_ROOM_INDEX, -1);
		this.setLastRoomIndex(roomIndex);
		int pageIndex = settings.getInt(STKNXControllerConstant.LAST_PAGE_INDEX, 0);
		this.setLastPageIndex(pageIndex);
		String timerId = settings.getString(STKNXControllerConstant.LAST_TIMER_ID, "");
		this.setLastTimerId(timerId);
		
		startTimingTaskService();
	}
	
	public void onDestroy() {
		STKNXControllerApp.app = null;
//		STKNXControllerApp.activityStack = null;
		this.mKNXApp = null;
		this.mGroupAddressMap = null;
		this.mGroupAddressIndexMap = null;
		this.currentPageKNXControlBaseMap = null;
		this.mGroupAddressIdMap = null;
		this.timerTaskMap = null;
	}
	
	public void startTimingTaskService() {
		Intent intent = new Intent("com.sation.knxcontroller.broadcastreceiver.AlarmMonitor");
	    PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);  

	    AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
	    // 1秒一个周期，不停的发送广播
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1*1000, pi);
	}

//	// 从assets 文件夹中获取文件并读取数据
//	public String getFromAssets(String fileName) {
//		String result = "";
//		try {
//			InputStream inputStream = getResources().getAssets().open(fileName);
//			// 获取文件的字节数
//			int lenght = inputStream.available();
//			// 创建byte数组
//			byte[] buffer = new byte[lenght];
//			// 将文件中的数据读到byte数组中
//			inputStream.read(buffer);
//			result = EncodingUtils.getString(buffer, "UTF-8");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
		
	private KNXApp mKNXApp;
	public KNXApp getAppConfig() {
//		if (mKNXApp == null) {
//			try {
//			String json = this.getFromAssets("controlapp.knxjson");
//
//			GsonBuilder gsonBuilder = new GsonBuilder();
//			gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
//			Gson gson = gsonBuilder.create();
//			KNXApp tempKNXApp = gson.fromJson(json, KNXApp.class);
//				
//			STKNXControllerApp.getInstance().setKNXAppConfig(tempKNXApp);
			
//				String json = FileUtils.readFileSdcardFile(STKNXControllerConstant.UiMetaFilePath);
//				GsonBuilder gsonBuilder = new GsonBuilder();
//				gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
//				Gson gson = gsonBuilder.create();
//				KNXApp mKNXApp = gson.fromJson(json, /*new TypeToken<KNXApp>(){}.getType()*/KNXApp.class);
//				setKNXAppConfig(mKNXApp);

//				startService(new Intent(this, RestartService.class));
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
		return mKNXApp;
	} 
	public void setKNXAppConfig(KNXApp mKNXApp) {
		this.mKNXApp = mKNXApp;
	}	
		
	/* 索引号组地址映射表<索引号， 组地址> */
	private Map<Integer, KNXGroupAddress> mGroupAddressMap;
	public Map<Integer, KNXGroupAddress> getGroupAddressMap() { 
		return mGroupAddressMap;
	}
	public void setGroupAddressMap(Map<Integer, KNXGroupAddress> groupAddressMap) {
		this.mGroupAddressMap = groupAddressMap;
	}
		 
	/* 组地址Id、索引号映射表<组地址Id，索引号> */
	private Map<String, Integer> mGroupAddressIndexMap;
	public Map<String, Integer> getGroupAddressIndexMap() { 
		return mGroupAddressIndexMap;
	}
	public void setGroupAddressIndexMap(Map<String, Integer> groupAddressIndexMap) {
		this.mGroupAddressIndexMap = groupAddressIndexMap;
	}
	
	/* 组地址Id、组地址索引表<组地址Id，组地址> */
	private Map<String, KNXGroupAddress> mGroupAddressIdMap;
	public Map<String, KNXGroupAddress> getGroupAddressIdMap() {
		return mGroupAddressIdMap;
	}
	public void setGroupAddressIdMap(Map<String, KNXGroupAddress> map) {
		mGroupAddressIdMap = map;
	}

	private Map<Integer, KNXControlBase> currentPageKNXControlBaseMap;
	public Map<Integer, KNXControlBase> getCurrentPageKNXControlBaseMap() { 
		return currentPageKNXControlBaseMap;
	}
	public void setCurrentPageKNXControlBaseMap(Map<Integer, KNXControlBase> mKNXControlBaseMap) {
		this.currentPageKNXControlBaseMap = mKNXControlBaseMap;
	} 
		
	private Map<String, List<TimingTaskItem>> timerTaskMap;
	public Map<String, List<TimingTaskItem>> getTimerTaskMap() {
		return timerTaskMap;
	}
	public void setTimerTaskMap(Map<String, List<TimingTaskItem>> mTimerTaskMap) {
		this.timerTaskMap = mTimerTaskMap;
	}
	
	private Map<String, Bitmap> mImageMap;
	public Map<String, Bitmap> getImageMap() {
		return mImageMap;
	}
	public void setImageMap(Map<String, Bitmap> imageMap) {
		this.mImageMap = imageMap;
	}
		
	public void saveTimerTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					FileUtils.writeObjectIntoFile(STKNXControllerApp.this, STKNXControllerConstant.FILE_TIMERTASK, timerTaskMap);
				} catch (IOException e) {
						
					Log.e(TAG, "保存定时任务失败"+"\n" +e.getLocalizedMessage()+"\n"+e.getMessage()+"\n"+e.getCause()+ "\ntimerTaskMap:"+timerTaskMap);
						
					e.printStackTrace();
				}
			}
		}).start();
	}

	/* 定时重启系统 */
	private boolean mAutoRebootFlag; // 定时重启标志
	public boolean getAutoRebootFlag() {
		return this.mAutoRebootFlag;
	}
	public void setAutoRebootFlag(boolean reboot) {
		this.mAutoRebootFlag = reboot;
	}
	private int mHourOfReboot; // 重启时间：时
	public int getHourOfRebooting() {
		return this.mHourOfReboot;
	}
	public void setHourOfReboot(int hour) {
		this.mHourOfReboot = hour;
	}
	private int mMinuteOfReboot; // 重启时间：分
	public int getMinuteOfReboot() {
		return this.mMinuteOfReboot;
	}
	public void setMinuteOfReboot(int minute) {
		this.mMinuteOfReboot = minute;
	}
	
	/* 主界面显示系统时间 */
	private boolean mDisplayTimeFlag; // 是否在主界面显示系统时间的标志
	public boolean getDisplayTimeFlag() {
		return this.mDisplayTimeFlag;
	}
	public void setDisplayTimeFlag(boolean flag) {
		this.mDisplayTimeFlag = flag;
	}
	
	/* 记住最后一个界面 */
	private boolean mRememberLastInterface;
	public boolean getRememberLastInterface() {
		return this.mRememberLastInterface;
	}
	public void setRememberLastInterface(boolean remember) {
		this.mRememberLastInterface = remember;
	}
	private int mLastRoomIndex;
	public int getLastRoomIndex() {
		return this.mLastRoomIndex;
	}
	public void setLastRoomIndex(int index) {
		this.mLastRoomIndex = index;
	}
	private int mLastPageIndex;
	public int getLastPageIndex() {
		return this.mLastPageIndex;
	}
	public void setLastPageIndex(int index) {
		this.mLastPageIndex = index;
	}
	private String mLastTimerId;
	public String getLastTimerId() {
		return this.mLastTimerId;
	}
	public void setLastTimerId(String Id) {
		this.mLastTimerId = Id;
	}

	/* 系统语言 */
	private String mLanguage;
	public String getLanguage() {
		return this.mLanguage;
	}
	public void setLanguage(String language) {
		this.mLanguage = language;
	}
}
