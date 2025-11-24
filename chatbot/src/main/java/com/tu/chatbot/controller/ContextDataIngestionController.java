package com.tu.chatbot.controller;

import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.service.AuditLogService;
import com.tu.chatbot.service.DataIngestionService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@PreAuthorize("hasRole('ADMIN')")
public class ContextDataIngestionController {
    DataIngestionService dataIngestionService;
    AuditLogService auditLogService;

    @Autowired
    public ContextDataIngestionController(DataIngestionService dataIngestionService, AuditLogService auditLogService) {
        this.dataIngestionService = dataIngestionService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingestDataFromUrl(@RequestBody @NotNull IngestUrl req) {
        dataIngestionService.ingestFromUrl(req.url);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/urls")
    public ResponseEntity<List<String>> getAllUrls() {
        return ResponseEntity.ok(dataIngestionService.getAllUrls());
    }

    @GetMapping("/url")
    public ResponseEntity<UrlContentResponse> getContentByUrl(@RequestParam String url) {
        val content = dataIngestionService.getContentByUrl(url);
        return ResponseEntity.ok(new UrlContentResponse(url, content));
    }

    @DeleteMapping("/url")
    public ResponseEntity<String> deleteByUrl(
            @RequestParam String url,
            Authentication authentication) {
        dataIngestionService.deleteByUrl(url);
        
        // Log the deletion
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "URL_DELETE",
            "URL",
            null,
            String.format("Deleted context for URL: %s", url),
            username,
            url,
            null
        );
        
        return ResponseEntity.ok("Deleted context for URL: " + url);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<String> upgrade() {
        // TODO: Implement upgrade logic (e.g., re-embedding all documents)
        return ResponseEntity.ok("Upgrade functionality not yet implemented");
    }

    record IngestUrl(String url) {}
    record UrlContentResponse(String url, String content) {}
}
