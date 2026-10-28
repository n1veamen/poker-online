package com.poker.engine;

import com.poker.model.Player;
import com.poker.model.Table;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PokerEngineOrderTest {

    @Test
    void shouldRotateDealerAndBlindsInOrder() {
        Table table = new Table();
        Player p1 = new Player("Alice", Table.DEFAULT_STACK, "s1", 0);
        Player p2 = new Player("Bob", Table.DEFAULT_STACK, "s2", 1);
        Player p3 = new Player("Charlie", Table.DEFAULT_STACK, "s3", 2);
        table.sitPlayer(p1);
        table.sitPlayer(p2);
        table.sitPlayer(p3);

        table.setDealerIndex(0);
        PokerEngine.applyBlinds(table, table.getPlayers());

        assertEquals(1, table.getSmallBlindIndex());
        assertEquals(2, table.getBigBlindIndex());
        assertEquals(new BigDecimal("10"), p2.getBet());
        assertEquals(new BigDecimal("20"), p3.getBet());
    }
}
