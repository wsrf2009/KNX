package com.zyyknx.android.widget; 

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXLabel;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity; 
import android.widget.TextView; 

public class MyLabel extends ControlView {

	/** The text view. */
	private TextView textView;

	/** The text. */
	private String text;

	public MyLabel(Context context) {
		super(context);
	}
	
	/**
	 * Instantiates a new label view. {@link #setGravity(int)} make the text be
	 * in center.
	 * 
	 * @param label
	 *            the label entity
	 */
	public MyLabel(Context context, KNXLabel mKNXLabel) {
		super(context);
		setKNXControl(mKNXLabel);
		//setGravity(Gravity.CENTER);
		if (mKNXLabel != null) {
			textView = new TextView(context, null, R.attr.styleLable);
			initLabel(mKNXLabel);
		}
	}

	private void initLabel(KNXLabel mKNXLabel) {
		textView.setGravity(Gravity.LEFT);
		textView.setId(mKNXLabel.getId());
		text = mKNXLabel.getText();
		if (text != null) {
			textView.setText(text);
		}
		/*
		if (mKNXLabel.getFontSize() > 0) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mKNXLabel.getFontSize());
		}
		if (mKNXLabel.getFontColor() != null) {
			textView.setTextColor(Color.parseColor(mKNXLabel.getFontColor()));
		}
		*/ 
		//增加控件
		addView(textView);
		//设置控件大小
		setControlDefaultSize(textView); 
	}
}
