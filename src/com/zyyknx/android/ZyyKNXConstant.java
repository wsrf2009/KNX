package com.zyyknx.android;

import java.io.File;

import android.os.Environment;

public class ZyyKNXConstant { 
	public static final String slash = "/";
	public static final String XML_TYPE = "xml";
	public static final String dot = ".";
	
	public static final int NOTIFY_ID  =  1000;
	
	
	public static final boolean INITIALIZE_DATABASE_DATA = false;
	public final static int NETWORK_NOTIFICATION = 110119;
	 
	public static final String BROADCAST_UPDATE_DEVICE_STATUS = "com.zyyknx.android.BROADCAST_UPDATE_DEVICE_STATUS";
	   
	
	public static String FILEPATH = Environment.getExternalStorageDirectory() + "/ZyyKNX/cache";
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String SETTING_FILE = "ZyyKNX_Config";
	public static final String LOCALVERSION = "localVersion";
	
	public static final String KNX_GETEWAY_IP = "KNX_GetWay_IP"; 
	public static final String KNX_GETEWAY_Port = "KNX_GetWay_Port";
	public static final String KNX_SOCKET_MODE = "KNX_SOCKET_Mode";
	public static final String KNX_REFRESH_STATUS_TIMESPAN = "KNX_Refresh_Status_Timespan";
	public static final String KNX_SETTING_SCRRENOFF_TIMESPAN = "KNX_Setting_ScreenOff_Timespan";
	
	
	public static final String KNX_PHYSICAL_ADDRESS_FIRST = "KNX_physical_address_first"; 
	public static final String KNX_PHYSICAL_ADDRESS_SECOND = "KNX_physical_address_Second"; 
	public static final String KNX_PHYSICAL_ADDRESS_THREE = "KNX_physical_address_Three"; 
	
	
	
	public final static String MENU_DRAWER_TRIGGER_KEY = "Menu_Drawer_Trigger_key";

	public final static String REMOTE_PARAM_KEY = "Remote_Param_key";
	
	public static final String FILE_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	
	
	public static final int CONTROL_DEFAULT_WIDTH = 236;

}
