package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformFeeManager
import com.example.data.model.RoomListingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.RoomPhotoView
import com.example.ui.components.formatRent
import com.example.ui.theme.EmeraldSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OwnerDashboardScreen(
  ownerRooms: List<RoomListingEntity>,
  activeUser: UserEntity,
  customBaseFee: Int,
  onAddNewRoom: () -> Unit,
  onEditRoom: (RoomListingEntity) -> Unit,
  onAdjustRent: (RoomListingEntity) -> Unit,
  onQuickRentDelta: (Long, Int, Int) -> Unit, // roomId, currentRent, delta
  onToggleVacancy: (Long, Boolean) -> Unit,
  onDeleteRoom: (Long) -> Unit,
  onRoomClick: (RoomListingEntity) -> Unit,
  onPayPlatformFee: () -> Unit,
  onOpenFeeSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var roomToDelete by remember { mutableStateOf<RoomListingEntity?>(null) }

  val totalRooms = ownerRooms.size
  val vacantRooms = ownerRooms.count { it.isVacant }
  val rentedRooms = totalRooms - vacantRooms
  val totalPotentialRent = ownerRooms.sumOf { it.rentPerMonth }

  val city = activeUser.city.ifBlank { "Kota" }
  val platformFee = PlatformFeeManager.calculateOwnerFee(city, "Single Room", customBaseFee)
  val isSubActive = activeUser.isSubscriptionActive && (activeUser.subscriptionPaidUntil > System.currentTimeMillis())
  val expiryDateStr = if (isSubActive) {
    SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(activeUser.subscriptionPaidUntil))
  } else "Expired"

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("owner_dashboard_screen")
  ) {
    // Top Dashboard Stats & Platform Fee Card
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "कमरा मालिक डैशबोर्ड",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "My Room Property & Rent Management",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        // Post New Room Button
        Button(
          onClick = onAddNewRoom,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("post_room_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Upload Room", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // PLATFORM FEE & APP OWNER COLLECTION BANNER (User Requirement)
      ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = if (isSubActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          } else {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
          }
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isSubActive) Icons.Default.Verified else Icons.Default.CreditCard,
                contentDescription = null,
                tint = if (isSubActive) EmeraldSuccess else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Monthly Platform Listing Fee",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSubActive) EmeraldSuccess.copy(alpha = 0.18f) else MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
            ) {
              Text(
                text = if (isSubActive) "ACTIVE (Till $expiryDateStr)" else "DUE: ₹$platformFee/mo",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSubActive) EmeraldSuccess else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Fee of ₹$platformFee/month is collected by App Owner (Mai Rul Platform) according to $city location.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onPayPlatformFee,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1.3f)
                .testTag("pay_platform_fee_banner_btn")
            ) {
              Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Pay ₹$platformFee Fee to App Owner", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = onOpenFeeSettings,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(0.9f)
            ) {
              Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Fee Rates", fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Stats Cards Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatCard(
          title = "Total Rooms",
          value = "$totalRooms",
          subtitle = "₹${formatRent(totalPotentialRent)}/mo",
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "Vacant (खाली)",
          value = "$vacantRooms",
          subtitle = "Ready to rent",
          color = EmeraldSuccess,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "Rented (किराएदार)",
          value = "$rentedRooms",
          subtitle = "Occupied",
          color = Color(0xFF64748B),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Property Listings Section Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Your Listed Rooms (कमरे व किराया)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Text(
        text = "$totalRooms Properties",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
    }

    if (ownerRooms.isEmpty()) {
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
              imageVector = Icons.Default.MeetingRoom,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Abhi tak koi room upload nahi kiya",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Upload room photos and details. Students can find your room and pay rent directly inside the app.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(horizontal = 24.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onAddNewRoom,
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Upload First Room (पहला कमरा जोड़ें)")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(
          items = ownerRooms,
          key = { it.id }
        ) { room ->
          OwnerRoomCardWithRentControls(
            room = room,
            onToggleVacancy = { isVacant -> onToggleVacancy(room.id, isVacant) },
            onEdit = { onEditRoom(room) },
            onAdjustRent = { onAdjustRent(room) },
            onQuickRentDelta = { delta -> onQuickRentDelta(room.id, room.rentPerMonth, delta) },
            onDelete = { roomToDelete = room },
            onClick = { onRoomClick(room) }
          )
        }
      }
    }
  }

  // Delete Confirmation Dialog
  roomToDelete?.let { targetRoom ->
    AlertDialog(
      onDismissRequest = { roomToDelete = null },
      title = { Text("Delete Room Listing?") },
      text = { Text("Are you sure you want to delete '${targetRoom.title}'? This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            onDeleteRoom(targetRoom.id)
            roomToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { roomToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun StatCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = color
      )
      Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
    }
  }
}

@Composable
private fun OwnerRoomCardWithRentControls(
  room: RoomListingEntity,
  onToggleVacancy: (Boolean) -> Unit,
  onEdit: () -> Unit,
  onAdjustRent: () -> Unit,
  onQuickRentDelta: (Int) -> Unit,
  onDelete: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("owner_room_card_${room.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RoomPhotoView(
          drawableName = room.photoDrawableName,
          contentDescription = room.title,
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = room.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${room.area}, ${room.city} • ${room.roomType}",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (room.isVacant) EmeraldSuccess.copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.15f)
          ) {
            Text(
              text = if (room.isVacant) "Vacant (खाली है)" else "Rented (किराए पर है)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (room.isVacant) EmeraldSuccess else Color(0xFF64748B),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

      // RENT INCREASE / DECREASE CONTROLS (User Requirement)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Monthly Rent (किराया):",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "₹${formatRent(room.rentPerMonth)}/mo",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }

          // Stepper +/- buttons & Adjust Modal button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Decrease rent by ₹200
            FilledTonalIconButton(
              onClick = { onQuickRentDelta(-200) },
              modifier = Modifier
                .size(32.dp)
                .testTag("quick_decrease_rent_${room.id}")
            ) {
              Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease rent", modifier = Modifier.size(16.dp))
            }

            // Increase rent by ₹200
            FilledTonalIconButton(
              onClick = { onQuickRentDelta(200) },
              modifier = Modifier
                .size(32.dp)
                .testTag("quick_increase_rent_${room.id}")
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Increase rent", modifier = Modifier.size(16.dp))
            }

            // More adjustment options button
            FilledTonalButton(
              onClick = onAdjustRent,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.testTag("adjust_rent_btn_${room.id}")
            ) {
              Text("Change Rent", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Vacancy Toggle & Management actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(
            checked = room.isVacant,
            onCheckedChange = onToggleVacancy,
            modifier = Modifier.testTag("vacancy_switch_${room.id}")
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (room.isVacant) "Vacant" else "Rented",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (room.isVacant) EmeraldSuccess else Color(0xFF64748B)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onEdit,
            modifier = Modifier.testTag("edit_room_${room.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_room_${room.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
          }

          FilledTonalButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Preview", fontSize = 11.sp)
          }
        }
      }
    }
  }
}
