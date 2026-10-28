package com.poker.service;

import com.poker.entity.ActionEntity;
import com.poker.entity.HandEntity;
import com.poker.entity.HandPlayerEntity;
import com.poker.model.Action;
import com.poker.model.Card;
import com.poker.model.Player;
import com.poker.repository.ActionRepository;
import com.poker.repository.HandPlayerRepository;
import com.poker.repository.HandRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class HistoryService {
    private final HandRepository handRepository;
    private final HandPlayerRepository handPlayerRepository;
    private final ActionRepository actionRepository;

    public HistoryService(HandRepository handRepository,
                          HandPlayerRepository handPlayerRepository,
                          ActionRepository actionRepository) {
        this.handRepository = handRepository;
        this.handPlayerRepository = handPlayerRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public HandEntity saveHand(List<Player> players, List<Card> board, BigDecimal potTotal, String winnerNickname) {
        HandEntity hand = new HandEntity();
        Instant now = Instant.now();
        hand.setStartedAt(now.minusSeconds(60));
        hand.setEndedAt(now);
        hand.setPotTotal(potTotal);
        hand.setBoardCards(board == null || board.isEmpty() ? "" : board.stream().map(Card::getCode).reduce((left, right) -> left + "," + right).orElse(""));
        hand.setWinnerNickname(winnerNickname);

        HandEntity savedHand = handRepository.save(hand);

        for (Player player : players) {
            HandPlayerEntity playerRecord = new HandPlayerEntity();
            playerRecord.setHand(savedHand);
            playerRecord.setNickname(player.getNickname());
            playerRecord.setStartingStack(player.getStack());
            playerRecord.setFinalStack(player.getStack());
            playerRecord.setHoleCards(player.getHoleCards() == null || player.getHoleCards().isEmpty()
                    ? "" : player.getHoleCards().stream().map(Card::getCode).reduce((left, right) -> left + "," + right).orElse(""));
            playerRecord.setFolded(player.isFolded());
            handPlayerRepository.save(playerRecord);
        }

        return savedHand;
    }

    @Transactional
    public void saveAction(Long handId, Action action) {
        HandEntity hand = handRepository.findById(handId).orElseThrow(() -> new IllegalArgumentException("Hand not found: " + handId));
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setHand(hand);
        actionEntity.setNickname(action.getPlayerNickname());
        actionEntity.setStreet(action.getStreet().name());
        actionEntity.setActionType(action.getActionType().name());
        actionEntity.setAmount(action.getAmount());
        actionEntity.setCreatedAt(action.getCreatedAt() == null ? Instant.now() : action.getCreatedAt());
        actionRepository.save(actionEntity);
    }
}
