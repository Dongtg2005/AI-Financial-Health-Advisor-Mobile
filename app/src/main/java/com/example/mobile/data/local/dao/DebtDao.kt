package com.example.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobile.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts_local WHERE isActive = 1 ORDER BY dueDate ASC")
    fun getActiveDebtsFlow(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts_local WHERE isActive = 1 ORDER BY dueDate ASC")
    suspend fun getActiveDebts(): List<DebtEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    @Query("DELETE FROM debts_local")
    suspend fun clearAll()

    @Query("UPDATE debts_local SET isActive = 0 WHERE id = :debtId")
    suspend fun markAsPaidOffline(debtId: String)
}
