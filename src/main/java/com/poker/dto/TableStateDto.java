package com.poker.dto;

import java.math.BigDecimal;
import java.util.List;

public class TableStateDto {
    private String tableId;
    private int maxSeats;
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;
    private BigDecimal pot;
    private String currentStreet;
    private List<PlayerDto> players;
    private List<String> board;
    private int currentActorSeat = -1;
    private BigDecimal currentBet = BigDecimal.ZERO;
    private BigDecimal minRaise = BigDecimal.ZERO;
    private boolean handInProgress;
    private String statusMessage;
    private long turnSecondsRemaining;
    private long handNumber;
    private boolean matchOver;
    private String matchWinner;

    public TableStateDto() {
    }

    public TableStateDto(String tableId, int maxSeats, int dealerIndex, int smallBlindIndex, int bigBlindIndex,
                         BigDecimal pot, String currentStreet, List<PlayerDto> players, List<String> board) {
        this.tableId = tableId;
        this.maxSeats = maxSeats;
        this.dealerIndex = dealerIndex;
        this.smallBlindIndex = smallBlindIndex;
        this.bigBlindIndex = bigBlindIndex;
        this.pot = pot;
        this.currentStreet = currentStreet;
        this.players = players;
        this.board = board;
    }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public int getMaxSeats() { return maxSeats; }
    public void setMaxSeats(int maxSeats) { this.maxSeats = maxSeats; }

    public int getDealerIndex() { return dealerIndex; }
    public void setDealerIndex(int dealerIndex) { this.dealerIndex = dealerIndex; }

    public int getSmallBlindIndex() { return smallBlindIndex; }
    public void setSmallBlindIndex(int smallBlindIndex) { this.smallBlindIndex = smallBlindIndex; }

    public int getBigBlindIndex() { return bigBlindIndex; }
    public void setBigBlindIndex(int bigBlindIndex) { this.bigBlindIndex = bigBlindIndex; }

    public BigDecimal getPot() { return pot; }
    public void setPot(BigDecimal pot) { this.pot = pot; }

    public String getCurrentStreet() { return currentStreet; }
    public void setCurrentStreet(String currentStreet) { this.currentStreet = currentStreet; }

    public List<PlayerDto> getPlayers() { return players; }
    public void setPlayers(List<PlayerDto> players) { this.players = players; }

    public List<String> getBoard() { return board; }
    public void setBoard(List<String> board) { this.board = board; }

    public int getCurrentActorSeat() { return currentActorSeat; }
    public void setCurrentActorSeat(int currentActorSeat) { this.currentActorSeat = currentActorSeat; }

    public BigDecimal getCurrentBet() { return currentBet; }
    public void setCurrentBet(BigDecimal currentBet) { this.currentBet = currentBet; }

    public BigDecimal getMinRaise() { return minRaise; }
    public void setMinRaise(BigDecimal minRaise) { this.minRaise = minRaise; }

    public boolean isHandInProgress() { return handInProgress; }
    public void setHandInProgress(boolean handInProgress) { this.handInProgress = handInProgress; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public long getTurnSecondsRemaining() { return turnSecondsRemaining; }
    public void setTurnSecondsRemaining(long turnSecondsRemaining) { this.turnSecondsRemaining = turnSecondsRemaining; }

    public long getHandNumber() { return handNumber; }
    public void setHandNumber(long handNumber) { this.handNumber = handNumber; }

    public boolean isMatchOver() { return matchOver; }
    public void setMatchOver(boolean matchOver) { this.matchOver = matchOver; }

    public String getMatchWinner() { return matchWinner; }
    public void setMatchWinner(String matchWinner) { this.matchWinner = matchWinner; }
}
