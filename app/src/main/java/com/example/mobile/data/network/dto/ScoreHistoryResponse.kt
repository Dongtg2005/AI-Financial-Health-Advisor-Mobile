package com.example.mobile.data.network.dto

data class ScoreHistoryResponse(
    val status: Int,
    val message: String,
    val data: List<ScoreHistoryDTO>
)

data class ScoreHistoryDTO(
    val id: String,
    val weekStartDate: String,
    val healthScore: Int,
    val spendingScore: Int,
    val debtScore: Int,
    val savingScore: Int,
    val awarenessScore: Int,
    val debtMode: String,
    val progressScore: Int,
    val insights: String
)
