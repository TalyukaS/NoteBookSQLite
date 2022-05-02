package com.talyuka.notebook.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// класс работы с базой данных
class MyDbManager(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null
    //открытие базы данных
    fun openDb() {db = myDbHelper.writableDatabase}
    // читаем базу данных
    @SuppressLint("Recycle", "Range")
    //функция запустится на второстепенном потоке(suspend и Dispatchers.IO)
    suspend fun readDb(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListItem>()
        //запрос данных из базы
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TAB_NAME,       // таблица для запроса
            null,                 // Возвращаемый массив столбцов (передайте значение null, чтобы получить все)
            selection,                    // Столбцы для предложения WHERE
            arrayOf("%$searchText%"),     // Значения для предложения WHERE
            null,                 // не группируйте строки
            null,                  // не фильтруйте по группам строк
            null                  // Порядок сортировки
        )
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataContent = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataImageUri = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
            val dataTime = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val item = ListItem()
            item.title = dataTitle
            item.desc = dataContent
            item.imageuri = dataImageUri
            item.time = dataTime
            item.id = dataId
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }
    // запись в базу данных
    suspend fun insertToDb(title: String, content: String,
        uri: String, time: String) = withContext(Dispatchers.IO) {
        //переменная с данными
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        // запись переменной в базу
        db?.insert(MyDbNameClass.TAB_NAME, null, values)
    }
    //изменяем запись в базе данных
    suspend fun uptateItemFromDb(title: String, content: String,
        uri: String, id: Int, time: String) = withContext(Dispatchers.IO) {
        // выбор по id
        val selection = BaseColumns._ID + "=$id"
        //переменная с данными для изменения
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        // запись измененной переменной в базу
        db?.update(MyDbNameClass.TAB_NAME, values, selection, null)
    }
    //удаляем запись в базе данных
    fun removeItemFromDb(id: String) {
        // выбор по id
        val selection = BaseColumns._ID + "=$id"
        //удаление по id
        db?.delete(MyDbNameClass.TAB_NAME, selection, null)
    }
    //закрываем базу данных
    fun closeDb() {myDbHelper.close()}
}