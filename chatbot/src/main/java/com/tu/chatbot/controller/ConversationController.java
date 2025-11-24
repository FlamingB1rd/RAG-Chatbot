package com.tu.chatbot.controller;

import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.model.dto.ConversationDetailResponse;
import com.tu.chatbot.model.dto.ConversationResponse;
import com.tu.chatbot.model.dto.CreateConversationRequest;
import com.tu.chatbot.service.ConversationService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class ConversationController {

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestBody(required = false) CreateConversationRequest request,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        String title = request != null ? request.title() : null;
        val conversation = conversationService.createConversation(userId, title);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getUserConversations(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        val conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable Long id,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        val conversation = conversationService.getConversation(id, userId);
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long id,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        conversationService.deleteConversation(id, userId);
        return ResponseEntity.noContent().build();
    }
}

