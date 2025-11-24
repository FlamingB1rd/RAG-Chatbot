package com.tu.chatbot.repository;

import com.tu.chatbot.model.ChatbotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatbotConfigRepository extends JpaRepository<ChatbotConfig, Long> {
    // We'll always have a single config row with id=1
    Optional<ChatbotConfig> findFirstByOrderByIdAsc();
}

