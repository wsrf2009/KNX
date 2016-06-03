package com.sation.knxcontroller.dataacess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.dataacess.SQLiteDataInitializer;
import com.sation.knxcontroller.dataacess.SQLiteDataInitializingHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Default open database class.
 */
public class DefaultSQLiteOpenHelper implements SQLiteInitializer {
	private SharedPreferences settings;

	private static final String TAG = "DefaultSQLiteOpenHelper";

	private static final String DATABASE_BONE = "database-bone";

	private static final String DATABASE_DEFAULT = "database";

	private static final String DATABASE_EXT = ".db";

	private static DefaultSQLiteOpenHelper instance;

	public static ConcurrentHashMap<String, SQLiteDatabase> dbMap = new ConcurrentHashMap<String, SQLiteDatabase>();

	public static synchronized DefaultSQLiteOpenHelper getInstance() {
		if (instance == null) {

			instance = new DefaultSQLiteOpenHelper();
		}
		return instance;
	}

	public SQLiteDatabase openDatabase(Context context, String databaseName) {
		settings = context.getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		int databaseVersion = settings.getInt("database_version", 0);
		if (databaseVersion != DBConfig.DATABASE_VERSION) {
			dbMap.clear();
			File file = new File(DBConfig.DB_PATH + databaseName + DATABASE_EXT);
			if (file.exists()) {
				file.delete();
			}
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("database_version",
					DBConfig.DATABASE_VERSION);
			editor.commit();
		}

		if (dbMap.containsKey(databaseName)) {
			return dbMap.get(databaseName);
		} else {
			SQLiteDatabase db = null;
			moveDatabase(context, databaseName);
			db = SQLiteDatabase.openDatabase(DBConfig.DB_PATH + databaseName + DATABASE_EXT, null, SQLiteDatabase.OPEN_READWRITE);
			dbMap.put(databaseName, db);
			return db;
		}
	}

	public SQLiteDatabase openDatabase(Context context, String databaseName,
			SQLiteDataInitializer dataInitializer,
			AbstractDBManager<?> dbManager) {
		if (dbMap.containsKey(databaseName)) {
			return dbMap.get(databaseName);
		} else {
			boolean hasMove = moveDatabase(context, databaseName);

			SQLiteDatabase db = SQLiteDatabase.openDatabase(DBConfig.DB_PATH
					+ databaseName + DATABASE_EXT, null,
					SQLiteDatabase.OPEN_READWRITE);

			if (isInitializeData(databaseName) && hasMove) {
				SQLiteDataInitializingHelper.execute(dataInitializer,
						dbManager, db, context);
			}
			dbMap.put(databaseName, db);
			return db;
		}
	}

	public boolean moveDatabase(Context context, String databaseName) {
		try {
			File dbDir = new File(DBConfig.DB_PATH);
			if (!dbDir.exists()) {
				try {
					dbDir.mkdir();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					return false;
				}
			}
			// Read sdcard database
			String database = databaseName + DATABASE_EXT;
			File databaseFile = new File(dbDir, database);
			if (!databaseFile.exists()) {

				databaseFile.createNewFile();
				InputStream in = context.getResources().getAssets()
						.open(getDatabaseDir(databaseName) + "/" + database);
				int size = in.available();
				byte[] buffer = new byte[size];
				in.read(buffer);
				in.close();
				FileOutputStream out = new FileOutputStream(databaseFile);
				out.write(buffer);
				out.close();
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return false;
	}

	private String getDatabaseDir(String databaseName) {
		String databaseDir = DATABASE_DEFAULT;
		if (isInitializeData(databaseName)) {
			databaseDir = DATABASE_BONE;
		}
		return databaseDir;
	}

	public boolean isInitializeData(String databaseName) {
		return STKNXControllerConstant.INITIALIZE_DATABASE_DATA;
	}

	public void afterMove(ConcurrentHashMap<String, SQLiteDatabase> dbMap) {

	}
}
