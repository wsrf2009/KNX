package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXPage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class STKNXPage extends STKNXViewContainer {
	private KNXPage mKNXPage;

	public STKNXPage(Context context, KNXPage page) {
		super(context, page);

		this.mKNXPage = page;
		this.setId(this.mKNXPage.getId());
	}

}
