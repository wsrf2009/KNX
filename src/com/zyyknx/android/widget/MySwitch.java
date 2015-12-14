package com.zyyknx.android.widget;

import android.content.Context; 
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.zyyknx.android.R; 
import com.zyyknx.android.control.KNXBlinds;
import com.zyyknx.android.control.KNXSwitch;
import com.zyyknx.android.widget.ControlView;

public class MySwitch extends ControlView {

	private KNXSwitch mKNXBlinds; 
	
	public final static long REPEAT_CMD_INTERVAL = 200;
	
	private ToggleButton toggleButton;
	
	public MySwitch(Context context) {
		super(context);
	}

	public MySwitch(Context context, KNXSwitch kNXSwitch) {
		super(context);
		this.mKNXBlinds = kNXSwitch;
		setKNXControl(kNXSwitch);
		if (kNXSwitch != null) {
			toggleButton = new ToggleButton(context, null, R.attr.StyleToggleButton);
			initButton(kNXSwitch);
		}
	}

	private void initButton(final KNXSwitch mKNXSwitch) { 
		toggleButton.setText(mKNXSwitch.getText());
		toggleButton.setTextOn(mKNXSwitch.getText());
		toggleButton.setTextOff(mKNXSwitch.getText());
		
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sendCommandRequest(mKNXBlinds.getWriteAddressIds(), isChecked == true ?  "1" : "0", false, null);
			}
		}); 
		
		//增加控件
		addView(toggleButton);
		//设置控件大小
		setControlDefaultSize(toggleButton); 
	}
}
