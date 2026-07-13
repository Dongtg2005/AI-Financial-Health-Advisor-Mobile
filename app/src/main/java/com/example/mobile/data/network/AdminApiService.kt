package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import java.math.BigDecimal
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class AdminUserDTO(
    val id: String,
    val username: String,
    val role: String,
    val financialStage: String,
    val monthlyIncome: BigDecimal?,
    val suggestedBudget: BigDecimal?,
    val healthScore: Int,
    val createdAt: String,
    val isEnabled: Boolean
)

data class AdminAnalyticsDTO(
    val totalUsers: Long,
    val averageHealthScore: Double,
    val activeUsersCount: Long,
    val debtRepaymentCount: Long,
    val emergencyFundCount: Long
)

data class AdminUserDetailDTO(
    val userId: String,
    val username: String,
    val healthScore: Int,
    val scoreSpending: Int,
    val scoreDebt: Int,
    val scoreSaving: Int,
    val scoreAwareness: Int,
    val categoryPercentages: Map<String, Double>
)

interface AdminApiService {
    @GET("api/v1/admin/users")
    suspend fun getAllUsers(): ApiResponse<List<AdminUserDTO>>

    @PUT("api/v1/admin/users/{id}/toggle-status")
    suspend fun toggleUserStatus(@Path("id") userId: String): ApiResponse<Unit>

    @GET("api/v1/admin/analytics")
    suspend fun getSystemAnalytics(): ApiResponse<AdminAnalyticsDTO>

    @GET("api/v1/admin/users/{id}/details")
    suspend fun getUserDetails(@Path("id") userId: String): ApiResponse<AdminUserDetailDTO>

    @PUT("api/v1/admin/users/{id}/send-warning")
    suspend fun sendWarning(@Path("id") userId: String, @Body payload: Map<String, String>): ApiResponse<Unit>

    @GET("api/v1/admin/settings")
    suspend fun getSettings(): ApiResponse<Map<String, String>>

    @PUT("api/v1/admin/settings")
    suspend fun updateSettings(@Body settings: Map<String, String>): ApiResponse<Unit>

    @POST("api/v1/admin/scan-risks")
    suspend fun scanRisks(): ApiResponse<Unit>

    @POST("api/v1/admin/users/batch-warning")
    suspend fun sendBatchWarning(@Body payload: Map<String, String>): ApiResponse<Unit>
}
