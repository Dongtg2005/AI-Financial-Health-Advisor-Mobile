package com.example.mobile.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.math.BigDecimal

data class OnboardingRequest(
    val monthlyIncome: BigDecimal,
    val suggestedBudget: BigDecimal
)

interface UserApiService {
    @POST("api/v1/users/onboarding")
    suspend fun submitOnboarding(@Body request: OnboardingRequest): Response<Unit>
}
