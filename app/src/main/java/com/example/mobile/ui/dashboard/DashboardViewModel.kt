package com.example.mobile.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.TokenManager
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.DebtApiService
import com.example.mobile.data.network.TransactionApiService
import com.example.mobile.data.network.dto.CashWeeklyEstimateRequest
import com.example.mobile.data.network.dto.MicroInsight
import com.example.mobile.data.network.dto.TransactionRequestDTO
import com.example.mobile.data.network.dto.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import com.example.mobile.common.AppEvent
import com.example.mobile.common.AppEventBus
import kotlinx.coroutines.flow.collectLatest

data class ScoreBreakdown(
    val spending: Int = 0,
    val debt: Int = 0,
    val saving: Int = 0,
    val awareness: Int = 0
)

data class DashboardUiState(
    val userName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val healthScore: Int = 0,
    val totalSpent: Long = 0,
    val totalBudget: Long = 0,
    val scoreBreakdown: ScoreBreakdown = ScoreBreakdown(),
    val hasAlert: Boolean = false,
    val alertTitle: String = "",
    val alertMessage: String = "",
    val budgetCategories: List<BudgetCategoryUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList(),
    val showInsightPopup: Boolean = false,
    val currentInsightData: MicroInsight? = null
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
        listenToGlobalEvents()
    }

    private fun listenToGlobalEvents() {
        viewModelScope.launch {
            AppEventBus.events.collectLatest { event ->
                when (event) {
                    is AppEvent.OnboardingCompleted, 
                    is AppEvent.TransactionCreated -> {
                        fetchDashboardData()
                    }
                }
            }
        }
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

    /**
     * TẢI DỮ LIỆU ĐỒNG BỘ LIVE SỨC KHỎE TÀI CHÍNH TỪ DATABASE BACKEND
     */
    fun fetchDashboardData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                // Gọi API tổng hợp mới từ Server
                val response = apiService.getDashboardSummary()
                
                // Gọi thêm API lấy danh sách giao dịch gần đây thực tế
                val txResponse = try {
                    transactionApiService.getTransactions()
                } catch (e: Exception) {
                    null
                }
                
                if (response.status == 200 && response.data != null) {
                    val serverData = response.data
                    val firstAlert = serverData.alerts.firstOrNull()
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            userName = serverData.userName,
                            healthScore = serverData.healthScore, // Điểm tổng live
                            
                            // Đấu nối trực tiếp 4 đầu điểm phân rã thực tế từ DB
                            scoreBreakdown = ScoreBreakdown(
                                spending = serverData.scoreSpending,
                                debt = serverData.scoreDebt,
                                saving = serverData.scoreSaving,
                                awareness = serverData.scoreAwareness
                            ),
                            
                            totalSpent = serverData.totalSpent,
                            totalBudget = serverData.totalBudget,
                            hasAlert = firstAlert != null || !tokenManager.getSuggestedMessage().isNullOrEmpty(),
                            alertTitle = when {
                                firstAlert?.type == "CRITICAL" -> "CẢNH BÁO NGUY HIỂM!"
                                firstAlert?.type == "WARNING" -> "THẺ TÍN DỤNG SẮP ĐẾN HẠN"
                                else -> "GỢI Ý NGÂN SÁCH ONBOARDING"
                            },
                            alertMessage = firstAlert?.message ?: tokenManager.getSuggestedMessage() ?: "",
                            
                            // Map danh mục chi tiêu thật từ DB lên UI
                            budgetCategories = serverData.budgetCategories.map { dto ->
                                BudgetCategoryUi(name = dto.name, spent = dto.spent, limit = dto.limit)
                            },
                            recentTransactions = (txResponse?.data ?: emptyList())
                                .take(4)
                                .map { dto ->
                                    TransactionUi(
                                        id = dto.id,
                                        name = dto.category.replaceFirstChar { it.uppercase() } + " - " + (if (dto.type == "INCOME") "Thu nhập" else "Chi tiêu"),
                                        date = dto.transactionAt.substringBefore("T"),
                                        amount = dto.amount.toLong(),
                                        isIncome = dto.type == "INCOME",
                                        category = dto.category.lowercase()
                                    )
                                }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.localizedMessage ?: "Không thể kết nối đồng bộ điểm sức khỏe"
                    ) 
                }
                e.printStackTrace();
            }
        }
    }

    fun saveTransaction(amount: BigDecimal, note: String, category: String, type: TransactionType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val request = TransactionRequestDTO(
                    amount = amount,
                    category = category,
                    type = type.name, // "EXPENSE" hoặc "INCOME"
                    transactionAt = null // Để Backend tự sinh theo giờ VN nếu trống
                )

                val response = transactionApiService.createTransaction(request)

                // PHÁT SÓNG TOÀN CỤC: Giao dịch mới đã tạo thành công
                com.example.mobile.common.AppEventBus.emit(com.example.mobile.common.AppEvent.TransactionCreated)

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
                if (type == TransactionType.EXPENSE) {
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

    /**
     * TẦNG 2: GỬI ƯỚC TÍNH TIỀN MẶT CUỐI TUẦN LÊN SERVER ĐỂ PHÂN BỔ NGẦM
     */
    fun submitWeeklyCashEstimate(totalAmount: BigDecimal, category: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Bật trạng thái Loading và xóa sạch lỗi cũ
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // 2. Đóng gói DTO theo đúng giao ước Contract
                val request = CashWeeklyEstimateRequest(
                    totalAmount = totalAmount,
                    category = category // Ví dụ: "FOOD", "TRANSPORT",...
                )

                // 3. Thực hiện bắn request lên mạng qua Retrofit
                val response = transactionApiService.createWeeklyCashEstimate(request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false) }
                    
                    // 4. Kích hoạt Callback báo về cho View biết để đóng BottomSheet/Dialog
                    onSuccess()
                    
                    // Cập nhật lại giao diện (Cộng dồn số tiền đã chi tiêu của tuần)
                    val addedAmount = totalAmount.toLong()
                    _uiState.update { currentState ->
                        val updatedSpent = currentState.totalSpent + addedAmount
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
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Lỗi từ Server: Không thể phân bổ tiền mặt (Mã ${response.code()})"
                        ) 
                    }
                }
            } catch (e: Exception) {
                // 5. Bắt các lỗi mất kết nối Internet, Timeout...
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.localizedMessage ?: "Lỗi kết nối đến máy chủ"
                    ) 
                }
                e.printStackTrace()
            }
        }
    }
}
