package com.finance.api.dto.request;

import java.math.BigDecimal;

public class CashWeeklyEstimateRequest {
    private BigDecimal totalAmount;
    private String category; // "FOOD", "TRANSPORT", "SHOPPING", "OTHER"

    // --- CONSTRUCTORS ---
    public CashWeeklyEstimateRequest() {}

    public CashWeeklyEstimateRequest(BigDecimal totalAmount, String category) {
        this.totalAmount = totalAmount;
        this.category = category;
    }

    // --- GETTERS & SETTERS ---
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
