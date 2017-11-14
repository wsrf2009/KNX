package com.sation.knxcontroller.models;

public enum MeasurementUnit {
	None(""),
	Centigrade("℃"),
//	Fahrenheit("℉"),
	Ampere("A"),
	Milliampere("mA"),
	Kilowatt("KW");
	
	private String description;
	private MeasurementUnit(String str) {
		description = str;
	}
	public String getDescription() {
		return description;
	}
}
