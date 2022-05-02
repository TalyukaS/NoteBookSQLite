package com.talyuka.notebook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.talyuka.notebook.db.MyAdapter
import com.talyuka.notebook.db.MyDbManager
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        //открытие базы данных
        myDbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрытие базы данных
        myDbManager.closeDb()
    }
    //функция кнопки создания нового элемента базы данных
    fun onClickNew(view: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }
    //инициализация адаптера
    private fun init() {
        //расположение элементов построчно
        rcView.layoutManager = LinearLayoutManager(this)
        //удаление строки с элементом
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcView)
        //включение адаптера
        rcView.adapter = myAdapter
    }
    //функция считывания базы данных и обновление данных
    private fun fillAdapter(text: String) {
        //корутина запустится на основном потоке(Dispatchers.Main)
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            //следующая строка запускается на второстепееном потоке из-за функции readDb
            val list = myDbManager.readDb(text)
            myAdapter.updateAdapter(list)
            if (list.size > 0) {
                tvNoElements.visibility = View.GONE
            } else {
                tvNoElements.visibility = View.VISIBLE
            }
        }
    }
    //функция удаления строк RecycleView свапом, сдвигание пальцем строки влево или вправо
    private fun getSwapMg(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }
    //функция поиска
    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(text: String?): Boolean {
                fillAdapter(text!!)
                return true
            }
        })
    }
}