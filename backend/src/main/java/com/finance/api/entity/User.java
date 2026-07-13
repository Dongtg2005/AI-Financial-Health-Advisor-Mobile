package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "financial_stage", columnDefinition = "financial_stage_enum")
    private FinancialStage financialStage = FinancialStage.DEBT_REPAYMENT;

    @Column(name = "monthly_income", columnDefinition = "bytea")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(monthly_income, 'FINANCE_SECRET_KEY')::numeric",
        write = "pgp_sym_encrypt(cast(? as text), 'FINANCE_SECRET_KEY')"
    )
    private BigDecimal monthlyIncome;

    @Column(name = "debt_free_since")
    private LocalDate debtFreeSince;

    @Column(name = "notification_time", length = 5)
    private String notificationTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "suggested_budget")
    private BigDecimal suggestedBudget;

    @Column(name = "health_score")
    private int healthScore = 100;

    @Column(name = "score_spending")
    private int scoreSpending = 35;

    @Column(name = "score_debt")
    private int scoreDebt = 35;

    @Column(name = "score_saving")
    private int scoreSaving = 20;

    @Column(name = "score_awareness")
    private int scoreAwareness = 10;

    @Column(name = "role", length = 20)
    private String role = "USER";

    @Column(name = "is_enabled")
    private boolean isEnabled = true;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "daily_notif_enabled")
    private boolean dailyNotifEnabled = true;

    @Column(name = "ai_alerts_enabled")
    private boolean aiAlertsEnabled = true;

    @Column(name = "billing_cycle_day")
    private int billingCycleDay = 5;

    // 🔒 CHƯƠNG 3 - KHÓA LẠC QUAN: Chống tranh chấp dữ liệu dòng tiền toàn cục
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    // Getters and Setters for settings fields
    public boolean isDailyNotifEnabled() {
        return dailyNotifEnabled;
    }

    public void setDailyNotifEnabled(boolean dailyNotifEnabled) {
        this.dailyNotifEnabled = dailyNotifEnabled;
    }

    public boolean isAiAlertsEnabled() {
        return aiAlertsEnabled;
    }

    public void setAiAlertsEnabled(boolean aiAlertsEnabled) {
        this.aiAlertsEnabled = aiAlertsEnabled;
    }

    public int getBillingCycleDay() {
        return billingCycleDay;
    }

    public void setBillingCycleDay(int billingCycleDay) {
        this.billingCycleDay = billingCycleDay;
    }

    // Constructors
    public User() {
    }

    public User(String username, String password, FinancialStage financialStage, BigDecimal monthlyIncome, LocalDate debtFreeSince, String notificationTime, LocalDateTime createdAt) {
        this.username = username;
        this.password = password;
        this.financialStage = financialStage;
        this.monthlyIncome = monthlyIncome;
        this.debtFreeSince = debtFreeSince;
        this.notificationTime = notificationTime;
        this.createdAt = createdAt;
    }

    // UserDetails Override Methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FinancialStage getFinancialStage() {
        return financialStage;
    }

    public void setFinancialStage(FinancialStage financialStage) {
        this.financialStage = financialStage;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public LocalDate getDebtFreeSince() {
        return debtFreeSince;
    }

    public void setDebtFreeSince(LocalDate debtFreeSince) {
        this.debtFreeSince = debtFreeSince;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getSuggestedBudget() {
        return suggestedBudget;
    }

    public void setSuggestedBudget(BigDecimal suggestedBudget) {
        this.suggestedBudget = suggestedBudget;
    }

    public int getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(int healthScore) {
        this.healthScore = healthScore;
    }

    public int getScoreSpending() {
        return scoreSpending;
    }

    public void setScoreSpending(int scoreSpending) {
        this.scoreSpending = scoreSpending;
    }

    public int getScoreDebt() {
        return scoreDebt;
    }

    public void setScoreDebt(int scoreDebt) {
        this.scoreDebt = scoreDebt;
    }

    public int getScoreSaving() {
        return scoreSaving;
    }

    public void setScoreSaving(int scoreSaving) {
        this.scoreSaving = scoreSaving;
    }

    public int getScoreAwareness() {
        return scoreAwareness;
    }

    public void setScoreAwareness(int scoreAwareness) {
        this.scoreAwareness = scoreAwareness;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
