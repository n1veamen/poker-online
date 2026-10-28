package com.poker.engine;

import com.poker.model.Card;
import com.poker.model.Suit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HandEvaluator {

    public static class Evaluation {
        private final int rank;
        private final String description;
        private final List<Integer> highCards;

        public Evaluation(int rank, String description, List<Integer> highCards) {
            this.rank = rank;
            this.description = description;
            this.highCards = highCards;
        }

        public int getRank() {
            return rank;
        }

        public String getDescription() {
            return description;
        }

        public List<Integer> getHighCards() {
            return highCards;
        }
    }

    public static Evaluation evaluate(List<Integer> values) {
        if (values == null || values.size() < 5) {
            throw new IllegalArgumentException("At least 5 values required");
        }

        List<Integer> sorted = new ArrayList<>(values);
        sorted.sort(Comparator.reverseOrder());

        if (isFourOfAKind(sorted)) {
            return new Evaluation(8, "Four of a Kind", fourOfAKindValue(sorted));
        }
        if (isFullHouse(sorted)) {
            return new Evaluation(7, "Full House", fullHouseValue(sorted));
        }
        if (isStraight(sorted)) {
            return new Evaluation(5, "Straight", List.of(determineStraightHigh(sorted)));
        }
        if (isThreeOfAKind(sorted)) {
            return new Evaluation(4, "Three of a Kind", threeOfAKindValue(sorted));
        }
        if (isTwoPair(sorted)) {
            return new Evaluation(3, "Two Pair", twoPairValue(sorted));
        }
        if (isPair(sorted)) {
            return new Evaluation(2, "Pair", pairValue(sorted));
        }
        return new Evaluation(1, "High Card", sorted.stream().sorted(Comparator.reverseOrder()).limit(5).toList());
    }

    public static Evaluation evaluateCards(List<Card> cards) {
        if (cards == null || cards.size() < 5) {
            throw new IllegalArgumentException("At least 5 cards required");
        }

        List<List<Card>> combinations = generateCombinations(cards, 5);
        Evaluation best = null;

        for (List<Card> combination : combinations) {
            Evaluation candidate = evaluateFiveCardCombination(combination);
            if (best == null || comparison(candidate, best) > 0) {
                best = candidate;
            }
        }

        return best;
    }

    private static Evaluation evaluateFiveCardCombination(List<Card> cards) {
        List<Integer> values = cards.stream()
                .map(card -> card.getRank().getValue())
                .sorted(Comparator.reverseOrder())
                .toList();

        boolean flush = cards.stream().map(Card::getSuit).distinct().count() == 1;
        boolean straight = isStraight(values);

        if (flush && straight) {
            int high = determineStraightHigh(values);
            if (high == 14) {
                return new Evaluation(10, "Royal Flush", List.of(14));
            }
            return new Evaluation(9, "Straight Flush", List.of(high));
        }

        Map<Integer, Integer> counts = countOccurrences(values);

        if (counts.values().stream().anyMatch(v -> v == 4)) {
            int quad = counts.entrySet().stream()
                    .filter(entry -> entry.getValue() == 4)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);
            int kicker = values.stream().filter(v -> v != quad).findFirst().orElse(0);
            return new Evaluation(8, "Four of a Kind", List.of(quad, kicker));
        }

        if (counts.values().stream().filter(v -> v == 3).count() == 1 && counts.values().stream().filter(v -> v == 2).count() == 1) {
            int trip = counts.entrySet().stream().filter(entry -> entry.getValue() == 3).map(Map.Entry::getKey).findFirst().orElse(0);
            int pair = counts.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).findFirst().orElse(0);
            return new Evaluation(7, "Full House", List.of(trip, pair));
        }

        if (flush) {
            return new Evaluation(6, "Flush", values.stream().sorted(Comparator.reverseOrder()).toList());
        }

        if (straight) {
            return new Evaluation(5, "Straight", List.of(determineStraightHigh(values)));
        }

        if (counts.values().stream().anyMatch(v -> v == 3)) {
            int trip = counts.entrySet().stream().filter(entry -> entry.getValue() == 3).map(Map.Entry::getKey).findFirst().orElse(0);
            List<Integer> sortedValues = values.stream().filter(v -> v != trip).sorted(Comparator.reverseOrder()).toList();
            List<Integer> result = new ArrayList<>();
            result.add(trip);
            result.addAll(sortedValues.subList(0, Math.min(2, sortedValues.size())));
            return new Evaluation(4, "Three of a Kind", result);
        }

        if (counts.values().stream().filter(v -> v == 2).count() == 2) {
            List<Integer> pairValues = counts.entrySet().stream()
                    .filter(entry -> entry.getValue() == 2)
                    .map(Map.Entry::getKey)
                    .sorted(Comparator.reverseOrder())
                    .toList();
            int kicker = values.stream().filter(v -> !pairValues.contains(v)).max(Integer::compareTo).orElse(0);
            List<Integer> result = new ArrayList<>(pairValues);
            result.add(kicker);
            return new Evaluation(3, "Two Pair", result);
        }

        if (counts.values().stream().anyMatch(v -> v == 2)) {
            int pair = counts.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).findFirst().orElse(0);
            List<Integer> kickers = values.stream().filter(v -> v != pair).sorted(Comparator.reverseOrder()).toList();
            List<Integer> result = new ArrayList<>();
            result.add(pair);
            result.addAll(kickers.subList(0, Math.min(3, kickers.size())));
            return new Evaluation(2, "Pair", result);
        }

        return new Evaluation(1, "High Card", values.stream().sorted(Comparator.reverseOrder()).toList());
    }

    private static int comparison(Evaluation first, Evaluation second) {
        if (first.getRank() != second.getRank()) {
            return Integer.compare(first.getRank(), second.getRank());
        }

        int limit = Math.max(first.getHighCards().size(), second.getHighCards().size());
        for (int i = 0; i < limit; i++) {
            int a = i < first.getHighCards().size() ? first.getHighCards().get(i) : 0;
            int b = i < second.getHighCards().size() ? second.getHighCards().get(i) : 0;
            if (a != b) {
                return Integer.compare(a, b);
            }
        }
        return 0;
    }

    private static boolean isFlushLike(List<Integer> values) {
        return values != null && values.size() >= 5;
    }

    private static boolean isFourOfAKind(List<Integer> values) {
        return countOccurrences(values).values().stream().anyMatch(v -> v == 4);
    }

    private static boolean isFullHouse(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        return counts.values().stream().filter(v -> v == 3).count() == 1 && counts.values().stream().filter(v -> v == 2).count() == 1;
    }

    private static boolean isStraight(List<Integer> values) {
        if (values == null || values.size() < 5) {
            return false;
        }

        Set<Integer> unique = new HashSet<>(values);
        List<Integer> normalized = new ArrayList<>(unique);
        normalized.sort(Integer::compareTo);

        if (normalized.contains(14)) {
            normalized.add(1);
        }
        normalized = normalized.stream().distinct().sorted().toList();

        for (int i = 0; i <= normalized.size() - 5; i++) {
            boolean isSequence = true;
            for (int offset = 1; offset <= 4; offset++) {
                if (!normalized.contains(normalized.get(i) + offset)) {
                    isSequence = false;
                    break;
                }
            }
            if (isSequence) {
                return true;
            }
        }

        return false;
    }

    private static int determineStraightHigh(List<Integer> values) {
        Set<Integer> unique = new HashSet<>(values);
        List<Integer> normalized = new ArrayList<>(unique);
        normalized.sort(Integer::compareTo);

        if (normalized.contains(14)) {
            normalized.add(1);
        }
        normalized = normalized.stream().distinct().sorted().toList();

        for (int i = 0; i <= normalized.size() - 5; i++) {
            boolean isSequence = true;
            for (int offset = 1; offset <= 4; offset++) {
                if (!normalized.contains(normalized.get(i) + offset)) {
                    isSequence = false;
                    break;
                }
            }
            if (isSequence) {
                return normalized.get(i + 4);
            }
        }

        return 0;
    }

    private static boolean isThreeOfAKind(List<Integer> values) {
        return countOccurrences(values).values().stream().anyMatch(v -> v == 3);
    }

    private static boolean isTwoPair(List<Integer> values) {
        return countOccurrences(values).values().stream().filter(v -> v == 2).count() == 2;
    }

    private static boolean isPair(List<Integer> values) {
        return countOccurrences(values).values().stream().filter(v -> v == 2).count() == 1;
    }

    private static Map<Integer, Integer> countOccurrences(List<Integer> values) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer value : values) {
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }
        return counts;
    }

    private static List<Integer> fourOfAKindValue(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        int quad = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 4)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
        int kicker = values.stream().filter(v -> v != quad).max(Integer::compareTo).orElse(0);
        return List.of(quad, kicker);
    }

    private static List<Integer> fullHouseValue(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        int trip = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
        int pair = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
        return List.of(trip, pair);
    }

    private static List<Integer> threeOfAKindValue(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        int trip = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
        List<Integer> kickers = values.stream()
                .filter(v -> v != trip)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .toList();
        List<Integer> result = new ArrayList<>();
        result.add(trip);
        result.addAll(kickers);
        return result;
    }

    private static List<Integer> twoPairValue(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        List<Integer> pairValues = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .sorted(Comparator.reverseOrder())
                .toList();
        int kicker = values.stream().filter(v -> !pairValues.contains(v)).max(Integer::compareTo).orElse(0);
        List<Integer> result = new ArrayList<>(pairValues);
        result.add(kicker);
        return result;
    }

    private static List<Integer> pairValue(List<Integer> values) {
        Map<Integer, Integer> counts = countOccurrences(values);
        int pair = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
        List<Integer> kickers = values.stream()
                .filter(v -> v != pair)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        List<Integer> result = new ArrayList<>();
        result.add(pair);
        result.addAll(kickers.subList(0, Math.min(3, kickers.size())));
        return result;
    }

    private static List<List<Card>> generateCombinations(List<Card> cards, int size) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinations(cards, size, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinations(List<Card> cards, int size, int start, List<Card> current, List<List<Card>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (start >= cards.size()) {
            return;
        }

        for (int i = start; i <= cards.size() - (size - current.size()); i++) {
            current.add(cards.get(i));
            generateCombinations(cards, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
