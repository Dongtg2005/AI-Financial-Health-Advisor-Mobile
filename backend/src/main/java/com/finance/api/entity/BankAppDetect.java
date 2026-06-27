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

    @Column(name = "app_name", length = 50)
    private String appName;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "detected_time", nullable = false)
    private LocalDateTime detectedTime;

    @Column(name = "is_processed")
    private Boolean isProcessed = false;

    // Constructors
    public BankAppDetect() {
    }

    public BankAppDetect(User user, String appName, UUID sessionId, LocalDateTime detectedTime, Boolean isProcessed) {
        this.user = user;
        this.appName = appName;
        this.sessionId = sessionId;
        this.detectedTime = detectedTime;
        this.isProcessed = isProcessed;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getDetectedTime() {
        return detectedTime;
    }

    public void setDetectedTime(LocalDateTime detectedTime) {
        this.detectedTime = detectedTime;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }
}
