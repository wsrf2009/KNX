package com.sation.knxcontroller.widget;
 
import com.sation.knxcontroller.control.KNXControlBase;

import android.content.Context;
import android.util.AttributeSet; 
import android.view.View;
import android.widget.FrameLayout;  

public class ComponentView extends FrameLayout /*LinearLayout*/ {

	private KNXControlBase mKNXControlBase;

	protected ComponentView(Context context) {
		super(context); 
	}
	
	protected ComponentView(Context context, AttributeSet attr) {
		super(context, attr); 
	
	}
	
	protected ComponentView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle); 
	}

	public KNXControlBase getKNXControl() {
		return mKNXControlBase;
	}

	public void setKNXControl(KNXControlBase _KNXControlBase) {
		this.mKNXControlBase = _KNXControlBase;
	}
	
	protected void setControlDefaultSize(View view) {
		/*
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ZyyKNXConstant.CONTROL_DEFAULT_WIDTH, getResources().getDisplayMetrics());
		layoutParams.width = px;
		view.setLayoutParams(layoutParams); 
		*/
	} 

}
