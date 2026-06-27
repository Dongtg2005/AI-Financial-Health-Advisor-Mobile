package com.example.mobile.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "Đông",
    val healthScore: Int = 72,
    val scoreBreakdown: ScoreBreakdown = ScoreBreakdown(),
    val hasAlert: Boolean = true,
    val alertTitle: String = "",
    val alertMessage: String = "",
    val totalSpent: Long = 7_200_000,
    val totalBudget: Long = 10_000_000,
    val budgetCategories: List<BudgetCategoryUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList()
)

data class ScoreBreakdown(
    val spending: Int = 25,
    val debt: Int = 28,
    val saving: Int = 12,
    val awareness: Int = 7
)

data class BudgetCategoryUi(
    val name: String,
    val spent: Long,
    val limit: Long
)

data class TransactionUi(
    val id: String,
    val name: String,
    val date: String,
    val amount: Long,
    val isIncome: Boolean,
    val category: String
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        _uiState.value = DashboardUiState(
            healthScore = 72,
            scoreBreakdown = ScoreBreakdown(25, 28, 12, 7),
            hasAlert = true,
            alertTitle = "Thẻ tín dụng sắp đến hạn",
            alertMessage = "Visa VCB đến hạn sau 3 ngày — cần thanh toán 8.500.000đ",
            totalSpent = 7_200_000,
            totalBudget = 10_000_000,
            budgetCategories = listOf(
                BudgetCategoryUi("Ăn uống",  2_975_000, 3_500_000),
                BudgetCategoryUi("Đi lại",     450_000, 1_000_000),
                BudgetCategoryUi("Mua sắm",  1_200_000, 2_000_000),
                BudgetCategoryUi("Khác",       575_000, 1_500_000)
            ),
            recentTransactions = listOf(
                TransactionUi("1", "Tiền hoa hồng tháng 6",           "Hôm nay, 09:00",    12_000_000, true,  "income"),
                TransactionUi("2", "Vé xe giường nằm về Phú Tân",     "Hôm qua, 20:15",       320_000, false, "transport"),
                TransactionUi("3", "Cà phê cuối tuần",                 "21/06, 08:30",          65_000, false, "food"),
                TransactionUi("4", "Shopee — Mua sắm online",          "20/06, 23:45",         450_000, false, "shopping"),
                TransactionUi("5", "Trả nợ thẻ tín dụng VCB",         "18/06, 10:00",       2_000_000, false, "debt")
            )
        )
    }
}
