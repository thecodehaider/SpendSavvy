package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.NeumorphicBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Top Navigation Bar matching the image header.
 * Has 4-dot grid icon on left, tracked uppercase title in center, and profile icon on right.
 */
@Composable
fun SpendSavvyTopBar(
  title: String = "SPENDSAVVY",
  onMenuClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 24.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // 4-dot grid icon (matches image)
    NeumorphicIconButton(
      onClick = onMenuClick,
      size = 40.dp,
      elevation = 3.dp
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
          Box(modifier = Modifier.size(3.5.dp).background(TextSecondary, CircleShape))
          Box(modifier = Modifier.size(3.5.dp).background(TextSecondary, CircleShape))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
          Box(modifier = Modifier.size(3.5.dp).background(TextSecondary, CircleShape))
          Box(modifier = Modifier.size(3.5.dp).background(TextSecondary, CircleShape))
        }
      }
    }

    // Centered title in uppercase with tracking
    Text(
      text = title.uppercase(),
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 2.5.sp,
      color = TextPrimary
    )

    // Profile Avatar Button (matches image)
    NeumorphicIconButton(
      onClick = onProfileClick,
      size = 40.dp,
      elevation = 3.dp
    ) {
      Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Profile",
        tint = TextSecondary,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

/**
 * Bottom Navigation Bar matching the image.
 * Soft docked pill layout with neumorphic interactive icon buttons.
 */
@Composable
fun SpendSavvyBottomNav(
  activeTab: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 28.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    // Docked Neumorphic Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .neumorphicRaised(
          cornerRadius = 34.dp,
          elevation = 6.dp,
          backgroundColor = NeumorphicBg
        )
        .padding(horizontal = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 0: Home / Expenses
        NavDockItem(
          selected = activeTab == 0,
          onClick = { onTabSelected(0) },
          icon = if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
          contentDescription = "Expenses"
        )

        // Tab 1: Settle Up
        NavDockItem(
          selected = activeTab == 1,
          onClick = { onTabSelected(1) },
          icon = if (activeTab == 1) Icons.Filled.Paid else Icons.Outlined.Paid,
          contentDescription = "Settle Up"
        )

        // Tab 2: Statistics (Equalizer chart icon matching image)
        NavDockItem(
          selected = activeTab == 2,
          onClick = { onTabSelected(2) },
          icon = if (activeTab == 2) Icons.Filled.Analytics else Icons.Outlined.Analytics,
          contentDescription = "Statistics"
        )

        // Tab 3: Group & Pro
        NavDockItem(
          selected = activeTab == 3,
          onClick = { onTabSelected(3) },
          icon = if (activeTab == 3) Icons.Filled.Group else Icons.Outlined.Group,
          contentDescription = "Group"
        )
      }
    }
  }
}

@Composable
private fun NavDockItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDescription: String
) {
  NeumorphicIconButton(
    onClick = onClick,
    size = 46.dp,
    cornerRadius = 23.dp,
    elevation = if (selected) 2.dp else 4.dp,
    backgroundColor = NeumorphicBg
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = if (selected) AccentPurple else TextMuted,
      modifier = Modifier.size(22.dp)
    )
  }
}
