package com.sation.knxcontroller.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View; 
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

import com.sation.knxcontroller.control.KNXMediaButton;
import com.sation.knxcontroller.control.MediaButtonType;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.R;

public class MyMediaButton extends ControlView {

public final static long REPEAT_CMD_INTERVAL = 200;
	
	private ImageButton uiImageButton;
	
	public MyMediaButton(Context context) {
		super(context);
	}

	public MyMediaButton(Context context, KNXMediaButton mKNXMediaButton) {
		super(context);
		setKNXControl(mKNXMediaButton);
		if (mKNXMediaButton != null) {
			uiImageButton = new ImageButton(context, null,  R.attr.styleMediaButton);
			initMediaButton(mKNXMediaButton);
		}
	}

	private void initMediaButton(final KNXMediaButton mKNXMediaButton) { 
		uiImageButton.setId(mKNXMediaButton.getId());  
		if(mKNXMediaButton.getMediaButtonType() != null) {
			int mediaButtonType = Integer.valueOf(mKNXMediaButton.getMediaButtonType());
			switch(mediaButtonType) 
			{ 
			   case 0: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_back);
			       break; 
			   case 1: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_backward);
			       break; 
			   case 2: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_menu);
			       break;
			   case 3: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_stop);
			       break; 
			   case 4: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_backward_skip);
			       break; 
			   case 5: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_menu);
			       break; 
			   case 6: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_up);
			       break; 
			   case 7: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_okay); 
			       break; 
			   case 8: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_volume_down); 
			       break;
			   case 9: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_down); 
			       break;
			   case 10: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_pause); 
			       break;
			   case 11: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_volume_up); 
			       break;
			   case 12: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_forward); 
			       break;
			   case 13: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_play); 
			       break;
			   case 14: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_forward_skip); 
			       break;
			   case 15: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_power); 
			       break;
			   case 16: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_left); 
			       break;
			   case 17: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_right); 
			       break; 
			   default: 
				   uiImageButton.setImageResource(R.drawable.default_media_icon_menu);
			       break; 
			}      
		} else {
			 uiImageButton.setImageBitmap(ImageUtils.getDiskBitmap(mKNXMediaButton.getCustomImage()));
			 uiImageButton.setAdjustViewBounds(true);
			 uiImageButton.setPadding(10, 10, 10, 10);
			 uiImageButton.setScaleType(ScaleType.FIT_CENTER);
		}
		 
		View.OnTouchListener touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 
					sendCommand();
					 
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							sendCommand();
						}
					}, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
					setTimer(timer);
				} 
				return false;
			}
		};
		uiImageButton.setOnTouchListener(touchListener); 
		uiImageButton.setClickable(true);
		
		//增加控件
		addView(uiImageButton);
		//设置控件大小
		setControlDefaultSize(uiImageButton);  
	}

	private void sendCommand() {
		//sendCommandRequest("click");
	}
}
