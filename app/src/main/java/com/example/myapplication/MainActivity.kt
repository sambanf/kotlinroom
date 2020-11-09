package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.room.Trans
import com.example.myapplication.room.TransDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val db by lazy { TransDB(this) }
    lateinit var transAdapter: TransAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val trans = db.transDao().getTrans()
            Log.d("MainAct", "dbrespose: $trans")
            withContext(Dispatchers.Main){
                transAdapter.setdata(trans)
            }
        }
    }
    fun setupListener(){
        button_create.setOnClickListener{
            startActivity(Intent(this,EditActivity::class.java))
        }
    }

    private fun setupRecyclerView(){
        transAdapter = TransAdapter(arrayListOf(), object:TransAdapter.OnAdapterListener{
            override fun onClick(tran: Trans) {
                Toast.makeText(applicationContext, "Barcode :"+tran.code + " Quantity :"+ tran.qty, Toast.LENGTH_SHORT).show()
            }

        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = transAdapter
        }
    }
}