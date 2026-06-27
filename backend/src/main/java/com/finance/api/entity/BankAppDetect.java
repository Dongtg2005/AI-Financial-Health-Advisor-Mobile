package com.finance.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_app_detects")
public class BankAppDetect {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "app_package_name", nullable = false, length = 100)
    private String appPackageName;

    @Column(name = "bank_name", nullable = false, length = 50) 
    private String bankName; // Lưu tên hiển thị: "Vietcombank", "MBBank", "TPBank" để UI dựng timeline chuẩn

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "is_processed", nullable = false)
    private boolean processed = false;

    // Constructors
    public BankAppDetect() {}

    public BankAppDetect(User user, String appPackageName, String bankName, LocalDateTime detectedAt, boolean processed) {
        this.user = user;
        this.appPackageName = appPackageName;
        this.bankName = bankName;
        this.detectedAt = detectedAt;
        this.processed = processed;
    }

    // --- MANUAL GETTERS & SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAppPackageName() { return appPackageName; }
    public void setAppPackageName(String appPackageName) { this.appPackageName = appPackageName; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
