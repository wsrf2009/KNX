package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXView;

import android.content.Context;
import android.widget.RelativeLayout;

public class STKNXView extends RelativeLayout {
	protected KNXView mKNXView;
	
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
}
