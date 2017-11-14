package com.sation.knxcontroller.broadcastreceiver;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.activity.SplashActivity;
import com.sation.knxcontroller.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "BootBroadcastReceiver";
    static final String action_boot="android.intent.action.BOOT_COMPLETED";
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (intent.getAction().equals(action_boot) &&
        		android.os.Build.MODEL.equals(STKNXControllerConstant.STKCPLATFORM) ) { // 若是世讯十寸机
        	Log.i(TAG, "onStartCommand()");
            Intent ootStartIntent=new Intent(context, SplashActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
    }
 
}