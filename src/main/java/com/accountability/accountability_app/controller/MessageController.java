package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.model.Message;
import com.accountability.accountability_app.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 📩 Send a message
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request) {
        Message message = messageService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getContent());
        return ResponseEntity.ok(message);
    }

    // 📜 Get chat history between two users
    @GetMapping("/{user1Id}/{user2Id}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        List<Message> messages = messageService.getChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(messages);
    }

    // **Start SSE Connection**
    @GetMapping("/stream/{userId}")
    public SseEmitter streamMessages(@PathVariable Long userId) {
        return messageService.connect(userId);
    }
}

// DTO for message requests
class MessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;

    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public String getContent() { return content; }
}
