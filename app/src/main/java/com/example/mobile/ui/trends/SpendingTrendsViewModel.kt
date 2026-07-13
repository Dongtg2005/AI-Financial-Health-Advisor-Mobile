package com.example.mobile.ui.trends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.TransactionApiService
import com.example.mobile.data.network.dto.TrendItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpendingTrendsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val monthlyTrends: List<TrendItem> = emptyList(),
    val quarterlyTrends: List<TrendItem> = emptyList(),
    val yearlyTrends: List<TrendItem> = emptyList()
)

class SpendingTrendsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SpendingTrendsUiState())
    val uiState: StateFlow<SpendingTrendsUiState> = _uiState.asStateFlow()

    private val apiService = NetworkModule.createService(application, TransactionApiService::class.java)

    init {
        fetchSpendingTrends()
    }

    fun fetchSpendingTrends() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = apiService.getSpendingTrends()
                if (response.status == 200 && response.data != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            monthlyTrends = response.data.monthlyTrends,
                            quarterlyTrends = response.data.quarterlyTrends,
                            yearlyTrends = response.data.yearlyTrends,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Không thể lấy thông tin xu hướng"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối máy chủ: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}
