package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.cocreate.CoCreate.utility.mapper.ProjectTaskMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectTaskMapper projectTaskMapper;

    public ProjectService(ProjectRepository projectRepository, UserService userService, ProjectTaskMapper projectTaskMapper) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectTaskMapper = projectTaskMapper;
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

    public boolean createProject(String userId, ProjectDTO projectDTO) {
        // Map the DTO to the Project entity
        Project project = projectTaskMapper.mapToProject(projectDTO, userId);

        // Save the project to the repository
        projectRepository.save(project);

        return true; // Return success
    }

    public boolean updateProject(String userId, String projectId, Project updatedProject) {
        Project existingProject = getProjectByIdAndUserId(userId, projectId);
        ProjectTaskMapper.mapToProject(updatedProject, existingProject);
        existingProject.setOwnerId(userId);
        projectRepository.save(existingProject);
        return true;
    }

    public boolean deleteProject(String userId, String projectId) {
        Project existingProject = getProjectByIdAndUserId(userId, projectId);
        projectRepository.delete(existingProject);
        return true;
    }


    public List<Task> getTasksByProject(String userId, String projectId) {
        Project project = getProjectByIdAndUserId(userId, projectId);
        return project.getTasks();
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




