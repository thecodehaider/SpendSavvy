package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupEntity
import com.example.data.model.MemberEntity
import com.example.data.model.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendSavvyDao {

  // Groups
  @Query("SELECT * FROM groups ORDER BY createdAt DESC")
  fun getAllGroups(): Flow<List<GroupEntity>>

  @Query("SELECT * FROM groups WHERE id = :groupId LIMIT 1")
  suspend fun getGroupById(groupId: Long): GroupEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroup(group: GroupEntity): Long

  @Update
  suspend fun updateGroup(group: GroupEntity)

  @Delete
  suspend fun deleteGroup(group: GroupEntity)

  @Query("DELETE FROM groups")
  suspend fun clearAllGroups()

  @Query("DELETE FROM members")
  suspend fun clearAllMembers()

  @Query("DELETE FROM expenses")
  suspend fun clearAllExpenses()

  @Query("DELETE FROM settlements")
  suspend fun clearAllSettlements()

  // Members
  @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY id ASC")
  fun getMembersForGroup(groupId: Long): Flow<List<MemberEntity>>

  @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY id ASC")
  suspend fun getMembersForGroupSync(groupId: Long): List<MemberEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: MemberEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMembers(members: List<MemberEntity>)

  // Expenses
  @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY timestamp DESC")
  fun getExpensesForGroup(groupId: Long): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY timestamp DESC")
  suspend fun getExpensesForGroupSync(groupId: Long): List<ExpenseEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity): Long

  @Update
  suspend fun updateExpense(expense: ExpenseEntity)

  @Delete
  suspend fun deleteExpense(expense: ExpenseEntity)

  @Query("UPDATE expenses SET isSettled = :settled WHERE id = :expenseId")
  suspend fun setExpenseSettled(expenseId: Long, settled: Boolean)

  // Settlements
  @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY timestamp DESC")
  fun getSettlementsForGroup(groupId: Long): Flow<List<SettlementEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSettlement(settlement: SettlementEntity): Long
}
