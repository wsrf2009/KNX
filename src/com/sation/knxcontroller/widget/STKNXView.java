package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class STKNXView extends RelativeLayout {
	protected KNXView mKNXView;

	public STKNXView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	protected STKNXView (Context context) {
		super(context);
		
		this.setWillNotDraw(false);
	}
	
	protected STKNXView(Context context, KNXView view) {
		this(context);

		this.mKNXView = view;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	public void onSuspend() {

	}

	public void onResume() {

	}
	
	public void onDestroy() {
		
	}
}
