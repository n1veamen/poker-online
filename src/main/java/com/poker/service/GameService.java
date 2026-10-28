package com.poker.service;

import com.poker.engine.HandEvaluator;
import com.poker.model.ActionType;
import com.poker.model.Card;
import com.poker.model.Deck;
import com.poker.model.Player;
import com.poker.model.Street;
import com.poker.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private static final BigDecimal SMALL_BLIND = new BigDecimal("10");
    private static final BigDecimal BIG_BLIND = new BigDecimal("20");

    private final TableService tableService;
    private final HistoryService historyService;
    private final Set<Player> handPlayers = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Integer> actedThisRound = new HashSet<>();

    private Deck deck;
    private boolean handInProgress;
    private int currentActorSeat = -1;
    private int currentBet;
    private int minimumRaise = BIG_BLIND.intValueExact();
    private long handNumber;
    private Instant turnDeadline;
    private String statusMessage = "Join with at least one other player to start a hand.";

    public GameService(TableService tableService, HistoryService historyService) {
        this.tableService = tableService;
        this.historyService = historyService;
    }

    public synchronized void startNewHand(String sessionId) {
        Table table = tableService.getTableState();
        Player requester = tableService.getPlayerBySession(sessionId);
        if (requester == null) {
            throw new IllegalStateException("Join the table before starting a hand.");
        }
        if (handInProgress) {
            throw new IllegalStateException("A hand is already in progress.");
        }

        List<Player> readyPlayers = table.getPlayers().stream()
                .filter(player -> player.isActive() && player.getStack().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparingInt(Player::getSeatIndex))
                .toList();
        if (readyPlayers.size() < 2) {
            throw new IllegalStateException("At least two players with chips are required to start.");
        }

        handPlayers.clear();
        handPlayers.addAll(readyPlayers);
        actedThisRound.clear();
        table.clearBoard();
        table.clearPot();
        table.setCurrentStreet(Street.PREFLOP);
        deck = new Deck();
        currentBet = 0;
        minimumRaise = BIG_BLIND.intValueExact();
        handNumber++;

        for (Player player : table.getPlayers()) {
            player.setFolded(false);
            player.setAllIn(false);
            player.setDealer(false);
            player.setBet(BigDecimal.ZERO);
            player.setHandContribution(BigDecimal.ZERO);
            player.setHoleCards(List.of());
        }

        int dealer = nextSeat(table, table.getDealerIndex(), handPlayers::contains);
        table.setDealerIndex(dealer);
        playerAt(table, dealer).setDealer(true);
        int smallBlind = readyPlayers.size() == 2 ? dealer : nextSeat(table, dealer, handPlayers::contains);
        int bigBlind = nextSeat(table, smallBlind, handPlayers::contains);
        table.setSmallBlindIndex(smallBlind);
        table.setBigBlindIndex(bigBlind);
        for (int cardRound = 0; cardRound < 2; cardRound++) {
            for (int seat = nextSeat(table, dealer, handPlayers::contains); ; seat = nextSeat(table, seat, handPlayers::contains)) {
                Player player = playerAt(table, seat);
                player.getHoleCards().add(deck.draw());
                if (seat == previousSeat(table, nextSeat(table, dealer, handPlayers::contains), handPlayers::contains)) {
                    break;
                }
            }
        }

        postBlind(table, playerAt(table, smallBlind), SMALL_BLIND);
        postBlind(table, playerAt(table, bigBlind), BIG_BLIND);
        currentBet = playerAt(table, bigBlind).getBet().intValueExact();
        handInProgress = true;
        if (handPlayers.stream().noneMatch(this::canAct)) {
            runOutBoard(table);
            settleShowdown(table);
            return;
        }
        setCurrentActor(nextSeat(table, bigBlind, this::canAct));
        statusMessage = "Pre-flop: " + playerAt(table, currentActorSeat).getNickname() + " to act.";
        log.info("Hand {} started with {} players", handNumber, handPlayers.size());
    }

    public synchronized void restartMatch(String sessionId) {
        Table table = tableService.getTableState();
        if (tableService.getPlayerBySession(sessionId) == null) {
            throw new IllegalStateException("Join the table before restarting the match.");
        }
        if (handInProgress) {
            throw new IllegalStateException("Finish the current hand before restarting the match.");
        }

        handPlayers.clear();
        actedThisRound.clear();
        deck = null;
        currentActorSeat = -1;
        currentBet = 0;
        minimumRaise = BIG_BLIND.intValueExact();
        turnDeadline = null;
        tableService.restoreStartingStacks();
        table.setDealerIndex(Table.MAX_SEATS - 1);
        table.setSmallBlindIndex(0);
        table.setBigBlindIndex(0);
        table.clearBoard();
        table.clearPot();
        table.setCurrentStreet(Street.PREFLOP);
        table.getPlayers().forEach(player -> {
            player.setStack(Table.DEFAULT_STACK);
            player.setBet(BigDecimal.ZERO);
            player.setHandContribution(BigDecimal.ZERO);
            player.setFolded(false);
            player.setAllIn(false);
            player.setDealer(false);
            player.setHoleCards(List.of());
        });
        statusMessage = "The match has been restarted. Waiting for players.";
        log.info("Match restarted by {}", tableService.getPlayerBySession(sessionId).getNickname());
    }

    public synchronized void processAction(String sessionId, ActionType actionType, BigDecimal amount) {
        if (!handInProgress) {
            throw new IllegalStateException("There is no hand in progress.");
        }
        Player player = tableService.getPlayerBySession(sessionId);
        Table table = tableService.getTableState();
        if (player == null || !handPlayers.contains(player)) {
            throw new IllegalStateException("You are not playing this hand.");
        }
        if (player.getSeatIndex() != currentActorSeat) {
            throw new IllegalStateException("It is not your turn.");
        }
        if (player.isFolded() || player.isAllIn()) {
            throw new IllegalStateException("You cannot act right now.");
        }

        BigDecimal previousBet = player.getBet();
        BigDecimal callAmount = BigDecimal.valueOf(currentBet).subtract(previousBet);
        BigDecimal requestedAmount = amount == null ? BigDecimal.ZERO : amount;
        boolean raised = false;

        switch (actionType) {
            case FOLD -> {
                if (callAmount.signum() <= 0) {
                    throw new IllegalStateException("You can check instead of folding.");
                }
                player.setFolded(true);
            }
            case CHECK -> {
                if (callAmount.signum() > 0) {
                    throw new IllegalStateException("You must call or fold; checking is not allowed.");
                }
            }
            case CALL -> {
                if (callAmount.signum() <= 0) {
                    throw new IllegalStateException("There is no bet to call; check instead.");
                }
                contribute(player, callAmount.min(player.getStack()), table);
            }
            case BET, RAISE -> {
                BigDecimal targetBet = requestedAmount;
                if (targetBet.stripTrailingZeros().scale() > 0) {
                    throw new IllegalStateException("Use whole chip amounts.");
                }
                if (targetBet.compareTo(previousBet) <= 0 || targetBet.subtract(previousBet).compareTo(player.getStack()) > 0) {
                    throw new IllegalStateException("Enter a valid total bet for this street.");
                }
                if (targetBet.compareTo(BigDecimal.valueOf(currentBet)) <= 0) {
                    throw new IllegalStateException("A bet or raise must exceed the current bet.");
                }
                if ((actionType == ActionType.BET && currentBet > 0)
                        || (actionType == ActionType.RAISE && currentBet == 0)) {
                    throw new IllegalStateException(actionType == ActionType.BET
                            ? "Use Raise because there is already a bet."
                            : "Use Bet when there is no bet to raise.");
                }
                if (actedThisRound.contains(player.getSeatIndex())) {
                    throw new IllegalStateException("A short all-in did not reopen raising.");
                }
                BigDecimal raiseSize = targetBet.subtract(BigDecimal.valueOf(currentBet));
                boolean allIn = targetBet.subtract(previousBet).compareTo(player.getStack()) == 0;
                if (raiseSize.compareTo(BigDecimal.valueOf(minimumRaise)) < 0 && !allIn) {
                    throw new IllegalStateException("Minimum raise is " + minimumRaise + ".");
                }
                contribute(player, targetBet.subtract(previousBet), table);
                if (raiseSize.compareTo(BigDecimal.valueOf(minimumRaise)) >= 0) {
                    minimumRaise = raiseSize.intValueExact();
                    actedThisRound.clear();
                }
                currentBet = targetBet.intValueExact();
                raised = true;
            }
            case ALL_IN -> {
                BigDecimal allInTarget = previousBet.add(player.getStack());
                if (allInTarget.signum() <= 0) {
                    throw new IllegalStateException("There are no chips left to go all-in.");
                }
                if (allInTarget.compareTo(BigDecimal.valueOf(currentBet)) > 0
                        && actedThisRound.contains(player.getSeatIndex())) {
                    throw new IllegalStateException("A short all-in did not reopen raising.");
                }
                BigDecimal raiseSize = allInTarget.subtract(BigDecimal.valueOf(currentBet));
                contribute(player, player.getStack(), table);
                player.setAllIn(true);
                if (raiseSize.signum() > 0) {
                    if (raiseSize.compareTo(BigDecimal.valueOf(minimumRaise)) >= 0) {
                        minimumRaise = raiseSize.intValueExact();
                        actedThisRound.clear();
                    }
                    currentBet = allInTarget.intValueExact();
                    raised = true;
                }
            }
            default -> throw new IllegalStateException("Unsupported action.");
        }

        actedThisRound.add(player.getSeatIndex());
        log.info("{} {} in hand {}", player.getNickname(), actionType, handNumber);
        if (remainingPlayers().size() == 1) {
            awardUncontested(table);
            return;
        }
        if (bettingRoundComplete()) {
            advanceStreetOrShowdown(table);
            return;
        }
        setCurrentActor(nextSeat(table, player.getSeatIndex(), this::needsAction));
        statusMessage = (raised ? "Bet raised. " : "") + playerAt(table, currentActorSeat).getNickname() + " to act.";
    }

    public synchronized List<String> getPrivateCards(String sessionId) {
        Player player = tableService.getPlayerBySession(sessionId);
        if (player == null || !handPlayers.contains(player)) {
            return List.of();
        }
        return player.getHoleCards().stream().map(Card::getCode).toList();
    }

    public synchronized boolean leaveSession(String sessionId) {
        Player player = tableService.getPlayerBySession(sessionId);
        if (player == null) {
            return false;
        }
        if (handInProgress && handPlayers.contains(player)) {
            player.setFolded(true);
            actedThisRound.add(player.getSeatIndex());
            if (remainingPlayers().size() == 1) {
                awardUncontested(tableService.getTableState());
            } else if (player.getSeatIndex() == currentActorSeat) {
                Table table = tableService.getTableState();
                if (bettingRoundComplete()) {
                    advanceStreetOrShowdown(table);
                } else {
                    setCurrentActor(nextSeat(table, player.getSeatIndex(), this::needsAction));
                    statusMessage = player.getNickname() + " disconnected and folded. "
                            + playerAt(table, currentActorSeat).getNickname() + " to act.";
                }
            }
        }
        return tableService.leaveTable(sessionId);
    }

    public synchronized boolean isHandInProgress() {
        return handInProgress;
    }

    public synchronized int getCurrentActorSeat() {
        return currentActorSeat;
    }

    public synchronized BigDecimal getCurrentBet() {
        return BigDecimal.valueOf(currentBet);
    }

    public synchronized BigDecimal getMinimumRaise() {
        return BigDecimal.valueOf(minimumRaise);
    }

    public synchronized long getHandNumber() {
        return handNumber;
    }

    public synchronized String getStatusMessage() {
        return statusMessage;
    }

    public synchronized boolean isMatchOver() {
        return handNumber > 0 && !handInProgress && getMatchWinnerNickname() != null;
    }

    public synchronized String getMatchWinnerNickname() {
        if (handNumber == 0 || handInProgress) {
            return null;
        }
        List<Player> playersWithChips = tableService.getTableState().getPlayers().stream()
                .filter(player -> player.getStack().signum() > 0)
                .toList();
        return playersWithChips.size() == 1 ? playersWithChips.getFirst().getNickname() : null;
    }

    public synchronized long getTurnSecondsRemaining() {
        if (!handInProgress || turnDeadline == null) {
            return 0;
        }
        return Math.max(0, Duration.between(Instant.now(), turnDeadline).toSeconds());
    }

    public synchronized boolean autoFoldExpiredTurn() {
        if (!handInProgress || turnDeadline == null || Instant.now().isBefore(turnDeadline)) {
            return false;
        }
        Player player = playerAt(tableService.getTableState(), currentActorSeat);
        boolean facingBet = player.getBet().compareTo(BigDecimal.valueOf(currentBet)) < 0;
        String timeoutMessage = player.getNickname() + (facingBet
                ? " timed out and folded." : " timed out and checked.");
        processAction(player.getSessionId(), facingBet ? ActionType.FOLD : ActionType.CHECK, BigDecimal.ZERO);
        statusMessage = timeoutMessage + " " + statusMessage;
        return true;
    }

    public synchronized boolean isInCurrentHand(Player player) {
        return handPlayers.contains(player);
    }

    private void postBlind(Table table, Player player, BigDecimal blind) {
        contribute(player, blind.min(player.getStack()), table);
        if (player.getStack().signum() == 0) {
            player.setAllIn(true);
        }
    }

    private void contribute(Player player, BigDecimal amount, Table table) {
        if (amount.signum() < 0 || amount.compareTo(player.getStack()) > 0) {
            throw new IllegalStateException("Bet exceeds your remaining chips.");
        }
        player.setStack(player.getStack().subtract(amount));
        player.setBet(player.getBet().add(amount));
        player.setHandContribution(player.getHandContribution().add(amount));
        table.addToPot(amount);
        if (player.getStack().signum() == 0) {
            player.setAllIn(true);
        }
    }

    private boolean canAct(Player player) {
        return handPlayers.contains(player) && !player.isFolded() && !player.isAllIn()
                && player.getStack().signum() > 0;
    }

    private boolean needsAction(Player player) {
        return canAct(player) && (!actedThisRound.contains(player.getSeatIndex())
                || player.getBet().compareTo(BigDecimal.valueOf(currentBet)) < 0);
    }

    private boolean bettingRoundComplete() {
        return handPlayers.stream()
                .filter(this::canAct)
                .allMatch(player -> actedThisRound.contains(player.getSeatIndex())
                        && player.getBet().compareTo(BigDecimal.valueOf(currentBet)) >= 0);
    }

    private void advanceStreetOrShowdown(Table table) {
        if (table.getCurrentStreet() == Street.RIVER) {
            settleShowdown(table);
            return;
        }
        table.getPlayers().forEach(Player::clearBet);
        currentBet = 0;
        minimumRaise = BIG_BLIND.intValueExact();
        actedThisRound.clear();
        dealNextStreet(table);
        long playersAbleToAct = handPlayers.stream().filter(this::canAct).count();
        if (playersAbleToAct < 2) {
            runOutBoard(table);
            settleShowdown(table);
            return;
        }
        setCurrentActor(nextSeat(table, table.getDealerIndex(), this::canAct));
        statusMessage = table.getCurrentStreet() + ": " + playerAt(table, currentActorSeat).getNickname() + " to act.";
    }

    private void dealNextStreet(Table table) {
        deck.draw();
        switch (table.getCurrentStreet()) {
            case PREFLOP -> {
                table.getBoard().addAll(deck.draw(3));
                table.setCurrentStreet(Street.FLOP);
            }
            case FLOP -> {
                table.getBoard().add(deck.draw());
                table.setCurrentStreet(Street.TURN);
            }
            case TURN -> {
                table.getBoard().add(deck.draw());
                table.setCurrentStreet(Street.RIVER);
            }
            default -> throw new IllegalStateException("Cannot deal another street.");
        }
    }

    private void runOutBoard(Table table) {
        while (table.getCurrentStreet() != Street.RIVER) {
            dealNextStreet(table);
        }
    }

    private void awardUncontested(Table table) {
        Player winner = remainingPlayers().getFirst();
        BigDecimal pot = table.getPot();
        winner.addToStack(pot);
        finishHand(table, pot, List.of(winner), " wins uncontested.");
    }

    private void settleShowdown(Table table) {
        List<Player> survivors = remainingPlayers();
        BigDecimal pot = table.getPot();
        List<Player> allPlayers = new ArrayList<>(handPlayers);
        allPlayers.sort(Comparator.comparingInt(Player::getSeatIndex));
        Set<BigDecimal> levels = new java.util.TreeSet<>();
        allPlayers.stream().map(Player::getHandContribution)
                .filter(contribution -> contribution.signum() > 0).forEach(levels::add);

        BigDecimal previousLevel = BigDecimal.ZERO;
        Set<Player> winners = new HashSet<>();
        for (BigDecimal level : levels) {
            BigDecimal layer = level.subtract(previousLevel).multiply(BigDecimal.valueOf(
                    allPlayers.stream().filter(player -> player.getHandContribution().compareTo(level) >= 0).count()));
            List<Player> eligible = allPlayers.stream()
                    .filter(player -> !player.isFolded() && player.getHandContribution().compareTo(level) >= 0)
                    .toList();
            if (!eligible.isEmpty() && layer.signum() > 0) {
                List<Player> layerWinners = bestPlayers(eligible, table.getBoard());
                distribute(layer, layerWinners);
                winners.addAll(layerWinners);
            }
            previousLevel = level;
        }
        table.setCurrentStreet(Street.SHOWDOWN);
        finishHand(table, pot, winners.isEmpty() ? survivors : List.copyOf(winners), " showdown.");
    }

    private List<Player> bestPlayers(List<Player> candidates, List<Card> board) {
        HandEvaluator.Evaluation best = null;
        List<Player> winners = new ArrayList<>();
        for (Player player : candidates) {
            List<Card> cards = new ArrayList<>(board);
            cards.addAll(player.getHoleCards());
            HandEvaluator.Evaluation evaluation = HandEvaluator.evaluateCards(cards);
            int comparison = compare(evaluation, best);
            if (comparison > 0) {
                best = evaluation;
                winners.clear();
                winners.add(player);
            } else if (comparison == 0) {
                winners.add(player);
            }
        }
        return winners;
    }

    private void distribute(BigDecimal amount, List<Player> winners) {
        if (winners.isEmpty()) {
            return;
        }
        BigDecimal share = amount.divide(BigDecimal.valueOf(winners.size()), 2, RoundingMode.DOWN);
        BigDecimal distributed = share.multiply(BigDecimal.valueOf(winners.size()));
        for (Player winner : winners) {
            winner.addToStack(share);
        }
        winners.getFirst().addToStack(amount.subtract(distributed));
    }

    private void finishHand(Table table, BigDecimal pot, List<Player> winners, String message) {
        List<Card> boardSnapshot = new ArrayList<>(table.getBoard());
        String winnerNames = winners.stream().map(Player::getNickname).distinct()
                .reduce((left, right) -> left + ", " + right).orElse("No winner");
        handInProgress = false;
        currentActorSeat = -1;
        turnDeadline = null;
        actedThisRound.clear();
        try {
            historyService.saveHand(new ArrayList<>(handPlayers), boardSnapshot, pot, winnerNames);
        } catch (RuntimeException exception) {
            log.error("Could not save history for hand {}", handNumber, exception);
        }
        table.getPlayers().forEach(Player::clearBet);
        handPlayers.forEach(player -> player.setHandContribution(BigDecimal.ZERO));
        table.clearPot();
        String champion = getMatchWinnerNickname();
        statusMessage = winnerNames + message + (champion == null
                ? ""
                : " Match complete! " + champion + " is the champion. Restart the match to play again.");
        log.info("Hand {} ended. Winners: {}", handNumber, winnerNames);
    }

    private List<Player> remainingPlayers() {
        return handPlayers.stream().filter(player -> !player.isFolded())
                .sorted(Comparator.comparingInt(Player::getSeatIndex)).toList();
    }

    private void setCurrentActor(int seatIndex) {
        currentActorSeat = seatIndex;
        turnDeadline = Instant.now().plusSeconds(30);
    }

    private int nextSeat(Table table, int current, java.util.function.Predicate<Player> predicate) {
        List<Player> seats = table.getSeatPlayers();
        for (int offset = 1; offset <= seats.size(); offset++) {
            int candidate = Math.floorMod(current + offset, seats.size());
            Player player = seats.get(candidate);
            if (player != null && predicate.test(player)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No eligible player is available to act.");
    }

    private int previousSeat(Table table, int current, java.util.function.Predicate<Player> predicate) {
        List<Player> seats = table.getSeatPlayers();
        for (int offset = 1; offset <= seats.size(); offset++) {
            int candidate = Math.floorMod(current - offset, seats.size());
            Player player = seats.get(candidate);
            if (player != null && predicate.test(player)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No eligible player is available.");
    }

    private Player playerAt(Table table, int seatIndex) {
        Player player = table.getSeatPlayers().get(seatIndex);
        if (player == null) {
            throw new IllegalStateException("Player seat is empty.");
        }
        return player;
    }

    private int compare(HandEvaluator.Evaluation left, HandEvaluator.Evaluation right) {
        if (right == null) {
            return 1;
        }
        int rankComparison = Integer.compare(left.getRank(), right.getRank());
        if (rankComparison != 0) {
            return rankComparison;
        }
        for (int index = 0; index < Math.max(left.getHighCards().size(), right.getHighCards().size()); index++) {
            int leftValue = index < left.getHighCards().size() ? left.getHighCards().get(index) : 0;
            int rightValue = index < right.getHighCards().size() ? right.getHighCards().get(index) : 0;
            if (leftValue != rightValue) {
                return Integer.compare(leftValue, rightValue);
            }
        }
        return 0;
    }
}
