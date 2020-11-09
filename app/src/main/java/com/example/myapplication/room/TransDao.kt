package com.example.myapplication.room

import androidx.room.*

@Dao
interface TransDao {

    @Insert
    fun addTrans(trans: Trans)

    @Update
    fun updateTrans(trans: Trans)

    @Delete
    fun deleteTrans(trans: Trans)

    @Query(value =  "SELECT * FROM trans")
    fun getTrans() :List<Trans>
}