package org.cocreate.CoCreate.utility.mapper;

import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;

public class ProjectTaskMapper {

    public static Project mapToProject(Project source, Project target) {
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
        if (source.getDepartment() != null) {
            target.setDepartment(source.getDepartment());
        }
        if (source.getOwner() != null) {
            target.setOwner(source.getOwner());
        }
        if (source.getTeamMembers() != null && !source.getTeamMembers().isEmpty()) {
            target.setTeamMembers(source.getTeamMembers());
        }
        if (source.getWorkflow() != null && !source.getWorkflow().isEmpty()) {
            target.setWorkflow(source.getWorkflow());
        }
        if (source.getSettings() != null && !source.getSettings().isEmpty()) {
            target.setSettings(source.getSettings());
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
        if (source.getRelatedTicketsId() != null && !source.getRelatedTicketsId().isEmpty()) {
            target.setRelatedTicketsId(source.getRelatedTicketsId());
        }
        if (source.getParentProjectId() != null && !source.getParentProjectId().isEmpty()) {
            target.setParentProjectId(source.getParentProjectId());
        }
        if (source.getBudget() != null && source.getBudget() != 0.0) {
            target.setBudget(source.getBudget());
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
        return target;
    }

    public static Task mapToTask(Task source, Task target) {
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
        return target;
    }
}
