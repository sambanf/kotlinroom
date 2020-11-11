package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.room.Trans
import com.example.myapplication.room.TransDB
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.adapter_trans.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditActivity : AppCompatActivity() {
    val db by lazy { TransDB(this) }
    private var tranid:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupListener()
        setupView()
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
        icon_plus.setOnClickListener{
            edit_note.setText((edit_note.text.toString().toInt() + 1).toString())
        }
        icon_min.setOnClickListener{
            edit_note.setText((edit_note.text.toString().toInt() - 1).toString())
        }
        button_update.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                db.transDao().updateTrans(
                    Trans(id = tranid, code = edit_title.text.toString(), qty = edit_note.text.toString().toInt())
                )
                finish()
            }
        }
        barcodeicon.setOnClickListener{
            val scanner = IntentIntegrator(this)

            scanner.initiateScan()
        }
    }

    fun setupView(){
        var intentype = intent.getIntExtra("intent_type",0)
        when(intentype){
            //Create
            0 -> {
                button_update.visibility = View.GONE
                edit_note.setText(0.toString())
            }
            1 -> {
                button_save.visibility = View.GONE
                button_update.visibility = View.GONE
                getforEdit()
                edit_title.isEnabled = false
                edit_note.isEnabled = false
            }
            2->{
                button_save.visibility = View.GONE
                getforEdit()
            }
        }
    }

    fun getforEdit(){
        tranid = intent.getIntExtra("intent_id",0)
        CoroutineScope(Dispatchers.IO).launch {
            var tran = db.transDao().getTran(tranid)[0]
            edit_title.setText(tran.code)
            edit_note.setText((tran.qty.toString()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    edit_title.setText(result.contents.toString())
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}