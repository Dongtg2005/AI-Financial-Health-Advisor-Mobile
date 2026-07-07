package com.example.mobile.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.common.AppEvent
import com.example.mobile.common.AppEventBus
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.TransactionApiService
import com.example.mobile.data.network.dto.TransactionRequestDTO
import com.example.mobile.data.network.dto.TransactionResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

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
                            filteredTransactions = applyFilter(response.data, currentState.selectedCategory, currentState.searchQuery),
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = "Khong the tai lich su giao dich") }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeCategoryFilter(category: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                filteredTransactions = applyFilter(currentState.transactions, category, currentState.searchQuery)
            )
        }
    }

    fun changeSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredTransactions = applyFilter(currentState.transactions, currentState.selectedCategory, query)
            )
        }
    }

    private fun applyFilter(
        list: List<TransactionResponseDTO>,
        category: String,
        query: String
    ): List<TransactionResponseDTO> {
        val byCategory = if (category == "Tat ca") {
            list
        } else {
            val mappedCategory = when (category) {
                "An uong" -> "food"
                "Di lai" -> "transport"
                "Mua sam" -> "shopping"
                "Tra no" -> "debt"
                else -> "other"
            }
            list.filter { it.category.lowercase() == mappedCategory }
        }

        return if (query.isBlank()) {
            byCategory
        } else {
            byCategory.filter {
                it.category.contains(query, ignoreCase = true) ||
                (if (it.category.lowercase() == "food") "an uong" else "").contains(query, ignoreCase = true) ||
                (if (it.category.lowercase() == "transport") "di lai" else "").contains(query, ignoreCase = true) ||
                (if (it.category.lowercase() == "shopping") "mua sam" else "").contains(query, ignoreCase = true) ||
                (if (it.category.lowercase() == "debt") "tra no" else "").contains(query, ignoreCase = true) ||
                it.amount.toString().contains(query)
            }
        }
    }

    /**
     * Luu giao dich moi tu FAB button tren man hinh lich su.
     * Goi API, cap nhat global event, sau do tu dong refresh danh sach.
     */
    fun saveTransaction(amountStr: String, category: String, type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = TransactionRequestDTO(
                    amount = BigDecimal(amountStr),
                    category = category,
                    type = type,
                    transactionAt = null
                )
                transactionApiService.createTransaction(request)
                // Thong bao cho Dashboard tu dong cap nhat diem suc khoe
                AppEventBus.emit(AppEvent.TransactionCreated)
                // Lam moi danh sach giao dich
                fetchTransactions()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = "Khong the luu giao dich: ${e.localizedMessage}") }
                _isLoading.value = false
            }
        }
    }
}
