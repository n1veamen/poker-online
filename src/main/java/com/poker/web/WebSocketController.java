package com.poker.web;

import com.poker.dto.ChatMessageDto;
import com.poker.dto.JoinRequestDto;
import com.poker.dto.PlayerDto;
import com.poker.dto.TableStateDto;
import com.poker.model.Table;
import com.poker.service.TableService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final TableService tableService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(TableService tableService, SimpMessagingTemplate messagingTemplate) {
        this.tableService = tableService;
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
        tableService.joinTable(nickname, sessionId);
        messagingTemplate.convertAndSend("/topic/table", buildTableState());
    }

    @MessageExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @SendToUser(value = "/queue/private", broadcast = false)
    public Map<String, String> handleJoinError(RuntimeException exception) {
        return Map.of("type", "error", "message", exception.getMessage());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        if (tableService.leaveTable(event.getSessionId())) {
            messagingTemplate.convertAndSend("/topic/table", buildTableState());
        }
    }

    @MessageMapping("/chat")
    public void sendChat(@Payload ChatMessageDto chatMessage) {
        messagingTemplate.convertAndSend("/topic/chat", chatMessage);
    }

    @MessageMapping("/action")
    public void handleAction(@Payload String payload) {
        messagingTemplate.convertAndSend("/topic/table", buildTableState());
    }

    public TableStateDto buildTableState() {
        Table table = tableService.getTableState();
        List<PlayerDto> players = table.getPlayers().stream()
                .map(player -> new PlayerDto(
                        player.getNickname(),
                        player.getStack(),
                        player.getBet(),
                        player.isFolded(),
                        player.isAllIn(),
                        player.isDealer(),
                        player.isActive(),
                        player.getSeatIndex(),
                        player.getHoleCards().stream().map(card -> card.getCode()).toList()))
                .toList();

        return new TableStateDto(
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
    }
}
