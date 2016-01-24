/**
 * 
 */
package com.zyyknx.android.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.models.AddressBit1Task;
import com.zyyknx.android.models.KNXDataType;
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXGroupAddressAction;
import com.zyyknx.android.util.Log;
import com.zyyknx.android.widget.ControlView;

import android.content.Context;

/**
 * @author wangchunfeng
 *
 */
public class TimingTaskItem implements Serializable, Cloneable, Runnable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean monthly;
	private boolean weekly;
	private boolean everyday;
	private boolean loop;
	private boolean oneOff;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int intervalHour;
	private int intervalMinute;
	private int intveralSecond;
	public int decCounterHour;
	public int decCounterMinute;
	public int decCounterSecond;
	private List<KNXGroupAddressAndAction> addressList;

	public TimingTaskItem() {
		/* 默认为一次性 */
		this.monthly = false;
		this.weekly = false;
		this.everyday = false;
		this.loop = false;
		this.oneOff = true;

		/* 默认为当前时间 */
		final Calendar c = Calendar.getInstance();  
		this.year = c.get(Calendar.YEAR);
		this.month = c.get(Calendar.MONTH)+1;
		this.day = c.get(Calendar.DAY_OF_MONTH);
		this.hour = c.get(Calendar.HOUR_OF_DAY); // 24小时制的时间
		this.minute = c.get(Calendar.MINUTE);
		this.second = c.get(Calendar.SECOND);
		
		/* 默认为当前的星期 */
		this.monday = false;
		this.tuesday = false;
		this.wednesday = false;
		this.thursday = false;
		this.friday = false;
		this.saturday = false;
		this.sunday = false;
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (1 == dayOfWeek) { // 周日
			this.sunday = true;
		} else if (2 == dayOfWeek) { // 周一
			this.monday = true;
		} else if (3 == dayOfWeek) { // 周二
			this.tuesday = true;
		} else if (4 == dayOfWeek) { // 周三
			this.wednesday = true;
		} else if (5 == dayOfWeek) { // 周四
			this.thursday = true;
		} else if (6 == dayOfWeek) { // 周五
			this.friday = true;
		} else if (7 == dayOfWeek) { // 周六
			this.saturday = true;
		}
		
		/* 默认时间间隔为30秒 */
		this.intervalHour = 0;
		this.intervalMinute = 0;
		this.intveralSecond = 30;
		
		this.decCounterHour = this.intervalHour;
		this.decCounterMinute = this.intervalMinute;
		this.decCounterSecond = this.intveralSecond;
		
		this.addressList = new ArrayList<KNXGroupAddressAndAction>();
	}
	
	@Override  
    public TimingTaskItem clone() {  
		TimingTaskItem item = null;
		try {
			item = (TimingTaskItem)super.clone();
			
			List<KNXGroupAddressAndAction> newList = new ArrayList<KNXGroupAddressAndAction>();
			for(KNXGroupAddressAndAction addressAndAction : item.getAddressList()) {
				newList.add(addressAndAction.clone());
			}
			
			item.setAddressList(newList);
			
		} catch (CloneNotSupportedException  e) {
			Log.e(ZyyKNXConstant.DEBUG, e.getLocalizedMessage());
		}
		
		return item;
	}

	public void setMonthly(boolean monthly) {
		this.monthly = monthly;
	}
	public boolean isMonthlySelected() {
		return this.monthly;
	}
	
	public void setWeekly(boolean weekly) {
		this.weekly = weekly;
	}
	public boolean isWeeklySelected() {
		return this.weekly;
	}
	
	public void setEveryday(boolean everyday) {
		this.everyday = everyday;
	}
	public boolean isEverydaySelected() {
		return this.everyday;
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	public boolean isLoopSelected() {
		return this.loop;
	}
	
	public void setOneOff(boolean oneOff) {
		this.oneOff = oneOff;
	}
	public boolean isOneOffSelected() {
		return this.oneOff;
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
	
	public String getWeekly(Context context) {
		String weekly = "";
		if(this.isMondaySelected()) {
			weekly += context.getResources().getString(R.string.monday)+" ";
		}
		if(this.isTuesdaySelected()) {
			weekly += context.getResources().getString(R.string.tuesday)+" ";
		}
		if(this.isWednesdaySelected()) {
			weekly += context.getResources().getString(R.string.wednesday)+" ";
		}
		if(this.isThursdaySelected()) {
			weekly += context.getResources().getString(R.string.thursday)+" ";
		}
		if(this.isFridaySelected()) {
			weekly += context.getResources().getString(R.string.friday)+" ";
		}
		if(this.isSaturdaySelected()) {
			weekly += context.getResources().getString(R.string.saturday)+" ";
		}
		if(this.isSundaySelected()) {
			weekly += context.getResources().getString(R.string.sunday)+" ";
		}
		
		if(weekly.length() > 1) {
			return weekly.substring(0, weekly.length()-1);
		} else {
			return weekly;
		}
	}

	public void setYear(int year) {
		this.year = year;
	}
	public int getYear(){
		return this.year;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	public int getMonth() {
		return this.month;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	public int getDay() {
		return this.day;
	}
	
	public String getDate() {
		return String.format("%04d-%02d-%02d", this.year, this.month, this.day);
	}

	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getHour() {
		return this.hour;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getMinute() {
		return this.minute;
	}

	public void setSecond(int second) {
		this.second = second;
	}
	public int getSecond() {
		return this.second;
	}
	
	public String getTimeWithoutSecond() {
		return String.format("%02d:%02d", this.hour, this.minute);
	}
	
	public String getTimeWithSecond() {
		return String.format("%02d:%02d:%02d", this.hour, this.minute, this.second);
	}
	
	public void setIntervalHour(int interval) {
		this.intervalHour = interval;
	}
	public int getIntervalHour() {
		return this.intervalHour;
	}
	
	public void setIntervalMinute(int interval) {
		this.intervalMinute = interval;
	}
	public int getIntervalMinute() {
		return this.intervalMinute;
	}
	
	public void setIntervalSecond(int interval) {
		this.intveralSecond = interval;
	}
	public int getIntervalSecond() {
		return this.intveralSecond;
	}

	public void setAddressList(List<KNXGroupAddressAndAction> list) {
		this.addressList = list;
	}
	public List<KNXGroupAddressAndAction> getAddressList() {
		return this.addressList;
	}

	@Override
	public void run() {
		for(KNXGroupAddressAndAction addressAndAction : addressList) {
			if(addressAndAction.getIsRead()) {
				ControlView.readDataFromAddress(addressAndAction.address);
			} else if (null != addressAndAction.action) {
				ControlView.sendDataToAddress(addressAndAction.address, String.valueOf(addressAndAction.action.getValue()));
			}
		}
	}
	
	public static class KNXGroupAddressAndAction implements Serializable, Cloneable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public KNXGroupAddressAndAction(KNXGroupAddress address) {
			this.address = address;
			List<KNXGroupAddressAction> defaultActions = address.getAddressAction();
			if(null != defaultActions) {
				this.action = defaultActions.get(0);
			}
			
			this.isRead = false;
		}
		
		@Override  
	    public KNXGroupAddressAndAction clone() {  
			KNXGroupAddressAndAction addressAndAction = null;
			
			try {
				addressAndAction = (KNXGroupAddressAndAction)super.clone();
				
			} catch (CloneNotSupportedException e) {
				Log.e(ZyyKNXConstant.DEBUG, e.getLocalizedMessage());
			}
			
			return addressAndAction;
		}
		
		private KNXGroupAddress address;
		public void setAddress(KNXGroupAddress address) {
			this.address = address;
		}
		public KNXGroupAddress getAddress() {
			return this.address;
		}
		
		private KNXGroupAddressAction action;
		public void setAction(KNXGroupAddressAction action) {
			this.action = action;
		}
		public KNXGroupAddressAction getAction() {
			return this.action;
		}
		
		private boolean isRead;
		public void setIsRead(boolean read) {
			this.isRead = read;
		}
		public boolean getIsRead() {
			return this.isRead;
		}
	}
}
