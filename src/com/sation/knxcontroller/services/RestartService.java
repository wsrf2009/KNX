package com.sation.knxcontroller.services; 

import com.sation.knxcontroller.activity.SplashActivity;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RestartService extends Service {
	private final String TAG = "RestartService";

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
					Log.i(TAG, "onStartCommand()");
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
