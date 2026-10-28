package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun AppLogoView(
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp)
) {
  val context = LocalContext.current
  val logoResId = rememberLogoDrawableId(context)

  Box(
    modifier = modifier
      .size(size)
      .clip(shape)
      .background(
        Brush.linearGradient(
          listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    if (logoResId != 0) {
      Image(
        painter = painterResource(id = logoResId),
        contentDescription = "My Room App Logo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size)
      )
    } else {
      Icon(
        imageVector = Icons.Default.Home,
        contentDescription = "My Room",
        tint = Color.White,
        modifier = Modifier.size(size * 0.6f)
      )
    }
  }
}

@Composable
fun AppHeaderBrand(modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    AppLogoView(size = 38.dp)
    Spacer(modifier = Modifier.width(10.dp))
    androidx.compose.foundation.layout.Column {
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
}

private fun rememberLogoDrawableId(context: android.content.Context): Int {
  val id = context.resources.getIdentifier(
    "ic_launcher_fg_1793006565902",
    "drawable",
    context.packageName
  )
  return id
}
