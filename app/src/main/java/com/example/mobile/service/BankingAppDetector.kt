package com.example.mobile.service

object BankingAppDetector {
    val bankingPackages = mapOf(
        "com.mbmobile" to "MB Bank",
        "vn.com.mbbank.mbanking" to "MB Bank",
        "com.vcb" to "Vietcombank",
        "vn.com.techcombank.bb.app" to "Techcombank",
        "com.vietinbank.ipay" to "VietinBank iPay",
        "com.bidv.smartbanking" to "BIDV SmartBanking",
        "com.vnpay.bidv" to "BIDV SmartBanking",
        "vn.com.vpbank.vpbankonline" to "VPBank NEO",
        "com.sacombank.mbanking" to "Sacombank mBanking",
        "vn.com.tpb.mbanking" to "TPBank Mobile",
        "com.acb.mbanking" to "ACB ONE",
        "com.mservice.momotransfer" to "MoMo PayLater",
        "vn.com.vnpay.wallet" to "VNPAY Wallet",
        "com.vng.inputmethod.uikit" to "ZaloPay",
        "com.shopee.vn" to "SPayLater (Shopee)"
    )

    fun getBankName(packageName: String?): String? {
        if (packageName == null) return null
        return bankingPackages[packageName]
    }

    fun shouldRecordDetection(
        packageName: String,
        currentTimeMs: Long,
        lastDetectedTimes: MutableMap<String, Long>,
        debounceIntervalMs: Long = 5000L
    ): Boolean {
        val lastTime = lastDetectedTimes[packageName] ?: 0L
        if (currentTimeMs - lastTime >= debounceIntervalMs) {
            lastDetectedTimes[packageName] = currentTimeMs
            return true
        }
        return false
    }
}
