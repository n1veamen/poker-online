package com.poker.engine;

import com.poker.model.Card;
import com.poker.model.Deck;
import com.poker.model.Player;
import com.poker.model.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PokerEngine {
    public static final BigDecimal SMALL_BLIND = new BigDecimal("10");
    public static final BigDecimal BIG_BLIND = new BigDecimal("20");

    public static void applyBlinds(Table table, List<Player> players) {
        if (players == null || players.size() < 2) {
            throw new IllegalStateException("At least 2 players are required for a hand");
        }

        List<Player> activePlayers = players.stream().filter(player -> player != null && player.isActive()).toList();
        if (activePlayers.size() < 2) {
            return;
        }

        int dealerIndex = table.getDealerIndex();
        int smallBlindIndex = nextSeatIndex(dealerIndex, activePlayers.size(), table);
        int bigBlindIndex = nextSeatIndex(smallBlindIndex, activePlayers.size(), table);

        table.setSmallBlindIndex(smallBlindIndex);
        table.setBigBlindIndex(bigBlindIndex);

        for (int i = 0; i < activePlayers.size(); i++) {
            Player player = activePlayers.get(i);
            player.setBet(BigDecimal.ZERO);
            if (i == smallBlindIndex) {
                applyBlind(player, SMALL_BLIND);
            }
            if (i == bigBlindIndex) {
                applyBlind(player, BIG_BLIND);
            }
        }
    }

    public static void dealCards(Table table, List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("No players to deal cards");
        }

        Deck deck = new Deck();
        for (Player player : players) {
            if (player == null || !player.isActive()) {
                continue;
            }
            List<Card> holeCards = deck.draw(2);
            player.setHoleCards(holeCards);
            player.setFolded(false);
            player.setAllIn(false);
        }
        table.setBoard(new ArrayList<>());
        table.setCurrentStreet(com.poker.model.Street.PREFLOP);
    }

    public static Player determineWinner(List<Player> activePlayers, List<Card> board) {
        Player bestPlayer = null;
        HandEvaluator.Evaluation bestEvaluation = null;

        for (Player player : activePlayers) {
            if (player == null || player.isFolded()) {
                continue;
            }

            List<Card> combined = new ArrayList<>(board);
            combined.addAll(player.getHoleCards());
            HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(combined);
            if (bestPlayer == null || compareEvaluation(evaluation, bestEvaluation) > 0) {
                bestPlayer = player;
                bestEvaluation = evaluation;
            }
        }

        return bestPlayer;
    }

    public static List<Player> determineWinners(List<Player> activePlayers, List<Card> board) {
        List<Player> winners = new ArrayList<>();
        HandEvaluator.Evaluation bestEvaluation = null;

        for (Player player : activePlayers) {
            if (player == null || player.isFolded()) {
                continue;
            }
            List<Card> combined = new ArrayList<>(board);
            combined.addAll(player.getHoleCards());
            HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(combined);
            if (bestEvaluation == null || compareEvaluation(evaluation, bestEvaluation) > 0) {
                winners.clear();
                winners.add(player);
                bestEvaluation = evaluation;
            } else if (compareEvaluation(evaluation, bestEvaluation) == 0) {
                winners.add(player);
            }
        }

        return winners;
    }

    public static void advanceStreet(Table table) {
        if (table == null) {
            return;
        }

        switch (table.getCurrentStreet()) {
            case PREFLOP -> table.setCurrentStreet(com.poker.model.Street.FLOP);
            case FLOP -> table.setCurrentStreet(com.poker.model.Street.TURN);
            case TURN -> table.setCurrentStreet(com.poker.model.Street.RIVER);
            case RIVER -> table.setCurrentStreet(com.poker.model.Street.SHOWDOWN);
            default -> table.setCurrentStreet(com.poker.model.Street.PREFLOP);
        }
    }

    private static void applyBlind(Player player, BigDecimal amount) {
        player.setBet(amount);
        player.setStack(player.getStack().subtract(amount));
    }

    private static int nextSeatIndex(int currentIndex, int totalPlayers, Table table) {
        List<Player> seats = table.getPlayers();
        int next = currentIndex + 1;
        for (int i = 0; i < seats.size(); i++) {
            int candidate = (next + i) % seats.size();
            if (seats.get(candidate) != null && seats.get(candidate).isActive()) {
                return candidate;
            }
        }
        return currentIndex;
    }

    private static int compareEvaluation(HandEvaluator.Evaluation left, HandEvaluator.Evaluation right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        if (left.getRank() != right.getRank()) {
            return Integer.compare(left.getRank(), right.getRank());
        }
        for (int i = 0; i < Math.min(left.getHighCards().size(), right.getHighCards().size()); i++) {
            int comparison = Integer.compare(left.getHighCards().get(i), right.getHighCards().get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
}
