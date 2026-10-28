package com.poker.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Action {
    private final String playerNickname;
    private final Street street;
    private final ActionType actionType;
    private final BigDecimal amount;
    private final Instant createdAt;

    public Action(String playerNickname, Street street, ActionType actionType, BigDecimal amount, Instant createdAt) {
        this.playerNickname = playerNickname;
        this.street = street;
        this.actionType = actionType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public Street getStreet() {
        return street;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
