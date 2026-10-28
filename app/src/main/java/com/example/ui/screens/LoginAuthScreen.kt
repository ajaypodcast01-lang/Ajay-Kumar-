package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomConstants
import com.example.data.model.UserRole
import com.example.ui.components.AppLogoView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginAuthScreen(
  onLoginSuccess: (name: String, phone: String, role: UserRole, city: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf(UserRole.TENANT) }
  var selectedCity by remember { mutableStateOf("Kota") }
  var phoneError by remember { mutableStateOf<String?>(null) }
  var nameError by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface)
      .testTag("login_auth_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // App Logo and "My Room" Header
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(RoundedCornerShape(22.dp))
          .background(
            Brush.linearGradient(
              listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
              )
            )
          )
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        AppLogoView(size = 80.dp, shape = RoundedCornerShape(18.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "My Room",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp,
          color = MaterialTheme.colorScheme.primary
        )
      )

      Text(
        text = "Student Room Search & Direct Rent App",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = EmeraldSuccess.copy(alpha = 0.12f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = EmeraldSuccess,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Zero Brokerage • Verified Rent Receipts",
              fontSize = 11.sp,
              color = EmeraldSuccess,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Main Login Card
      ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Login with Mobile Number (लॉग इन करें)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Enter your full name and mobile number to start",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Full Name Input
          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              if (nameError != null) nameError = null
            },
            label = { Text("Full Name (पूरा नाम) *") },
            placeholder = { Text("e.g. Rahul Sharma or Rajesh Kumar") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_name_input"),
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Mobile Number Input with +91 prefix
          OutlinedTextField(
            value = phone,
            onValueChange = { input ->
              val clean = input.filter { it.isDigit() }
              if (clean.length <= 10) {
                phone = clean
              }
              if (phoneError != null) phoneError = null
            },
            label = { Text("Mobile Number (मोबाइल नंबर) *") },
            placeholder = { Text("9876543210") },
            leadingIcon = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Phone,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "+91",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
                )
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_phone_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneError != null,
            supportingText = phoneError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Select Role
          Text(
            text = "Select Your Role (आप कौन हैं?):",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Student / Tenant option
            RoleCard(
              title = "Student / Tenant",
              hindiTitle = "छात्र / किरायेदार",
              desc = "Find rooms, pay rent, get rent receipts",
              icon = Icons.Default.School,
              isSelected = selectedRole == UserRole.TENANT,
              onClick = { selectedRole = UserRole.TENANT },
              modifier = Modifier
                .weight(1f)
                .testTag("role_select_tenant")
            )

            // Room Owner option
            RoleCard(
              title = "Room Owner",
              hindiTitle = "कमरा मालिक",
              desc = "List rooms, set rent, pay ₹250 fee to app owner",
              icon = Icons.Default.Home,
              isSelected = selectedRole == UserRole.ROOM_OWNER,
              onClick = { selectedRole = UserRole.ROOM_OWNER },
              modifier = Modifier
                .weight(1f)
                .testTag("role_select_owner")
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // City selector
          Text(
            text = "Select City (शहर):",
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            RoomConstants.CITIES.take(6).forEach { c ->
              val isSelected = selectedCity == c
              FilterChip(
                selected = isSelected,
                onClick = { selectedCity = c },
                label = { Text(c, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Button
          Button(
            onClick = {
              var valid = true
              if (name.trim().isBlank()) {
                nameError = "Please enter your name"
                valid = false
              }
              if (phone.trim().length != 10) {
                phoneError = "Please enter valid 10-digit mobile number"
                valid = false
              }
              if (valid) {
                onLoginSuccess(name.trim(), phone.trim(), selectedRole, selectedCity)
                Toast.makeText(
                  context,
                  "Welcome to My Room, ${name.trim()}!",
                  Toast.LENGTH_SHORT
                ).show()
              }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("login_submit_button")
          ) {
            Text(
              text = "Continue to My Room (लॉग इन करें)",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Demo One-Tap Logins
      Text(
        text = "Or quick test with demo profiles:",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            onLoginSuccess("Rahul Sharma", "9876543210", UserRole.TENANT, "Kota")
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_demo_student")
        ) {
          Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Demo Student", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = {
            onLoginSuccess("Rajesh Kumar (Malik)", "9811223344", UserRole.ROOM_OWNER, "Kota")
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_demo_owner")
        ) {
          Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Demo Room Malik", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun RoleCard(
  title: String,
  hindiTitle: String,
  desc: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(14.dp)
      ),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      }
    )
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .background(
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
          fontSize = 13.sp
        )
      )
      Text(
        text = hindiTitle,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = desc,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 12.sp
      )
    }
  }
}
