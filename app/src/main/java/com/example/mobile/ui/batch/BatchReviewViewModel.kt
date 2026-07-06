package com.example.mobile.ui.batch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.AppDatabase
import com.example.mobile.data.local.entity.DetectEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatchReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val detectDao = AppDatabase.getDatabase(application).detectDao()

    private val _unprocessedList = MutableStateFlow<List<DetectEntity>>(emptyList())
    val unprocessedList: StateFlow<List<DetectEntity>> = _unprocessedList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun confirmDetect(detect: DetectEntity, category: String, amount: Double, note: String) {
        viewModelScope.launch {
            try {
                // Đánh dấu giao dịch nhận diện đã xử lý
                detectDao.markAsProcessed(listOf(detect.id))
                // (Ở phiên bản tích hợp đầy đủ, sẽ lưu thêm vào TransactionDao/API)
            } catch (e: Exception) {
                e.printStackTrace()
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
}
