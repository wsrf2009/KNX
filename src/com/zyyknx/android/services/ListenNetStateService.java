package com.zyyknx.android.services;

import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.R;
import com.zyyknx.android.util.KNX0X01Lib;
import com.zyyknx.android.util.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

public class ListenNetStateService extends Service {
	private BroadcastReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new NetworkStateReceiver();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);
		return super.onStartCommand(intent, flags, startId);
	}

	static class NetworkStateReceiver extends BroadcastReceiver {
		// http://developer.android.com/training/basics/network-ops/managing.html

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
				int type = -1;
				if (!Utils.checkNetState(context)) {
					//notifyShow(context);
					type = Utils.isNetworkAvailable(context);
				}
				KNX0X01Lib.SetNetworkType(type);
			}

		}

		private void notifyShow(Context context) {
			int icon = R.drawable.launcher; // icon from resources
			CharSequence tickerText = "网络不可用"; // ticker-text
			long when = System.currentTimeMillis(); // notification time
			CharSequence contentTitle = "网络检查"; // message title
			CharSequence contentText = "网络不可用，请检查"; // message
													// text

			Intent notificationIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

			((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(ZyyKNXConstant.NETWORK_NOTIFICATION, notification);
		}
	}
}