package com.finance.api.dto.request;

import com.finance.api.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRequestDTO {
    
    private UUID userId; 
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private LocalDateTime transactionAt;

    // --- CONSTRUCTORS ---
    public TransactionRequestDTO() {}

    public TransactionRequestDTO(UUID userId, TransactionType type, BigDecimal amount, String category, LocalDateTime transactionAt) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.transactionAt = transactionAt;
    }

    // --- GETTERS & SETTERS ---
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getTransactionAt() { return transactionAt; }
    public void setTransactionAt(LocalDateTime transactionAt) { this.transactionAt = transactionAt; }
}
