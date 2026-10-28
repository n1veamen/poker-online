package com.poker.engine;

import com.poker.model.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SidePotCalculator {

    public static List<BigDecimal> calculateSidePots(List<Player> players, BigDecimal totalPot) {
        if (players == null || players.isEmpty()) {
            return List.of();
        }

        boolean hasHandContributions = players.stream()
                .anyMatch(player -> player.getHandContribution().signum() > 0);
        List<BigDecimal> playerContributions = players.stream()
                .map(player -> hasHandContributions ? player.getHandContribution() : player.getBet())
                .toList();
        TreeSet<BigDecimal> levels = new TreeSet<>(playerContributions.stream()
                .filter(amount -> amount != null && amount.signum() > 0)
                .toList());
        if (levels.isEmpty()) {
            return List.of();
        }

        List<BigDecimal> pots = new ArrayList<>();
        BigDecimal previous = BigDecimal.ZERO;
        for (BigDecimal current : levels) {
            long contributors = playerContributions.stream()
                    .filter(amount -> amount != null && amount.compareTo(current) >= 0)
                    .count();
            BigDecimal potSize = current.subtract(previous).multiply(BigDecimal.valueOf(contributors));
            pots.add(potSize);
            previous = current;
        }

        return pots;
    }
}
