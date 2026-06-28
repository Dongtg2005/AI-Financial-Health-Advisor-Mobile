package com.example.mobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {

    // 🔒 KHỞI TẠO BỘ KHÓA PHẦN CỨNG: Tạo Master Key dùng thuật toán AES256-GCM
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // 🛡️ ENCRYPTED SHAREDPREFERENCES: Tự động mã hóa hai chiều toàn bộ dữ liệu ghi xuống bộ nhớ
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_finance_prefs", // Tên file XML lưu trữ đã mã hóa
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // Mã hóa Key
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Mã hóa Value
    )

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_SUGGESTED_BUDGET = "suggested_budget"
        private const val KEY_BUDGET_MESSAGE = "budget_message"
    }

    /**
     * Lưu trữ Token JWT an toàn sau khi Đăng nhập/Đăng ký thành công
     */
    fun saveJwtToken(token: String) {
        sharedPreferences.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    /**
     * Lấy Token JWT để gắn vào Header của các API gửi lên Backend
     */
    fun getJwtToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }

    // Tương thích ngược với các thành phần cũ gọi saveToken/getToken/clearToken
    fun saveToken(token: String) {
        saveJwtToken(token)
    }

    fun getToken(): String? {
        return getJwtToken()
    }

    fun clearToken() {
        clearAuthData()
    }

    /**
     * Đồng bộ Signature 2 tham số thực tế của Đông tại OnboardingViewModel
     */
    fun saveSuggestedBudget(budget: Double, message: String) {
        sharedPreferences.edit().apply {
            putFloat(KEY_SUGGESTED_BUDGET, budget.toFloat())
            putString(KEY_BUDGET_MESSAGE, message)
            apply()
        }
    }

    /**
     * Lấy ngân sách đã lưu phục vụ hiển thị live tại Dashboard
     */
    fun getSuggestedBudget(): Double {
        return sharedPreferences.getFloat(KEY_SUGGESTED_BUDGET, 0.0f).toDouble()
    }

    /**
     * Lấy tin nhắn gợi ý ngân sách phục vụ hiển thị live tại Dashboard
     */
    fun getSuggestedMessage(): String? {
        return sharedPreferences.getString(KEY_BUDGET_MESSAGE, null)
    }

    /**
     * Đăng xuất - Xóa sạch toàn bộ cache mã hóa
     */
    fun clearAuthData() {
        sharedPreferences.edit().clear().apply()
    }
}
