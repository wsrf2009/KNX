package com.sation.knxcontroller.control;

public class KNXSliderSwitch extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//Slider左边背景图片(SliderSymbol与此属性不能共存)
	private String LeftImage;
	public String getLeftImage() {
		return LeftImage;
	}
	public void setLeftImage(String leftImage) {
		LeftImage = leftImage;
	}
	
	//Slider左边背景图片(SliderSymbol与此属性不能共存)
	private String RightImage;
	public String getRightImage() {
		return RightImage;
	}
	public void setRightImage(String rightImage) {
		RightImage = rightImage;
	} 
	
	//Slider滑动图片
	private String SliderImage;
	public String getSliderImage() {
		return SliderImage;
	}
	public void setSliderImage(String sliderImage) {
		SliderImage = sliderImage;
	} 
	
	public boolean IsRelativeControl;
}
