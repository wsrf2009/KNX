package com.sation.knxcontroller.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class ImageUtils {
	private static final String TAG = "ImageUtils";
	
	public static Bitmap getDiskBitmap(String path) {
		Bitmap bitmap = null;
		
		if(StringUtil.isEmpty(path)) {
			return bitmap;
		}
		
		if(null == STKNXControllerApp.getInstance().getImageMap()) {
			STKNXControllerApp.getInstance().setImageMap(new HashMap<String, Bitmap>());
		}
		
		if(STKNXControllerApp.getInstance().getImageMap().containsKey(path)) {
			bitmap = STKNXControllerApp.getInstance().getImageMap().get(path);
			if(!bitmap.isRecycled()) {
				return bitmap;
			} else {
				Log.i(TAG, path +" has been recycled!!!");
			}
		}
		
		try {
			Log.i(TAG, "loading file:"+path);
			File file = new File(path);
			if(file.exists()) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				// 设置为ture只获取图片大小
				//opts.inJustDecodeBounds = true;
				opts.inPurgeable = true;  
				opts.inPreferredConfig = Bitmap.Config.RGB_565; 
//				opts.inSampleSize = 2;
				bitmap = BitmapFactory.decodeFile(path, opts);

				STKNXControllerApp.getInstance().getImageMap().put(path, bitmap);
			} else { 
				Log.e(TAG, file+" is not exist!");
			}
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
		} 

		return bitmap;
	}
	
	public static Bitmap getDiskBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File file = new File(path);
		if(file.exists()) {
			  BitmapFactory.decodeFile(path, opts);
		} 
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

	/**
	 * load Icon from local.
	 * 
	 * @param ctx
	 * @param imageName
	 * @return
	 */
	public static Drawable getDrawable(Context ctx, String imageName) {
		Drawable defaultIcon = ctx.getResources().getDrawable(R.drawable.launcher);
		try {
			InputStream inputStream = ctx.getAssets().open(imageName);
			return Drawable.createFromStream(inputStream, null);
		} catch (IOException e) {
			Log.e(TAG, "No image find: " + e);
			return defaultIcon;
		}

	}

	public static Drawable getDrawable2(Context ctx, String imageName) {
		Drawable defaultIcon = ctx.getResources().getDrawable(R.drawable.launcher);
		try {
			InputStream is = FileUtils.getInputStream(ctx, imageName, FileUtils.IMAGE);
			return Drawable.createFromStream(is, null);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "No image find: " + e);
			return defaultIcon;
		}
	}

	/**
	 * 从软引用集合中得到Bitmap对象
	 */
	public static Bitmap getBitmap(Context ctx, String url) {
		Bitmap bitmap = null;
		String fileName = getFileNameFromUrl(url);
		if (fileName != null) {
			String filePath = ctx.getCacheDir() + fileName;
			File file = new File(filePath);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(filePath);
				if (bitmap == null) {
					Log.w(TAG, url + "对象已经被GC回收");
				} else {

				}
			}
		}
		return bitmap;
	}

	public static String getFileNameFromUrl(String url) {
		int index = url.lastIndexOf("/") + 1;
		String fileName = "".equals(url) ? url : url.substring(index);

		return fileName;
	}
}
