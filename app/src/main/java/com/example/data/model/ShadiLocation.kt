package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "shadi_locations")
data class ShadiLocationEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String, // e.g. "Grand Wedding & Preetibhoj (शादी व रिसेप्शन)"
  val venueName: String, // e.g. "Rajwada Marriage Garden"
  val city: String, // e.g. "Kota"
  val area: String, // e.g. "Rajeev Gandhi Nagar"
  val landmark: String, // e.g. "Near City Mall & Allen Samyak"
  val dateStr: String, // e.g. "Today, 28 Oct 2026"
  val dayOfWeek: String, // e.g. "Wednesday"
  val functionType: String = "Wedding (शादी)", // Wedding (शादी), Reception (प्रीतिभोज), Tilak (तिलक), Feast (दावत)
  val timing: String = "7:30 PM - 11:30 PM",
  val addedByStudent: String = "Kota Student",
  val studentNote: String = "Huge buffet dinner & desserts, spacious lawn, easy entry from Gate 1",
  val timestamp: Long = System.currentTimeMillis(),
  val isToday: Boolean = true
) {
  fun toShareableText(): String {
    return """
🎉 SHADI SAMAROH & FUNCTION UPDATE (शादी लोकेशन):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📍 Venue: $venueName
🏠 Area: $area, $city (Near $landmark)
🎉 Event: $title ($functionType)
🗓️ Date & Day: $dayOfWeek, $dateStr
⏰ Timing: $timing
📝 Student Tips: $studentNote
👤 Added by: $addedByStudent

Shared via My Room - Student Room & Community App!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    """.trimIndent()
  }

  companion object {
    fun getCurrentDateAndDay(): Pair<String, String> {
      val now = Date()
      val dateStr = "Today, " + SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(now)
      val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(now)
      return Pair(dateStr, dayOfWeek)
    }
  }
}
