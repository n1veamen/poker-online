package com.poker.engine;

import com.poker.model.Card;
import com.poker.model.Rank;
import com.poker.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandEvaluatorTest {

    @Test
    void shouldEvaluateRoyalFlush() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.ACE, Suit.HEARTS)
        ));

        assertEquals(10, evaluation.getRank());
        assertEquals("Royal Flush", evaluation.getDescription());
        assertEquals(List.of(14), evaluation.getHighCards());
    }

    @Test
    void shouldEvaluateStraightFlush() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.JACK, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.KING, Suit.CLUBS)
        ));

        assertEquals(9, evaluation.getRank());
        assertEquals("Straight Flush", evaluation.getDescription());
        assertEquals(List.of(13), evaluation.getHighCards());
    }

    @Test
    void shouldEvaluateFourOfAKind() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.TWO, Suit.CLUBS)
        ));

        assertEquals(8, evaluation.getRank());
        assertEquals("Four of a Kind", evaluation.getDescription());
    }

    @Test
    void shouldEvaluateFullHouse() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.EIGHT, Suit.CLUBS),
                new Card(Rank.EIGHT, Suit.DIAMONDS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.SPADES)
        ));

        assertEquals(7, evaluation.getRank());
        assertEquals("Full House", evaluation.getDescription());
        assertEquals(List.of(8, 4), evaluation.getHighCards());
    }

    @Test
    void shouldEvaluateFlush() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.ACE, Suit.HEARTS)
        ));

        assertEquals(6, evaluation.getRank());
        assertEquals("Flush", evaluation.getDescription());
    }

    @Test
    void shouldEvaluateStraight() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.THREE, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.CLUBS)
        ));

        assertEquals(5, evaluation.getRank());
        assertEquals("Straight", evaluation.getDescription());
        assertEquals(List.of(7), evaluation.getHighCards());
    }

    @Test
    void shouldEvaluateThreeOfAKind() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.SIX, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.TEN, Suit.SPADES),
                new Card(Rank.TWO, Suit.CLUBS)
        ));

        assertEquals(4, evaluation.getRank());
        assertEquals("Three of a Kind", evaluation.getDescription());
    }

    @Test
    void shouldEvaluateTwoPair() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.SPADES),
                new Card(Rank.TWO, Suit.CLUBS)
        ));

        assertEquals(3, evaluation.getRank());
        assertEquals("Two Pair", evaluation.getDescription());
    }

    @Test
    void shouldEvaluatePair() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.JACK, Suit.CLUBS),
                new Card(Rank.JACK, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.NINE, Suit.CLUBS)
        ));

        assertEquals(2, evaluation.getRank());
        assertEquals("Pair", evaluation.getDescription());
    }

    @Test
    void shouldEvaluateHighCard() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.SPADES),
                new Card(Rank.TWO, Suit.CLUBS)
        ));

        assertEquals(1, evaluation.getRank());
        assertEquals("High Card", evaluation.getDescription());
        assertEquals(List.of(14, 12, 9, 4, 2), evaluation.getHighCards());
    }

    @Test
    void shouldPickBestHandFromSevenCards() {
        HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(List.of(
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.SPADES),
                new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.KING, Suit.SPADES)
        ));

        assertEquals(5, evaluation.getRank());
        assertEquals("Straight", evaluation.getDescription());
        assertEquals(List.of(6), evaluation.getHighCards());
    }
}
