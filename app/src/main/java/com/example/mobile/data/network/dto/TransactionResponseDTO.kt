package com.example.mobile.data.network.dto

import java.math.BigDecimal

data class TransactionResponseDTO(
    val id: String,
    val userId: String,
    val type: String,
    val amount: BigDecimal,
    val category: String,
    val transactionAt: String,
    val isConfirmed: Boolean
)
