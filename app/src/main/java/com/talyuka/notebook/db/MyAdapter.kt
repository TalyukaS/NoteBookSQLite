package com.talyuka.notebook.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.talyuka.notebook.EditActivity
import com.talyuka.notebook.R

class MyAdapter(listMain: ArrayList<ListItem>, contextM: Context): RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM
    class MyHolder(itemView: View, contextV: Context): RecyclerView.ViewHolder(itemView) {
        private var tvTitle: TextView = itemView.findViewById(R.id.textView)
        private var tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private var context = contextV
        fun setData(item: ListItem) {
            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_IMAGE_URI_KEY, item.imageuri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }
    // создание шаблона одной строки списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.recycle_item, parent, false),context)
    }
    //функция заполнения всех строк списка
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }
    //показывает сколько строк в списке всего
    override fun getItemCount(): Int {return listArray.size}
    //обновление адаптера, очисткой и помещаем новые данные, затем новая проверка
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: List<ListItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }
    //удаление позиций списка
    fun removeItem(pos: Int, dbManager: MyDbManager) {
        dbManager.removeItemFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}