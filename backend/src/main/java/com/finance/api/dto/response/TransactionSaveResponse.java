package com.finance.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionSaveResponse {
    private int status;
    private String message;
    private TransactionData data;
    private MicroInsightDTO microInsight;

    public TransactionSaveResponse(int status, String message, TransactionData data, MicroInsightDTO microInsight) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.microInsight = microInsight;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public TransactionData getData() { return data; }
    public void setData(TransactionData data) { this.data = data; }

    public MicroInsightDTO getMicroInsight() { return microInsight; }
    public void setMicroInsight(MicroInsightDTO microInsight) { this.microInsight = microInsight; }

    public static class TransactionData {
        private UUID transactionId;
        private BigDecimal amount;
        private String category;

        public TransactionData(UUID transactionId, BigDecimal amount, String category) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.category = category;
        }

        public UUID getTransactionId() { return transactionId; }
        public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class MicroInsightDTO {
        private boolean shouldShow;
        private String type;
        private String title;
        private double todayTotalAmount;
        private double projectedMonthlyAmount;
        private String message;
        private String tone;

        public boolean isShouldShow() { return shouldShow; }
        public void setShouldShow(boolean shouldShow) { this.shouldShow = shouldShow; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public double getTodayTotalAmount() { return todayTotalAmount; }
        public void setTodayTotalAmount(double todayTotalAmount) { this.todayTotalAmount = todayTotalAmount; }
        public double getProjectedMonthlyAmount() { return projectedMonthlyAmount; }
        public void setProjectedMonthlyAmount(double projectedMonthlyAmount) { this.projectedMonthlyAmount = projectedMonthlyAmount; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTone() { return tone; }
        public void setTone(String tone) { this.tone = tone; }
    }
}
