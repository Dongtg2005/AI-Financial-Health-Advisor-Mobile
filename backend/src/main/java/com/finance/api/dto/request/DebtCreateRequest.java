package com.finance.api.dto.request;

import com.finance.api.entity.DebtType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtCreateRequest {
    private DebtType type;
    private BigDecimal balance;
    private BigDecimal minimumPayment;
    private LocalDate dueDate;

    public DebtCreateRequest() {}

    public DebtCreateRequest(DebtType type, BigDecimal balance, BigDecimal minimumPayment, LocalDate dueDate) {
        this.type = type;
        this.balance = balance;
        this.minimumPayment = minimumPayment;
        this.dueDate = dueDate;
    }

    public DebtType getType() { return type; }
    public void setType(DebtType type) { this.type = type; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getMinimumPayment() { return minimumPayment; }
    public void setMinimumPayment(BigDecimal minimumPayment) { this.minimumPayment = minimumPayment; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
