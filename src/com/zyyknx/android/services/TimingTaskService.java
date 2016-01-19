/**
 * 
 */
package com.zyyknx.android.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.control.TimingTaskItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author wangchunfeng
 *
 */
public class TimingTaskService extends BroadcastReceiver {

	/**
	 * 
	 */
	public TimingTaskService() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.d("ZyyKNXApp", "intent.getAction: "+intent.getAction());
		
		/* 获取系统时区 */
//		TimeZone tz = TimeZone.getDefault();
//		String timeZoneOfSystem = tz.getDisplayName(false, TimeZone.SHORT);
		
		
		
		final Calendar c = Calendar.getInstance();  
//		c.setTimeZone(TimeZone.getTimeZone(timeZoneOfSystem));  
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY); // 24小时制的时间
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
//		final Calendar c = Calendar.getInstance();  
//        c.setTimeZone(TimeZone.getTimeZone(timeZoneOfSystem));  
//        String dayOfWeek = TimingTaskItem.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        
        Log.i(ZyyKNXConstant.DEBUG, "system time:"+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+" dayOfWeek:"+dayOfWeek);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//		String currentTime = sdf.format(new Date());
//		Log.d("ZyyKNXApp", "currentTime: " + currentTime);
		
		/* 获取当前的系统时间 */
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//		String currentTime = sdf.format(new Date());
//		
//		Log.i(ZyyKNXConstant.DEBUG, "currentTime:"+currentTime);
//		
		Map<String, List<TimingTaskItem>> timerTaskMap = ZyyKNXApp.getInstance().getTimerTaskMap();
		for(Map.Entry<String, List<TimingTaskItem>> taskMap : timerTaskMap.entrySet()) { // 枚举定时任务按钮所对应的定时任务列表
			List<TimingTaskItem> delList = new ArrayList<TimingTaskItem>();
			boolean refreshUI = false;
			
			List<TimingTaskItem> taskList = taskMap.getValue();
			for(TimingTaskItem item:taskList) { // 枚举定时任务列表下的每个定时任务
				boolean delete = false;

				if(item.isOneOffSelected()) { // 若是一次性任务
					if ((year == item.getYear()) && (month == item.getMonth()) && (day == item.getDay()) &&
								(hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) { // 当前时间与任务所指时间一致
						item.execute(); // 执行任务
						delete = true; // 从定时任务列表移除该任务
					}
				} else if(item.isEverydaySelected()) { // 若是每天都要执行的任务
					if((hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
						item.execute(); // 执行任务
					}
				} else if (item.isWeeklySelected()) { // 若是周期性的
					if((hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
						if (1 == dayOfWeek) { // 周日
							if(item.isSundaySelected()) {
								item.execute();
							}
						} else if (2 == dayOfWeek) { // 周一
							if(item.isMondaySelected()) {
								item.execute();
							}
						} else if (3 == dayOfWeek) { // 周二
							if(item.isTuesdaySelected()) {
								item.execute();
							}
						} else if (4 == dayOfWeek) { // 周三
							if(item.isWednesdaySelected()){
								item.execute();
							}
						} else if (5 == dayOfWeek) { // 周四
							if(item.isThursdaySelected()) {
								item.execute();
							}
						} else if (6 == dayOfWeek) { // 周五
							if(item.isFridaySelected()) {
								item.execute();
							}
						} else if (7 == dayOfWeek) { // 周六
							if(item.isSaturdaySelected()) {
								item.execute();
							}
						}
					}
				} else if (item.isMonthlySelected()) { // 每月
					if((day == item.getDay()) && (hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
						item.execute();
					}
				} else if (item.isLoopSelected()) { // 循环.

					if(item.decCounterSecond > 0) {
						item.decCounterSecond--;
					} else {
						if (item.decCounterMinute > 0) {
							item.decCounterSecond = 59;
							item.decCounterMinute--;
						} else {
							if(item.decCounterHour > 0) {
								item.decCounterSecond = 59;
								item.decCounterMinute = 59;
								item.decCounterHour--;
							} else {
								
							}
						}
					}
					
					if ((0 >= item.decCounterHour) && (0 >= item.decCounterMinute) && (0 >= item.decCounterSecond)) {
						item.decCounterSecond = item.getIntervalSecond();
						item.decCounterMinute = item.getIntervalMinute();
						item.decCounterHour = item.getIntervalHour();
						
						item.execute();
					}
					
					Log.i(ZyyKNXConstant.DEBUG, "item.decCounterSecond:" + item.decCounterSecond+"item.getSecond():"+item.getSecond());
					
					int newHour = hour+item.decCounterHour;
					int newMinute = minute+item.decCounterMinute;
					int newSecond = second+item.decCounterSecond;
					if(newSecond >= 60) {
						newSecond -= 60;
						newMinute += 1;
					}
					if(newMinute >= 60) {
						newMinute -= 60;
						newHour += 1;
					}
					if(newHour != item.getHour()) {
						item.setHour(newHour);
						item.setMinute(newMinute);
						item.setSecond(newSecond);
						refreshUI = true;
					} else if(newMinute != item.getMinute()) {
						item.setMinute(newMinute);
						item.setSecond(newSecond);
						refreshUI = true;
					} else if(newSecond != item.getSecond()) {
						item.setSecond(newSecond);
						refreshUI = true;
					}

				} 
				
				if (delete) {
					delList.add(item);
					
					delete = false;
				}
			}
			
			if (delList.size() > 0) {
				taskList.removeAll(delList);
				refreshUI = true;
			}

			if (refreshUI) {
				Intent mIntent = new Intent(ZyyKNXConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
				context.sendBroadcast(mIntent);
			}
		}
	}
}
