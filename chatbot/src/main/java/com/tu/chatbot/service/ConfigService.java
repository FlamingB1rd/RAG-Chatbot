package com.tu.chatbot.service;

import com.tu.chatbot.model.ChatbotConfig;
import com.tu.chatbot.repository.ChatbotConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ChatbotConfigRepository configRepository;
    private ChatbotConfig config;

    @PostConstruct
    @Transactional
    public void init() {
        loadConfig();
    }

    @Transactional
    public void loadConfig() {
        config = configRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    ChatbotConfig defaultConfig = new ChatbotConfig();
                    defaultConfig.setTopK(5);
                    defaultConfig.setSimilarityThreshold(0.65);
                    defaultConfig.setCronExpression("0 0 9 ? * FRI"); // Default: Every Friday at 9:00 AM
                    ChatbotConfig saved = configRepository.save(defaultConfig);
                    return saved;
                });
    }

    public int getTopK() {
        if (config == null) {
            loadConfig();
        }
        return config.getTopK();
    }

    public double getSimilarityThreshold() {
        if (config == null) {
            loadConfig();
        }
        return config.getSimilarityThreshold();
    }

    @Transactional
    public void updateConfig(int topK, double similarityThreshold, String updatedBy) {
        if (config == null) {
            loadConfig();
        }
        config.setTopK(topK);
        config.setSimilarityThreshold(similarityThreshold);
        config.setUpdatedBy(updatedBy);
        configRepository.save(config);
    }

    @Transactional
    public void updateCronExpression(String cronExpression, String updatedBy) {
        if (config == null) {
            loadConfig();
        }
        config.setCronExpression(cronExpression);
        config.setUpdatedBy(updatedBy);
        configRepository.save(config);
    }

    public String getCronExpression() {
        if (config == null) {
            loadConfig();
        }
        return config.getCronExpression() != null ? config.getCronExpression() : "0 0 9 ? * FRI";
    }

    public ConfigResponse getConfig() {
        if (config == null) {
            loadConfig();
        }
        return new ConfigResponse(config.getTopK(), config.getSimilarityThreshold(), config.getCronExpression());
    }

    public record ConfigResponse(int topK, double similarityThreshold, String cronExpression) {}
}

