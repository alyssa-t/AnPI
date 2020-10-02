package com.ple2020pi.memoranki;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

    //versao database
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "MyCardDB_test.db";
    private static final String TABLE_NAME = "mycardtb";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_GROUP_NAME = "groupName";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_GROUP_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public OpenHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate (SQLiteDatabase db){
        db.execSQL(
                SQL_CREATE_ENTRIES
        );
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersdion, int newVersion) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade (SQLiteDatabase db, int oldVersdion, int newVersion) {
        onUpgrade(db, oldVersdion,newVersion);
    }
}
