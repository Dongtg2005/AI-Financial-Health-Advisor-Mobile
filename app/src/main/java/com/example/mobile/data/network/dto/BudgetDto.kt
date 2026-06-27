package com.example.mobile.data.network.dto

data class BudgetSuggestionResponse(
    val suggestedTotalBudget: Double,
    val message: String,
    val categoryDistribution: Map<String, Double>
)
