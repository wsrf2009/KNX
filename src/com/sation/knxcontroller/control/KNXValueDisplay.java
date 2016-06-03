package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.MeasurementUnit;

public class KNXValueDisplay extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
//	public enum EDisplayAccurancy {
//		None("无小数"),
//		Bit1("1位小数");
//		
//		private String description;
//		private EDisplayAccurancy(String str) {
//			description = str;
//		}
//		
//		public String getDescription() {
//			return description;
//		}
//	}
	
	
	
//	// 标签
//	private String Lable;
//	public String getLable() {
//		return Lable;
//	}
//	public void setLable(String lable) {
//		Lable = lable;
//	} 
	
//	public String Unit;

	// 用于添加单元标识符显示的值，例如一个可选字段：用于显示当前温度，”°C”可以插入。
	private int Unit;
//	public int getUnit() {
//		return Unit;
//	}
//	public void setUnit(int unit) {
//		Unit = unit;
//	}
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

	// 显示的值
	private String Value;
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}

//	// 可选的姓名或名称的显示值。这是类似的标签，但直接显示的实际值。
//	private String Description;
//	public String getDescription() {
//		return Description;
//	} 
//	public void setDescription(String description) {
//		Description = description;
//	}

//	// 定义小数位数
//	private EDisplayAccurancy DecimalPlaces;
//	public EDisplayAccurancy getDecimalPlaces() {
//		return DecimalPlaces;
//	} 
//	public void setDecimalPlaces(EDisplayAccurancy decimalPlaces) {
//		DecimalPlaces = decimalPlaces;
//	}
}
