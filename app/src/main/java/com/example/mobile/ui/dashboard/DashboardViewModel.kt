package com.example.mobile.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.TokenManager
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.DebtApiService
import com.example.mobile.data.network.TransactionApiService
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
    val errorMessage: String? = null,
    val healthScore: Int = 100,
    val totalSpent: Long = 0,
    val totalBudget: Long = 0,
    val scoreBreakdown: ScoreBreakdown = ScoreBreakdown(),
    val hasAlert: Boolean = false,
    val alertTitle: String = "",
    val alertMessage: String = "",
    val budgetCategories: List<BudgetCategoryUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList(),
    val showInsightPopup: Boolean = false,
    val currentInsightData: com.example.mobile.data.network.dto.MicroInsight? = null
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
    private val transactionApiService = NetworkModule.createService(application, TransactionApiService::class.java)
    private val tokenManager = TokenManager(application)

    init {
        loadDashboardInitialData()
    }

    private fun loadDashboardInitialData() {
        val savedBudget = tokenManager.getSuggestedBudget()
        val savedMessage = tokenManager.getSuggestedMessage()

        _uiState.value = DashboardUiState(
            healthScore = 100,
            scoreBreakdown = ScoreBreakdown(35, 35, 20, 10),
            totalBudget = if (savedBudget > 0) savedBudget.toLong() else 10_000_000L,
            totalSpent = 0L, // Bắt đầu chi tiêu từ 0đ sau khi onboarding xong
            hasAlert = !savedMessage.isNullOrEmpty(),
            alertTitle = "GỢI Ý NGÂN SÁCH ONBOARDING",
            alertMessage = savedMessage ?: "",
            budgetCategories = listOf(
                BudgetCategoryUi("Ăn uống",  0, if (savedBudget > 0) (savedBudget * 0.35).toLong() else 3_500_000),
                BudgetCategoryUi("Đi lại",     0, if (savedBudget > 0) (savedBudget * 0.10).toLong() else 1_000_000),
                BudgetCategoryUi("Mua sắm",  0, if (savedBudget > 0) (savedBudget * 0.15).toLong() else 2_000_000),
                BudgetCategoryUi("Khác",       0, if (savedBudget > 0) (savedBudget * 0.40).toLong() else 1_500_000)
            ),
            recentTransactions = emptyList() // Bắt đầu danh sách trống sau khi onboarding
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
                            // Quy tắc ưu tiên: Nếu Backend có cảnh báo nợ (CRITICAL/WARNING) thì đè lên câu nhắc Onboarding ban đầu
                            hasAlert = firstAlert != null || !tokenManager.getSuggestedMessage().isNullOrEmpty(),
                            alertTitle = when {
                                firstAlert?.type == "CRITICAL" -> "CẢNH BÁO NGUY HIỂM!"
                                firstAlert?.type == "WARNING" -> "THẺ TÍN DỤNG SẮP ĐẾN HẠN"
                                else -> "GỢI Ý NGÂN SÁCH ONBOARDING"
                            },
                            alertMessage = firstAlert?.message ?: tokenManager.getSuggestedMessage() ?: ""
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

    fun saveTransaction(amount: java.math.BigDecimal, note: String, category: String, type: com.example.mobile.data.network.dto.TransactionType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val request = com.example.mobile.data.network.dto.TransactionRequestDTO(
                    amount = amount,
                    category = category,
                    type = type.name, // "EXPENSE" hoặc "INCOME"
                    transactionAt = null // Để Backend tự sinh theo giờ VN nếu trống
                )

                val response = transactionApiService.createTransaction(request)

                // Kiểm tra xem Backend có kích hoạt trả về MicroInsight (lần nhập thứ 3) hay không
                val insightFromServer = response.microInsight
                if (insightFromServer != null && insightFromServer.shouldShow) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showInsightPopup = true,
                            currentInsightData = insightFromServer
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                
                // Cập nhật lại số tiền đã chi tiêu (nếu là chi tiêu)
                if (type == com.example.mobile.data.network.dto.TransactionType.EXPENSE) {
                    val addedAmount = amount.toLong()
                    _uiState.update { currentState ->
                        val updatedSpent = currentState.totalSpent + addedAmount
                        // Cập nhật chi tiết danh mục
                        val updatedCategories = currentState.budgetCategories.map { cat ->
                            val isMatch = when (category.lowercase()) {
                                "food" -> cat.name == "Ăn uống"
                                "transport" -> cat.name == "Đi lại"
                                "shopping" -> cat.name == "Mua sắm"
                                else -> cat.name == "Khác"
                            }
                            if (isMatch) {
                                cat.copy(spent = cat.spent + addedAmount)
                            } else {
                                cat
                            }
                        }
                        currentState.copy(
                            totalSpent = updatedSpent,
                            budgetCategories = updatedCategories
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Lỗi kết nối Internet") }
                e.printStackTrace()
            }
        }
    }

    fun dismissInsightPopup() {
        _uiState.update { currentState ->
            currentState.copy(
                showInsightPopup = false,
                currentInsightData = null
            )
        }
    }
}
