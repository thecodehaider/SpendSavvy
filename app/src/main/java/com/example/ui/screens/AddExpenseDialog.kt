package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun AddExpenseDialog(
  viewModel: SpendSavvyViewModel,
  onDismiss: () -> Unit
) {
  val members by viewModel.members.collectAsStateWithLifecycle()

  var title by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Groceries") }
  var paidByMemberId by remember(members) { mutableStateOf(members.firstOrNull()?.id ?: 0L) }
  var isRecurring by remember { mutableStateOf(false) }

  val effectivePaidBy = if (members.any { it.id == paidByMemberId }) paidByMemberId else (members.firstOrNull()?.id ?: 0L)

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
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Add Shared Expense",
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

      // Title Input (recessed neumorphic)
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Expense Name",
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
          if (title.isEmpty()) {
            Text(text = "e.g. Whole Foods Groceries, Rent, Wifi", color = TextMuted, fontSize = 14.sp)
          }
          BasicTextField(
            value = title,
            onValueChange = { title = it },
            textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
            cursorBrush = SolidColor(AccentPurple),
            modifier = Modifier.fillMaxWidth().testTag("expense_title_input")
          )
        }
      }

      // Amount Input (recessed neumorphic)
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Amount ($)",
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
          if (amountText.isEmpty()) {
            Text(text = "0.00", color = TextMuted, fontSize = 16.sp)
          }
          BasicTextField(
            value = amountText,
            onValueChange = { amountText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = TextStyle(fontSize = 18.sp, color = TextPrimary, fontWeight = FontWeight.Bold),
            cursorBrush = SolidColor(AccentPurple),
            modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
          )
        }
      }

      // Category Picker Chips
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Category",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        val categories = listOf("Groceries", "Rent", "Dining", "Utilities", "Entertainment", "Other")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
              modifier = Modifier
                .neumorphicRaised(
                  cornerRadius = 12.dp,
                  elevation = if (isSelected) 2.dp else 4.dp,
                  isPressed = isSelected
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable { selectedCategory = cat }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentPurple else TextSecondary
              )
            }
          }
        }
      }

      // Paid By Member
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Paid By",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          members.forEach { member ->
            val isSelected = paidByMemberId == member.id
            Row(
              modifier = Modifier
                .neumorphicRaised(
                  cornerRadius = 14.dp,
                  elevation = if (isSelected) 2.dp else 4.dp,
                  isPressed = isSelected
                )
                .clip(RoundedCornerShape(14.dp))
                .clickable { paidByMemberId = member.id }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(Color(android.graphics.Color.parseColor(member.avatarColorHex))),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = member.name.take(1),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
              Text(
                text = member.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentPurple else TextSecondary
              )
            }
          }
        }
      }

      // Recurring bill toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Recurring Monthly Bill",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Text(
            text = "Auto-repeat every month (e.g. Rent, Fiber WiFi)",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
        Switch(
          checked = isRecurring,
          onCheckedChange = { isRecurring = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = AccentPurple,
            uncheckedTrackColor = NeumorphicBg
          )
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Submit Button
      val amountVal = amountText.toDoubleOrNull() ?: 0.0
      val canSubmit = title.isNotBlank() && amountVal > 0.0 && effectivePaidBy > 0L

      NeumorphicButton(
        onClick = {
          if (canSubmit) {
            viewModel.addExpense(
              title = title.trim(),
              amount = amountVal,
              paidByMemberId = effectivePaidBy,
              category = selectedCategory,
              splitMemberIds = members.map { it.id },
              isRecurring = isRecurring
            )
          }
        },
        cornerRadius = 16.dp,
        elevation = 5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_expense_button")
      ) {
        Text(
          text = "Save Expense & Split with Group",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (canSubmit) AccentPurple else TextMuted
        )
      }
    }
  }
}
