package com.finance.api.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public class AppDetectBatchRequest {
    
    private List<DetectItem> detects;

    public List<DetectItem> getDetects() { return detects; }
    public void setDetects(List<DetectItem> detects) { this.detects = detects; }

    public static class DetectItem {
        private String appPackageName;
        private String bankName; // Nhận từ Mobile gửi lên
        private LocalDateTime detectedAt;

        public String getAppPackageName() { return appPackageName; }
        public void setAppPackageName(String appPackageName) { this.appPackageName = appPackageName; }

        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    }
}
