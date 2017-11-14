<<<<<<< HEAD
package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.EDecimalDigit;
import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.models.MeasurementUnit;

public class KNXValueDisplay extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	// 用于添加单元标识符显示的值，例如一个可选字段：用于显示当前温度，”°C”可以插入。
	private int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

	private int DecimalDigit;
	public int getDecimalDigitInt() {
		return this.DecimalDigit;
	}
	public EDecimalDigit getDecimalDigit() {
		return EDecimalDigit.values()[this.DecimalDigit];
	}
	public String getDecimalDigitDescription() {
		EDecimalDigit dd = EDecimalDigit.values()[this.DecimalDigit];
		return dd.getDescription();
	}

	public KNXFont ValueFont;
}
=======
package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.EDecimalDigit;
import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.models.MeasurementUnit;

public class KNXValueDisplay extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	// 用于添加单元标识符显示的值，例如一个可选字段：用于显示当前温度，”°C”可以插入。
	private int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

	private int DecimalDigit;
	public int getDecimalDigitInt() {
		return this.DecimalDigit;
	}
	public EDecimalDigit getDecimalDigit() {
		return EDecimalDigit.values()[this.DecimalDigit];
	}
	public String getDecimalDigitDescription() {
		EDecimalDigit dd = EDecimalDigit.values()[this.DecimalDigit];
		return dd.getDescription();
	}

	public KNXFont ValueFont;
}
>>>>>>> SationCentralControl(10inch)
