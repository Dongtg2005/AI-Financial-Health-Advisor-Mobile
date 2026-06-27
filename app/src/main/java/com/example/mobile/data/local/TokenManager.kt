package com.example.mobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val TOKEN_KEY = "jwt_token"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    fun saveSuggestedBudget(amount: Double, message: String) {
        sharedPreferences.edit()
            .putFloat("suggested_budget", amount.toFloat())
            .putString("suggested_message", message)
            .apply()
    }

    fun getSuggestedBudget(): Double {
        return sharedPreferences.getFloat("suggested_budget", 0f).toDouble()
    }

    fun getSuggestedMessage(): String? {
        return sharedPreferences.getString("suggested_message", null)
    }
}
