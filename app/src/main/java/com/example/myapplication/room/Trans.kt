package com.example.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trans(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val code:String,
    val qty:Int
)