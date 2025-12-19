package com.chat.chat_java.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private String type;        // "CHAT", "JOIN", "LEAVE"
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    
    public ChatMessageDTO() {
        this.timestamp = LocalDateTime.now();
    }
}