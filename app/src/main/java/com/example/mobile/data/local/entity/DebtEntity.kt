package com.example.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts_local")
data class DebtEntity(
    @PrimaryKey val id: String,
    val type: String,
    val balance: Double,
    val minimumPayment: Double,
    val dueDate: String,
    val daysRemaining: Long,
    val overdueDays: Long,
    val overdueSince: String?,
    val isActive: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)
