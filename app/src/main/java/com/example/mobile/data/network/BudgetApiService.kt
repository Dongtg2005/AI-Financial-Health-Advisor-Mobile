package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import com.example.mobile.data.network.dto.BudgetSuggestionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BudgetApiService {
    @GET("api/v1/budgets/suggestion")
    suspend fun getBudgetSuggestion(
        @Query("income") income: Double
    ): ApiResponse<BudgetSuggestionResponse>
}
