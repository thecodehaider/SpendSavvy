package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupEntity
import com.example.data.model.MemberEntity
import com.example.data.model.SettlementEntity
import kotlinx.coroutines.CoroutineScope

@Database(
  entities = [
    GroupEntity::class,
    MemberEntity::class,
    ExpenseEntity::class,
    SettlementEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class SpendSavvyDatabase : RoomDatabase() {
  abstract fun spendSavvyDao(): SpendSavvyDao

  companion object {
    @Volatile
    private var INSTANCE: SpendSavvyDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): SpendSavvyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SpendSavvyDatabase::class.java,
          "spendsavvy_db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
