package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.KNXObject;

/**
 * Created by wangchunfeng on 2017/8/28.
 */

public class KNXDimmer extends KNXControlBase {
    private String Symbol;
    public String getSymbol() {
        return this.Symbol;
    }

    private String ImageOn;
    public String getImageOn() {
        return this.ImageOn;
    }

    private String ImageOff;
    public String getImageOff() {
        return this.ImageOff;
    }

    private KNXObject Switch;
    public KNXObject getSwitch() {
        return this.Switch;
    }

    private KNXObject DimRelatively;
    public KNXObject getDimRelatively() {
        return this.DimRelatively;
    }

    private KNXObject DimAbsolutely;
    public KNXObject getDimAbsolutely() {
        return this.DimAbsolutely;
    }

    private KNXObject StateOnOff;
    public KNXObject getStateOnOff() {
        return this.StateOnOff;
    }

    private KNXObject StateDimValue;
    public KNXObject getStateDimValue() {
        return this.StateDimValue;
    }
}
