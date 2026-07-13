package com.finance.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class SpendingTrendResponseDTO {
    private List<TrendItem> monthlyTrends;
    private List<TrendItem> quarterlyTrends;
    private List<TrendItem> yearlyTrends;

    public SpendingTrendResponseDTO() {}

    public SpendingTrendResponseDTO(List<TrendItem> monthlyTrends, List<TrendItem> quarterlyTrends, List<TrendItem> yearlyTrends) {
        this.monthlyTrends = monthlyTrends;
        this.quarterlyTrends = quarterlyTrends;
        this.yearlyTrends = yearlyTrends;
    }

    public List<TrendItem> getMonthlyTrends() { return monthlyTrends; }
    public void setMonthlyTrends(List<TrendItem> monthlyTrends) { this.monthlyTrends = monthlyTrends; }

    public List<TrendItem> getQuarterlyTrends() { return quarterlyTrends; }
    public void setQuarterlyTrends(List<TrendItem> quarterlyTrends) { this.quarterlyTrends = quarterlyTrends; }

    public List<TrendItem> getYearlyTrends() { return yearlyTrends; }
    public void setYearlyTrends(List<TrendItem> yearlyTrends) { this.yearlyTrends = yearlyTrends; }

    public static class TrendItem {
        private String label;
        private BigDecimal amount;

        public TrendItem() {}

        public TrendItem(String label, BigDecimal amount) {
            this.label = label;
            this.amount = amount;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}
