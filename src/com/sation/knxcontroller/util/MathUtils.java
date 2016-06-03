package com.sation.knxcontroller.util;

public class MathUtils {

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

}
