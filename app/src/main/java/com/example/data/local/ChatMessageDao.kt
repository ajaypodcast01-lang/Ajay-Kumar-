package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
  @Query("SELECT * FROM chat_messages WHERE roomId = :roomId AND ((senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1)) ORDER BY timestamp ASC")
  fun getConversation(roomId: Long, userId1: String, userId2: String): Flow<List<ChatMessageEntity>>

  @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
  fun getAllUserMessages(userId: String): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(messages: List<ChatMessageEntity>)

  @Query("UPDATE chat_messages SET isRead = 1 WHERE roomId = :roomId AND receiverId = :currentUserId")
  suspend fun markAsRead(roomId: Long, currentUserId: String)

  @Query("SELECT COUNT(*) FROM chat_messages WHERE receiverId = :userId AND isRead = 0")
  fun getUnreadCount(userId: String): Flow<Int>
}
