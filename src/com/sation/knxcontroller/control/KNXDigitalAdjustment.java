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
	
	public String LeftImage;

	public String RightImage;
	
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
