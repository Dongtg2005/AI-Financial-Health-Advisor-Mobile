package com.example.mobile.data.network.dto

data class AppDetectBatchRequest(
    val detects: List<DetectItemDto>
)

data class DetectItemDto(
    val appPackageName: String,
    val bankName: String,
    val detectedAt: String
)
