package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatConversationSummary
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PlatformFeeManager
import com.example.data.model.RentReceiptEntity
import com.example.data.model.RoomConstants
import com.example.data.model.RoomListingEntity
import com.example.data.model.ShadiLocationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.MaiRulRepository
import com.example.ui.screens.CallType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
  PRICE_LOW_TO_HIGH("Rent: Low to High"),
  PRICE_HIGH_TO_LOW("Rent: High to Low"),
  NEWEST("Recently Added")
}

data class FilterState(
  val query: String = "",
  val selectedCity: String = "All Cities",
  val minRent: Float = 1000f,
  val maxRent: Float = 15000f,
  val selectedRoomType: String = "All Types",
  val selectedSuitability: String = "All",
  val selectedAmenities: Set<String> = emptySet(),
  val onlyVacant: Boolean = false,
  val sortOption: SortOption = SortOption.PRICE_LOW_TO_HIGH
)

data class ActiveCallState(
  val type: CallType,
  val callerName: String,
  val callerPhone: String,
  val room: RoomListingEntity
)

class MaiRulViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: MaiRulRepository

  init {
    val database = AppDatabase.getDatabase(application)
    repository = MaiRulRepository(database)
    viewModelScope.launch {
      repository.initializeSampleDataIfNeeded()
    }
  }

  val activeUser: StateFlow<UserEntity> = repository.activeUser
    .combine(MutableStateFlow(Unit)) { user, _ ->
      user ?: UserEntity()
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UserEntity()
    )

  private val _filterState = MutableStateFlow(FilterState())
  val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

  private val _selectedRoom = MutableStateFlow<RoomListingEntity?>(null)
  val selectedRoom: StateFlow<RoomListingEntity?> = _selectedRoom.asStateFlow()

  private val _showFilterSheet = MutableStateFlow(false)
  val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

  private val _showAddRoomDialog = MutableStateFlow(false)
  val showAddRoomDialog: StateFlow<Boolean> = _showAddRoomDialog.asStateFlow()

  private val _roomToEdit = MutableStateFlow<RoomListingEntity?>(null)
  val roomToEdit: StateFlow<RoomListingEntity?> = _roomToEdit.asStateFlow()

  // Rent adjustment modal for room owners
  private val _roomForRentAdjustment = MutableStateFlow<RoomListingEntity?>(null)
  val roomForRentAdjustment: StateFlow<RoomListingEntity?> = _roomForRentAdjustment.asStateFlow()

  // Rent Payment Dialog state
  private val _showPayRentDialog = MutableStateFlow(false)
  val showPayRentDialog: StateFlow<Boolean> = _showPayRentDialog.asStateFlow()
  private val _payRentRoom = MutableStateFlow<RoomListingEntity?>(null)
  val payRentRoom: StateFlow<RoomListingEntity?> = _payRentRoom.asStateFlow()

  // Platform Fee Dialog state (Room Owner pays to App Owner)
  private val _showPlatformFeeDialog = MutableStateFlow(false)
  val showPlatformFeeDialog: StateFlow<Boolean> = _showPlatformFeeDialog.asStateFlow()

  // App Owner Fee Settings Dialog state
  private val _showFeeSettingsDialog = MutableStateFlow(false)
  val showFeeSettingsDialog: StateFlow<Boolean> = _showFeeSettingsDialog.asStateFlow()

  private val _customBaseFee = MutableStateFlow(PlatformFeeManager.DEFAULT_BASE_FEE)
  val customBaseFee: StateFlow<Int> = _customBaseFee.asStateFlow()

  // Receipt details dialog
  private val _selectedReceipt = MutableStateFlow<RentReceiptEntity?>(null)
  val selectedReceipt: StateFlow<RentReceiptEntity?> = _selectedReceipt.asStateFlow()

  // All Receipts from DB
  val allReceipts: StateFlow<List<RentReceiptEntity>> = repository.allReceipts
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Brain Puzzle Screen State
  private val _showBrainPuzzle = MutableStateFlow(false)
  val showBrainPuzzle: StateFlow<Boolean> = _showBrainPuzzle.asStateFlow()

  // Shadi Samaroh & Function State
  private val _showShadiScreen = MutableStateFlow(false)
  val showShadiScreen: StateFlow<Boolean> = _showShadiScreen.asStateFlow()
  private val _showAddShadiDialog = MutableStateFlow(false)
  val showAddShadiDialog: StateFlow<Boolean> = _showAddShadiDialog.asStateFlow()

  val allShadiLocations: StateFlow<List<ShadiLocationEntity>> = repository.allShadiLocations
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // In-App Audio & Video Call State
  private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
  val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()

  val favoriteRoomIds: StateFlow<Set<Long>> = repository.getFavoriteRoomIds("user_default")
    .combine(MutableStateFlow(Unit)) { ids, _ -> ids.toSet() }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptySet()
    )

  val favoriteRooms: StateFlow<List<RoomListingEntity>> = repository.getFavoriteRooms("user_default")
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // In-App Chat State
  private val _activeChatRoom = MutableStateFlow<RoomListingEntity?>(null)
  val activeChatRoom: StateFlow<RoomListingEntity?> = _activeChatRoom.asStateFlow()

  private val _activeChatOtherUser = MutableStateFlow<Pair<String, String>?>(null) // (id, name)
  val activeChatOtherUser: StateFlow<Pair<String, String>?> = _activeChatOtherUser.asStateFlow()

  val allUserMessages: StateFlow<List<ChatMessageEntity>> = repository.getAllUserMessages("user_default")
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val unreadMessagesCount: StateFlow<Int> = repository.getUnreadCount("user_default")
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  val conversationSummaries: StateFlow<List<ChatConversationSummary>> = allUserMessages
    .combine(MutableStateFlow(Unit)) { messages, _ ->
      val currentUserId = "user_default"
      messages.groupBy { msg ->
        val otherId = if (msg.senderId == currentUserId) msg.receiverId else msg.senderId
        "${msg.roomId}_$otherId"
      }.map { (_, group) ->
        val sortedGroup = group.sortedByDescending { it.timestamp }
        val latest = sortedGroup.first()
        val otherId = if (latest.senderId == currentUserId) latest.receiverId else latest.senderId
        val otherName = if (latest.senderId == currentUserId) latest.receiverName else latest.senderName
        val unreads = group.count { it.receiverId == currentUserId && !it.isRead }
        ChatConversationSummary(
          roomId = latest.roomId,
          roomTitle = latest.roomTitle,
          otherUserId = otherId,
          otherUserName = otherName,
          lastMessage = latest.messageText,
          lastTimestamp = latest.timestamp,
          unreadCount = unreads,
          lastMediaType = latest.mediaType
        )
      }.sortedByDescending { it.lastTimestamp }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val activeConversationMessages: StateFlow<List<ChatMessageEntity>> = combine(
    _activeChatRoom,
    _activeChatOtherUser,
    allUserMessages
  ) { room, otherUser, messages ->
    if (room == null || otherUser == null) {
      emptyList()
    } else {
      messages.filter { msg ->
        msg.roomId == room.id &&
            ((msg.senderId == "user_default" && msg.receiverId == otherUser.first) ||
                (msg.senderId == otherUser.first && msg.receiverId == "user_default"))
      }.sortedBy { it.timestamp }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  fun openChatWithRoom(room: RoomListingEntity) {
    val current = activeUser.value
    val otherId = if (current.role == UserRole.ROOM_OWNER) "tenant_inquiry" else room.ownerId
    val otherName = if (current.role == UserRole.ROOM_OWNER) "Student Inquiry" else room.ownerName
    _activeChatRoom.value = room
    _activeChatOtherUser.value = Pair(otherId, otherName)
    viewModelScope.launch {
      repository.markChatAsRead(room.id, "user_default")
    }
  }

  fun openChatConversation(summary: ChatConversationSummary) {
    viewModelScope.launch {
      val room = filteredRooms.value.find { it.id == summary.roomId }
        ?: RoomListingEntity(
          id = summary.roomId,
          ownerId = summary.otherUserId,
          ownerName = summary.otherUserName,
          ownerPhone = "9876543210",
          ownerWhatsapp = "9876543210",
          title = summary.roomTitle,
          description = "",
          rentPerMonth = 4500,
          securityDeposit = 4500,
          city = "Kota",
          area = "Student Hub",
          fullAddress = "",
          nearbyLandmark = "",
          amenities = emptyList()
        )
      _activeChatRoom.value = room
      _activeChatOtherUser.value = Pair(summary.otherUserId, summary.otherUserName)
      repository.markChatAsRead(summary.roomId, "user_default")
    }
  }

  fun closeActiveChat() {
    _activeChatRoom.value = null
    _activeChatOtherUser.value = null
  }

  // Personal Chat with Media Sharing (Photos, Audio, Video)
  fun sendChatMessage(
    text: String,
    inquiryType: String = "GENERAL",
    mediaType: String = "TEXT",
    mediaUri: String? = null,
    duration: String? = null
  ) {
    val room = _activeChatRoom.value ?: return
    val otherUser = _activeChatOtherUser.value ?: return
    val current = activeUser.value

    viewModelScope.launch {
      val message = ChatMessageEntity(
        roomId = room.id,
        roomTitle = room.title,
        senderId = "user_default",
        senderName = current.name.ifBlank { "User" },
        receiverId = otherUser.first,
        receiverName = otherUser.second,
        messageText = text.trim(),
        inquiryType = inquiryType,
        mediaType = mediaType,
        mediaUri = mediaUri,
        mediaDuration = duration,
        timestamp = System.currentTimeMillis(),
        isRead = false
      )
      repository.sendMessage(message)
    }
  }

  // Call System (Audio & Video)
  fun startCall(type: CallType, name: String, phone: String, room: RoomListingEntity) {
    _activeCall.value = ActiveCallState(
      type = type,
      callerName = name,
      callerPhone = phone,
      room = room
    )
  }

  fun endCall(durationSeconds: Int) {
    val call = _activeCall.value
    _activeCall.value = null
    if (call != null) {
      val min = durationSeconds / 60
      val sec = durationSeconds % 60
      val durStr = String.format("%02d:%02d", min, sec)
      val icon = if (call.type == CallType.VIDEO) "📹 Video Call" else "📞 Audio Call"
      sendChatMessage(
        text = "$icon ended ($durStr)",
        inquiryType = "CALL_RECORD",
        mediaType = "TEXT"
      )
    }
  }

  // Brain Puzzle
  fun setBrainPuzzleVisible(visible: Boolean) {
    _showBrainPuzzle.value = visible
  }

  // Shadi Location
  fun setShadiScreenVisible(visible: Boolean) {
    _showShadiScreen.value = visible
  }

  fun setAddShadiDialogVisible(visible: Boolean) {
    _showAddShadiDialog.value = visible
  }

  fun addShadiLocation(
    venueName: String,
    title: String,
    city: String,
    area: String,
    landmark: String,
    functionType: String,
    timing: String,
    studentNote: String
  ) {
    viewModelScope.launch {
      val current = activeUser.value
      val (dateStr, dayOfWeek) = ShadiLocationEntity.getCurrentDateAndDay()
      val shadi = ShadiLocationEntity(
        title = title,
        venueName = venueName,
        city = city.ifBlank { "Kota" },
        area = area,
        landmark = landmark,
        dateStr = dateStr,
        dayOfWeek = dayOfWeek,
        functionType = functionType,
        timing = timing,
        addedByStudent = current.name.ifBlank { "Student Community" },
        studentNote = studentNote,
        timestamp = System.currentTimeMillis(),
        isToday = true
      )
      repository.addShadiLocation(shadi)
      _showAddShadiDialog.value = false
    }
  }

  fun deleteShadiLocation(id: Long) {
    viewModelScope.launch {
      repository.deleteShadiLocation(id)
    }
  }

  // Filtered rooms for tenants
  val filteredRooms: StateFlow<List<RoomListingEntity>> = combine(
    repository.allListings,
    _filterState
  ) { listings, filters ->
    listings.filter { room ->
      val queryMatches = if (filters.query.isBlank()) {
        true
      } else {
        val q = filters.query.trim().lowercase()
        room.title.lowercase().contains(q) ||
            room.area.lowercase().contains(q) ||
            room.city.lowercase().contains(q) ||
            room.nearbyLandmark.lowercase().contains(q) ||
            room.description.lowercase().contains(q)
      }

      val cityMatches = if (filters.selectedCity == "All Cities") {
        true
      } else {
        room.city.equals(filters.selectedCity, ignoreCase = true)
      }

      val rentMatches = room.rentPerMonth >= filters.minRent && room.rentPerMonth <= filters.maxRent

      val typeMatches = if (filters.selectedRoomType == "All Types") {
        true
      } else {
        room.roomType.equals(filters.selectedRoomType, ignoreCase = true)
      }

      val suitabilityMatches = if (filters.selectedSuitability == "All") {
        true
      } else {
        room.suitableFor.equals(filters.selectedSuitability, ignoreCase = true) ||
            room.suitableFor.equals("Anyone", ignoreCase = true)
      }

      val amenitiesMatch = if (filters.selectedAmenities.isEmpty()) {
        true
      } else {
        filters.selectedAmenities.all { requiredAmenity ->
          room.amenities.any { it.equals(requiredAmenity, ignoreCase = true) }
        }
      }

      val vacancyMatches = if (filters.onlyVacant) room.isVacant else true

      queryMatches && cityMatches && rentMatches && typeMatches && suitabilityMatches && amenitiesMatch && vacancyMatches
    }.sortedWith(
      when (filters.sortOption) {
        SortOption.PRICE_LOW_TO_HIGH -> compareBy { it.rentPerMonth }
        SortOption.PRICE_HIGH_TO_LOW -> compareByDescending { it.rentPerMonth }
        SortOption.NEWEST -> compareByDescending { it.createdAt }
      }
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Owner listings: all listings where owner matches current user or demo owners
  val ownerRooms: StateFlow<List<RoomListingEntity>> = repository.allListings
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Filter actions
  fun updateSearchQuery(query: String) {
    _filterState.value = _filterState.value.copy(query = query)
  }

  fun updateCity(city: String) {
    _filterState.value = _filterState.value.copy(selectedCity = city)
  }

  fun updateRentRange(min: Float, max: Float) {
    _filterState.value = _filterState.value.copy(minRent = min, maxRent = max)
  }

  fun updateRoomType(type: String) {
    _filterState.value = _filterState.value.copy(selectedRoomType = type)
  }

  fun updateSuitability(suitability: String) {
    _filterState.value = _filterState.value.copy(selectedSuitability = suitability)
  }

  fun toggleAmenityFilter(amenity: String) {
    val current = _filterState.value.selectedAmenities
    val updated = if (current.contains(amenity)) current - amenity else current + amenity
    _filterState.value = _filterState.value.copy(selectedAmenities = updated)
  }

  fun toggleVacantOnly(onlyVacant: Boolean) {
    _filterState.value = _filterState.value.copy(onlyVacant = onlyVacant)
  }

  fun updateSortOption(sort: SortOption) {
    _filterState.value = _filterState.value.copy(sortOption = sort)
  }

  fun clearAllFilters() {
    _filterState.value = FilterState()
  }

  fun applyTenantPreferences() {
    val user = activeUser.value
    _filterState.value = _filterState.value.copy(
      selectedCity = if (user.city.isNotBlank()) user.city else "All Cities",
      minRent = user.preferredBudgetMin.toFloat(),
      maxRent = user.preferredBudgetMax.toFloat(),
      selectedRoomType = if (user.preferredRoomType.isNotBlank()) user.preferredRoomType else "All Types",
      selectedAmenities = user.preferredAmenities.toSet()
    )
  }

  fun setFilterSheetVisible(visible: Boolean) {
    _showFilterSheet.value = visible
  }

  fun selectRoom(room: RoomListingEntity?) {
    _selectedRoom.value = room
  }

  fun setAddRoomDialogVisible(visible: Boolean, roomToEdit: RoomListingEntity? = null) {
    _roomToEdit.value = roomToEdit
    _showAddRoomDialog.value = visible
  }

  fun toggleFavorite(roomId: Long) {
    viewModelScope.launch {
      val isCurrentlyFav = favoriteRoomIds.value.contains(roomId)
      repository.toggleFavorite(roomId, "user_default", !isCurrentlyFav)
    }
  }

  // User Authentication & Profile
  fun login(name: String, phone: String, role: UserRole, city: String) {
    viewModelScope.launch {
      repository.loginOrRegister(name, phone, role, city)
    }
  }

  fun logout() {
    viewModelScope.launch {
      repository.logoutUser()
    }
  }

  fun switchUserRole(role: UserRole) {
    viewModelScope.launch {
      repository.switchUserRole(role)
    }
  }

  fun updateUserProfile(user: UserEntity) {
    viewModelScope.launch {
      repository.saveUser(user)
    }
  }

  // Room Owner can increase or decrease room rent
  fun setRoomForRentAdjustment(room: RoomListingEntity?) {
    _roomForRentAdjustment.value = room
  }

  fun updateRoomRent(roomId: Long, newRent: Int) {
    viewModelScope.launch {
      repository.updateRoomRent(roomId, newRent)
      _roomForRentAdjustment.value = null
      if (_selectedRoom.value?.id == roomId) {
        _selectedRoom.value = _selectedRoom.value?.copy(rentPerMonth = newRent.coerceAtLeast(500))
      }
    }
  }

  fun adjustRentByDelta(roomId: Long, currentRent: Int, delta: Int) {
    val updated = (currentRent + delta).coerceAtLeast(500)
    updateRoomRent(roomId, updated)
  }

  // Rent Payment Dialog and Flow
  fun openPayRentDialog(room: RoomListingEntity? = null) {
    _payRentRoom.value = room ?: filteredRooms.value.firstOrNull()
    _showPayRentDialog.value = true
  }

  fun closePayRentDialog() {
    _showPayRentDialog.value = false
    _payRentRoom.value = null
  }

  fun processRentPayment(
    room: RoomListingEntity,
    amount: Int,
    rentMonth: String,
    paymentMethod: String,
    notes: String
  ) {
    viewModelScope.launch {
      val currentUser = activeUser.value
      val (dateStr, timeStr, dayOfWeek) = RentReceiptEntity.getCurrentDateTimeParts()
      val receiptNo = RentReceiptEntity.generateReceiptNo("MR-REC")
      val txRef = "UPI/RENT/${System.currentTimeMillis() % 100000000}/PAY"

      val receipt = RentReceiptEntity(
        receiptNo = receiptNo,
        receiptType = RentReceiptEntity.TYPE_ROOM_RENT,
        roomId = room.id,
        roomTitle = room.title,
        roomAddress = "${room.fullAddress.ifBlank { room.area }}, ${room.city}",
        senderName = currentUser.name.ifBlank { "Rahul Sharma" },
        senderPhone = currentUser.phone.ifBlank { "9876543210" },
        senderRole = "Tenant / Student",
        receiverName = room.ownerName,
        receiverPhone = room.ownerPhone,
        receiverRole = "Room Malik",
        amount = amount,
        paymentMethod = paymentMethod,
        transactionRef = txRef,
        rentMonth = rentMonth,
        dateStr = dateStr,
        timeStr = timeStr,
        dayOfWeek = dayOfWeek,
        timestamp = System.currentTimeMillis(),
        status = "PAID & VERIFIED",
        notes = notes.ifBlank { "Direct rent payment to room owner with zero brokerage." }
      )
      repository.addRentReceipt(receipt)
      _showPayRentDialog.value = false
      _selectedReceipt.value = receipt
    }
  }

  // Platform Fee Dialog and Flow (Room owner pays ₹250 to App Owner)
  fun openPlatformFeeDialog() {
    _showPlatformFeeDialog.value = true
  }

  fun closePlatformFeeDialog() {
    _showPlatformFeeDialog.value = false
  }

  fun setFeeSettingsDialogVisible(visible: Boolean) {
    _showFeeSettingsDialog.value = visible
  }

  fun setCustomBaseFee(newBaseFee: Int) {
    _customBaseFee.value = newBaseFee
  }

  fun processPlatformFeePayment(
    amount: Int,
    paymentMethod: String,
    city: String = "Kota",
    roomTitle: String = "All Listed Rooms"
  ) {
    viewModelScope.launch {
      val receipt = repository.payPlatformListingFee(
        amount = amount,
        paymentMethod = paymentMethod,
        roomTitle = roomTitle,
        city = city
      )
      _showPlatformFeeDialog.value = false
      _selectedReceipt.value = receipt
    }
  }

  // Receipt viewing and deletion
  fun viewReceipt(receipt: RentReceiptEntity?) {
    _selectedReceipt.value = receipt
  }

  fun deleteReceipt(id: Long) {
    viewModelScope.launch {
      repository.deleteRentReceipt(id)
      if (_selectedReceipt.value?.id == id) {
        _selectedReceipt.value = null
      }
    }
  }

  // Room Owner property management
  fun saveRoomListing(
    title: String,
    description: String,
    rent: Int,
    deposit: Int,
    electricityCharge: String,
    roomType: String,
    suitableFor: String,
    city: String,
    area: String,
    fullAddress: String,
    nearbyLandmark: String,
    amenities: List<String>,
    photoDrawable: String,
    isVacant: Boolean,
    noticeDays: Int,
    gateTiming: String,
    existingId: Long? = null
  ) {
    viewModelScope.launch {
      val user = activeUser.value
      val listing = RoomListingEntity(
        id = existingId ?: 0,
        ownerId = user.id,
        ownerName = user.name.ifBlank { "Room Malik" },
        ownerPhone = user.phone.ifBlank { "9876543210" },
        ownerWhatsapp = user.whatsapp.ifBlank { "9876543210" },
        title = title,
        description = description,
        rentPerMonth = rent,
        securityDeposit = deposit,
        electricityCharge = electricityCharge,
        roomType = roomType,
        suitableFor = suitableFor,
        city = city,
        area = area,
        fullAddress = fullAddress,
        nearbyLandmark = nearbyLandmark,
        amenities = amenities,
        photoDrawableName = photoDrawable,
        isVacant = isVacant,
        noticePeriodDays = noticeDays,
        gateClosingTime = gateTiming
      )
      if (existingId != null && existingId > 0) {
        repository.updateListing(listing)
      } else {
        repository.addListing(listing)
      }
      _showAddRoomDialog.value = false
      _roomToEdit.value = null
    }
  }

  fun toggleRoomVacancy(roomId: Long, isVacant: Boolean) {
    viewModelScope.launch {
      repository.updateVacancy(roomId, isVacant)
    }
  }

  fun deleteRoomListing(roomId: Long) {
    viewModelScope.launch {
      repository.deleteListing(roomId)
      if (_selectedRoom.value?.id == roomId) {
        _selectedRoom.value = null
      }
    }
  }
}
