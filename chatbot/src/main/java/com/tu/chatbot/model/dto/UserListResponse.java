package com.tu.chatbot.model.dto;

import java.util.Set;

public record UserListResponse(Long id, String username, String email, Set<String> roles) {
}

