
package com.sita.enterprises.TractorNow.service;

public interface ChatService {

    // Create a conversation between farmer and equipment owner
    Object createConversation(
            Long farmerId,
            Long ownerId
    );

    // Get all conversations for a user
    Object getConversations(
            Long userId
    );

    // Get all messages in a conversation
    Object getMessages(
            Long conversationId
    );

    // Send a message in a conversation
    Object sendMessage(
            Long conversationId,
            Long senderId,
            String message
    );

    // Mark a message as read
    Object markMessageRead(
            Long messageId
    );
}
