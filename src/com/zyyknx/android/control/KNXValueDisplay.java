package com.zyyknx.android.control;

public class KNXValueDisplay extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	// 标签
	private String Lable;
	public String getLable() {
		return Lable;
	}
	public void setLable(String lable) {
		Lable = lable;
	} 

	// 用于添加单元标识符显示的值，例如一个可选字段：用于显示当前温度，”°C”可以插入。
	private String Unit;
	public String getUnit() {
		return Unit;
	}
	public void setUnit(String unit) {
		Unit = unit;
	}

	// 显示的值
	private String Value;
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}

	// 可选的姓名或名称的显示值。这是类似的标签，但直接显示的实际值。
	private String Description;
	public String getDescription() {
		return Description;
	} 
	public void setDescription(String description) {
		Description = description;
	}

	// 定义小数位数
	private int DecimalPlaces;
	public int getDecimalPlaces() {
		return DecimalPlaces;
	} 
	public void setDecimalPlaces(int decimalPlaces) {
		DecimalPlaces = decimalPlaces;
	}
}
