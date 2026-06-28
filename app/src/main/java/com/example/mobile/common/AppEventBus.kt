package com.example.mobile.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Định nghĩa các loại sự kiện tài chính toàn cục
sealed class AppEvent {
    object OnboardingCompleted : AppEvent()
    object TransactionCreated : AppEvent()
}

object AppEventBus {
    // Sử dụng SharedFlow để phát tín hiệu đến nhiều ViewModel cùng lúc
    private val _events = MutableSharedFlow<AppEvent>(replay = 0)
    val events = _events.asSharedFlow()

    suspend fun emit(event: AppEvent) {
        _events.emit(event)
    }
}
