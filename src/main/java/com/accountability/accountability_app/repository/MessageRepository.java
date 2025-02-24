package com.accountability.accountability_app.repository;

import com.accountability.accountability_app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
//    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestamp(
//        Long senderId, Long receiverId, Long receiverId2, Long senderId2
//    );
@Query("SELECT m FROM Message m WHERE (m.senderId = :userId AND m.receiverId = :partnerId) OR (m.senderId = :partnerId AND m.receiverId = :userId) ORDER BY m.timestamp ASC")
List<Message> findChatHistory(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

}
