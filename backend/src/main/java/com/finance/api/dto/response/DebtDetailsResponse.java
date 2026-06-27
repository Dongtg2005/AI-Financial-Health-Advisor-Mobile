package com.finance.api.dto.response;

import com.finance.api.entity.Debt;
import com.finance.api.entity.DebtType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class DebtDetailsResponse {
    private UUID id;
    private DebtType type;
    private BigDecimal balance;
    private BigDecimal minimumPayment;
    private LocalDate dueDate;
    private Long daysRemaining;
    private Long overdueDays;
    private LocalDate overdueSince;
    private Boolean isActive;

    public DebtDetailsResponse() {}

    public static DebtDetailsResponse fromEntity(Debt entity, LocalDate now) {
        DebtDetailsResponse dto = new DebtDetailsResponse();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setBalance(entity.getBalance());
        dto.setMinimumPayment(entity.getMinimumPayment());
        dto.setDueDate(entity.getDueDate());
        dto.setOverdueSince(entity.getOverdueSince());
        dto.setIsActive(entity.getIsActive());

        // Tính toán số ngày còn lại hoặc trễ hạn dựa trên múi giờ Việt Nam
        if (entity.getIsActive()) {
            if (entity.getOverdueSince() != null || entity.getDueDate().isBefore(now)) {
                dto.setDaysRemaining(0L);
                LocalDate startOverdue = entity.getOverdueSince() != null ? entity.getOverdueSince() : entity.getDueDate();
                dto.setOverdueDays(Math.max(0, ChronoUnit.DAYS.between(startOverdue, now)));
            } else {
                dto.setDaysRemaining(Math.max(0L, ChronoUnit.DAYS.between(now, entity.getDueDate())));
                dto.setOverdueDays(0L);
            }
        } else {
            dto.setDaysRemaining(0L);
            dto.setOverdueDays(0L);
        }

        return dto;
    }

    // --- GETTERS & SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public DebtType getType() { return type; }
    public void setType(DebtType type) { this.type = type; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getMinimumPayment() { return minimumPayment; }
    public void setMinimumPayment(BigDecimal minimumPayment) { this.minimumPayment = minimumPayment; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Long daysRemaining) { this.daysRemaining = daysRemaining; }

    public Long getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Long overdueDays) { this.overdueDays = overdueDays; }

    public LocalDate getOverdueSince() { return overdueSince; }
    public void setOverdueSince(LocalDate overdueSince) { this.overdueSince = overdueSince; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
