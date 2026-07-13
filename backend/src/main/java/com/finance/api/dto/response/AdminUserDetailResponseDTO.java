package com.finance.api.dto.response;

import java.util.Map;
import java.util.UUID;

public class AdminUserDetailResponseDTO {
    private UUID userId;
    private String username;
    private int healthScore;
    private int scoreSpending;
    private int scoreDebt;
    private int scoreSaving;
    private int scoreAwareness;
    private Map<String, Double> categoryPercentages;

    public AdminUserDetailResponseDTO() {}

    public AdminUserDetailResponseDTO(UUID userId, String username, int healthScore, int scoreSpending, int scoreDebt, int scoreSaving, int scoreAwareness, Map<String, Double> categoryPercentages) {
        this.userId = userId;
        this.username = username;
        this.healthScore = healthScore;
        this.scoreSpending = scoreSpending;
        this.scoreDebt = scoreDebt;
        this.scoreSaving = scoreSaving;
        this.scoreAwareness = scoreAwareness;
        this.categoryPercentages = categoryPercentages;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getHealthScore() { return healthScore; }
    public void setHealthScore(int healthScore) { this.healthScore = healthScore; }

    public int getScoreSpending() { return scoreSpending; }
    public void setScoreSpending(int scoreSpending) { this.scoreSpending = scoreSpending; }

    public int getScoreDebt() { return scoreDebt; }
    public void setScoreDebt(int scoreDebt) { this.scoreDebt = scoreDebt; }

    public int getScoreSaving() { return scoreSaving; }
    public void setScoreSaving(int scoreSaving) { this.scoreSaving = scoreSaving; }

    public int getScoreAwareness() { return scoreAwareness; }
    public void setScoreAwareness(int scoreAwareness) { this.scoreAwareness = scoreAwareness; }

    public Map<String, Double> getCategoryPercentages() { return categoryPercentages; }
    public void setCategoryPercentages(Map<String, Double> categoryPercentages) { this.categoryPercentages = categoryPercentages; }
}
