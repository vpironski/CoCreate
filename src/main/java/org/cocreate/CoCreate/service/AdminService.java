package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.entity.AuditLog;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.repository.AuditRepository;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class AdminService {
    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final AuditRepository auditLogRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public AdminService(AuditRepository auditLogRepository, ProjectRepository projectRepository, UserService userService) {
        this.auditLogRepository = auditLogRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public boolean restoreProject(String userId, String projectId) {
        AuditLog auditLog = auditLogRepository.findByEntityIdAndUserId(projectId, userId)
                .orElseThrow(() -> new EntityNotFoundException("No deleted project found with ID: " + projectId + " for user " + userId));

        Project restoredProject = (Project) auditLog.getOriginalData();

        projectRepository.save(restoredProject);
        auditLogRepository.delete(auditLog); // Remove from audit log after restoration

        logger.info("User {} restored project {}", userId, projectId);
        return true;
    }

    private Task restoreTask(String taskId, String userId) {
        AuditLog taskAudit = auditLogRepository.findByEntityIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found in deleted log: " + taskId));

        return (Task) taskAudit.getOriginalData();
    }
}
