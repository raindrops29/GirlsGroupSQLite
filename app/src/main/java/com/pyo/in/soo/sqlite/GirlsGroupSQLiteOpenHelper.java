package com.pyo.in.soo.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by samsung on 2016-01-24.
 */
public class GirlsGroupSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "girls_group_schema.db";
    private static GirlsGroupSQLiteOpenHelper dbHelper;

    private GirlsGroupSQLiteOpenHelper() {
        super(MYDBApplication.getGirlsContext(), DB_NAME, null, DB_VERSION);
    }

    public static GirlsGroupSQLiteOpenHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new GirlsGroupSQLiteOpenHelper();
        }
        return dbHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder tableOne = new StringBuilder();
        StringBuilder tableTwo = new StringBuilder();

        tableOne.append(" CREATE TABLE " + GirlsGroupTableInfos.GirlsGroupCompanyInfo.TABLE_NAME)
                .append(" (")
                .append(GirlsGroupTableInfos.GirlsGroupCompanyInfo._ID + " INTEGER primary key autoincrement ,")
                .append(GirlsGroupTableInfos.GirlsGroupCompanyInfo.COMPANY_NAME + " TEXT ")
                .append(" );");
        tableTwo.append(" CREATE TABLE " + GirlsGroupTableInfos.GirlsGroupInfo.TABLE_NAME)
                .append(" (")
                .append(GirlsGroupTableInfos.GirlsGroupInfo._ID + " INTEGER primary key autoincrement , ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.RELATION_COMPANY_ID + " INTEGER, ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.GROUP_NAME + " TEXT, ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_NAME + " TEXT, ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_BIRTHDAY + " TEXT, ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_IMAGE + " TEXT, ")
                .append(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_MEMO + " TEXT ")
                .append(" );");
        db.execSQL(tableOne.toString());

        db.execSQL(" INSERT INTO company_table_tbl(company_name_col) VALUES('" + "SM" + "');");
        db.execSQL(" INSERT INTO company_table_tbl(company_name_col) VALUES('" + "FNC" + "');");
        db.execSQL(" INSERT INTO company_table_tbl(company_name_col) VALUES('" + "JYP" + "');");
        db.execSQL(" INSERT INTO company_table_tbl(company_name_col) VALUES('" + "YG" + "');");

        db.execSQL(tableTwo.toString());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public ArrayList<String> getCompanyNames() {

        ArrayList<String> companyNames = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String[] columnNames = {GirlsGroupTableInfos.GirlsGroupCompanyInfo.COMPANY_NAME};
            db = getReadableDatabase();
            cursor = db.query(GirlsGroupTableInfos.GirlsGroupCompanyInfo.TABLE_NAME, columnNames,
                    null, null, null, null, null);

            companyNames = new ArrayList<String>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                companyNames.add(cursor.getString(cursor.getColumnIndex(
                        GirlsGroupTableInfos.GirlsGroupCompanyInfo.COMPANY_NAME)));
                cursor.moveToNext();
            }

        } catch (Exception e) {
            Log.e("getCompanyNames", e.toString());
        } finally {
            DBCloseManager.close(db, cursor);
        }
        return companyNames;
    }

    //걸그룹 입력.
    public boolean insertGirlsGroup(GirlsGroupValueObject girlInfo) {
        boolean flag = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int companyID = 0;
        try {

            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT _id FROM company_table_tbl WHERE company_name_col=?",
                    new String[]{girlInfo.companyName});
            cursor.moveToFirst();
            companyID = cursor.getInt(0);

            DBCloseManager.close(null, cursor);

            ContentValues values = new ContentValues();

            values.put(GirlsGroupTableInfos.GirlsGroupInfo.GROUP_NAME, girlInfo.groupName);
            values.put(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_BIRTHDAY, girlInfo.memberBirth);
            values.put(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_NAME, girlInfo.memberName);
            values.put(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_IMAGE, girlInfo.memberImageURI);
            values.put(GirlsGroupTableInfos.GirlsGroupInfo.RELATION_COMPANY_ID, companyID);
            values.put(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_MEMO, girlInfo.memberMemo);

            db.beginTransaction();
            long insertedID = db.insertOrThrow(GirlsGroupTableInfos.GirlsGroupInfo.TABLE_NAME, null, values);
            if (insertedID > 0) {
                flag = true;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(" insertGirlsGroup ", e.toString());
        } finally {
            db.endTransaction();
            DBCloseManager.close(db, cursor);
        }
        return flag;
    }
    public ArrayList<GirlsGroupValueObject> findGirlsGroupListAll(){
        ArrayList<GirlsGroupValueObject> list = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();

            cursor = db.query(GirlsGroupTableInfos.GirlsGroupInfo.TABLE_NAME, null, null ,null,null,null,null);
            if(cursor.getCount() > 0){
                cursor.moveToNext();
                list = new ArrayList<GirlsGroupValueObject>(cursor.getCount());
                while (!cursor.isAfterLast()) {
                    GirlsGroupValueObject valueObject = new GirlsGroupValueObject();
                    valueObject.companyID = cursor.getInt(cursor.getColumnIndex(GirlsGroupTableInfos.GirlsGroupInfo._ID));
                    valueObject.memberImageURI = cursor.getString(cursor.getColumnIndex(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_IMAGE));
                    valueObject.memberBirth = cursor.getString(cursor.getColumnIndex(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_BIRTHDAY));
                    valueObject.memberName = cursor.getString(cursor.getColumnIndex(GirlsGroupTableInfos.GirlsGroupInfo.MEMBER_NAME));
                    list.add(valueObject);
                    Log.e("values", String.valueOf(valueObject.memberImageURI) + "," +
                            String.valueOf(valueObject.memberBirth) + "," +
                            String.valueOf(valueObject.memberName));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("findGirlsGroupListAll", e.toString());
        } finally {
            DBCloseManager.close(db, cursor);
        }
        return list;
    }
}