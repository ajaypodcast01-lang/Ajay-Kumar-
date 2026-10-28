package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomListingEntity
import com.example.ui.components.RoomPhotoView
import com.example.ui.components.formatRent
import com.example.ui.theme.EmeraldSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustRentDialog(
  room: RoomListingEntity,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSaveRent: (newRent: Int) -> Unit
) {
  val context = LocalContext.current
  var currentRent by remember(room) { mutableIntStateOf(room.rentPerMonth) }
  var rentInputText by remember(room) { mutableStateOf("${room.rentPerMonth}") }

  fun applyRentChange(newAmount: Int) {
    val clamped = newAmount.coerceAtLeast(500)
    currentRent = clamped
    rentInputText = "$clamped"
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("adjust_rent_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Increase or Decrease Rent",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "कमरे का किराया घटाएं या बढ़ाएं",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Room Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RoomPhotoView(
            drawableName = room.photoDrawableName,
            contentDescription = room.title,
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(8.dp))
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = room.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              text = "${room.area}, ${room.city}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = "Current: ₹${formatRent(room.rentPerMonth)}/mo",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Stepper & Display Row
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Decrease Button
          FilledTonalIconButton(
            onClick = { applyRentChange(currentRent - 200) },
            modifier = Modifier
              .size(46.dp)
              .testTag("decrease_rent_button")
          ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease rent")
          }

          // Central Rent Display
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "New Monthly Rent",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "₹${formatRent(currentRent)}",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            val diff = currentRent - room.rentPerMonth
            if (diff != 0) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (diff > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                  contentDescription = null,
                  tint = if (diff > 0) MaterialTheme.colorScheme.primary else EmeraldSuccess,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (diff > 0) "+₹$diff" else "-₹${-diff}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (diff > 0) MaterialTheme.colorScheme.primary else EmeraldSuccess
                )
              }
            }
          }

          // Increase Button
          FilledTonalIconButton(
            onClick = { applyRentChange(currentRent + 200) },
            modifier = Modifier
              .size(46.dp)
              .testTag("increase_rent_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase rent")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Adjustment Chips
      Text(
        text = "Quick Adjust Rent (त्वरित बदलाव):",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        FilledTonalButton(
          onClick = { applyRentChange(currentRent - 500) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("- ₹500", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        FilledTonalButton(
          onClick = { applyRentChange(currentRent - 200) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("- ₹200", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        FilledTonalButton(
          onClick = { applyRentChange(currentRent + 200) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("+ ₹200", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        FilledTonalButton(
          onClick = { applyRentChange(currentRent + 500) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("+ ₹500", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Direct Input
      OutlinedTextField(
        value = rentInputText,
        onValueChange = { input ->
          val digits = input.filter { it.isDigit() }
          rentInputText = digits
          digits.toIntOrNull()?.let { currentRent = it }
        },
        label = { Text("Exact Rent Amount (₹)") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("exact_rent_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Save Button
      Button(
        onClick = {
          if (currentRent < 500) {
            Toast.makeText(context, "Minimum rent must be at least ₹500", Toast.LENGTH_SHORT).show()
            return@Button
          }
          onSaveRent(currentRent)
          Toast.makeText(context, "Rent updated to ₹$currentRent/month!", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("save_rent_adjustment_button")
      ) {
        Text(
          text = "Save Updated Rent (₹${formatRent(currentRent)})",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
