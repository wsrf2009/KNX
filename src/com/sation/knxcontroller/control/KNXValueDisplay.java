package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.MeasurementUnit;

public class KNXValueDisplay extends KNXControlBase {
	private static final long serialVersionUID = 1L;

//	private Map<String ,KNXSelectedAddress> ReadAddressId; 
//	public Map<String ,KNXSelectedAddress> getReadAddressId() {
//		return ReadAddressId;
//	}

	// 用于添加单元标识符显示的值，例如一个可选字段：用于显示当前温度，”°C”可以插入。
	private int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

//	// 显示的值
//	private String Value;
//	public String getValue() {
//		return Value;
//	}
//	public void setValue(String value) {
//		Value = value;
//	}
}
