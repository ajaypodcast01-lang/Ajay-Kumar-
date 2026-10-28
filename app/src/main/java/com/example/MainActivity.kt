package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.AppHeaderBrand
import com.example.ui.components.AppLogoView
import com.example.ui.components.FilterBottomSheet
import com.example.ui.screens.AddEditRoomDialog
import com.example.ui.screens.AddShadiLocationDialog
import com.example.ui.screens.AdjustRentDialog
import com.example.ui.screens.AppOwnerFeeSettingsDialog
import com.example.ui.screens.BrainPuzzleScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.CallType
import com.example.ui.screens.ChatThreadSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginAuthScreen
import com.example.ui.screens.MessagesListScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.PayPlatformFeeDialog
import com.example.ui.screens.PayRentDialog
import com.example.ui.screens.ProfileAuthScreen
import com.example.ui.screens.ReceiptsScreen
import com.example.ui.screens.RentReceiptViewDialog
import com.example.ui.screens.RoomDetailSheet
import com.example.ui.screens.SavedRoomsScreen
import com.example.ui.screens.ShadiSamarohScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MaiRulViewModel

enum class MainTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  EXPLORE("Search", Icons.Default.Explore),
  MY_ROOMS("My Rooms", Icons.Default.MeetingRoom),
  RECEIPTS("Receipts", Icons.Default.ReceiptLong),
  MESSAGES("Chat", Icons.Default.Chat),
  PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MaiRulApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaiRulApp(viewModel: MaiRulViewModel = viewModel()) {
  var selectedTab by remember { mutableStateOf(MainTab.EXPLORE) }

  val activeUser by viewModel.activeUser.collectAsState()
  val filteredRooms by viewModel.filteredRooms.collectAsState()
  val ownerRooms by viewModel.ownerRooms.collectAsState()
  val filterState by viewModel.filterState.collectAsState()
  val selectedRoom by viewModel.selectedRoom.collectAsState()
  val showFilterSheet by viewModel.showFilterSheet.collectAsState()
  val showAddRoomDialog by viewModel.showAddRoomDialog.collectAsState()
  val roomToEdit by viewModel.roomToEdit.collectAsState()
  val favoriteRoomIds by viewModel.favoriteRoomIds.collectAsState()
  val favoriteRooms by viewModel.favoriteRooms.collectAsState()
  val conversationSummaries by viewModel.conversationSummaries.collectAsState()
  val activeChatRoom by viewModel.activeChatRoom.collectAsState()
  val activeChatOtherUser by viewModel.activeChatOtherUser.collectAsState()
  val activeConversationMessages by viewModel.activeConversationMessages.collectAsState()
  val unreadMessagesCount by viewModel.unreadMessagesCount.collectAsState()

  // New states
  val allReceipts by viewModel.allReceipts.collectAsState()
  val selectedReceipt by viewModel.selectedReceipt.collectAsState()
  val showPayRentDialog by viewModel.showPayRentDialog.collectAsState()
  val payRentRoom by viewModel.payRentRoom.collectAsState()
  val showPlatformFeeDialog by viewModel.showPlatformFeeDialog.collectAsState()
  val showFeeSettingsDialog by viewModel.showFeeSettingsDialog.collectAsState()
  val customBaseFee by viewModel.customBaseFee.collectAsState()
  val roomForRentAdjustment by viewModel.roomForRentAdjustment.collectAsState()
  val showBrainPuzzle by viewModel.showBrainPuzzle.collectAsState()
  val showShadiScreen by viewModel.showShadiScreen.collectAsState()
  val showAddShadiDialog by viewModel.showAddShadiDialog.collectAsState()
  val allShadiLocations by viewModel.allShadiLocations.collectAsState()
  val activeCall by viewModel.activeCall.collectAsState()

  val scope = rememberCoroutineScope()
  val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val addRoomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val chatSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val receiptSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val payRentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val platformFeeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val feeSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val adjustRentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val addShadiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  // Handle system back navigation if detail, chat, or secondary tabs open
  BackHandler(
    enabled = activeCall != null || showBrainPuzzle || showShadiScreen || selectedReceipt != null ||
        showPayRentDialog || showPlatformFeeDialog || roomForRentAdjustment != null ||
        selectedRoom != null || activeChatRoom != null || selectedTab != MainTab.EXPLORE
  ) {
    when {
      activeCall != null -> viewModel.endCall(10)
      showBrainPuzzle -> viewModel.setBrainPuzzleVisible(false)
      showShadiScreen -> viewModel.setShadiScreenVisible(false)
      selectedReceipt != null -> viewModel.viewReceipt(null)
      showPayRentDialog -> viewModel.closePayRentDialog()
      showPlatformFeeDialog -> viewModel.closePlatformFeeDialog()
      roomForRentAdjustment != null -> viewModel.setRoomForRentAdjustment(null)
      activeChatRoom != null -> viewModel.closeActiveChat()
      selectedRoom != null -> viewModel.selectRoom(null)
      else -> selectedTab = MainTab.EXPLORE
    }
  }

  // If not logged in, display startup Login Screen with App Logo and My Room branding (User Requirement)
  if (!activeUser.isLoggedIn) {
    LoginAuthScreen(
      onLoginSuccess = { name, phone, role, city ->
        viewModel.login(name, phone, role, city)
      }
    )
    return
  }

  // Active Audio or Video Call Screen (User Requirement)
  if (activeCall != null) {
    CallScreen(
      callType = activeCall!!.type,
      callerName = activeCall!!.callerName,
      callerPhone = activeCall!!.callerPhone,
      roomTitle = activeCall!!.room.title,
      photoDrawableName = activeCall!!.room.photoDrawableName,
      onEndCall = { duration -> viewModel.endCall(duration) }
    )
    return
  }

  // Brain Power IQ Puzzles Screen (User Requirement)
  if (showBrainPuzzle) {
    BrainPuzzleScreen(
      onClose = { viewModel.setBrainPuzzleVisible(false) }
    )
    return
  }

  // Shadi Samaroh & Function Screen (User Requirement)
  if (showShadiScreen) {
    ShadiSamarohScreen(
      shadiList = allShadiLocations,
      onAddShadiClick = { viewModel.setAddShadiDialogVisible(true) },
      onDeleteShadi = { viewModel.deleteShadiLocation(it) },
      onClose = { viewModel.setShadiScreenVisible(false) }
    )

    if (showAddShadiDialog) {
      AddShadiLocationDialog(
        sheetState = addShadiSheetState,
        onDismiss = { viewModel.setAddShadiDialogVisible(false) },
        onSave = { venue, title, city, area, landmark, type, timing, note ->
          viewModel.addShadiLocation(venue, title, city, area, landmark, type, timing, note)
        }
      )
    }
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          // App Logo and "My Room" Header (User Requirement)
          Row(verticalAlignment = Alignment.CenterVertically) {
            AppLogoView(size = 36.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "My Room",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 0.5.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              Text(
                text = "Student Room & Rent App",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 10.sp
                )
              )
            }
          }
        },
        actions = {
          // Brain Power IQ Shortcut
          IconButton(
            onClick = { viewModel.setBrainPuzzleVisible(true) },
            modifier = Modifier.testTag("topbar_brain_puzzle_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = "Brain IQ Game",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }

          // Shadi Samaroh Shortcut
          IconButton(
            onClick = { viewModel.setShadiScreenVisible(true) },
            modifier = Modifier.testTag("topbar_shadi_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Celebration,
              contentDescription = "Shadi Samaroh",
              tint = Color(0xFFB45309),
              modifier = Modifier.size(22.dp)
            )
          }

          // Role Indicator & Quick Switch Chip
          Surface(
            onClick = {
              val nextRole = if (activeUser.role == UserRole.ROOM_OWNER) UserRole.TENANT else UserRole.ROOM_OWNER
              viewModel.switchUserRole(nextRole)
              selectedTab = if (nextRole == UserRole.ROOM_OWNER) MainTab.MY_ROOMS else MainTab.EXPLORE
            },
            shape = RoundedCornerShape(20.dp),
            color = if (activeUser.role == UserRole.ROOM_OWNER) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              AmberAccent.copy(alpha = 0.18f)
            },
            modifier = Modifier
              .padding(end = 8.dp)
              .testTag("quick_role_switch_chip")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (activeUser.role == UserRole.ROOM_OWNER) Icons.Default.Home else Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (activeUser.role == UserRole.ROOM_OWNER) MaterialTheme.colorScheme.primary else Color(0xFFB45309)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (activeUser.role == UserRole.ROOM_OWNER) "Malik" else "Student",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (activeUser.role == UserRole.ROOM_OWNER) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFFB45309)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch role",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        MainTab.entries.forEach { tab ->
          val isSelected = selectedTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTab = tab },
            icon = {
              if (tab == MainTab.RECEIPTS && allReceipts.isNotEmpty()) {
                BadgedBox(badge = {
                  Badge { Text("${allReceipts.size}") }
                }) {
                  Icon(imageVector = tab.icon, contentDescription = tab.label)
                }
              } else if (tab == MainTab.MESSAGES && unreadMessagesCount > 0) {
                BadgedBox(badge = {
                  Badge { Text("$unreadMessagesCount") }
                }) {
                  Icon(imageVector = tab.icon, contentDescription = tab.label)
                }
              } else {
                Icon(imageVector = tab.icon, contentDescription = tab.label)
              }
            },
            label = {
              Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
          )
        }
      }
    },
    floatingActionButton = {
      if (selectedTab == MainTab.MY_ROOMS || activeUser.role == UserRole.ROOM_OWNER) {
        ExtendedFloatingActionButton(
          onClick = {
            viewModel.setAddRoomDialogVisible(true, null)
          },
          icon = { Icon(Icons.Default.Add, contentDescription = "Add room") },
          text = { Text("Upload Room (कमरा जोड़ें)", fontWeight = FontWeight.Bold) },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          modifier = Modifier.testTag("fab_upload_room")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        MainTab.EXPLORE -> {
          HomeScreen(
            rooms = filteredRooms,
            filterState = filterState,
            favoriteIds = favoriteRoomIds,
            activeUser = activeUser,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onCitySelect = { viewModel.updateCity(it) },
            onOpenFilterSheet = { viewModel.setFilterSheetVisible(true) },
            onClearFilters = { viewModel.clearAllFilters() },
            onApplyPreferences = { viewModel.applyTenantPreferences() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onRoomClick = { viewModel.selectRoom(it) },
            onMessageRoom = { room -> viewModel.openChatWithRoom(room) }
          )
        }

        MainTab.MY_ROOMS -> {
          OwnerDashboardScreen(
            ownerRooms = ownerRooms,
            activeUser = activeUser,
            customBaseFee = customBaseFee,
            onAddNewRoom = { viewModel.setAddRoomDialogVisible(true, null) },
            onEditRoom = { viewModel.setAddRoomDialogVisible(true, it) },
            onAdjustRent = { room -> viewModel.setRoomForRentAdjustment(room) },
            onQuickRentDelta = { roomId, currentRent, delta ->
              viewModel.adjustRentByDelta(roomId, currentRent, delta)
            },
            onToggleVacancy = { id, isVacant -> viewModel.toggleRoomVacancy(id, isVacant) },
            onDeleteRoom = { viewModel.deleteRoomListing(it) },
            onRoomClick = { viewModel.selectRoom(it) },
            onPayPlatformFee = { viewModel.openPlatformFeeDialog() },
            onOpenFeeSettings = { viewModel.setFeeSettingsDialogVisible(true) }
          )
        }

        MainTab.RECEIPTS -> {
          ReceiptsScreen(
            receipts = allReceipts,
            activeUser = activeUser,
            onViewReceipt = { viewModel.viewReceipt(it) },
            onPayRentClick = { viewModel.openPayRentDialog() },
            onDeleteReceipt = { viewModel.deleteReceipt(it) }
          )
        }

        MainTab.MESSAGES -> {
          MessagesListScreen(
            conversations = conversationSummaries,
            onConversationClick = { conv ->
              viewModel.openChatConversation(conv)
            },
            onExploreRoomsClick = { selectedTab = MainTab.EXPLORE }
          )
        }

        MainTab.PROFILE -> {
          ProfileAuthScreen(
            activeUser = activeUser,
            onSaveProfile = { viewModel.updateUserProfile(it) },
            onSwitchRole = { viewModel.switchUserRole(it) },
            onOpenBrainPuzzles = { viewModel.setBrainPuzzleVisible(true) },
            onLogout = { viewModel.logout() }
          )
        }
      }
    }
  }

  // Filter Bottom Sheet
  if (showFilterSheet) {
    FilterBottomSheet(
      filterState = filterState,
      sheetState = filterSheetState,
      onDismiss = { viewModel.setFilterSheetVisible(false) },
      onApply = { city, min, max, type, suit, amenities, vacant, sort ->
        viewModel.updateCity(city)
        viewModel.updateRentRange(min, max)
        viewModel.updateRoomType(type)
        viewModel.updateSuitability(suit)
        amenities.forEach { am ->
          if (!filterState.selectedAmenities.contains(am)) {
            viewModel.toggleAmenityFilter(am)
          }
        }
        filterState.selectedAmenities.forEach { am ->
          if (!amenities.contains(am)) {
            viewModel.toggleAmenityFilter(am)
          }
        }
        viewModel.toggleVacantOnly(vacant)
        viewModel.updateSortOption(sort)
        viewModel.setFilterSheetVisible(false)
      },
      onReset = {
        viewModel.clearAllFilters()
      }
    )
  }

  // Room Detail Sheet (with Pay Rent Action)
  selectedRoom?.let { room ->
    RoomDetailSheet(
      room = room,
      isFavorite = favoriteRoomIds.contains(room.id),
      onToggleFavorite = { viewModel.toggleFavorite(room.id) },
      sheetState = detailSheetState,
      onDismiss = { viewModel.selectRoom(null) },
      onMessageClick = {
        viewModel.openChatWithRoom(room)
      },
      onPayRentClick = {
        viewModel.openPayRentDialog(room)
      }
    )
  }

  // In-App Chat Thread Sheet with Calling & Media
  if (activeChatRoom != null && activeChatOtherUser != null) {
    ChatThreadSheet(
      room = activeChatRoom!!,
      otherUserId = activeChatOtherUser!!.first,
      otherUserName = activeChatOtherUser!!.second,
      messages = activeConversationMessages,
      activeUser = activeUser,
      sheetState = chatSheetState,
      onDismiss = { viewModel.closeActiveChat() },
      onSendMessage = { text, inquiryType, mediaType, mediaUri, duration ->
        viewModel.sendChatMessage(text, inquiryType, mediaType, mediaUri, duration)
      },
      onStartAudioCall = {
        viewModel.startCall(
          type = CallType.AUDIO,
          name = activeChatOtherUser!!.second,
          phone = activeChatRoom!!.ownerPhone,
          room = activeChatRoom!!
        )
      },
      onStartVideoCall = {
        viewModel.startCall(
          type = CallType.VIDEO,
          name = activeChatOtherUser!!.second,
          phone = activeChatRoom!!.ownerPhone,
          room = activeChatRoom!!
        )
      },
      onViewRoomDetails = {
        viewModel.selectRoom(activeChatRoom)
      }
    )
  }

  // Pay Room Rent Dialog (User Requirement: students can pay room rent in room owner in this app)
  if (showPayRentDialog && payRentRoom != null) {
    PayRentDialog(
      room = payRentRoom!!,
      activeUser = activeUser,
      sheetState = payRentSheetState,
      onDismiss = { viewModel.closePayRentDialog() },
      onPaySuccess = { room, amount, month, method, notes ->
        viewModel.processRentPayment(room, amount, month, method, notes)
      }
    )
  }

  // View Official Rent Receipt Voucher (User Requirement: linked date, time, day, sender, receiver, month name, thank you, share anywhere)
  selectedReceipt?.let { receipt ->
    RentReceiptViewDialog(
      receipt = receipt,
      sheetState = receiptSheetState,
      onDismiss = { viewModel.viewReceipt(null) }
    )
  }

  // Platform Fee Dialog (User Requirement: room owner pay 250 monthly to app owner)
  if (showPlatformFeeDialog) {
    PayPlatformFeeDialog(
      activeUser = activeUser,
      customBaseFee = customBaseFee,
      sheetState = platformFeeSheetState,
      onDismiss = { viewModel.closePlatformFeeDialog() },
      onPaySuccess = { amount, method, city ->
        viewModel.processPlatformFeePayment(amount, method, city)
      },
      onOpenFeeSettings = {
        viewModel.closePlatformFeeDialog()
        viewModel.setFeeSettingsDialogVisible(true)
      }
    )
  }

  // App Owner Fee Settings Dialog (User Requirement: app owner can take fees accordingly to location and room)
  if (showFeeSettingsDialog) {
    AppOwnerFeeSettingsDialog(
      currentBaseFee = customBaseFee,
      sheetState = feeSettingsSheetState,
      onDismiss = { viewModel.setFeeSettingsDialogVisible(false) },
      onSaveBaseFee = { newFee ->
        viewModel.setCustomBaseFee(newFee)
      }
    )
  }

  // Room Owner Increase or Decrease Rent Dialog (User Requirement: room owner can increase or decrease room rent)
  roomForRentAdjustment?.let { targetRoom ->
    AdjustRentDialog(
      room = targetRoom,
      sheetState = adjustRentSheetState,
      onDismiss = { viewModel.setRoomForRentAdjustment(null) },
      onSaveRent = { updatedRent ->
        viewModel.updateRoomRent(targetRoom.id, updatedRent)
      }
    )
  }

  // Add/Edit Room Bottom Sheet
  if (showAddRoomDialog) {
    AddEditRoomDialog(
      roomToEdit = roomToEdit,
      activeUser = activeUser,
      sheetState = addRoomSheetState,
      onDismiss = { viewModel.setAddRoomDialogVisible(false, null) },
      onSave = { title, desc, rent, deposit, electricity, type, suit, city, area, addr, landmark, ams, photo, vacant, notice, gate, existingId ->
        viewModel.saveRoomListing(
          title, desc, rent, deposit, electricity, type, suit, city, area, addr, landmark, ams, photo, vacant, notice, gate, existingId
        )
      }
    )
  }
}
