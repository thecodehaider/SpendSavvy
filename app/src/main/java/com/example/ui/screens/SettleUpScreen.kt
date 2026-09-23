package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DebtTransfer
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.neumorphicInset
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SpendSavvyViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettleUpScreen(
  viewModel: SpendSavvyViewModel,
  modifier: Modifier = Modifier
) {
  val simplifiedDebts by viewModel.simplifiedDebts.collectAsStateWithLifecycle()
  val settlements by viewModel.settlements.collectAsStateWithLifecycle()
  val members by viewModel.members.collectAsStateWithLifecycle()
  val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
  val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()
  val activeGroup = allGroups.find { it.id == selectedGroupId }

  val context = LocalContext.current
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.US)

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = "Settle Balances",
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "Smart debt simplification: fewest payments possible",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary
        )
      }
    }

    // Smart Simplification Banner
    item {
      NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(AccentPurple.copy(alpha = 0.12f))
              .neumorphicInset(cornerRadius = 12.dp, depth = 2.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Savings,
              contentDescription = "Smart Settle",
              tint = AccentPurple,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Text(
              text = "Splitwise-Style Algorithm",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = if (simplifiedDebts.isEmpty()) {
                "All debts settled! Everyone is even."
              } else {
                "${simplifiedDebts.size} simple transfer(s) needed to clear all balances."
              },
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = TextSecondary
            )
          }
        }
      }
    }

    // Transfers needed
    if (simplifiedDebts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Settled",
              tint = AccentGreen,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Everyone is all settled up!",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "No pending debts in ${activeGroup?.name ?: "your group"}.",
              fontSize = 13.sp,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      items(simplifiedDebts) { transfer ->
        DebtTransferCard(
          transfer = transfer,
          currencyFormat = currencyFormat,
          onSettle = { viewModel.settleDebt(transfer) },
          onOpenP2P = { service ->
            // Intent to open Venmo or PayPal or web browser
            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://venmo.com")
            }
            try {
              context.startActivity(intent)
            } catch (_: Exception) {}
          }
        )
      }
    }

    // Recent Settlements History
    if (settlements.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Settlement History",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }

      items(settlements) { item ->
        val fromName = members.find { it.id == item.fromMemberId }?.name ?: "Someone"
        val toName = members.find { it.id == item.toMemberId }?.name ?: "Someone"

        NeumorphicCard(
          cornerRadius = 16.dp,
          elevation = 3.dp,
          modifier = Modifier.fillMaxWidth()
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
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
              )
              Column {
                Text(
                  text = "$fromName paid $toName",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Text(
                  text = dateFormat.format(Date(item.timestamp)),
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
            }

            Text(
              text = currencyFormat.format(item.amount),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = AccentGreen
            )
          }
        }
      }
    }
  }
}

@Composable
private fun DebtTransferCard(
  transfer: DebtTransfer,
  currencyFormat: NumberFormat,
  onSettle: () -> Unit,
  onOpenP2P: (String) -> Unit
) {
  NeumorphicCard(
    cornerRadius = 20.dp,
    elevation = 5.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      // Members flow: [Debtor] -> [Creditor]
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Debtor
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(android.graphics.Color.parseColor(transfer.fromMember.avatarColorHex))),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = transfer.fromMember.name.take(1),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          Column {
            Text(
              text = transfer.fromMember.name,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
            Text(
              text = "owes",
              fontSize = 11.sp,
              color = AccentPink
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "to",
          tint = TextMuted,
          modifier = Modifier.size(18.dp)
        )

        // Creditor
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = transfer.toMember.name,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
            Text(
              text = "is owed",
              fontSize = 11.sp,
              color = AccentGreen
            )
          }
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(android.graphics.Color.parseColor(transfer.toMember.avatarColorHex))),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = transfer.toMember.name.take(1),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      // Amount & Settle action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = currencyFormat.format(transfer.amount),
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          NeumorphicButton(
            onClick = { onOpenP2P("Venmo") },
            cornerRadius = 12.dp,
            elevation = 3.dp
          ) {
            Text(
              text = "Pay",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = AccentCyan
            )
          }

          NeumorphicButton(
            onClick = onSettle,
            cornerRadius = 12.dp,
            elevation = 4.dp
          ) {
            Text(
              text = "Mark Settled",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AccentGreen
            )
          }
        }
      }
    }
  }
}
