package com.zyyknx.android;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack; 

import org.apache.http.util.EncodingUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder; 
import com.zyyknx.android.control.KNXControlBaseDeserializerAdapter;
import com.zyyknx.android.control.TimingTaskItem;
import com.zyyknx.android.control.KNXControlBase;
import com.zyyknx.android.models.KNXApp; 
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.services.KXNResponseService;
import com.zyyknx.android.util.BitmapLruCache;
import com.zyyknx.android.util.FileUtils;
import com.zyyknx.android.util.Log;
import com.zyyknx.android.widget.MyTimerTaskButton;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.text.TextUtils;

public class ZyyKNXApp extends Application {
	
	private SharedPreferences settings;
	
	/**
	 * Log or request TAG.
	 */
	public static final String TAG = "ZyyKNXApp"; 

	private static ZyyKNXApp app = null;
	private static Stack<Activity> activityStack;
	
	/*
	 * private EHomeApp() { token = ""; users = null; selectedStudent = null; }
	 */

	public final static synchronized ZyyKNXApp getInstance() {
		if (app == null) {
			app = new ZyyKNXApp();
		}
		return app;
	}

	@Override
	public void onCreate() { 
		// TODO Auto-generated method stub
		super.onCreate(); 
		app = this;  
//		Log.i(TAG, "onCreate()");
		settings = app.getSharedPreferences(ZyyKNXConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		
		mImageLoader = new ImageLoader(getRequestQueue(), new BitmapLruCache(getApplicationContext(), getCacheDir().getPath()));
		mRoundImageLoader = new ImageLoader(getRequestQueue(), new BitmapLruCache(getApplicationContext(), getCacheDir().getPath(), true));

		if (null == timerTaskMap) {
			/* 从文件中读取已保存的定时器任务 */
			try {
				timerTaskMap = (Map<String, List<TimingTaskItem>>)FileUtils.readObjectFromFile(this, ZyyKNXConstant.FILE_TIMERTASK);
			} catch (IOException e) {
				Log.w(ZyyKNXConstant.DEBUG, "读取定时任务失败"+" " +e.getLocalizedMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				Log.w(ZyyKNXConstant.DEBUG, "读取定时任务失败"+" " +e.getLocalizedMessage());
				e.printStackTrace();
			}
			
			if (null == timerTaskMap) { // 读取不成功
				timerTaskMap = new HashMap<String, List<TimingTaskItem>>(); // 创建定时器任务列表
			}
		}
		
		startTimingTaskService();
	}
	
	public void startGetKNXResponseService() {
//		Log.d(TAG, "start alarm"); 
		int knxRefreshStatusTimespan = settings.getInt(ZyyKNXConstant.KNX_REFRESH_STATUS_TIMESPAN, 1000); 
		if(knxRefreshStatusTimespan > 0) { 
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			Intent collectIntent = new Intent(this, KXNResponseService.class);
			PendingIntent collectSender  = PendingIntent.getService(this, 0, collectIntent, 0);
			am.cancel(collectSender);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), knxRefreshStatusTimespan, collectSender);
			//am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 5000, collectSender);
		}
	}
	
	public void startTimingTaskService() {
//		Log.d(TAG, "startTimingTaskService()");
		Intent intent = new Intent("com.zyyknx.android.services.TimingTaskService");
//	    intent.setAction("repeating");
	    PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);  
	    
	    // 开始时间
	    long firstime=SystemClock.elapsedRealtime();

	    AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
	    // 5秒一个周期，不停的发送广播
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1*1000, pi);
	}
	
	@Override
	public final void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public final void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Global request queue for Volley.
	 */
	private RequestQueue mRequestQueue;

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		} 
		return mRequestQueue;
	}

	private ImageLoader mImageLoader; 
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
	
	private ImageLoader mRoundImageLoader; 
	public ImageLoader getRoundImageLoader() {
		return mRoundImageLoader;
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	// manager the activity.
		/**
		 * add activity to stack.
		 * 
		 * @param activity
		 */
		public void pushActivity(Activity activity) {
			if (activityStack == null) {
				activityStack = new Stack<Activity>();
			}
			activityStack.add(activity);
		}

		/**
		 * finish all activity
		 */
		public void popAllActivity() {
			while (!activityStack.isEmpty()) {
				Activity activity = activityStack.lastElement();
				if (activity == null) {
					break;
				}

				activity.finish();
				activityStack.remove(activity);
				activity = null;
			}
		}

		// 遍历所有Activity并finish
		public void exit() {
			for (Activity activity : activityStack) {
				activity.finish();
			}
			// System.exit(0);
		}

		// 从assets 文件夹中获取文件并读取数据
		public String getFromAssets(String fileName) {
			String result = "";
			try {
				InputStream inputStream = getResources().getAssets().open(fileName);
				// 获取文件的字节数
				int lenght = inputStream.available();
				// 创建byte数组
				byte[] buffer = new byte[lenght];
				// 将文件中的数据读到byte数组中
				inputStream.read(buffer);
				result = EncodingUtils.getString(buffer, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		private KNXApp mKNXApp;
		public KNXApp getAppConfig() {
			if (mKNXApp == null) {
				String json = this.getFromAssets("controlapp.knxjson");

				GsonBuilder gsonBuilder = new GsonBuilder();
				//gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
				//gsonBuilder.registerTypeAdapter(Date.class, new GsonHelper.WCFDateDeserializer());
				gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
				Gson gson = gsonBuilder.create();
//				Log.d(ZyyKNXConstant.DEBUG, "KNXApp.class:"+KNXApp.class+"\njson:"+json);
				KNXApp tempKNXApp = gson.fromJson(json, KNXApp.class);
				
				ZyyKNXApp.getInstance().setKNXAppConfig(tempKNXApp);
			}
			return mKNXApp;
		} 
		public void setKNXAppConfig(KNXApp mKNXApp) {
			this.mKNXApp = mKNXApp;
		}
		
		
		//索引号对应的组地址列表
		private Map<Integer, KNXGroupAddress> mGroupAddressMap;
		public Map<Integer, KNXGroupAddress> getGroupAddressMap() { 
			return mGroupAddressMap;
		}
		public void setGroupAddressMap(Map<Integer, KNXGroupAddress> groupAddressMap) {
			this.mGroupAddressMap = groupAddressMap;
		} 
		 
		//索引号对应的组地址列表
		private Map<String, Integer> mGroupAddressIndexMap;
		public Map<String, Integer> getGroupAddressIndexMap() { 
			return mGroupAddressIndexMap;
		}
		public void setGroupAddressIndexMap(Map<String, Integer> groupAddressIndexMap) {
			this.mGroupAddressIndexMap = groupAddressIndexMap;
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
		
		public void saveTimerTask() {
			try {
				FileUtils.writeObjectIntoFile(this, ZyyKNXConstant.FILE_TIMERTASK, timerTaskMap);
			} catch (IOException e) {
				
				Log.e(ZyyKNXConstant.DEBUG, "保存定时任务失败"+"\n" +e.getLocalizedMessage()+"\n"+e.getMessage()+"\n"+e.getCause()+ "\ntimerTaskMap:"+timerTaskMap);
				
				e.printStackTrace();
			}
		}

}
