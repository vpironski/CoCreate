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

    public Project getProjectByIdAndUserId(String userId, String projectId){
        User user = userService.getUserById(userId);
        return projectRepository.findProjectByIdAndOwner(projectId,user);
    }

    public Project createProject(String userId, Project project) {
        User user = userService.getUserById(userId);
        project.setOwner(user);
        return projectRepository.save(project);
    }

    public Project updateProject(String userId, String projectId, Project updatedProject) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        User user = userService.getUserById(userId);
        if (!existingProject.getOwner().equals(user)) {
            throw new IllegalArgumentException("User does not own this project");
        }

        ProjectTaskMapper.mapToProject(updatedProject, existingProject);

        return projectRepository.save(existingProject);
    }


    public void deleteProject(String userId, String projectId) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        User user = userService.getUserById(userId);
        if (!existingProject.getOwner().equals(user)) {
            throw new IllegalArgumentException("User does not own this project");
        }

        projectRepository.delete(existingProject);
    }

    public Task createTask(String projectId, Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        task.setId(UUID.randomUUID().toString());
        project.getTasks().add(task);
        projectRepository.save(project);
        return task;
    }

    public List<Task> getTasksByProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return project.getTasks();
    }

    public Task updateTask(String projectId, String taskId, Task updatedTask) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Task existingTask = project.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        ProjectTaskMapper.mapToTask(updatedTask, existingTask);

        projectRepository.save(project);
        return existingTask;
    }


    public void deleteTask(String projectId, String taskId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean removed = project.getTasks().removeIf(task -> task.getId().equals(taskId));
        if (!removed) {
            throw new RuntimeException("Task not found");
        }

        projectRepository.save(project);
    }
}

