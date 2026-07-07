package com.example.mobile.ui.batch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.common.AppEvent
import com.example.mobile.common.AppEventBus
import com.example.mobile.data.local.AppDatabase
import com.example.mobile.data.local.entity.DetectEntity
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.TransactionApiService
import com.example.mobile.data.network.dto.TransactionRequestDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BatchReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val detectDao = AppDatabase.getDatabase(application).detectDao()
    private val transactionApiService = NetworkModule.createService(
        application, TransactionApiService::class.java
    )

    private val _unprocessedList = MutableStateFlow<List<DetectEntity>>(emptyList())
    val unprocessedList: StateFlow<List<DetectEntity>> = _unprocessedList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _confirmError = MutableStateFlow<String?>(null)
    val confirmError: StateFlow<String?> = _confirmError.asStateFlow()

    init {
        loadUnprocessedDetects()
    }

    fun loadUnprocessedDetects() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                detectDao.getUnprocessedDetectsFlow().collect { list ->
                    _unprocessedList.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    /**
     * Xác nhận giao dịch: Gọi API tạo Transaction thật, sau đó đánh dấu Detect đã xử lý.
     */
    fun confirmDetect(detect: DetectEntity, category: String, amount: Double, note: String) {
        viewModelScope.launch {
            _confirmError.value = null
            try {
                // 1. Gọi API Backend để lưu giao dịch thật
                val request = TransactionRequestDTO(
                    amount = BigDecimal.valueOf(amount),
                    category = category.lowercase(),
                    type = "EXPENSE",    // Bank detect luôn là chi tiêu
                    transactionAt = null // Server tự lấy giờ hiện tại VN
                )
                transactionApiService.createTransaction(request)

                // 2. Đánh dấu đã xử lý trong Room DB
                detectDao.markAsProcessed(listOf(detect.id))

                // 3. Phát sự kiện toàn cục để Dashboard tự reload điểm sức khỏe
                AppEventBus.emit(AppEvent.TransactionCreated)

            } catch (e: Exception) {
                e.printStackTrace()
                _confirmError.value = "Lỗi kết nối: ${e.localizedMessage ?: "Không thể lưu giao dịch"}"
                // Fallback: vẫn đánh dấu local để không bị mắc kẹt ở màn hình này
                try { detectDao.markAsProcessed(listOf(detect.id)) } catch (_: Exception) {}
            }
        }
    }

    fun skipDetect(detect: DetectEntity) {
        viewModelScope.launch {
            try {
                detectDao.markAsProcessed(listOf(detect.id))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun skipAll() {
        viewModelScope.launch {
            try {
                val ids = _unprocessedList.value.map { it.id }
                if (ids.isNotEmpty()) {
                    detectDao.markAsProcessed(ids)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _confirmError.value = null
    }
}
