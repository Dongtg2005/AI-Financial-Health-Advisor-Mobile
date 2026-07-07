package com.example.mobile.ui.scores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.network.DebtApiService
import com.example.mobile.data.network.NetworkModule
import com.example.mobile.data.network.dto.ScoreHistoryDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScoreHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val scores: List<ScoreHistoryDTO> = emptyList()
)

class ScoreHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ScoreHistoryUiState())
    val uiState: StateFlow<ScoreHistoryUiState> = _uiState.asStateFlow()

    private val apiService = NetworkModule.createService(application, DebtApiService::class.java)

    init {
        fetchScoreHistory()
    }

    fun fetchScoreHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = apiService.getScoreHistory()
                if (response.status == 200) {
                    _uiState.update { it.copy(isLoading = false, scores = response.data) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Loi ket noi may chu") }
            }
        }
    }
}
