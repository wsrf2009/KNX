package com.sation.knxcontroller.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
	/**
	 * @author  获取当前的网络状态
	 *         
	 * @param context
	 * @return 
	 * 			-1：没有网络
	 * 			 0：	其他
	 * 			 1：移动蜂窝网络
	 * 			 2：WIFI
	 * 			 3：以太网
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) { // 无网络
			return netType;
		}
		int nType = networkInfo.getType();
		if(networkInfo.isConnected()) {
			if (nType == ConnectivityManager.TYPE_MOBILE) { // 移动蜂窝网络
//				Log.e("networkInfo.getExtraInfo() is "+ networkInfo.getExtraInfo());
//				if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) { // Net
//					netType = 3;
//				} else { // wap网络
					netType = 1;
//				}
			} else if (nType == ConnectivityManager.TYPE_WIFI) { // WIFI网络
				netType = 2;
			} else if (nType == ConnectivityManager.TYPE_ETHERNET) { // 以太网
				netType = 3;
			} else { // 其他链接
				netType = 0;
			} 
		}

		return netType;
	}

	/**
	 * 判断WiFi网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断数据流量是否可用
	 * 
	 * @param context
	 * @return
	 */
	public  static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 获取本机IP地址
	 */
	public static String getIpAddress() {
    	String ipaddress = null;
    	try{
    		for (Enumeration<NetworkInterface>  en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    			NetworkInterface intf = en.nextElement();
    			if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")) { 
    				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();enumIpAddr.hasMoreElements();) {
    					InetAddress inetAddress = enumIpAddr.nextElement();
    					if (!inetAddress.isLoopbackAddress()) {
    						ipaddress = inetAddress.getHostAddress().toString();
    						if(!ipaddress.contains("::")){//ipV6的地址
    							return ipaddress;
    						}
    					}
    				}
    			} else {
    				continue;
    			}
    		}
    	} catch (Exception e) {}
    	
    	return ipaddress;
    }
}
