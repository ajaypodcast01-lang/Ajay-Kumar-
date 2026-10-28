package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomListingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.RoomPhotoView
import com.example.ui.components.formatRent
import com.example.ui.theme.EmeraldSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayRentDialog(
  room: RoomListingEntity,
  activeUser: UserEntity,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onPaySuccess: (
    room: RoomListingEntity,
    amount: Int,
    month: String,
    method: String,
    notes: String
  ) -> Unit
) {
  val context = LocalContext.current
  var rentAmountStr by remember { mutableStateOf("${room.rentPerMonth}") }
  val currentMonthStr = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
  var selectedMonth by remember { mutableStateOf(currentMonthStr) }

  val paymentMethods = listOf(
    PaymentOption("UPI (GPay / PhonePe / Paytm)", Icons.Default.QrCode2),
    PaymentOption("Net Banking (IMPS/NEFT)", Icons.Default.AccountBalance),
    PaymentOption("Debit / Credit Card", Icons.Default.CreditCard),
    PaymentOption("Direct Cash to Landlord", Icons.Default.Money)
  )
  var selectedMethod by remember { mutableStateOf(paymentMethods.first().title) }
  var noteText by remember { mutableStateOf("") }
  var isProcessing by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("pay_rent_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Pay Room Rent (किराया भरें)",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "Direct payment to room owner with instant official receipt",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Room & Owner Summary Card
      Card(
        shape = RoundedCornerShape(14.dp),
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
              .size(65.dp)
              .clip(RoundedCornerShape(10.dp))
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = room.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              text = "Owner: ${room.ownerName} • Phone: +91 ${room.ownerPhone}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = "${room.area}, ${room.city}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Rent Amount Input
      Text(
        text = "Rent Amount (किराया राशि):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = rentAmountStr,
        onValueChange = { rentAmountStr = it.filter { ch -> ch.isDigit() } },
        leadingIcon = {
          Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
        },
        label = { Text("Rent Amount in INR *") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rent_amount_input"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Month Selection
      Text(
        text = "Rent for Month (किस महीने का किराया):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      val months = listOf(currentMonthStr, "November 2026", "December 2026", "Security Deposit")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        months.take(3).forEach { m ->
          val isSelected = selectedMonth == m
          FilterChip(
            selected = isSelected,
            onClick = { selectedMonth = m },
            label = { Text(m, fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Payment Method Options
      Text(
        text = "Select Payment Method (भुगतान माध्यम):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      paymentMethods.forEach { method ->
        val isSelected = selectedMethod == method.title
        Surface(
          onClick = { selectedMethod = method.title },
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("payment_method_${method.title.take(3)}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = method.icon,
              contentDescription = null,
              tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = method.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.weight(1f)
            )
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Optional Notes
      OutlinedTextField(
        value = noteText,
        onValueChange = { noteText = it },
        label = { Text("Note (Optional, e.g. Room 204 or Advance)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Pay Button
      val rentAmount = rentAmountStr.toIntOrNull() ?: 0
      Button(
        onClick = {
          if (rentAmount <= 0) {
            Toast.makeText(context, "Please enter valid rent amount", Toast.LENGTH_SHORT).show()
            return@Button
          }
          isProcessing = true
          onPaySuccess(room, rentAmount, selectedMonth, selectedMethod, noteText)
          Toast.makeText(context, "Rent Paid Successfully! Receipt Generated.", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("confirm_pay_rent_button"),
        shape = RoundedCornerShape(12.dp),
        enabled = !isProcessing && rentAmount > 0
      ) {
        Icon(imageVector = Icons.Default.Payment, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Pay ₹${formatRent(rentAmount)} & Get Receipt",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(26.dp))
    }
  }
}

private data class PaymentOption(
  val title: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)
