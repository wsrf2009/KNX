/**
 * 
 */
package com.sation.knxcontroller.widget;

import java.util.Calendar;

import com.sation.knxcontroller.R;

import android.content.Context;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;

public class CustomTimerPickerDialog extends PromptDialog.Builder implements OnValueChangeListener, OnScrollListener, Formatter {
	private NumberPicker npHour;
	private NumberPicker npMinute;
	private NumberPicker npSecond;
	private OnTimerChangedListener onTimerChangedListener;
	
	public CustomTimerPickerDialog(Context context) {
		super(context);
		
		View view = View.inflate(context, R.layout.custom_time_picker_layout, null);
		setView(view);
		
		npHour = (NumberPicker)view.findViewById(R.id.customTimePickerLayouNumberPickerTimeIntervalHour);
		npMinute = (NumberPicker)view.findViewById(R.id.customTimePickerLayouNumberPickerTimeIntervalMinute);
		npSecond = (NumberPicker)view.findViewById(R.id.customTimePickerLayouNumberPickerTimeIntervalSecond);
		
		npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npSecond.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		
		Calendar cal = Calendar.getInstance();
		
		npHour.setFormatter(CustomTimerPickerDialog.this);
		npHour.setOnValueChangedListener(this);
		npHour.setOnScrollListener(this);
		npHour.setMaxValue(23);
		npHour.setMinValue(0);
		npHour.setValue(cal.get(Calendar.HOUR_OF_DAY));
	    
		npMinute.setFormatter(this);
		npMinute.setOnValueChangedListener(this);
		npMinute.setOnScrollListener(this);
		npMinute.setMaxValue(59);
		npMinute.setMinValue(0);
	    npMinute.setValue(cal.get(Calendar.MINUTE));
	    
	    npSecond.setFormatter(this);
	    npSecond.setOnValueChangedListener(this);
	    npSecond.setOnScrollListener(this);
	    npSecond.setMaxValue(59);
	    npSecond.setMinValue(0);
	    npSecond.setValue(cal.get(Calendar.SECOND));
	}
	
	public CustomTimerPickerDialog(Context context, int hour, int minute, int second) {
		this(context);
		
		npHour.setValue(hour);
		npMinute.setValue(minute);
		npSecond.setValue(second);
	}
	
	public interface OnTimerChangedListener {
		public void onTimerChangedListener(CustomTimerPickerDialog dialog, int hour, int minute, int second);
	}

	/* (non-Javadoc)
	 * @see android.widget.NumberPicker.Formatter#format(int)
	 */
	@Override
	public String format(int value) {
		String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
	}

	/* (non-Javadoc)
	 * @see android.widget.NumberPicker.OnValueChangeListener#onValueChange(android.widget.NumberPicker, int, int)
	 */
	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		if (null != onTimerChangedListener) {
			onTimerChangedListener.onTimerChangedListener(this, getHour(), getMinute(), getSecond());
		}
	}

	@Override
	public void onScrollStateChange(NumberPicker view, int scrollState) {

	}
	
	public void setHour(int hour) {
		npHour.setValue(hour);
	}
	
	public void setMinute(int minute) {
		npMinute.setValue(minute);
	}
	
	public void setSecond(int second) {
		npSecond.setValue(second);
	}
	
	public void setOnTimerChangedListener(OnTimerChangedListener listener) {
		onTimerChangedListener = listener;
	}
	
	public int getHour() {
		return npHour.getValue();
	}
	
	public int getMinute() {
		return npMinute.getValue();
	}
	
	public int getSecond() {
		return npSecond.getValue();
	}


}
