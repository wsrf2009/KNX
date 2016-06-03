package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXSceneButton;

import android.content.Context;
import android.view.View;

public class STKNXGroupBox extends STKNXViewContainer {
	public KNXGroupBox mKNXGroupBox;

	public STKNXGroupBox(Context context, KNXGroupBox groupBox) {
		super(context, groupBox);

		this.mKNXGroupBox = groupBox; 
		this.setId(this.mKNXGroupBox.getId());
	}

	public void setSelectedValue(int value) {
		for(int i=0; i<this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			if(v instanceof STKNXSceneButton) {
				STKNXSceneButton mSTKNXSceneButton = (STKNXSceneButton)v;
				KNXSceneButton mKNXSceneButton = mSTKNXSceneButton.mKNXSceneButton;
				if(mKNXSceneButton.IsGroup) {
					if(value == mKNXSceneButton.DefaultValue) {
						mSTKNXSceneButton.setSelected(true);
					} else {
						mSTKNXSceneButton.setSelected(false);
					}
				}
			}
		}
	}
}
