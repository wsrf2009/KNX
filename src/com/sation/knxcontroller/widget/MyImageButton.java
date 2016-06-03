package com.sation.knxcontroller.widget; 

import android.content.Context; 
import android.view.LayoutInflater; 
import android.view.View;
import android.view.ViewGroup;  
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.sation.knxcontroller.control.KNXImageButton;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.ControlView.ICallBack;
import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;

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
		uiImageICON.setImageBitmap(ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + mKNXImageButton.getImage())); 
		uiImageICON.setScaleType(ScaleType.FIT_CENTER); 
		uiTextView.setText(mKNXImageButton.getText());
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
