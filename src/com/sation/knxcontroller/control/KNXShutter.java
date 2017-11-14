package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.KNXObject;

/**
 * Created by wangchunfeng on 2017/8/15.
 */

public class KNXShutter extends KNXControlBase {
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

    private KNXObject ShutterUpDown;
    public KNXObject getShutterUpDown() {
        return this.ShutterUpDown;
    }

    private KNXObject ShutterStop;
    public KNXObject getShutterStop() {
        return this.ShutterStop;
    }

    private KNXObject AbsolutePositionOfShutter;
    public KNXObject getAbsolutePositionOfShutter() {
        return this.AbsolutePositionOfShutter;
    }

    private KNXObject AbsolutePositionOfBlinds;
    public KNXObject getAbsolutePositionOfBlinds() {
        return this.AbsolutePositionOfBlinds;
    }

    private KNXObject StateUpperPosition;
    public KNXObject getStateUpperPosition() {
        return this.StateUpperPosition;
    }

    private KNXObject StateLowerPosition;
    public KNXObject getStateLowerPosition() {
        return this.StateLowerPosition;
    }

    private KNXObject StatusActualPositionOfShutter;
    public KNXObject getStatusActualPositionOfShutter() {
        return this.StatusActualPositionOfShutter;
    }

    private KNXObject StatusActualPositionOfBlinds;
    public KNXObject getStatusActualPositionOfBlinds() {
        return this.StatusActualPositionOfBlinds;
    }
}
