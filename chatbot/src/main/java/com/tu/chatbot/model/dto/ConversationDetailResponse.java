package com.tu.chatbot.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationDetailResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ChatMessageResponse> messages
) {}

