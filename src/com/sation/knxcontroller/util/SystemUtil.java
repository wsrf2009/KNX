package com.sation.knxcontroller.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;

public class SystemUtil {

	/**
	 * 获取版本号(内部识别号)  
	 */
    public static int getVersionCode(Context context)
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
    
    /**
     * 获取版本号  
     */
    public static String getVersion(Context context)
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            return null;  
        }  
    } 

    public static String getPackageName(Context context) {
    	return context.getPackageName();
    }
    
    /**
     * 获取扩展SD卡存储目录
     * 
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     * 
     * @return
     */
    public static String getExternalSdCardPath() {
 
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sdCardFile.getAbsolutePath();
        }
 
//        String path = null;
// 
//        File sdCardFile = null;
 
//        ArrayList<String> devMountList = getDevMountList();
 
//        for (String devMount : devMountList) {
//            File file = new File(devMount);
// 
//            if (file.isDirectory() && file.canWrite()) {
//                path = file.getAbsolutePath();
// 
//                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
//                File testWritable = new File(path, "test_" + timeStamp);
// 
//                if (testWritable.mkdirs()) {
//                    testWritable.delete();
//                } else {
//                    path = null;
//                }
//            }
//        }
 
//        if (path != null) {
//            sdCardFile = new File(path);
//            return sdCardFile.getAbsolutePath();
//        }
 
        return null;
    }
    
    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     * 
     * @return
     */
//    private static ArrayList<String> getDevMountList() {
//        String[] toSearch = FileUtils.readFile("/etc/vold.fstab").split(" ");
//        ArrayList<String> out = new ArrayList<String>();
//        for (int i = 0; i < toSearch.length; i++) {
//            if (toSearch[i].contains("dev_mount")) {
//                if (new File(toSearch[i + 2]).exists()) {
//                    out.add(toSearch[i + 2]);
//                }
//            }
//        }
//        return out;
//    }
    
    /**
     * 获取所有存储卡挂载路径
     * @return
     */
    public static List<String> getMountPathList() {  
        List<String> pathList = new ArrayList<String>();  
        final String cmd = "cat /proc/mounts";  
        Runtime run = Runtime.getRuntime();//取得当前JVM的运行时环境 
        try {  
            Process p = run.exec(cmd);//执行命令  
            BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());  
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));  
     
            String line;  
            while ((line = bufferedReader.readLine()) != null) {  
                // 获得命令执行后在控制台的输出信息  
                Log.i("debug", line);  
                //输出信息内容：  /data/media /storage/emulated/0 sdcardfs rw,nosuid,nodev,relatime,uid=1023,gid=1023 0 0
                String[] temp = TextUtils.split(line, " ");  
                //分析内容可看出第二个空格后面是路径  
                String result = temp[1];  
                File file = new File(result);  
                //类型为目录、可读、可写，就算是一条挂载路径
                if (file.isDirectory() && file.canRead() && file.canWrite() && file.exists() && !file.isHidden()) {  
                    Log.d("debug", "add --> "+file.getAbsolutePath());  
                    pathList.add(result);  
                }  
     
                // 检查命令是否执行失败
                if (p.waitFor() != 0 && p.exitValue() == 1) {  
                    // p.exitValue()==0表示正常结束，1：非正常结束  
                    Log.e("debug", "命令执行失败!");  
                }  
            }  
            bufferedReader.close();  
            inputStream.close();  
        } catch (Exception e) {  
            Log.e("debug", e.toString());  
            //命令执行异常，就添加默认的路径
            pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());  
        }
        return pathList;  
    }
    
	/** 获取状态栏的高度 */
	public static int getBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    } 
	
	 /**
     * 判断某个界面是否在前台
     * 
     * @param context
     * @param className
     *            某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
