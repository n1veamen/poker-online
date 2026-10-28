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
}
