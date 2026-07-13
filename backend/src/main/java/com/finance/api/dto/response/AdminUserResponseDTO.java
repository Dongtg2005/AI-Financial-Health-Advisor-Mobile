package com.finance.api.dto.response;

import com.finance.api.entity.FinancialStage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AdminUserResponseDTO {
    private UUID id;
    private String username;
    private String role;
    private FinancialStage financialStage;
    private BigDecimal monthlyIncome;
    private BigDecimal suggestedBudget;
    private int healthScore;
    private LocalDateTime createdAt;
    private boolean isEnabled;

    public AdminUserResponseDTO() {}

    public AdminUserResponseDTO(UUID id, String username, String role, FinancialStage financialStage, BigDecimal monthlyIncome, BigDecimal suggestedBudget, int healthScore, LocalDateTime createdAt, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.financialStage = financialStage;
        this.monthlyIncome = monthlyIncome;
        this.suggestedBudget = suggestedBudget;
        this.healthScore = healthScore;
        this.createdAt = createdAt;
        this.isEnabled = isEnabled;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public FinancialStage getFinancialStage() { return financialStage; }
    public void setFinancialStage(FinancialStage financialStage) { this.financialStage = financialStage; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public BigDecimal getSuggestedBudget() { return suggestedBudget; }
    public void setSuggestedBudget(BigDecimal suggestedBudget) { this.suggestedBudget = suggestedBudget; }

    public int getHealthScore() { return healthScore; }
    public void setHealthScore(int healthScore) { this.healthScore = healthScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
}
