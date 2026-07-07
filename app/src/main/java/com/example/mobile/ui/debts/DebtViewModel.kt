package com.example.mobile.ui.debts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.AppDatabase
import com.example.mobile.data.local.entity.DebtEntity
import com.example.mobile.data.network.DebtApiService
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.dto.DebtCreateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DebtUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val debts: List<DebtEntity> = emptyList(),
    val totalActiveDebt: Double = 0.0,
    val overdueCount: Int = 0,
    val upcomingCount: Int = 0
)

class DebtViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DebtUiState())
    val uiState: StateFlow<DebtUiState> = _uiState.asStateFlow()

    private val debtDao = AppDatabase.getDatabase(application).debtDao()
    private val apiService = NetworkModule.createService(application, DebtApiService::class.java)

    init {
        observeLocalDebts()
        syncDebtsFromServer()
    }

    private fun observeLocalDebts() {
        viewModelScope.launch {
            debtDao.getActiveDebtsFlow().collectLatest { localList ->
                val total = localList.sumOf { it.balance }
                val overdue = localList.count { it.overdueDays > 0 || isDatePast(it.dueDate) }
                val upcoming = localList.count { (it.overdueDays <= 0 && !isDatePast(it.dueDate)) && it.daysRemaining in 0..7 }

                _uiState.update { currentState ->
                    currentState.copy(
                        debts = localList,
                        totalActiveDebt = total,
                        overdueCount = overdue,
                        upcomingCount = upcoming
                    )
                }
            }
        }
    }

    fun syncDebtsFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = apiService.getDebts()
                if (response.status == 200 && response.data != null) {
                    val entities = response.data.map { dto ->
                        DebtEntity(
                            id = dto.id,
                            type = dto.type,
                            balance = dto.balance,
                            minimumPayment = dto.minimumPayment,
                            dueDate = dto.dueDate,
                            daysRemaining = dto.daysRemaining,
                            overdueDays = dto.overdueDays,
                            overdueSince = dto.overdueSince,
                            isActive = dto.isActive
                        )
                    }
                    debtDao.insertDebts(entities)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Không thể đồng bộ danh sách khoản nợ"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun createDebt(
        type: String,
        balance: Double,
        minimumPayment: Double,
        dueDate: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val request = DebtCreateRequest(
                    type = type,
                    balance = balance,
                    minimumPayment = minimumPayment,
                    dueDate = dueDate
                )
                val response = apiService.createDebt(request)
                if (response.status == 201 && response.data != null) {
                    val dto = response.data
                    val entity = DebtEntity(
                        id = dto.id,
                        type = dto.type,
                        balance = dto.balance,
                        minimumPayment = dto.minimumPayment,
                        dueDate = dto.dueDate,
                        daysRemaining = dto.daysRemaining,
                        overdueDays = dto.overdueDays,
                        overdueSince = dto.overdueSince,
                        isActive = dto.isActive
                    )
                    debtDao.insertDebt(entity)
                    _uiState.update { it.copy(isLoading = false) }
                    // Phát sự kiện toàn cục để Dashboard tự động reload điểm sức khỏe
                    com.example.mobile.common.AppEventBus.emit(com.example.mobile.common.AppEvent.DebtCreated)
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Tạo khoản nợ thất bại"
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback offline: nếu mất kết nối internet, vẫn lưu tạm vào Room để trải nghiệm mượt mà!
                val offlineId = "offline_${System.currentTimeMillis()}"
                val daysRem = calculateDaysRemaining(dueDate)
                val isOverdue = daysRem < 0
                val offlineEntity = DebtEntity(
                    id = offlineId,
                    type = type,
                    balance = balance,
                    minimumPayment = minimumPayment,
                    dueDate = dueDate,
                    daysRemaining = if (isOverdue) 0 else daysRem,
                    overdueDays = if (isOverdue) Math.abs(daysRem) else 0,
                    overdueSince = if (isOverdue) dueDate else null,
                    isActive = true
                )
                debtDao.insertDebt(offlineEntity)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Đã lưu offline (mất kết nối máy chủ)"
                    )
                }
                onSuccess()
                e.printStackTrace()
            }
        }
    }

    fun payoffDebt(debtId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = apiService.payoffDebt(debtId)
                if (response.status == 200 && response.data != null) {
                    val dto = response.data
                    val entity = DebtEntity(
                        id = dto.id,
                        type = dto.type,
                        balance = dto.balance,
                        minimumPayment = dto.minimumPayment,
                        dueDate = dto.dueDate,
                        daysRemaining = dto.daysRemaining,
                        overdueDays = dto.overdueDays,
                        overdueSince = dto.overdueSince,
                        isActive = dto.isActive
                    )
                    debtDao.insertDebt(entity)

                    // Phát sự kiện toàn cục để Dashboard tự động reload
                    com.example.mobile.common.AppEventBus.emit(com.example.mobile.common.AppEvent.DebtCreated)
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Tất toán khoản nợ thất bại"
                        )
                    }
                }
            } catch (e: Exception) {
                try {
                    debtDao.markAsPaidOffline(debtId)
                    com.example.mobile.common.AppEventBus.emit(com.example.mobile.common.AppEvent.DebtCreated)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Đã tất toán offline (mất kết nối máy chủ)"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    private fun isDatePast(dateStr: String): Boolean {
        return try {
            val date = LocalDate.parse(dateStr)
            date.isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateDaysRemaining(dueDateStr: String): Long {
        return try {
            val date = LocalDate.parse(dueDateStr)
            ChronoUnit.DAYS.between(LocalDate.now(), date)
        } catch (e: Exception) {
            30L
        }
    }
}
