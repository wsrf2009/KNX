package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXPage;

import android.content.Context;

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

//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		int cCount = getChildCount();
//		
//		/*
//		 * 遍历所有childView根据其宽和高，以及margin进行布局
//		 */
//		for (int i = 0; i < cCount; i++) {
//			STKNXView childView = (STKNXView)getChildAt(i);
//
//			int cl = 0, ct = 0, cr = 0, cb = 0;
//			cl = childView.mKNXView.Left;
//			ct = childView.mKNXView.Top;
//			cr = cl+childView.mKNXView.Width;
//			cb = ct+childView.mKNXView.Height;
//
//			childView.layout(cl, ct, cr, cb);
//		}
//	}
}
