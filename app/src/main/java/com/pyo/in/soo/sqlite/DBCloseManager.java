package com.pyo.in.soo.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by samsung on 2016-01-24.
 */
public class DBCloseManager {
    public static void close(SQLiteDatabase db, Cursor cursor) {
        if(cursor != null){
            cursor.close();
        }
        if( db != null){
            db.close();
        }
    }
}
