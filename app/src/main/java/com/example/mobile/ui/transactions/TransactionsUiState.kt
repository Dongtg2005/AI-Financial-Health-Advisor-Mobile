package com.example.mobile.ui.transactions

import com.example.mobile.data.network.dto.TransactionResponseDTO

data class TransactionsUiState(
    val transactions: List<TransactionResponseDTO> = emptyList(),
    val filteredTransactions: List<TransactionResponseDTO> = emptyList(),
    val selectedCategory: String = "Tất cả",
    val errorMessage: String? = null
)
