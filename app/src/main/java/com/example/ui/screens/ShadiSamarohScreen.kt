package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShadiLocationEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import java.net.URLEncoder

@Composable
fun ShadiSamarohScreen(
  shadiList: List<ShadiLocationEntity>,
  onAddShadiClick: () -> Unit,
  onDeleteShadi: (Long) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf("TODAY") } // TODAY, HISTORY
  var selectedArea by remember { mutableStateOf("All Areas") }

  val allAreas = listOf("All Areas") + shadiList.map { it.area }.distinct()

  val filteredList = shadiList.filter { item ->
    val areaMatches = selectedArea == "All Areas" || item.area.equals(selectedArea, ignoreCase = true)
    val tabMatches = if (selectedTab == "TODAY") item.isToday else true
    areaMatches && tabMatches
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("shadi_samaroh_screen")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(AmberAccent.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Shadi Samaroh (शादी लोकेशन)",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              Text(
                text = "Track active wedding feasts & locations in your student area",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }

          IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Today Active vs History
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedTab == "TODAY",
            onClick = { selectedTab = "TODAY" },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today's Active Functions (${shadiList.count { it.isToday }})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
          )

          FilterChip(
            selected = selectedTab == "HISTORY",
            onClick = { selectedTab = "HISTORY" },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Full History (${shadiList.size})", fontSize = 11.sp)
              }
            },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Area Filter Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          allAreas.take(4).forEach { area ->
            val isSelected = selectedArea == area
            FilterChip(
              selected = isSelected,
              onClick = { selectedArea = area },
              label = { Text(area, fontSize = 11.sp) }
            )
          }
        }
      }

      // Shadi List
      if (filteredList.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(AmberAccent.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(30.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "No Shadi Locations Reported Yet in $selectedArea",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Saw a wedding tent or banquet feast in your coaching area today? Add the location to notify fellow students!",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddShadiClick) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Shadi Location Today (शादी लोकेशन जोड़ें)")
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(
            items = filteredList,
            key = { it.id }
          ) { shadi ->
            ShadiCard(
              shadi = shadi,
              onShare = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_SUBJECT, "Shadi Location in ${shadi.area}: ${shadi.venueName}")
                  putExtra(Intent.EXTRA_TEXT, shadi.toShareableText())
                }
                context.startActivity(Intent.createChooser(intent, "Share Shadi Location with Friends"))
              },
              onOpenMap = {
                try {
                  val query = URLEncoder.encode("${shadi.venueName}, ${shadi.area}, ${shadi.city}", "UTF-8")
                  val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
                  context.startActivity(mapIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open map", Toast.LENGTH_SHORT).show()
                }
              },
              onDelete = { onDeleteShadi(shadi.id) }
            )
          }
        }
      }
    }

    // Floating Button to Add Shadi Location Today
    FloatingActionButton(
      onClick = onAddShadiClick,
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("fab_add_shadi_location")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add shadi")
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Shadi Today (शादी जोड़ें)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }
    }
  }
}

@Composable
private fun ShadiCard(
  shadi: ShadiLocationEntity,
  onShare: () -> Unit,
  onOpenMap: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top row: Event Title & Today badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = shadi.venueName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
          )
          Text(
            text = shadi.title,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AmberAccent.copy(alpha = 0.2f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Celebration,
              contentDescription = null,
              tint = Color(0xFFB45309),
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (shadi.isToday) "ACTIVE TODAY" else "PAST FUNCTION",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB45309)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Location & Landmark
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${shadi.area}, ${shadi.city} (${shadi.landmark})",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Date, Day & Timing
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${shadi.dayOfWeek}, ${shadi.dateStr}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = EmeraldSuccess,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = shadi.timing,
            fontSize = 11.sp,
            color = EmeraldSuccess,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Student Tip / Food Note
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .size(16.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Student Info & Feast Tips:",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = shadi.studentNote,
              style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp)
            )
            Text(
              text = "Reported by: ${shadi.addedByStudent}",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.outline,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Actions: Map & Share with roommates
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onOpenMap,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("View Map", fontSize = 11.sp)
        }

        Button(
          onClick = onShare,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share (दोस्तों को बताएं)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShadiLocationDialog(
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSave: (
    venueName: String,
    title: String,
    city: String,
    area: String,
    landmark: String,
    functionType: String,
    timing: String,
    studentNote: String
  ) -> Unit
) {
  val context = LocalContext.current
  var venueName by remember { mutableStateOf("") }
  var title by remember { mutableStateOf("Grand Wedding & Bhoj (विवाह व प्रीतिभोज)") }
  var city by remember { mutableStateOf("Kota") }
  var area by remember { mutableStateOf("Rajeev Gandhi Nagar") }
  var landmark by remember { mutableStateOf("") }
  var timing by remember { mutableStateOf("7:30 PM - 11:30 PM (Dinner)") }
  var studentNote by remember { mutableStateOf("Grand dinner buffet, unlimited sweets, spacious banquet lawn.") }

  val functionTypes = listOf("Wedding (शादी)", "Reception (प्रीतिभोज)", "Tilak (तिलक)", "Dinner Feast (दावत)")
  var selectedType by remember { mutableStateOf(functionTypes.first()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("add_shadi_dialog")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Add Shadi Location Today",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "आज का शादी समारोह व लोकेशन जोड़ें (Notify other students)",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = venueName,
        onValueChange = { venueName = it },
        label = { Text("Marriage Garden / Banquet / Venue Name *") },
        placeholder = { Text("e.g. Rajwada Marriage Garden, Utsav Vatika") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = area,
        onValueChange = { area = it },
        label = { Text("Area / Coaching Hub *") },
        placeholder = { Text("e.g. Rajeev Gandhi Nagar, Mukherjee Nagar, Boring Road") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = landmark,
        onValueChange = { landmark = it },
        label = { Text("Nearby Landmark") },
        placeholder = { Text("e.g. Near Allen Samyak / City Mall") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Function Type Selector
      Text(
        text = "Function Type (समारोह प्रकार):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        functionTypes.forEach { type ->
          val isSelected = selectedType == type
          FilterChip(
            selected = isSelected,
            onClick = { selectedType = type },
            label = { Text(type, fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = timing,
        onValueChange = { timing = it },
        label = { Text("Timing / Dinner Hours") },
        placeholder = { Text("e.g. 7:30 PM - 11:30 PM") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = studentNote,
        onValueChange = { studentNote = it },
        label = { Text("Feast Tips & Student Notes (खाने व दावत की जानकारी)") },
        placeholder = { Text("e.g. Huge buffet, sweets, entry details") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 4
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          if (venueName.trim().isBlank() || area.trim().isBlank()) {
            Toast.makeText(context, "Please enter venue name and area", Toast.LENGTH_SHORT).show()
            return@Button
          }
          onSave(
            venueName.trim(),
            title.trim(),
            city.trim(),
            area.trim(),
            landmark.trim(),
            selectedType,
            timing.trim(),
            studentNote.trim()
          )
          Toast.makeText(context, "Shadi Location Added Today! Other students in $area will be notified.", Toast.LENGTH_LONG).show()
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Icon(imageVector = Icons.Default.Celebration, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Publish Shadi Location Today", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
