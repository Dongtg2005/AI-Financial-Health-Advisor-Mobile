package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import com.example.mobile.data.network.dto.DebtSummaryResponse
import retrofit2.http.GET

interface DebtApiService {
    @GET("api/v1/debts/summary")
    suspend fun getDebtSummary(): ApiResponse<DebtSummaryResponse>
}
