package com.tu.chatbot.service;

import com.tu.chatbot.model.ChatMessage;
import com.tu.chatbot.model.Conversation;
import com.tu.chatbot.model.dto.ChatMessageResponse;
import com.tu.chatbot.model.dto.ConversationDetailResponse;
import com.tu.chatbot.model.dto.ConversationResponse;
import com.tu.chatbot.repository.ChatMessageRepository;
import com.tu.chatbot.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ConversationResponse createConversation(Long userId, String title) {
        val conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title != null && !title.trim().isEmpty() ? title : "New Conversation");
        val saved = conversationRepository.save(conversation);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversation(Long conversationId, Long userId) {
        val conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        val messages = chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
        
        return new ConversationDetailResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messages
        );
    }

    @Transactional
    public ChatMessageResponse addMessage(Long conversationId, Long userId, String role, String content) {
        val conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        val message = new ChatMessage();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);

        if ("user".equals(role) && conversation.getTitle().equals("New Conversation")) {
            String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
            conversation.setTitle(title);
        }
        
        val saved = chatMessageRepository.save(message);
        return toMessageResponse(saved);
    }

    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        val conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        conversationRepository.delete(conversation);
    }

    private ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

