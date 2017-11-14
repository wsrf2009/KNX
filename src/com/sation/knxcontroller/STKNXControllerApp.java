package com.sation.knxcontroller;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXControlBaseDeserializerAdapter;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXVersion;
import com.sation.knxcontroller.util.BitUtils;
import com.sation.knxcontroller.util.ByteUtil;
import com.sation.knxcontroller.util.CompressStatus;
import com.sation.knxcontroller.util.FileUtils;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.NetWorkUtil;
import com.sation.knxcontroller.util.RebootSystem;
import com.sation.knxcontroller.util.ZipUtil;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import static com.sation.knxcontroller.util.KNX0X01Lib.UGetAndClearRequestState;
import static com.sation.knxcontroller.util.KNX0X01Lib.USetAndTransmitObject;
import static com.sation.knxcontroller.util.MapUtils.getFirstOrNull;
import static com.sation.knxcontroller.util.ZipUtil.NO_ERROR;

public class STKNXControllerApp extends Application implements Runnable {
	private final String TAG = "STKNXControllerApp"; 
	private static STKNXControllerApp app = null;
	private SharedPreferences settings;
	
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
				Log.i(TAG, "Read timing task successful!"+ " count = "+ timerTaskMap.size() + "  timerTaskMap:" + timerTaskMap);
			}
		}

		/* 定时重启 */
		settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		boolean reboot = settings.getBoolean(STKNXControllerConstant.SYSTEM_REBOOT_FLAG, 
				STKNXControllerConstant.SYSTEM_REBOOT_FLAG_VALUE);
		this.setAutoRebootFlag(reboot);
		int hour = settings.getInt(STKNXControllerConstant.SYSTEM_REBOOT_HOUR, 
				STKNXControllerConstant.SYSTEM_REBOOT_HOUR_VALUE);
		this.setHourOfReboot(hour);
		int minute = settings.getInt(STKNXControllerConstant.SYSTEM_REBOOT_MINUTE, 
				STKNXControllerConstant.SYSTEM_REBOOT_MINUTE_VALUE);
		this.setMinuteOfReboot(minute);

		/* 是否显示系统时间 */
		boolean display = settings.getBoolean(STKNXControllerConstant.DISPLAY_SYSTEM_TIME_FLAG,
				STKNXControllerConstant.DISPLAY_SYSTEM_TIME_FLAG_VALUE);
		this.setDisplayTimeFlag(display);

		/* 重启APP后是否返回到重启前APP所在界面 */
		boolean remember = settings.getBoolean(STKNXControllerConstant.REMEMBER_LAST_INTERFACE,
	    		STKNXControllerConstant.REMEMBER_LAST_INTERFACE_VALUE);
		this.setRememberLastInterface(remember);
		int areaIndex = settings.getInt(STKNXControllerConstant.LAST_AREA_INDEX, -1);
		this.setmLastAreaIndex(areaIndex);
		int roomIndex = settings.getInt(STKNXControllerConstant.LAST_ROOM_INDEX, -1);
		this.setLastRoomIndex(roomIndex);
		int pageIndex = settings.getInt(STKNXControllerConstant.LAST_PAGE_INDEX, 0);
		this.setLastPageIndex(pageIndex);
		String timerId = settings.getString(STKNXControllerConstant.LAST_TIMER_ID, "");
		this.setLastTimerId(timerId);
		
		startTimingTaskService();

		new Thread(this).start();
	}
	
	public void onDestroy() {
		STKNXControllerApp.app = null;
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

	    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	    // 1秒一个周期，不停的发送广播
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1*1000, pi);
		Log.i(TAG, "");
	}
		
	private KNXApp mKNXApp;
	public KNXApp getAppConfig() {
		return mKNXApp;
	} 
	public void setKNXAppConfig(KNXApp mKNXApp) {
		this.mKNXApp = mKNXApp;
	}

	private KNXVersion mKNXVersion;
	public KNXVersion getKNXVersion() {
		return mKNXVersion;
	}
	public void setKNXVersion(KNXVersion mKNXVersion) {
		this.mKNXVersion = mKNXVersion;
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
		try {
			FileUtils.writeObjectIntoFile(STKNXControllerApp.this, STKNXControllerConstant.FILE_TIMERTASK, timerTaskMap);
		} catch (IOException e) {

			Log.e(TAG, "保存定时任务失败" + "\n" + e.getLocalizedMessage() + "\n" + e.getMessage() + "\n" + e.getCause() + "\ntimerTaskMap:" + timerTaskMap);

			e.printStackTrace();
		}
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
	private int mLastAreaIndex;
	public int getmLastAreaIndex(){
		return this.mLastAreaIndex;
	}
	public void setmLastAreaIndex(int index) {
		this.mLastAreaIndex = index;
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

	@Override
	public void run() {
		do {
			try {
				Thread.sleep(5000);

				if(this.getAutoRebootFlag()) {
					final Calendar c = Calendar.getInstance();
					int hour = c.get(Calendar.HOUR_OF_DAY); // 24小时制的时间
					int minute = c.get(Calendar.MINUTE);
					int second = c.get(Calendar.SECOND);

					if((hour == STKNXControllerApp.getInstance().getHourOfRebooting()) &&
							(minute == STKNXControllerApp.getInstance().getMinuteOfReboot()) &&
							(second < 20)) {

						RebootSystem.Exec();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} while (true);
	}

	public void getProject(final Handler handler) {
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String zipFilePath = STKNXControllerConstant.ConfigFilePath;
						File zipFile = new File(zipFilePath);
						if (zipFile.exists()) { // 工程文件是否存在
							long localVersion = settings.getLong(STKNXControllerConstant.LOCALVERSION, 0);
							long lastlastModified = zipFile.lastModified();
							if (lastlastModified == 0 || lastlastModified != localVersion) { // 工程文件是否有更新
								SharedPreferences.Editor editor = settings.edit();
								editor.putLong(STKNXControllerConstant.LOCALVERSION, lastlastModified);
								editor.apply();

								int err = ZipUtil.unZipFileWithProgress(zipFile, // 解压工程文件
										STKNXControllerConstant.ProRootPath, false, null, false);
								if (NO_ERROR != err) {
									Message msg = new Message();
									msg.what = CompressStatus.ERROR_UNZIP;
									handler.sendMessage(msg);

									return;
								}
							}

							int versionIsValid = VertifyProjectVersion();
							if (CompressStatus.NO_ERROR != versionIsValid) {
								Message msg = new Message();
								msg.what = versionIsValid;
								handler.sendMessage(msg);
								return;
							}

							boolean projectIsValid = parseProjectFile();
							if (projectIsValid) {
								Message msg = new Message();
								msg.what = CompressStatus.PARSE_COMPLETED;
								handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = CompressStatus.ERROR_PARSE;
								handler.sendMessage(msg);
							}

						} else {
							Message msg = new Message();
							msg.what = CompressStatus.ERROR_NO_FILE;
							handler.sendMessage(msg);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
		}).start();
	}

	private KNXVersion getProjectVersion() {
		KNXVersion projectVersion = STKNXControllerApp.getInstance().getKNXVersion();
		if (null == projectVersion) {
			try {
				String jsonVersion = FileUtils.readFileSdcardFile(STKNXControllerConstant.VersionFilePath);
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				projectVersion = gson.fromJson(jsonVersion, KNXVersion.class);
				STKNXControllerApp.getInstance().setKNXVersion(projectVersion);
			} catch (Exception ex) {
				Log.e(ex.getLocalizedMessage());
			}
		}

		return projectVersion;
	}

	private int VertifyProjectVersion() {
		KNXVersion projectVersion = getProjectVersion();
		if (null != projectVersion) {
			String editorVersion = projectVersion.getEditorVersion();
			try {
				if (editorVersion.compareTo("2.1.5") < 0) { // 需要对控件的Title和Image进行兼容性转化
					return CompressStatus.ERROR_EDITORVERSION_215;
				} else if (editorVersion.compareTo("2.1.8") < 0) { // 需要对数字调节和数值显示的单位进行兼容转化
					return CompressStatus.ERROR_EDITORVERSION_218;
				} else if (editorVersion.compareTo("2.5.2") < 0) {
					return CompressStatus.ERROR_EDITORVERSION_252;
				} else if (editorVersion.compareTo("2.5.3") < 0) {
					return CompressStatus.ERROR_EDITORVERSION_253;
				} else if (editorVersion.compareTo("2.5.4") < 0) {
					return CompressStatus.ERROR_EDITORVERSION_254;
				} else if (editorVersion.compareTo("2.5.6") < 0) {
					return CompressStatus.ERROR_EDITORVERSION_256;
				} else if (editorVersion.compareTo("2.5.7") < 0) {
					return CompressStatus.ERROR_EDITORVERSION_257;
				} else {
					return CompressStatus.NO_ERROR;
				}
			} catch (Exception ex) {
				Log.e(TAG, ex.getLocalizedMessage());
			}
		}

		return CompressStatus.ERROR_INVALID_FILE;
	}

	private boolean parseProjectFile() {
		try {
			String json = FileUtils.readFileSdcardFile(STKNXControllerConstant.UiMetaFilePath);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
			Gson gson = gsonBuilder.create();
			KNXApp mKNXApp = gson.fromJson(json, KNXApp.class);
			STKNXControllerApp.getInstance().setKNXAppConfig(mKNXApp);
			String groupAddressJson = FileUtils.readFileSdcardFile(STKNXControllerConstant.GroupAddFilePath);
			List<KNXGroupAddress> mKNXGroupAddressList = gson.fromJson(groupAddressJson,
					new TypeToken<List<KNXGroupAddress>>() {
					}.getType());
			Collections.sort(mKNXGroupAddressList, new Comparator<KNXGroupAddress>() {
				@Override
				public int compare(KNXGroupAddress o1, KNXGroupAddress o2) {
					return (o2.getKnxAddress().compareTo(o1.getKnxAddress()));
				}
			});
			//索引号对应的组地址列表
			Map<Integer, KNXGroupAddress> sortGroupAddressMap = new HashMap<Integer, KNXGroupAddress>();
			//索引号对应的组地址列表
			Map<String, Integer> groupAddressIndexMap = new HashMap<String, Integer>();
			Map<String, KNXGroupAddress> groupAddressIdMap = new HashMap<String, KNXGroupAddress>();
			for (int i = 0; i < mKNXGroupAddressList.size(); i++) {
				sortGroupAddressMap.put(i + 1, mKNXGroupAddressList.get(i));
				groupAddressIndexMap.put(mKNXGroupAddressList.get(i).getId(), i + 1);
				groupAddressIdMap.put(mKNXGroupAddressList.get(i).getId(), mKNXGroupAddressList.get(i));
			}
			STKNXControllerApp.getInstance().setGroupAddressMap(sortGroupAddressMap);
			STKNXControllerApp.getInstance().setGroupAddressIndexMap(groupAddressIndexMap);
			STKNXControllerApp.getInstance().setGroupAddressIdMap(groupAddressIdMap);

			String fileName2 = STKNXControllerConstant.StructFilePath;
			String fileName = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator
					+ STKNXControllerConstant.StructFile;
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

			out.write(String.valueOf("SATIONGROUP Pad").getBytes());
			out.write((byte) 0);
			//组对象结构

			out.write(ByteUtil.getBytes((short) (mKNXGroupAddressList.size() + 1))); // 对象个数
			out.write(ByteUtil.getBytes((short) 0));                       //  /* 备用 */
			out.write(ByteUtil.getBytes(0));                             // /* 组队象数组指针 */
			//组关联表结构表数据
			out.write(ByteUtil.getBytes((short) 0));                           ///* 关联表个数 */
			out.write(ByteUtil.getBytes((short) 0));                           // /* 备用 */
			out.write(ByteUtil.getBytes(0));                             // /* 关联表所在位置指针*/
			//对象结构表数据

			//组地址
			byte[] phyAddress = new byte[6];
			out.write(phyAddress);

			int physicalAddressFirst = settings.getInt(
					STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_FIRST);
			int physicalAddressSecond = settings.getInt(
					STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_SECOND);
			int physicalAddressThree = settings.getInt(
					STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THIRD, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_THIRD);
			int physicalAddress = (physicalAddressFirst * 16 + physicalAddressSecond) * 256 + physicalAddressThree;
			out.write(ByteUtil.getBytes((short) physicalAddress));  ///* 备用 */

			byte[] phyAddress2 = new byte[4];
			out.write(phyAddress2);

			for (int j = 0; j < mKNXGroupAddressList.size(); j++) {
				byte config = 1;
				if (mKNXGroupAddressList.get(j).getPriority() == 0) {
					config = BitUtils.setBitValue(config, 0, (byte) 0);
					config = BitUtils.setBitValue(config, 1, (byte) 0);
				} else if (mKNXGroupAddressList.get(j).getPriority() == 1) {
					config = BitUtils.setBitValue(config, 0, (byte) 1);
					config = BitUtils.setBitValue(config, 1, (byte) 0);
				} else if (mKNXGroupAddressList.get(j).getPriority() == 2) {
					config = BitUtils.setBitValue(config, 0, (byte) 0);
					config = BitUtils.setBitValue(config, 1, (byte) 1);
				} else if (mKNXGroupAddressList.get(j).getPriority() == 3) {
					config = BitUtils.setBitValue(config, 0, (byte) 1);
					config = BitUtils.setBitValue(config, 1, (byte) 1);
				}

				config = BitUtils.setBitValue(config, 2, mKNXGroupAddressList.get(j).getIsCommunication()); //commuEnable
				config = BitUtils.setBitValue(config, 3, mKNXGroupAddressList.get(j).getIsRead());      //readEnable
				config = BitUtils.setBitValue(config, 4, mKNXGroupAddressList.get(j).getIsWrite());     //writeEnable
				config = BitUtils.setBitValue(config, 6, mKNXGroupAddressList.get(j).getIsTransmit());  //transEnable
				config = BitUtils.setBitValue(config, 7, mKNXGroupAddressList.get(j).getIsUpgrade());   //updateEnable

				out.write(config);

				out.write((byte) (mKNXGroupAddressList.get(j).getType()));  ///* 数据类型，长度    */
				if (mKNXGroupAddressList.get(j).getIsRead()) {
					out.write(ByteUtil.getBytes(mKNXGroupAddressList.get(j).getReadTimeSpan()));  ///* 读取轮训时间 */
				} else {
					out.write(ByteUtil.getBytes((short) 0));  ///* 读取轮训 */
				}
				out.write(ByteUtil.getBytes(mKNXGroupAddressList.get(j).getKnxAddress()));   //读地址
				out.write(ByteUtil.getBytes(mKNXGroupAddressList.get(j).getKnxAddress()));   //写地址
				out.write(ByteUtil.getBytes(j));   ///* 指向obj数值存储位置 */
				//out.writeShort(1);   //读地址
				//out.writeShort(2);   //写地址
			}
			out.close();

			FileUtils.CopyStatus status = FileUtils.copyFile(fileName, fileName2, true);
			if ((FileUtils.CopyStatus.COPY_SUCCESSFUL == status)
					|| (FileUtils.CopyStatus.SAME_PATH == status)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void loadLibrary() {
		KNX0X01Lib.UCLOSENet();

		//初始化连接
		final String mKNXGatewayIP = settings.getString(STKNXControllerConstant.KNX_GATEWAY_IP,
				STKNXControllerConstant.KNX_GATEWAY_DEFAULT);
		final int mKNXGatewayPort = settings.getInt(STKNXControllerConstant.KNX_GATEWAY_PORT,
				STKNXControllerConstant.KNX_GATEWAY_PORT_DEFAULT);
		final int mKNXUDPWorkWay = settings.getInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
				STKNXControllerConstant.KNX_UDP_WORK_WAY_DEFAULT);

		try {
			final int type = NetWorkUtil.getAPNType(this);

			new Thread(new Runnable() {

				@Override
				public void run() {
					KNX0X01Lib.SetNetworkType(type);
					boolean isConnect = KNX0X01Lib.UOPENNet(mKNXGatewayIP, mKNXGatewayPort, mKNXUDPWorkWay);
				}

			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
