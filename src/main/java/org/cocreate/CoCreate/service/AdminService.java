package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.entity.AuditLog;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.ProjectStatus;
import org.cocreate.CoCreate.repository.AuditRepository;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> originalData = auditLog.getOriginalData();

        Project restoredProject = new Project();
        restoredProject.setId(auditLog.getEntityId());
        restoredProject.setName((String) originalData.get("name"));
        restoredProject.setDescription((String) originalData.get("description"));
        restoredProject.setStatus(ProjectStatus.valueOf((String) originalData.get("status")));
        restoredProject.setStartDate(LocalDateTime.parse((String) originalData.get("startDate")));
        restoredProject.setEndDate(LocalDateTime.parse((String) originalData.get("endDate")));
        restoredProject.setDepartmentId((String) originalData.get("departmentId"));
        restoredProject.setOwnerId((String) originalData.get("ownerId"));
        restoredProject.setPriority(Priority.valueOf((String) originalData.get("priority")));
        restoredProject.setProgress((Integer) originalData.get("progress"));

        restoredProject.setTags((List<String>) originalData.get("tags"));
        restoredProject.setRelatedTicketsId((List<String>) originalData.get("relatedTicketsId"));
        restoredProject.setParentProjectId((String) originalData.get("parentProjectId"));
        restoredProject.setBudget(Double.valueOf(originalData.get("budget").toString()));
        restoredProject.setResources((Map<String, Object>) originalData.get("resources"));
        restoredProject.setActivityLog((List<Map<String, Object>>) originalData.get("activityLog"));
        restoredProject.setCustomFields((Map<String, Object>) originalData.get("customFields"));

        // Restore team members
        List<String> teamMemberIds = (List<String>) originalData.get("teamMembers");
        restoredProject.setTeamMembers(teamMemberIds.stream()
                .map(userService::getUserById)
                .toList());

        // Restore tasks
        List<String> taskIds = (List<String>) originalData.get("tasks");
        restoredProject.setTasks(taskIds.stream()
                .map(taskId -> restoreTask(taskId, userId))
                .toList());

        projectRepository.save(restoredProject);
        auditLogRepository.delete(auditLog); // Remove from audit log after restoration

        logger.info("User {} restored project {}", userId, projectId);
        return true;
    }

    private Task restoreTask(String taskId, String userId) {
        AuditLog taskAudit = auditLogRepository.findByEntityIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found in deleted log: " + taskId));

        Map<String, Object> taskData = taskAudit.getOriginalData();
        Task task = new Task();
        task.setId(taskAudit.getEntityId());
        task.setTitle((String) taskData.get("title"));
        task.setDescription((String) taskData.get("description"));
        task.setStartDate(LocalDateTime.parse((String) taskData.get("startDate")));
        task.setEndDate(LocalDateTime.parse((String) taskData.get("endDate")));

        // Restore assigned users
        List<String> userIds = (List<String>) taskData.get("assignedUsers");
        task.setAssignedUsers(userIds.stream().map(userService::getUserById).toList());

        return task;
    }
}
