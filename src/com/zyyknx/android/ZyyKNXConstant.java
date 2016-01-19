package com.zyyknx.android;

import java.io.File;

import android.os.Environment;

public class ZyyKNXConstant { 
	public static final String slash = "/";
	public static final String XML_TYPE = "xml";
	public static final String dot = ".";
	
	public static final String FILE_TIMERTASK = "TimerTask.dat";
	
	public static final int NOTIFY_ID  =  1000;
	
	
	public static final boolean INITIALIZE_DATABASE_DATA = false;
	public final static int NETWORK_NOTIFICATION = 110119;
	 
	public static final String BROADCAST_UPDATE_DEVICE_STATUS = "com.zyyknx.android.BROADCAST_UPDATE_DEVICE_STATUS";
	public static final String GROUP_ADDRESS_INDEX = "GroupAddressIndex";
	public static final String GROUP_ADDRESS_NEW_VALUE = "GroupAddressNewValue";
	public static final String GROUP_ADDRESS_NEW_VALUE_LENGTH = "GroupAddressNewValueLength";
	
	public static String FILEPATH = Environment.getExternalStorageDirectory() + "/ZyyKNX/cache";
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String SETTING_FILE = "ZyyKNX_Config";
	public static final String LOCALVERSION = "localVersion";
	
	/** KNX网关IP */
	public static final String KNX_GATEWAY_IP = "KNX_Gateway_IP"; 
	/** 缺省网关: 224.0.23.12 */
	public static final String KNX_GATEWAY_DEFAULT = "224.0.23.12";
	
	/** KNX网关端口 */
	public static final String KNX_GATEWAY_PORT = "KNX_Gateway_Port";
	/** 缺省网关端口：3617 */
	public static final int KNX_GATEWAY_PORT_DEFAULT = 3671;
	
	/** UDP工作方式 */
	public static final String KNX_UDP_WORK_WAY = "KNX_UDP_Work_Way";
	/** UDP工作方式---组播 */
	public static final int KNX_UDP_WORK_WAY_GROUP_BROADCAST = 2;
	/** UDP工作方式---点对点 */
	public static final int KNX_UDP_WORK_WAY_PEER_TO_PEER = 3;
	/** UDP默认的工作方式：组播 */
	public static final int KNX_UDP_WORK_WAY_DEFAULT = KNX_UDP_WORK_WAY_GROUP_BROADCAST;
	
	public static final String KNX_REFRESH_STATUS_TIMESPAN = "KNX_Refresh_Status_Timespan";
	
	
	public static final String KNX_PHYSICAL_ADDRESS_FIRST = "KNX_physical_address_first"; 
	public static final String KNX_PHYSICAL_ADDRESS_SECOND = "KNX_physical_address_Second"; 
	public static final String KNX_PHYSICAL_ADDRESS_THREE = "KNX_physical_address_Three"; 
	
	
	
	public final static String MENU_DRAWER_TRIGGER_KEY = "Menu_Drawer_Trigger_key";

	public final static String REMOTE_PARAM_KEY = "Remote_Param_key";
	
	public static final String FILE_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	
	
	public static final int CONTROL_DEFAULT_WIDTH = 236;
	
	public static final String BROADCAST_REFRESH_TIMING_TASK_LIST = "com.zyyknx.android.services.refreshTimingTaskList";
	
	public static final String SEND_COMMAND = "SendCommand";
	public static final String ACTIVITY_JUMP = "ActivityJump";
	public static final String DEBUG = "debug";
	public static final String CALLBACK = "CallBack";
	
	public static final String CONTROL_ID = "ControlId";
	public static final String TIMERTASKBUTTONOBJECT = "TimerTaskButtonObject";
}
