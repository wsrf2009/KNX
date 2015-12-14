package com.zyyknx.android.control;

//网络摄像头
public class KNXWebcamViewer extends KNXControlBase {
	private static final long serialVersionUID = 1L;
 
	private String PinCode;
	public String getPinCode() {
		return PinCode;
	} 
	public void setPinCode(String pinCode) {
		PinCode = pinCode;
	}

}
