package com.zyyknx.android.control;

public class KNXSnapperSwitch extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//SnapperSwitch左边背景图片(SliderSymbol与此属性不能共存)
	private String LeftImage;
	public String getLeftImage() {
		return LeftImage;
	}
	public void setLeftImage(String leftImage) {
		LeftImage = leftImage;
	}
	
	//SnapperSwitch左边背景图片(SliderSymbol与此属性不能共存)
	private String RightImage;
	public String getRightImage() {
		return RightImage;
	}
	public void setRightImage(String rightImage) {
		RightImage = rightImage;
	} 
	
	//Slider滑动图片
	private String SnapperImage;
	public String getSnapperImage() {
		return SnapperImage;
	}
	public void setSnapperImage(String snapperImage) {
		SnapperImage = snapperImage;
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
	private int SliderSymbol; 
	public int getSliderSymbol() {
		return SliderSymbol;
	}
	public void setSliderSymbol(int controlSymbol) {
		SliderSymbol = controlSymbol;
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
