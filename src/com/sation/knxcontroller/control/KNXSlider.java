package com.sation.knxcontroller.control; 
 
public class KNXSlider extends KNXControlBase {
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

	//最小值
	private int MinValue; 
	public int getMinValue() {
		return MinValue;
	}
	public void setMinValue(int minValue) {
		MinValue = minValue;
	}
	
	//最大值
	private int MaxValue;
	public int getMaxValue() {
		return MaxValue;
	}
	public void setMaxValue(int maxValue) {
		MaxValue = maxValue;
	}
	
	//要滑块两侧显示的符号。
	private int ControlSymbol; 
	public int getControlSymbol() {
		return ControlSymbol;
	}
	public void setControlSymbol(int controlSymbol) {
		ControlSymbol = controlSymbol;
	}

	//滑动时延迟时间(单位毫秒)
	private int SendInterval;
	public int getSendInterval() {
		return SendInterval;
	}
	public void setSendInterval(int sendInterval) {
		SendInterval = sendInterval;
	}
}
