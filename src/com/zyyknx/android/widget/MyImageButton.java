package com.zyyknx.android.widget; 

import android.content.Context; 
import android.view.LayoutInflater; 
import android.view.View;
import android.view.ViewGroup;  
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.zyyknx.android.R; 
import com.zyyknx.android.control.KNXImageButton; 
import com.zyyknx.android.util.ImageUtils;  
import com.zyyknx.android.util.StringUtil;
import com.zyyknx.android.widget.ControlView.ICallBack;

public class MyImageButton extends ControlView {

	public final static long REPEAT_CMD_INTERVAL = 200;
	 
	private KNXImageButton mKNXImageButton; 
	private ImageView uiImageICON;
	private TextView uiTextView; 
	
	public MyImageButton(Context context) {
		super(context);
	}

	public MyImageButton(Context context, KNXImageButton mKNXImageButton) {
		super(context);
		
		this.mKNXImageButton = mKNXImageButton;
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.image_text_widge, this, true);
		 
		
		uiImageICON = (ImageView) v.findViewById(R.id.uiImageICON); 
		uiTextView = (TextView) v.findViewById(R.id.uiTextView);
		
		uiImageICON.setOnClickListener(onClickListener); 
		
		setKNXControl(mKNXImageButton);
		if (mKNXImageButton != null) { 
			initButton(mKNXImageButton);
		}
	}

	private void initButton(final KNXImageButton mKNXImageButton) {  
		uiImageICON.setTag(mKNXImageButton.getId());
		uiImageICON.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXImageButton.getImage())); 
		uiImageICON.setScaleType(ScaleType.FIT_CENTER); 
		//uiImageICON.setVisibility(View.GONE);
		uiTextView.setText(mKNXImageButton.getText());
		/*
		View.OnTouchListener touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 
					//sendCommand(mKNXImageButton);   
					uiImageButton.postDelayed(new Runnable() {

			            @Override
			            public void run() {
			                
			            }
			        }, 10); 
					
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							sendCommand(mKNXImageButton);
						}
					}, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
					setTimer(timer);
				}
				return false;
			}
		};
		 
		uiImageICON.setOnTouchListener(touchListener); 
		*/
		
		uiImageICON.setClickable(true); 
		
	} 
	
	 OnClickListener onClickListener = new OnClickListener() {
	    	
	        public void onClick(final View v) { 
	        	
	        	sendCommandRequest(mKNXImageButton.getWriteAddressIds(), v.getTag().toString(), false, new ICallBack() {

					@Override
					public void onCallBack() {
						if(v.getTag().toString().equals("0")) { 
			        		v.setTag("1");   
			        	} else {
			        		v.setTag("0");  
			        	} 
					}
	        		 
	        	 }); 
	        }
	    }; 
}
