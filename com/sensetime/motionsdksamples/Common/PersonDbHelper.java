package com.sensetime.motionsdksamples.Common;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lyt on 2017/10/10.
 */

public class PersonDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "person.db";
    public static final String TABLE_NAME = "person";

    public PersonDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //debug
        //sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " + TABLE_NAME +
                " (Id integer primary key, " +
                "Uid text, Name text, Pinyin text, Age integer, Gender text, Registered integer, Feature blob)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public void dropTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}
