package com.zyyknx.android.dataacess;

import com.zyyknx.android.dataacess.AbstractDBManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface for support capacity for initial data for database.
 */
public interface SQLiteDataInitializer {
	void initData(AbstractDBManager<?> manager, SQLiteDatabase db,
			Context context);
}
