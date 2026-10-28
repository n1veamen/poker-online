package com.poker.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private final String nickname;
    private BigDecimal stack;
    private BigDecimal bet;
    private boolean folded;
    private boolean allIn;
    private boolean dealer;
    private boolean active;
    private final List<Card> holeCards = new ArrayList<>();
    private String sessionId;
    private int seatIndex;

    public Player(String nickname, BigDecimal stack, String sessionId, int seatIndex) {
        this.nickname = nickname;
        this.stack = stack;
        this.bet = BigDecimal.ZERO;
        this.sessionId = sessionId;
        this.seatIndex = seatIndex;
        this.active = true;
    }

    public String getNickname() {
        return nickname;
    }

    public BigDecimal getStack() {
        return stack;
    }

    public void setStack(BigDecimal stack) {
        this.stack = stack;
    }

    public BigDecimal getBet() {
        return bet;
    }

    public void setBet(BigDecimal bet) {
        this.bet = bet;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public boolean isDealer() {
        return dealer;
    }

    public void setDealer(boolean dealer) {
        this.dealer = dealer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(List<Card> cards) {
        holeCards.clear();
        holeCards.addAll(cards);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public void addToStack(BigDecimal amount) {
        stack = stack.add(amount);
    }

    public void removeFromStack(BigDecimal amount) {
        stack = stack.subtract(amount);
    }

    public void clearBet() {
        bet = BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return Objects.equals(nickname, player.nickname) && Objects.equals(sessionId, player.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, sessionId);
    }
}
