package com.example.mobile.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.DebtApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScoreBreakdown(
    val spending: Int = 35,
    val debt: Int = 35,
    val saving: Int = 20,
    val awareness: Int = 10
)

data class DashboardUiState(
    val userName: String = "Đông",
    val isLoading: Boolean = false,
    val healthScore: Int = 100,
    val totalSpent: Long = 0,
    val totalBudget: Long = 0,
    val scoreBreakdown: ScoreBreakdown = ScoreBreakdown(),
    val hasAlert: Boolean = false,
    val alertTitle: String = "",
    val alertMessage: String = "",
    val budgetCategories: List<BudgetCategoryUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList()
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

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val apiService = NetworkModule.createService(application, DebtApiService::class.java)

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        _uiState.value = DashboardUiState(
            healthScore = 100,
            scoreBreakdown = ScoreBreakdown(35, 35, 20, 10),
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

    fun fetchDebtSummary() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = apiService.getDebtSummary()
                
                if (response.status == 200 && response.data != null) {
                    val summary = response.data
                    val firstAlert = summary.alerts.firstOrNull()
                    
                    // 1. Thực thi Quy tắc tử huyệt của Yếu tố 2 (Điểm DTI về 0 lập tức nếu có trễ hạn)
                    val calculatedDebtScore = if (summary.overdueCount > 0) {
                        0 
                    } else {
                        // Tính động dựa trên các khoản nợ sắp đến hạn trong tuần
                        Math.max(10, 35 - (summary.upcomingCount * 5))
                    }

                    // 2. Tính toán lại điểm tổng Health Score từ breakdown mới
                    val breakdown = _uiState.value.scoreBreakdown.copy(debt = calculatedDebtScore)
                    val newHealthScore = breakdown.spending + breakdown.debt + breakdown.saving + breakdown.awareness

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            healthScore = newHealthScore,
                            scoreBreakdown = breakdown,
                            hasAlert = firstAlert != null,
                            alertTitle = if (firstAlert?.type == "CRITICAL") "CẢNH BÁO NGUY HIỂM!" else "THẺ TÍN DỤNG SẠP ĐẾN HẠN",
                            alertMessage = firstAlert?.message ?: ""
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }
}
