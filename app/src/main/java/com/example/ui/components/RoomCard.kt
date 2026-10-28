package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomListingEntity
import com.example.ui.theme.CallBlue
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.WhatsAppGreen
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomCard(
  room: RoomListingEntity,
  isFavorite: Boolean,
  onFavoriteClick: () -> Unit,
  onClick: () -> Unit,
  onMessageClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("room_card_${room.id}")
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Photo with status overlays
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
      ) {
        RoomPhotoView(
          drawableName = room.photoDrawableName,
          contentDescription = room.title,
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        )

        // Availability Badge (Top Left)
        Surface(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopStart),
          shape = RoundedCornerShape(20.dp),
          color = if (room.isVacant) EmeraldSuccess else Color(0xFF64748B)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (room.isVacant) "AVAILABLE (खाली है)" else "RENTED (किराए पर है)",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Favorite Button (Top Right)
        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopEnd)
            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            .size(36.dp)
            .testTag("favorite_button_${room.id}")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Color(0xFFEF4444) else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Room Type & Suitability (Bottom Left of photo)
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f)
          ) {
            Text(
              text = room.roomType,
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f)
          ) {
            Text(
              text = room.suitableFor,
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Card Content
      Column(modifier = Modifier.padding(16.dp)) {
        // Price & Deposit Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "₹${formatRent(room.rentPerMonth)}",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = " / month",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
          }

          Text(
            text = "Deposit: ₹${formatRent(room.securityDeposit)}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.Medium
            )
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
          text = room.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Location & Landmark
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${room.area}, ${room.city} • ${room.nearbyLandmark}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Top Amenities Chips
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          maxItemsInEachRow = 4
        ) {
          room.amenities.take(4).forEach { amenity ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Text(
                text = "✓ $amenity",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                fontWeight = FontWeight.Medium
              )
            }
          }
          if (room.amenities.size > 4) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Text(
                text = "+${room.amenities.size - 4} more",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row (Call & WhatsApp & Details)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Call Button
          FilledTonalButton(
            onClick = {
              dialPhoneNumber(context, room.ownerPhone)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("call_owner_button_${room.id}"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = CallBlue.copy(alpha = 0.12f),
              contentColor = CallBlue
            )
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "Call",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call Malik", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          // WhatsApp Button
          FilledTonalButton(
            onClick = {
              openWhatsAppChat(context, room.ownerWhatsapp, room.title, room.rentPerMonth, room.city)
            },
            modifier = Modifier
              .weight(1.2f)
              .testTag("whatsapp_owner_button_${room.id}"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = WhatsAppGreen.copy(alpha = 0.15f),
              contentColor = Color(0xFF047857)
            )
          ) {
            Icon(
              imageVector = Icons.Default.Chat,
              contentDescription = "WhatsApp",
              modifier = Modifier.size(16.dp),
              tint = Color(0xFF047857)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          if (onMessageClick != null) {
            FilledTonalButton(
              onClick = onMessageClick,
              modifier = Modifier.testTag("in_app_message_button_${room.id}"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            ) {
              Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          // View Details Button
          ElevatedButton(
            onClick = onClick,
            modifier = Modifier.testTag("view_details_button_${room.id}"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color.White
            )
          ) {
            Text("Details", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

fun formatRent(amount: Int): String {
  return String.format("%,d", amount)
}

fun dialPhoneNumber(context: Context, phoneNumber: String) {
  try {
    val clean = phoneNumber.replace("[^0-9+]".toRegex(), "")
    val intent = Intent(Intent.ACTION_DIAL).apply {
      data = Uri.parse("tel:$clean")
    }
    context.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(context, "Cannot open dialer: $phoneNumber", Toast.LENGTH_SHORT).show()
  }
}

fun openWhatsAppChat(context: Context, rawPhone: String, roomTitle: String, rent: Int, city: String) {
  try {
    val cleanPhone = rawPhone.replace("[^0-9]".toRegex(), "")
    val phoneWithCode = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
    val message = "Hello Sir! Mai rul app se aapka room dekha: '$roomTitle' in $city (Rent: ₹$rent/month). Kya yeh room abhi available hai? Mujhe visit karna hai."
    val encoded = URLEncoder.encode(message, "UTF-8")
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneWithCode&text=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(context, "Could not open WhatsApp for $rawPhone", Toast.LENGTH_SHORT).show()
  }
}
