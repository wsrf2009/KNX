package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXPage;

import android.content.Context;
import android.view.View;

public class STKNXPage extends STKNXViewContainer {
	private KNXPage mKNXPage;
	public KNXPage getKNXPage(){
		return mKNXPage;
	}

	public STKNXPage(Context context, KNXPage page) {
		super(context, page);

		this.mKNXPage = page;
		this.setId(this.mKNXPage.getId());
	}

	public void onDestroy() {
//		this.mKNXPage = null;
				
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = (View)getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onDestroy();
				v = null;
			}
		}
	}
}
