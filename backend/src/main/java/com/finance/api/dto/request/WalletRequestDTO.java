package com.finance.api.dto.request;

import java.math.BigDecimal;

public class WalletRequestDTO {
    private String name;
    private BigDecimal balance;

    public WalletRequestDTO() {}

    public WalletRequestDTO(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
