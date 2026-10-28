package com.poker.dto;

public class ChatMessageDto {
    private String nickname;
    private String message;
    private String timestamp;

    public ChatMessageDto() {
    }

    public ChatMessageDto(String nickname, String message, String timestamp) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
