package com.example.mobile.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.network.AdminApiService
import com.example.mobile.data.network.AdminUserDTO
import com.example.mobile.data.network.AdminAnalyticsDTO
import com.example.mobile.data.network.AdminUserDetailDTO
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val users: List<AdminUserDTO> = emptyList(),
    val analytics: AdminAnalyticsDTO? = null,
    val selectedUserDetail: AdminUserDetailDTO? = null,
    val showDetailDialog: Boolean = false,
    val currentTab: Int = 0, // 0: Users, 1: Risk Alerts, 2: Settings
    val settings: Map<String, String> = emptyMap(),
    val searchQuery: String = "",
    val sortOrder: String = "DATE_DESC", // DATE_DESC, SCORE_ASC, SCORE_DESC
    val isSettingsSaving: Boolean = false,
    val settingsSaveSuccess: Boolean = false,
    val currentAdminRole: String = "USER"
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val apiService = NetworkModule.createService(application, AdminApiService::class.java)
    private val tokenManager = TokenManager(application)

    init {
        _uiState.update { it.copy(currentAdminRole = tokenManager.getUserRole()) }
        refreshDashboard()
    }

    fun refreshDashboard() {
        val role = tokenManager.getUserRole()
        if (role in listOf("ADMIN", "ADMIN_SUPPORT", "ADMIN_SECURITY")) {
            fetchUsers()
        }
        if (role in listOf("ADMIN", "ADMIN_SUPPORT", "ADMIN_SYSTEM", "ADMIN_SECURITY")) {
            fetchAnalytics()
        }
        if (role in listOf("ADMIN", "ADMIN_SYSTEM")) {
            fetchSettings()
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = apiService.getAllUsers()
                if (response.status == 200 && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            users = response.data,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Không thể lấy danh sách người dùng"
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

    fun fetchAnalytics() {
        viewModelScope.launch {
            try {
                val response = apiService.getSystemAnalytics()
                if (response.status == 200 && response.data != null) {
                    _uiState.update { it.copy(analytics = response.data) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSettings() {
        viewModelScope.launch {
            try {
                val response = apiService.getSettings()
                if (response.status == 200 && response.data != null) {
                    _uiState.update { it.copy(settings = response.data) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleUserStatus(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.toggleUserStatus(userId)
                if (response.status == 200) {
                    refreshDashboard()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Không thể thay đổi trạng thái người dùng"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getUserDetails(userId)
                if (response.status == 200 && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedUserDetail = response.data,
                            showDetailDialog = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Không thể lấy chi tiết người dùng"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun sendWarningMessage(userId: String, message: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.sendWarning(userId, mapOf("message" to message))
                if (response.status == 200) {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                    refreshDashboard()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi gửi cảnh báo: ${e.localizedMessage}") }
            }
        }
    }

    fun saveSystemSettings(settings: Map<String, String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSettingsSaving = true, settingsSaveSuccess = false) }
            try {
                val response = apiService.updateSettings(settings)
                if (response.status == 200) {
                    _uiState.update { it.copy(isSettingsSaving = false, settingsSaveSuccess = true, settings = settings) }
                } else {
                    _uiState.update { it.copy(isSettingsSaving = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isSettingsSaving = false, errorMessage = "Lỗi lưu cấu hình: ${e.localizedMessage}") }
            }
        }
    }

    fun updateTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex, errorMessage = null) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSortOrder(order: String) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun dismissDetailDialog() {
        _uiState.update { it.copy(showDetailDialog = false, selectedUserDetail = null) }
    }

    fun runRiskScan(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.scanRisks()
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(response.message ?: "AI quét rủi ro hoàn tất.")
                refreshDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = "Quét thất bại: ${e.localizedMessage}") }
            }
        }
    }

    fun sendBatchWarning(stage: String, message: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.sendBatchWarning(mapOf("stage" to stage, "message" to message))
                if (response.status == 200) {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                    refreshDashboard()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gửi hàng loạt thất bại: ${e.localizedMessage}") }
            }
        }
    }
}
