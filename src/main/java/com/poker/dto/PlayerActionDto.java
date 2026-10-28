package com.poker.dto;

import java.math.BigDecimal;

public class PlayerActionDto {
    private String action;
    private BigDecimal amount;

    public PlayerActionDto() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
