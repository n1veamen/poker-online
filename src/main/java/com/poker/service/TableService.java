package com.poker.service;

import com.poker.model.Player;
import com.poker.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TableService {
    private static final Logger log = LoggerFactory.getLogger(TableService.class);
    private final Map<String, Player> playersBySession = new ConcurrentHashMap<>();
    private final Table table = new Table();

    public synchronized Player joinTable(String nickname, String sessionId) {
        Player existingPlayer = playersBySession.get(sessionId);
        if (existingPlayer != null) {
            return existingPlayer;
        }
        if (table.isFull()) {
            throw new IllegalStateException("Table is full");
        }
        Player player = new Player(nickname, Table.DEFAULT_STACK, sessionId, table.getSeatCount());
        table.sitPlayer(player);
        playersBySession.put(sessionId, player);
        log.info("Player {} joined table with session {}", nickname, sessionId);
        return player;
    }

    public synchronized boolean leaveTable(String sessionId) {
        Player player = playersBySession.remove(sessionId);
        if (player != null) {
            table.removePlayer(player);
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

    public synchronized BigDecimal getPot() {
        return table.getPot();
    }
}
