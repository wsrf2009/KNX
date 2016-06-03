package com.sation.knxcontroller.widget;

import java.util.Calendar;

import com.sation.knxcontroller.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class CustomDateAndTimeDialog extends PromptDialog.Builder {
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private OnDateAndTimeChangedListener dateAndTimeChangedListener;
	private OnDateChangedListener dateChangedListener;
	private OnTimeChangedListener timeChangedListener;

	public CustomDateAndTimeDialog(Context context) {
		super(context);

		View view = View.inflate(context, R.layout.date_time_layout, null);
		setView(view);
		
		mDatePicker = (DatePicker)view.findViewById(R.id.datePickerDateTimeLayoutDate);
		final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH)+1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
		mDatePicker.init(year, month, day, new DateChangedListener());
		
		mTimePicker = (TimePicker)view.findViewById(R.id.timePickerDateTimeLayoutTime);
		mTimePicker.setOnTimeChangedListener(new TimeChangedListener());
	}
	
	public CustomDateAndTimeDialog(Context context, int year, int month, int day, int hour, int minute) {
		this(context);

		mDatePicker.updateDate(year, month, day);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
	}
	
	public interface OnDateAndTimeChangedListener {  
        public void onDateAndTimeChanged(CustomDateAndTimeDialog dialog, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute);
    }
	
	public interface OnDateChangedListener {
		public void onDateChanged(CustomDateAndTimeDialog dialog, int year, int monthOfYear, int dayOfMonth);
	}
	
	public interface OnTimeChangedListener {
		public void onTimeChanged(CustomDateAndTimeDialog dialog, int hourOfDay, int minute);
	}
	
	public CustomDateAndTimeDialog setOnDateAndTimeChangedListener(OnDateAndTimeChangedListener listener) {
		dateAndTimeChangedListener = listener;
		return this;
	}
	
	public CustomDateAndTimeDialog setOnDateChangedListener(OnDateChangedListener listener) {
		dateChangedListener = listener;
		return this;
	}
	
	public CustomDateAndTimeDialog setOnTimeChangedListener(OnTimeChangedListener listener) {
		timeChangedListener = listener;
		return this;
	}

	public CustomDateAndTimeDialog setDatePickerGone() {
		mDatePicker.setVisibility(View.GONE);
		return this;
	}
	
	public CustomDateAndTimeDialog setYearGone() {
		((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE); 
		return this;
	}
	
	public CustomDateAndTimeDialog setMonthGone() {
		((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE); 
		return this;
	}
	
	public CustomDateAndTimeDialog setIs24HourView(boolean is24Hour) {
		mTimePicker.setIs24HourView(is24Hour);
		return this;
	}
	
	public CustomDateAndTimeDialog setDate(int year, int month, int day) {
		mDatePicker.updateDate(year, month, day);
		return this;
	}
	
	public CustomDateAndTimeDialog setTime(boolean is24Hour, int hour, int minute) {
		mTimePicker.setIs24HourView(is24Hour);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		return this;
	}
	
	public int getYear() {
		return mDatePicker.getYear();
	}
	
	public int getMonth() {
		return mDatePicker.getMonth();
	}
	
	public int getDay() {
		return mDatePicker.getDayOfMonth();
	}
	
	public int getHour() {
		return mTimePicker.getCurrentHour();
	}
	
	public int getMinute() {
		return mTimePicker.getCurrentMinute();
	}

	private class DateChangedListener implements DatePicker.OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			if(null != dateAndTimeChangedListener) {
				dateAndTimeChangedListener.onDateAndTimeChanged(CustomDateAndTimeDialog.this, year, monthOfYear, dayOfMonth, getHour(), getMinute());
			}
			
			if (null != dateChangedListener) {
				dateChangedListener.onDateChanged(CustomDateAndTimeDialog.this, year, monthOfYear, dayOfMonth);
			}
		}
		
	}
	
	private class TimeChangedListener implements TimePicker.OnTimeChangedListener {

		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			if(null != dateAndTimeChangedListener) {
				dateAndTimeChangedListener.onDateAndTimeChanged(CustomDateAndTimeDialog.this, getYear(), getMonth(), getDay(), getHour(), getMinute());
			}
			
			if(null != timeChangedListener) {
				timeChangedListener.onTimeChanged(CustomDateAndTimeDialog.this, hourOfDay, minute);
			}
		}
		
	}
}
