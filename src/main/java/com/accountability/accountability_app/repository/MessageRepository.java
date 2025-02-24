package com.accountability.accountability_app.repository;

import com.accountability.accountability_app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestamp(
        Long senderId, Long receiverId, Long receiverId2, Long senderId2
    );
}
