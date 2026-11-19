package com.sita.enterprises.TractorNow.repository;

import com.sita.enterprises.TractorNow.entity.Message;
import com.sita.enterprises.TractorNow.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    List<Message> findByConversationIdAndSenderIdOrderByCreatedAtAsc(
            Long conversationId,
            Long senderId
    );

    long countByConversationIdAndStatus(
            Long conversationId,
            MessageStatus status
    );

    List<Message> findByConversationIdAndStatus(
            Long conversationId,
            MessageStatus status
    );
}