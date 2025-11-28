package com.tu.chatbot.controller;

import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.model.dto.ScheduledUrlRequest;
import com.tu.chatbot.model.dto.ScheduledUrlResponse;
import com.tu.chatbot.service.AuditLogService;
import com.tu.chatbot.service.ScheduledUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/scheduled-urls")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminScheduledUrlController {
    private final ScheduledUrlService scheduledUrlService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<ScheduledUrlResponse>> getAllScheduledUrls() {
        return ResponseEntity.ok(scheduledUrlService.getAllScheduledUrls());
    }

    @PostMapping
    public ResponseEntity<ScheduledUrlResponse> createScheduledUrl(
            @RequestBody @Valid ScheduledUrlRequest request,
            Authentication authentication) {
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        ScheduledUrlResponse response = scheduledUrlService.createScheduledUrl(request, username);

        auditLogService.log(
            "SCHEDULED_URL_CREATE",
            "SCHEDULED_URL",
            response.id(),
            String.format("Created scheduled URL: %s", request.url()),
            username,
            null,
            String.format("url=%s, description=%s", request.url(), request.description())
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScheduledUrl(
            @PathVariable Long id,
            Authentication authentication) {
        ScheduledUrlResponse scheduledUrl = scheduledUrlService.getAllScheduledUrls().stream()
                .filter(url -> url.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Scheduled URL not found"));

        scheduledUrlService.deleteScheduledUrl(id);

        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "SCHEDULED_URL_DELETE",
            "SCHEDULED_URL",
            id,
            String.format("Deleted scheduled URL: %s", scheduledUrl.url()),
            username,
            String.format("url=%s, description=%s", scheduledUrl.url(), scheduledUrl.description()),
            null
        );

        return ResponseEntity.ok("Scheduled URL deleted successfully");
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ScheduledUrlResponse> toggleScheduledUrl(
            @PathVariable Long id,
            Authentication authentication) {
        ScheduledUrlResponse oldUrl = scheduledUrlService.getAllScheduledUrls().stream()
                .filter(url -> url.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Scheduled URL not found"));

        ScheduledUrlResponse response = scheduledUrlService.toggleScheduledUrl(id);

        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "SCHEDULED_URL_TOGGLE",
            "SCHEDULED_URL",
            id,
            String.format("%s scheduled URL: %s", response.isActive() ? "Activated" : "Deactivated", response.url()),
            username,
            String.format("isActive=%s", oldUrl.isActive()),
            String.format("isActive=%s", response.isActive())
        );

        return ResponseEntity.ok(response);
    }
}

