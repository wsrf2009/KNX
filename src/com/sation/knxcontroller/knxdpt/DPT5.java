package com.sation.knxcontroller.knxdpt;

import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MathUtils;

/**
 * Created by wangchunfeng on 2017/5/3.
 * 8-Bit Unsigned Value
 */
public class DPT5 {
    /**
    * 5.001 DPT_Scaling
    */
    public static int byte2Scaling(byte[] array) {
        int i = array[0] & 0xFF;
        return  Math.round((float) i * 100 / 255);
    }
    public static int Scaling2byte(float value) {
        return (int)(value * 255);
    }

    /**
     * 5.003 DPT_Angle
     */
    public static int byte2Angle(byte[] array) {
        int i = array[0] & 0xFF;
        return Math.round((float) i * 360 / 255);
    }

    /**
     * 5.004 DPT_Percent_U8
     */
    public static int byte2percentu8(byte[] array) {
        return array[0] & 0xFF;
    }

    /**
     * 5.005 DPT_DecimalFactor
     *
     * @param array
     * @return
     */
    public static int byte2DecimalFactor(byte[] array) {
        return array[0] & 0xFF;
    }

    /**
     * 5.010 DPT_Value_1_Ucount
     */
    public static int byte2Value1Ucount(byte[] array) {
        return array[0] & 0xFF;
    }
}
