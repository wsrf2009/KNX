package com.sation.knxcontroller.dataacess;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface for initial database.
 */
public interface SQLiteInitializer {
	boolean moveDatabase(Context context, String databaseName);

	void afterMove(ConcurrentHashMap<String, SQLiteDatabase> dbMap);
}
