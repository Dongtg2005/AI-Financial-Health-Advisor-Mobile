package com.example.mobile.data.network.dto

data class DashboardResponse(
    val status: Int,
    val message: String,
    val data: DashboardData
) {
    data class DashboardData(
        val userName: String,
        val healthScore: Int,
        // Đón nhận 4 đầu điểm tử huyệt từ Backend
        val scoreSpending: Int,
        val scoreDebt: Int,
        val scoreSaving: Int,
        val scoreAwareness: Int,
        val totalSpent: Long,
        val totalBudget: Long,
        val alerts: List<DebtAlertUi>,
        val budgetCategories: List<BudgetCategoryUiDTO>,
        val adminNote: String?
    )

    data class DebtAlertUi(
        val type: String, // "CRITICAL", "WARNING"
        val message: String
    )

    data class BudgetCategoryUiDTO(
        val name: String,
        val spent: Long,
        val limit: Long
    )
}
