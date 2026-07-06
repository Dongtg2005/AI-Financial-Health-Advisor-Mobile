package com.example.mobile.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.mobile.data.local.AppDatabase
import com.example.mobile.data.local.entity.DetectEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

class AppDetectService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val lastDetectedTimes = ConcurrentHashMap<String, Long>()
    private val debounceIntervalMs = 5000L // Debounce 5 giây theo yêu cầu Task 3.8

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            
            val packageName = event.packageName?.toString() ?: return
            val appName = BankingAppDetector.getBankName(packageName)

            if (appName != null) {
                val currentTime = System.currentTimeMillis()
                if (BankingAppDetector.shouldRecordDetection(packageName, currentTime, lastDetectedTimes, debounceIntervalMs)) {
                    recordDetection(appName, packageName)
                }
            }
        }
    }

    private fun recordDetection(appName: String, packageName: String) {
        serviceScope.launch {
            try {
                val detectDao = AppDatabase.getDatabase(applicationContext).detectDao()
                val now = System.currentTimeMillis()
                
                val entity = DetectEntity(
                    id = "detect_${now}_${packageName.hashCode()}",
                    appPackageName = packageName,
                    bankName = appName,
                    detectedAt = now,
                    isProcessed = false
                )
                
                detectDao.insertDetect(entity)
                Log.i("AppDetectService", "Đã phát hiện ứng dụng ngân hàng: $appName ($packageName)")
            } catch (e: Exception) {
                Log.e("AppDetectService", "Lỗi lưu dữ liệu nhận diện vào Room DB", e)
            }
        }
    }

    override fun onInterrupt() {
        Log.w("AppDetectService", "Accessibility Service bị ngắt kết nối")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
