package com.sation.knxcontroller.util;

public class StringUtil {
	/**
	 * if str == null or str is "" or "  ", return true.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		} else if ("".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
}
