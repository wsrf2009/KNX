/**
 * 
 */
package com.sation.knxcontroller.services;

import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.NetWorkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStateMonitor extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		int type = NetWorkUtil.getAPNType(context);
		
//		Log.i(STKNXControllerConstant.DEBUG, "context:"+context+ "\ntype:"+type);
		
		KNX0X01Lib.SetNetworkType(type);
	}

}
