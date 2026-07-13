package com.example.mobile.data.network.dto

import java.math.BigDecimal

data class SpendingTrendResponseDTO(
    val monthlyTrends: List<TrendItem>,
    val quarterlyTrends: List<TrendItem>,
    val yearlyTrends: List<TrendItem>
)

data class TrendItem(
    val label: String,
    val amount: BigDecimal
)
