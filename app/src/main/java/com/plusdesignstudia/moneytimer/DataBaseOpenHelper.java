package com.plusdesignstudia.moneytimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
/** Класс создающий, удаляющий и редактирующий базу */
public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moneytimer.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DEBUG_TAG = DataBaseOpenHelper.class.getSimpleName();
    private static final boolean LOGV = true;

    public DataBaseOpenHelper(Context context) {
       super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Удаление всех таблиц из базы
     *
     * @param db
     *            - object of SQLiteDatabase */
    public void dropTables(SQLiteDatabase db) {

        if (LOGV) {
            Log.d(DEBUG_TAG, "onDropTables called");
        }
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Names.TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (LOGV) {
            Log.v(DEBUG_TAG, "onCreate()");
        }
        db.execSQL("CREATE TABLE " + DBContract.Names.TABLE_NAME + " ("
                + BaseColumns._ID          + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                + "date"  + " date default CURRENT_DATE, "
                + DBContract.Names.NamesColumns.HOUR_RATE + " REAL NOT NULL, "
                + DBContract.Names.NamesColumns.TIME_MS    + " INTEGER NOT NULL );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(DEBUG_TAG, "onUpgrade called");
    }
}