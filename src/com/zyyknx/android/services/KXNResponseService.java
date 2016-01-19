package com.zyyknx.android.services;

import java.util.Iterator;
import java.util.Map; 
import java.util.TimerTask;
 
import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant; 
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXResponse;
import com.zyyknx.android.util.ByteUtil;
import com.zyyknx.android.util.KNX0X01Lib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log; 

public class KXNResponseService extends Service {

	private static final int MSG = 0x001;
	private NotificationManager mNotificationManager;
	private boolean isStart = false;// 是否与服务器连接上
	private Notification mNotification;
	private Context mContext = this; 

	/*
	// 用来更新通知栏消息的handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG:
				// 播放声音
				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.msg);
				mediaPlayer.release();
				// 更新通知栏
				String content = (String) msg.getData().getSerializable("msg");

				int icon = R.drawable.logo;
				long when = System.currentTimeMillis();
				mNotification = new Notification(icon, content, when);
				mNotification.flags = Notification.FLAG_AUTO_CANCEL;

				// mNotification.flags = Notification.FLAG_NO_CLEAR;
				// 设置默认声音
				mNotification.defaults |= Notification.DEFAULT_SOUND;
				// 设定震动(需加VIBRATE权限)
				mNotification.defaults |= Notification.DEFAULT_VIBRATE;
				mNotification.contentView = null;
				Intent intent = new Intent(mContext, RoomListActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
				mNotification.setLatestEventInfo(mContext, "ZyyDLNA接受到新消息", content, contentIntent);

				mNotificationManager.notify(ZyyKNXConstant.NOTIFY_ID, mNotification);// 通知一下才会生效哦
				break;

			default:
				break;
			}
		}
	};
	*/

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {// 在onCreate方法里面注册广播接收者
		// TODO Auto-generated method stub
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);

		isSendComplete = false;
		// 获取设备的状态
		GetDeviceStatusTask getDeviceStatusTask = new GetDeviceStatusTask();
		getDeviceStatusTask.run(); 
		 
		/*
		new Thread() {

			@Override
			public void run() {
			
			}
		}.start();
		*/
	}

	@Override
	// 在服务被摧毁时，做一些事情
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 创建通知
	 */
	private void setMsgNotification() {
		int icon = R.drawable.logo;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		/*
		 * RemoteViews contentView = new
		 * RemoteViews(mContext.getPackageName(),R.layout.notify_view);
		 * contentView.setTextViewText(R.id.notify_name, "LED 控制进入后台运行");
		 * contentView.setTextViewText(R.id.notify_msg, "手机正在后台运行");
		 * contentView.setTextViewText(R.id.notify_time, Utility.getDate()); //
		 * 指定个性化视图 mNotification.contentView = contentView;
		 * 
		 * Intent intent = new Intent(this, LampActivity.class); PendingIntent
		 * contentIntent = PendingIntent.getActivity(mContext, 0,intent,
		 * PendingIntent.FLAG_UPDATE_CURRENT); // 指定内容意图
		 * mNotification.contentIntent = contentIntent;
		 * mNotificationManager.notify(ZyyConstant.NOTIFY_ID, mNotification);
		 */
	} 
	
	
	private boolean isSendComplete = false;

	// 设备状态定时器
	public class GetDeviceStatusTask extends TimerTask {

		public void run() { 
			//Log.d("Test", "启动开始111");  
			//Message message = new Message(); 
			ZyyKNXApp mZyyKNXApp = (ZyyKNXApp) getApplicationContext();
			if (!isSendComplete) { 
				//message.what = 1;
				try {
					
					//Log.d("Test", "启动开始1"); 
					Map<Integer, KNXGroupAddress> mGroupAddressMap = mZyyKNXApp.getGroupAddressMap();
					if(mGroupAddressMap != null) {   
						//Log.d("Test", "启动开始"+ mGroupAddressMap.size()  +""); 
						Iterator<Integer> iter = mGroupAddressMap.keySet().iterator();
						//Log.d("Test", "启动开始2"); 
						while (iter.hasNext()) {
							int index = iter.next();
							if(mGroupAddressMap.get(index).getIsRead()) {
								byte[] contentBytes = new byte[32];
								byte[] length  = new byte[1];
								boolean isChange = KNX0X01Lib.UTestAndCopyObject(index, contentBytes, length);
								//Thread.sleep(15);
								//String isChangeStr = isChange ? "有改变" : "没改变";
								//Log.d("Test", "索引号为"+ index +"的值："+ isChangeStr +""); 
								if(isChange)
								{
//									Log.d("Test1", "索引号为"+ index +"的值改变后为："+ ByteUtil.getInt(contentBytes) +""); 
									
									KNXResponse mKNXResponse = new KNXResponse();
									mKNXResponse.setControlValue(ByteUtil.getInt(contentBytes));
									mKNXResponse.setKNXId(mGroupAddressMap.get(index).getId());
									
									Intent intent = new Intent();
									intent.setAction(ZyyKNXConstant.BROADCAST_UPDATE_DEVICE_STATUS);
									Bundle bundle = new Bundle();
									bundle.putSerializable("value", mKNXResponse); 
									intent.putExtras(bundle);
									sendBroadcast(intent); 
								}
								contentBytes = null;
								length = null;
							}
						}
					}
					isSendComplete = true;
				} catch (Exception e) {
					//message.what = -2;
					//e.printStackTrace();
				}  
				//Log.d("Test", "启动完成"); 
			}
		}
	}

}
