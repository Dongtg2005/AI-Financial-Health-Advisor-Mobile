package com.example.mobile.data.network.dto

import java.math.BigDecimal

data class CashWeeklyEstimateRequest(
    val totalAmount: BigDecimal,
    val category: String
)
