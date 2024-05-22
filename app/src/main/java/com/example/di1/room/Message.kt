package com.example.di1.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "from") val from: String,
    @ColumnInfo(name = "sim_id") val simId: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
