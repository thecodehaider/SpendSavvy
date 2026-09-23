package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupSummary
import com.example.data.model.MemberEntity
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurpleDark
import com.example.ui.theme.NeumorphicBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SpendSavvyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpensesScreen(
  viewModel: SpendSavvyViewModel,
  onNavigateToSettle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
  val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()
  val activeGroup = allGroups.find { it.id == selectedGroupId } ?: allGroups.firstOrNull()

  val expenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
  val members by viewModel.members.collectAsStateWithLifecycle()
  val summary by viewModel.groupSummary.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  Box(modifier = modifier.fillMaxSize()) {
    if (activeGroup == null) {
      // Empty State: No Group created yet
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(28.dp),
        contentAlignment = Alignment.Center
      ) {
        NeumorphicCard(
          cornerRadius = 24.dp,
          elevation = 6.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(AccentPurple.copy(alpha = 0.12f))
                .neumorphicInset(cornerRadius = 30.dp, depth = 3.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.GroupAdd,
                contentDescription = "Create Group",
                tint = AccentPurple,
                modifier = Modifier.size(30.dp)
              )
            }

            Text(
              text = "Welcome to SpendSavvy",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Start by creating a group for your apartment, housemates, or trip to track shared expenses and settle up effortlessly.",
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              lineHeight = 18.sp
            )

            NeumorphicButton(
              onClick = { viewModel.showCreateGroup(true) },
              cornerRadius = 16.dp,
              elevation = 4.dp,
              modifier = Modifier.fillMaxWidth().testTag("initial_create_group_button")
            ) {
              Text(
                text = "Create Your First Group",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple
              )
            }
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Hero Section
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = activeGroup.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "${members.size} member${if (members.size == 1) "" else "s"}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
              )
            }

            NeumorphicButton(
              onClick = onNavigateToSettle,
              cornerRadius = 14.dp,
              elevation = 4.dp
            ) {
              Text(
                text = "Settle",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentPurple
              )
            }
          }
        }

        // 2. Metrics Summary Card
        item {
          NeumorphicCard(
            cornerRadius = 20.dp,
            elevation = 5.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Total Spent",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextSecondary
                )
                Text(
                  text = currencyFormat.format(summary.totalExpenses),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Your Share",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextSecondary
                )
                Text(
                  text = currencyFormat.format(summary.userOwedTotal.coerceAtLeast(summary.userOwesTotal)),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Net Balance",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextSecondary
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  val intensityRatio = if (summary.totalExpenses > 0) summary.settledPercentage.coerceIn(0.1f, 1f) else 0.05f
                  Box(
                    modifier = Modifier
                      .width(70.dp)
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp))
                      .background(NeumorphicBg)
                      .neumorphicInset(cornerRadius = 3.dp, depth = 1.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(intensityRatio)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                          Brush.horizontalGradient(
                            listOf(AccentAmber, AccentPink)
                          )
                        )
                    )
                  }

                  val net = summary.userNetBalance
                  val netText = when {
                    net > 0.01 -> "+${currencyFormat.format(net)} (Owed)"
                    net < -0.01 -> "-${currencyFormat.format(-net)} (You owe)"
                    else -> "$0.00 (Even)"
                  }
                  Text(
                    text = netText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (net >= 0) AccentGreen else AccentPink
                  )
                }
              }
            }
          }
        }

        // 3. Category Filter Chips
        item {
          val categories = listOf("All", "Rent", "Groceries", "Dining", "Utilities")
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            categories.forEach { cat ->
              val isSelected = selectedCategory == cat
              Box(
                modifier = Modifier
                  .neumorphicRaised(
                    cornerRadius = 14.dp,
                    elevation = if (isSelected) 2.dp else 4.dp,
                    isPressed = isSelected
                  )
                  .clip(RoundedCornerShape(14.dp))
                  .clickable { viewModel.setCategoryFilter(cat) }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
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

        // 4. Empty state or Expense Items
        if (expenses.isEmpty()) {
          item {
            NeumorphicCard(
              cornerRadius = 20.dp,
              elevation = 3.dp,
              modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ReceiptLong,
                  contentDescription = "No Expenses",
                  tint = TextMuted,
                  modifier = Modifier.size(36.dp)
                )
                Text(
                  text = "No shared expenses yet",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Text(
                  text = "Tap the + button below to add your first expense and split it with your group.",
                  fontSize = 12.sp,
                  color = TextSecondary,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        } else {
          items(expenses, key = { it.id }) { expense ->
            ExpenseItemCard(
              expense = expense,
              members = members,
              currencyFormat = currencyFormat,
              onToggleSettled = { viewModel.toggleExpenseSettled(expense) }
            )
          }
        }
      }

      // Floating Action Button to add expense
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 24.dp, bottom = 86.dp)
      ) {
        NeumorphicIconButton(
          onClick = { viewModel.showAddExpense(true) },
          size = 56.dp,
          cornerRadius = 28.dp,
          elevation = 6.dp,
          modifier = Modifier.testTag("add_expense_fab")
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(AccentPurple, AccentPurpleDark)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Expense",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ExpenseItemCard(
  expense: ExpenseEntity,
  members: List<MemberEntity>,
  currencyFormat: NumberFormat,
  onToggleSettled: () -> Unit,
  modifier: Modifier = Modifier
) {
  val payer = members.find { it.id == expense.paidByMemberId }?.name ?: "Someone"
  val categoryIcon = getCategoryIcon(expense.category)
  val categoryColor = getCategoryColor(expense.category)

  NeumorphicCard(
    cornerRadius = 18.dp,
    elevation = 4.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(categoryColor.copy(alpha = 0.12f))
            .neumorphicInset(cornerRadius = 12.dp, depth = 2.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = categoryIcon,
            contentDescription = expense.category,
            tint = categoryColor,
            modifier = Modifier.size(24.dp)
          )
        }

        Column {
          Text(
            text = expense.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${currencyFormat.format(expense.amount)} · Paid by $payer",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
          )
        }
      }

      NeumorphicIconButton(
        onClick = onToggleSettled,
        size = 38.dp,
        cornerRadius = 12.dp,
        elevation = 3.dp
      ) {
        if (expense.isSettled) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Settled",
            tint = AccentGreen,
            modifier = Modifier.size(16.dp)
          )
        } else {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Details",
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

private fun getCategoryIcon(category: String): ImageVector {
  return when (category.lowercase()) {
    "rent" -> Icons.Default.Home
    "groceries" -> Icons.Default.ShoppingBag
    "dining" -> Icons.Default.Fastfood
    "utilities" -> Icons.Default.Wifi
    else -> Icons.Default.Receipt
  }
}

private fun getCategoryColor(category: String): Color {
  return when (category.lowercase()) {
    "rent" -> AccentCyan
    "groceries" -> AccentAmber
    "dining" -> AccentPink
    "utilities" -> AccentPurple
    else -> Color(0xFF6C5CE7)
  }
}
