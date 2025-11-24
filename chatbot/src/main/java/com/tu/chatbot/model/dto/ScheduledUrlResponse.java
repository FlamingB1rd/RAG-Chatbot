package com.tu.chatbot.model.dto;

import java.time.LocalDateTime;

public record ScheduledUrlResponse(
        Long id,
        String url,
        String description,
        LocalDateTime createdAt,
        String createdBy,
        Boolean isActive
) {
}

