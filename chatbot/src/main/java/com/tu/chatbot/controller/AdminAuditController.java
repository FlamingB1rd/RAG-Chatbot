package com.tu.chatbot.controller;

import com.tu.chatbot.model.AuditLog;
import com.tu.chatbot.model.dto.AuditLogResponse;
import com.tu.chatbot.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAuditController {
    private final AuditLogService auditLogService;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs() {
        List<AuditLog> logs = auditLogService.getAllLogs();
        List<AuditLogResponse> responses = logs.stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getActionType(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDescription(),
                        log.getPerformedBy(),
                        log.getPerformedAt(),
                        log.getOldValue(),
                        log.getNewValue()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}

