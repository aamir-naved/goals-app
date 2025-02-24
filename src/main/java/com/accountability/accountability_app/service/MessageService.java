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

    // Store active SSE connections
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Save and notify recipient
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message message = new Message(senderId, receiverId, content);
        message = messageRepository.save(message);

        // Notify the receiver if they have an active SSE connection
        SseEmitter emitter = emitters.get(receiverId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitters.remove(receiverId);
            }
        }
        return message;
    }

    // Get chat history
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestamp(
                user1Id, user2Id, user2Id, user1Id
        );
    }

    // Start SSE connection
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }
}
