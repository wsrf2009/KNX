package com.sation.knxcontroller.dataacess;

import com.sation.knxcontroller.dataacess.AbstractDBManager;
import com.sation.knxcontroller.dataacess.DefaultSQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Abstract database manager.
 */
public abstract class AbstractDBManager<T> {
	public SQLiteDatabase db;

	public Context context;

	public AbstractDBManager() {

	}

	public AbstractDBManager(Context context, String databaseName) {
		this.context = context;
		this.db = DefaultSQLiteOpenHelper.getInstance().openDatabase(context,
				databaseName);
	}

	public AbstractDBManager(Context context, String databaseName, SQLiteDataInitializer dataInitializer, AbstractDBManager<T> dbManager) {
		this.context = context;
		this.db = DefaultSQLiteOpenHelper.getInstance().openDatabase(context,
				databaseName, dataInitializer, dbManager);
	}

	// public abstract long save(T object);

	public void closeDB() {
		if (db != null || db.isOpen()) {
			db.close();
		}
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
