package com.sation.knxcontroller.third.webcamer.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangchunfeng on 2017/9/28.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_SQL = "CREATE TABLE t_onvif_cameras (id integer PRIMARY KEY AUTOINCREMENT, UUID varchar(32), name varchar(32), username varchar(16), password varchar(16), width integer, height integer, rate integer);";

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name)
    {
        this(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
