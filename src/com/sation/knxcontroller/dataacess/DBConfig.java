package com.sation.knxcontroller.dataacess;

import android.os.Environment;

public class DBConfig {
	/**
	 * 数据库版本
	 */
	public static final int DATABASE_VERSION = 4;
	/** 用户数据库 */
	public static final String DATABASE_NAME = "ZyyEHome";

	public static final String TABLE_DeviceDirectives = "DeviceDirectives";
	public static final String TABLE_GPSDeviceParams = "GPSDeviceParams";

	/** 目录 */
	public final static String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()	+ "/com.ehome.android/databases/";
}
