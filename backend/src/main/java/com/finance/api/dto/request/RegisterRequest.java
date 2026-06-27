package com.finance.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class RegisterRequest {

    private String username;
    private String password;

    @JsonProperty("monthly_income")
    private BigDecimal monthlyIncome;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, BigDecimal monthlyIncome) {
        this.username = username;
        this.password = password;
        this.monthlyIncome = monthlyIncome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
}
