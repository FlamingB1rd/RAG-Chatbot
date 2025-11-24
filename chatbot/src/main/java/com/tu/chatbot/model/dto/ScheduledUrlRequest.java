package com.tu.chatbot.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScheduledUrlRequest(
        @NotBlank
        @Size(max = 1000)
        String url,
        @Size(max = 500)
        String description
) {
}

