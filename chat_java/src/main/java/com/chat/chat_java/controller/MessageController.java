package com.chat.chat_java.controller;



import com.chat.chat_java.model.Message;
import com.chat.chat_java.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MessageController {
    
    private final MessageRepository repository;
    
    @PostMapping
    public Message send(@RequestBody Message message) {
        return repository.save(message);
    }
    
    @GetMapping
    public List<Message> getAll() {
        return repository.findAll();
    }
    
    @GetMapping("/from/{sender}")
    public List<Message> getFrom(@PathVariable String sender) {
        return repository.findBySender(sender);
    }
    
    @GetMapping("/to/{receiver}")
    public List<Message> getTo(@PathVariable String receiver) {
        return repository.findByReceiver(receiver);
    }
}