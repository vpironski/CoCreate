package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.AuditLog;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.entity.custom.fields.StringCustomField;
import org.cocreate.CoCreate.repository.AuditRepository;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.cocreate.CoCreate.utility.mapper.ProjectTaskMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final LogService logService;
    private final AuditRepository auditLogRepository;

    public ProjectService(ProjectRepository projectRepository, UserService userService, LogService logService, AuditRepository auditLogRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.logService = logService;
        this.auditLogRepository = auditLogRepository;
    }

    public List<Project> getProjectsByUserId(String userId) {
        User user = userService.getUserById(userId);
        return projectRepository.findAllByOwnerId(user.getId());
    }

    public Project getProjectByIdAndUserId(String userId, String projectId) {
        User user = userService.getUserById(userId);
        return projectRepository.findProjectByIdAndOwnerId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));
    }

    public Project getProjectForEdit(String userId, String projectId) {
        return getProjectByIdAndUserId(userId, projectId);
    }

    public CustomFields getProjectCustomFields(String userId){
        Map<String, String> fieldSettings = userService.getUserById(userId).getFieldSettings();
        CustomFields customFields = new CustomFields();

        if (fieldSettings != null && !fieldSettings.isEmpty()) {
            fieldSettings.forEach((fieldName, dataType) -> {
                customFields.add(
                        switch (dataType){
                            case "string" -> new StringCustomField(fieldName, "");
                        });
            });
        }

        return customFields;
    }

//    private Object getDefaultValueForDataType(String dataType) {
//        if (dataType == null) return null;
//
//        return switch (dataType.toLowerCase()) {
//            case "string" -> "";
//            case "integer" -> 0;
//            case "double" -> 0.0;
//            case "boolean" -> false;
//            case "date" -> LocalDate.now().toString();
//            case "datetime" -> Instant.now().toString();
//            default -> null;
//        };
//    }

    public boolean createProject(String userId, ProjectDTO projectDTO) {
        try {
            Project project = ProjectTaskMapper.mapToProject(projectDTO, userId);

            Map<String, List<String>> defaultWorkflow = new HashMap<>();
            defaultWorkflow.put("To Do", new ArrayList<>());
            defaultWorkflow.put("In Progress", new ArrayList<>());
            defaultWorkflow.put("Done", new ArrayList<>());
            project.setWorkflow(defaultWorkflow);

            projectRepository.save(project);

            logService.logInfo("Project created successfully", userId, project.getId(), "Project",
                    Map.of("name", project.getName(), "description", project.getDescription()));

            return true;
        } catch (Exception e) {
            logService.logError("Error creating project", userId, null, "Project",
                    Map.of("error", e.getMessage()), e);
            throw e;
        }
    }

    public boolean updateProject(String userId, String projectId, Project updatedProject) {
        try {
            Project existingProject = getProjectByIdAndUserId(userId, projectId);
            ProjectTaskMapper.mapToProject(updatedProject, existingProject);
            existingProject.setOwnerId(userId);
            projectRepository.save(existingProject);

            logService.logInfo("Project updated successfully", userId, projectId, "Project",
                    Map.of("name", updatedProject.getName(), "status", updatedProject.getStatus()));

            return true;
        } catch (Exception e) {
            logService.logError("Error updating project", userId, projectId, "Project",
                    Map.of("error", e.getMessage()), e);
            throw e;
        }
    }

    public boolean deleteProject(String userId, String projectId) throws IllegalAccessException {
        Project existingProject = getProjectByIdAndUserId(userId, projectId);

        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType("Project");
            auditLog.setEntityId(existingProject.getId());
            auditLog.setUserId(userId);
            auditLog.setDeletedAt(LocalDateTime.now());

            Map<String, Object> originalData = new HashMap<>();
            for (Field field : Project.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(existingProject);

                if (value instanceof Enum<?>) {
                    value = ((Enum<?>) value).name();
                } else if (value instanceof LocalDateTime) {
                    value = value.toString();
                } else if (value instanceof List<?>) {
                    value = ((List<?>) value).stream()
                            .map(item -> extractId(item))
                            .toList();
                }

                originalData.put(field.getName(), value);
            }

            auditLog.setOriginalData(originalData);
            auditLogRepository.save(auditLog);

            projectRepository.delete(existingProject);
            logService.logInfo("Project deleted", userId, projectId, "Project", originalData);

            return true;
        } catch (Exception e) {
            logService.logError("Error deleting project", userId, projectId, "Project", Map.of("error", e.getMessage()), e);
            throw e;
        }
    }
    private String extractId(Object obj) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (String) idField.get(obj); // Extract and return ID
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return obj.toString(); // Fallback: Store object as string if no ID field
        }
    }

    public Task getTaskForEdit(String userId, String projectId, String taskId) {
        Project project = getProjectByIdAndUserId(userId, projectId);
        return project.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found in project: " + project.getName() + " !"));
    }

    public boolean createTask(String userId, String projectId, TaskDTO taskDTO) {
        Project project = getProjectByIdAndUserId(userId, projectId);

        List<User> assignedUsers = taskDTO.getUserIds().stream()
                .map(userService::getUserById)
                .toList();

        Task task = ProjectTaskMapper.mapToTask(taskDTO, assignedUsers);

        project.getTasks().add(task);
        projectRepository.save(project);
        return true;
    }

    public boolean updateTask(String userId, String projectId, String taskId, Task updatedTask) {
        Project project = getProjectByIdAndUserId(userId, projectId);
        List<Task> tasks = project.getTasks();

        Task existingTask = tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found in project: " + project.getName() + " !"));

        ProjectTaskMapper.mapToTask(updatedTask, existingTask);
        projectRepository.save(project);
        return true;
    }

    public boolean deleteTask(String userId, String projectId, String taskId) {
        Project project = getProjectByIdAndUserId(userId, projectId);
        List<Task> tasks = project.getTasks();

        boolean removed = tasks.removeIf(task -> task.getId().equals(taskId));

        if (!removed) {
            throw new EntityNotFoundException("Task not found in project: " + project.getName() + " !");
        }

        projectRepository.save(project);
        return true;
    }
}




