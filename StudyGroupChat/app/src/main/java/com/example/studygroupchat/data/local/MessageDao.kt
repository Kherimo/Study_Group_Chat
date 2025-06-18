package com.example.studygroupchat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY sentAt")
    suspend fun getMessagesForRoom(roomId: Int): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE roomId = :roomId")
    suspend fun clearMessagesForRoom(roomId: Int)
}