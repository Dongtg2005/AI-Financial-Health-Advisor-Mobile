package com.example.mobile.ui.auth

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.TokenManager
import com.example.mobile.data.network.AuthApiService
import com.example.mobile.data.network.AuthRequest
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val authApiService = NetworkModule.createService(application, AuthApiService::class.java)
    private val tokenManager = TokenManager(application)

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authApiService.login(AuthRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.data?.token
                    if (!token.isNullOrEmpty()) {
                        tokenManager.saveJwtToken(token)
                        onSuccess()
                    } else {
                        Toast.makeText(getApplication(), "Lỗi: Không nhận được token từ server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(getApplication(), "Đăng nhập thất bại: Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authApiService.register(RegisterRequest(email, pass, 0.0))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.data?.token
                    if (!token.isNullOrEmpty()) {
                        tokenManager.saveJwtToken(token)
                        onSuccess()
                    } else {
                        Toast.makeText(getApplication(), "Lỗi: Không nhận được token từ server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(getApplication(), "Đăng ký thất bại: Tài khoản có thể đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
