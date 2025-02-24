package com.accountability.accountability_app.service;

import com.accountability.accountability_app.model.Message;
import com.accountability.accountability_app.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        System.out.println("Sending message from " + senderId + " to " + receiverId);
        Message message = new Message(senderId, receiverId, content);
        message = messageRepository.save(message);

        SseEmitter emitter = emitters.get(receiverId);
        if (emitter != null) {
            try {
                System.out.println("Notifying receiver " + receiverId);
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                System.out.println("Error sending SSE notification to " + receiverId + ", removing emitter.");
                emitter.complete();
                emitters.remove(receiverId);
            }
        }
        return message;
    }

    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        System.out.println("Fetching chat history between " + user1Id + " and " + user2Id);
        return messageRepository.findChatHistory(user1Id,user2Id);
    }

//    public SseEmitter connect(Long userId) {
//        System.out.println("Establishing SSE connection for user " + userId);
//        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
//        emitters.put(userId, emitter);
//
//        emitter.onCompletion(() -> {
//            System.out.println("SSE connection completed for user " + userId);
//            emitters.remove(userId);
//        });
//
//        emitter.onTimeout(() -> {
//            System.out.println("SSE connection timed out for user " + userId);
//            emitter.complete();
//            emitters.remove(userId);
//        });
//
//        emitter.onError((ex) -> {
//            System.out.println("SSE connection error for user " + userId + ": " + ex.getMessage());
//            emitter.complete();
//            emitters.remove(userId);
//        });
//
//        return emitter;
//    }
}
