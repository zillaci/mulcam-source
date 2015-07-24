package com.destroy.ddopagoihanda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by student on 2015-07-21.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String studentSql = "create table tb_student " +
                "(_id integer primary key autoincrement," +
                "name not null," +
                "email, " +
                "phone, " +
                "photo, " +
                "memo)";

        String scoreSql = "create table tb_score " +
                "(_id integer primary key autoincrement," +
                "student_id, "+
                "date, "+
                "score)";

        db.execSQL(studentSql);
        db.execSQL(scoreSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropStudentSql = "drop table tb_student";
        String dropScoreSql = "drop table tb_score";

        db.execSQL(dropScoreSql);
        db.execSQL(dropStudentSql);

        onCreate(db);
    }
}
