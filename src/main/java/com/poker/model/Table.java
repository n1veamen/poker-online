package com.poker.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table {
    public static final int MAX_SEATS = 8;
    public static final BigDecimal DEFAULT_STACK = new BigDecimal("1000");

    private final List<Player> seats = new ArrayList<>(MAX_SEATS);
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;
    private BigDecimal pot = BigDecimal.ZERO;
    private final List<Card> board = new ArrayList<>();
    private Street currentStreet = Street.PREFLOP;

    public Table() {
        for (int i = 0; i < MAX_SEATS; i++) {
            seats.add(null);
        }
    }

    public boolean seatAvailable() {
        return seats.stream().anyMatch(Objects::isNull);
    }

    public boolean isFull() {
        return seats.stream().noneMatch(Objects::isNull);
    }

    public List<Player> getPlayers() {
        return seats.stream().filter(Objects::nonNull).toList();
    }

    public Player getPlayerByNickname(String nickname) {
        return getPlayers().stream()
                .filter(player -> player.getNickname().equalsIgnoreCase(nickname))
                .findFirst()
                .orElse(null);
    }

    public void sitPlayer(Player player) {
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i) == null) {
                seats.set(i, player);
                player.setSeatIndex(i);
                return;
            }
        }
        throw new IllegalStateException("Table is full");
    }

    public void removePlayer(String nickname) {
        for (int i = 0; i < seats.size(); i++) {
            Player current = seats.get(i);
            if (current != null && current.getNickname().equalsIgnoreCase(nickname)) {
                seats.set(i, null);
                return;
            }
        }
    }

    public void removePlayer(Player player) {
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i) == player) {
                seats.set(i, null);
                return;
            }
        }
    }

    public int getSeatCount() {
        return (int) seats.stream().filter(Objects::nonNull).count();
    }

    public List<Player> getSeatPlayers() {
        return new ArrayList<>(seats);
    }

    public int getDealerIndex() {
        return dealerIndex;
    }

    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    public int getSmallBlindIndex() {
        return smallBlindIndex;
    }

    public void setSmallBlindIndex(int smallBlindIndex) {
        this.smallBlindIndex = smallBlindIndex;
    }

    public int getBigBlindIndex() {
        return bigBlindIndex;
    }

    public void setBigBlindIndex(int bigBlindIndex) {
        this.bigBlindIndex = bigBlindIndex;
    }

    public BigDecimal getPot() {
        return pot;
    }

    public void setPot(BigDecimal pot) {
        this.pot = pot;
    }

    public void addToPot(BigDecimal amount) {
        this.pot = this.pot.add(amount);
    }

    public void clearPot() {
        this.pot = BigDecimal.ZERO;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> cards) {
        board.clear();
        board.addAll(cards);
    }

    public void clearBoard() {
        board.clear();
    }

    public Street getCurrentStreet() {
        return currentStreet;
    }

    public void setCurrentStreet(Street currentStreet) {
        this.currentStreet = currentStreet;
    }
}
