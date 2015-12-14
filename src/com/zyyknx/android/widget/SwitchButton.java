package com.zyyknx.android.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXButton;
import com.zyyknx.android.control.KNXImageButton; 

public class SwitchButton  extends ControlView {
	
public final static long REPEAT_CMD_INTERVAL = 200;
	
	private IconButton iconButton;
	
	public SwitchButton(Context context) {
		super(context);
	}

	public SwitchButton(Context context, KNXButton mKNXImageButton) {
		super(context);
		
		setKNXControl(mKNXImageButton);
		if (mKNXImageButton != null) { 
			iconButton = new IconButton(context, null, R.attr.styleButton);
			initButton(mKNXImageButton);
		}
	}

	private void initButton(final KNXButton mKNXImageButton) { 
		iconButton.setTag(mKNXImageButton.getId()); 
		
		Drawable drawable= getResources().getDrawable(R.drawable.shixun_silder_swich_light_down);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(20, 20, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		iconButton.setCompoundDrawables(drawable,null,null,null); 
		
		View.OnTouchListener touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 
					//sendCommand(mKNXImageButton); 
					
					/*
					uiImageButton.postDelayed(new Runnable() {

			            @Override
			            public void run() {
			                
			            }
			        }, 10);
					*/
					
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							//sendCommand(mKNXImageButton);
						}
					}, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
					setTimer(timer);
				}
				return false;
			}
		};
		iconButton.setOnTouchListener(touchListener); 
		iconButton.setClickable(true);
		
		//增加控件
		addView(iconButton);
		//设置控件大小
		setControlDefaultSize(iconButton);  
	}

	private void sendCommand(KNXImageButton mKNXImageButton) {
		//sendCommandRequest("click");
		sendCommandRequest(mKNXImageButton.getWriteAddressIds(), "1", false, null);
	}
}
