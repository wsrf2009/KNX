package com.zyyknx.android.dataacess;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zyyknx.android.ZyyKNXConstant;;

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
				.append(ZyyKNXConstant.slash).append(databaseName)
				.append(ZyyKNXConstant.slash).append(tableName)
				.append(ZyyKNXConstant.dot).append(ZyyKNXConstant.XML_TYPE)
				.toString();
		try {
			in = context.getResources().getAssets().open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
}
