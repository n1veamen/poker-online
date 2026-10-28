package com.poker.web;

import com.poker.dto.ChatMessageDto;
import com.poker.dto.JoinRequestDto;
import com.poker.dto.PlayerActionDto;
import com.poker.dto.PlayerDto;
import com.poker.dto.TableStateDto;
import com.poker.model.ActionType;
import com.poker.model.Player;
import com.poker.model.Table;
import com.poker.service.GameService;
import com.poker.service.TableService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final TableService tableService;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(TableService tableService, GameService gameService,
                               SimpMessagingTemplate messagingTemplate) {
        this.tableService = tableService;
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/table.join")
    public void joinTable(
            @Payload JoinRequestDto request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        if (request == null || request.getNickname() == null || request.getNickname().isBlank()) {
            throw new IllegalArgumentException("Enter a nickname first");
        }
        String nickname = request.getNickname().trim();
        if (nickname.length() > 20) {
            throw new IllegalArgumentException("Nickname must be 20 characters or fewer");
        }
        if (request.getClientId() == null || request.getClientId().isBlank() || request.getClientId().length() > 80) {
            throw new IllegalArgumentException("Browser session is missing. Refresh the page and try again.");
        }
        tableService.joinTable(nickname, sessionId, request.getClientId());
        broadcastTableState();
    }

    @MessageExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @SendToUser(value = "/queue/private", broadcast = false)
    public Map<String, String> handleJoinError(RuntimeException exception) {
        return Map.of("type", "error", "message", exception.getMessage());
    }

    @MessageMapping("/table.startHand")
    public void startHand(@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        gameService.startNewHand(sessionId);
        broadcastTableState();
    }

    @MessageMapping("/table.privateCards")
    @SendToUser(value = "/queue/private", broadcast = false)
    public Map<String, Object> privateCards(
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        return Map.of(
                "type", "your_cards",
                "handNumber", gameService.getHandNumber(),
                "cards", gameService.getPrivateCards(sessionId));
    }

    @MessageMapping("/action")
    public void handleAction(
            @Payload PlayerActionDto request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        if (request == null || request.getAction() == null) {
            throw new IllegalArgumentException("Choose a poker action.");
        }
        ActionType actionType;
        try {
            actionType = ActionType.valueOf(request.getAction());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown poker action.");
        }
        gameService.processAction(sessionId, actionType, request.getAmount());
        broadcastTableState();
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        if (gameService.leaveSession(event.getSessionId())) {
            broadcastTableState();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void handleTurnTimeout() {
        boolean timedOut = gameService.autoFoldExpiredTurn();
        if (timedOut || gameService.isHandInProgress()) {
            broadcastTableState();
        }
    }

    @MessageMapping("/chat")
    public void sendChat(@Payload ChatMessageDto chatMessage) {
        messagingTemplate.convertAndSend("/topic/chat", chatMessage);
    }

    public TableStateDto buildTableState() {
        Table table = tableService.getTableState();
        List<PlayerDto> players = table.getPlayers().stream()
                .map(player -> {
                    boolean revealCards = !gameService.isHandInProgress()
                            && table.getCurrentStreet() == com.poker.model.Street.SHOWDOWN
                            && !player.isFolded()
                            && gameService.isInCurrentHand(player);
                    PlayerDto dto = new PlayerDto(
                        player.getNickname(),
                        player.getStack(),
                        player.getBet(),
                        player.isFolded(),
                        player.isAllIn(),
                        player.isDealer(),
                        player.isActive(),
                        player.getSeatIndex(),
                        revealCards ? player.getHoleCards().stream().map(card -> card.getCode()).toList() : List.of());
                    dto.setInHand(gameService.isInCurrentHand(player));
                    return dto;
                })
                .toList();

        TableStateDto state = new TableStateDto(
                "table-1",
                Table.MAX_SEATS,
                table.getDealerIndex(),
                table.getSmallBlindIndex(),
                table.getBigBlindIndex(),
                table.getPot(),
                table.getCurrentStreet().name(),
                players,
                table.getBoard().stream().map(card -> card.getCode()).toList()
        );
        state.setCurrentActorSeat(gameService.getCurrentActorSeat());
        state.setCurrentBet(gameService.getCurrentBet());
        state.setMinRaise(gameService.getMinimumRaise());
        state.setHandInProgress(gameService.isHandInProgress());
        state.setStatusMessage(gameService.getStatusMessage());
        state.setTurnSecondsRemaining(gameService.getTurnSecondsRemaining());
        state.setHandNumber(gameService.getHandNumber());
        return state;
    }

    private void broadcastTableState() {
        messagingTemplate.convertAndSend("/topic/table", buildTableState());
    }
}
