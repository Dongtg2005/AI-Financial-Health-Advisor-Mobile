package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "transaction_type_enum", nullable = false)
    private TransactionType type;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "category", length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "entry_method", columnDefinition = "entry_method_enum")
    private EntryMethod entryMethod = EntryMethod.QUICK_ADD;

    @Column(name = "transaction_at", nullable = false)
    private LocalDateTime transactionAt;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // Constructors
    public Transaction() {
    }

    public Transaction(User user, TransactionType type, BigDecimal amount, String category, EntryMethod entryMethod, LocalDateTime transactionAt, Boolean isConfirmed, LocalDateTime confirmedAt) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.entryMethod = entryMethod;
        this.transactionAt = transactionAt;
        this.isConfirmed = isConfirmed;
        this.confirmedAt = confirmedAt;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public EntryMethod getEntryMethod() {
        return entryMethod;
    }

    public void setEntryMethod(EntryMethod entryMethod) {
        this.entryMethod = entryMethod;
    }

    public LocalDateTime getTransactionAt() {
        return transactionAt;
    }

    public void setTransactionAt(LocalDateTime transactionAt) {
        this.transactionAt = transactionAt;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
