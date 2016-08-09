package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.R;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimeAndWeekdayPickerDialog extends PromptDialog.Builder {
	private TimePicker mTimePicker;
    private CheckBox cbMonday;
    private CheckBox cbTuesday;
    private CheckBox cbWednesday;
    private CheckBox cbThursday;
    private CheckBox cbFriday;
    private CheckBox cbSaturday;
    private CheckBox cbSunday;
    private OnChangedListener changedListener;

	public TimeAndWeekdayPickerDialog(Context context) {
		super(context);

		View view = View.inflate(context, R.layout.time_and_weekday_layout, null);
		setView(view);
		
		mTimePicker = (TimePicker)view.findViewById(R.id.timeAndWeekdayLayoutTimePickerTime);
		mTimePicker.setOnTimeChangedListener(new TimeChangedListener());
		
		cbMonday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxMonday);
		cbMonday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbTuesday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxTuesday);
		cbTuesday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbWednesday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxWednesday);
		cbWednesday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbThursday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxThursday);
		cbThursday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbFriday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxFriday);
		cbFriday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbSaturday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxSaturday);
		cbSaturday.setOnCheckedChangeListener(new WeekdayChangedListener());
		
		cbSunday = (CheckBox)view.findViewById(R.id.timeAndWeekdayLayoutCheckBoxSunday);
		cbSunday.setOnCheckedChangeListener(new WeekdayChangedListener());
	}
	
	public TimeAndWeekdayPickerDialog(Context context, int hour, int minute, boolean monday, 
			boolean tuesday, boolean wednesday, boolean thursday, boolean friday, 
			boolean saturday, boolean sunday) {
		this(context);
		
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		cbMonday.setChecked(monday);
		cbTuesday.setChecked(tuesday);
		cbWednesday.setChecked(wednesday);
		cbThursday.setChecked(thursday);
		cbFriday.setChecked(friday);
		cbSaturday.setChecked(saturday);
		cbSunday.setChecked(sunday);
	}
	
	public interface OnChangedListener {
		public void onChangedListener(TimeAndWeekdayPickerDialog dialog, int hourOfDay, int minute, boolean isMondaySelected, boolean isTuesdaySelected,
				boolean isWednesdaySelected, boolean isThursdaySelected, boolean isFridaySelected,
				boolean isSaturdaySelected, boolean isSundaySelected);
	}
	
	public void setTime(boolean is24Hour, int hour, int minute) {
		mTimePicker.setIs24HourView(is24Hour);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
	}
	
	public void setMondayChecked(boolean checked) {
		cbMonday.setChecked(checked);
	}
	
	public void setTuesdayChecked(boolean checked) {
		cbTuesday.setChecked(checked);
	}
	
	public void setWednesdayChecked(boolean checked) {
		cbWednesday.setChecked(checked);
	}

	public void setThursdayChecked(boolean checked) {
		cbThursday.setChecked(checked);
	}

	public void setFridayChecked(boolean checked) {
		cbFriday.setChecked(checked);
	}

	public void setSaturdayChecked(boolean checked) {
		cbSaturday.setChecked(checked);
	}

	public void setSundayChecked(boolean checked) {
		cbSunday.setChecked(checked);
	}
	
	public void setOnChangedListener(OnChangedListener listener) {
		changedListener = listener;
	}
	
	public int getHour() {
		return mTimePicker.getCurrentHour();
	}
	
	public int getMinute() {
		return mTimePicker.getCurrentMinute();
	}
	
	public boolean getMondayChecked() {
		return cbMonday.isChecked();
	}
	
	public boolean getTuesdayChecked() {
		return cbTuesday.isChecked();
	}
	
	public boolean getWednesdayChecked() {
		return cbWednesday.isChecked();
	}
	
	public boolean getThursdayChecked() {
		return cbThursday.isChecked();
	}
	
	public boolean getFridayChecked() {
		return cbFriday.isChecked();
	}
	
	public boolean getSaturdayChecked() {
		return cbSaturday.isChecked();
	}
	
	public boolean getSundayChecked() {
		return cbSunday.isChecked();
	}
	
	private class TimeChangedListener implements OnTimeChangedListener {

		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			if(null != changedListener) {
				changedListener.onChangedListener(TimeAndWeekdayPickerDialog.this, hourOfDay, minute, cbMonday.isChecked(), cbTuesday.isChecked(),
						cbWednesday.isChecked(), cbThursday.isChecked(), cbFriday.isChecked(), cbSaturday.isChecked(), cbSunday.isChecked());
			}
		}
		
	}
	
	private class WeekdayChangedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(null != changedListener) {
				changedListener.onChangedListener(TimeAndWeekdayPickerDialog.this, getHour(), getMinute(), cbMonday.isChecked(), cbTuesday.isChecked(),
						cbWednesday.isChecked(), cbThursday.isChecked(), cbFriday.isChecked(), cbSaturday.isChecked(), cbSunday.isChecked());
			}
		}
	}
}
