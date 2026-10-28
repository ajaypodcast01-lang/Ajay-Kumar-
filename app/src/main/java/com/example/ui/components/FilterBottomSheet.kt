package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomConstants
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.SortOption
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
  filterState: FilterState,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onApply: (
    city: String,
    minRent: Float,
    maxRent: Float,
    roomType: String,
    suitability: String,
    amenities: Set<String>,
    onlyVacant: Boolean,
    sortOption: SortOption
  ) -> Unit,
  onReset: () -> Unit
) {
  var tempCity by remember(filterState) { mutableStateOf(filterState.selectedCity) }
  var tempMinRent by remember(filterState) { mutableFloatStateOf(filterState.minRent) }
  var tempMaxRent by remember(filterState) { mutableFloatStateOf(filterState.maxRent) }
  var tempRoomType by remember(filterState) { mutableStateOf(filterState.selectedRoomType) }
  var tempSuitability by remember(filterState) { mutableStateOf(filterState.selectedSuitability) }
  var tempAmenities by remember(filterState) { mutableStateOf(filterState.selectedAmenities) }
  var tempOnlyVacant by remember(filterState) { mutableStateOf(filterState.onlyVacant) }
  var tempSort by remember(filterState) { mutableStateOf(filterState.sortOption) }

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
        .testTag("filter_bottom_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Filter Rooms (कमरा फ़िल्टर)",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(
          onClick = {
            onReset()
            tempCity = "All Cities"
            tempMinRent = 1000f
            tempMaxRent = 15000f
            tempRoomType = "All Types"
            tempSuitability = "All"
            tempAmenities = emptySet()
            tempOnlyVacant = false
            tempSort = SortOption.PRICE_LOW_TO_HIGH
          },
          modifier = Modifier.testTag("reset_filters_button")
        ) {
          Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset all")
        }
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

      // Location / City
      Text(
        text = "City / शहर",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val cityOptions = listOf("All Cities") + RoomConstants.CITIES
        cityOptions.forEach { city ->
          val isSelected = tempCity == city
          FilterChip(
            selected = isSelected,
            onClick = { tempCity = city },
            label = { Text(city, fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("filter_city_$city")
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Rent Range
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Monthly Rent Range (किराया)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "₹${tempMinRent.roundToInt()} - ₹${tempMaxRent.roundToInt()}",
          style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      RangeSlider(
        value = tempMinRent..tempMaxRent,
        onValueChange = { range ->
          tempMinRent = (range.start / 250).roundToInt() * 250f
          tempMaxRent = (range.endInclusive / 250).roundToInt() * 250f
        },
        valueRange = 1000f..15000f,
        steps = 27,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rent_range_slider")
      )

      // Quick budget pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val presets = listOf(
          "Under ₹4k" to (1000f to 4000f),
          "₹4k - ₹7k" to (4000f to 7000f),
          "Above ₹7k" to (7000f to 15000f)
        )
        presets.forEach { (label, range) ->
          OutlinedButton(
            onClick = {
              tempMinRent = range.first
              tempMaxRent = range.second
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text(label, fontSize = 11.sp, maxLines = 1)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Available Amenities
      Text(
        text = "Available Amenities (सुविधाएं)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.ALL_AMENITIES.forEach { amenity ->
          val isSelected = tempAmenities.contains(amenity)
          FilterChip(
            selected = isSelected,
            onClick = {
              tempAmenities = if (isSelected) tempAmenities - amenity else tempAmenities + amenity
            },
            label = { Text(amenity, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("filter_amenity_${amenity.replace(" ", "_")}")
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Room Type
      Text(
        text = "Room Type (कमरा का प्रकार)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.ROOM_TYPES.forEach { type ->
          val isSelected = tempRoomType == type
          FilterChip(
            selected = isSelected,
            onClick = { tempRoomType = type },
            label = { Text(type, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Suitable For
      Text(
        text = "Suitable For (किसके लिए)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoomConstants.SUITABILITY_OPTIONS.forEach { suit ->
          val isSelected = tempSuitability == suit
          FilterChip(
            selected = isSelected,
            onClick = { tempSuitability = suit },
            label = { Text(suit, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Only Vacant Switch
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Show Only Vacant (सिर्फ खाली कमरे)",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
          )
          Text(
            text = "Hide rooms that are already rented out",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        Switch(
          checked = tempOnlyVacant,
          onCheckedChange = { tempOnlyVacant = it },
          modifier = Modifier.testTag("only_vacant_switch")
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Sort By
      Text(
        text = "Sort Results By",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SortOption.entries.forEach { opt ->
          val isSelected = tempSort == opt
          FilterChip(
            selected = isSelected,
            onClick = { tempSort = opt },
            label = { Text(opt.label, fontSize = 12.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            onApply(
              tempCity,
              tempMinRent,
              tempMaxRent,
              tempRoomType,
              tempSuitability,
              tempAmenities,
              tempOnlyVacant,
              tempSort
            )
          },
          modifier = Modifier
            .weight(1.5f)
            .testTag("apply_filters_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
