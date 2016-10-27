package com.sation.knxcontroller.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.util.EncodingUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXControlBaseDeserializerAdapter;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.util.BitUtils;
import com.sation.knxcontroller.util.ByteUtil;
import com.sation.knxcontroller.util.CompressStatus;
import com.sation.knxcontroller.util.CopyFileUtil;
import com.sation.knxcontroller.util.FileUtils;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.NetWorkUtil;
import com.sation.knxcontroller.util.ZipUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import net.lingala.zip4j.exception.ZipException;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
//	private final static int HEAP_SIZE = 6* 1024* 1024 ;
//    private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private ImageView splashabout;
	String zipFilePath;
	
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		this.splashabout = (ImageView) findViewById(R.id.splashabout);
		this.splashabout.setImageResource(R.drawable.companylogo);
		//设置最小  
//		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
//	    VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);  

		settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, MODE_PRIVATE);

		Resources resources = getResources();//获得res资源对象  
	    Configuration config = resources.getConfiguration();//获得设置对象  
	    DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。  
		String language = settings.getString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, "");
		Log.i(TAG, "language:" + language+"locale:"+Locale.CHINA);
		if(language.isEmpty()) {
			/* 若APP尚未配置语言 */
		    String locLanguage = config.locale.getLanguage();
		    SharedPreferences.Editor editor = settings.edit();
		    if(locLanguage.equals(Locale.CHINESE.toString())) {
		    	/* 系统设置为中文 */
		    	config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文  
			    editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toString());
				STKNXControllerApp.getInstance().setLanguage(Locale.SIMPLIFIED_CHINESE.toString());
		    } else {
		    	/* 系统设置为其他语言 */
		    	config.locale = Locale.US; // 美式英语
		    	editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
				STKNXControllerApp.getInstance().setLanguage(Locale.US.toString());
		    }
		    editor.commit(); 
		} else if(language.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
		    config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			STKNXControllerApp.getInstance().setLanguage(Locale.SIMPLIFIED_CHINESE.toString());
		} else {
		    config.locale = Locale.US; // 美式英语
			STKNXControllerApp.getInstance().setLanguage(Locale.US.toString());
		}
		resources.updateConfiguration(config, dm);
		
		long localVersion = settings.getLong(STKNXControllerConstant.LOCALVERSION, 0);
		try {
			zipFilePath = STKNXControllerConstant.ConfigFilePath; 
			File zipFile = new File(zipFilePath);
			boolean exist = zipFile.exists();
			if(!exist) {
				
			}
			long lastlastModified = zipFile.lastModified();
			if(lastlastModified == 0 ||  lastlastModified != localVersion) { 
				SharedPreferences.Editor editor = settings.edit(); 
				editor.putLong(STKNXControllerConstant.LOCALVERSION, lastlastModified);
				editor.commit();
				try {
					ZipUtil.unZipFileWithProgress(zipFile, STKNXControllerConstant.ProRootPath, handler, false);
				} catch (ZipException e) {
					e.printStackTrace();
				}
			} else {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						new AppConfigAsyncTask().execute();
					}
				}, 1000);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		System.gc();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CompressStatus.START:
				break;
			case CompressStatus.HANDLING:
				break;
			case CompressStatus.COMPLETED:
				Log.i(TAG, "language:" + "handleMessage():"+"CompressStatus.COMPLETED");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						new AppConfigAsyncTask().execute();
					}
				}, 1000);

				break;
			case CompressStatus.ERROR:
				break;
			}
		};
	};

	private void jumpActivity() {
		//加载KNX lib
//		KNX0X01Lib.loadLibraryTest();
//		KNX0X01Lib.UCLOSENet();
 
		//初始化连接
		final String mKNXGatewayIP = settings.getString(STKNXControllerConstant.KNX_GATEWAY_IP, 
				STKNXControllerConstant.KNX_GATEWAY_DEFAULT);
		final int mKNXGatewayPort = settings.getInt(STKNXControllerConstant.KNX_GATEWAY_PORT, 
				STKNXControllerConstant.KNX_GATEWAY_PORT_DEFAULT);
		final int mKNXUDPWorkWay = settings.getInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, 
				STKNXControllerConstant.KNX_UDP_WORK_WAY_DEFAULT);
		  
		try {
			Log.i(TAG, "1");
			final int type = NetWorkUtil.getAPNType(this);
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					
					Log.i(TAG, "2");
					KNX0X01Lib.SetNetworkType(type);
					Log.i(TAG, "3");
					boolean isConnect = KNX0X01Lib.UOPENNet(mKNXGatewayIP, mKNXGatewayPort, mKNXUDPWorkWay);
					Log.i(TAG, "4");
					Log.d(TAG, "jumpActivity()"+" mKNXGatewayIP: "+mKNXGatewayIP+
							" mKNXGatewayPort: "+mKNXGatewayPort+" mKNXUDPWorkWay: "+mKNXUDPWorkWay+
							" isConnect: " + isConnect);
				}
			
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Intent intent = new Intent(SplashActivity.this, RoomTilesListActivity.class);
		startActivity(intent);
		finish();
		
		Log.i(TAG, "5");
	}

	private class AppConfigAsyncTask extends AsyncTask<Void, Void, Void> {
		/** 在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。
		 * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息 
		 */
		@SuppressLint("UseSparseArrays")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String json = FileUtils.readFileSdcardFile(STKNXControllerConstant.UiMetaFilePath);
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
				Gson gson = gsonBuilder.create();
				KNXApp mKNXApp = gson.fromJson(json, /*new TypeToken<KNXApp>(){}.getType()*/KNXApp.class);
				STKNXControllerApp.getInstance().setKNXAppConfig(mKNXApp); 
				String groupAddressJson = FileUtils.readFileSdcardFile(STKNXControllerConstant.GroupAddFilePath);  
				List<KNXGroupAddress> mKNXGroupAddressList = gson.fromJson(groupAddressJson, 
						new TypeToken<List<KNXGroupAddress>>() { }.getType());
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

				String fileName2 =  STKNXControllerConstant.StructFilePath;  
				String fileName =  getApplicationContext().getFilesDir().getAbsolutePath()+File.separator
						+ STKNXControllerConstant.StructFile;  
//		        try {  
		            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		            
		            out.write(String.valueOf("SATIONGROUP Pad").getBytes());
		            out.write((byte)0);
		            //组对象结构 
		            		
		            out.write(ByteUtil.getBytes((short)(mKNXGroupAddressList.size()+ 1))); // 对象个数
		            out.write(ByteUtil.getBytes((short)0));                       //  /* 备用 */ 
		            out.write(ByteUtil.getBytes(0));                             // /* 组队象数组指针 */
		            //组关联表结构表数据
		            out.write(ByteUtil.getBytes((short)0));                           ///* 关联表个数 */
		            out.write(ByteUtil.getBytes((short)0));                           // /* 备用 */ 
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
					out.write(ByteUtil.getBytes((short)physicalAddress));  ///* 备用 */
					 
					byte[] phyAddress2 = new byte[4];
		            out.write(phyAddress2); 
		            
		            for (int j = 0; j < mKNXGroupAddressList.size(); j++) {
		            	byte config = 1;
		            	if(mKNXGroupAddressList.get(j).getPriority() == 0) {
		            		config = BitUtils.setBitValue(config, 0, (byte)0);
		            		config = BitUtils.setBitValue(config, 1, (byte)0);
		            	} else if(mKNXGroupAddressList.get(j).getPriority() == 1) {
		            		config = BitUtils.setBitValue(config, 0, (byte)1);
		            		config = BitUtils.setBitValue(config, 1, (byte)0);
		            	} else if(mKNXGroupAddressList.get(j).getPriority() == 2) {
		            		config = BitUtils.setBitValue(config, 0, (byte)0);
		            		config = BitUtils.setBitValue(config, 1, (byte)1);
		            	} else if(mKNXGroupAddressList.get(j).getPriority() == 3) {
		            		config = BitUtils.setBitValue(config, 0, (byte)1);
		            		config = BitUtils.setBitValue(config, 1, (byte)1);
			            }
		            	 
		            	config = BitUtils.setBitValue(config, 2, mKNXGroupAddressList.get(j).getIsCommunication()); //commuEnable 
		            	config = BitUtils.setBitValue(config, 3, mKNXGroupAddressList.get(j).getIsRead());      //readEnable             
		            	config = BitUtils.setBitValue(config, 4, mKNXGroupAddressList.get(j).getIsWrite());     //writeEnable        
		            	config = BitUtils.setBitValue(config, 6, mKNXGroupAddressList.get(j).getIsTransmit());  //transEnable         
		            	config = BitUtils.setBitValue(config, 7, mKNXGroupAddressList.get(j).getIsUpgrade());   //updateEnable          
		            	
			            out.write((byte)config);
			            
			            out.write((byte)(mKNXGroupAddressList.get(j).getType()));  ///* 数据类型，长度    */
			            if(mKNXGroupAddressList.get(j).getIsRead()) {
			            	out.write(ByteUtil.getBytes((short)mKNXGroupAddressList.get(j).getReadTimeSpan()));  ///* 读取轮训时间 */
			            } else {
			            	out.write(ByteUtil.getBytes((short)0));  ///* 读取轮训 */
			            }
			            out.write(ByteUtil.getBytes((short)mKNXGroupAddressList.get(j).getKnxAddress()));   //读地址
			            out.write(ByteUtil.getBytes((short)mKNXGroupAddressList.get(j).getKnxAddress()));   //写地址 
			            out.write(ByteUtil.getBytes(j));   ///* 指向obj数值存储位置 */ 
			            //out.writeShort(1);   //读地址
			            //out.writeShort(2);   //写地址 
		            } 
		            out.close();  
		            
//		        } catch (Exception e) {  
//		            e.printStackTrace();  
//		        }   
				 
		        CopyFileUtil.copyFile(fileName, fileName2, true);
		        
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("debug", e.getLocalizedMessage());
			} 
			
			return null;
		}

		/** 在execute方法被执行后，即执行此方法。通常是对UI做一些标记 */
		@Override
		protected void onPreExecute() {
			// msgView.setText(getString(R.string.initialMenuMsg));
			super.onPreExecute();
		}

		/** 当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。 */
		@Override
		protected void onPostExecute(Void result) {
			Log.i(TAG, "");

			try {
				byte[] b = new byte[8096];
				DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(STKNXControllerConstant.StructFilePath)));  
				int len = dos.read(b);
//				Log.i(TAG, "len:"+len+"   " + new String(b, "UTF-8"));
//				System.out.println(new String(b, 0, len));
				System.out.println("len = " + len);
				for(int i=0; i<len;i++) {
					System.out.print(String.valueOf(b[i]));
				}
				System.out.println();
				dos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	        
			jumpActivity();
			super.onPostExecute(result);
		}

		/** 在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上 */
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
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

}
