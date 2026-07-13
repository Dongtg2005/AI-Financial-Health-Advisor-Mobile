package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import java.math.BigDecimal

data class OnboardingRequest(
    val monthlyIncome: BigDecimal,
    val suggestedBudget: BigDecimal
)

data class UserProfileResponse(
    val username: String,
    val createdAt: String?,
    val suggestedBudget: BigDecimal?,
    val dailyNotifEnabled: Boolean,
    val aiAlertsEnabled: Boolean,
    val billingCycleDay: Int
)

data class UpdateSettingsRequest(
    val dailyNotifEnabled: Boolean,
    val aiAlertsEnabled: Boolean,
    val billingCycleDay: Int
)

interface UserApiService {
    @POST("api/v1/users/onboarding")
    suspend fun submitOnboarding(@Body request: OnboardingRequest): Response<Unit>

    @GET("api/v1/users/profile")
    suspend fun getProfile(): ApiResponse<UserProfileResponse>

    @PUT("api/v1/users/settings")
    suspend fun updateSettings(@Body request: UpdateSettingsRequest): Response<Unit>
}
