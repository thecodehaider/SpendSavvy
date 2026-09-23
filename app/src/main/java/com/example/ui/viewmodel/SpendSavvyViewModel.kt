package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.SpendSavvyDatabase
import com.example.data.model.CategorySpend
import com.example.data.model.DebtTransfer
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupEntity
import com.example.data.model.GroupSummary
import com.example.data.model.MemberBalance
import com.example.data.model.MemberEntity
import com.example.data.model.SettlementEntity
import com.example.data.repository.SpendSavvyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

class SpendSavvyViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: SpendSavvyRepository
  val allGroups: StateFlow<List<GroupEntity>>

  private val _selectedGroupId = MutableStateFlow<Long?>(null)
  val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

  private val _selectedCategoryFilter = MutableStateFlow<String?>("All")
  val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

  private val _chartViewMode = MutableStateFlow(true) // true = Chart, false = List
  val chartViewMode: StateFlow<Boolean> = _chartViewMode.asStateFlow()

  private val _activeTab = MutableStateFlow(0) // 0 = Expenses, 1 = Settle, 2 = Statistics, 3 = Pro/Settings
  val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

  private val _showAddExpenseDialog = MutableStateFlow(false)
  val showAddExpenseDialog: StateFlow<Boolean> = _showAddExpenseDialog.asStateFlow()

  private val _showCreateGroupDialog = MutableStateFlow(false)
  val showCreateGroupDialog: StateFlow<Boolean> = _showCreateGroupDialog.asStateFlow()

  private val _showProDialog = MutableStateFlow(false)
  val showProDialog: StateFlow<Boolean> = _showProDialog.asStateFlow()

  private val _members = MutableStateFlow<List<MemberEntity>>(emptyList())
  val members: StateFlow<List<MemberEntity>> = _members.asStateFlow()

  private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
  val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

  private val _settlements = MutableStateFlow<List<SettlementEntity>>(emptyList())
  val settlements: StateFlow<List<SettlementEntity>> = _settlements.asStateFlow()

  init {
    val db = SpendSavvyDatabase.getDatabase(application, viewModelScope)
    repository = SpendSavvyRepository(db.spendSavvyDao())

    allGroups = repository.allGroups.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    // Observe groups to select the default one
    viewModelScope.launch {
      allGroups.collect { groups ->
        if (groups.isNotEmpty() && _selectedGroupId.value == null) {
          _selectedGroupId.value = groups.first().id
        }
      }
    }

    // When selected group changes, observe its members, expenses, and settlements
    viewModelScope.launch {
      _selectedGroupId.collect { groupId ->
        if (groupId != null) {
          launch {
            repository.getMembersForGroup(groupId).collect { _members.value = it }
          }
          launch {
            repository.getExpensesForGroup(groupId).collect { _expenses.value = it }
          }
          launch {
            repository.getSettlementsForGroup(groupId).collect { _settlements.value = it }
          }
        } else {
          _members.value = emptyList()
          _expenses.value = emptyList()
          _settlements.value = emptyList()
        }
      }
    }
  }

  fun setActiveTab(tabIndex: Int) {
    _activeTab.value = tabIndex
  }

  fun selectGroup(groupId: Long) {
    _selectedGroupId.value = groupId
  }

  fun setCategoryFilter(category: String?) {
    _selectedCategoryFilter.value = category
  }

  fun setChartViewMode(isChart: Boolean) {
    _chartViewMode.value = isChart
  }

  fun showAddExpense(show: Boolean) {
    _showAddExpenseDialog.value = show
  }

  fun showCreateGroup(show: Boolean) {
    _showCreateGroupDialog.value = show
  }

  fun showProModal(show: Boolean) {
    _showProDialog.value = show
  }

  // Filtered expenses based on category chip
  val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(
    _expenses,
    _selectedCategoryFilter
  ) { expensesList, category ->
    if (category == null || category == "All") {
      expensesList
    } else {
      expensesList.filter { it.category.equals(category, ignoreCase = true) }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Member Balances & Debt Simplification
  val memberBalances: StateFlow<List<MemberBalance>> = combine(
    _members,
    _expenses,
    _settlements
  ) { memberList, expenseList, settlementList ->
    calculateMemberBalances(memberList, expenseList, settlementList)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Simplified Debts ("Jordan owes Alex $45.00")
  val simplifiedDebts: StateFlow<List<DebtTransfer>> = combine(
    memberBalances,
    _members
  ) { balances, _ ->
    calculateSimplifiedDebts(balances)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Group Financial Summary
  val groupSummary: StateFlow<GroupSummary> = combine(
    _expenses,
    memberBalances,
    _members
  ) { expenseList, balances, memberList ->
    calculateGroupSummary(expenseList, balances, memberList)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    GroupSummary(
      totalExpenses = 0.0,
      settledExpenses = 0.0,
      activeExpenses = 0.0,
      settledPercentage = 0.0f,
      userNetBalance = 0.0,
      userOwedTotal = 0.0,
      userOwesTotal = 0.0
    )
  )

  // Category breakdown for Statistics & Equalizer
  val categoryBreakdown: StateFlow<List<CategorySpend>> = _expenses
    .map { expenseList -> calculateCategorySpending(expenseList) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Create a new group with members
  fun createGroup(
    name: String,
    type: String = "Apartment",
    memberNames: List<String> = emptyList(),
    currency: String = "$"
  ) {
    viewModelScope.launch {
      val newGroup = GroupEntity(
        name = name.ifBlank { "My Group" },
        type = type,
        currencySymbol = currency,
        isPro = false
      )
      val groupId = repository.insertGroup(newGroup)

      val colors = listOf("#7C4DFF", "#00D2FF", "#FF9F43", "#FF416C", "#10B981", "#6366F1")
      // Add current user
      repository.insertMember(
        MemberEntity(
          groupId = groupId,
          name = "You",
          avatarColorHex = colors[0],
          isCurrentUser = true
        )
      )

      // Add other members entered
      memberNames.filter { it.isNotBlank() }.forEachIndexed { index, memberName ->
        val color = colors[(index + 1) % colors.size]
        repository.insertMember(
          MemberEntity(
            groupId = groupId,
            name = memberName.trim(),
            avatarColorHex = color,
            isCurrentUser = false
          )
        )
      }

      _selectedGroupId.value = groupId
      _showCreateGroupDialog.value = false
    }
  }

  // Add a member to the current group
  fun addMemberToGroup(name: String) {
    val groupId = _selectedGroupId.value ?: return
    if (name.isBlank()) return
    viewModelScope.launch {
      val colors = listOf("#7C4DFF", "#00D2FF", "#FF9F43", "#FF416C", "#10B981", "#6366F1")
      val color = colors[_members.value.size % colors.size]
      repository.insertMember(
        MemberEntity(
          groupId = groupId,
          name = name.trim(),
          avatarColorHex = color,
          isCurrentUser = false
        )
      )
    }
  }

  // Add a new expense
  fun addExpense(
    title: String,
    amount: Double,
    paidByMemberId: Long,
    category: String,
    splitMemberIds: List<Long>,
    isRecurring: Boolean = false,
    note: String = ""
  ) {
    val groupId = _selectedGroupId.value ?: return
    viewModelScope.launch {
      val expense = ExpenseEntity(
        groupId = groupId,
        title = title,
        amount = amount,
        paidByMemberId = paidByMemberId,
        category = category,
        timestamp = System.currentTimeMillis(),
        splitMembersCsv = splitMemberIds.joinToString(","),
        isSettled = false,
        isRecurring = isRecurring,
        note = note
      )
      repository.insertExpense(expense)
      _showAddExpenseDialog.value = false
    }
  }

  // Toggle settled status
  fun toggleExpenseSettled(expense: ExpenseEntity) {
    viewModelScope.launch {
      repository.setExpenseSettled(expense.id, !expense.isSettled)
    }
  }

  // Delete expense
  fun deleteExpense(expense: ExpenseEntity) {
    viewModelScope.launch {
      repository.deleteExpense(expense)
    }
  }

  // Record a settlement between two members
  fun settleDebt(transfer: DebtTransfer) {
    val groupId = _selectedGroupId.value ?: return
    viewModelScope.launch {
      val settlement = SettlementEntity(
        groupId = groupId,
        fromMemberId = transfer.fromMember.id,
        toMemberId = transfer.toMember.id,
        amount = transfer.amount,
        timestamp = System.currentTimeMillis(),
        note = "Settled with SpendSavvy"
      )
      repository.insertSettlement(settlement)
    }
  }

  // Toggle group Pro subscription
  fun toggleGroupPro(groupId: Long) {
    viewModelScope.launch {
      val group = allGroups.value.find { it.id == groupId } ?: return@launch
      repository.updateGroup(group.copy(isPro = !group.isPro))
    }
  }

  // Clear all data for testing
  fun clearAllData() {
    viewModelScope.launch {
      repository.clearAllData()
      _selectedGroupId.value = null
      _members.value = emptyList()
      _expenses.value = emptyList()
      _settlements.value = emptyList()
    }
  }

  // Helper calculation functions
  private fun calculateMemberBalances(
    members: List<MemberEntity>,
    expenses: List<ExpenseEntity>,
    settlements: List<SettlementEntity>
  ): List<MemberBalance> {
    if (members.isEmpty()) return emptyList()

    val balanceMap = members.associate { it.id to 0.0 }.toMutableMap()

    for (expense in expenses) {
      val paidBy = expense.paidByMemberId
      val splitList = if (expense.splitMembersCsv.isBlank()) {
        members.map { it.id }
      } else {
        expense.splitMembersCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
      }
      val participants = if (splitList.isEmpty()) members.map { it.id } else splitList
      val share = expense.amount / participants.size.coerceAtLeast(1)

      // Payer gets credited
      balanceMap[paidBy] = (balanceMap[paidBy] ?: 0.0) + expense.amount

      // Participants get debited
      for (partId in participants) {
        balanceMap[partId] = (balanceMap[partId] ?: 0.0) - share
      }
    }

    // Factor in settlements
    for (settlement in settlements) {
      balanceMap[settlement.fromMemberId] = (balanceMap[settlement.fromMemberId] ?: 0.0) + settlement.amount
      balanceMap[settlement.toMemberId] = (balanceMap[settlement.toMemberId] ?: 0.0) - settlement.amount
    }

    return members.map { member ->
      MemberBalance(
        member = member,
        netBalance = balanceMap[member.id] ?: 0.0
      )
    }
  }

  private fun calculateSimplifiedDebts(balances: List<MemberBalance>): List<DebtTransfer> {
    val results = mutableListOf<DebtTransfer>()

    val debtors = balances.filter { it.netBalance < -0.01 }.map { it.member to abs(it.netBalance) }.toMutableList()
    val creditors = balances.filter { it.netBalance > 0.01 }.map { it.member to it.netBalance }.toMutableList()

    var i = 0
    var j = 0

    while (i < debtors.size && j < creditors.size) {
      val (debtor, debitAmount) = debtors[i]
      val (creditor, creditAmount) = creditors[j]

      val payment = min(debitAmount, creditAmount)
      if (payment > 0.01) {
        results.add(DebtTransfer(fromMember = debtor, toMember = creditor, amount = payment))
      }

      val remainingDebit = debitAmount - payment
      val remainingCredit = creditAmount - payment

      if (remainingDebit <= 0.01) {
        i++
      } else {
        debtors[i] = debtor to remainingDebit
      }

      if (remainingCredit <= 0.01) {
        j++
      } else {
        creditors[j] = creditor to remainingCredit
      }
    }

    return results
  }

  private fun calculateGroupSummary(
    expenses: List<ExpenseEntity>,
    balances: List<MemberBalance>,
    members: List<MemberEntity>
  ): GroupSummary {
    val total = expenses.sumOf { it.amount }
    val settledTotal = expenses.filter { it.isSettled }.sumOf { it.amount }
    val activeTotal = total - settledTotal
    val percentage = if (total > 0.0) (settledTotal / total).toFloat().coerceIn(0f, 1f) else 0.0f

    val currentUser = members.find { it.isCurrentUser } ?: members.firstOrNull()
    val currentUserBalance = balances.find { it.member.id == currentUser?.id }?.netBalance ?: 0.0

    return GroupSummary(
      totalExpenses = total,
      settledExpenses = settledTotal,
      activeExpenses = activeTotal,
      settledPercentage = percentage,
      userNetBalance = currentUserBalance,
      userOwedTotal = if (currentUserBalance > 0) currentUserBalance else 0.0,
      userOwesTotal = if (currentUserBalance < 0) abs(currentUserBalance) else 0.0
    )
  }

  private fun calculateCategorySpending(expenses: List<ExpenseEntity>): List<CategorySpend> {
    if (expenses.isEmpty()) {
      return listOf(
        CategorySpend("Rent", 0.0, 0f, "#00D2FF"),
        CategorySpend("Utilities", 0.0, 0f, "#B9C8D8"),
        CategorySpend("Groceries", 0.0, 0f, "#FF9F43"),
        CategorySpend("Dining", 0.0, 0f, "#FF416C")
      )
    }

    val total = expenses.sumOf { it.amount }.coerceAtLeast(1.0)
    val grouped = expenses.groupBy { it.category }

    val defaultColors = mapOf(
      "Rent" to "#00D2FF",
      "Utilities" to "#B9C8D8",
      "Groceries" to "#FF9F43",
      "Dining" to "#FF416C",
      "Entertainment" to "#7C4DFF",
      "Other" to "#A0B0C0"
    )

    val baseCategories = listOf("Rent", "Utilities", "Groceries", "Dining")

    return baseCategories.map { catName ->
      val catExpenses = grouped[catName] ?: emptyList()
      val catTotal = catExpenses.sumOf { it.amount }
      val settledTotal = catExpenses.filter { it.isSettled }.sumOf { it.amount }
      val settledRatio = if (catTotal > 0.0) (settledTotal / catTotal).toFloat().coerceIn(0f, 1f) else 0f

      CategorySpend(
        category = catName,
        totalAmount = catTotal,
        percentageOfTotal = settledRatio,
        colorHex = defaultColors[catName] ?: "#7C4DFF"
      )
    }
  }
}
