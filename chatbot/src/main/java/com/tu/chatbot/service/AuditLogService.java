package com.tu.chatbot.service;

import com.tu.chatbot.model.AuditLog;
import com.tu.chatbot.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String actionType, String entityType, Long entityId, String description, String performedBy) {
        log(actionType, entityType, entityId, description, performedBy, null, null);
    }

    @Transactional
    public void log(String actionType, String entityType, Long entityId, String description, String performedBy, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setPerformedBy(performedBy);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop50ByOrderByPerformedAtDesc();
    }
}

