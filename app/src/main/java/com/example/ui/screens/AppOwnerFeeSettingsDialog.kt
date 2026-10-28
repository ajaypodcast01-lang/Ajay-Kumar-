package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PriceCheck
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformFeeManager
import com.example.ui.components.formatRent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOwnerFeeSettingsDialog(
  currentBaseFee: Int,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSaveBaseFee: (Int) -> Unit
) {
  val context = LocalContext.current
  var baseFee by remember { mutableIntStateOf(currentBaseFee) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("app_owner_fee_settings_dialog")
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
            text = "App Owner Fee Rules (फीस निर्धारण)",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "Platform fee collected according to location & room type",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Base Fee Selector
      Text(
        text = "Select Base Monthly Platform Fee (मूल मासिक दर):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(6.dp))

      val baseOptions = listOf(200, 250, 300, 350)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        baseOptions.forEach { fee ->
          val isSelected = baseFee == fee
          FilterChip(
            selected = isSelected,
            onClick = { baseFee = fee },
            label = {
              Text(
                text = "₹$fee / mo" + if (fee == 250) " (Standard)" else "",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Location / City Adjustment Rules
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Fee According to Location (शहर अनुसार दर):",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          TierRow(
            tier = "Delhi / NCR (Metro Hub)",
            formula = "Base + ₹100",
            total = "₹${baseFee + 100}/mo"
          )
          TierRow(
            tier = "Kota / Pune (Coaching / Edu Hub)",
            formula = "Base + ₹50",
            total = "₹${baseFee + 50}/mo"
          )
          TierRow(
            tier = "Patna / Prayagraj / Indore (Tier-2)",
            formula = "Base Standard",
            total = "₹${baseFee}/mo"
          )
          TierRow(
            tier = "Other Towns / Tier-3",
            formula = "Base - ₹50 (Subsidized)",
            total = "₹${(baseFee - 50).coerceAtLeast(150)}/mo"
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Room Type Adjustments
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Fee According to Room Type (कमरे अनुसार):",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          TierRow(
            tier = "Single Study Room",
            formula = "Standard Base",
            total = "+₹0"
          )
          TierRow(
            tier = "1 RK Flat / Apartment",
            formula = "Premium Listing",
            total = "+₹50"
          )
          TierRow(
            tier = "1 BHK Flat",
            formula = "Multi-room",
            total = "+₹80"
          )
          TierRow(
            tier = "Shared Budget (2-Bed)",
            formula = "Budget Friendly",
            total = "-₹30"
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          onSaveBaseFee(baseFee)
          Toast.makeText(context, "App Owner Platform Fee Rules Updated (Base: ₹$baseFee)!", Toast.LENGTH_SHORT).show()
          onDismiss()
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("save_fee_rules_button")
      ) {
        Icon(imageVector = Icons.Default.PriceCheck, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Platform Fee Rules", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun TierRow(tier: String, formula: String, total: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = tier, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Text(text = formula, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Text(text = total, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
  }
}
