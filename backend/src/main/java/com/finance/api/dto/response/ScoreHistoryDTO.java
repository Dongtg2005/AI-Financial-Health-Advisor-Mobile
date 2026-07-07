package com.finance.api.dto.response;

import java.time.LocalDate;

public class ScoreHistoryDTO {
    private String id;
    private LocalDate weekStartDate;
    private int healthScore;
    private int spendingScore;
    private int debtScore;
    private int savingScore;
    private int awarenessScore;
    private String debtMode;
    private int progressScore;
    private String insights;

    public ScoreHistoryDTO() {}

    public ScoreHistoryDTO(String id, LocalDate weekStartDate, int healthScore, int spendingScore, int debtScore, int savingScore, int awarenessScore, String debtMode, int progressScore, String insights) {
        this.id = id;
        this.weekStartDate = weekStartDate;
        this.healthScore = healthScore;
        this.spendingScore = spendingScore;
        this.debtScore = debtScore;
        this.savingScore = savingScore;
        this.awarenessScore = awarenessScore;
        this.debtMode = debtMode;
        this.progressScore = progressScore;
        this.insights = insights;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public int getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(int healthScore) {
        this.healthScore = healthScore;
    }

    public int getSpendingScore() {
        return spendingScore;
    }

    public void setSpendingScore(int spendingScore) {
        this.spendingScore = spendingScore;
    }

    public int getDebtScore() {
        return debtScore;
    }

    public void setDebtScore(int debtScore) {
        this.debtScore = debtScore;
    }

    public int getSavingScore() {
        return savingScore;
    }

    public void setSavingScore(int savingScore) {
        this.savingScore = savingScore;
    }

    public int getAwarenessScore() {
        return awarenessScore;
    }

    public void setAwarenessScore(int awarenessScore) {
        this.awarenessScore = awarenessScore;
    }

    public String getDebtMode() {
        return debtMode;
    }

    public void setDebtMode(String debtMode) {
        this.debtMode = debtMode;
    }

    public int getProgressScore() {
        return progressScore;
    }

    public void setProgressScore(int progressScore) {
        this.progressScore = progressScore;
    }

    public String getInsights() {
        return insights;
    }

    public void setInsights(String insights) {
        this.insights = insights;
    }
}
