package com.zyyknx.android.activity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException; 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.util.EncodingUtils;

import net.lingala.zip4j.exception.ZipException; 
  
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.control.*;
import com.zyyknx.android.models.KNXApp;
import com.zyyknx.android.models.KNXArea;
import com.zyyknx.android.models.KNXGrid;
import com.zyyknx.android.models.KNXGroupAddress; 
import com.zyyknx.android.models.KNXPage;
import com.zyyknx.android.models.KNXRoom;
import com.zyyknx.android.util.BitUtils;
import com.zyyknx.android.util.ByteUtil;
import com.zyyknx.android.util.CompressStatus; 
import com.zyyknx.android.util.CopyFileUtil;
import com.zyyknx.android.util.KNX0X01Lib;
import com.zyyknx.android.util.Log;
import com.zyyknx.android.util.NetWorkUtil;
import com.zyyknx.android.util.ZipUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	String zipFilePath;
	
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash); 
		 
		/*
		 * boolean ss1 = KNX0X01Lib.UOPENNet("192.168.1.2", 8999, 1);
		 * 
		 * boolean ss2 = KNX0X01Lib.UCLOSENet();
		 * 
		 * boolean ss3 = KNX0X01Lib.UTestAndCopyObject(1, 1);
		 * 
		 * boolean ss4 = KNX0X01Lib.USetAndRequestObject(1);
		 * 
		 * boolean ss5 = KNX0X01Lib.USetAndTransmitObject(1, "11", 1);
		 * 
		 * boolean ss6 = KNX0X01Lib.UGetAndClearRequestState(1);
		 */
		settings = getSharedPreferences(ZyyKNXConstant.SETTING_FILE, MODE_PRIVATE);
		long localVersion = settings.getLong(ZyyKNXConstant.LOCALVERSION, 0);
		
		try {
			//加载KNX lib
			//KNX0X01Lib.loadLibrary();
			KNX0X01Lib.loadLibraryTest();
			KNX0X01Lib.UCLOSENet();
			
			zipFilePath = Environment.getExternalStorageDirectory().toString() + "/KnxUiProject.knxuie"; 
			File zipFile = new File(zipFilePath);
			long lastlastModified = zipFile.lastModified();
			if(lastlastModified == 0 ||  lastlastModified != localVersion) { 
				SharedPreferences.Editor editor = settings.edit(); 
				editor.putLong(ZyyKNXConstant.LOCALVERSION, lastlastModified);
				editor.commit();
				try {
					ZipUtil.unZipFileWithProgress(zipFile, Environment.getExternalStorageDirectory() + "/zyyknxandroid/.nomedia/", handler, false);
				} catch (ZipException e) {
					e.printStackTrace();
				}
				
			} else {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// startPushService();
						new AppConfigAsyncTask().execute();
					}
				}, 1000);
			} 
			 
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CompressStatus.START:
				// tv.setText("开始解压!");
				break;
			case CompressStatus.HANDLING:
				Bundle b = msg.getData();
				// tv.setText(b.getInt(CompressStatus.PERCENT)+" ");
				break;
			case CompressStatus.COMPLETED:
				// tv.setText("解压完成!");
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// startPushService();
						new AppConfigAsyncTask().execute();
					}
				}, 1000);

				break;
			case CompressStatus.ERROR:
				// tv.setText("解压完成");
				break;
			}
		};
	};

	private void jumpActivity() {
		//加载KNX lib
		//KNX0X01Lib.loadLibrary();
		//KNX0X01Lib.loadLibraryTest();
		//KNX0X01Lib.UCLOSENet();
		
		
		//初始化连接
		String mKNXGatewayIP = settings.getString(ZyyKNXConstant.KNX_GATEWAY_IP, 
				ZyyKNXConstant.KNX_GATEWAY_DEFAULT);
		int mKNXGatewayPort = settings.getInt(ZyyKNXConstant.KNX_GATEWAY_PORT, 
				ZyyKNXConstant.KNX_GATEWAY_PORT_DEFAULT);
		int mKNXUDPWorkWay = settings.getInt(ZyyKNXConstant.KNX_UDP_WORK_WAY, 
				ZyyKNXConstant.KNX_UDP_WORK_WAY_DEFAULT);
		
		KNX0X01Lib.SetNetworkType(NetWorkUtil.getAPNType(this));
		boolean isConnect = KNX0X01Lib.UOPENNet(mKNXGatewayIP, mKNXGatewayPort, mKNXUDPWorkWay);
		
		Log.d(ZyyKNXConstant.DEBUG, "jumpActivity()"+" mKNXGatewayIP: "+mKNXGatewayIP+
				" mKNXGatewayPort: "+mKNXGatewayPort+" mKNXUDPWorkWay: "+mKNXUDPWorkWay+
				" isConnect: " + isConnect);
		
		Intent intent = new Intent(SplashActivity.this, RoomTilesListActivity.class);
		startActivity(intent);
		finish();
	}

	// 读SD中的文件
	public String readFileSdcardFile(String fileName) throws IOException {
		String res = "";
		try {
			
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private class AppConfigAsyncTask extends AsyncTask<Void, Void, Void> {
		
		public AppConfigAsyncTask() {

		}

		/** 在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。在执行过程中可以调用publishProgress(Progress... values)来更新进度信息 */
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// String json =
				// ZyyKNXApp.getInstance().getFromAssets("KNXUI_Data.knxjson");
				//String json = ZyyKNXApp.getInstance().getFromAssets("KNXUI_Data.knxjson");
				String json = readFileSdcardFile(Environment.getExternalStorageDirectory() + "/zyyknxandroid/.nomedia/KnxUiMetaData.json");

				/*
				 * GsonBuilder gsonBuilder = new GsonBuilder();
				 * gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
				 * gsonBuilder.registerTypeAdapter(Date.class, new
				 * GsonHelper.WCFDateDeserializer());
				 * gsonBuilder.setFieldNamingPolicy
				 * (FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
				 * KNXControlBaseDeserializer deserializer = new
				 * KNXControlBaseDeserializer("KNXControlBase");
				 * deserializer.registerControlBase("KNXBlinds",
				 * KNXBlinds.class);
				 * deserializer.registerControlBase("KNXButton",
				 * KNXButton.class);
				 * gsonBuilder.registerTypeAdapter(KNXControlBase.class,
				 * deserializer); Gson gson = gsonBuilder.create(); KNXApp
				 * mKNXApp = gson.fromJson(json, KNXApp.class);
				 * ZyyKNXApp.getInstance().setKNXAppConfig(mKNXApp);
				 */

				GsonBuilder gsonBuilder = new GsonBuilder();
				// gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
				// gsonBuilder.registerTypeAdapter(Date.class, new
				// GsonHelper.WCFDateDeserializer());
				gsonBuilder.registerTypeAdapter(KNXControlBase.class, new KNXControlBaseDeserializerAdapter());
				Gson gson = gsonBuilder.create();
				KNXApp mKNXApp = gson.fromJson(json, KNXApp.class);

				ZyyKNXApp.getInstance().setKNXAppConfig(mKNXApp); 
				
				/*
				Map<Integer, String> mKNXControlIdMapKNXId = new HashMap<Integer, String>(); 
				for (int i = 0; i < mKNXApp.getAreas().size(); i++) {
					KNXArea mKNXArea = mKNXApp.getAreas().get(i);
					for (int j = 0; j < mKNXArea.getRooms().size(); j++) {
						KNXRoom mKNXRoom = mKNXArea.getRooms().get(j); 
						for (int x = 0; x < mKNXRoom.getPages().size(); x++) {
							
							KNXPage mKNXPage = mKNXRoom.getPages().get(x);
							if(mKNXPage.getControls() != null && mKNXPage.getControls().size() > 0) { 
								for (int a = 0; a < mKNXPage.getControls().size(); a++) { 
									mKNXControlIdMapKNXId.put(mKNXPage.getControls().get(a).getId(), getFirstOrNull(mKNXPage.getControls().get(a).getReadAddressId()).getId());
								}
							}
							
							for (int y = 0; y < mKNXPage.getGrids().size(); y++) {
								KNXGrid mKNXGrid = mKNXPage.getGrids().get(y);
								if(mKNXGrid.getControls() != null && mKNXGrid.getControls().size() > 0) { 
									for (int b = 0; b < mKNXPage.getControls().size(); b++) { 
										mKNXControlIdMapKNXId.put(mKNXPage.getControls().get(b).getId(), getFirstOrNull(mKNXPage.getControls().get(b).getReadAddressId()).getId());
									}
								}
								
							}
						} 
					}
				}
				*/
				
				
				
				String groupAddressJson = readFileSdcardFile(Environment.getExternalStorageDirectory() + "/zyyknxandroid/.nomedia/GroupAddress.json");
				List<KNXGroupAddress> mKNXGroupAddressList = gson.fromJson(groupAddressJson, new TypeToken<List<KNXGroupAddress>>() { }.getType());
				Collections.sort(mKNXGroupAddressList, new Comparator<KNXGroupAddress>() { 
					@Override 
				    public int compare(KNXGroupAddress o1, KNXGroupAddress o2) {
				           //return (o2.getId().compareTo(o1.getId()));
				           return (o2.getKnxAddress().compareTo(o1.getKnxAddress()));
				     }	
				});
				//索引号对应的组地址列表
				Map<Integer, KNXGroupAddress> sortGroupAddressMap = new HashMap<Integer, KNXGroupAddress>(); 
				//索引号对应的组地址列表
				Map<String, Integer> groupAddressIndexMap = new HashMap<String, Integer>(); 
				for (int i = 0; i < mKNXGroupAddressList.size(); i++) { 
					sortGroupAddressMap.put(i + 1, mKNXGroupAddressList.get(i));
					groupAddressIndexMap.put(mKNXGroupAddressList.get(i).getId(), i + 1);
				} 
				ZyyKNXApp.getInstance().setGroupAddressMap(sortGroupAddressMap);
				ZyyKNXApp.getInstance().setGroupAddressIndexMap(groupAddressIndexMap);
				
				//String fileName=  Environment.getExternalStorageDirectory() + "/struck.dat";  
				String fileName2=  Environment.getExternalStorageDirectory() + "/struck.dat";  
				String fileName=  getApplicationContext().getFilesDir().getAbsolutePath()+ "/struck.dat";  
		        try  
		        {  
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
		            
		            int physicalAddressFirst = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_FIRST, 0);
					int physicalAddressSecond = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_SECOND, 0);
					int physicalAddressThree = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_THREE, 0); 
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
		            	
		            	config = BitUtils.setBitValue(config, 2, mKNXGroupAddressList.get(j).getIsCommunication());     //commuEnable 
		            	config = BitUtils.setBitValue(config, 3, mKNXGroupAddressList.get(j).getIsRead());              //readEnable             
		            	config = BitUtils.setBitValue(config, 4, mKNXGroupAddressList.get(j).getIsWrite());             //writeEnable        
		            	config = BitUtils.setBitValue(config, 6, mKNXGroupAddressList.get(j).getIsTransmit());          //transEnable         
		            	config = BitUtils.setBitValue(config, 7, mKNXGroupAddressList.get(j).getIsUpgrade());           //updateEnable          
		            	
			            out.write((byte)config);
			            
			            out.write((byte)(mKNXGroupAddressList.get(j).getType() - 1));  ///* 数据类型，长度    */
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
		            
		        } catch (Exception e)  
		        {  
		            e.printStackTrace();  
		        }   
				 
		        CopyFileUtil.copyFile(fileName, fileName2, true);
		        
			} catch (Exception e) {
				e.printStackTrace();
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
