package com.poker.service;

import com.poker.model.ActionType;
import com.poker.model.Player;
import com.poker.model.Street;
import com.poker.model.Table;
import com.poker.entity.HandEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {
    private TableService tableService;
    private GameService gameService;
    private Player alice;
    private Player bob;

    @BeforeEach
    void setUp() {
        tableService = new TableService();
        HistoryService historyService = new HistoryService(null, null, null) {
            @Override
            public HandEntity saveHand(List<Player> players, List<com.poker.model.Card> board,
                                       BigDecimal potTotal, String winnerNickname) {
                return null;
            }
        };
        gameService = new GameService(tableService, historyService);
        alice = tableService.joinTable("Alice", "alice-session");
        bob = tableService.joinTable("Bob", "bob-session");
    }

    @Test
    void startsHandDealsUniquePrivateCardsAndPostsBlinds() {
        gameService.startNewHand("alice-session");

        assertTrue(gameService.isHandInProgress());
        assertEquals(2, alice.getHoleCards().size());
        assertEquals(2, bob.getHoleCards().size());
        assertNotEquals(alice.getHoleCards().get(0), bob.getHoleCards().get(0));
        assertEquals(new BigDecimal("30"), tableService.getPot());
        assertEquals(new BigDecimal("20"), gameService.getCurrentBet());
        assertEquals(2, gameService.getPrivateCards("alice-session").size());
        assertTrue(gameService.getPrivateCards("unknown-session").isEmpty());
    }

    @Test
    void reconnectingBrowserKeepsItsHandSeatAndPrivateCards() {
        gameService.startNewHand("alice-session");

        Player resumedPlayer = tableService.joinTable("Alice", "alice-new-session", "alice-session");

        assertEquals(alice, resumedPlayer);
        assertEquals(2, gameService.getPrivateCards("alice-new-session").size());
        gameService.processAction("alice-new-session", ActionType.CALL, BigDecimal.ZERO);
        assertNotEquals(alice.getSessionId(), "alice-session");
    }

    @Test
    void cannotStartWithOnlyOnePlayer() {
        tableService.leaveTable("bob-session");

        assertThrows(IllegalStateException.class, () -> gameService.startNewHand("alice-session"));
    }

    @Test
    void disconnectingPlayerOnTurnFoldsAndSettlesTheHand() {
        gameService.startNewHand("alice-session");

        assertTrue(gameService.leaveSession(playerAt(gameService.getCurrentActorSeat()).getSessionId()));

        assertFalse(gameService.isHandInProgress());
        assertEquals(BigDecimal.ZERO, tableService.getPot());
        assertEquals(1, tableService.getTableState().getSeatCount());
    }

    @Test
    void checkCallBettingAdvancesThroughAllStreetsAndSettles() {
        gameService.startNewHand("alice-session");
        assertThrows(IllegalStateException.class,
                () -> gameService.processAction("alice-session", ActionType.CHECK, BigDecimal.ZERO));

        Player actor = playerAt(gameService.getCurrentActorSeat());
        gameService.processAction(actor.getSessionId(), ActionType.CALL, BigDecimal.ZERO);
        actor = playerAt(gameService.getCurrentActorSeat());
        gameService.processAction(actor.getSessionId(), ActionType.CHECK, BigDecimal.ZERO);
        assertEquals(Street.FLOP, tableService.getTableState().getCurrentStreet());
        assertEquals(3, tableService.getTableState().getBoard().size());

        while (gameService.isHandInProgress()) {
            actor = playerAt(gameService.getCurrentActorSeat());
            gameService.processAction(actor.getSessionId(), ActionType.CHECK, BigDecimal.ZERO);
        }

        assertEquals(Street.SHOWDOWN, tableService.getTableState().getCurrentStreet());
        assertEquals(5, tableService.getTableState().getBoard().size());
        assertEquals(BigDecimal.ZERO, tableService.getPot());
        assertEquals(0, alice.getStack().add(bob.getStack())
                .compareTo(Table.DEFAULT_STACK.multiply(BigDecimal.valueOf(2))));
    }

    @Test
    void foldingAwardsPotAndAllowsNextHand() {
        gameService.startNewHand("alice-session");
        Player actor = playerAt(gameService.getCurrentActorSeat());

        gameService.processAction(actor.getSessionId(), ActionType.FOLD, BigDecimal.ZERO);

        assertFalse(gameService.isHandInProgress());
        assertEquals(BigDecimal.ZERO, tableService.getPot());
        assertTrue(gameService.getStatusMessage().contains("wins uncontested"));
        assertTrue(alice.getStack().add(bob.getStack()).compareTo(Table.DEFAULT_STACK.multiply(BigDecimal.valueOf(2))) == 0);

        gameService.startNewHand("bob-session");
        assertTrue(gameService.isHandInProgress());
    }

    @Test
    void matchEndsWhenOnlyOneSeatedPlayerHasChips() {
        gameService.startNewHand("alice-session");
        Player actor = playerAt(gameService.getCurrentActorSeat());
        gameService.processAction(actor.getSessionId(), ActionType.FOLD, BigDecimal.ZERO);

        alice.setStack(BigDecimal.ZERO);
        bob.setStack(new BigDecimal("2000"));

        assertTrue(gameService.isMatchOver());
        assertEquals("Bob", gameService.getMatchWinnerNickname());
    }

    @Test
    void restartingMatchRestoresStacksAndClearsHandState() {
        gameService.startNewHand("alice-session");
        Player actor = playerAt(gameService.getCurrentActorSeat());
        gameService.processAction(actor.getSessionId(), ActionType.FOLD, BigDecimal.ZERO);
        alice.setStack(BigDecimal.ZERO);
        bob.setStack(new BigDecimal("2000"));

        gameService.restartMatch("alice-session");

        assertEquals(Table.DEFAULT_STACK, alice.getStack());
        assertEquals(Table.DEFAULT_STACK, bob.getStack());
        assertEquals(BigDecimal.ZERO, tableService.getPot());
        assertTrue(tableService.getTableState().getBoard().isEmpty());
        assertEquals(Street.PREFLOP, tableService.getTableState().getCurrentStreet());
        assertFalse(gameService.isHandInProgress());
        assertFalse(gameService.isMatchOver());
    }

    @Test
    void cannotRestartMatchDuringAnActiveHand() {
        gameService.startNewHand("alice-session");

        assertThrows(IllegalStateException.class, () -> gameService.restartMatch("alice-session"));
        assertTrue(gameService.isHandInProgress());
    }

    @Test
    void raiseRequiresCallAndAdvancesWhenMatched() {
        gameService.startNewHand("alice-session");
        Player smallBlind = playerAt(gameService.getCurrentActorSeat());

        gameService.processAction(smallBlind.getSessionId(), ActionType.RAISE, new BigDecimal("40"));
        assertEquals(new BigDecimal("40"), gameService.getCurrentBet());
        assertEquals(new BigDecimal("60"), tableService.getPot());

        Player bigBlind = playerAt(gameService.getCurrentActorSeat());
        gameService.processAction(bigBlind.getSessionId(), ActionType.CALL, BigDecimal.ZERO);

        assertEquals(Street.FLOP, tableService.getTableState().getCurrentStreet());
        assertEquals(new BigDecimal("80"), tableService.getPot());
    }

    @Test
    void invalidRaiseDoesNotChangeThePotOrTurn() {
        gameService.startNewHand("alice-session");
        int actorSeat = gameService.getCurrentActorSeat();
        Player actor = playerAt(actorSeat);

        assertThrows(IllegalStateException.class,
                () -> gameService.processAction(actor.getSessionId(), ActionType.RAISE, new BigDecimal("30")));

        assertEquals(0, tableService.getPot().compareTo(new BigDecimal("30")));
        assertEquals(actorSeat, gameService.getCurrentActorSeat());
    }

    @Test
    void shortAllInCreatesSidePotAndAllChipsAreAwarded() {
        TableService threePlayerTable = new TableService();
        GameService threePlayerGame = new GameService(threePlayerTable,
                new HistoryService(null, null, null) {
                    @Override
                    public HandEntity saveHand(List<Player> players, List<com.poker.model.Card> board,
                                               BigDecimal potTotal, String winnerNickname) {
                        return null;
                    }
                });
        Player shortStack = threePlayerTable.joinTable("Short", "short-session");
        Player middleStack = threePlayerTable.joinTable("Middle", "middle-session");
        Player deepStack = threePlayerTable.joinTable("Deep", "deep-session");
        shortStack.setStack(new BigDecimal("50"));
        middleStack.setStack(new BigDecimal("100"));
        deepStack.setStack(new BigDecimal("200"));
        threePlayerTable.getTableState().setDealerIndex(1);

        threePlayerGame.startNewHand("short-session");
        Player actor = playerAt(threePlayerTable, threePlayerGame.getCurrentActorSeat());
        threePlayerGame.processAction(actor.getSessionId(), ActionType.RAISE, new BigDecimal("60"));
        actor = playerAt(threePlayerTable, threePlayerGame.getCurrentActorSeat());
        threePlayerGame.processAction(actor.getSessionId(), ActionType.ALL_IN, BigDecimal.ZERO);
        actor = playerAt(threePlayerTable, threePlayerGame.getCurrentActorSeat());
        threePlayerGame.processAction(actor.getSessionId(), ActionType.CALL, BigDecimal.ZERO);

        while (threePlayerGame.isHandInProgress()) {
            actor = playerAt(threePlayerTable, threePlayerGame.getCurrentActorSeat());
            threePlayerGame.processAction(actor.getSessionId(), ActionType.CHECK, BigDecimal.ZERO);
        }

        assertEquals(0, shortStack.getStack().add(middleStack.getStack()).add(deepStack.getStack())
                .compareTo(new BigDecimal("350")));
        assertEquals(BigDecimal.ZERO, threePlayerTable.getPot());
    }

    private Player playerAt(int seatIndex) {
        return tableService.getTableState().getSeatPlayers().get(seatIndex);
    }

    private Player playerAt(TableService service, int seatIndex) {
        return service.getTableState().getSeatPlayers().get(seatIndex);
    }
}
