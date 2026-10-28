package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomConstants
import com.example.data.model.RoomListingEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.RoomCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.viewmodel.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  rooms: List<RoomListingEntity>,
  filterState: FilterState,
  favoriteIds: Set<Long>,
  activeUser: UserEntity,
  onSearchQueryChange: (String) -> Unit,
  onCitySelect: (String) -> Unit,
  onOpenFilterSheet: () -> Unit,
  onClearFilters: () -> Unit,
  onApplyPreferences: () -> Unit,
  onToggleFavorite: (Long) -> Unit,
  onRoomClick: (RoomListingEntity) -> Unit,
  onMessageRoom: ((RoomListingEntity) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  // Count active filters (non-default)
  val activeFilterCount = (if (filterState.selectedCity != "All Cities") 1 else 0) +
      (if (filterState.minRent > 1000f || filterState.maxRent < 15000f) 1 else 0) +
      (if (filterState.selectedRoomType != "All Types") 1 else 0) +
      (if (filterState.selectedSuitability != "All") 1 else 0) +
      filterState.selectedAmenities.size +
      (if (filterState.onlyVacant) 1 else 0)

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen")
  ) {
    // Top Search & Filter Bar Section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(top = 8.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    ) {
      // Search Bar Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = filterState.query,
          onValueChange = onSearchQueryChange,
          modifier = Modifier
            .weight(1f)
            .testTag("search_rooms_input"),
          placeholder = {
            Text(
              text = "Search by City, Area, Landmark...",
              fontSize = 14.sp,
              maxLines = 1
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.primary
            )
          },
          trailingIcon = {
            if (filterState.query.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChange("") }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear search",
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background
          )
        )

        // Filter Sheet Trigger Button with Badge
        Surface(
          onClick = onOpenFilterSheet,
          shape = RoundedCornerShape(14.dp),
          color = if (activeFilterCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.testTag("open_filters_button")
        ) {
          BadgedBox(
            badge = {
              if (activeFilterCount > 0) {
                Badge(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = Color.White
                ) {
                  Text("$activeFilterCount")
                }
              }
            },
            modifier = Modifier.padding(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Filters",
              tint = if (activeFilterCount > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // City Quick Pills Scroll
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val cities = listOf("All Cities") + RoomConstants.CITIES
        cities.forEach { city ->
          val isSelected = filterState.selectedCity == city
          FilterChip(
            selected = isSelected,
            onClick = { onCitySelect(city) },
            label = { Text(city, fontSize = 12.sp) },
            leadingIcon = if (city != "All Cities") {
              {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
              }
            } else null,
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = Color.White,
              selectedLeadingIconColor = Color.White
            ),
            modifier = Modifier.testTag("quick_city_chip_$city")
          )
        }
      }

      // Tenant Saved Preferences Quick Apply Pill (if Tenant)
      if (activeUser.role == UserRole.TENANT) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onApplyPreferences() }
            .background(
              color = AmberAccent.copy(alpha = 0.12f),
              shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = AmberAccent,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Apply My Saved Preferences (${activeUser.preferredRoomType} in ${activeUser.city}, under ₹${activeUser.preferredBudgetMax})",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
          )
        }
      }

      // Active filters summary & Clear Button if active
      if (activeFilterCount > 0) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "$activeFilterCount filters active",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Clear All Filters",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clickable { onClearFilters() }
              .padding(4.dp)
              .testTag("clear_filters_text")
          )
        }
      }
    }

    // Results Header Count
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (filterState.selectedCity == "All Cities") {
          "Available Rooms (${rooms.size})"
        } else {
          "Rooms in ${filterState.selectedCity} (${rooms.size})"
        },
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      Text(
        text = "Direct from Room Malik",
        style = MaterialTheme.typography.bodySmall.copy(
          color = EmeraldSuccess,
          fontWeight = FontWeight.SemiBold
        )
      )
    }

    // Rooms List or Empty State
    if (rooms.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(36.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Koi Room Nahi Mila (No Rooms Found)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Try searching for a different city, increasing the rent budget, or clearing your amenity filters.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onClearFilters,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Reset All Filters")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(
          items = rooms,
          key = { it.id }
        ) { room ->
          RoomCard(
            room = room,
            isFavorite = favoriteIds.contains(room.id),
            onFavoriteClick = { onToggleFavorite(room.id) },
            onClick = { onRoomClick(room) },
            onMessageClick = if (onMessageRoom != null) { { onMessageRoom(room) } } else null
          )
        }
      }
    }
  }
}
