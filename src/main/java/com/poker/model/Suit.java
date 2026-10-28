package com.poker.model;

public enum Suit {
    CLUBS("♣", "C"),
    DIAMONDS("♦", "D"),
    HEARTS("♥", "H"),
    SPADES("♠", "S");

    private final String symbol;
    private final String code;

    Suit(String symbol, String code) {
        this.symbol = symbol;
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCode() {
        return code;
    }
}
