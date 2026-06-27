package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_scores")
public class FinancialScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "health_score")
    private Integer healthScore;

    @Column(name = "spending_score")
    private Integer spendingScore;

    @Column(name = "debt_score")
    private Integer debtScore;

    @Column(name = "saving_score")
    private Integer savingScore;

    @Column(name = "awareness_score")
    private Integer awarenessScore;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "debt_mode", columnDefinition = "debt_mode_enum")
    private DebtMode debtMode;

    @Column(name = "progress_score")
    private Integer progressScore;

    @Column(name = "insights", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String insights = "[]";

    // Constructors
    public FinancialScore() {
    }

    public FinancialScore(User user, LocalDate weekStartDate, Integer healthScore, Integer spendingScore, Integer debtScore, Integer savingScore, Integer awarenessScore, DebtMode debtMode, Integer progressScore, String insights) {
        this.user = user;
        this.weekStartDate = weekStartDate;
        this.healthScore = healthScore;
        this.spendingScore = spendingScore;
        this.debtScore = debtScore;
        this.savingScore = savingScore;
        this.awarenessScore = awarenessScore;
        this.debtMode = debtMode;
        this.progressScore = progressScore;
        this.insights = insights;
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

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public Integer getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }

    public Integer getSpendingScore() {
        return spendingScore;
    }

    public void setSpendingScore(Integer spendingScore) {
        this.spendingScore = spendingScore;
    }

    public Integer getDebtScore() {
        return debtScore;
    }

    public void setDebtScore(Integer debtScore) {
        this.debtScore = debtScore;
    }

    public Integer getSavingScore() {
        return savingScore;
    }

    public void setSavingScore(Integer savingScore) {
        this.savingScore = savingScore;
    }

    public Integer getAwarenessScore() {
        return awarenessScore;
    }

    public void setAwarenessScore(Integer awarenessScore) {
        this.awarenessScore = awarenessScore;
    }

    public DebtMode getDebtMode() {
        return debtMode;
    }

    public void setDebtMode(DebtMode debtMode) {
        this.debtMode = debtMode;
    }

    public Integer getProgressScore() {
        return progressScore;
    }

    public void setProgressScore(Integer progressScore) {
        this.progressScore = progressScore;
    }

    public String getInsights() {
        return insights;
    }

    public void setInsights(String insights) {
        this.insights = insights;
    }
}
