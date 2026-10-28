package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
  ROOM_OWNER, // कमरा मालिक
  TENANT      // छात्र / किरायेदार
}

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey
  val id: String = "user_default",
  val role: UserRole = UserRole.TENANT,
  val name: String = "Rahul Sharma",
  val phone: String = "9876543210",
  val whatsapp: String = "9876543210",
  val email: String = "rahul.student@example.com",
  val city: String = "Kota",
  val isLoggedIn: Boolean = true,
  // Privacy setting: Personal number of students can't show to other students without personal choice
  val showPhoneToOthers: Boolean = false,
  // Platform fee subscription (For Room Owner paid to App Owner)
  val isSubscriptionActive: Boolean = true,
  val subscriptionPaidUntil: Long = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
  val lastSubscriptionPaidAt: Long = System.currentTimeMillis(),
  val ownerUpiId: String = "owner.sharma@okaxis",
  // Specific to Tenant / Student
  val collegeOrCoaching: String = "Allen Career Institute",
  val preferredBudgetMin: Int = 2000,
  val preferredBudgetMax: Int = 7500,
  val preferredRoomType: String = "Single Room",
  val preferredAmenities: List<String> = listOf("WiFi", "RO Water", "Study Table"),
  // Specific to Room Owner
  val propertyName: String = "Sharma Niwas Student PG & Rooms",
  val ownerBio: String = "Owner of 12 furnished student rooms near coaching hub. Quiet, safe & CCTV protected.",
  val callingHours: String = "8:00 AM - 9:00 PM"
) {
  fun getDisplayPhone(isViewingOwnProfileOrMalik: Boolean = false): String {
    if (role == UserRole.ROOM_OWNER || showPhoneToOthers || isViewingOwnProfileOrMalik) {
      return phone
    }
    // Mask student number for other students
    return if (phone.length >= 6) {
      phone.take(2) + "••••••" + phone.takeLast(2)
    } else {
      "••••••"
    }
  }
}
