package com.zyyknx.android.models;

import java.io.Serializable;

public class KNXResponse implements Serializable {
	
	///KNXId
	public String KNXId;
	public String getKNXId() {
		return KNXId;
	}
	public void setKNXId(String mKNXId) {
		this.KNXId = mKNXId;
	}
	
	///控件的的值
	public int ControlValue;
	public int getControlValue() {
		return ControlValue;
	}
	public void setControlValue(int mControlValue) {
		this.ControlValue = mControlValue;
	}
}
