package com.naze.typing_practice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    final static String DB_NAME = "result.db"; //DB 이름
    final static int DB_VERSION = 2; //DB 버전

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE RESULT_TEST (NUM INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, TYPE INTEGER, ACC INTEGER)";
        db.execSQL(qry);

        qry = "INSERT INTO RESULT_TEST VALUES(NULL, '아름답고도 아프구나', 234, 98)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String qry = "DROP TABLE IF EXISTS RESULT_TEST";
        db.execSQL(qry);

        onCreate(db);
    }
}
