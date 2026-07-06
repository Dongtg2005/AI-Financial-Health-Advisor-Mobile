package com.example.mobile

import com.example.mobile.service.BankingAppDetector
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

/**
 * Unit Test kiểm tra toàn bộ logic nghiệp vụ cốt lõi của EPIC 3:
 * 1. Nhận diện gói ứng dụng ngân hàng Việt Nam (MB, VCB, TCB, v.v.)
 * 2. Cơ chế Debounce 5 giây để chống spam sự kiện khi người dùng thao tác trong app ngân hàng
 */
class Epic3UnitTest {

    @Test
    fun `test BankingAppDetector recognizes Vietnamese banking packages`() {
        assertEquals("MB Bank", BankingAppDetector.getBankName("com.mbmobile"))
        assertEquals("Vietcombank", BankingAppDetector.getBankName("com.vcb"))
        assertEquals("Techcombank", BankingAppDetector.getBankName("vn.com.techcombank.bb.app"))
        assertEquals("MoMo PayLater", BankingAppDetector.getBankName("com.mservice.momotransfer"))
        assertNull(BankingAppDetector.getBankName("com.facebook.katana"))
        assertNull(BankingAppDetector.getBankName("com.android.chrome"))
    }

    @Test
    fun `test BankingAppDetector debounce mechanism prevents continuous recording`() {
        val lastTimes = ConcurrentHashMap<String, Long>()
        val packageName = "com.mbmobile"
        val startTime = 1000000L
        val debounceInterval = 5000L // 5 giây

        // Lần đầu mở app -> Cho phép ghi nhận
        val firstDetection = BankingAppDetector.shouldRecordDetection(
            packageName = packageName,
            currentTimeMs = startTime,
            lastDetectedTimes = lastTimes,
            debounceIntervalMs = debounceInterval
        )
        assertTrue("Lần đầu phát hiện phải được ghi nhận", firstDetection)

        // Lần 2 trong vòng 2 giây (người dùng chuyển màn hình trong app) -> Bị Debounce chặn
        val secondDetection = BankingAppDetector.shouldRecordDetection(
            packageName = packageName,
            currentTimeMs = startTime + 2000L,
            lastDetectedTimes = lastTimes,
            debounceIntervalMs = debounceInterval
        )
        assertFalse("Lần 2 trong vòng 5 giây phải bị debounce chặn", secondDetection)

        // Lần 3 sau 6 giây -> Cho phép ghi nhận lại
        val thirdDetection = BankingAppDetector.shouldRecordDetection(
            packageName = packageName,
            currentTimeMs = startTime + 6000L,
            lastDetectedTimes = lastTimes,
            debounceIntervalMs = debounceInterval
        )
        assertTrue("Lần 3 sau 5 giây phải được phép ghi nhận lại", thirdDetection)
    }
}
