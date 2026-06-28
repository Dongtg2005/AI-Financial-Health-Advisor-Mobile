package com.example.mobile.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.TokenManager
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.BudgetApiService
import com.example.mobile.data.network.UserApiService
import com.example.mobile.data.network.OnboardingRequest
import com.example.mobile.data.network.dto.BudgetSuggestionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class OnboardingUiState(
    val isSubmitting: Boolean = false,
    val suggestion: BudgetSuggestionResponse? = null
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val budgetApiService = NetworkModule.createService(application, BudgetApiService::class.java)
    private val userApiService = NetworkModule.createService(application, UserApiService::class.java)
    private val tokenManager = TokenManager(application)

    fun fetchBudgetSuggestion(income: Double) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSubmitting = true) }
                val response = budgetApiService.getBudgetSuggestion(income)
                if (response.status == 200 && response.data != null) {
                    val data = response.data
                    // Lưu trữ ngầm vào TokenManager để Dashboard có thể hiển thị ngay
                    tokenManager.saveSuggestedBudget(data.suggestedTotalBudget, data.message)
                    _uiState.update { 
                        it.copy(isSubmitting = false, suggestion = data)
                    }
                } else {
                    _uiState.update { it.copy(isSubmitting = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false) }
                e.printStackTrace()
            }
        }
    }

    fun submitOnboarding(income: String, budget: String, onSuccess: () -> Unit) {
        val incomeVal = income.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val budgetVal = budget.toBigDecimalOrNull() ?: BigDecimal.ZERO

        if (incomeVal <= BigDecimal.ZERO || budgetVal <= BigDecimal.ZERO) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userApiService.submitOnboarding(OnboardingRequest(incomeVal, budgetVal))
                if (response.isSuccessful) {
                    // Lưu cục bộ để Dashboard dùng ngay không cần chờ load lại
                    tokenManager.saveSuggestedBudget(budgetVal.toDouble(), "Ngân sách tự chọn")
                    
                    // PHÁT SÓNG TOÀN CỤC: Báo cho Dashboard biết để cập nhật cấu hình mới
                    viewModelScope.launch {
                        com.example.mobile.common.AppEventBus.emit(com.example.mobile.common.AppEvent.OnboardingCompleted)
                    }

                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
