package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val type: String = "Housemates",
  val currencySymbol: String = "$",
  val isPro: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "members")
data class MemberEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val groupId: Long,
  val name: String,
  val avatarColorHex: String = "#7C4DFF",
  val isCurrentUser: Boolean = false
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val groupId: Long,
  val title: String,
  val amount: Double,
  val paidByMemberId: Long,
  val category: String, // Rent, Groceries, Dining, Utilities, Entertainment, Other
  val timestamp: Long = System.currentTimeMillis(),
  val splitMembersCsv: String = "", // e.g. "1,2,3,4"
  val isSettled: Boolean = false,
  val isRecurring: Boolean = false,
  val note: String = ""
)

@Entity(tableName = "settlements")
data class SettlementEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val groupId: Long,
  val fromMemberId: Long,
  val toMemberId: Long,
  val amount: Double,
  val timestamp: Long = System.currentTimeMillis(),
  val note: String = "Settled via SpendSavvy"
)

// UI models and calculations
data class MemberBalance(
  val member: MemberEntity,
  val netBalance: Double // Positive = owed money, Negative = owes money
)

data class DebtTransfer(
  val fromMember: MemberEntity,
  val toMember: MemberEntity,
  val amount: Double
)

data class GroupSummary(
  val totalExpenses: Double,
  val settledExpenses: Double,
  val activeExpenses: Double,
  val settledPercentage: Float,
  val userNetBalance: Double,
  val userOwedTotal: Double,
  val userOwesTotal: Double
)

data class CategorySpend(
  val category: String,
  val totalAmount: Double,
  val percentageOfTotal: Float,
  val colorHex: String
)
