package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.room.Trans
import com.example.myapplication.room.TransDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val CREATE_REQUEST_CODE = 40
    private val OPEN_REQUEST_CODE = 41
    private val SAVE_REQUEST_CODE = 42
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
        button_export.setOnClickListener{

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "export.txt")

            startActivityForResult(intent, CREATE_REQUEST_CODE)
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
               deleteDialog(tran)
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

    private fun deleteDialog(trans: Trans){
        val alertDialog = AlertDialog.Builder(this);
        alertDialog.apply {
            setTitle("Confirm Delete")
            setMessage("${trans.code} with ${trans.qty} Quantity")
            setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete"){dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.transDao().deleteTrans(trans)
                    loadData()
                }
            }
        }
        alertDialog.show();
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        var currentUri: Uri? = null

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CREATE_REQUEST_CODE) {
                resultData?.let {
                    currentUri = it.data
                    writeFileContent(currentUri)
                }
            }
        }
    }
    private fun writeFileContent(uri: Uri?) {
        try {
            val pfd = uri?.let { contentResolver.openFileDescriptor(it, "w") }

            val fileOutputStream = FileOutputStream(
                pfd?.fileDescriptor
            )

            CoroutineScope(Dispatchers.IO).launch {
                val trans = db.transDao().getTrans()
                Log.d("MainAct", "dbrespose: $trans")

                for (tr in trans) {
                    // Here your room is available
                    var textContent = "${tr.code}, ${tr.qty}\n"
                    fileOutputStream.write(textContent.toByteArray())
                }

                fileOutputStream.close()
                pfd?.close()
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}