package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Project createProject(String userId, Project project) {
        User user = userService.getUserById(userId);
        project.setOwner(user);
        return projectRepository.save(project);
    }

    public Project updateProject(String userId, String projectId, Project updatedProject) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project Not Found with ID " + projectId));

        User user = userService.getUserById(userId);
        if (!existingProject.getOwner().equals(user)) {
            throw new IllegalArgumentException("User does not own this project");
        }

        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setPriority(updatedProject.getPriority());
        existingProject.setTags(updatedProject.getTags());
        return projectRepository.save(existingProject);
    }

    public void deleteProject(String userId, String projectId) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project Not Found with ID " + projectId));

        User user = userService.getUserById(userId);
        if (!existingProject.getOwner().equals(user)) {
            throw new IllegalArgumentException("User does not own this project");
        }

        projectRepository.delete(existingProject);
    }
}

