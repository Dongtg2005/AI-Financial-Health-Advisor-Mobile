package com.finance.api.dto.response;

import java.util.List;

public class DashboardResponse {
    private int status;
    private String message;
    private DashboardData data;

    public DashboardResponse() {}

    public DashboardResponse(int status, String message, DashboardData data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DashboardData getData() { return data; }
    public void setData(DashboardData data) { this.data = data; }

    public static class DashboardData {
        private String userName;
        private int healthScore;
        private int scoreSpending;
        private int scoreDebt;
        private int scoreSaving;
        private int scoreAwareness;
        private long totalSpent;
        private long totalBudget;
        private List<DebtAlertDTO> alerts;
        private List<BudgetCategoryUiDTO> budgetCategories;
        private String adminNote;

        public DashboardData() {}

        public DashboardData(String userName, int healthScore, int scoreSpending, int scoreDebt, int scoreSaving, int scoreAwareness, long totalSpent, long totalBudget, List<DebtAlertDTO> alerts, List<BudgetCategoryUiDTO> budgetCategories, String adminNote) {
            this.userName = userName;
            this.healthScore = healthScore;
            this.scoreSpending = scoreSpending;
            this.scoreDebt = scoreDebt;
            this.scoreSaving = scoreSaving;
            this.scoreAwareness = scoreAwareness;
            this.totalSpent = totalSpent;
            this.totalBudget = totalBudget;
            this.alerts = alerts;
            this.budgetCategories = budgetCategories;
            this.adminNote = adminNote;
        }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

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

        public long getTotalSpent() { return totalSpent; }
        public void setTotalSpent(long totalSpent) { this.totalSpent = totalSpent; }

        public long getTotalBudget() { return totalBudget; }
        public void setTotalBudget(long totalBudget) { this.totalBudget = totalBudget; }

        public List<DebtAlertDTO> getAlerts() { return alerts; }
        public void setAlerts(List<DebtAlertDTO> alerts) { this.alerts = alerts; }

        public List<BudgetCategoryUiDTO> getBudgetCategories() { return budgetCategories; }
        public void setBudgetCategories(List<BudgetCategoryUiDTO> budgetCategories) { this.budgetCategories = budgetCategories; }

        public String getAdminNote() { return adminNote; }
        public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    }

    public static class BudgetCategoryUiDTO {
        private String name;
        private long spent;
        private long limit;

        public BudgetCategoryUiDTO() {}

        public BudgetCategoryUiDTO(String name, long spent, long limit) {
            this.name = name;
            this.spent = spent;
            this.limit = limit;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public long getSpent() { return spent; }
        public void setSpent(long spent) { this.spent = spent; }

        public long getLimit() { return limit; }
        public void setLimit(long limit) { this.limit = limit; }
    }
}
