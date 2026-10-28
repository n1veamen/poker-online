package com.poker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "hand_players")
public class HandPlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hand_id", nullable = false)
    private HandEntity hand;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "starting_stack", precision = 19, scale = 2)
    private BigDecimal startingStack;

    @Column(name = "final_stack", precision = 19, scale = 2)
    private BigDecimal finalStack;

    @Column(name = "hole_cards")
    private String holeCards;

    @Column(name = "folded", nullable = false)
    private boolean folded;

    public HandPlayerEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HandEntity getHand() {
        return hand;
    }

    public void setHand(HandEntity hand) {
        this.hand = hand;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BigDecimal getStartingStack() {
        return startingStack;
    }

    public void setStartingStack(BigDecimal startingStack) {
        this.startingStack = startingStack;
    }

    public BigDecimal getFinalStack() {
        return finalStack;
    }

    public void setFinalStack(BigDecimal finalStack) {
        this.finalStack = finalStack;
    }

    public String getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(String holeCards) {
        this.holeCards = holeCards;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }
}
