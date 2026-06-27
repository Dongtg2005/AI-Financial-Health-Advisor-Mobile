package com.finance.api.dto.response;

public class FinancialScoreDTO {
    private int totalScore;
    private int spendingScore;
    private int debtOrReserveScore;
    private int savingScore;
    private int awarenessScore;
    private String currentStage;
    private String penaltyReason;

    public FinancialScoreDTO() {}

    public FinancialScoreDTO(int totalScore, int spendingScore, int debtOrReserveScore, int savingScore, int awarenessScore, String currentStage, String penaltyReason) {
        this.totalScore = totalScore;
        this.spendingScore = spendingScore;
        this.debtOrReserveScore = debtOrReserveScore;
        this.savingScore = savingScore;
        this.awarenessScore = awarenessScore;
        this.currentStage = currentStage;
        this.penaltyReason = penaltyReason;
    }

    // --- GETTERS & SETTERS ---
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getSpendingScore() { return spendingScore; }
    public void setSpendingScore(int spendingScore) { this.spendingScore = spendingScore; }

    public int getDebtOrReserveScore() { return debtOrReserveScore; }
    public void setDebtOrReserveScore(int debtOrReserveScore) { this.debtOrReserveScore = debtOrReserveScore; }

    public int getSavingScore() { return savingScore; }
    public void setSavingScore(int savingScore) { this.savingScore = savingScore; }

    public int getAwarenessScore() { return awarenessScore; }
    public void setAwarenessScore(int awarenessScore) { this.awarenessScore = awarenessScore; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public String getPenaltyReason() { return penaltyReason; }
    public void setPenaltyReason(String penaltyReason) { this.penaltyReason = penaltyReason; }

    // --- MANUAL BUILDER ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalScore;
        private int spendingScore;
        private int debtOrReserveScore;
        private int savingScore;
        private int awarenessScore;
        private String currentStage;
        private String penaltyReason;

        public Builder totalScore(int totalScore) {
            this.totalScore = totalScore;
            return this;
        }

        public Builder spendingScore(int spendingScore) {
            this.spendingScore = spendingScore;
            return this;
        }

        public Builder debtOrReserveScore(int debtOrReserveScore) {
            this.debtOrReserveScore = debtOrReserveScore;
            return this;
        }

        public Builder savingScore(int savingScore) {
            this.savingScore = savingScore;
            return this;
        }

        public Builder awarenessScore(int awarenessScore) {
            this.awarenessScore = awarenessScore;
            return this;
        }

        public Builder currentStage(String currentStage) {
            this.currentStage = currentStage;
            return this;
        }

        public Builder penaltyReason(String penaltyReason) {
            this.penaltyReason = penaltyReason;
            return this;
        }

        public FinancialScoreDTO build() {
            return new FinancialScoreDTO(
                totalScore, spendingScore, debtOrReserveScore, savingScore, awarenessScore, currentStage, penaltyReason
            );
        }
    }
}
