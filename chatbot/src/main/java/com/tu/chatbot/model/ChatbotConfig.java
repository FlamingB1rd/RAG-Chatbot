package com.tu.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "top_k", nullable = false, columnDefinition = "INTEGER DEFAULT 5")
    private Integer topK = 5;

    @Column(name = "similarity_threshold", nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.65")
    private Double similarityThreshold = 0.65;

    @Column(name = "cron_expression", length = 100)
    private String cronExpression = "0 0 9 ? * FRI"; // Default: Every Friday at 9:00 AM

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

