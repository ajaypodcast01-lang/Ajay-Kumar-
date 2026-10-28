package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RentReceiptEntity
import com.example.ui.components.AppLogoView
import com.example.ui.components.formatRent
import com.example.ui.theme.EmeraldSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentReceiptViewDialog(
  receipt: RentReceiptEntity,
  sheetState: SheetState,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val isPlatformFee = receipt.receiptType == RentReceiptEntity.TYPE_PLATFORM_FEE

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("receipt_detail_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      // Top actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          AppLogoView(size = 32.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "My Room Receipt",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // The Official Voucher Container
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(16.dp)
          )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Receipt Header & Verification Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isPlatformFee) "PLATFORM LISTING RECEIPT" else "OFFICIAL ROOM RENT RECEIPT",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              Text(
                text = if (isPlatformFee) "प्लेटफ़ॉर्म फीस रसीद (App Owner)" else "कमरा किराया भुगतान रसीद",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Ref No: ${receipt.receiptNo}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
            }

            // Green Paid Stamp
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = EmeraldSuccess.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.6f))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = EmeraldSuccess,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "PAID",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = EmeraldSuccess
                )
              }
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

          // Big Amount Display
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "₹${formatRent(receipt.amount)}",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = RentReceiptEntity.formatAmountInWords(receipt.amount),
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
              ),
              textAlign = TextAlign.Center
            )
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.padding(top = 8.dp)
            ) {
              Text(
                text = "🗓️ Payment of Month: ${receipt.rentMonth}",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

          // Date, Time, Day Row (Mandatory user requirement)
          Text(
            text = "DATE, TIME & DAY (समय व दिन):",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CalendarToday,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = receipt.dateStr,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = receipt.timeStr,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = receipt.dayOfWeek,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.secondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Room Owner & Tenant linked details
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // ROOM OWNER
            PartyBox(
              roleTag = if (isPlatformFee) "RECEIVER (App Owner)" else "ROOM OWNER (कमरा मालिक का नाम)",
              name = receipt.receiverName,
              phone = receipt.receiverPhone,
              modifier = Modifier.weight(1f)
            )

            // TENANT / SENDER
            PartyBox(
              roleTag = if (isPlatformFee) "SENDER (Room Owner)" else "TENANT / SENDER (किरायेदार)",
              name = receipt.senderName,
              phone = receipt.senderPhone,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Name of Room or Hostel
          if (!isPlatformFee && receipt.roomTitle.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Home,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "NAME OF ROOM OR HOSTEL (कमरा / हॉस्टल का नाम):",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                  Text(
                    text = receipt.roomTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold)
                  )
                  if (receipt.roomAddress.isNotBlank()) {
                    Text(
                      text = receipt.roomAddress,
                      style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          // Payment Method & Ref Details
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Payment,
                  contentDescription = null,
                  tint = EmeraldSuccess,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Payment Method: ${receipt.paymentMethod}",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Transaction ID: ${receipt.transactionRef}",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Digital verification stamp
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = EmeraldSuccess,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Digitally verified by My Room App • Valid for Tax/HRA Exemption",
              fontSize = 11.sp,
              color = EmeraldSuccess,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Prominent "Thank You" Banner (User Requirement)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "🙏 THANK YOU! (धन्यवाद!)",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 0.5.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              Text(
                text = "Thank you for your payment and using My Room app!",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Share Receipt Anywhere Button (User Requirement)
      Button(
        onClick = {
          shareReceiptEverywhere(context, receipt)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("share_receipt_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Share Receipt Anywhere (रसीद शेयर करें)",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Copy text button
      OutlinedButton(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("Rent Receipt", receipt.toShareableText())
          clipboard.setPrimaryClip(clip)
          Toast.makeText(context, "Receipt text copied to clipboard!", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("copy_receipt_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Copy Full Receipt Text", fontSize = 13.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PartyBox(
  roleTag: String,
  name: String,
  phone: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surface,
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = roleTag,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = name,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        maxLines = 1
      )
      Text(
        text = if (phone.startsWith("+91") || phone.contains("@")) phone else "+91 $phone",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

fun shareReceiptEverywhere(context: Context, receipt: RentReceiptEntity) {
  val shareText = receipt.toShareableText()
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "My Room Rent Receipt - ${receipt.receiptNo}")
    putExtra(Intent.EXTRA_TEXT, shareText)
  }
  context.startActivity(Intent.createChooser(intent, "Share Rent Receipt via"))
}
