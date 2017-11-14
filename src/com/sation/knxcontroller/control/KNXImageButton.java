package com.sation.knxcontroller.control;

/**
 * Created by wangchunfeng on 2017/5/19.
 */

public class KNXImageButton extends KNXControlBase {
    private static final long serialVersionUID = 1L;

    private String ImageOn;
    public String getImageOn() {
        return ImageOn;
    }
//    public String getImageOn() {
//        return this.getImagePath() + "ImageOn.png";
//    }

    private String ImageOff;
    public String getImageOff() {
        return ImageOff;
    }
//    public String getImageOff() {
//        return this.getImagePath() + "ImageOff.png";
//    }
}
