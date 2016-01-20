package com.zyyknx.android.widget;

import java.math.BigDecimal;

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXBlinds;
import com.zyyknx.android.control.KNXValueDisplay;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.TextView;

public class MyValueDisplay extends ControlView {
	
	private KNXValueDisplay mKNXValueDisplay;
	
	private TextView leftTextView;
	private TextView rightTextView;
	
	public MyValueDisplay(Context context) {
		this(context, null, null);
	} 
	
	public MyValueDisplay(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, null);
	} 

	public MyValueDisplay(Context context, AttributeSet attrs , KNXValueDisplay mKNXValueDisplay) {
		super(context, attrs); 
		setKNXControl(mKNXValueDisplay);
		this.mKNXValueDisplay = mKNXValueDisplay;
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.value_display_widge, this, true);
		 
		 
		leftTextView = (TextView) v.findViewById(R.id.leftTextView);
		leftTextView.setGravity(Gravity.LEFT);
		rightTextView = (TextView) v.findViewById(R.id.rightTextView);
		rightTextView.setGravity(Gravity.RIGHT); 

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyValueDisplay, 0, 0);  
		a.recycle();
		
		//初始化值
		initValueDisplay(mKNXValueDisplay);  
	} 
	
	
	private void initValueDisplay(final KNXValueDisplay mKNXValueDisplay) {  
		if( mKNXValueDisplay != null) {
			leftTextView.setText(mKNXValueDisplay.getText());
			if(mKNXValueDisplay.getDecimalPlaces() >0) {
				BigDecimal bd = new BigDecimal(mKNXValueDisplay.getValue());     
		        BigDecimal bd2 = bd.setScale(mKNXValueDisplay.getDecimalPlaces(), BigDecimal.ROUND_HALF_UP);  
		        rightTextView.setText(bd2 + mKNXValueDisplay.getUnit());
			} else {
				rightTextView.setText(mKNXValueDisplay.getValue() + mKNXValueDisplay.getUnit());
			}
		}
	}
}
