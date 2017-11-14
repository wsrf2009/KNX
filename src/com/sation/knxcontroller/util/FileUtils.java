package com.sation.knxcontroller.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Base64;

public class FileUtils {
	public static final int IMAGE = 0;
	public static final int VIDEO = 1;
	public static final int XML = 2;

	public enum CopyStatus {
		COPY_SUCCESSFUL,
		SAME_PATH,
		SAME_FILE,

		SOURCE_NO_FOUND,
		SOURCE_NOT_FILE,
		CREATE_DES_DIR_FAILED,
		SOURCE_NOT_DIR,
		DES_DIR_EXIST,
		COPY_FAILED,
	}


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
	
	public static void writeObjectIntoFile(Context mContext, String fileName, Object mObject) throws IOException{
		FileOutputStream fileOutputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);  
        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);  
        oos.writeObject(mObject);//timingTaskList is an Instance of TableData;  
	}
	
	public static Object readObjectFromFile(Context mContext, String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = mContext.openFileInput(fileName);  
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);
        return ois.readObject();
	}
	
	public static boolean fileIsExists(String strFile) {  
        try {  
            File f=new File(strFile);  
            if(!f.exists()) {  
            	return false;  
            }  
        } catch (Exception e) {  
            return false;  
        }  
  
        return true;  
    }
	
	// 读SD中的文件
	public static String readFileSdcardFile(String fileName) throws IOException {
		String res = "";
		try {
			
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 复制单个文件
	 *
	 * @param srcFileName
	 *            待复制的文件名
	 * @param destFileName
	 *            目标文件名
	 * @param overlay
	 *            如果目标文件存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static CopyStatus copyFile(String srcFileName, String destFileName,
								   boolean overlay) {
		if (srcFileName.equals(destFileName)) {
			return CopyStatus.SAME_PATH;
		}

		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		if (srcFile.equals(destFile)) {
//			return CopyStatus.SAME_FILE;
		}

		// 判断源文件是否存在
		if (!srcFile.exists()) {
//			MESSAGE = "源文件：" + srcFileName + "不存在！";
			return CopyStatus.SOURCE_NO_FOUND;
		} else if (!srcFile.isFile()) {
//			MESSAGE = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";
			return CopyStatus.SOURCE_NOT_FILE;
		}

		// 判断目标文件是否存在
		if (destFile.exists()) {
			// 如果目标文件存在并允许覆盖
			if (overlay) {
				// 删除已经存在的目标文件，无论目标文件是目录还是单个文件
				new File(destFileName).delete();
			}
		} else {
			// 如果目标文件所在目录不存在，则创建目录
			if (!destFile.getParentFile().exists()) {
				// 目标文件所在目录不存在
				if (!destFile.getParentFile().mkdirs()) {
					// 复制文件失败：创建目标文件所在目录失败
					return CopyStatus.CREATE_DES_DIR_FAILED;
				}
			}
		}

		// 复制文件
		int byteread = 0; // 读取的字节数
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			return CopyStatus.COPY_SUCCESSFUL;
		} catch (FileNotFoundException e) {
			return CopyStatus.SOURCE_NO_FOUND;
		} catch (IOException e) {
			return CopyStatus.COPY_FAILED;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 复制整个目录的内容
	 *
	 * @param srcDirName
	 *            待复制目录的目录名
	 * @param destDirName
	 *            目标目录名
	 * @param overlay
	 *            如果目标目录存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static CopyStatus copyDirectory(String srcDirName, String destDirName,
										boolean overlay) {
		if (srcDirName.equals(destDirName)) {
			return CopyStatus.SAME_PATH;
		}

		// 判断源目录是否存在
		File srcDir = new File(srcDirName);
		if (!srcDir.exists()) {
//			MESSAGE = "复制目录失败：源目录" + srcDirName + "不存在！";
			return CopyStatus.SOURCE_NO_FOUND;
		} else if (!srcDir.isDirectory()) {
//			MESSAGE = "复制目录失败：" + srcDirName + "不是目录！";
			return CopyStatus.SOURCE_NOT_DIR;
		}

		// 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		File destDir = new File(destDirName);
		// 如果目标文件夹存在
		if (destDir.exists()) {
			// 如果允许覆盖则删除已存在的目标目录
			if (overlay) {
				new File(destDirName).delete();
			} else {
//				MESSAGE = "复制目录失败：目的目录" + destDirName + "已存在！";
				return CopyStatus.DES_DIR_EXIST;
			}
		} else {
			// 创建目的目录
			System.out.println("目的目录不存在，准备创建。。。");
			if (!destDir.mkdirs()) {
				System.out.println("复制目录失败：创建目的目录失败！");
				return CopyStatus.CREATE_DES_DIR_FAILED;
			}
		}

		CopyStatus status = CopyStatus.COPY_SUCCESSFUL;
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 复制文件
			if (files[i].isFile()) {
				status = copyFile(files[i].getAbsolutePath(),
						destDirName + files[i].getName(), overlay);
				if ((CopyStatus.COPY_SUCCESSFUL != status) && (CopyStatus.SAME_PATH != status)
						&& (CopyStatus.SAME_PATH != status)) {
					break;
				}
			} else if (files[i].isDirectory()) {
				status = copyDirectory(files[i].getAbsolutePath(),
						destDirName + files[i].getName(), overlay);
				if ((CopyStatus.COPY_SUCCESSFUL != status) && (CopyStatus.SAME_PATH != status)
						&& (CopyStatus.SAME_PATH != status))
					break;
			}
		}
		if ((CopyStatus.COPY_SUCCESSFUL != status) && (CopyStatus.SAME_PATH != status)
				&& (CopyStatus.SAME_PATH != status)) {
//			MESSAGE = "复制目录" + srcDirName + "至" + destDirName + "失败！";
			return CopyStatus.COPY_FAILED;
		} else {
			return CopyStatus.COPY_SUCCESSFUL;
		}
	}


	public static boolean assetsCopy(Context c) {
		try {
			assetsCopy(c, "wfs", Environment.getExternalStorageDirectory()
					+ "/.wfs"); // assets内.wfs文件找不到==
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void assetsCopy(Context c,  String assetsPath, String dirPath)
			throws IOException {
		AssetManager manager = c.getAssets();
		String[] list = manager.list(assetsPath);
		if (list.length == 0) { // 文件
			InputStream in = manager.open(assetsPath);
			File file = new File(dirPath);
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			/* 复制 */
			byte[] buf = new byte[1024];
			int count;
			while ((count = in.read(buf)) != -1) {
				fout.write(buf, 0, count);
				fout.flush();
			}
			/* 关闭 */
			in.close();
			fout.close();
		} else { // 目录
			for (String path : list) {
				assetsCopy(c, assetsPath + "/" + path, dirPath + "/" + path);
			}
		}
	}
}
