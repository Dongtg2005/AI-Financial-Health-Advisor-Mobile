package com.finance.api.dto.request;

public class UpdateSettingsRequest {
    private boolean dailyNotifEnabled;
    private boolean aiAlertsEnabled;
    private int billingCycleDay;

    public UpdateSettingsRequest() {}

    public UpdateSettingsRequest(boolean dailyNotifEnabled, boolean aiAlertsEnabled, int billingCycleDay) {
        this.dailyNotifEnabled = dailyNotifEnabled;
        this.aiAlertsEnabled = aiAlertsEnabled;
        this.billingCycleDay = billingCycleDay;
    }

    public boolean isDailyNotifEnabled() { return dailyNotifEnabled; }
    public void setDailyNotifEnabled(boolean dailyNotifEnabled) { this.dailyNotifEnabled = dailyNotifEnabled; }

    public boolean isAiAlertsEnabled() { return aiAlertsEnabled; }
    public void setAiAlertsEnabled(boolean aiAlertsEnabled) { this.aiAlertsEnabled = aiAlertsEnabled; }

    public int getBillingCycleDay() { return billingCycleDay; }
    public void setBillingCycleDay(int billingCycleDay) { this.billingCycleDay = billingCycleDay; }
}
