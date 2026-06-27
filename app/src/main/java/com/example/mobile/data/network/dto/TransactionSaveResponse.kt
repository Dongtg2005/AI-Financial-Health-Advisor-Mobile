package com.example.mobile.data.network.dto

import java.math.BigDecimal

data class TransactionSaveResponse(
    val status: Int,
    val message: String,
    val data: TransactionData,
    val microInsight: MicroInsight?
) {
    data class TransactionData(
        val transactionId: String,
        val amount: BigDecimal,
        val category: String
    )
}
