package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.room.Trans
import com.example.myapplication.room.TransDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {
    val db by lazy { TransDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupListener()
    }

    fun setupListener() {
        button_save.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                db.transDao().addTrans(
                    Trans(id = 0, code = edit_title.text.toString(), qty = edit_note.text.toString().toInt())
                )
                finish()
            }
        }
    }
}