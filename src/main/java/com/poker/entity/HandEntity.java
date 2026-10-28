package com.poker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hands")
public class HandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "pot_total", precision = 19, scale = 2)
    private BigDecimal potTotal;

    @Column(name = "board_cards")
    private String boardCards;

    @Column(name = "winner_nickname")
    private String winnerNickname;

    @OneToMany(mappedBy = "hand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HandPlayerEntity> players = new ArrayList<>();

    @OneToMany(mappedBy = "hand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionEntity> actions = new ArrayList<>();

    public HandEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public BigDecimal getPotTotal() {
        return potTotal;
    }

    public void setPotTotal(BigDecimal potTotal) {
        this.potTotal = potTotal;
    }

    public String getBoardCards() {
        return boardCards;
    }

    public void setBoardCards(String boardCards) {
        this.boardCards = boardCards;
    }

    public String getWinnerNickname() {
        return winnerNickname;
    }

    public void setWinnerNickname(String winnerNickname) {
        this.winnerNickname = winnerNickname;
    }

    public List<HandPlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<HandPlayerEntity> players) {
        this.players = players;
    }

    public List<ActionEntity> getActions() {
        return actions;
    }

    public void setActions(List<ActionEntity> actions) {
        this.actions = actions;
    }
}
