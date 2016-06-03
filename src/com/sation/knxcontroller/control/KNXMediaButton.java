package com.sation.knxcontroller.control;

public class KNXMediaButton extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//自定义媒体按钮图片Icon(CustomIcon和MediaButtonType不能共存)
	private String CustomImage;
	public String getCustomImage() {
		return CustomImage;
	}
	public void setCustomImage(String customImage) {
		CustomImage = customImage;
	}
	
	//自定义媒体按钮类型(CustomIcon和MediaButtonType不能共存)
	private String MediaButtonType;
	public String getMediaButtonType() {
		return MediaButtonType;
	}
	public void setMediaButtonType(String mediaButtonType) {
		MediaButtonType = mediaButtonType;
	} 
}
