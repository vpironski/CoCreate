package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cocreate/{userId}/")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/viewProjects")
    public List<Project> viewProjects(@PathVariable String userId) {
        return projectService.getProjectsByUserId(userId);
    }

    @PostMapping("/createProject")
    public Project createProject(@PathVariable String userId, @RequestBody Project project) {
        return projectService.createProject(userId, project);
    }

    @PutMapping("/editProject/{projectId}")
    public Project editProject(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody Project updatedProject
    ) {
        return projectService.updateProject(userId, projectId, updatedProject);
    }

    @DeleteMapping("/deleteProject/{projectId}")
    public void deleteProject(@PathVariable String userId, @PathVariable String projectId) {
        projectService.deleteProject(userId, projectId);
    }
}
