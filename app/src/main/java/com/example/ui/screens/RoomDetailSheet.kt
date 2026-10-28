package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomListingEntity
import com.example.ui.components.RoomPhotoView
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.formatRent
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.CallBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.WhatsAppGreen
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoomDetailSheet(
  room: RoomListingEntity,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onMessageClick: (() -> Unit)? = null,
  onPayRentClick: (() -> Unit)? = null
) {
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .testTag("room_detail_sheet")
    ) {
      // Photo Header with close & favorite buttons
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
      ) {
        RoomPhotoView(
          drawableName = room.photoDrawableName,
          contentDescription = room.title,
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
        )

        // Close button (Top Left)
        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopStart)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            .size(36.dp)
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        // Action icons (Top Right: Share & Favorite)
        Row(
          modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopEnd),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = {
              shareRoomListing(context, room)
            },
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.5f), CircleShape)
              .size(36.dp)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
          }

          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.5f), CircleShape)
              .size(36.dp)
          ) {
            Icon(
              imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Favorite",
              tint = if (isFavorite) Color(0xFFEF4444) else Color.White
            )
          }
        }

        // Vacancy Badge
        Surface(
          modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomStart),
          shape = RoundedCornerShape(20.dp),
          color = if (room.isVacant) EmeraldSuccess else Color(0xFF64748B)
        ) {
          Text(
            text = if (room.isVacant) "AVAILABLE (खाली है)" else "RENTED (किराए पर है)",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }

      // Content Body
      Column(modifier = Modifier.padding(20.dp)) {
        // Rent & Tags
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "₹${formatRent(room.rentPerMonth)}",
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              Text(
                text = " / month",
                style = MaterialTheme.typography.bodyLarge.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
              )
            }
            Text(
              text = "Zero Brokerage • Direct Owner Listing",
              style = MaterialTheme.typography.bodySmall.copy(
                color = EmeraldSuccess,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = room.roomType,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title
        Text(
          text = room.title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Address & Nearby Landmark
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
              .size(20.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "${room.area}, ${room.city}",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = room.fullAddress,
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = "Landmark: ${room.nearbyLandmark}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
              ),
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Open Map Button
        OutlinedButton(
          onClick = {
            openMapLocation(context, "${room.nearbyLandmark}, ${room.area}, ${room.city}")
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Open Location in Maps (दिशा देखें)", fontSize = 13.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Financial & House Terms Grid
        Text(
          text = "Rental Terms & Charges (किराया विवरण)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          TermCard(
            label = "Security Deposit",
            value = "₹${formatRent(room.securityDeposit)}",
            subtext = "Refundable",
            modifier = Modifier.weight(1f)
          )
          TermCard(
            label = "Electricity",
            value = room.electricityCharge,
            subtext = "Sub-meter",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          TermCard(
            label = "Suitable For",
            value = room.suitableFor,
            subtext = "Tenant Preference",
            modifier = Modifier.weight(1f)
          )
          TermCard(
            label = "Gate Timing",
            value = room.gateClosingTime,
            subtext = "${room.noticePeriodDays} days notice",
            modifier = Modifier.weight(1f)
          )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Description
        Text(
          text = "About this Room (कमरे का विवरण)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = room.description,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Available Amenities
        Text(
          text = "Available Amenities (${room.amenities.size} सुविधाएं)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          room.amenities.forEach { amenity ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "✓",
                  color = EmeraldSuccess,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = amenity,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Room Malik (Owner) Contact Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = room.ownerName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Verified Owner",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
              }
              Text(
                text = "Room Malik (Landlord)",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
              Text(
                text = "Phone: +91 ${room.ownerPhone}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pay Room Rent Action (User Requirement: students can pay room rent in room owner in this app and make room rent receipt)
        if (onPayRentClick != null) {
          Button(
            onClick = onPayRentClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("detail_pay_rent_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pay Room Rent (किराया भरें व रसीद लें)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        // In-App Chat & Inquire Action
        if (onMessageClick != null) {
          FilledTonalButton(
            onClick = onMessageClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("detail_chat_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          ) {
            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("In-App Chat / Inquire (मैसेज करें)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        // Pinned Contact Row (Call & WhatsApp)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = {
              dialPhoneNumber(context, room.ownerPhone)
            },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("detail_call_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CallBlue)
          ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Call Malik", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              openWhatsAppChat(context, room.ownerWhatsapp, room.title, room.rentPerMonth, room.city)
            },
            modifier = Modifier
              .weight(1.3f)
              .height(50.dp)
              .testTag("detail_whatsapp_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
          ) {
            Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("WhatsApp Chat", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
private fun TermCard(
  label: String,
  value: String,
  subtext: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 1
      )
      Text(
        text = subtext,
        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
      )
    }
  }
}

fun openMapLocation(context: Context, locationQuery: String) {
  try {
    val encoded = URLEncoder.encode(locationQuery, "UTF-8")
    val uri = Uri.parse("geo:0,0?q=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(context, "Could not open map: $locationQuery", Toast.LENGTH_SHORT).show()
  }
}

fun shareRoomListing(context: Context, room: RoomListingEntity) {
  val shareText = """
    🏠 Room Available for Rent on 'Mai Rul' App!
    
    📍 ${room.title}
    💰 Rent: ₹${room.rentPerMonth}/month | Deposit: ₹${room.securityDeposit}
    📌 Location: ${room.area}, ${room.city} (Near: ${room.nearbyLandmark})
    👥 Suitable for: ${room.suitableFor} (${room.roomType})
    ✨ Amenities: ${room.amenities.joinToString(", ")}
    
    📞 Contact Owner (${room.ownerName}): +91 ${room.ownerPhone}
    
    Find student rooms and flats on Mai Rul App!
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "Room Rent Listing: ${room.title}")
    putExtra(Intent.EXTRA_TEXT, shareText)
  }
  context.startActivity(Intent.createChooser(intent, "Share Room via"))
}
