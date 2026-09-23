package com.example.data.repository

import com.example.data.database.SpendSavvyDao
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupEntity
import com.example.data.model.MemberEntity
import com.example.data.model.SettlementEntity
import kotlinx.coroutines.flow.Flow

class SpendSavvyRepository(private val dao: SpendSavvyDao) {

  val allGroups: Flow<List<GroupEntity>> = dao.getAllGroups()

  fun getMembersForGroup(groupId: Long): Flow<List<MemberEntity>> =
    dao.getMembersForGroup(groupId)

  fun getExpensesForGroup(groupId: Long): Flow<List<ExpenseEntity>> =
    dao.getExpensesForGroup(groupId)

  fun getSettlementsForGroup(groupId: Long): Flow<List<SettlementEntity>> =
    dao.getSettlementsForGroup(groupId)

  suspend fun insertExpense(expense: ExpenseEntity): Long =
    dao.insertExpense(expense)

  suspend fun updateExpense(expense: ExpenseEntity) =
    dao.updateExpense(expense)

  suspend fun deleteExpense(expense: ExpenseEntity) =
    dao.deleteExpense(expense)

  suspend fun setExpenseSettled(expenseId: Long, settled: Boolean) =
    dao.setExpenseSettled(expenseId, settled)

  suspend fun insertSettlement(settlement: SettlementEntity): Long =
    dao.insertSettlement(settlement)

  suspend fun insertGroup(group: GroupEntity): Long =
    dao.insertGroup(group)

  suspend fun updateGroup(group: GroupEntity) =
    dao.updateGroup(group)

  suspend fun insertMember(member: MemberEntity): Long =
    dao.insertMember(member)

  suspend fun deleteGroup(group: GroupEntity) =
    dao.deleteGroup(group)

  suspend fun clearAllData() {
    dao.clearAllExpenses()
    dao.clearAllSettlements()
    dao.clearAllMembers()
    dao.clearAllGroups()
  }
}
