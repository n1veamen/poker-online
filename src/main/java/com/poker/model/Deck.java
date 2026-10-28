package com.poker.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final SecureRandom random = new SecureRandom();

    public Deck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in deck");
        }
        return cards.removeLast();
    }

    public List<Card> draw(int count) {
        List<Card> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(draw());
        }
        return result;
    }

    public int remaining() {
        return cards.size();
    }
}
