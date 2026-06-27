package com.finance.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
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
        return Collections.emptyList();
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
        return true;
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
}
