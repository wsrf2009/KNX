package com.zyyknx.android.services; 

import com.zyyknx.android.activity.SplashActivity;
import com.zyyknx.android.util.KNX0X01Lib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RestartService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try { 
					KNX0X01Lib.UCLOSENet();
					
					Intent intent = new Intent(RestartService.this, SplashActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				} catch (Exception ex) {

				}
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}
}
