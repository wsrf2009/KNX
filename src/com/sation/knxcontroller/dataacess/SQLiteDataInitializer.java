package com.sation.knxcontroller.dataacess;

import com.sation.knxcontroller.dataacess.AbstractDBManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface for support capacity for initial data for database.
 */
public interface SQLiteDataInitializer {
	void initData(AbstractDBManager<?> manager, SQLiteDatabase db,
			Context context);
}
