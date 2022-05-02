package com.talyuka.notebook.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context): SQLiteOpenHelper(context, MyDbNameClass.DATABASE_NAME,null,
    MyDbNameClass.DATABASE_VERSION){
    // создание базы данных
    override fun onCreate(db: SQLiteDatabase?) {db?.execSQL(MyDbNameClass.SQL_CREATE_TABLE)}
    // обновление базы данных
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(MyDbNameClass.SQL_DELETE_TABLE)
        onCreate(db)
    }
}