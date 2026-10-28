package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomConstants
import com.example.data.model.RoomListingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.RoomPhotoView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditRoomDialog(
  roomToEdit: RoomListingEntity?,
  activeUser: UserEntity,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSave: (
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
    existingId: Long?
  ) -> Unit
) {
  var title by remember { mutableStateOf(roomToEdit?.title ?: "") }
  var rentText by remember { mutableStateOf(roomToEdit?.rentPerMonth?.toString() ?: "4500") }
  var depositText by remember { mutableStateOf(roomToEdit?.securityDeposit?.toString() ?: "4500") }
  var electricityCharge by remember { mutableStateOf(roomToEdit?.electricityCharge ?: "Metered (₹8/unit)") }
  var selectedCity by remember { mutableStateOf(roomToEdit?.city ?: activeUser.city.ifBlank { "Kota" }) }
  var area by remember { mutableStateOf(roomToEdit?.area ?: "") }
  var fullAddress by remember { mutableStateOf(roomToEdit?.fullAddress ?: "") }
  var nearbyLandmark by remember { mutableStateOf(roomToEdit?.nearbyLandmark ?: "") }
  var roomType by remember { mutableStateOf(roomToEdit?.roomType ?: "Single Room") }
  var suitableFor by remember { mutableStateOf(roomToEdit?.suitableFor ?: "Students Only") }
  var description by remember {
    mutableStateOf(
      roomToEdit?.description
        ?: "Well-ventilated student room with study table, bright window, and peaceful study atmosphere. 24x7 water and zero brokerage."
    )
  }
  var selectedPhoto by remember {
    mutableStateOf(roomToEdit?.photoDrawableName ?: "room_single_study_1793006717976")
  }
  var selectedAmenities by remember {
    mutableStateOf(roomToEdit?.amenities?.toSet() ?: setOf("WiFi", "RO Water", "Attached Washroom", "Study Table", "Bed & Mattress"))
  }
  var isVacant by remember { mutableStateOf(roomToEdit?.isVacant ?: true) }
  var noticeDaysText by remember { mutableStateOf(roomToEdit?.noticePeriodDays?.toString() ?: "30") }
  var gateTiming by remember { mutableStateOf(roomToEdit?.gateClosingTime ?: "10:30 PM") }

  var titleError by remember { mutableStateOf(false) }
  var areaError by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
        .testTag("add_room_dialog")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (roomToEdit == null) "Upload Room (कमरा अपलोड करें)" else "Edit Room (कमरा बदलें)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Post your room directly for students & tenants",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

      // Room Title
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          titleError = false
        },
        label = { Text("Room Title (कमरे का शीर्षक) *") },
        placeholder = { Text("e.g. Furnished Single Room near Allen Samyak") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("room_title_input"),
        isError = titleError,
        supportingText = if (titleError) {
          { Text("Please enter a title for your room") }
        } else null,
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Rent and Security Deposit Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = rentText,
          onValueChange = { rentText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Rent / Month (₹) *") },
          modifier = Modifier
            .weight(1f)
            .testTag("room_rent_input"),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true
        )

        OutlinedTextField(
          value = depositText,
          onValueChange = { depositText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Security Deposit (₹)") },
          modifier = Modifier
            .weight(1f)
            .testTag("room_deposit_input"),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Electricity Charge Term
      OutlinedTextField(
        value = electricityCharge,
        onValueChange = { electricityCharge = it },
        label = { Text("Electricity Terms (बिजली बिल)") },
        placeholder = { Text("e.g. Sub-meter ₹8/unit or Included in rent") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      // City & Location
      Text(
        text = "Location Details (स्थान की जानकारी)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      // City Chips
      Text(
        text = "Select City:",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Spacer(modifier = Modifier.height(4.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.CITIES.forEach { city ->
          val isSelected = selectedCity == city
          FilterChip(
            selected = isSelected,
            onClick = { selectedCity = city },
            label = { Text(city, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = area,
        onValueChange = {
          area = it
          areaError = false
        },
        label = { Text("Area / Colony / Mohalla *") },
        placeholder = { Text("e.g. Rajeev Gandhi Nagar / Mukherjee Nagar") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("room_area_input"),
        isError = areaError,
        supportingText = if (areaError) {
          { Text("Please enter the area/locality") }
        } else null,
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = nearbyLandmark,
        onValueChange = { nearbyLandmark = it },
        label = { Text("Nearby Coaching / College / Metro Landmark *") },
        placeholder = { Text("e.g. 200m from Allen Supath / Near Batra Cinema") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = fullAddress,
        onValueChange = { fullAddress = it },
        label = { Text("Full Address (मकान नंबर / गली)") },
        placeholder = { Text("Plot 42-B, Street 3") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Room Type & Suitable For
      Text(
        text = "Room Type & Tenants (कमरा प्रकार एवं किरायेदार)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      // Room Type
      Text(
        text = "Room Type:",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Spacer(modifier = Modifier.height(4.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.ROOM_TYPES.filter { it != "All Types" }.forEach { type ->
          val isSelected = roomType == type
          FilterChip(
            selected = isSelected,
            onClick = { roomType = type },
            label = { Text(type, fontSize = 12.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Suitable For
      Text(
        text = "Suitable For:",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Spacer(modifier = Modifier.height(4.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.SUITABILITY_OPTIONS.filter { it != "All" }.forEach { suit ->
          val isSelected = suitableFor == suit
          FilterChip(
            selected = isSelected,
            onClick = { suitableFor = suit },
            label = { Text(suit, fontSize = 12.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Room Photo Selection Preset
      Text(
        text = "Select Room Photo (कमरे की फ़ोटो चुनें)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Text(
        text = "Pick the real photo style that best matches your room",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RoomConstants.PHOTO_PRESETS.forEach { preset ->
          val isSelected = selectedPhoto == preset.drawableName
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedPhoto = preset.drawableName }
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
              ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RoomPhotoView(
                drawableName = preset.drawableName,
                contentDescription = preset.label,
                modifier = Modifier
                  .size(60.dp)
                  .clip(RoundedCornerShape(8.dp))
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = preset.label,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
              }
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Available Amenities Checkboxes
      Text(
        text = "Amenities Included (उपलब्ध सुविधाएं)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.ALL_AMENITIES.forEach { amenity ->
          val isSelected = selectedAmenities.contains(amenity)
          FilterChip(
            selected = isSelected,
            onClick = {
              selectedAmenities = if (isSelected) selectedAmenities - amenity else selectedAmenities + amenity
            },
            label = { Text(amenity, fontSize = 12.sp) },
            leadingIcon = if (isSelected) {
              { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // House Rules & Gate Timings
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = gateTiming,
          onValueChange = { gateTiming = it },
          label = { Text("Gate Timing") },
          placeholder = { Text("10:30 PM / 24x7") },
          modifier = Modifier.weight(1f),
          singleLine = true
        )

        OutlinedTextField(
          value = noticeDaysText,
          onValueChange = { noticeDaysText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Notice (Days)") },
          modifier = Modifier.weight(1f),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Description
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description / Room Details (कमरे की जानकारी)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 5
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Current Status: Vacant or Rented
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Currently Available / Vacant (कमरा अभी खाली है)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Text(
            text = if (isVacant) "Students can contact you immediately" else "Marked as occupied/rented",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        Switch(
          checked = isVacant,
          onCheckedChange = { isVacant = it }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save / Upload Button
      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
            return@Button
          }
          if (area.isBlank()) {
            areaError = true
            return@Button
          }
          val rent = rentText.toIntOrNull() ?: 4500
          val deposit = depositText.toIntOrNull() ?: rent
          val notice = noticeDaysText.toIntOrNull() ?: 30

          onSave(
            title.trim(),
            description.trim(),
            rent,
            deposit,
            electricityCharge.trim(),
            roomType,
            suitableFor,
            selectedCity,
            area.trim(),
            fullAddress.trim(),
            nearbyLandmark.trim(),
            selectedAmenities.toList(),
            selectedPhoto,
            isVacant,
            notice,
            gateTiming.trim(),
            roomToEdit?.id
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .padding(bottom = 8.dp)
          .testTag("submit_room_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (roomToEdit == null) "Upload Room (कमरा अपलोड करें)" else "Save Changes (अपडेट करें)",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
