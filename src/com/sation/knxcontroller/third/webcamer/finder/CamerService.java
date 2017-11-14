package com.sation.knxcontroller.third.webcamer.finder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.sation.knxcontroller.third.webcamer.database.Database;
import com.sation.knxcontroller.util.Log;

import java.util.List;

/**
 * Created by wangchunfeng on 2017/9/28.
 */

public class CamerService extends Service {
    private static final String TAG = "CamerService";
    public static final String CamerListUpdated = "com.sation.knxcontroller.third.webcamer.finder.camerservice.camerlistupdated";
    public static final String CamerFound = "com.sation.knxcontroller.third.webcamer.finder.camerservice.camerfound";

    private CamerFinder mFinder;
    private CamerBinder mBinder = new CamerBinder();
    private Database mDb;
    private List<CamerDevice> mDevices;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "");

        mFinder = new CamerFinder(getApplicationContext());
        mFinder.setOnCamerFinderListener(finderListener);

        mDb = new Database(getApplicationContext());
        mDevices = mDb.getCamerDevices();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "");

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "");
        mFinder.stopWorking();
    }

    public CamerFinder getFinder() {
        return mFinder;
    }

    public class CamerBinder extends Binder {
        public CamerService getService() {
            return CamerService.this;
        }

        public void findCamer() {

        }
    }

    public void sendBroadcast() {
        if (mFinder != null) {
            mFinder.sendProbe();
        }
    }

    public Database getDb() {
        return mDb;
    }

    public List<CamerDevice> getDevices() {
        return mDevices;
    }

    private OnCamerFinderListener finderListener = new OnCamerFinderListener() {
        @Override
        public void OnCamerListUpdated() {
            if (mDevices != null && mDevices.size() > 0) {
                for (CamerDevice cd1 : mDevices) {
                    for (CamerDevice cd2 : mFinder.getCamerList()) {
                        if (cd1.uuid.equals(cd2.uuid)) {
                            cd1.setOnline(true);
                            break;
                        }
                    }
                }
            }

            Intent intent = new Intent(CamerListUpdated);
            sendBroadcast(intent);
        }

        @Override
        public void OnCamerFound() {
            Intent intent = new Intent(CamerFound);
            sendBroadcast(intent);
        }
    };
}
