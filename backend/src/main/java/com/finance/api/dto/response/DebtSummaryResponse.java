package com.finance.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DebtSummaryResponse {
    private BigDecimal totalActiveDebt;
    private Integer overdueCount;
    private Integer upcomingCount;
    private List<DebtAlertDTO> alerts;
    private List<DebtDetailsResponse> debts;

    public DebtSummaryResponse() {}

    public DebtSummaryResponse(BigDecimal totalActiveDebt, Integer overdueCount, Integer upcomingCount, List<DebtAlertDTO> alerts, List<DebtDetailsResponse> debts) {
        this.totalActiveDebt = totalActiveDebt;
        this.overdueCount = overdueCount;
        this.upcomingCount = upcomingCount;
        this.alerts = alerts;
        this.debts = debts;
    }

    // --- GETTERS & SETTERS ---
    public BigDecimal getTotalActiveDebt() {
        return totalActiveDebt;
    }

    public void setTotalActiveDebt(BigDecimal totalActiveDebt) {
        this.totalActiveDebt = totalActiveDebt;
    }

    public Integer getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(Integer overdueCount) {
        this.overdueCount = overdueCount;
    }

    public Integer getUpcomingCount() {
        return upcomingCount;
    }

    public void setUpcomingCount(Integer upcomingCount) {
        this.upcomingCount = upcomingCount;
    }

    public List<DebtAlertDTO> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<DebtAlertDTO> alerts) {
        this.alerts = alerts;
    }

    public List<DebtDetailsResponse> getDebts() {
        return debts;
    }

    public void setDebts(List<DebtDetailsResponse> debts) {
        this.debts = debts;
    }
}
