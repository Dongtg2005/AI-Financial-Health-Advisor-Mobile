package com.example.mobile.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.TransactionApiService
import com.example.mobile.data.network.dto.TransactionResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionApiService = NetworkModule.createService(application, TransactionApiService::class.java)

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = transactionApiService.getTransactions()
                if (response.status == 200 && response.data != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            transactions = response.data,
                            filteredTransactions = applyFilter(response.data, currentState.selectedCategory),
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = "Không thể tải lịch sử giao dịch") }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeCategoryFilter(category: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                filteredTransactions = applyFilter(currentState.transactions, category)
            )
        }
    }

    private fun applyFilter(list: List<TransactionResponseDTO>, category: String): List<TransactionResponseDTO> {
        if (category == "Tất cả") return list
        val mappedCategory = when (category) {
            "Ăn uống" -> "food"
            "Đi lại" -> "transport"
            "Mua sắm" -> "shopping"
            "Trả nợ" -> "debt"
            else -> "other"
        }
        return list.filter { it.category.lowercase() == mappedCategory }
    }
}
