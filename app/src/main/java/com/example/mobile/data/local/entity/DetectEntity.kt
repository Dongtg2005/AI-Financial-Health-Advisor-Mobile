package com.example.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "detects_local")
data class DetectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val appPackageName: String,
    val bankName: String,
    val detectedAt: Long = System.currentTimeMillis(),
    val isProcessed: Boolean = false
)
