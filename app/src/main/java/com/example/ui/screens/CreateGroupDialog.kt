package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.NeumorphicBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SpendSavvyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
  viewModel: SpendSavvyViewModel,
  onDismiss: () -> Unit
) {
  var groupName by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf("Apartment") }
  var membersText by remember { mutableStateOf("Jordan, Sam, Taylor") }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = NeumorphicBg,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Create New Group",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        NeumorphicIconButton(
          onClick = onDismiss,
          size = 36.dp,
          elevation = 3.dp
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Group Name
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Group Name",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .neumorphicInset(cornerRadius = 14.dp, depth = 3.dp)
            .padding(horizontal = 16.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          if (groupName.isEmpty()) {
            Text(text = "e.g. Apartment 4B, Tahoe Ski Trip", color = TextMuted, fontSize = 14.sp)
          }
          BasicTextField(
            value = groupName,
            onValueChange = { groupName = it },
            textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
            cursorBrush = SolidColor(AccentPurple),
            modifier = Modifier.fillMaxWidth().testTag("group_name_input")
          )
        }
      }

      // Type Chips
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Group Type",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        val types = listOf("Apartment", "Housemates", "Trip", "Couple", "Project")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          types.forEach { type ->
            val isSelected = selectedType == type
            Box(
              modifier = Modifier
                .neumorphicRaised(
                  cornerRadius = 12.dp,
                  elevation = if (isSelected) 2.dp else 4.dp,
                  isPressed = isSelected
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable { selectedType = type }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = type,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentPurple else TextSecondary
              )
            }
          }
        }
      }

      // Members
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Add Housemates / Friends (comma separated)",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .neumorphicInset(cornerRadius = 14.dp, depth = 3.dp)
            .padding(horizontal = 16.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          if (membersText.isEmpty()) {
            Text(text = "Jordan, Sam, Taylor", color = TextMuted, fontSize = 14.sp)
          }
          BasicTextField(
            value = membersText,
            onValueChange = { membersText = it },
            textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
            cursorBrush = SolidColor(AccentPurple),
            modifier = Modifier.fillMaxWidth().testTag("group_members_input")
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      val canSubmit = groupName.isNotBlank()
      NeumorphicButton(
        onClick = {
          if (canSubmit) {
            val list = membersText.split(",").map { it.trim() }.filter { it.isNotBlank() }
            viewModel.createGroup(
              name = groupName.trim(),
              type = selectedType,
              memberNames = list
            )
          }
        },
        cornerRadius = 16.dp,
        elevation = 5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_group_button")
      ) {
        Text(
          text = "Create Group",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (canSubmit) AccentPurple else TextMuted
        )
      }
    }
  }
}
