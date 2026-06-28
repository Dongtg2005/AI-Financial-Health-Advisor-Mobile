package com.finance.api.dto.request;

import java.math.BigDecimal;

public class OnboardingRequest {
    private BigDecimal monthlyIncome;
    private BigDecimal suggestedBudget;

    public OnboardingRequest() {
    }

    public OnboardingRequest(BigDecimal monthlyIncome, BigDecimal suggestedBudget) {
        this.monthlyIncome = monthlyIncome;
        this.suggestedBudget = suggestedBudget;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getSuggestedBudget() {
        return suggestedBudget;
    }

    public void setSuggestedBudget(BigDecimal suggestedBudget) {
        this.suggestedBudget = suggestedBudget;
    }
}
