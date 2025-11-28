package com.tu.chatbot.config;

import com.tu.chatbot.model.ChatbotConfig;
import com.tu.chatbot.repository.ChatbotConfigRepository;
import com.tu.chatbot.repository.FaqRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializes database tables by accessing repositories.
 * This ensures Hibernate creates the tables from JPA entities.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {
    private final FaqRepository faqRepository;
    private final ChatbotConfigRepository configRepository;

    @PostConstruct
    @Transactional
    public void initialize() {
        try {
            log.info("Initializing database tables...");

            if (configRepository.findFirstByOrderByIdAsc().isEmpty()) {
                log.info("Creating default chatbot config...");
                ChatbotConfig defaultConfig = new ChatbotConfig();
                defaultConfig.setTopK(5);
                defaultConfig.setSimilarityThreshold(0.65);
                configRepository.save(defaultConfig);
                log.info("Default chatbot config created successfully");
            }

            faqRepository.count();
            log.info("Database tables initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing database tables", e);
            throw e;
        }
    }
}

