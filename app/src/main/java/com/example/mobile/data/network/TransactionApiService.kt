package com.example.mobile.data.network

import com.example.mobile.data.network.dto.TransactionRequestDTO
import com.example.mobile.data.network.dto.TransactionSaveResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService {

    @POST("api/v1/transactions")
    suspend fun createTransaction(
        @Body request: TransactionRequestDTO
    ): TransactionSaveResponse
}
