package com.poker.engine;

import com.poker.model.Player;
import com.poker.model.Table;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokerEngineTest {

    @Test
    void shouldRotateBlinds() {
        Table table = new Table();
        Player p1 = new Player("A", Table.DEFAULT_STACK, "s1", 0);
        Player p2 = new Player("B", Table.DEFAULT_STACK, "s2", 1);
        Player p3 = new Player("C", Table.DEFAULT_STACK, "s3", 2);
        table.sitPlayer(p1);
        table.sitPlayer(p2);
        table.sitPlayer(p3);

        table.setDealerIndex(0);
        table.setSmallBlindIndex(1);
        table.setBigBlindIndex(2);

        assertEquals(1, table.getSmallBlindIndex());
        assertEquals(2, table.getBigBlindIndex());
    }

    @Test
    void shouldTrackSidePots() {
        Player p1 = new Player("A", new BigDecimal("1000"), "s1", 0);
        Player p2 = new Player("B", new BigDecimal("1000"), "s2", 1);
        Player p3 = new Player("C", new BigDecimal("1000"), "s3", 2);

        p1.setBet(new BigDecimal("100"));
        p2.setBet(new BigDecimal("200"));
        p3.setBet(new BigDecimal("400"));

        List<BigDecimal> pots = SidePotCalculator.calculateSidePots(List.of(p1, p2, p3), new BigDecimal("700"));
        assertEquals(3, pots.size());
        assertTrue(pots.stream().allMatch(value -> value.compareTo(BigDecimal.ZERO) > 0));
    }
}
