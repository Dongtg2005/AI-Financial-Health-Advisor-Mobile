package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import com.example.mobile.data.network.dto.AppDetectBatchRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface DetectApiService {
    @POST("api/v1/detects/batch")
    suspend fun sendDetectBatch(@Body request: AppDetectBatchRequest): ApiResponse<Any>
}
