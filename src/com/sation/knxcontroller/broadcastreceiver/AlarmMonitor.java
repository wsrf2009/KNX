/**
 * 
 */
package com.sation.knxcontroller.broadcastreceiver;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.services.TimingService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangchunfeng
 *
 */
public class AlarmMonitor extends BroadcastReceiver {

	@SuppressLint("DefaultLocale")
	@Override
	public void onReceive(Context context, Intent intent) {
//		final Calendar c = Calendar.getInstance();
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH)+1;
//		int day = c.get(Calendar.DAY_OF_MONTH);
//		int hour = c.get(Calendar.HOUR_OF_DAY); // 24小时制的时间
//		int minute = c.get(Calendar.MINUTE);
//		int second = c.get(Calendar.SECOND);
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

//		Map<String, List<TimingTaskItem>> timerTaskMap = STKNXControllerApp.getInstance().getTimerTaskMap();
//		if(null != timerTaskMap) {
			Intent mIntent = new Intent(context, TimingService.class);
			context.startService(mIntent);
//		}
		
//		/* 刷新主界面时间 */
//		if(STKNXControllerApp.getInstance().getDisplayTimeFlag()) {
//			String weekday = "";
//			if (1 == dayOfWeek) { // 周日
//				weekday = context.getResources().getString(R.string.sunday);
//			} else if (2 == dayOfWeek) { // 周一
//				weekday = context.getResources().getString(R.string.monday);
//			} else if (3 == dayOfWeek) { // 周二
//				weekday = context.getResources().getString(R.string.tuesday);
//			} else if (4 == dayOfWeek) { // 周三
//				weekday = context.getResources().getString(R.string.wednesday);
//			} else if (5 == dayOfWeek) { // 周四
//				weekday = context.getResources().getString(R.string.thursday);
//			} else if (6 == dayOfWeek) { // 周五
//				weekday = context.getResources().getString(R.string.friday);
//			} else if (7 == dayOfWeek) { // 周六
//				weekday = context.getResources().getString(R.string.saturday);
//			}
//
//			String languange = STKNXControllerApp.getInstance().getLanguage();
//			String time;
//			if ((null != languange) && languange.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
//				time = String.format("%04d/%02d/%02d  %02d:%02d  %s",
//						year, month, day, hour, minute, weekday);
//			} else {
//				time = String.format("%02d/%02d/%04d  %02d:%02d  %s",
//						month, day, year, hour, minute, weekday);
//			}
//			Intent mIntent = new Intent(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME);
//			mIntent.putExtra(STKNXControllerConstant.SYSTEMTIME, time);
//			context.sendBroadcast(mIntent);
//		}
		
//		/* 定时重启系统 */
//		if(STKNXControllerApp.getInstance().getAutoRebootFlag()) {
//			if((hour == STKNXControllerApp.getInstance().getHourOfRebooting()) &&
//					(minute == STKNXControllerApp.getInstance().getMinuteOfReboot()) &&
//					(second <= 5)) {
//				Intent mIntent = new Intent();
//				ComponentName comp = new ComponentName("com.example.sationsystem",
//				"com.example.sationsystem.ServiceReboot");
//				mIntent.setComponent(comp);
//				context.startService(mIntent);
//			}
//		}
	}
}
