package com.tu.chatbot.model.dto;

public record FaqResponse(
        Long id,
        String question,
        String answer
) {}

