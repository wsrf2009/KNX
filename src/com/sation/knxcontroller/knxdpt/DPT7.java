package com.sation.knxcontroller.knxdpt;

import com.sation.knxcontroller.util.Log;

import java.math.BigDecimal;

/**
 * Created by wangchunfeng on 2017/4/28.
 * 2-Octet Unsigned Value
 */
public class DPT7 {
    private static final String TAG = "DPT7";

    public static byte[] int2bytes(int val) {
        int h = val / 256;
        int l = val % 256;

        byte[] arr = new byte[2];
        arr[0] = Integer.valueOf(h).byteValue();
        arr[1] = Integer.valueOf(l).byteValue();

        return arr;
    }

    public static int bytes2int(byte[] arr) {
        try {

            String h = Integer.toHexString(arr[0]); // 高位
            String l = Integer.toHexString(arr[1]); // 低位
            h = (h.length()>2)? h.substring(h.length()-2):h; // 只取最低两位
            l = (l.length()>2)?l.substring(l.length()-2):l; // 只取最低两位
            String v = h + l;
//            Log.i(TAG, "arr[0] = " + h + " " + "arr[1] = " + l + " v:" + v);
            int value = Integer.parseInt(v, 16);


//            Log.i(TAG, "value:" + value);
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
