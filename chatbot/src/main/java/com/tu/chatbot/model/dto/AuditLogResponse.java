package com.tu.chatbot.model.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actionType,
        String entityType,
        Long entityId,
        String description,
        String performedBy,
        LocalDateTime performedAt,
        String oldValue,
        String newValue
) {
}

