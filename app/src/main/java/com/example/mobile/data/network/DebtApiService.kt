package com.example.mobile.data.network

import com.example.mobile.data.network.dto.DashboardResponse
import retrofit2.http.GET

interface DebtApiService {
    @GET("api/v1/dashboard/summary")
    suspend fun getDashboardSummary(): DashboardResponse
}
