package com.poker.dto;

import java.math.BigDecimal;
import java.util.List;

public class PlayerDto {
    private String nickname;
    private BigDecimal stack;
    private BigDecimal bet;
    private boolean folded;
    private boolean allIn;
    private boolean dealer;
    private boolean active;
    private int seatIndex;
    private List<String> holeCards;

    public PlayerDto() {
    }

    public PlayerDto(String nickname, BigDecimal stack, BigDecimal bet, boolean folded, boolean allIn,
                     boolean dealer, boolean active, int seatIndex, List<String> holeCards) {
        this.nickname = nickname;
        this.stack = stack;
        this.bet = bet;
        this.folded = folded;
        this.allIn = allIn;
        this.dealer = dealer;
        this.active = active;
        this.seatIndex = seatIndex;
        this.holeCards = holeCards;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public BigDecimal getStack() { return stack; }
    public void setStack(BigDecimal stack) { this.stack = stack; }

    public BigDecimal getBet() { return bet; }
    public void setBet(BigDecimal bet) { this.bet = bet; }

    public boolean isFolded() { return folded; }
    public void setFolded(boolean folded) { this.folded = folded; }

    public boolean isAllIn() { return allIn; }
    public void setAllIn(boolean allIn) { this.allIn = allIn; }

    public boolean isDealer() { return dealer; }
    public void setDealer(boolean dealer) { this.dealer = dealer; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getSeatIndex() { return seatIndex; }
    public void setSeatIndex(int seatIndex) { this.seatIndex = seatIndex; }

    public List<String> getHoleCards() { return holeCards; }
    public void setHoleCards(List<String> holeCards) { this.holeCards = holeCards; }
}
