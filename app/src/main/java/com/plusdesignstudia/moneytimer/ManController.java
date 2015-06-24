package com.plusdesignstudia.moneytimer;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.plusdesignstudia.moneytimer.DBContract.Names;
import java.sql.Date;


public class ManController {

    private static final boolean LOGV = true;
    private static int maxRowsInNames = -1;
    private static final String TAG = ManController.class.getSimpleName();

    private ManController() {
    }

    /**
     * Функция возвращает количесво заработанных денег и затраченное время за сегодня
     *
     * @param context
     * @return
     */
    public static MoneyAndTime getTodaySessions(Context context) {

        MoneyAndTime list = new MoneyAndTime(0,0);
        try {
            DataBaseOpenHelper dbhelper = new DataBaseOpenHelper(context);
            SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
            String[] columnsToTake = {Names.NamesColumns.HOUR_RATE, Names.NamesColumns.TIME_MS};
            String query = "select hour_rate, time_ms from sessions WHERE date = date()";
            Cursor cursor = sqliteDB.rawQuery(query, null);
            //Cursor cursor = sqliteDB.query(Names.TABLE_NAME, columnsToTake, "date = datetime()", null, null, null,null);
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                float hourRate = cursor.getFloat(cursor.getColumnIndexOrThrow(Names.NamesColumns.HOUR_RATE));
                long time      = cursor.getLong(cursor.getColumnIndexOrThrow(Names.NamesColumns.TIME_MS));
                list.money    += hourRate*time/3600;
                list.time     += time;
            }
            cursor.close();
            dbhelper.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get today's money and time.", e);
        }
        return list;
    }

    /**
     * Функция возвращает количесво заработанных денег и затраченное время за указанную дату
     *
     * @param context
     * @param date
     * @return
     */
    public static MoneyAndTime getSessions(Context context, Date date) {

        MoneyAndTime list = new MoneyAndTime(0,0);
        try {
            DataBaseOpenHelper dbhelper = new DataBaseOpenHelper(context);
            SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
            String[] columnsToTake = {Names.NamesColumns.HOUR_RATE, Names.NamesColumns.TIME_MS};
            Cursor cursor = sqliteDB.query(Names.TABLE_NAME, columnsToTake, "date = "+date, null, null, null,null);
            if (cursor.moveToFirst()) {
                list = new MoneyAndTime();
            }
            while (cursor.moveToNext()) {
                float hourRate = cursor.getFloat(cursor.getColumnIndexOrThrow(Names.NamesColumns.HOUR_RATE));
                long time      = cursor.getLong(cursor.getColumnIndexOrThrow(Names.NamesColumns.TIME_MS));
                list.money    += hourRate*time/3600;
                list.time     += time;
            }
            cursor.close();
            dbhelper.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get today's money and time.", e);
        }
        return list;
    }

    /**
     * Эта функция записывает данные сессии в базу данных
     *
     * @param context
     * @param hourRate
     * @param time
     */
    public static void writeSession(Context context, float hourRate, long time) {
        try {
            //создали нашу базу и открыли для записи
            DataBaseOpenHelper dbhelper = new DataBaseOpenHelper(context);
            SQLiteDatabase sqliteDB = dbhelper.getWritableDatabase();
            String quer = String.format("INSERT INTO %s (%s, %s) VALUES ('%f', %d);",
                        // таблица
                        Names.TABLE_NAME,
                        // колонки

                        Names.NamesColumns.HOUR_RATE,
                        Names.NamesColumns.TIME_MS,
                        // поля
                   hourRate, time);
            //закрыли всю базу
            sqliteDB.execSQL(quer);
            sqliteDB.close();
            dbhelper.close();
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed open database. ", e);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to insert session. ", e);
        }
    }


    public static void clearTodaysSessions(Context context) {
        try {
            //создали нашу базу и открыли для записи
            DataBaseOpenHelper dbhelper = new DataBaseOpenHelper(context);
            SQLiteDatabase sqliteDB = dbhelper.getWritableDatabase();
            String quer = "DELETE FROM " + Names.TABLE_NAME + " WHERE date = 'date()'";
            Log.v("delete","delete="+sqliteDB.delete(Names.TABLE_NAME, "date = date('now')", null));
            Log.v("delete", "2delete=" + sqliteDB.rawQuery(quer, null));
            sqliteDB.close();
            dbhelper.close();
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed open database. ", e);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to clear sessions. ", e);
        }
    }

    public static void clearStatistic(Context context) {
        try {
            //создали нашу базу и открыли для записи
            DataBaseOpenHelper dbhelper = new DataBaseOpenHelper(context);
            SQLiteDatabase sqliteDB = dbhelper.getWritableDatabase();
            String quer = "DELETE FROM " + Names.TABLE_NAME;
            sqliteDB.execSQL(quer);
            quer = "VACUUM;";
            sqliteDB.execSQL(quer);
            dbhelper.close();
            dbhelper.dropTables(sqliteDB);
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed open database. ", e);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to truncate table. ", e);
        }
    }

}