package com.sation.knxcontroller.control;

import java.util.Map;

import com.sation.knxcontroller.models.KNXSelectedAddress;

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
		return LeftImage;
	}
//	public void setLeftImage(String leftImage) {
//		LeftImage = leftImage;
//	}
	
	//Slider左边背景图片(SliderSymbol与此属性不能共存)
	private String RightImage;
	public String getRightImage() {
		return RightImage;
	}
//	public void setRightImage(String rightImage) {
//		RightImage = rightImage;
//	} 
	
	//Slider滑动图片
	private String SliderImage;
	public String getSliderImage() {
		return SliderImage;
	}
//	public void setSliderImage(String sliderImage) {
//		SliderImage = sliderImage;
//	} 
	
	public int IsRelativeControl;
	public EBool getIsRelativeControl(){
		return EBool.values()[this.IsRelativeControl];
	}
}
