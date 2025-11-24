package com.tu.chatbot.model.dto;

public record ChatRequest(
        String question,
        Long conversationId
) {}

