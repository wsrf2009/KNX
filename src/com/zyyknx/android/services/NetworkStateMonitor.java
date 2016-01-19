/**
 * 
 */
package com.zyyknx.android.services;

import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.util.KNX0X01Lib;
import com.zyyknx.android.util.Log;
import com.zyyknx.android.util.NetWorkUtil;

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
		
		Log.i(ZyyKNXConstant.DEBUG, "context:"+context+ "\ntype:"+type);
		
		KNX0X01Lib.SetNetworkType(type);
	}

}
