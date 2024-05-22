package com.example.di1.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MessageDao {
    @Upsert
    fun upsertMessage(message: Message)

    @Delete
    fun deleteMessage(message: Message)

    @Query("SELECT * FROM message")
    fun getMessages(): List<Message>
}