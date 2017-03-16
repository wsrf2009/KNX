package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.MeasurementUnit;

public class KNXDigitalAdjustment extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	public enum EDigitalNumber {
		OneDigit("1位数字"),
		TwoDigit("2位数字"),
		ThreeDigit("3位数字");
		
		private String description;
		private EDigitalNumber(String str) {
			description = str;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
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
		if(null != this.LeftImage) {
			return this.getImagePath()+this.LeftImage;
		} else {
			return null;
		}
	}

	private String RightImage;
	public String getRightImage() {
		if (null != this.RightImage) {
			return this.getImagePath() + this.RightImage;
		} else {
			return null;
		}
	}
	
	public int DigitalNumber;
	public EDigitalNumber getDigitalNumber() {
		return EDigitalNumber.values()[this.DigitalNumber];
	}
	public void setDigitalNumber(EDigitalNumber num) {
		this.DigitalNumber = num.ordinal();
	}
	
	public int MaxValue;
	
	public int MinValue;
	
	public int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}
	
}
