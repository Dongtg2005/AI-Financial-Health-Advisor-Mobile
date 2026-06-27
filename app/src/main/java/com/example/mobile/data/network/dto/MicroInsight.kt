package com.example.mobile.data.network.dto

data class MicroInsight(
    val shouldShow: Boolean,
    val type: String,
    val title: String,
    val todayTotalAmount: Double,
    val projectedMonthlyAmount: Double,
    val message: String,
    val tone: String
)
