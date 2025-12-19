package com.chat.chat_java.controller;





import com.chat.chat_java.dto.ChatMessageDTO;
import com.chat.chat_java.model.Message;
import com.chat.chat_java.model.User;
import com.chat.chat_java.repository.MessageRepository;
import com.chat.chat_java.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserService userService;
    
    private final Map<String, String> userSessions = new HashMap<>();
    
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage, Principal principal) {
        String sender = principal.getName();
        log.info("📨 {} -> {}: {}", sender, chatMessage.getReceiver(), chatMessage.getContent());
        
        // Salva no banco
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(chatMessage.getReceiver());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        
        // Atualiza DTO com remetente real
        chatMessage.setSender(sender);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        return chatMessage;
    }
    
    @MessageMapping("/chat.join")
    @SendTo("/topic/online")
    public Map<String, Object> addUser(Principal principal) {
        String username = principal.getName();
        
        // Atualiza status online
        userService.setOnlineStatus(username, true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "USER_JOINED");
        response.put("username", username);
        response.put("timestamp", LocalDateTime.now());
        
        log.info("👤 {} entrou no chat", username);
        return response;
    }
    
    @MessageMapping("/chat.leave")
    @SendTo("/topic/online")
    public Map<String, Object> removeUser(Principal principal) {
        String username = principal.getName();
        
        // Atualiza status offline
        userService.setOnlineStatus(username, false);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "USER_LEFT");
        response.put("username", username);
        response.put("timestamp", LocalDateTime.now());
        
        log.info("👤 {} saiu do chat", username);
        return response;
    }
    
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessageDTO chatMessage, Principal principal) {
        String sender = principal.getName();
        
        log.info("🔒 {} -> {}: {}", sender, chatMessage.getReceiver(), chatMessage.getContent());
        
        // Salva no banco
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(chatMessage.getReceiver());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        
        chatMessage.setSender(sender);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Envia para destinatário
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiver(),
            "/queue/private",
            chatMessage
        );
        
        // Envia cópia para remetente (confirmação)
        messagingTemplate.convertAndSendToUser(
            sender,
            "/queue/private",
            chatMessage
        );
    }
    
    @SubscribeMapping("/topic/online")
    public List<User> getOnlineUsers() {
        // Retorna lista de usuários online (você precisa implementar este método no UserService)
        return List.of(); // Placeholder
    }
}