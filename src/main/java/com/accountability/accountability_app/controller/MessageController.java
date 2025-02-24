package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.model.Message;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import com.accountability.accountability_app.security.JwtUtil;
import com.accountability.accountability_app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 📩 Send a message
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request) {
        System.out.println("📤 Received message request from " + request.getSenderId() + " to " + request.getReceiverId());
        Message message = messageService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getContent());
        System.out.println("✅ Message sent successfully: " + message.getContent());
        return ResponseEntity.ok(message);
    }

    // 📜 Get chat history between two users
    @GetMapping("/{user1Id}/{user2Id}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        System.out.println("📜 Fetching chat history between " + user1Id + " and " + user2Id);
        List<Message> messages = messageService.getChatHistory(user1Id, user2Id);
        System.out.println("✅ Retrieved " + messages.size() + " messages.");
        return ResponseEntity.ok(messages);
    }

    // **Start SSE Connection**
    @GetMapping("/stream/{userId}")
    public SseEmitter streamMessages(@PathVariable Long userId, @RequestParam("token") String token) {
        System.out.println("🔄 Starting SSE connection for user ID: " + userId);

        String email;
        try {
            email = jwtUtil.extractEmail(token);
            System.out.println("📧 Extracted email from token: " + email);
        } catch (Exception e) {
            System.out.println("❌ Failed to extract email from token: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Token");
        }

        if (!jwtUtil.validateToken(token, email)) {
            System.out.println("❌ Token validation failed for email: " + email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ No user found for email: " + email);
                    return new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
                });

        if (!user.getId().equals(userId)) {
            System.out.println("❌ User ID mismatch! Token belongs to " + user.getId() + " but request is for " + userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User ID does not match token");
        }

        System.out.println("✅ SSE connection established for user ID: " + userId);
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
