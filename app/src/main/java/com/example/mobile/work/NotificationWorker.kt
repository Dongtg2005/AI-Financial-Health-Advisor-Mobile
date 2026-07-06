package com.example.mobile.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobile.MainActivity
import com.example.mobile.data.local.AppDatabase

class NotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "ai_financial_advisor_batch"
        const val CHANNEL_NAME = "Xác nhận giao dịch hàng ngày"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_OPEN_BATCH_REVIEW = "open_batch_review"
    }

    override suspend fun doWork(): Result {
        try {
            val detectDao = AppDatabase.getDatabase(appContext).detectDao()
            val pendingList = detectDao.getUnprocessedDetects()

            // Nếu có giao dịch phát hiện chờ duyệt, hoặc chạy theo giờ nhắc nhở định kỳ
            val count = pendingList.size
            val title = "AI Financial Advisor - Nhắc nhở tài chính"
            val message = if (count > 0) {
                "Bạn có $count hoạt động ngân hàng mới được ghi nhận. Chạm để kiểm tra và phân loại ngay!"
            } else {
                "Đã đến giờ tổng kết tài chính! Hãy kiểm tra chi tiêu và cập nhật điểm sức khỏe hôm nay."
            }

            sendNotification(title, message)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Nhắc nhở phân loại chi tiêu và rà soát các khoản nợ"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_OPEN_BATCH_REVIEW, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Sử dụng icon hệ thống an toàn
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
