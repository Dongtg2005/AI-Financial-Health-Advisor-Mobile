package com.example.mobile.ui.profile

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.TokenManager
import com.example.mobile.data.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val suggestedBudget: Double = 10000000.0,
    val dailyNotificationsEnabled: Boolean = true,
    val aiAlertsEnabled: Boolean = true,
    val billingCycleDay: Int = 5,
    val walletsList: List<Pair<String, Double>> = emptyList(),
    val isExporting: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val tokenManager = TokenManager(application)
    private val userApiService = NetworkModule.createService(application, UserApiService::class.java)
    private val walletApiService = NetworkModule.createService(application, WalletApiService::class.java)
    private val transactionApiService = NetworkModule.createService(application, TransactionApiService::class.java)

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 1. Fetch user settings & profile
                val profileResponse = userApiService.getProfile()
                // 2. Fetch wallets list
                val walletsResponse = walletApiService.getWallets()

                if (profileResponse.status == 200 && profileResponse.data != null && walletsResponse.status == 200 && walletsResponse.data != null) {
                    val profile = profileResponse.data
                    val budget = profile.suggestedBudget?.toDouble() ?: tokenManager.getSuggestedBudget()
                    
                    // Sync tokenManager
                    tokenManager.saveSuggestedBudget(budget, "Sync from server")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            suggestedBudget = budget,
                            dailyNotificationsEnabled = profile.dailyNotifEnabled,
                            aiAlertsEnabled = profile.aiAlertsEnabled,
                            billingCycleDay = profile.billingCycleDay,
                            walletsList = walletsResponse.data.map { w -> w.name to w.balance.toDouble() },
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = profileResponse.message ?: walletsResponse.message ?: "Lỗi tải thông tin cá nhân"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối máy chủ: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun updateBudget(newBudget: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userApiService.submitOnboarding(
                    OnboardingRequest(
                        monthlyIncome = BigDecimal(newBudget * 1.5),
                        suggestedBudget = BigDecimal(newBudget)
                    )
                )
                if (response.isSuccessful) {
                    tokenManager.saveSuggestedBudget(newBudget, "Updated Budget")
                    _uiState.update { it.copy(suggestedBudget = newBudget) }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun addWallet(name: String, balance: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = walletApiService.createWallet(
                    WalletRequestDTO(name, BigDecimal(balance))
                )
                if (response.status == 201 && response.data != null) {
                    _uiState.update {
                        it.copy(
                            walletsList = it.walletsList + (name to balance)
                        )
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun updateSettings(dailyNotif: Boolean, aiAlerts: Boolean, cycleDay: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userApiService.updateSettings(
                    UpdateSettingsRequest(dailyNotif, aiAlerts, cycleDay)
                )
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            dailyNotificationsEnabled = dailyNotif,
                            aiAlertsEnabled = aiAlerts,
                            billingCycleDay = cycleDay
                        )
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun exportTransactionsExcel(onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            try {
                val responseBody = transactionApiService.exportTransactions()
                val bytes = responseBody.bytes()

                // Save to downloads directory
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "FinanceReport.csv")
                
                FileOutputStream(file).use { fos ->
                    fos.write(bytes)
                }

                _uiState.update { it.copy(isExporting = false) }
                onSuccess(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isExporting = false) }
                onFailure(e.localizedMessage ?: "Lỗi xuất file")
            }
        }
    }
}
