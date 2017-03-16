package com.sation.knxcontroller.util.uikit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * Created by wangchunfeng on 2017/2/20.
 */

public class ApkUtils {

    public static String getPackageNameWithFileName(Context c, String fn) {
        try {
            PackageManager pm = c.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(fn, PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = null;
            if (info != null) {
                appInfo = info.applicationInfo;
                String packageName = appInfo.packageName;

                return packageName;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void doStartApplicationWithPackageName(Context c, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = c.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = c.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            c.startActivity(intent);
        }
    }

    public static void install(Context c, String fn) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fn)),
                    "application/vnd.android.package-archive");
            c.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void uninstal(Context c, String pn) {
        try {
            Uri packageURI = Uri.parse("package:"+pn);
            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
            c.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
