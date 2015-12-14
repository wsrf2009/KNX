package com.zyyknx.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream; 
import org.apache.http.util.EncodingUtils; 

import android.content.Context;
import android.os.Environment;
import android.util.Base64; 

public class FileUtils {

	public static final int IMAGE = 0;
	public static final int VIDEO = 1;
	public static final int XML = 2;

	private static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator; 
	private static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Video/"; 

	
	public static String getSDFileContent(String path) {
		String returnValue = "";
		try {
			File file = new File(SD_PATH + path);
			if(file.exists()) {   
				/* FileInputSteam 输入流的对象， */
				FileInputStream fis = new FileInputStream(file); 
				/* 准备一个字节数组用户装即将读取的数据 */
				byte[] buffer = new byte[fis.available()]; 
				/* 开始进行文件的读取 */
				fis.read(buffer); 
				/* 关闭流  */
				fis.close(); 
				/* 将字节数组转换成字符创， 并转换编码的格式 */
				returnValue = EncodingUtils.getString(buffer, "UTF-8");
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return returnValue;
	}
	
	public static String buildFileName(String url) {
		if (url == null) {
			return null;
		} else {
			int index = url.lastIndexOf(".");
			if (index > 0) {
				return Base64.encodeToString(url.getBytes(), Base64.NO_WRAP)
						+ url.substring(index);
			} else {
				return Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
			}
		}

	} 

	/**
	 * call it to get stream when we want to read data from file.
	 * 
	 * @param ctx
	 * @param name
	 * @param fileType
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getInputStream(Context ctx, String name,
			int fileType) throws FileNotFoundException {
		InputStream is = null;
		switch (fileType) {
		case IMAGE:
		case XML:
			is = ctx.openFileInput(name);
			break;
		case VIDEO:
			is = new FileInputStream(new File(getVideoPath(), name));
			break;
		default:
			break;
		}
		return is;
	}

	/**
	 * call it when we want to write data to file.
	 * 
	 * @param ctx
	 * @param name
	 * @param fileType
	 * @return
	 * @throws FileNotFoundException
	 */
	public static OutputStream getOutputStream(Context ctx, String name,
			int fileType) throws FileNotFoundException {
		OutputStream os = null;
		switch (fileType) {
		case IMAGE:
		case XML:
			os = ctx.openFileOutput(name, Context.MODE_PRIVATE);
			break;
		case VIDEO:
			File file = new File(getVideoPath(), name);
			os = new FileOutputStream(file);
			break;
		default:
			break;
		}
		return os;
	}

	private static String getVideoPath() {
		File pathDirFile = new File(VIDEO_PATH);
		if (!pathDirFile.exists()) {
			pathDirFile.mkdir();
		}
		return VIDEO_PATH;
	}
}
