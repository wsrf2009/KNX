/**
 * 
 */
package com.sation.knxcontroller.services;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.NetWorkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author wangchunfeng
 *
 */
public class NetworkStateMonitor extends BroadcastReceiver {

	/**
	 * 
	 */
	public NetworkStateMonitor() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		int type = NetWorkUtil.getAPNType(context);
		
		Log.i(STKNXControllerConstant.DEBUG, "context:"+context+ "\ntype:"+type);
		
		KNX0X01Lib.SetNetworkType(type);
	}

}
