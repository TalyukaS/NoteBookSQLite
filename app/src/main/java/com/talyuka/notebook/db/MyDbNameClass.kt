package com.talyuka.notebook.db

import android.provider.BaseColumns

object MyDbNameClass {
    // таблица                                           id   title    content    uri           time
    const val TAB_NAME = "My_Table"                    // 1   водка      40     c://*.image1    14:02
    const val COLUMN_NAME_TITLE = "tablitsa"           // 2   пиво       10     c://*.image2    05:44
    const val COLUMN_NAME_CONTENT = "content"          // 3   сок         0     c://*.image3    11:22
    const val COLUMN_NAME_IMAGE_URI = "uri"            // 4   картинка    0     c://*.image4    23:15
    const val COLUMN_NAME_TIME = "time"                // 5   картинка    0     c://*.image5    15:11
    // база данных
    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "NoteBook.db"
    // создаем таблицу в базе данных
    const val SQL_CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TAB_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_NAME_TITLE TEXT," +
            " $COLUMN_NAME_CONTENT TEXT, $COLUMN_NAME_IMAGE_URI TEXT, $COLUMN_NAME_TIME TEXT)"
    // удаляем таблицу в базе данных
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TAB_NAME"
}