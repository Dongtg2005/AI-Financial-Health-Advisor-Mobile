package com.example.mobile.data.network

import com.example.mobile.data.network.dto.CashWeeklyEstimateRequest
import com.example.mobile.data.network.dto.TransactionRequestDTO
import com.example.mobile.data.network.dto.TransactionSaveResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService {

    @POST("api/v1/transactions")
    suspend fun createTransaction(
        @Body request: TransactionRequestDTO
    ): TransactionSaveResponse

    /**
     * TẦNG 2: ĐÓN ĐẦU ENDPOINT ƯỚC TÍNH TIỀN MẶT CUỐI TUẦN
     * Bắn dữ liệu lên đường dẫn: api/v1/transactions/cash-estimate
     */
    @POST("api/v1/transactions/cash-estimate")
    suspend fun createWeeklyCashEstimate(
        @Body request: CashWeeklyEstimateRequest
    ): retrofit2.Response<Map<String, Any>>
}
