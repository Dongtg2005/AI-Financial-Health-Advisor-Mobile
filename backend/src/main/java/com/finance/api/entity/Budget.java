package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "target_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "category_limits", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String categoryLimits = "{}";

    @Column(name = "is_suggested")
    private Boolean isSuggested = true;

    // Constructors
    public Budget() {
    }

    public Budget(User user, LocalDate month, BigDecimal targetAmount, String categoryLimits, Boolean isSuggested) {
        this.user = user;
        this.month = month;
        this.targetAmount = targetAmount;
        this.categoryLimits = categoryLimits;
        this.isSuggested = isSuggested;
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

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getCategoryLimits() {
        return categoryLimits;
    }

    public void setCategoryLimits(String categoryLimits) {
        this.categoryLimits = categoryLimits;
    }

    public Boolean getIsSuggested() {
        return isSuggested;
    }

    public void setIsSuggested(Boolean isSuggested) {
        this.isSuggested = isSuggested;
    }
}
