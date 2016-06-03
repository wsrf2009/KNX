package com.sation.knxcontroller.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapSoftRefCache implements ImageCache {
	// private static final String TAG = "BitmapSoftRefCache";

	private String path;

	public BitmapSoftRefCache(String defautlPath) {
		path = defautlPath;
	}

	/**
	 * 从软引用集合中得到Bitmap对象
	 */
	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		String fileName = getFileNameFromUrl(url);
		if (fileName != null) {
			String filePath = path + fileName;
			File file = new File(filePath);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(filePath);
				if (bitmap == null) {
					Log.w(url + "对象已经被GC回收");
				} else {
					Log.i("命中" + url);
				}
			}
		}
		return bitmap;
	}

	/**
	 * 从软引用集合中添加bitmap对象
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(bitmap);
		FileOutputStream b = null;
		String fileName = getFileNameFromUrl(url);
		final String filePath = path + fileName;
		try {
			b = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
				Log.w("图片本地缓存成功!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getFileNameFromUrl(String url) {
		int index = url.lastIndexOf("/") + 1;
		String fileName = "".equals(url) ? url : url.substring(index);

		return fileName;
	}
}
