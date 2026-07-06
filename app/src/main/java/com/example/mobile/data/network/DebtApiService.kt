package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import com.example.mobile.data.network.dto.DashboardResponse
import com.example.mobile.data.network.dto.DebtCreateRequest
import com.example.mobile.data.network.dto.DebtDetails
import com.example.mobile.data.network.dto.DebtSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DebtApiService {
    @GET("api/v1/dashboard/summary")
    suspend fun getDashboardSummary(): DashboardResponse

    @GET("api/v1/debts/summary")
    suspend fun getDebtSummary(): ApiResponse<DebtSummaryResponse>

    @GET("api/v1/debts")
    suspend fun getDebts(): ApiResponse<List<DebtDetails>>

    @POST("api/v1/debts")
    suspend fun createDebt(@Body request: DebtCreateRequest): ApiResponse<DebtDetails>
}
