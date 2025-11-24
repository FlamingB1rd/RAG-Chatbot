package com.tu.chatbot.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        String username,
        @NotBlank
        @Size(min = 6, max = 120)
        String password,
        @NotBlank
        @Email
        @Size(max = 100)
        String email
) {
}
