package com.sation.knxcontroller.util;

import android.graphics.Color;

public class ColorUtils {
	/**
	 * 相对调节颜色的亮度
	 * 
	 * @param oldColor 调节亮度的基础色，类型为Integer
	 * @param percent 调节的百分比，整型量：-100~100
	 * 
	 * @return 调节亮度后的颜色，类型为Integer
	 * 
	 * */
    public static int changeBrightnessOfColor(int oldColor, int percent)
    {
        int R = Color.red(oldColor);
        int G = Color.green(oldColor);
        int B = Color.blue(oldColor);

        Double Y = 0.299 * R + 0.587 * G + 0.114 * B; ;
        Double Cb = 0.564*(B - Y);
        Double Cr = 0.713 * (R - Y);

        Y += Y * percent / 100;

        R = (int)(Y + 1.042 * Cr);
        G = (int)(Y - 0.344 * Cb - 0.714 * Cr);
        B = (int)(Y + 1.772 * Cb);

        if (R > 255)
        {
            R = 255;
        }
        else if (R < 0)
        {
            R = 0;
        }

        if (G > 255)
        {
            G = 255;
        }
        else if (G < 0)
        {
            G = 0;
        }

        if (B > 255)
        {
            B = 255;
        }
        else if (B < 0)
        {
            B = 0;
        }

        return Color.rgb(R, G, B);
    }

}
