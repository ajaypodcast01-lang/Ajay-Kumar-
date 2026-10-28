package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_listings")
data class RoomListingEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val ownerId: String,
  val ownerName: String,
  val ownerPhone: String,
  val ownerWhatsapp: String,
  val title: String,
  val description: String,
  val rentPerMonth: Int,
  val securityDeposit: Int,
  val electricityCharge: String = "Metered (₹8/unit)",
  val roomType: String = "Single Room", // Single Room, Shared (2-Bed), 1 RK Flat, 1 BHK Flat, PG Room
  val suitableFor: String = "Students Only", // Students Only, Boys Only, Girls Only, Anyone
  val city: String,
  val area: String,
  val fullAddress: String,
  val nearbyLandmark: String,
  val amenities: List<String>,
  val photoDrawableName: String = "room_single_study_1793006717976", // fallback or preset
  val isVacant: Boolean = true,
  val noticePeriodDays: Int = 30,
  val gateClosingTime: String = "10:30 PM",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites", primaryKeys = ["roomId", "userId"])
data class FavoriteEntity(
  val roomId: Long,
  val userId: String
)

object RoomConstants {
  val CITIES = listOf(
    "Kota",
    "Delhi",
    "Patna",
    "Prayagraj",
    "Indore",
    "Pune",
    "Jaipur",
    "Lucknow"
  )

  val ROOM_TYPES = listOf(
    "All Types",
    "Single Room",
    "Shared (2-Bed)",
    "1 RK Flat",
    "1 BHK Flat",
    "PG Room"
  )

  val SUITABILITY_OPTIONS = listOf(
    "All",
    "Students Only",
    "Boys Only",
    "Girls Only",
    "Anyone"
  )

  val ALL_AMENITIES = listOf(
    "WiFi",
    "RO Water",
    "Attached Washroom",
    "Study Table",
    "Bed & Mattress",
    "AC / Air Cooler",
    "Food / Mess",
    "Almirah",
    "Geyser",
    "No Gate Restriction",
    "Bike Parking",
    "CCTV Security"
  )

  val PHOTO_PRESETS = listOf(
    PhotoPreset("room_single_study_1793006717976", "Single Study Room (Single Bed + Desk)"),
    PhotoPreset("room_deluxe_pg_1793006751063", "Deluxe Twin PG (Twin Beds + AC)"),
    PhotoPreset("room_apartment_1rk_1793006771068", "Modern 1RK (Room + Kitchenette)")
  )
}

data class PhotoPreset(
  val drawableName: String,
  val label: String
)
