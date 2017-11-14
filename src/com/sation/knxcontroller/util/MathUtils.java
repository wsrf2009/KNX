package com.sation.knxcontroller.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

	/**
	 * 根据val值在arr中找与其最接近值得索引
	 * @param val
	 * @param arr
	 * @return
	 */
	public static int getTheClosetIndex(int val, int[] arr){
		if(arr.length <= 0) {
			return 0;
		}
		
		if(1 == arr.length) {
			return 0;
		}
		
		int diff=Math.abs(val-arr[0]);
		int index = 0;
		for(int i=1; i<arr.length; i++) {
			int _diff = Math.abs(val-arr[i]);
			if(diff > _diff) {
				diff = _diff;
				index = i;
			}
		}
		
		return index;
	}

	public static  double Rounding(double d, int dg) {
		BigDecimal b   =   new   BigDecimal(d);
		double   v   =   b.setScale(dg,   RoundingMode.HALF_UP).doubleValue();
		return v;
	}

}
