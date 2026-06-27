package com.example.mobile.data.network.dto

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

data class DebtSummaryResponse(
    val totalActiveDebt: Double,
    val overdueCount: Int,
    val upcomingCount: Int,
    val alerts: List<DebtAlert>,
    val debts: List<DebtDetails>
)

data class DebtAlert(
    val type: String, // "CRITICAL", "WARNING"
    val message: String
)

data class DebtDetails(
    val id: String,
    val type: String,
    val balance: Double,
    val minimumPayment: Double,
    val dueDate: String,
    val daysRemaining: Long,
    val overdueDays: Long,
    val overdueSince: String?,
    val isActive: Boolean
)
