package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.components.RoomPhotoView
import kotlinx.coroutines.delay

enum class CallType {
  AUDIO,
  VIDEO
}

@Composable
fun CallScreen(
  callType: CallType,
  callerName: String,
  callerPhone: String,
  roomTitle: String,
  photoDrawableName: String = "room_single_study_1793006717976",
  onEndCall: (durationSeconds: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var isMuted by remember { mutableStateOf(false) }
  var isSpeakerOn by remember { mutableStateOf(callType == CallType.VIDEO) }
  var isVideoActive by remember { mutableStateOf(callType == CallType.VIDEO) }
  var isFrontCamera by remember { mutableStateOf(true) }
  var callDurationSeconds by remember { mutableIntStateOf(0) }
  var isConnected by remember { mutableStateOf(false) }

  // Simulate ringing -> connect after 1.5 seconds, then count up
  LaunchedEffect(Unit) {
    delay(1500)
    isConnected = true
    while (true) {
      delay(1000)
      callDurationSeconds++
    }
  }

  // Pulsing animation for audio avatar
  val pulseScale = remember { Animatable(1f) }
  LaunchedEffect(isConnected) {
    if (!isConnected) {
      pulseScale.animateTo(
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
          animation = tween(800, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      pulseScale.snapTo(1f)
    }
  }

  val minutes = callDurationSeconds / 60
  val seconds = callDurationSeconds % 60
  val timerFormatted = String.format("%02d:%02d", minutes, seconds)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .testTag("active_call_screen")
  ) {
    // If Video Call and video is active, show simulated video backdrop
    if (callType == CallType.VIDEO && isVideoActive) {
      RoomPhotoView(
        drawableName = photoDrawableName,
        contentDescription = roomTitle,
        modifier = Modifier.fillMaxSize()
      )

      // Dark gradient overlay for text readability
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color.Black.copy(alpha = 0.7f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.85f)
              )
            )
          )
      )

      // Small self-view PiP in top-right
      Surface(
        modifier = Modifier
          .padding(top = 48.dp, end = 16.dp)
          .size(width = 90.dp, height = 130.dp)
          .align(Alignment.TopEnd)
          .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Self Camera",
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(36.dp)
          )
          Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(4.dp)
          ) {
            Text(
              text = if (isFrontCamera) "You (Front)" else "You (Back)",
              color = Color.White,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    // Call Content & Controls
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Info
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (callType == CallType.VIDEO) "My Room HD Video Call" else "My Room Voice Call",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = callerName,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = roomTitle,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (!isConnected) "Ringing (कॉल लग रही है)..." else "Connected • $timerFormatted",
          style = MaterialTheme.typography.bodySmall.copy(
            color = if (isConnected) EmeraldSuccess else Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold
          )
        )
      }

      // Center Avatar for Audio Call
      if (callType == CallType.AUDIO || !isVideoActive) {
        Box(
          modifier = Modifier
            .size(140.dp)
            .scale(pulseScale.value),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(140.dp)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape)
          )
          Box(
            modifier = Modifier
              .size(110.dp)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape)
          )
          Box(
            modifier = Modifier
              .size(84.dp)
              .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(44.dp)
            )
          }
        }
      } else {
        Spacer(modifier = Modifier.weight(1f))
      }

      // Bottom Call Controls
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 30.dp)
      ) {
        // Feature Buttons Row (Mute, Video/Cam, Speaker)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mute Button
          CallControlButton(
            icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
            label = if (isMuted) "Unmute" else "Mute",
            isActive = isMuted,
            onClick = { isMuted = !isMuted }
          )

          // Video Toggle or Camera Switch
          if (callType == CallType.VIDEO) {
            CallControlButton(
              icon = Icons.Default.Cameraswitch,
              label = "Flip Cam",
              isActive = false,
              onClick = { isFrontCamera = !isFrontCamera }
            )
            CallControlButton(
              icon = if (isVideoActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
              label = if (isVideoActive) "Cam Off" else "Cam On",
              isActive = !isVideoActive,
              onClick = { isVideoActive = !isVideoActive }
            )
          }

          // Speaker Button
          CallControlButton(
            icon = Icons.Default.VolumeUp,
            label = if (isSpeakerOn) "Speaker On" else "Speaker",
            isActive = isSpeakerOn,
            onClick = { isSpeakerOn = !isSpeakerOn }
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Big Red End Call Button
        IconButton(
          onClick = { onEndCall(callDurationSeconds) },
          modifier = Modifier
            .size(68.dp)
            .background(Color(0xFFEF4444), CircleShape)
            .testTag("end_call_button")
        ) {
          Icon(
            imageVector = Icons.Default.CallEnd,
            contentDescription = "End call",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "End Call (कॉल काटें)",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun CallControlButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    IconButton(
      onClick = onClick,
      modifier = Modifier
        .size(54.dp)
        .background(
          if (isActive) Color.White else Color.White.copy(alpha = 0.2f),
          CircleShape
        )
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isActive) Color.Black else Color.White,
        modifier = Modifier.size(24.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = Color.White.copy(alpha = 0.8f),
      fontSize = 11.sp
    )
  }
}
