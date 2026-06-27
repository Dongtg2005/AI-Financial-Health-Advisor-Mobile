package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "debts")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "debt_type_enum")
    private DebtType type;

    @Column(name = "balance", columnDefinition = "bytea", nullable = false)
    @ColumnTransformer(
        read = "pgp_sym_decrypt(balance, 'FINANCE_SECRET_KEY')::numeric",
        write = "pgp_sym_encrypt(cast(? as text), 'FINANCE_SECRET_KEY')"
    )
    private BigDecimal balance;

    @Column(name = "minimum_payment", precision = 15, scale = 2)
    private BigDecimal minimumPayment;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "overdue_since")
    private LocalDate overdueSince;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public Debt() {
    }

    public Debt(User user, DebtType type, BigDecimal balance, BigDecimal minimumPayment, LocalDate dueDate, LocalDate overdueSince, Boolean isActive) {
        this.user = user;
        this.type = type;
        this.balance = balance;
        this.minimumPayment = minimumPayment;
        this.dueDate = dueDate;
        this.overdueSince = overdueSince;
        this.isActive = isActive;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DebtType getType() {
        return type;
    }

    public void setType(DebtType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getOverdueSince() {
        return overdueSince;
    }

    public void setOverdueSince(LocalDate overdueSince) {
        this.overdueSince = overdueSince;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
