package com.finance.api.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public class BudgetSuggestionResponse {
    private BigDecimal suggestedTotalBudget;
    private String message;
    private Map<String, BigDecimal> categoryDistribution; // Gợi ý chi tiết: food, transport, shopping...

    public BudgetSuggestionResponse() {}

    public BudgetSuggestionResponse(BigDecimal suggestedTotalBudget, String message, Map<String, BigDecimal> categoryDistribution) {
        this.suggestedTotalBudget = suggestedTotalBudget;
        this.message = message;
        this.categoryDistribution = categoryDistribution;
    }

    // --- GETTERS & SETTERS ---
    public BigDecimal getSuggestedTotalBudget() { return suggestedTotalBudget; }
    public void setSuggestedTotalBudget(BigDecimal suggestedTotalBudget) { this.suggestedTotalBudget = suggestedTotalBudget; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, BigDecimal> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, BigDecimal> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
}
