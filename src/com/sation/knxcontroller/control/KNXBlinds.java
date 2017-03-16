package com.sation.knxcontroller.control;

public class KNXBlinds extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
//	private Map<String ,KNXSelectedAddress> ReadAddressId; 
//	public Map<String ,KNXSelectedAddress> getReadAddressId() {
//		return ReadAddressId;
//	}
//
//	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
//	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
//		return WriteAddressIds;
//	}

	private String LeftImage;
	public String getLeftImage() {
		if (null != this.LeftImage) {
			return this.getImagePath() + this.LeftImage;
		} else {
			return null;
		}
	}
	
	public String LeftText;
	
	public int LeftTextFontSize;
	
	public String LeftTextFontColor;
	
	private String RightImage;
	public String getRightImage() {
		if (null != this.LeftImage) {
			return this.getImagePath() + this.RightImage;
		} else {
			return null;
		}
	}
	
	public String RightText;
	
	public int RightTextFontSize;
	
	public String RightTextFontColor;
}
