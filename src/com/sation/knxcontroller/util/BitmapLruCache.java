/*
 * Copyright (C) 2013 lytsing.org
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sation.knxcontroller.util;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
	// private static final String TAG = "BitmapLruCache";

	private BitmapSoftRefCache softRefCache;
	private String path;
	private boolean isDisplayRound;

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		return cacheSize;
	}

	public BitmapLruCache(Context context) {
		this(context, getDefaultLruCacheSize());
		path = context.getCacheDir().getPath();
		softRefCache = new BitmapSoftRefCache(path);
	}

	public BitmapLruCache(Context context, String defautlPath) {
		this(context, getDefaultLruCacheSize());
		softRefCache = new BitmapSoftRefCache(path);
		path = defautlPath;
	}
	
	public BitmapLruCache(Context context, String defautlPath, boolean isRound) {
		this(context, getDefaultLruCacheSize());
		softRefCache = new BitmapSoftRefCache(path);
		path = defautlPath;
		this.isDisplayRound = isRound;
	}

	public BitmapLruCache(Context context, int sizeInKiloBytes) {
		super(sizeInKiloBytes);
		path = context.getCacheDir().getPath();
		softRefCache = new BitmapSoftRefCache(path);
	}

	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
		if (evicted) {
			Log.i("空间已满，缓存图片被挤出:" + key);
			// 将被挤出的bitmap对象，添加至软引用BitmapSoftRefCache
			softRefCache.putBitmap(key, oldValue);
		}
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = softRefCache.getBitmap(url);
		if (bitmap == null) {
			bitmap = get(url);
			Log.i("LruCache从url提取：" + url);
		} else {
			Log.i("LruCache命中：" + url);
		}
		/*
		 * Bitmap bitmap = get(url); // 如果bitmap为null，尝试从软引用缓存中查找 if (bitmap ==
		 * null) { bitmap = softRefCache.getBitmap(url); } else {
		 * Log.i("LruCache命中：" + url); }
		 */
		if(isDisplayRound && bitmap != null) {
			bitmap = toRoundBitmap(bitmap);
		} 
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
		softRefCache.putBitmap(url, bitmap);
	}
	
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

}
