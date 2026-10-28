package com.example.ui.screens

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatConversationSummary
import com.example.data.model.ChatMessageEntity
import com.example.data.model.RoomConstants
import com.example.data.model.RoomListingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.RoomPhotoView
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.formatRent
import com.example.ui.theme.CallBlue
import com.example.ui.theme.EmeraldSuccess
import java.util.Date

@Composable
fun MessagesListScreen(
  conversations: List<ChatConversationSummary>,
  onConversationClick: (ChatConversationSummary) -> Unit,
  onExploreRoomsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("messages_list_screen")
  ) {
    // Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "Personal Chats & Calls (व्यक्तिगत बातचीत)",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      )
      Text(
        text = "Chat, share photos, audio voice notes, video tours, and make calls",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
    }

    if (conversations.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Chat,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No Chats Yet (कोई बातचीत नहीं)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Send an inquiry to room owners or students to start chatting, sharing media, and making calls.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onExploreRoomsClick,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Browse Rooms & Start Chat")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = conversations,
          key = { "${it.roomId}_${it.otherUserId}" }
        ) { conv ->
          ConversationCard(
            summary = conv,
            onClick = { onConversationClick(conv) }
          )
        }
      }
    }
  }
}

@Composable
private fun ConversationCard(
  summary: ChatConversationSummary,
  onClick: () -> Unit
) {
  val formattedTime = remember(summary.lastTimestamp) {
    DateFormat.format("hh:mm a", Date(summary.lastTimestamp)).toString()
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("conversation_card_${summary.roomId}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Home,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = summary.otherUserName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = summary.roomTitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = summary.lastMessage,
          style = MaterialTheme.typography.bodySmall.copy(
            color = if (summary.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (summary.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      if (summary.unreadCount > 0) {
        Spacer(modifier = Modifier.width(8.dp))
        Badge(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White
        ) {
          Text("${summary.unreadCount}")
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadSheet(
  room: RoomListingEntity,
  otherUserId: String,
  otherUserName: String,
  messages: List<ChatMessageEntity>,
  activeUser: UserEntity,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSendMessage: (text: String, inquiryType: String, mediaType: String, mediaUri: String?, duration: String?) -> Unit,
  onStartAudioCall: () -> Unit,
  onStartVideoCall: () -> Unit,
  onViewRoomDetails: () -> Unit
) {
  val context = LocalContext.current
  var textInput by remember { mutableStateOf("") }
  var showAttachmentSheet by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  // Scroll to bottom when new messages arrive
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .testTag("chat_thread_sheet")
    ) {
      // Chat Header with Audio & Video Call Buttons (User Requirement)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close chat")
        }

        Spacer(modifier = Modifier.width(2.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = otherUserName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Text(
            text = "Personal Chat • End-to-End Private",
            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldSuccess, fontSize = 10.sp)
          )
        }

        // Voice Audio Call Button
        IconButton(
          onClick = onStartAudioCall,
          modifier = Modifier.testTag("audio_call_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Audio Call",
            tint = CallBlue,
            modifier = Modifier.size(22.dp)
          )
        }

        // Video Call Button
        IconButton(
          onClick = onStartVideoCall,
          modifier = Modifier.testTag("video_call_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = "Video Call",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Room Context Banner at top of chat
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp)
          .clickable { onViewRoomDetails() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RoomPhotoView(
            drawableName = room.photoDrawableName,
            contentDescription = room.title,
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(8.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = room.title,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "₹${formatRent(room.rentPerMonth)}/mo • ${room.area}, ${room.city}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
            )
          }
          FilledTonalButton(
            onClick = onViewRoomDetails,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
          ) {
            Text("Details", fontSize = 11.sp)
          }
        }
      }

      HorizontalDivider(modifier = Modifier.padding(top = 6.dp))

      // Chat Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = messages,
          key = { it.id }
        ) { msg ->
          val isMe = msg.senderId == "user_default"
          ChatBubbleRichMedia(message = msg, isMe = isMe)
        }
      }

      // Quick Suggestion Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val quickReplies = listOf(
          "📅 Request Viewing (विज़िट तय करें)" to "VIEWING_REQUEST",
          "Is electricity bill included in rent?" to "INQUIRY",
          "Can you send photo of study table?" to "PHOTO_REQUEST",
          "Please send room video walkthrough" to "VIDEO_REQUEST"
        )
        quickReplies.forEach { (replyText, type) ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            modifier = Modifier.clickable {
              onSendMessage(replyText, type, "TEXT", null, null)
            }
          ) {
            Text(
              text = replyText,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      // Input Bar with Attachment, Voice, and Send (User Requirement: Photos, Audio, Video)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 10.dp, vertical = 8.dp)
          .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Attachment Button (+)
        IconButton(
          onClick = { showAttachmentSheet = true },
          modifier = Modifier.testTag("chat_attachment_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Attach media",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }

        // Quick Voice Note Recorder Button
        IconButton(
          onClick = {
            onSendMessage(
              "🎤 Voice Note (0:24) - Room details discussion",
              "AUDIO",
              "AUDIO",
              null,
              "0:24"
            )
            Toast.makeText(context, "Voice note sent!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("chat_quick_voice_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Send Voice Note",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
          )
        }

        OutlinedTextField(
          value = textInput,
          onValueChange = { textInput = it },
          placeholder = { Text("Type message...") },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field"),
          shape = RoundedCornerShape(24.dp),
          maxLines = 3,
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background
          )
        )

        IconButton(
          onClick = {
            if (textInput.isNotBlank()) {
              onSendMessage(textInput.trim(), "GENERAL", "TEXT", null, null)
              textInput = ""
            }
          },
          modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .size(44.dp)
            .testTag("send_chat_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }

  // Media Attachment Picker Sheet (User Requirement: Share photos, audio, video)
  if (showAttachmentSheet) {
    ModalBottomSheet(
      onDismissRequest = { showAttachmentSheet = false },
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Share Photos, Audio or Video (मीडिया शेयर करें)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          // Send Photo Option
          MediaOptionButton(
            icon = Icons.Default.Image,
            label = "Room Photo\n(फोटो)",
            color = MaterialTheme.colorScheme.primary,
            onClick = {
              showAttachmentSheet = false
              onSendMessage(
                "📸 Room Study Desk & Sunlight Photo",
                "PHOTO",
                "PHOTO",
                room.photoDrawableName,
                null
              )
              Toast.makeText(context, "Room Photo shared in chat!", Toast.LENGTH_SHORT).show()
            }
          )

          // Send Audio Voice Note Option
          MediaOptionButton(
            icon = Icons.Default.Audiotrack,
            label = "Audio Note\n(वॉयस नोट)",
            color = Color(0xFF0284C7),
            onClick = {
              showAttachmentSheet = false
              onSendMessage(
                "🎵 Voice Note (0:36): Inquiring about gate timings & WiFi speed",
                "AUDIO",
                "AUDIO",
                null,
                "0:36"
              )
              Toast.makeText(context, "Audio Voice Note shared!", Toast.LENGTH_SHORT).show()
            }
          )

          // Send Video Tour Option
          MediaOptionButton(
            icon = Icons.Default.VideoLibrary,
            label = "Video Tour\n(वीडियो टूर)",
            color = Color(0xFF9333EA),
            onClick = {
              showAttachmentSheet = false
              onSendMessage(
                "🎥 360° Room Walkthrough Video Tour",
                "VIDEO",
                "VIDEO",
                room.photoDrawableName,
                "0:45"
              )
              Toast.makeText(context, "Video Tour shared in chat!", Toast.LENGTH_SHORT).show()
            }
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun MediaOptionButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  color: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .background(color.copy(alpha = 0.15f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Center,
      lineHeight = 14.sp
    )
  }
}

@Composable
private fun ChatBubbleRichMedia(
  message: ChatMessageEntity,
  isMe: Boolean
) {
  val formattedTime = remember(message.timestamp) {
    DateFormat.format("hh:mm a", Date(message.timestamp)).toString()
  }
  var isAudioPlaying by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMe) 16.dp else 4.dp,
        bottomEnd = if (isMe) 4.dp else 16.dp
      ),
      color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
      tonalElevation = 1.dp
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        // Special badge for viewing requests
        if (message.inquiryType == "VIEWING_REQUEST") {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isMe) Color.White.copy(alpha = 0.2f) else EmeraldSuccess.copy(alpha = 0.15f),
            modifier = Modifier.padding(bottom = 6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = if (isMe) Color.White else EmeraldSuccess,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Viewing Request (विज़िट तय करें)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMe) Color.White else EmeraldSuccess
              )
            }
          }
        }

        // PHOTO MEDIA BUBBLE (User Requirement: share photos)
        if (message.mediaType == "PHOTO" && message.mediaUri != null) {
          RoomPhotoView(
            drawableName = message.mediaUri,
            contentDescription = "Shared photo",
            modifier = Modifier
              .size(width = 220.dp, height = 150.dp)
              .clip(RoundedCornerShape(10.dp))
          )
          Spacer(modifier = Modifier.height(6.dp))
        }

        // VIDEO MEDIA BUBBLE (User Requirement: share video)
        if (message.mediaType == "VIDEO") {
          Box(
            modifier = Modifier
              .size(width = 230.dp, height = 140.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            RoomPhotoView(
              drawableName = message.mediaUri ?: "room_single_study_1793006717976",
              contentDescription = "Video preview",
              modifier = Modifier.fillMaxSize()
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
            )
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play video", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color.Black.copy(alpha = 0.7f),
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
            ) {
              Text(
                text = message.mediaDuration ?: "0:45",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
        }

        // AUDIO VOICE NOTE BUBBLE (User Requirement: share audio)
        if (message.mediaType == "AUDIO") {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isMe) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(210.dp)
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              IconButton(
                onClick = { isAudioPlaying = !isAudioPlaying },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Play/Pause Audio",
                  tint = if (isMe) Color.White else MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Column(modifier = Modifier.weight(1f)) {
                // Waveform simulation
                Row(
                  horizontalArrangement = Arrangement.spacedBy(2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.height(18.dp)
                ) {
                  val heights = listOf(6, 12, 16, 10, 14, 8, 16, 12, 18, 10, 6, 14, 10, 8, 12)
                  heights.forEach { h ->
                    Box(
                      modifier = Modifier
                        .width(3.dp)
                        .height(h.dp)
                        .background(
                          if (isMe) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                          RoundedCornerShape(1.dp)
                        )
                    )
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = if (isAudioPlaying) "Playing..." else (message.mediaDuration ?: "0:24 • Voice Note"),
                  fontSize = 10.sp,
                  color = if (isMe) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
        }

        // Caption / text
        Text(
          text = message.messageText,
          color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = formattedTime,
          color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline,
          fontSize = 9.sp,
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}
