package com.finance.api.dto.response;

import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponseDTO {

    private UUID id;
    private UUID userId; // Chỉ trả về ID, giấu toàn bộ thông tin nhạy cảm của User
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private String entryMethod;
    private LocalDateTime transactionAt;
    private Boolean isConfirmed;
    private LocalDateTime confirmedAt;

    public TransactionResponseDTO() {}

    // Hàm tiện ích để chuyển đổi từ Entity sang DTO ngay lập tức
    public static TransactionResponseDTO fromEntity(Transaction entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setType(entity.getType());
        dto.setAmount(entity.getAmount());
        dto.setCategory(entity.getCategory());
        dto.setEntryMethod(entity.getEntryMethod() != null ? entity.getEntryMethod().name() : null);
        dto.setTransactionAt(entity.getTransactionAt());
        dto.setIsConfirmed(entity.getIsConfirmed());
        dto.setConfirmedAt(entity.getConfirmedAt());
        return dto;
    }

    // --- GETTERS & SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getEntryMethod() { return entryMethod; }
    public void setEntryMethod(String entryMethod) { this.entryMethod = entryMethod; }

    public LocalDateTime getTransactionAt() { return transactionAt; }
    public void setTransactionAt(LocalDateTime transactionAt) { this.transactionAt = transactionAt; }

    public Boolean getIsConfirmed() { return isConfirmed; }
    public void setIsConfirmed(Boolean isConfirmed) { this.isConfirmed = isConfirmed; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
}
