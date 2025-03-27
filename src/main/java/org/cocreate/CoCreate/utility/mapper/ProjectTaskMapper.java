package org.cocreate.CoCreate.utility.mapper;

import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.ProjectStatus;
import org.cocreate.CoCreate.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ProjectTaskMapper {
    public static Project mapToProject(ProjectDTO projectDTO, String userId) {
        Project project = new Project();

        // Map fields from ProjectDTO to Project entity
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setParentProjectId(projectDTO.getParentProjectId());
        project.setCustomFields(projectDTO.getCustomFields());
        project.setPriority(projectDTO.getPriority());

        // Set default values or values that are not in the DTO
        project.setId(UUID.randomUUID().toString());
        project.setOwnerId(userId); // Set the ownerId from the provided userId
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.DRAFT); // Set status to DRAFT
        project.setProgress(0); // Set default progress to 0
        project.setTags(List.of()); // Set empty list for tags if none are provided
        project.setResources(Map.of()); // Set empty map for resources
        project.setActivityLog(List.of()); // Set empty list for activity log
        project.setTeamMembers(List.of()); // Set empty list for team members
        project.setTasks(List.of()); // Set empty list for tasks

        return project;
    }

    public static void mapToProject(Project source, Project target) {
        if (source.getName() != null && !source.getName().isEmpty()) {
            target.setName(source.getName());
        }
        if (source.getDescription() != null && !source.getDescription().isEmpty()) {
            target.setDescription(source.getDescription());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getTasks() != null && !source.getTasks().isEmpty()) {
            target.setTasks(source.getTasks());
        }
        if (source.getStartDate() != null) {
            target.setStartDate(source.getStartDate());
        }
        if (source.getEndDate() != null) {
            target.setEndDate(source.getEndDate());
        }
        if (source.getCreatedAt() != null) {
            target.setCreatedAt(source.getCreatedAt());
        }
        if (source.getUpdatedAt() != null) {
            target.setUpdatedAt(source.getUpdatedAt());
        }
        if (source.getOwnerId() != null) {
            target.setOwnerId(source.getOwnerId());
        }
        if (source.getTeamMembers() != null && !source.getTeamMembers().isEmpty()) {
            target.setTeamMembers(source.getTeamMembers());
        }
        if (source.getPriority() != null) {
            target.setPriority(source.getPriority());
        }
        if (source.getProgress() != null && source.getProgress() != 0) {
            target.setProgress(source.getProgress());
        }
        if (source.getTags() != null && !source.getTags().isEmpty()) {
            target.setTags(source.getTags());
        }
        if (source.getParentProjectId() != null && !source.getParentProjectId().isEmpty()) {
            target.setParentProjectId(source.getParentProjectId());
        }
        if (source.getResources() != null && !source.getResources().isEmpty()) {
            target.setResources(source.getResources());
        }
        if (source.getActivityLog() != null && !source.getActivityLog().isEmpty()) {
            target.setActivityLog(source.getActivityLog());
        }
        if (source.getCustomFields() != null && !source.getCustomFields().isEmpty()) {
            target.setCustomFields(source.getCustomFields());
        }
    }

    public static Task mapToTask(TaskDTO taskDTO, List<User> assignedUsers) {
        Task task = new Task();

        task.setId(UUID.randomUUID().toString());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStartDate(taskDTO.getStartDate());
        task.setEndDate(taskDTO.getEndDate());
        task.setDependencies(taskDTO.getDependencies());
        task.setAssignedUsers(assignedUsers);

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.MEDIUM);

        return task;
    }

    public static void mapToTask(Task source, Task target) {
        if (source.getId() != null && !source.getId().isEmpty()) {
            target.setId(source.getId());
        }
        if (source.getTitle() != null && !source.getTitle().isEmpty()) {
            target.setTitle(source.getTitle());
        }
        if (source.getDescription() != null && !source.getDescription().isEmpty()) {
            target.setDescription(source.getDescription());
        }
        if (source.getAssignedUsers() != null && !source.getAssignedUsers().isEmpty()) {
            target.setAssignedUsers(source.getAssignedUsers());
        }
        if (source.getStartDate() != null) {
            target.setStartDate(source.getStartDate());
        }
        if (source.getEndDate() != null) {
            target.setEndDate(source.getEndDate());
        }
        if (source.getPriority() != null) {
            target.setPriority(source.getPriority());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getDependencies() != null && !source.getDependencies().isEmpty()) {
            target.setDependencies(source.getDependencies());
        }
    }
}
