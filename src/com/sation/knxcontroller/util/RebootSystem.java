package com.sation.knxcontroller.util;

import java.io.DataOutputStream;
import java.io.OutputStream;

import android.R.string;

/**
 * 2016-07-25
 * @author Administrator
 *
 */
public class RebootSystem {
	
	/**
	 *
	 */
	public static void Exec(/*String ip,String netmask,String dns,String mac,String gtw*/){
		
		try {
			Runtime runtime = Runtime.getRuntime();
            Process localProcess = runtime.exec("/system/xbin/su");
            String cmd0 = "reboot\n";
            String [] cmdStrings = new String[]{cmd0};
            for (int i = 0; i < cmdStrings.length; i++) {
                localProcess.getOutputStream().write(cmdStrings[i].getBytes());
            }
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		
	}
}
