package com.example.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobile.data.local.dao.DebtDao
import com.example.mobile.data.local.dao.DetectDao
import com.example.mobile.data.local.entity.DebtEntity
import com.example.mobile.data.local.entity.DetectEntity

@Database(entities = [DebtEntity::class, DetectEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao
    abstract fun detectDao(): DetectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_advisor_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
