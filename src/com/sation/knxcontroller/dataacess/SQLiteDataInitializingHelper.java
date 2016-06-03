package com.sation.knxcontroller.dataacess;

import java.io.IOException;
import java.io.InputStream;

import com.sation.knxcontroller.STKNXControllerConstant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;;

/**
 * The executor for initializing data.
 */
public class SQLiteDataInitializingHelper {
	private static String INIT_DATA_DIRECTORY = "initdata";

	public static void execute(SQLiteDataInitializer dataInitializer,
			AbstractDBManager<?> dbManager, SQLiteDatabase db, Context context) {
		dataInitializer.initData(dbManager, db, context);
	}

	public static InputStream getDataStream(Context context, String databaseName, String tableName) {
		InputStream in = null;
		StringBuffer sf = new StringBuffer();
		String path = sf.append(INIT_DATA_DIRECTORY)
				.append(STKNXControllerConstant.slash).append(databaseName)
				.append(STKNXControllerConstant.slash).append(tableName)
				.append(STKNXControllerConstant.dot).append(STKNXControllerConstant.XML_TYPE)
				.toString();
		try {
			in = context.getResources().getAssets().open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
}
