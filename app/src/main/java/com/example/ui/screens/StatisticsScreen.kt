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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicDoughnutGauge
import com.example.ui.components.NeumorphicEqualizerTrack
import com.example.ui.components.NeumorphicToggleSwitch
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SpendSavvyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatisticsScreen(
  viewModel: SpendSavvyViewModel,
  modifier: Modifier = Modifier
) {
  val summary by viewModel.groupSummary.collectAsStateWithLifecycle()
  val isChartMode by viewModel.chartViewMode.collectAsStateWithLifecycle()
  val categorySpending by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
  val memberBalances by viewModel.memberBalances.collectAsStateWithLifecycle()

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  val rentCat = categorySpending.find { it.category.equals("Rent", true) }
  val utilsCat = categorySpending.find { it.category.equals("Utilities", true) }
  val grocCat = categorySpending.find { it.category.equals("Groceries", true) }
  val diningCat = categorySpending.find { it.category.equals("Dining", true) }

  val rentPct = rentCat?.percentageOfTotal ?: 0f
  val utilsPct = utilsCat?.percentageOfTotal ?: 0f
  val grocPct = grocCat?.percentageOfTotal ?: 0f
  val diningPct = diningCat?.percentageOfTotal ?: 0f

  val settledPct = summary.settledPercentage

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(22.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Statistics",
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        NeumorphicToggleSwitch(
          checked = isChartMode,
          onCheckedChange = { viewModel.setChartViewMode(it) },
          label = if (isChartMode) "Chart" else "List"
        )
      }
    }

    // 2. Signature Neumorphic Doughnut Ring Gauge (Dynamic percentage)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
      ) {
        NeumorphicDoughnutGauge(
          percentage = settledPct,
          size = 200.dp,
          title = "${(settledPct * 100).toInt()}%",
          subtitle = if (summary.totalExpenses > 0) "Settled" else "No Spend"
        )
      }
    }

    // 3. Recessed Vertical Equalizer Tracks (Dynamic percentages)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.Bottom
        ) {
          // Track 1: Rent (Cyan gradient)
          NeumorphicEqualizerTrack(
            percentage = rentPct,
            label = "Rent",
            valueText = "${(rentPct * 100).toInt()}%",
            gradientColors = listOf(AccentCyan, AccentTeal),
            trackWidth = 40.dp,
            trackHeight = 135.dp
          )

          // Track 2: Utilities (Muted / recessed track)
          NeumorphicEqualizerTrack(
            percentage = utilsPct,
            label = "Utilities",
            valueText = "${(utilsPct * 100).toInt()}%",
            gradientColors = listOf(Color(0xFFB8C8D8), Color(0xFF9FB0C2)),
            trackWidth = 40.dp,
            trackHeight = 135.dp
          )

          // Track 3: Groceries (Amber gradient)
          NeumorphicEqualizerTrack(
            percentage = grocPct,
            label = "Groceries",
            valueText = "${(grocPct * 100).toInt()}%",
            gradientColors = listOf(AccentAmber, AccentOrange),
            trackWidth = 40.dp,
            trackHeight = 135.dp
          )

          // Track 4: Dining (Pink gradient)
          NeumorphicEqualizerTrack(
            percentage = diningPct,
            label = "Dining",
            valueText = "${(diningPct * 100).toInt()}%",
            gradientColors = listOf(AccentPink, AccentRose),
            trackWidth = 40.dp,
            trackHeight = 135.dp
          )
        }
      }
    }

    // 4. Breakdown details card (Dynamic from real categories)
    item {
      NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 5.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = "Settlement Breakdown",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )

          if (summary.totalExpenses <= 0.0) {
            Text(
              text = "No shared expenses recorded yet.",
              fontSize = 13.sp,
              color = TextSecondary
            )
          } else {
            categorySpending.forEach { cat ->
              val dotColor = when (cat.category.lowercase()) {
                "rent" -> AccentCyan
                "groceries" -> AccentAmber
                "dining" -> AccentPink
                "utilities" -> AccentTeal
                else -> Color(0xFF7C4DFF)
              }
              CategoryStatRow(
                label = cat.category,
                percentage = "${(cat.percentageOfTotal * 100).toInt()}%",
                amount = currencyFormat.format(cat.totalAmount),
                dotColor = dotColor
              )
            }
          }
        }
      }
    }

    // 5. Housemate Balances Summary (Dynamic)
    item {
      NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 5.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Housemate Net Balances",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )

          if (memberBalances.isEmpty()) {
            Text(
              text = "No members added yet.",
              fontSize = 13.sp,
              color = TextSecondary
            )
          } else {
            memberBalances.forEach { balance ->
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
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(Color(android.graphics.Color.parseColor(balance.member.avatarColorHex))),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = balance.member.name.take(1),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }

                  Text(
                    text = balance.member.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                  )
                }

                val net = balance.netBalance
                Text(
                  text = when {
                    net > 0.01 -> "+${currencyFormat.format(net)}"
                    net < -0.01 -> "-${currencyFormat.format(-net)}"
                    else -> "$0.00"
                  },
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (net >= 0) Color(0xFF10B981) else AccentPink
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryStatRow(
  label: String,
  percentage: String,
  amount: String,
  dotColor: Color
) {
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
          .size(8.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(
        text = percentage,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextMuted
      )
      Text(
        text = amount,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }
  }
}
