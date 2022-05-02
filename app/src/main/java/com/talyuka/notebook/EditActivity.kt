package com.talyuka.notebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.talyuka.notebook.db.MyDbManager
import com.talyuka.notebook.db.MyIntentConstants
import kotlinx.android.synthetic.main.edit_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    var id = 0
    var isEditState = false
    var tempImageUri = "empty"
    private val myDbManager = MyDbManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        //открытие базы данных
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрытие базы данных
        myDbManager.closeDb()
    }
    //функция кнопки добавления фото
    fun onClickAddImage(view: View) {
        myImageLayout.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
    }
    //функция кнопки удаления фото
    fun onClickDeliteImage(view: View) {
        myImageLayout.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
    }
    //функция кнопки для записи данных в базу данных
    fun onClickSave(view: View) {
        val myTitle = edTitle.text.toString()
        val myDesc = edDesc.text.toString()
        if (myTitle != "" && myDesc != "") {
            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState){
                    myDbManager.uptateItemFromDb(myTitle, myDesc, tempImageUri, id, getRealTime())
                } else {myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getRealTime())}
                finish()
            }
        }
    }
    //функция кнопки изменения фото из галлереи
    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        getAction.launch(intent)
    }
    //новая замена onActivityResult
    private val getAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            imageView.setImageURI(it?.data?.data)
            tempImageUri = it?.data?.data.toString()
            contentResolver.takePersistableUriPermission(
                it?.data?.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    //функция разрешения редактирования записи
    fun onEditEnable(view: View) {
        myImageLayout.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
        fbDel.visibility = View.VISIBLE
        fbEdit.visibility = View.VISIBLE
        fbEditItem.visibility = View.GONE
        edTitle.isEnabled = true
        edDesc.isEnabled = true
    }
    //функция вызова из базы данных на страницу редактирования записей
    private fun getMyIntents() {
        val int = intent
        if (int != null) {
            if (int.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {
                edTitle.setText(int.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                fbEditItem.visibility = View.VISIBLE
                edTitle.isEnabled = false
                edDesc.isEnabled = false
                edDesc.setText(int.getStringExtra(MyIntentConstants.I_DESC_KEY))
                id = int.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (int.getStringExtra(MyIntentConstants.I_IMAGE_URI_KEY) != "empty") {
                    myImageLayout.visibility = View.VISIBLE
                    imageView.setImageURI(Uri.parse(int.getStringExtra(MyIntentConstants.I_IMAGE_URI_KEY)))
                    fbDel.visibility = View.GONE
                    fbEdit.visibility = View.GONE
                }
            }
        }
    }
    //функция добавления времени
    private fun getRealTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }
}