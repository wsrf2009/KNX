package com.zyyknx.android.util; 

import java.util.Map;

import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXResponse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class KNX0X01Lib { 
	
	/**
	 * Libreria a cargar para poder usar la llamadas a methodos nativos
	 */
	static{
		System.loadLibrary("KNX0X01lib");
		
		KNX0X01Lib KNX0X01LibClass = new KNX0X01Lib();
		KNX0X01LibClass.StartKNX0X01Lib();
//		StartKNX0X01Lib();
	}
	
	public static void loadLibrary(){  
		//Log.i(tag, "test1");  
		//log .info( "into one method" );
	}
	
	public static void loadLibraryTest(){  
//	    Log.i(tag, "test1");  
//		log .info( "into one method" ); 
	} 
	
	  public char SendGroupValue(int asap, int lenArray, byte[] Value) 
	    {
		    android.util.Log.d("回调函数\n ZyyKNXApp","回调函数\n");
		    android.util.Log.d("Test1", "索引号为"+ asap +"的值改变后为："+ ByteUtil.getInt(Value) +""); 
		    try {
			    ZyyKNXApp mZyyKNXApp = (ZyyKNXApp)ZyyKNXApp.getInstance().getApplicationContext();
			    Map<Integer, KNXGroupAddress> mGroupAddressMap = mZyyKNXApp.getGroupAddressMap();
			    
			    KNXResponse mKNXResponse = new KNXResponse();
				mKNXResponse.setControlValue(ByteUtil.getInt(Value));
				mKNXResponse.setKNXId(mGroupAddressMap.get(asap).getId());
				
				Intent intent = new Intent();
				intent.setAction(ZyyKNXConstant.BROADCAST_UPDATE_DEVICE_STATUS);
				Bundle bundle = new Bundle();
				bundle.putSerializable("value", mKNXResponse); 
				intent.putExtras(bundle);
				ZyyKNXApp.getInstance().sendBroadcast(intent); 
		    
	    
		    } catch (Exception e) {
		    	
			} 
			return 1;
	    }
	    
		public native boolean StartKNX0X01Lib(); 
	
	/******************************************************************************
	* 描述   :  打开并初始化协议栈工作。
	*
	*
	* 参数   :  *NetAddr  以太网地址，根据创建连接模式不一样所指的地址也不一样，服务端时为本机地址，客户端时为对方地址，UDP为广播地址
	             port     端口号
	             mode     网络连接模式，0 server 1 client 3 UDP
	*
	* 返回值 :  1  成功打开，则返回true.
	*        :  0  失败，则返回false.
	******************************************************************************/
	public static native boolean UOPENNet(String NetAddr, int Port,int mode); 
	
	
	
	// 当前网络连接成功的类型，在调用UOPENNet前必须先调用，在网络发生变化，如掉线，切换网络的时候必须调用同步状态
	// 参数值Type： -1：没有网络，0:其他连接，1：GPRS，2：wifi，3：Net
	public static native void SetNetworkType(int Type); 
	
	 
	/******************************************************************************
	* 描述   :  关闭协议栈。
	*
	*
	* 参数   :
	*
	* 返回值 :  1  成功，则返回true.
	*        :  0  失败，则返回false.
	******************************************************************************/
	public static native boolean UCLOSENet(); 
	
	
	/******************************************************************************
	* 描述   :  检测Update标志是否设置，当检测到标志被设置后，标志将被清零。
	*           不管Update标志是否设置，都将复制对象数据到指定的缓冲。
	*
	* 参数   :  asap    对象索引.
	*
	*           pData   缓冲指针，当不需要复制数据时，应该设置为NULL.
	*
	*           length  需要复制的数据个数(通常为对象的数据类型长度).
	*
	* 返回值 :  1  假如Update标志已设置，则返回true.
	*        :  0  假如Update标志没有设置，则返回false.
	******************************************************************************/
	public static native boolean UTestAndCopyObject(int asap,byte[] Retst, byte[] length);

	/******************************************************************************
	* 描述   :  设置Read标志，并且请求一次读操作。
	*
	* 参数   :  asap    对象索引.
	*
	* 返回值 :  1  假如函数操作成功返回true.
	*        :  0  假如函数操作失败返回false.
	******************************************************************************/
	public static native boolean USetAndRequestObject(int asap);





	/******************************************************************************
	* 描述   :  设置Transmitting标志，并且请求一次写操作。
	*
	* 参数   :  asap    对象索引.
	*
	*           pData   待发送的数据指针.
	*
	*           length  待发送的数据个数(通常为对象的数据类型长度).
	*
	* returns    :   2  if the operation of Read/Write was processing.
	*            :   1  if the operation of Read/Write was successful.
	*            :   0  if the operation of Read/Write was failed.
	******************************************************************************/
	//public static native boolean USetAndTransmitObject(int asap, String pData, int length);
	//public static native int USetAndTransmitObject(int asap, byte[] pData, int length);
	
	public static native int USetAndTransmitObject(int asap, byte[] pData, int length, int mode); 

	/******************************************************************************
	* 描述   :   获取读写操作的执行情况.
	*
	* 参数   :   asap   对象索引.
	*
	* 返回值 :   1  假如读写操作正在进行.
	*        :   0  假如读写操作成功了.
	*        :   -1 假如读写操作失败了.
	******************************************************************************/
	public static native int UGetAndClearRequestState(int asap);
	
}
