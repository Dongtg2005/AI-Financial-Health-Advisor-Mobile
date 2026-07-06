package com.example.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobile.data.local.entity.DetectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectDao {
    @Query("SELECT * FROM detects_local WHERE isProcessed = 0 ORDER BY detectedAt DESC")
    fun getUnprocessedDetectsFlow(): Flow<List<DetectEntity>>

    @Query("SELECT * FROM detects_local WHERE isProcessed = 0 ORDER BY detectedAt DESC")
    suspend fun getUnprocessedDetects(): List<DetectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetect(detect: DetectEntity)

    @Query("UPDATE detects_local SET isProcessed = 1 WHERE id IN (:ids)")
    suspend fun markAsProcessed(ids: List<String>)

    @Query("DELETE FROM detects_local WHERE isProcessed = 1")
    suspend fun deleteProcessed()
}
