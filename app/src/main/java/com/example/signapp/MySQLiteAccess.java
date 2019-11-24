package com.example.signapp;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteAccess extends SQLiteOpenHelper{

    public MySQLiteAccess(Context context,int version) {
        super(context,"students.db", null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建学生表
        db.execSQL("create table student(sno varchar(20) primary key" +
                ",sclass varchar(20)" +
                ",sname varchar(20)" +
                ",ssex char(2)" +
                ",photo blob" +
                ",num_jiafen integer" +
                ",num_taoke integer " +
                ",num_zaotui integer " +
                ",num_qingjia integer " +
                ",num_chidao integer " +
                ",score integer )");
        db.execSQL("create table stuclass(classname varchar(20))");
    }
    //当数据库版本发生变化时，会自动执行
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
