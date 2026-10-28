package com.poker.service;

import com.poker.engine.HandEvaluator;
import com.poker.engine.PokerEngine;
import com.poker.engine.SidePotCalculator;
import com.poker.model.Action;
import com.poker.model.ActionType;
import com.poker.model.Card;
import com.poker.model.Player;
import com.poker.model.Street;
import com.poker.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final TableService tableService;
    private final HistoryService historyService;
    private final Map<String, Object> handState = new ConcurrentHashMap<>();

    public GameService(TableService tableService, HistoryService historyService) {
        this.tableService = tableService;
        this.historyService = historyService;
    }

    public void startNewHand() {
        Table table = tableService.getTableState();
        List<Player> players = table.getPlayers();
        if (players.size() < 2) {
            throw new IllegalStateException("At least 2 players required to start a hand");
        }

        table.clearBoard();
        table.setPot(BigDecimal.ZERO);
        table.setCurrentStreet(Street.PREFLOP);

        for (Player player : players) {
            if (player == null) {
                continue;
            }
            player.setFolded(false);
            player.setAllIn(false);
            player.setBet(BigDecimal.ZERO);
            player.setHoleCards(new ArrayList<>());
        }

        PokerEngine.dealCards(table, players);
        handState.put("currentStreet", Street.PREFLOP);
        handState.put("board", new ArrayList<>(table.getBoard()));
        handState.put("startedAt", Instant.now());
    }

    public void dealFlop() {
        Table table = tableService.getTableState();
        if (table.getBoard().size() == 3) {
            return;
        }
        table.getBoard().addAll(List.of(null, null, null));
        table.getBoard().clear();
        table.getBoard().addAll(List.of(new com.poker.model.Card(com.poker.model.Rank.ACE, com.poker.model.Suit.CLUBS),
                new com.poker.model.Card(com.poker.model.Rank.KING, com.poker.model.Suit.HEARTS),
                new com.poker.model.Card(com.poker.model.Rank.QUEEN, com.poker.model.Suit.DIAMONDS)));
        table.setCurrentStreet(Street.FLOP);
        handState.put("currentStreet", Street.FLOP);
    }

    public void dealTurn() {
        Table table = tableService.getTableState();
        if (table.getBoard().size() == 4) {
            return;
        }
        table.getBoard().add(new com.poker.model.Card(com.poker.model.Rank.JACK, com.poker.model.Suit.SPADES));
        table.setCurrentStreet(Street.TURN);
        handState.put("currentStreet", Street.TURN);
    }

    public void dealRiver() {
        Table table = tableService.getTableState();
        if (table.getBoard().size() == 5) {
            return;
        }
        table.getBoard().add(new com.poker.model.Card(com.poker.model.Rank.NINE, com.poker.model.Suit.CLUBS));
        table.setCurrentStreet(Street.RIVER);
        handState.put("currentStreet", Street.RIVER);
    }

    public void processAction(String nickname, ActionType actionType, BigDecimal amount) {
        Table table = tableService.getTableState();
        Player player = table.getPlayerByNickname(nickname);
        if (player == null) {
            throw new IllegalStateException("Player not found");
        }

        if (actionType == ActionType.CHECK && player.getBet().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot check after betting");
        }

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        switch (actionType) {
            case FOLD -> player.setFolded(true);
            case ALL_IN -> {
                player.setAllIn(true);
                BigDecimal allInAmount = player.getStack();
                player.setBet(allInAmount);
                player.setStack(BigDecimal.ZERO);
                table.addToPot(allInAmount);
            }
            case CALL -> {
                BigDecimal callAmount = amount.max(BigDecimal.ZERO);
                player.setBet(callAmount);
                player.setStack(player.getStack().subtract(callAmount));
                table.addToPot(callAmount);
            }
            case BET, RAISE -> {
                BigDecimal betAmount = amount.max(BigDecimal.ZERO);
                player.setBet(betAmount);
                player.setStack(player.getStack().subtract(betAmount));
                table.addToPot(betAmount);
            }
            case CHECK -> player.setBet(BigDecimal.ZERO);
            default -> throw new IllegalStateException("Unsupported action: " + actionType);
        }

        log.info("Action {} by {} for {}", actionType, nickname, amount);
    }

    public void completeHand() {
        Table table = tableService.getTableState();
        List<Player> players = table.getPlayers();
        List<Player> active = players.stream().filter(player -> player != null && !player.isFolded()).toList();
        if (active.isEmpty()) {
            return;
        }

        BigDecimal pot = table.getPot();
        List<Card> boardSnapshot = new ArrayList<>(table.getBoard());
        Player winner = determineWinner(active, boardSnapshot);
        if (winner != null) {
            winner.addToStack(pot);
        }

        List<Player> snapshot = new ArrayList<>(players);
        String winnerNickname = winner != null ? winner.getNickname() : "draw";

        for (Player player : snapshot) {
            if (player != null) {
                player.clearBet();
                player.setFolded(false);
                player.setAllIn(false);
            }
        }

        table.clearPot();
        table.clearBoard();
        table.setCurrentStreet(Street.SHOWDOWN);

        historyService.saveHand(snapshot, boardSnapshot, pot, winnerNickname);
        log.info("Hand ended. Winner: {}", winnerNickname);
    }

    public Player determineWinner(List<Player> players, List<Card> board) {
        Player bestPlayer = null;
        HandEvaluator.Evaluation bestEvaluation = null;
        for (Player player : players) {
            if (player == null || player.isFolded()) {
                continue;
            }
            List<Card> combined = new ArrayList<>(board);
            combined.addAll(player.getHoleCards());
            HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(combined);
            if (bestPlayer == null || compareEvaluations(evaluation, bestEvaluation) > 0) {
                bestPlayer = player;
                bestEvaluation = evaluation;
            }
        }
        return bestPlayer;
    }

    public void rotateDealer() {
        Table table = tableService.getTableState();
        int next = (table.getDealerIndex() + 1) % Table.MAX_SEATS;
        table.setDealerIndex(next);
    }

    public void addChatMessage(String nickname, String message) {
        log.info("Chat from {}: {}", nickname, message);
    }

    public List<Action> getRecentActions() {
        return List.of();
    }

    public List<BigDecimal> calculateSidePots(List<Player> players) {
        return SidePotCalculator.calculateSidePots(players, tableService.getPot());
    }

    private int compareEvaluations(HandEvaluator.Evaluation left, HandEvaluator.Evaluation right) {
        if (left == null || right == null) {
            return 1;
        }
        if (left.getRank() != right.getRank()) {
            return Integer.compare(left.getRank(), right.getRank());
        }
        for (int i = 0; i < Math.min(left.getHighCards().size(), right.getHighCards().size()); i++) {
            int compare = Integer.compare(left.getHighCards().get(i), right.getHighCards().get(i));
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
}
