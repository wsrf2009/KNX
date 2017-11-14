package com.sation.knxcontroller.util;

import java.io.File;

import com.sation.knxcontroller.STKNXControllerConstant;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ZipUtil
{
	public final static int NO_ERROR = 11000;
	public final static int ERROR_GENERAL = 11001;
	public final static int ERROR_INVALID_FILE = 11002;

	public final static int UNZIP_START = 10001;
	public final static int UNZIP_HANDLING = 10002;
	public final static int UNZIP_COMPLETED = 10003;

	public final static String PERCENT = "PERCENT";
	public final static String ERROR_MESSAGE = "ERROR_MESSAGE";

	public static int unZipFileWithProgress(final File zipFile, final String filePath,
											boolean inThread, final Handler handler,
											final boolean isDeleteZip) throws ZipException
	{
		ZipFile zFile = new ZipFile(zipFile);
		zFile.setFileNameCharset("GBK");

		if (!zFile.isValidZipFile()) { // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
//			throw new ZipException("压缩文件不合法,可能被损坏.");

			return ERROR_INVALID_FILE;
		}
		File destDir = new File(filePath); // 解压目录
		if (destDir.isDirectory() && !destDir.exists()) {
			destDir.mkdir();
		}
		if (zFile.isEncrypted())
		{
			zFile.setPassword(STKNXControllerConstant.PASSWORD_UNZIP); // 设置密码
		}

		if (inThread) {
			final ProgressMonitor progressMonitor = zFile.getProgressMonitor();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle bundle = null;
					Message msg = null;
					try {
						int precentDone = 0;
						if (handler == null) {
							return;
						}
						handler.sendEmptyMessage(UNZIP_START);
						while (true) {
							// 启动线程，每隔一秒向外handler一条消息
							Thread.sleep(1);
							precentDone = progressMonitor.getPercentDone();
							bundle = new Bundle();
							bundle.putInt(PERCENT, precentDone);
							msg = new Message();
							msg.what = UNZIP_HANDLING;
							msg.setData(bundle);
							handler.sendMessage(msg);
							if (precentDone >= 100) {
								break;
							}
						}
						handler.sendEmptyMessage(UNZIP_COMPLETED);
					} catch (InterruptedException e) {
						bundle = new Bundle();
						bundle.putString(ERROR_MESSAGE, e.getMessage());
						msg = new Message();
						msg.what = ERROR_GENERAL;
						msg.setData(bundle);
						handler.sendMessage(msg);
						e.printStackTrace();
					} finally {
						if (isDeleteZip) {
							zipFile.delete();
						}
					}
				}
			});

			thread.start();
			zFile.setRunInThread(true);
		}

		zFile.extractAll(filePath); // 解压到此文件夹中

		return NO_ERROR;
	}
}
