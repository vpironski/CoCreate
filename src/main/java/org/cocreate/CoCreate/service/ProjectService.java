package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.cocreate.CoCreate.utility.mapper.ProjectTaskMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public List<Project> getProjectsByUserId(String userId) {
        User user = userService.getUserById(userId);
        return projectRepository.findAllByOwner(user);
    }

    public Project getProjectByIdAndUserId(String userId, String projectId) {
        User user = userService.getUserById(userId);
        return projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));
    }

    public Project createProject(String userId, Project project) {
        User user = userService.getUserById(userId);
        project.setOwner(user);
        return projectRepository.save(project);
    }

    public Project updateProject(String userId, String projectId, Project updatedProject) {
        User user = userService.getUserById(userId);
        Project existingProject = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        ProjectTaskMapper.mapToProject(updatedProject, existingProject);
        return projectRepository.save(existingProject);
    }

    public void deleteProject(String userId, String projectId) {
        User user = userService.getUserById(userId);
        Project existingProject = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        projectRepository.delete(existingProject);
    }

    public Task createTask(String userId, String projectId, Task task) {
        User user = userService.getUserById(userId);
        Project project = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        task.setId(UUID.randomUUID().toString());
        project.getTasks().add(task);
        projectRepository.save(project);
        return task;
    }

    public List<Task> getTasksByProject(String userId, String projectId) {
        User user = userService.getUserById(userId);
        Project project = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        return project.getTasks();
    }

    public Task updateTask(String userId, String projectId, String taskId, Task updatedTask) {
        User user = userService.getUserById(userId);
        Project project = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        Task existingTask = project.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found in project for user: " + user.getUsername() + " !"));

        ProjectTaskMapper.mapToTask(updatedTask, existingTask);
        projectRepository.save(project);
        return existingTask;
    }

    public void deleteTask(String userId, String projectId, String taskId) {
        User user = userService.getUserById(userId);
        Project project = projectRepository.findProjectByIdAndOwner(projectId, user)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for user: " + user.getUsername() + " !"));

        boolean removed = project.getTasks().removeIf(task -> task.getId().equals(taskId));
        if (!removed) {
            throw new EntityNotFoundException("Task not found in project for user: " + user.getUsername() + " !");
        }

        projectRepository.save(project);
    }
}


