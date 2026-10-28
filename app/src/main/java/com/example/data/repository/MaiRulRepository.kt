package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.PlatformFeeManager
import com.example.data.model.RentReceiptEntity
import com.example.data.model.RoomListingEntity
import com.example.data.model.ShadiLocationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaiRulRepository(private val database: AppDatabase) {
  private val userDao = database.userDao()
  private val roomDao = database.roomListingDao()
  private val chatDao = database.chatMessageDao()
  private val receiptDao = database.rentReceiptDao()
  private val shadiDao = database.shadiLocationDao()

  val activeUser: Flow<UserEntity?> = userDao.getActiveUser()
  val allListings: Flow<List<RoomListingEntity>> = roomDao.getAllListings()
  val allReceipts: Flow<List<RentReceiptEntity>> = receiptDao.getAllReceipts()
  val allShadiLocations: Flow<List<ShadiLocationEntity>> = shadiDao.getAllShadiLocations()

  fun getConversation(roomId: Long, otherUserId: String): Flow<List<ChatMessageEntity>> {
    return chatDao.getConversation(roomId, "user_default", otherUserId)
  }

  fun getAllUserMessages(userId: String): Flow<List<ChatMessageEntity>> {
    return chatDao.getAllUserMessages(userId)
  }

  fun getUnreadCount(userId: String): Flow<Int> {
    return chatDao.getUnreadCount(userId)
  }

  suspend fun sendMessage(message: ChatMessageEntity): Long = withContext(Dispatchers.IO) {
    chatDao.insertMessage(message)
  }

  suspend fun markChatAsRead(roomId: Long, currentUserId: String) = withContext(Dispatchers.IO) {
    chatDao.markAsRead(roomId, currentUserId)
  }

  fun getOwnerListings(ownerId: String): Flow<List<RoomListingEntity>> {
    return roomDao.getOwnerListings(ownerId)
  }

  fun getFavoriteRoomIds(userId: String): Flow<List<Long>> {
    return roomDao.getFavoriteRoomIds(userId)
  }

  fun getFavoriteRooms(userId: String): Flow<List<RoomListingEntity>> {
    return roomDao.getFavorites(userId)
  }

  suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
    userDao.insertOrUpdate(user)
  }

  suspend fun loginOrRegister(name: String, phone: String, role: UserRole, city: String) = withContext(Dispatchers.IO) {
    val existing = userDao.getActiveUser().firstOrNull()
    val updated = (existing ?: UserEntity()).copy(
      id = "user_default",
      name = name.trim().ifBlank { if (role == UserRole.ROOM_OWNER) "Rajesh Kumar (Owner)" else "Rahul Sharma" },
      phone = phone.trim().ifBlank { "9876543210" },
      whatsapp = phone.trim().ifBlank { "9876543210" },
      role = role,
      city = city.ifBlank { "Kota" },
      isLoggedIn = true,
      isSubscriptionActive = true,
      subscriptionPaidUntil = System.currentTimeMillis() + 30L * 24 * 3600 * 1000
    )
    userDao.insertOrUpdate(updated)
  }

  suspend fun logoutUser() = withContext(Dispatchers.IO) {
    val existing = userDao.getActiveUser().firstOrNull()
    if (existing != null) {
      userDao.insertOrUpdate(existing.copy(isLoggedIn = false))
    }
  }

  suspend fun switchUserRole(newRole: UserRole) = withContext(Dispatchers.IO) {
    val current = userDao.getActiveUser().firstOrNull() ?: UserEntity()
    val updated = current.copy(
      role = newRole,
      name = if (newRole == UserRole.ROOM_OWNER) {
        if (current.name.contains("Rahul")) "Rajesh Kumar (Malik)" else current.name
      } else {
        if (current.name.contains("Rajesh")) "Rahul Sharma (Student)" else current.name
      },
      propertyName = if (newRole == UserRole.ROOM_OWNER) "Rajesh Niwas Student Rooms & PG" else current.propertyName
    )
    userDao.insertOrUpdate(updated)
  }

  // Room Owner can increase or decrease room rent
  suspend fun updateRoomRent(roomId: Long, newRent: Int) = withContext(Dispatchers.IO) {
    val safeRent = newRent.coerceAtLeast(500)
    roomDao.updateRent(roomId, safeRent)
  }

  suspend fun addListing(listing: RoomListingEntity): Long = withContext(Dispatchers.IO) {
    roomDao.insertListing(listing)
  }

  suspend fun updateListing(listing: RoomListingEntity) = withContext(Dispatchers.IO) {
    roomDao.updateListing(listing)
  }

  suspend fun updateVacancy(roomId: Long, isVacant: Boolean) = withContext(Dispatchers.IO) {
    roomDao.updateVacancy(roomId, isVacant)
  }

  suspend fun deleteListing(roomId: Long) = withContext(Dispatchers.IO) {
    roomDao.deleteListingById(roomId)
  }

  suspend fun toggleFavorite(roomId: Long, userId: String, isFav: Boolean) = withContext(Dispatchers.IO) {
    if (isFav) {
      roomDao.addFavorite(FavoriteEntity(roomId, userId))
    } else {
      roomDao.removeFavorite(roomId, userId)
    }
  }

  // Rent Receipt creation
  suspend fun addRentReceipt(receipt: RentReceiptEntity): Long = withContext(Dispatchers.IO) {
    receiptDao.insertReceipt(receipt)
  }

  suspend fun deleteRentReceipt(id: Long) = withContext(Dispatchers.IO) {
    receiptDao.deleteReceipt(id)
  }

  // Shadi Location & Functions
  suspend fun addShadiLocation(shadi: ShadiLocationEntity): Long = withContext(Dispatchers.IO) {
    shadiDao.insert(shadi)
  }

  suspend fun deleteShadiLocation(id: Long) = withContext(Dispatchers.IO) {
    shadiDao.deleteById(id)
  }

  // Room Owner pays ₹250 (or location/room dynamic fee) monthly to App Owner
  suspend fun payPlatformListingFee(
    amount: Int,
    paymentMethod: String,
    roomTitle: String = "All Listed Rooms",
    city: String = "Kota"
  ): RentReceiptEntity = withContext(Dispatchers.IO) {
    val current = userDao.getActiveUser().firstOrNull() ?: UserEntity()
    val (dateStr, timeStr, dayOfWeek) = RentReceiptEntity.getCurrentDateTimeParts()
    val receiptNo = RentReceiptEntity.generateReceiptNo("MR-FEE")
    val txRef = "UPI/PLT/${System.currentTimeMillis() % 100000000}/OKAXIS"

    val receipt = RentReceiptEntity(
      receiptNo = receiptNo,
      receiptType = RentReceiptEntity.TYPE_PLATFORM_FEE,
      roomId = 0,
      roomTitle = roomTitle,
      roomAddress = "$city Student Area (Platform Maintenance & Verification)",
      senderName = current.name.ifBlank { "Room Malik" },
      senderPhone = current.phone.ifBlank { "9876543210" },
      senderRole = "Room Malik (Landlord)",
      receiverName = PlatformFeeManager.APP_OWNER_NAME,
      receiverPhone = PlatformFeeManager.APP_OWNER_UPI,
      receiverRole = "App Owner / Mai Rul Platform",
      amount = amount,
      paymentMethod = paymentMethod,
      transactionRef = txRef,
      rentMonth = "Platform Listing Fee (30 Days)",
      dateStr = dateStr,
      timeStr = timeStr,
      dayOfWeek = dayOfWeek,
      timestamp = System.currentTimeMillis(),
      status = "PAID & VERIFIED",
      notes = "Monthly Listing Fee collected by App Owner. Valid for 30 days."
    )
    receiptDao.insertReceipt(receipt)

    // Update user subscription state
    val updatedUser = current.copy(
      isSubscriptionActive = true,
      subscriptionPaidUntil = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
      lastSubscriptionPaidAt = System.currentTimeMillis()
    )
    userDao.insertOrUpdate(updatedUser)

    receipt
  }

  suspend fun initializeSampleDataIfNeeded() = withContext(Dispatchers.IO) {
    val currentCount = roomDao.getCount()
    if (currentCount == 0) {
      val sampleRooms = listOf(
        RoomListingEntity(
          ownerId = "owner_rajesh",
          ownerName = "Rajesh Verma",
          ownerPhone = "9876543210",
          ownerWhatsapp = "9876543210",
          title = "Spacious Single Room near Allen Samyak",
          description = "Furnished peaceful single room for serious NEET/JEE aspirants. Includes solid wooden study desk, chair, single bed with orthopedic mattress, bright sunlight window, and separate electricity sub-meter.",
          rentPerMonth = 4500,
          securityDeposit = 4500,
          electricityCharge = "₹8/unit (Sub-meter)",
          roomType = "Single Room",
          suitableFor = "Students Only",
          city = "Kota",
          area = "Rajeev Gandhi Nagar",
          fullAddress = "Plot 42-B, Sector 2, Rajeev Gandhi Nagar, Kota, Rajasthan",
          nearbyLandmark = "300 meters from Allen Samyak & City Mall",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Study Table", "Bed & Mattress", "AC / Air Cooler", "CCTV Security"),
          photoDrawableName = "room_single_study_1793006717976",
          isVacant = true,
          noticePeriodDays = 30,
          gateClosingTime = "10:30 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_sharma",
          ownerName = "Suresh Sharma",
          ownerPhone = "9811223344",
          ownerWhatsapp = "9811223344",
          title = "Deluxe AC Student Room with High Speed WiFi",
          description = "Newly renovated room in Mukherjee Nagar for UPSC and SSC students. Peaceful study atmosphere, 100 Mbps fiber internet, daily housekeeping of corridors, and 24x7 power backup.",
          rentPerMonth = 7500,
          securityDeposit = 7500,
          electricityCharge = "₹9/unit",
          roomType = "Single Room",
          suitableFor = "Students Only",
          city = "Delhi",
          area = "Mukherjee Nagar",
          fullAddress = "House 18, Street 4, Commercial Complex Side, Mukherjee Nagar, Delhi",
          nearbyLandmark = "Behind Batra Cinema & Drishti IAS",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Study Table", "Bed & Mattress", "AC / Air Cooler", "Almirah", "Geyser"),
          photoDrawableName = "room_deluxe_pg_1793006751063",
          isVacant = true,
          noticePeriodDays = 30,
          gateClosingTime = "11:00 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_mishra",
          ownerName = "Awadhesh Mishra",
          ownerPhone = "9431098765",
          ownerWhatsapp = "9431098765",
          title = "Modern 1RK Student Flat with Kitchen Slab",
          description = "Independent 1 Room-Kitchen set for students or working professionals. Private balcony, kitchen slab with gas cylinder space, attached bathroom, and bike parking inside gated boundary.",
          rentPerMonth = 5200,
          securityDeposit = 5000,
          electricityCharge = "Fixed ₹400/month",
          roomType = "1 RK Flat",
          suitableFor = "Anyone",
          city = "Patna",
          area = "Boring Road",
          fullAddress = "Lane 3, Anandpuri, Boring Road, Patna, Bihar",
          nearbyLandmark = "Near Chauraha & A.N. College",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Bed & Mattress", "Almirah", "Bike Parking", "No Gate Restriction"),
          photoDrawableName = "room_apartment_1rk_1793006771068",
          isVacant = true,
          noticePeriodDays = 30,
          gateClosingTime = "No restriction 24x7"
        ),
        RoomListingEntity(
          ownerId = "owner_kulkarni",
          ownerName = "Vijay Kulkarni",
          ownerPhone = "9822334455",
          ownerWhatsapp = "9822334455",
          title = "Twin Sharing Budget Room with Food & Mess",
          description = "Affordable double sharing room ideal for college students. Two separate single cots, individual study tables and wardrobes. Hygienic two-time homemade vegetarian tiffin/food option available.",
          rentPerMonth = 3800,
          securityDeposit = 3000,
          electricityCharge = "Included in Rent",
          roomType = "Shared (2-Bed)",
          suitableFor = "Boys Only",
          city = "Pune",
          area = "Kothrud",
          fullAddress = "Shastri Nagar, Paud Road, Kothrud, Pune, Maharashtra",
          nearbyLandmark = "500m from MIT World Peace University",
          amenities = listOf("WiFi", "RO Water", "Study Table", "Bed & Mattress", "Food / Mess", "Almirah", "Bike Parking"),
          photoDrawableName = "room_deluxe_pg_1793006751063",
          isVacant = true,
          noticePeriodDays = 15,
          gateClosingTime = "10:30 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_tiwari",
          ownerName = "Geeta Tiwari (Owner)",
          ownerPhone = "9450123456",
          ownerWhatsapp = "9450123456",
          title = "Safe Girls Only Single Room near University",
          description = "Quiet and secure single room strictly for female students preparing for civil services or university exams. CCTV in common areas, RO chilled water on each floor, and in-house warden support.",
          rentPerMonth = 4000,
          securityDeposit = 4000,
          electricityCharge = "₹8/unit",
          roomType = "Single Room",
          suitableFor = "Girls Only",
          city = "Prayagraj",
          area = "Katra",
          fullAddress = "Opposite Katra Police Chowki, University Road, Prayagraj, UP",
          nearbyLandmark = "Walking distance to Allahabad University North Campus",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Study Table", "Bed & Mattress", "Geyser", "CCTV Security"),
          photoDrawableName = "room_single_study_1793006717976",
          isVacant = true,
          noticePeriodDays = 30,
          gateClosingTime = "9:30 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_jain",
          ownerName = "Sunil Jain",
          ownerPhone = "9826011223",
          ownerWhatsapp = "9826011223",
          title = "Clean 1RK Studio Room in Coaching Hub",
          description = "Bright ventilated studio room with attached western washroom, kitchen platform, and private balcony. Located in the heart of Indore coaching area. Daily drinking water supply and zero brokerage.",
          rentPerMonth = 6000,
          securityDeposit = 6000,
          electricityCharge = "₹8.5/unit",
          roomType = "1 RK Flat",
          suitableFor = "Students Only",
          city = "Indore",
          area = "Bhanwarkuan",
          fullAddress = "Sector C, Bholaram Ustad Marg, Bhanwarkuan, Indore, MP",
          nearbyLandmark = "Near Kautilya Academy & Bhanwarkuan Square",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Bed & Mattress", "Almirah", "Bike Parking", "CCTV Security"),
          photoDrawableName = "room_apartment_1rk_1793006771068",
          isVacant = true,
          noticePeriodDays = 30,
          gateClosingTime = "11:00 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_gupta",
          ownerName = "Mahesh Gupta",
          ownerPhone = "9810987654",
          ownerWhatsapp = "9810987654",
          title = "Budget Single Room near Metro Station",
          description = "Low budget independent room for CA/CS/College students. Just 2 minutes walk from Metro. High speed 5G WiFi, 24 hrs running water, and safe residential neighbourhood.",
          rentPerMonth = 3200,
          securityDeposit = 3000,
          electricityCharge = "₹8/unit",
          roomType = "Single Room",
          suitableFor = "Boys Only",
          city = "Delhi",
          area = "Laxmi Nagar",
          fullAddress = "Gali No 2, Guru Nanak Pura, Laxmi Nagar, Delhi",
          nearbyLandmark = "200m from Laxmi Nagar Metro Gate 3",
          amenities = listOf("WiFi", "RO Water", "Study Table", "Bed & Mattress", "Almirah"),
          photoDrawableName = "room_single_study_1793006717976",
          isVacant = true,
          noticePeriodDays = 15,
          gateClosingTime = "11:30 PM"
        ),
        RoomListingEntity(
          ownerId = "owner_yadav",
          ownerName = "Ram Singh Yadav",
          ownerPhone = "9784561230",
          ownerWhatsapp = "9784561230",
          title = "Air-Cooled Single Room near Allen Supath",
          description = "Spacious well-lit room with large desert cooler, wooden study table with bookshelves, large wardrobe with lock, and attached sanitized washroom with geyser. Complete peace for self study.",
          rentPerMonth = 5000,
          securityDeposit = 5000,
          electricityCharge = "₹8/unit",
          roomType = "Single Room",
          suitableFor = "Students Only",
          city = "Kota",
          area = "Coral Park",
          fullAddress = "Behind Allen Supath Campus, Baran Road, Coral Park, Kota, Rajasthan",
          nearbyLandmark = "150m from Allen Supath main gate",
          amenities = listOf("WiFi", "RO Water", "Attached Washroom", "Study Table", "Bed & Mattress", "AC / Air Cooler", "Geyser", "CCTV Security"),
          photoDrawableName = "room_single_study_1793006717976",
          isVacant = false,
          noticePeriodDays = 30,
          gateClosingTime = "10:30 PM"
        )
      )
      roomDao.insertAll(sampleRooms)
    }

    // Default user profile
    val defaultUser = UserEntity(
      id = "user_default",
      role = UserRole.TENANT,
      name = "Rahul Sharma",
      phone = "9876543210",
      whatsapp = "9876543210",
      email = "rahul.student@example.com",
      city = "Kota",
      isLoggedIn = true,
      isSubscriptionActive = true,
      subscriptionPaidUntil = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
      lastSubscriptionPaidAt = System.currentTimeMillis(),
      ownerUpiId = "owner.rajesh@okaxis",
      collegeOrCoaching = "Allen Career Institute",
      preferredBudgetMin = 2000,
      preferredBudgetMax = 7500,
      preferredRoomType = "Single Room",
      preferredAmenities = listOf("WiFi", "RO Water", "Attached Washroom", "Study Table"),
      propertyName = "Sharma Niwas Student PG & Rooms",
      ownerBio = "Owner of 12 furnished student rooms near coaching hub. Quiet, safe & CCTV protected.",
      callingHours = "8:00 AM - 9:00 PM"
    )
    userDao.insertOrUpdate(defaultUser)

    val sampleChats = listOf(
      ChatMessageEntity(
        roomId = 1,
        roomTitle = "Spacious Single Room near Allen Samyak",
        senderId = "user_default",
        senderName = "Rahul Sharma",
        receiverId = "owner_rajesh",
        receiverName = "Rajesh Verma (Room Malik)",
        messageText = "Namaste Sir, Mai rul app par aapka single room dekha Kota me. Kya ye room abhi vacant hai?",
        inquiryType = "INQUIRY",
        timestamp = System.currentTimeMillis() - 3600000 * 2,
        isRead = true
      ),
      ChatMessageEntity(
        roomId = 1,
        roomTitle = "Spacious Single Room near Allen Samyak",
        senderId = "owner_rajesh",
        senderName = "Rajesh Verma (Room Malik)",
        receiverId = "user_default",
        receiverName = "Rahul Sharma",
        messageText = "Namaste Rahul beta! Haan ye room available hai. Room me wooden study desk aur attached washroom hai. Aap kal 11:00 AM visit karke dekh sakte hain.",
        inquiryType = "VIEWING_REQUEST",
        timestamp = System.currentTimeMillis() - 3600000 * 1,
        isRead = false
      )
    )
    chatDao.insertAll(sampleChats)

    // Seed initial sample receipts if none exist
    if (receiptDao.getCount() == 0) {
      val now = Date()
      val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(now)
      val timeStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(now)
      val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(now)

      val initialReceipts = listOf(
        RentReceiptEntity(
          receiptNo = "MR-REC-20261028-4821",
          receiptType = RentReceiptEntity.TYPE_ROOM_RENT,
          roomId = 1,
          roomTitle = "Spacious Single Room near Allen Samyak",
          roomAddress = "Plot 42-B, Sector 2, Rajeev Gandhi Nagar, Kota",
          senderName = "Rahul Sharma",
          senderPhone = "9876543210",
          senderRole = "Student / Tenant",
          receiverName = "Rajesh Verma",
          receiverPhone = "9876543210",
          receiverRole = "Room Malik",
          amount = 4500,
          paymentMethod = "UPI (Google Pay)",
          transactionRef = "UPI/20261028/89123490",
          rentMonth = "October 2026",
          dateStr = dateStr,
          timeStr = timeStr,
          dayOfWeek = dayOfWeek,
          timestamp = System.currentTimeMillis() - 86400000L,
          status = "PAID & VERIFIED",
          notes = "Full monthly rent received via UPI. Zero brokerage."
        ),
        RentReceiptEntity(
          receiptNo = "MR-FEE-20261028-1092",
          receiptType = RentReceiptEntity.TYPE_PLATFORM_FEE,
          roomId = 0,
          roomTitle = "Monthly Listing & Verification Plan",
          roomAddress = "Kota Coaching Hub - 8 Student Rooms Listed",
          senderName = "Rajesh Verma (Room Malik)",
          senderPhone = "9876543210",
          senderRole = "Room Malik",
          receiverName = PlatformFeeManager.APP_OWNER_NAME,
          receiverPhone = PlatformFeeManager.APP_OWNER_UPI,
          receiverRole = "App Owner / Mai Rul Platform",
          amount = 300, // ₹250 base + ₹50 Kota location fee
          paymentMethod = "UPI (PhonePe)",
          transactionRef = "UPI/PLT/49210921/OKAXIS",
          rentMonth = "Platform Listing Fee (30 Days)",
          dateStr = dateStr,
          timeStr = timeStr,
          dayOfWeek = dayOfWeek,
          timestamp = System.currentTimeMillis() - 86400000L * 2,
          status = "PAID & VERIFIED",
          notes = "Monthly platform fee paid to App Owner according to Kota location."
        )
      )
      receiptDao.insertAll(initialReceipts)
    }

    // Seed initial active Shadi Locations for students in coaching hubs
    if (shadiDao.getCount() == 0) {
      val (dateStr, dayOfWeek) = com.example.data.model.ShadiLocationEntity.getCurrentDateAndDay()
      val sampleShadis = listOf(
        com.example.data.model.ShadiLocationEntity(
          title = "Verma & Sharma Grand Wedding & Bhoj (शादी व प्रीतिभोज)",
          venueName = "Rajwada Marriage Garden & Resort",
          city = "Kota",
          area = "Rajeev Gandhi Nagar",
          landmark = "Near City Mall & Allen Samyak",
          dateStr = dateStr,
          dayOfWeek = dayOfWeek,
          functionType = "Wedding & Dinner (शादी व दावत)",
          timing = "7:30 PM - 11:30 PM",
          addedByStudent = "Aman (Kota Aspirant)",
          studentNote = "Huge wedding pandal! Unlimited gulab jamun, paneer butter masala & ice creams. Very close to student mess.",
          timestamp = System.currentTimeMillis() - 3600000 * 2,
          isToday = true
        ),
        com.example.data.model.ShadiLocationEntity(
          title = "Royal Preetibhoj & Sangeet (रिसेप्शन व दावत)",
          venueName = "Utsav Vatika Banquet & Lawns",
          city = "Delhi",
          area = "Mukherjee Nagar",
          landmark = "Behind Batra Cinema & Drishti IAS",
          dateStr = dateStr,
          dayOfWeek = dayOfWeek,
          functionType = "Reception (प्रीतिभोज)",
          timing = "8:00 PM - Midnight",
          addedByStudent = "Vikas (UPSC Student)",
          studentNote = "Large outdoor lawn, delicious buffet dinner, lots of student crowd attending in Mukherjee Nagar.",
          timestamp = System.currentTimeMillis() - 3600000 * 5,
          isToday = true
        ),
        com.example.data.model.ShadiLocationEntity(
          title = "Mishra Family Shadi Samaroh (विवाह समारोह)",
          venueName = "Shehnai Marriage Palace",
          city = "Patna",
          area = "Boring Road",
          landmark = "Opposite A.N. College Lane",
          dateStr = dateStr,
          dayOfWeek = dayOfWeek,
          functionType = "Wedding (शादी)",
          timing = "7:00 PM - 11:00 PM",
          addedByStudent = "Rohan (Patna Student)",
          studentNote = "Grand lighting and dinner feast. Safe location with bike parking on Boring Road.",
          timestamp = System.currentTimeMillis() - 3600000 * 7,
          isToday = true
        )
      )
      shadiDao.insertAll(sampleShadis)
    }
  }
}
