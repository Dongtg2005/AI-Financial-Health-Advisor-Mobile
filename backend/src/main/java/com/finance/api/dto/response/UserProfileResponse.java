package com.finance.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserProfileResponse {
    private String username;
    private LocalDateTime createdAt;
    private BigDecimal suggestedBudget;
    private boolean dailyNotifEnabled;
    private boolean aiAlertsEnabled;
    private int billingCycleDay;

    public UserProfileResponse() {}

    public UserProfileResponse(String username, LocalDateTime createdAt, BigDecimal suggestedBudget, boolean dailyNotifEnabled, boolean aiAlertsEnabled, int billingCycleDay) {
        this.username = username;
        this.createdAt = createdAt;
        this.suggestedBudget = suggestedBudget;
        this.dailyNotifEnabled = dailyNotifEnabled;
        this.aiAlertsEnabled = aiAlertsEnabled;
        this.billingCycleDay = billingCycleDay;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getSuggestedBudget() { return suggestedBudget; }
    public void setSuggestedBudget(BigDecimal suggestedBudget) { this.suggestedBudget = suggestedBudget; }

    public boolean isDailyNotifEnabled() { return dailyNotifEnabled; }
    public void setDailyNotifEnabled(boolean dailyNotifEnabled) { this.dailyNotifEnabled = dailyNotifEnabled; }

    public boolean isAiAlertsEnabled() { return aiAlertsEnabled; }
    public void setAiAlertsEnabled(boolean aiAlertsEnabled) { this.aiAlertsEnabled = aiAlertsEnabled; }

    public int getBillingCycleDay() { return billingCycleDay; }
    public void setBillingCycleDay(int billingCycleDay) { this.billingCycleDay = billingCycleDay; }
}
