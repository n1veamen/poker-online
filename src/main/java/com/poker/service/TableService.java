package com.poker.service;

import com.poker.model.Player;
import com.poker.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TableService {
    private static final Logger log = LoggerFactory.getLogger(TableService.class);
    private final Map<String, Player> playersBySession = new ConcurrentHashMap<>();
    private final Map<String, Player> playersByClientId = new ConcurrentHashMap<>();
    private final Table table = new Table();

    public synchronized Player joinTable(String nickname, String sessionId) {
        return joinTable(nickname, sessionId, sessionId);
    }

    public synchronized Player joinTable(String nickname, String sessionId, String clientId) {
        Player existingPlayer = playersBySession.get(sessionId);
        if (existingPlayer != null) {
            if (!existingPlayer.getNickname().equalsIgnoreCase(nickname)) {
                throw new IllegalStateException("This connection already has a seat.");
            }
            return existingPlayer;
        }
        Player existingClient = playersByClientId.get(clientId);
        if (existingClient != null) {
            if (!existingClient.getNickname().equalsIgnoreCase(nickname)) {
                throw new IllegalStateException("This browser already has a seat under another nickname.");
            }
            boolean alreadySeated = table.getPlayers().stream().anyMatch(player -> player == existingClient);
            boolean nicknameAlreadySeated = table.getPlayers().stream()
                    .anyMatch(player -> player != existingClient
                            && player.getNickname().equalsIgnoreCase(nickname));
            if (nicknameAlreadySeated) {
                throw new IllegalStateException("That nickname is already seated.");
            }
            if (!alreadySeated && table.isFull()) {
                throw new IllegalStateException("Table is full");
            }
            playersBySession.remove(existingClient.getSessionId());
            existingClient.setSessionId(sessionId);
            if (!alreadySeated) {
                table.sitPlayer(existingClient);
            }
            playersBySession.put(sessionId, existingClient);
            log.info("Player {} resumed table session", nickname);
            return existingClient;
        }
        boolean nicknameReserved = playersByClientId.values().stream()
                .anyMatch(player -> player.getNickname().equalsIgnoreCase(nickname));
        if (nicknameReserved || table.getPlayerByNickname(nickname) != null) {
            throw new IllegalStateException("That nickname is already seated.");
        }
        if (table.isFull()) {
            throw new IllegalStateException("Table is full");
        }
        Player player = new Player(nickname, Table.DEFAULT_STACK, sessionId, table.getSeatCount());
        table.sitPlayer(player);
        playersBySession.put(sessionId, player);
        playersByClientId.put(clientId, player);
        log.info("Player {} joined table with session {}", nickname, sessionId);
        return player;
    }

    public synchronized boolean leaveTable(String sessionId) {
        Player player = playersBySession.remove(sessionId);
        if (player != null) {
            table.removePlayer(player);
            if (player.getStack().signum() > 0) {
                playersByClientId.values().removeIf(existing -> existing == player);
            }
            log.info("Player {} left table", player.getNickname());
            return true;
        }
        return false;
    }

    public synchronized Table getTableState() {
        return table;
    }

    public synchronized Map<String, Player> getPlayersBySession() {
        return playersBySession;
    }

    public synchronized Player getPlayerBySession(String sessionId) {
        return playersBySession.get(sessionId);
    }

    public synchronized BigDecimal getPot() {
        return table.getPot();
    }

    public synchronized void restoreStartingStacks() {
        Set<Player> players = Collections.newSetFromMap(new IdentityHashMap<>());
        players.addAll(table.getPlayers());
        players.addAll(playersByClientId.values());
        players.forEach(player -> player.setStack(Table.DEFAULT_STACK));
    }
}
