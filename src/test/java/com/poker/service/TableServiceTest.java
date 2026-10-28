package com.poker.service;

import com.poker.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableServiceTest {
    private final TableService tableService = new TableService();

    @Test
    void joiningAgainFromSameSessionDoesNotOccupyAnotherSeat() {
        Player firstJoin = tableService.joinTable("Player", "session-1");

        Player secondJoin = tableService.joinTable("Player", "session-1");

        assertSame(firstJoin, secondJoin);
        assertEquals(1, tableService.getTableState().getSeatCount());
    }

    @Test
    void disconnectReleasesSeatAndAllowsAnotherPlayerToJoin() {
        Player player = tableService.joinTable("Player", "session-1");

        assertTrue(tableService.leaveTable("session-1"));
        assertFalse(tableService.leaveTable("session-1"));

        Player nextPlayer = tableService.joinTable("Next", "session-2");
        assertNotSame(player, nextPlayer);
        assertEquals(1, tableService.getTableState().getSeatCount());
    }

    @Test
    void tableRejectsPlayersAfterEightUniqueSessions() {
        for (int i = 0; i < 8; i++) {
            tableService.joinTable("Player " + i, "session-" + i);
        }

        assertThrows(IllegalStateException.class,
                () -> tableService.joinTable("Extra", "session-extra"));
        assertEquals(8, tableService.getTableState().getSeatCount());
    }
}
