<<<<<<< HEAD
package com.sation.knxcontroller.control;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.sation.knxcontroller.models.EDecimalDigit;
import com.sation.knxcontroller.models.ERegulationStep;
import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.models.MeasurementUnit;
import com.sation.knxcontroller.util.StringUtil;

public class KNXDigitalAdjustment extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	private String LeftImage;
	public String getLeftImage() {
		return LeftImage;
	}

	private String RightImage;
	public String getRightImage() {
		return RightImage;
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

	private float MaxValue;
	public float getMaxValue() {
		return this.MaxValue;
	}

	private float MinValue;
	public float getMinValue(){
		return this.MinValue;
	}

	private String RegulationStep;
    public String getRegulationStep() {
        return this.RegulationStep;
    }
	
	private int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

    public KNXFont ValueFont;
}
=======
package com.sation.knxcontroller.control;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.sation.knxcontroller.models.EDecimalDigit;
import com.sation.knxcontroller.models.ERegulationStep;
import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.models.MeasurementUnit;
import com.sation.knxcontroller.util.StringUtil;

public class KNXDigitalAdjustment extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	private String LeftImage;
	public String getLeftImage() {
		return LeftImage;
	}

	private String RightImage;
	public String getRightImage() {
		return RightImage;
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

	private float MaxValue;
	public float getMaxValue() {
		return this.MaxValue;
	}

	private float MinValue;
	public float getMinValue(){
		return this.MinValue;
	}

	private String RegulationStep;
    public String getRegulationStep() {
        return this.RegulationStep;
    }
	
	private int Unit;
	public String getUnit() {
		MeasurementUnit eUnit = MeasurementUnit.values()[this.Unit];
		return eUnit.getDescription();
	}

    public KNXFont ValueFont;
}
>>>>>>> SationCentralControl(10inch)
