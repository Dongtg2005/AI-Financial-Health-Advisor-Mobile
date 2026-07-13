package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    @SerializedName("monthly_income")
    val monthlyIncome: Double = 0.0
)

data class AuthResponse(
    val token: String,
    val role: String? = "USER"
)

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
}
