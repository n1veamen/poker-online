package com.poker.engine;

import com.poker.model.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SidePotCalculator {

    public static List<BigDecimal> calculateSidePots(List<Player> players, BigDecimal totalPot) {
        if (players == null || players.isEmpty()) {
            return List.of();
        }

        List<BigDecimal> contributions = players.stream()
                .map(Player::getBet)
                .filter(amount -> amount != null && amount.compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.naturalOrder())
                .toList();

        if (contributions.isEmpty()) {
            return List.of();
        }

        List<BigDecimal> pots = new ArrayList<>();
        BigDecimal previous = BigDecimal.ZERO;
        int activePlayers = contributions.size();

        for (BigDecimal current : contributions) {
            if (current.compareTo(previous) <= 0) {
                continue;
            }
            BigDecimal potSize = current.subtract(previous).multiply(BigDecimal.valueOf(activePlayers));
            pots.add(potSize);
            previous = current;
            activePlayers--;
        }

        if (pots.isEmpty()) {
            return List.of(BigDecimal.ZERO);
        }

        BigDecimal sum = pots.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPot != null && totalPot.compareTo(BigDecimal.ZERO) > 0 && sum.compareTo(totalPot) != 0) {
            return pots;
        }

        return pots;
    }
}
