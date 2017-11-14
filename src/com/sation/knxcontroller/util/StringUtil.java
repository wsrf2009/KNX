package com.sation.knxcontroller.util;

public class StringUtil {
	private final static String TAG = "StringUtil";
	/**
	 * if str == null or str is "" or "  ", return true.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		if(null == str || str.trim().length() <= 0) {
			return true;
		} else {
			return false;
		}
	}
}
