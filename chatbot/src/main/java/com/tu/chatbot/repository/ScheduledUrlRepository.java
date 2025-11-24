package com.tu.chatbot.repository;

import com.tu.chatbot.model.ScheduledUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledUrlRepository extends JpaRepository<ScheduledUrl, Long> {
    List<ScheduledUrl> findByIsActiveTrueOrderByCreatedAtAsc();
    List<ScheduledUrl> findAllByOrderByCreatedAtDesc();
}

