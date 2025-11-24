package com.tu.chatbot.controller;

import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.service.AuditLogService;
import com.tu.chatbot.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminConfigController {

    private final ConfigService configService;
    private final AuditLogService auditLogService;

    @Autowired
    public AdminConfigController(ConfigService configService, AuditLogService auditLogService) {
        this.configService = configService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/config")
    public ResponseEntity<ConfigService.ConfigResponse> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PostMapping("/config")
    public ResponseEntity<ConfigService.ConfigResponse> updateConfig(
            @RequestBody ConfigRequest request,
            Authentication authentication) {
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        
        // Get old config for logging
        ConfigService.ConfigResponse oldConfig = configService.getConfig();
        String oldValue = String.format("topK=%d, similarityThreshold=%.2f, cronExpression=%s", 
                oldConfig.topK(), oldConfig.similarityThreshold(), oldConfig.cronExpression());
        String newValue = String.format("topK=%d, similarityThreshold=%.2f, cronExpression=%s", 
                request.topK(), request.similarityThreshold(), request.cronExpression());
        
        configService.updateConfig(request.topK(), request.similarityThreshold(), username);
        if (request.cronExpression() != null && !request.cronExpression().isEmpty()) {
            configService.updateCronExpression(request.cronExpression(), username);
        }
        
        // Log the change
        auditLogService.log(
            "CONFIG_UPDATE",
            "CONFIG",
            1L,
            String.format("Updated chatbot config: topK=%d, similarityThreshold=%.2f, cronExpression=%s", 
                    request.topK(), request.similarityThreshold(), request.cronExpression()),
            username,
            oldValue,
            newValue
        );
        
        return ResponseEntity.ok(configService.getConfig());
    }

    record ConfigRequest(int topK, double similarityThreshold, String cronExpression) {}
}

