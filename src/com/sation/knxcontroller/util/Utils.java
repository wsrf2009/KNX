package com.sation.knxcontroller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale; 
 
import android.content.Context; 
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo; 
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Utils {
  
	public static String formatDate(String pattern, Date date) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, new Locale("zh", "ZH"));

		return format.format(date);
	}

	public static Date parseDate(String pattern, String date) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	} 

	/**
	 * if net work is available, return true.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetState(Context context) {
		ConnectivityManager cgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cgr != null) {
			NetworkInfo info = cgr.getActiveNetworkInfo();
			if (info != null && info.isConnected() && (info.getState() == NetworkInfo.State.CONNECTED)) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	  * 网络已经连接，然后去判断是wifi连接还是GPRS连接 
	  * 设置一些自己的逻辑调用 
	  *  返回值： 0:其他连接，1：GPRS，2：wifi，3：Net
	  */  
	public static int isNetworkAvailable(Context context)
	 {         
		 ConnectivityManager cgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo networkInfo = cgr.getActiveNetworkInfo();  
		 if (networkInfo == null)
			 return -1;
       if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
           return 1;  
       } 
       if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
           return 2;  
       }  
       if (networkInfo.getType() == ConnectivityManager.DEFAULT_NETWORK_PREFERENCE) {  
           return 3;  
       }  
	     return 0;   //      
	 }  

	/**
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	public static String convertStreamToString(Context context, String path) throws IOException {
		InputStream is = context.getResources().getAssets().open(path);
		Writer writer = new StringWriter();

		char[] buffer = new char[2048];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		String text = writer.toString();
		return text;
	} 
	
	//dip转像素
	public static int dipToPixels(Context context,int dip) {
		final float SCALE = context.getResources().getDisplayMetrics().density;
		float valueDips =  dip;
		int valuePixels = (int)(valueDips * SCALE + 0.5f); 
		return valuePixels;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**获取屏幕分辨率宽*/
	public static int getScreenWidth(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		//((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
		Display display = wm.getDefaultDisplay();  
		display.getMetrics(dm);

		return dm.widthPixels;
	}

}
