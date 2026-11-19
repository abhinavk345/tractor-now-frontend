package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.entity.Conversation;
import com.sita.enterprises.TractorNow.entity.Message;
import com.sita.enterprises.TractorNow.entity.User;
import com.sita.enterprises.TractorNow.enums.MessageStatus;
import com.sita.enterprises.TractorNow.enums.UserRole;
import com.sita.enterprises.TractorNow.repository.ConversationRepository;
import com.sita.enterprises.TractorNow.repository.MessageRepository;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl
        implements ChatService {

    private final ConversationRepository
            conversationRepository;

    private final MessageRepository
            messageRepository;

    private final UserRepository userRepository;

    @Override
    public Object createConversation(
            Long farmerId,
            Long ownerId
    ) {

        User farmer =
                findUser(farmerId);

        User owner =
                findUser(ownerId);

        if (farmer.getRole()
                != UserRole.FARMER) {

            throw new RuntimeException(
                    "Invalid farmer"
            );
        }

        if (owner.getRole()
                != UserRole.EQUIPMENT_OWNER) {

            throw new RuntimeException(
                    "Invalid equipment owner"
            );
        }

        Conversation conversation =
                Conversation.builder()
                        .farmer(farmer)
                        .owner(owner)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        return toConversationResponse(
                conversationRepository.save(
                        conversation
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Object getConversations(
            Long userId
    ) {

        return conversationRepository
                .findByFarmerIdOrOwnerId(
                        userId,
                        userId
                )
                .stream()
                .map(
                        this::toConversationResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMessages(
            Long conversationId
    ) {

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                )
                .stream()
                .map(
                        this::toMessageResponse
                )
                .toList();
    }

    @Override
    public Object sendMessage(
            Long conversationId,
            Long senderId,
            String message
    ) {

        Conversation conversation =
                conversationRepository
                        .findById(
                                conversationId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Conversation not found"
                                )
                        );

        User sender =
                findUser(senderId);

        if (!conversation.getFarmer()
                .getId()
                .equals(senderId)
                &&
                !conversation.getOwner()
                        .getId()
                        .equals(senderId)) {

            throw new RuntimeException(
                    "User is not part of this conversation"
            );
        }

        if (message == null
                || message.isBlank()) {

            throw new RuntimeException(
                    "Message cannot be empty"
            );
        }

        Message newMessage =
                Message.builder()
                        .conversation(conversation)
                        .sender(sender)
                        .message(message.trim())
                        .status(
                                MessageStatus.SENT
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        conversation.setUpdatedAt(
                LocalDateTime.now()
        );

        conversationRepository.save(
                conversation
        );

        return toMessageResponse(
                messageRepository.save(
                        newMessage
                )
        );
    }

    @Override
    public Object markMessageRead(
            Long messageId
    ) {

        Message message =
                messageRepository
                        .findById(messageId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Message not found"
                                )
                        );

        message.setStatus(
                MessageStatus.READ
        );

        return toMessageResponse(
                messageRepository.save(
                        message
                )
        );
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    private Map<String, Object>
    toConversationResponse(
            Conversation conversation
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                conversation.getId()
        );

        response.put(
                "farmerId",
                conversation.getFarmer().getId()
        );

        response.put(
                "farmerName",
                conversation.getFarmer()
                        .getFullName()
        );

        response.put(
                "ownerId",
                conversation.getOwner().getId()
        );

        response.put(
                "ownerName",
                conversation.getOwner()
                        .getFullName()
        );

        response.put(
                "updatedAt",
                conversation.getUpdatedAt()
        );

        return response;
    }

    private Map<String, Object>
    toMessageResponse(
            Message message
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                message.getId()
        );

        response.put(
                "conversationId",
                message.getConversation()
                        .getId()
        );

        response.put(
                "senderId",
                message.getSender().getId()
        );

        response.put(
                "senderName",
                message.getSender()
                        .getFullName()
        );

        response.put(
                "message",
                message.getMessage()
        );

        response.put(
                "status",
                message.getStatus()
        );

        response.put(
                "createdAt",
                message.getCreatedAt()
        );

        return response;
    }
}