package com.example.myapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database
    (
    entities = [Trans::class],
    version = 1
)
abstract class TransDB : RoomDatabase(){

    abstract fun transDao() : TransDao

    companion object {

        @Volatile private var instance : TransDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TransDB::class.java,
            "trans.dbase"
        ).build()

    }
}