package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.model.entity.LogEntry;
import org.cocreate.CoCreate.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LogService {
    private final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<LogEntry> getAllByUserID(String userId){
        return logRepository.findAllByUserId(userId);
    }

    public void logInfo(String message, String userId, String entityId, String entityType, Object details) {
        saveLog("INFO", message, userId, entityId, entityType, details);
        logger.info(message);
    }

    public void logError(String message, String userId, String entityId, String entityType, Object details, Exception e) {
        saveLog("ERROR", message, userId, entityId, entityType, details);
        logger.error(message, e);
    }

    private void saveLog(String level, String message, String userId, String entityId, String entityType, Object details) {
        LogEntry log = new LogEntry(UUID.randomUUID().toString(), level, message, userId, entityId, entityType, LocalDateTime.now(), details);
        logRepository.save(log);
    }
}
