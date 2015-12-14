package com.zyyknx.android.widget;
 

import android.content.Context; 
import android.view.View; 
import android.widget.Button;

import com.zyyknx.android.R; 
import com.zyyknx.android.activity.SceneRecordDialog; 
import com.zyyknx.android.control.KNXSceneButton;
import com.zyyknx.android.util.Log;

public class MySceneButton extends ControlView {

	public final static long REPEAT_CMD_INTERVAL = 200;
	
	private Button uiButton;
	private KNXSceneButton mKNXSceneButton;
	private Context mContext;
	 
	
	public MySceneButton(Context context) {
		super(context);
		mContext = context;
	}

	public MySceneButton(Context context, KNXSceneButton mKNXSceneButton) {
		super(context);
		this.mContext = context;
		this.mKNXSceneButton = mKNXSceneButton;
		
		setKNXControl(mKNXSceneButton);
		if (mKNXSceneButton != null) {
			uiButton = new Button(context, null, R.attr.styleButton);
			initButton(mKNXSceneButton);
		}
	}

	private void initButton(final KNXSceneButton mKNXSceneButton) { 
		uiButton.setId(mKNXSceneButton.getId());
		uiButton.setTag("1");
		uiButton.setText(mKNXSceneButton.getText());   
		
		/*
		View.OnTouchListener touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 
					sendCommand(mKNXSceneButton);
					 
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							sendCommand(mKNXSceneButton);
						}
					}, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
					setTimer(timer);
				}
				return false;
			}
		};
		uiButton.setOnTouchListener(touchListener); 
		*/
		
		uiButton.setClickable(true); 
		uiButton.setOnClickListener(onClickListener); 
		
		uiButton.setLongClickable(true);
		uiButton.setOnLongClickListener(new OnLongClickListener() { 
			 @Override
			 public boolean onLongClick(View v) {
				new SceneRecordDialog(mContext, mKNXSceneButton.getId()).Show();  
				return false;  
			 } 
		});
		
		//增加控件
		addView(uiButton);
		//设置控件大小
		setControlDefaultSize(uiButton);  
	}
	
	OnClickListener onClickListener = new OnClickListener() {
    	
        public void onClick(View v) { 
        	if(v.getTag().toString().equals("0")) { 
        		v.setTag("1");
        	} else {
        		v.setTag("0");  
        	}

        	sendCommandRequest(mKNXSceneButton.getWriteAddressIds(), v.getTag().toString(), false, null); 
        }
    };

	private void sendCommand(KNXSceneButton mKNXSceneButton) {
		//sendCommandRequest("click");
		sendCommandRequest(mKNXSceneButton.getWriteAddressIds(), "1", true, null);
	}
}
