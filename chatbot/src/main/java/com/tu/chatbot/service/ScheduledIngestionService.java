package com.tu.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledIngestionService {
    private final ScheduledUrlService scheduledUrlService;
    private final DataIngestionService dataIngestionService;

    /**
     * Scheduled job that runs based on the cron expression stored in the database.
     * The cron expression is read from ChatbotConfig and can be updated by admin.
     * Note: This uses a fixed cron expression. For dynamic cron, we'd need a TaskScheduler.
     * For now, we'll use a default schedule and allow manual triggering.
     */
    @Scheduled(cron = "0 0 9 ? * FRI") // Default: Every Friday at 9:00 AM
    @Transactional
    public void ingestScheduledUrls() {
        log.info("Starting scheduled URL ingestion job");
        
        try {
            List<com.tu.chatbot.model.ScheduledUrl> activeUrls = scheduledUrlService.getActiveScheduledUrls();
            
            if (activeUrls.isEmpty()) {
                log.info("No active scheduled URLs found. Skipping ingestion.");
                return;
            }

            log.info("Found {} active scheduled URLs to ingest", activeUrls.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (com.tu.chatbot.model.ScheduledUrl scheduledUrl : activeUrls) {
                try {
                    log.info("Processing scheduled URL: {}", scheduledUrl.getUrl());
                    boolean urlExisted = dataIngestionService.existsByUrl(scheduledUrl.getUrl());
                    String existingContent = urlExisted ? dataIngestionService.getContentByUrl(scheduledUrl.getUrl()) : null;
                    
                    dataIngestionService.ingestFromUrl(scheduledUrl.getUrl());
                    
                    // Log whether content was updated or added
                    if (urlExisted && existingContent != null && !existingContent.isEmpty()) {
                        String newContent = dataIngestionService.getContentByUrl(scheduledUrl.getUrl());
                        String normalizedExisting = existingContent.trim().replaceAll("\\s+", " ");
                        String normalizedNew = newContent.trim().replaceAll("\\s+", " ");
                        if (!normalizedExisting.equals(normalizedNew)) {
                            log.info("Content updated for URL: {}", scheduledUrl.getUrl());
                        } else {
                            log.info("Content unchanged for URL: {} (no update needed)", scheduledUrl.getUrl());
                        }
                    } else {
                        log.info("New content added for URL: {}", scheduledUrl.getUrl());
                    }
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to ingest URL: {}. Error: {}", scheduledUrl.getUrl(), e.getMessage(), e);
                }
            }
            
            log.info("Scheduled ingestion job completed. Success: {}, Failures: {}", successCount, failureCount);
        } catch (Exception e) {
            log.error("Error during scheduled URL ingestion job", e);
        }
    }
}

