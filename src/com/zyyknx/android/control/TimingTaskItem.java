/**
 * 
 */
package com.zyyknx.android.control;

import com.zyyknx.android.models.AddressBit1Task;
import com.zyyknx.android.models.KNXDataType;
import com.zyyknx.android.models.KNXGroupAddress;

/**
 * @author wangchunfeng
 *
 */
public class TimingTaskItem {
	private KNXGroupAddress address;
	private boolean oneOff;
	private boolean everyday;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	final private static String[] week = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

	
	public TimingTaskItem(KNXGroupAddress address) {
		this.address = address;
	}
	
	public TimingTaskItem(int year, int month, int day, 
			int hour, int minute) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}
	
	public void setOneOff(boolean oneOff) {
		this.oneOff = oneOff;
	}
	public boolean isOneOffSelected() {
		return this.oneOff;
	}
	
	public void setEveryday(boolean everyday) {
		this.everyday = everyday;
	}
	public boolean isEverydaySelected() {
		return this.everyday;
	}
	
	public void setMonday(boolean monday) {
		this.monday = monday;
	}
	public boolean isMondaySelected() {
		return this.monday;
	}
	
	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}
	public boolean isTuesdaySelected() {
		return this.tuesday;
	}
	
	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}
	public boolean isWednesdaySelected() {
		return this.wednesday;
	}
	
	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}
	public boolean isThursdaySelected() {
		return this.thursday;
	}
	
	public void setFriday(boolean friday) {
		this.friday = friday;
	}
	public boolean isFridaySelected() {
		return this.friday;
	}
	
	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}
	public boolean isSaturdaySelected() {
		return this.saturday;
	}
	
	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}
	public boolean isSundaySelected() {
		return this.sunday;
	}
	
	public void setControl(KNXGroupAddress address) {
		this.address = address;
	}
	public KNXGroupAddress getControl() {
		return this.address;
	}
	
	private int year;
	public void setYear(int year) {
		this.year = year;
	}
	public int getYear(){
		return this.year;
	}
	
	private int month;
	public void setMonth(int month) {
		this.month = month;
	}
	public int getMonth() {
		return this.month;
	}
	
	private int day;
	public void setDay(int day) {
		this.day = day;
	}
	public int getDay() {
		return this.day;
	}

	public String getDate() {
		String date = "";
		
		if(oneOff) {
			date = String.format("%04d-%02d-%02d", year, month, day);
		} else if (everyday) {
			date = "每天";
		} else {
			if(monday) {
				date += week[1]+" ";
			}
			if(tuesday) {
				date += week[2]+" ";
			}
			if(wednesday) {
				date += week[3]+" ";
			}
			if(thursday) {
				date += week[4]+" ";
			}
			if(friday) {
				date += week[5]+" ";
			}
			if(saturday) {
				date += week[6]+" ";
			}
			if(sunday) {
				date += week[0]+" ";
			}
		}
		
		return date;
	}
	
	private int hour;
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getHour() {
		return this.hour;
	}
	
	private int minute;
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getMinute() {
		return this.minute;
	}
	
	public String getTime() {
		return String.format("%02d:%02d", hour, minute);
	}
	
	public String getDateAndTime() {
		return getDate()+" "+getTime();
	}
	
	private boolean taskOpen = false;
	public void setTaskOpen(boolean open) {
		this.taskOpen = open;
	}
	public boolean getTaskOpen() {
		return this.taskOpen;
	}
	
	private boolean taskClose = false;
	public void setTaskClose(boolean close) {
		this.taskClose = close;
	}
	public boolean getTaskClose() {
		return this.taskClose;
	}
	
	public String getAction() {
		String action = "";
		
		if(taskOpen) {
			action = "打开";
		} else if(taskClose) {
			action = "关闭";
		}
		
		action = action + address.getName();
		
		return action;
	}
	
	public void executeTask() {
		if(address.getType() == 1) {
			if (taskOpen) {
				(new AddressBit1Task(address)).toggleSwitchOpen();
			} else if (taskClose) {
				(new AddressBit1Task(address)).toggleSwitchClose();
			}
		}
	}
}
