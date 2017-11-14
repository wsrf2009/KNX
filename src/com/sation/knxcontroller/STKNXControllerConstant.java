package com.sation.knxcontroller;

import java.io.File;

import android.os.Environment;

public class STKNXControllerConstant { 
	public static final String STKCPLATFORM = "SABRESD-MX6DQ";
	public static final String slash = "/";
	public static final String XML_TYPE = "xml";
	public static final String dot = ".";

	public static final String PASSWORD_UNZIP = "com.sation.knxcontroller.uieditor";

	public static final String SuffixConfigFile = "knxuie";
	public static final String ConfigFile = "KnxUiProject.knxuie";

	public static final String PrefixResImg = "id_";
	
	public static final String UiMetaFile = "KnxUiMetaData.json";
	public static final String GroupAddFile = "GroupAddress.json";
	public static final String VersionFile = "Version.json";
	public static final String ConfigResDir = "res";
	public static final String ConfigResImgDir = "img";
	public static final String CompanyName = "Sation";
	public static final String ProRootDir = "KNXController";
	public static final String StructFile = "struck.dat";
	public static final String SDCardPath = Environment.getExternalStorageDirectory()+File.separator;
	public static final String ConfigFilePath = SDCardPath+ConfigFile;
	public static final String ProRootPath = SDCardPath+CompanyName+File.separator+ProRootDir+File.separator;
	public static final String StructFilePath = SDCardPath+StructFile;
	public static final String UiMetaFilePath = ProRootPath+UiMetaFile;
	public static final String GroupAddFilePath = ProRootPath+GroupAddFile;
	public static final String VersionFilePath = ProRootPath+VersionFile;
	public static final String ConfigResPath = ProRootPath+ConfigResDir+File.separator;
	public static final String ConfigResImgPath = ConfigResPath+ConfigResImgDir+File.separator;
	
	public static final String FILE_TIMERTASK = "TimerTask.dat";

	public static final int NOTIFY_ID  =  1000;
	
	
	public static final boolean INITIALIZE_DATABASE_DATA = false;
	public final static int NETWORK_NOTIFICATION = 110119;
	 
	public static final String BROADCAST_UPDATE_DEVICE_STATUS = "com.sation.knxcontroller.android.BROADCAST_UPDATE_DEVICE_STATUS";
	public static final String GROUP_ADDRESS_INDEX = "GroupAddressIndex";
	public static final String GROUP_ADDRESS_NEW_VALUE = "GroupAddressNewValue";
	public static final String GROUP_ADDRESS_NEW_VALUE_LENGTH = "GroupAddressNewValueLength";
	
//	public static String FILEPATH = Environment.getExternalStorageDirectory() + "/ZyyKNX/cache";
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String SETTING_FILE = "STKNXController_Config";
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
	
	public static final String REMEMBER_LAST_INTERFACE = "remember_last_interface";
	public static final boolean REMEMBER_LAST_INTERFACE_VALUE = true;
	public static final String LAST_AREA_INDEX = "last_area_index";
	public static final String LAST_ROOM_INDEX = "last_room_index";
	public static final String LAST_PAGE_INDEX = "last_page_index";
	public static final String LAST_TIMER_ID = "last_timer_id";
	
	public static final String KNX_PHYSICAL_ADDRESS_FIRST = "KNX_physical_address_first";
	public static final String KNX_PHYSICAL_ADDRESS_SECOND = "KNX_physical_address_Second";
	public static final String KNX_PHYSICAL_ADDRESS_THIRD = "KNX_physical_address_Three";
	
	public static final int PHYSICAL_ADDRESS_VALUE_FIRST = 15;
	public static final int PHYSICAL_ADDRESS_VALUE_SECOND = 15;
	public static final int PHYSICAL_ADDRESS_VALUE_THIRD = 210;
	
	public static final String KNX_SETTING_SCRRENOFF_TIMESPAN = "KNX_Setting_ScreenOff_Timespan";
	
	public static final String APP_APPEARANCE_LANGUAGE = "app_appearance_language";
	
	/* 系统定时启动：默认不重启、默认重启时间 03:00 */
	public static final String SYSTEM_REBOOT_FLAG = "system_reboot_falg";
	public static final boolean SYSTEM_REBOOT_FLAG_VALUE = false;
	public static final String SYSTEM_REBOOT_HOUR = "system_reboot_hour";
	public static final int SYSTEM_REBOOT_HOUR_VALUE = 03;
	public static final String SYSTEM_REBOOT_MINUTE = "system_reboot_minute";
	public static final int SYSTEM_REBOOT_MINUTE_VALUE = 00;
	public static final String SYSTEM_REBOOT_EXECUTE_FLAG = "system_reboot_execute_flag";
	
	/* 系统设置界面密码 */
	public static final String SYSTEM_SETTING_PASSWORD = "system_setting_password";
	public static final String SYSTEM_SETTING_PASSWORD_VALUE = "654321";
	
	/* 主界面系统时间：默认不显示 */
	public static final String DISPLAY_SYSTEM_TIME_FLAG = "display_system_time_flag";
	public static final boolean DISPLAY_SYSTEM_TIME_FLAG_VALUE = false;
	public static final String BROADCASTRECEIVER_REFRESH_SYSTEM_TIME = "com.sation.knxcontroller.RoomTilesListActivity.RefreshSystemTimeReceiver";
	public static final String SYSTEMTIME = "SystemTime";
	
	public final static String MENU_DRAWER_TRIGGER_KEY = "Menu_Drawer_Trigger_key";

	public final static String REMOTE_PARAM_KEY = "Remote_Param_key";
	
	public static final String FILE_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	
	
	public static final int CONTROL_DEFAULT_WIDTH = 236;
	
	public static final String BROADCAST_REFRESH_TIMING_TASK_LIST = "com.sation.knxcontroller.services.refreshTimingTaskList";

	public static final String CONTROL_ID = "ControlId";
	public static final String TIMERTASKBUTTONOBJECT = "TimerTaskButtonObject";
}
