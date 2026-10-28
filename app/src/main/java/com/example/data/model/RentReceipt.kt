package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Entity(tableName = "rent_receipts")
data class RentReceiptEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val receiptNo: String,
  val receiptType: String = TYPE_ROOM_RENT, // TYPE_ROOM_RENT or TYPE_PLATFORM_FEE
  val roomId: Long = 0,
  val roomTitle: String = "",
  val roomAddress: String = "",
  // Sender (Tenant/Student or Room Owner)
  val senderName: String,
  val senderPhone: String,
  val senderRole: String = "Tenant / Student",
  // Receiver (Room Owner or App Owner)
  val receiverName: String,
  val receiverPhone: String,
  val receiverRole: String = "Room Malik",
  // Payment info
  val amount: Int,
  val paymentMethod: String, // UPI, Net Banking, Card, Cash
  val transactionRef: String,
  val rentMonth: String, // e.g. "October 2026"
  // Linked timestamp details
  val dateStr: String, // e.g. "28 Oct 2026"
  val timeStr: String, // e.g. "03:30 PM"
  val dayOfWeek: String, // e.g. "Wednesday"
  val timestamp: Long = System.currentTimeMillis(),
  val status: String = "PAID & VERIFIED",
  val notes: String = ""
) {
  companion object {
    const val TYPE_ROOM_RENT = "ROOM_RENT"
    const val TYPE_PLATFORM_FEE = "PLATFORM_FEE"

    fun generateReceiptNo(prefix: String = "MR-REC"): String {
      val datePart = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date())
      val randomDigits = Random.nextInt(1000, 9999)
      return "$prefix-$datePart-$randomDigits"
    }

    fun getCurrentDateTimeParts(): Triple<String, String, String> {
      val now = Date()
      val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(now)
      val timeStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(now)
      val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(now)
      return Triple(dateStr, timeStr, dayOfWeek)
    }

    fun formatAmountInWords(amount: Int): String {
      return try {
        val units = arrayOf(
          "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
          "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
          "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
          "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        fun convertLessThanThousand(number: Int): String {
          var num = number
          var current = ""
          if (num % 100 < 20) {
            current = units[num % 100]
            num /= 100
          } else {
            current = units[num % 10]
            num /= 10
            current = tens[num % 10] + if (current.isNotEmpty()) " $current" else ""
            num /= 10
          }
          if (num == 0) return current
          return units[num] + " Hundred" + if (current.isNotEmpty()) " and $current" else ""
        }

        if (amount == 0) return "Zero Rupees"
        var num = amount
        var prefix = ""
        if (num < 0) {
          num = -num
          prefix = "Negative "
        }
        var result = ""
        if (num >= 100000) {
          result += convertLessThanThousand(num / 100000) + " Lakh "
          num %= 100000
        }
        if (num >= 1000) {
          result += convertLessThanThousand(num / 1000) + " Thousand "
          num %= 1000
        }
        if (num > 0) {
          result += convertLessThanThousand(num)
        }
        "$prefix${result.trim()} Rupees Only"
      } catch (e: Exception) {
        "₹$amount Only"
      }
    }
  }

  fun toShareableText(): String {
    val isPlatformFee = receiptType == TYPE_PLATFORM_FEE
    val title = if (isPlatformFee) {
      "MAI RUL - ROOM OWNER PLATFORM FEE RECEIPT (प्लेटफ़ॉर्म फीस रसीद)"
    } else {
      "MAI RUL - OFFICIAL ROOM RENT RECEIPT (किराया रसीद)"
    }

    return """
════════════════════════════════════
🧾 $title
════════════════════════════════════
🔖 Receipt No: $receiptNo
✅ Status: $status

📅 Date: $dateStr
🕒 Time: $timeStr
🗓️ Day: $dayOfWeek

📅 PAYMENT FOR MONTH: $rentMonth
💰 AMOUNT PAID: ₹$amount
(${formatAmountInWords(amount)})

🏢 ROOM OWNER (कमरा मालिक का नाम):
• Owner Name: $receiverName
• Contact / Mobile: +91 $receiverPhone

${if (!isPlatformFee && roomTitle.isNotBlank()) "🏨 NAME OF ROOM OR HOSTEL (कमरा या हॉस्टल का नाम):\n• Room / Hostel: $roomTitle\n• Location: $roomAddress\n" else ""}
👤 SENDER / TENANT (किरायेदार/छात्र):
• Tenant Name: $senderName
• Mobile: +91 $senderPhone

💳 Payment Method: $paymentMethod
🔖 Transaction / Ref ID: $transactionRef
${if (notes.isNotBlank()) "📝 Notes: $notes\n" else ""}
------------------------------------
🙏 THANK YOU! (धन्यवाद!)
Thank you for your payment and trust with My Room!
✨ Digitally Verified & Generated via My Room App
Zero Brokerage Student Room & Rent Platform
════════════════════════════════════
    """.trimIndent()
  }
}

object PlatformFeeManager {
  const val DEFAULT_BASE_FEE = 250
  const val APP_OWNER_NAME = "Mai Rul App Platform Owner"
  const val APP_OWNER_PHONE = "9800011122"
  const val APP_OWNER_UPI = "mairul.admin@okaxis"
  const val APP_OWNER_EMAIL = "admin@mairul.app"

  fun getCityAdjustment(city: String): Int {
    return when (city.trim().lowercase()) {
      "delhi" -> 100 // Metro Tier: ₹250 + ₹100 = ₹350
      "kota" -> 50   // Coaching Hub: ₹250 + ₹50 = ₹300
      "pune" -> 50   // Education Hub: ₹250 + ₹50 = ₹300
      "jaipur" -> 30 // ₹280
      "patna" -> 0   // ₹250
      "prayagraj" -> 0 // ₹250
      "indore" -> 0  // ₹250
      "lucknow" -> 0 // ₹250
      else -> -50    // Tier 3/Other towns: ₹200
    }
  }

  fun getRoomTypeAdjustment(roomType: String): Int {
    return when (roomType.trim().lowercase()) {
      "single room" -> 0
      "shared (2-bed)" -> -30 // Discount for budget sharing
      "1 rk flat" -> 50
      "1 bhk flat" -> 80
      "pg room" -> 30
      else -> 0
    }
  }

  fun calculateOwnerFee(city: String, roomType: String = "Single Room", customBase: Int = DEFAULT_BASE_FEE): Int {
    val fee = customBase + getCityAdjustment(city) + getRoomTypeAdjustment(roomType)
    return fee.coerceAtLeast(150)
  }
}
