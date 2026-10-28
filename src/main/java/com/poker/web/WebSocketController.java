package com.poker.web;

import com.poker.dto.ChatMessageDto;
import com.poker.dto.PlayerDto;
import com.poker.dto.TableStateDto;
import com.poker.model.Player;
import com.poker.model.Table;
import com.poker.service.TableService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
    public void joinTable(@Payload String nickname) {
        String sessionId = tableService.createSessionId();
        Player player = tableService.joinTable(nickname, sessionId);
        messagingTemplate.convertAndSend("/topic/table", buildTableState());
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/private", Map.of("type", "player_joined", "player", player.getNickname()));
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
