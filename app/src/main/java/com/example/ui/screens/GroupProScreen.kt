package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.neumorphicInset
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurpleDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SpendSavvyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GroupProScreen(
  viewModel: SpendSavvyViewModel,
  modifier: Modifier = Modifier
) {
  val members by viewModel.members.collectAsStateWithLifecycle()
  val memberBalances by viewModel.memberBalances.collectAsStateWithLifecycle()
  val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
  val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()

  val activeGroup = allGroups.find { it.id == selectedGroupId } ?: allGroups.firstOrNull()
  val isPro = activeGroup?.isPro == true

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  var newMemberName by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = activeGroup?.name ?: "Groups & Members",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = if (activeGroup != null) "${members.size} Members · ${activeGroup.type}" else "No active group",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
          )
        }

        NeumorphicIconButton(
          onClick = { viewModel.showCreateGroup(true) },
          size = 40.dp,
          elevation = 4.dp
        ) {
          Icon(
            imageVector = Icons.Default.GroupAdd,
            contentDescription = "New Group",
            tint = AccentPurple,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // SpendSavvy Pro Subscription Feature (Monetization from user request)
    item {
      NeumorphicCard(
        cornerRadius = 22.dp,
        elevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(listOf(AccentPurple, AccentPurpleDark))
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "Pro",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = "SpendSavvy Pro",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = "$4.99/mo per group",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AccentPurple
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPro) AccentGreen.copy(alpha = 0.15f) else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isPro) "PRO ACTIVE" else "FREE TIER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPro) AccentGreen else TextMuted
              )
            }
          }

          ProPerkRow(
            icon = Icons.Default.Repeat,
            title = "Recurring Bills Automation",
            subtitle = "Automatic monthly rent, WiFi & utility splitting"
          )
          ProPerkRow(
            icon = Icons.Default.DocumentScanner,
            title = "Smart Receipt Scanning",
            subtitle = "OCR itemized split from grocery and restaurant receipts"
          )
          ProPerkRow(
            icon = Icons.Default.Shield,
            title = "Zero Friction Guarantee",
            subtitle = "Automatic P2P settlement reminders with Venmo sync"
          )

          NeumorphicButton(
            onClick = {
              activeGroup?.let { viewModel.toggleGroupPro(it.id) }
            },
            cornerRadius = 14.dp,
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (isPro) "Active Subscription (Tap to Toggle)" else "Upgrade Group for $4.99/mo",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (isPro) AccentGreen else AccentPurple
            )
          }
        }
      }
    }

    // Add Member Section
    if (activeGroup != null) {
      item {
        NeumorphicCard(
          cornerRadius = 18.dp,
          elevation = 4.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "Add Housemate / Friend",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .neumorphicInset(cornerRadius = 12.dp, depth = 2.dp)
                  .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                if (newMemberName.isEmpty()) {
                  Text(text = "Housemate name", color = TextMuted, fontSize = 13.sp)
                }
                BasicTextField(
                  value = newMemberName,
                  onValueChange = { newMemberName = it },
                  textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
                  cursorBrush = SolidColor(AccentPurple),
                  modifier = Modifier.fillMaxWidth().testTag("new_member_input")
                )
              }

              NeumorphicButton(
                onClick = {
                  if (newMemberName.isNotBlank()) {
                    viewModel.addMemberToGroup(newMemberName)
                    newMemberName = ""
                  }
                },
                cornerRadius = 12.dp,
                elevation = 3.dp
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = AccentPurple,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "Add",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                  )
                }
              }
            }
          }
        }
      }
    }

    // Members in group
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Housemates & Balances",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }

    if (members.isEmpty()) {
      item {
        Text(
          text = "No members in this group yet.",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }
    } else {
      items(members) { member ->
        val balance = memberBalances.find { it.member.id == member.id }?.netBalance ?: 0.0

        NeumorphicCard(
          cornerRadius = 16.dp,
          elevation = 4.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color(android.graphics.Color.parseColor(member.avatarColorHex))),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = member.name.take(1),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }

              Column {
                Text(
                  text = member.name,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Text(
                  text = if (member.isCurrentUser) "Primary Account" else "Housemate",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Text(
              text = when {
                balance > 0.01 -> "+${currencyFormat.format(balance)}"
                balance < -0.01 -> "-${currencyFormat.format(-balance)}"
                else -> "$0.00"
              },
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (balance >= 0) AccentGreen else AccentPink
            )
          }
        }
      }
    }

    // Clear All Data Option (for testing fresh APK state)
    item {
      Spacer(modifier = Modifier.height(10.dp))
      NeumorphicButton(
        onClick = { viewModel.clearAllData() },
        cornerRadius = 16.dp,
        elevation = 3.dp,
        modifier = Modifier.fillMaxWidth().testTag("clear_all_data_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Clear All Data",
            tint = AccentPink,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "Clear All Data (Reset for Testing)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentPink
          )
        }
      }
    }
  }
}

@Composable
private fun ProPerkRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(AccentPurple.copy(alpha = 0.1f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = AccentPurple,
        modifier = Modifier.size(18.dp)
      )
    }

    Column {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = TextSecondary
      )
    }
  }
}
