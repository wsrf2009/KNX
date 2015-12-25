/**
 * 
 */
package com.zyyknx.android.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.zyyknx.android.ZyyKNXApp;
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
		
//		final Calendar c = Calendar.getInstance();  
//        c.setTimeZone(TimeZone.getTimeZone(timeZoneOfSystem));  
//        String dayOfWeek = TimingTaskItem.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//		String currentTime = sdf.format(new Date());
//		Log.d("ZyyKNXApp", "currentTime: " + currentTime);
		
		List<TimingTaskItem> timingTaskList = ZyyKNXApp.getInstance().getTimingTaskList();
		for(TimingTaskItem item : timingTaskList) {
			if(item.isOneOffSelected()) { // 若是一次性的
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
				String currentTime = sdf.format(new Date());
				
				String time = item.getDateAndTime();
				
				Log.d("ZyyKNXApp", "currentTime: " + currentTime + " time: "+time);
				
				if (currentTime.equals(time)) {
					item.executeTask();
					
					timingTaskList.remove(item);
					break;
				}
			} else if (item.isEverydaySelected()) { // 每天
				item.executeTask();
			} else { // 若是周期性的
				Calendar c = Calendar.getInstance();
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				
				switch(dayOfWeek) {
					case 0: // 周日
						if(item.isSundaySelected()) {
							item.executeTask();
						}
					break;
					
					case 1: // 周一
						if(item.isMondaySelected()) {
							item.executeTask();
						}
						break;
						
					case 2: // 周二
						if(item.isTuesdaySelected()) {
							item.executeTask();
						}
						break;
						
					case 3: // 周三
						if(item.isWednesdaySelected()){
							item.executeTask();
						}
						break;
					
					case 4: // 周四
						if(item.isThursdaySelected()) {
							item.executeTask();
						}
						break;
						
					case 5: // 周五
						if(item.isFridaySelected()) {
							item.executeTask();
						}
						break;
						
					case 6: // 周六
						if(item.isSaturdaySelected()) {
							item.executeTask();
						}
						break;
					
					default:
						break;
				}
			}
		}
	}

}
