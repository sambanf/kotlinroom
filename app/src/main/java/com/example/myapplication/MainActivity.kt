package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.room.Trans
import com.example.myapplication.room.TransDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_trans.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    internal var myExternalFile: File?=null
    val db by lazy { TransDB(this) }
    lateinit var transAdapter: TransAdapter

    private val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)
    }
    private val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(extStorageState)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()
        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            Log.d("MainAct", "error: Permission")
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }
    fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            val trans = db.transDao().getTrans()
            Log.d("MainAct", "dbrespose: $trans")
            withContext(Dispatchers.Main){
                transAdapter.setdata(trans)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupListener(){
        button_create.setOnClickListener{
            intentSwitch(0,0)
        }
        val fileName = "hello.txt"
        val fileData = "hello"
        button_export.setOnClickListener{
                myExternalFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                try {
                    val fileOutPutStream = FileOutputStream(myExternalFile)
                    fileOutPutStream.write(fileData.toByteArray())
                    fileOutPutStream.close()
                } catch (e: Exception) {
                    Log.d("MainAct", "error: ${e.message}")
                }
                Toast.makeText(applicationContext,"data save to $myExternalFile",Toast.LENGTH_SHORT).show()
            }

        button_deleteall.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                db.transDao().deleteAll()
                loadData()
            }
        }

    }

    private fun setupRecyclerView(){
        transAdapter = TransAdapter(arrayListOf(), object:TransAdapter.OnAdapterListener{
            override fun onClick(tran: Trans) {
                intentSwitch(tran.id, 2)
            }

            override fun onUpdate(tran: Trans) {
                intentSwitch(tran.id, 2)
            }

            override fun onDelete(tran: Trans) {
                CoroutineScope(Dispatchers.IO).launch {
                    db.transDao().deleteTrans(tran)
                    loadData()
                }
            }
        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = transAdapter
        }
    }
    fun intentSwitch(tranid:Int, intentype:Int){
        startActivity(Intent(applicationContext,EditActivity::class.java).putExtra("intent_id", tranid).putExtra("intent_type", intentype))
    }
}