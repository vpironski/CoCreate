package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.*;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.entity.custom.fields.impl.*;
import org.cocreate.CoCreate.model.dto.CardDTO;
import org.cocreate.CoCreate.model.enums.EntityType;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.repository.AuditRepository;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.cocreate.CoCreate.config.mapper.ProjectTaskMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
                dataType = dataType.toLowerCase();
                customFields.add(
                        switch (dataType){
                            case "string" -> new StringCustomField(fieldName, "");
                            case "integer" -> new IntegerCustomField(fieldName, 0);
                            case "double" -> new DoubleCustomField(fieldName, 0.0);
                            case "boolean" -> new BooleanCustomField(fieldName, false);
                            case "localdate" -> new LocalDateCustomField(fieldName, LocalDate.now());
                            case "localdatetime" -> new LocalDateTimeCustomField(fieldName, LocalDateTime.now());
                            default -> throw new BadRequestException("Unsupported type: " + dataType);
                        });
            });
        }

        return customFields;
    }

    public ResponseMessage addCard(String userId, String projectId, CardDTO cardDTO) {
        if (userService.getUserById(userId) == null) {
            throw new BadRequestException("User not found");
        }

        Project project = getProjectByIdAndUserId(userId, projectId);

        project.getWorkflow().createCard(cardDTO.cardName());
        projectRepository.save(project);
        logService.logInfo("Project workflow updated successfully", userId, projectId, "Project",
                Map.of("name", project.getName()));

        return new ResponseMessage("Project workflow updated successfully");
    }

    public ResponseMessage removeCard(String userId, String projectId, CardDTO cardDTO) {
        if (userService.getUserById(userId) == null) {
            throw new BadRequestException("User not found");
        }

        Project project = getProjectByIdAndUserId(userId, projectId);

        project.getWorkflow().removeCard(cardDTO.cardName());
        projectRepository.save(project);
        logService.logInfo("Project workflow updated successfully", userId, projectId, "Project",
                Map.of("name", project.getName()));

        return new ResponseMessage("Project workflow updated successfully");
    }

    public ResponseMessage reorderCards(String userId, String projectId, List<String> newOrder) {
        try {
            Project project = getProjectByIdAndUserId(userId, projectId);

            // Get the current workflow
            Workflow workflow = project.getWorkflow();
            if (workflow == null) {
                workflow = new Workflow();
                project.setWorkflow(workflow);
            }

            // Create a new ordered map for the cards
            LinkedHashMap<String, List<Task>> reorderedCards = new LinkedHashMap<>();

            // Rebuild the cards map in the new order
            for (String cardName : newOrder) {
                if (workflow.getCards().containsKey(cardName)) {
                    reorderedCards.put(cardName, workflow.getCards().get(cardName));
                }
            }

            // Add any remaining cards that weren't in the new order (shouldn't happen if frontend sends complete list)
            workflow.getCards().forEach((cardName, tasks) -> {
                if (!reorderedCards.containsKey(cardName)) {
                    reorderedCards.put(cardName, tasks);
                }
            });

            // Update the workflow with the new order
            workflow.setCards(reorderedCards);

            // Save the project
            projectRepository.save(project);

            return new ResponseMessage("Cards reordered successfully");
        } catch (Exception e) {
            logService.logError("Failed to reorder cards", userId, projectId, "Workflow",
                    Map.of("error", e.getMessage()), e);
            throw new BadRequestException("Failed to reorder cards: " + e.getMessage());
        }
    }

    public String createProject(String userId, ProjectDTO projectDTO) {
        try {
            Project project = ProjectTaskMapper.mapToProject(projectDTO, userId);
            projectRepository.save(project);

            logService.logInfo("Project created successfully", userId, project.getId(), "Project",
                    Map.of("name", project.getName(), "description", project.getDescription()));

            return "Project created successfully!";
        } catch (Exception e) {
            logService.logError("Failed to create project", userId, null, "Project",
                    Map.of("error", e.getMessage()), e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public String updateProject(String userId, String projectId, Project updatedProject) {
        try {
            Project existingProject = getProjectByIdAndUserId(userId, projectId);
            ProjectTaskMapper.mapToProject(updatedProject, existingProject);
            existingProject.setOwnerId(userId);
            projectRepository.save(existingProject);

            logService.logInfo("Project updated successfully", userId, projectId, "Project",
                    Map.of("name", updatedProject.getName(), "status", updatedProject.getStatus()));

            return "Project updated successfully!";
        } catch (Exception e) {
            logService.logError("Error updating project", userId, projectId, "Project",
                    Map.of("error", e.getMessage()), e);
            throw new BadRequestException(e.getMessage());
        }
    }


    public String deleteProject(String userId, String projectId) {
        try {
            Project existingProject = getProjectByIdAndUserId(userId, projectId);

            AuditLog auditLog = new AuditLog();
            auditLog.setEntityId(existingProject.getId());
            auditLog.setUserId(userId);
            auditLog.setDeletedAt(LocalDateTime.now());
            auditLog.setOriginalData(existingProject);
            auditLog.setType(EntityType.PROJECT);
            auditLogRepository.save(auditLog);

            projectRepository.delete(existingProject);
            logService.logInfo("Project deleted", userId, projectId, "Project", existingProject);

            return "Project deleted successfully!";
        } catch (Exception e) {
            logService.logError("Failed to delete project", userId, projectId, "Project",
                    Map.of("error", e.getMessage()), e);
            throw new BadRequestException(e.getMessage());
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
        return getProjectByIdAndUserId(userId, projectId)
                .getWorkflow()
                .getTaskById(taskId);
    }

    public String createTask(String userId, String projectId, TaskDTO taskDTO) {
        try {
            Project project = getProjectByIdAndUserId(userId, projectId);
            Task task = ProjectTaskMapper.mapToTask(taskDTO);
            project.getWorkflow().addTaskToCard(taskDTO.getCard(), task);
            projectRepository.save(project);
            return "Task created successfully!";
        } catch (Exception e) {
            logService.logError("Failed to create task", userId, projectId, "Task",
                    Map.of("error", e.getMessage()), e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public String updateTask(String userId, String projectId, String taskId, Task updatedTask) {
        try {
            Project project = getProjectByIdAndUserId(userId, projectId);
            Task existingTask = project.getWorkflow().getTaskById(taskId);
            ProjectTaskMapper.mapToTask(updatedTask, existingTask);
            projectRepository.save(project);
            return "Task updated successfully!";
        } catch (Exception e) {
            logService.logError("Failed to update task", userId, projectId, "Task",
                    Map.of("error", e.getMessage(), "taskId", taskId), e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public String deleteTask(String userId, String projectId, String taskId) {
        try {
            Project project = getProjectByIdAndUserId(userId, projectId);
            project.getWorkflow().removeTaskById(taskId);
            projectRepository.save(project);
            return "Task deleted successfully!";
        } catch (Exception e) {
            logService.logError("Failed to delete task", userId, projectId, "Task",
                    Map.of("error", e.getMessage(), "taskId", taskId), e);
            throw new BadRequestException(e.getMessage());
        }
    }


}




