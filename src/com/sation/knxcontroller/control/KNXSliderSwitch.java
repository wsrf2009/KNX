package com.sation.knxcontroller.control;

import android.graphics.drawable.GradientDrawable;

public class KNXSliderSwitch extends KNXControlBase {
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
	
	//Slider左边背景图片(SliderSymbol与此属性不能共存)
	private String LeftImage;
	public String getLeftImage() {
		if (null != this.LeftImage) {
			return this.getImagePath() + this.LeftImage;
		} else {
			return null;
		}
	}
//	public void setLeftImage(String leftImage) {
//		LeftImage = leftImage;
//	}
	
	//Slider左边背景图片(SliderSymbol与此属性不能共存)
	private String RightImage;
	public String getRightImage() {
		if (null != this.RightImage) {
			return this.getImagePath() + this.RightImage;
		} else {
			return null;
		}
	}
//	public void setRightImage(String rightImage) {
//		RightImage = rightImage;
//	} 
	
	//Slider滑动图片
	private String SliderImage;
	public String getSliderImage() {
		if (null != this.SliderImage) {
			return this.getImagePath() + this.SliderImage;
		} else {
			return null;
		}
	}
//	public void setSliderImage(String sliderImage) {
//		SliderImage = sliderImage;
//	} 
	
	public int IsRelativeControl;
	public EBool getIsRelativeControl(){
		return EBool.values()[this.IsRelativeControl];
	}

	private int Orientation;
	public EOrientation getOrientation() {
		return EOrientation.values()[this.Orientation];
	}

	private int SliderWidth;
	public int getSliderWidth() {
		return SliderWidth;
	}
	public void setSliderWidth(int width) {
		this.SliderWidth = width;
	}
}
