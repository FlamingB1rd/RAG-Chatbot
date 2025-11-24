package com.tu.chatbot.service;

import com.tu.chatbot.model.ScheduledUrl;
import com.tu.chatbot.model.dto.ScheduledUrlRequest;
import com.tu.chatbot.model.dto.ScheduledUrlResponse;
import com.tu.chatbot.repository.ScheduledUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledUrlService {
    private final ScheduledUrlRepository scheduledUrlRepository;

    @Transactional(readOnly = true)
    public List<ScheduledUrlResponse> getAllScheduledUrls() {
        return scheduledUrlRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduledUrl> getActiveScheduledUrls() {
        return scheduledUrlRepository.findByIsActiveTrueOrderByCreatedAtAsc();
    }

    @Transactional
    public ScheduledUrlResponse createScheduledUrl(ScheduledUrlRequest request, String createdBy) {
        ScheduledUrl scheduledUrl = new ScheduledUrl();
        scheduledUrl.setUrl(request.url());
        scheduledUrl.setDescription(request.description());
        scheduledUrl.setCreatedBy(createdBy);
        scheduledUrl.setIsActive(true);
        
        ScheduledUrl saved = scheduledUrlRepository.save(scheduledUrl);
        return toResponse(saved);
    }

    @Transactional
    public void deleteScheduledUrl(Long id) {
        scheduledUrlRepository.deleteById(id);
    }

    @Transactional
    public ScheduledUrlResponse toggleScheduledUrl(Long id) {
        ScheduledUrl scheduledUrl = scheduledUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheduled URL not found with id: " + id));
        scheduledUrl.setIsActive(!scheduledUrl.getIsActive());
        ScheduledUrl saved = scheduledUrlRepository.save(scheduledUrl);
        return toResponse(saved);
    }

    private ScheduledUrlResponse toResponse(ScheduledUrl scheduledUrl) {
        return new ScheduledUrlResponse(
                scheduledUrl.getId(),
                scheduledUrl.getUrl(),
                scheduledUrl.getDescription(),
                scheduledUrl.getCreatedAt(),
                scheduledUrl.getCreatedBy(),
                scheduledUrl.getIsActive()
        );
    }
}

