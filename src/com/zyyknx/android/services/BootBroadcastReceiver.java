package com.zyyknx.android.services;

import com.zyyknx.android.activity.SplashActivity;
import android.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	
    static final String action_boot="android.intent.action.BOOT_COMPLETED";
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	Log.d("ZyyKNXApp", "intent.getAction: "+intent.getAction());
    	
        if (intent.getAction().equals(action_boot)){
            Intent ootStartIntent=new Intent(context, SplashActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
    }
 
}