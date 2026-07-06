package com.example.mobile.data.network

import android.content.Context
import com.example.mobile.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // 10.0.2.2 là địa chỉ IP đặc biệt để Android Emulator kết nối với localhost của máy tính host
    private const val BASE_URL = "http://10.0.2.2:8088/"

    private var retrofit: Retrofit? = null

    @Synchronized
    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager(context)

            // 1. Tạo Interceptor tự động thêm Bearer Token vào Header của mọi request
            val authInterceptor = okhttp3.Interceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                val token = tokenManager.getToken()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }

            // 2. Tạo Http Logging Interceptor để gỡ lỗi các yêu cầu HTTP khi phát triển
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // 3. Cấu hình OkHttpClient với timeout và interceptors
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            // 4. Xây dựng đối tượng Retrofit dùng chung
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    /**
     * Hàm tiện ích giúp khởi tạo nhanh các interface API Service
     */
    fun <T> createService(context: Context, serviceClass: Class<T>): T {
        return getRetrofit(context).create(serviceClass)
    }
}
