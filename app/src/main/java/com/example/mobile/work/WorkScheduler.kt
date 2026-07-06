package com.example.mobile.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_NAME_13H = "daily_batch_review_13h"
    private const val WORK_NAME_21H = "daily_batch_review_21h"

    fun scheduleDailyBatchReviews(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Lập lịch cho 13:00 hàng ngày
        scheduleForTime(workManager, WORK_NAME_13H, LocalTime.of(13, 0))

        // Lập lịch cho 21:00 hàng ngày
        scheduleForTime(workManager, WORK_NAME_21H, LocalTime.of(21, 0))
    }

    private fun scheduleForTime(workManager: WorkManager, workName: String, targetTime: LocalTime) {
        val now = LocalDateTime.now()
        var targetDateTime = now.with(targetTime)

        // Nếu giờ mục tiêu hôm nay đã qua, lập lịch cho ngày mai
        if (now.isAfter(targetDateTime) || now.isEqual(targetDateTime)) {
            targetDateTime = targetDateTime.plusDays(1)
        }

        val initialDelayMillis = Duration.between(now, targetDateTime).toMillis()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS // Lặp lại mỗi 24 giờ
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
}
