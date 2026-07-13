package com.example.mobile.data.network

import com.example.mobile.data.network.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.math.BigDecimal

data class WalletRequestDTO(
    val name: String,
    val balance: BigDecimal
)

data class WalletResponseDTO(
    val id: String,
    val name: String,
    val balance: BigDecimal
)

interface WalletApiService {
    @GET("api/v1/wallets")
    suspend fun getWallets(): ApiResponse<List<WalletResponseDTO>>

    @POST("api/v1/wallets")
    suspend fun createWallet(@Body request: WalletRequestDTO): ApiResponse<WalletResponseDTO>
}
