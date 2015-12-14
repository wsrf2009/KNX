package com.zyyknx.android.widget;
 
import java.util.Timer;
import java.util.TimerTask;
 
import com.zyyknx.android.control.KNXButton;  
import com.zyyknx.android.R;

import android.content.Context; 
import android.view.MotionEvent;
import android.view.View;  
import android.widget.Button;  


public class MyButton extends ControlView {

	public final static long REPEAT_CMD_INTERVAL = 200;
	
	private Button uiButton;
	
	public MyButton(Context context) {
		super(context);
	}

	public MyButton(Context context, KNXButton mKNXButton) {
		super(context);
		
		setKNXControl(mKNXButton);
		if (mKNXButton != null) {
			uiButton = new Button(context, null, R.attr.styleButton);
			initButton(mKNXButton);
		}
	}

	private void initButton(final KNXButton mKNXButton) { 
		uiButton.setId(mKNXButton.getId());
		uiButton.setText(mKNXButton.getText());   
		
		View.OnTouchListener touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 
					sendCommand(mKNXButton);
					 
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							sendCommand(mKNXButton);
						}
					}, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
					setTimer(timer);
				}
				return false;
			}
		};
		uiButton.setOnTouchListener(touchListener); 
		uiButton.setClickable(true);
		
		//增加控件
		addView(uiButton);
		//设置控件大小
		setControlDefaultSize(uiButton);  
	}

	private void sendCommand(KNXButton mKNXButton) {
		//sendCommandRequest("click");
		sendCommandRequest(mKNXButton.getWriteAddressIds(), "1", false, null);
	}
}
