package com.sation.knxcontroller.services;

import com.sation.knxcontroller.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * use to invoke notification service when device boot complete or net work
 * status changed.
 * 
 * @author roger
 * 
 */
public class NotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent notificationIntent = new Intent("com.zyyEHome.NotificationService");
		if (intent.getAction().equals(
				android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {

			if (!Utils.checkNetState(context)) {
				context.stopService(notificationIntent);
			} else {
				context.startService(notificationIntent);
			}
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			Log.d("NotificationReceiver", "System shutdown, stopping service.");
		} else if (intent.getAction().equals(
				"android.intent.action.BOOT_COMPLETED")) {
			context.startService(notificationIntent);
		}
	}

}
