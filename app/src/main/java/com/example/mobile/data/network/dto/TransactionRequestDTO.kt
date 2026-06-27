package com.example.mobile.data.network.dto

import java.math.BigDecimal

data class TransactionRequestDTO(
    val amount: BigDecimal,
    val category: String,
    val type: String,
    val transactionAt: String? = null
)
