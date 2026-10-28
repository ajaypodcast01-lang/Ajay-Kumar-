package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val roomId: Long,
  val roomTitle: String,
  val senderId: String,
  val senderName: String,
  val receiverId: String,
  val receiverName: String,
  val messageText: String,
  val inquiryType: String = "GENERAL", // GENERAL, INQUIRY, VIEWING_REQUEST, PHOTO, AUDIO, VIDEO
  val mediaType: String = "TEXT", // TEXT, PHOTO, AUDIO, VIDEO
  val mediaUri: String? = null,
  val mediaDuration: String? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)

data class ChatConversationSummary(
  val roomId: Long,
  val roomTitle: String,
  val otherUserId: String,
  val otherUserName: String,
  val lastMessage: String,
  val lastTimestamp: Long,
  val unreadCount: Int,
  val lastMediaType: String = "TEXT"
)
