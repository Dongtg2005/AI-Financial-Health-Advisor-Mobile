package com.finance.api.dto.response;

public class AdminAnalyticsResponseDTO {
    private long totalUsers;
    private double averageHealthScore;
    private long activeUsersCount;
    private long debtRepaymentCount;
    private long emergencyFundCount;

    public AdminAnalyticsResponseDTO() {}

    public AdminAnalyticsResponseDTO(long totalUsers, double averageHealthScore, long activeUsersCount, long debtRepaymentCount, long emergencyFundCount) {
        this.totalUsers = totalUsers;
        this.averageHealthScore = averageHealthScore;
        this.activeUsersCount = activeUsersCount;
        this.debtRepaymentCount = debtRepaymentCount;
        this.emergencyFundCount = emergencyFundCount;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public double getAverageHealthScore() { return averageHealthScore; }
    public void setAverageHealthScore(double averageHealthScore) { this.averageHealthScore = averageHealthScore; }

    public long getActiveUsersCount() { return activeUsersCount; }
    public void setActiveUsersCount(long activeUsersCount) { this.activeUsersCount = activeUsersCount; }

    public long getDebtRepaymentCount() { return debtRepaymentCount; }
    public void setDebtRepaymentCount(long debtRepaymentCount) { this.debtRepaymentCount = debtRepaymentCount; }

    public long getEmergencyFundCount() { return emergencyFundCount; }
    public void setEmergencyFundCount(long emergencyFundCount) { this.emergencyFundCount = emergencyFundCount; }
}
