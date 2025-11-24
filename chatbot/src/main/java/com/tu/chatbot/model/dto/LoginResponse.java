package com.tu.chatbot.model.dto;

import java.util.Set;

public record LoginResponse(
        String username,
        Set<String> roles,
        String token,
        String tokenType,
        long expiresInSeconds
) {
}

