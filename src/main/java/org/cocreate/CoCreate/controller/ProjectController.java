package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.model.dto.ResponseMessage;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cocreate/{userId}/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/viewProjects")
    public ResponseEntity<List<Project>> viewProjects(@PathVariable String userId) {
        return ResponseEntity.ok(projectService.getProjectsByUserId(userId));
    }

    @GetMapping("/dashboard/{projectId}")
    public ResponseEntity<Project> viewProject(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectByIdAndUserId(userId, projectId));
    }

    @PostMapping("/createProject")
    public ResponseEntity<ResponseMessage> createProject(@PathVariable String userId, @RequestBody Project project) {
        projectService.createProject(userId, project);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Project created successfully!"));
    }

    @PutMapping("/editProject/{projectId}")
    public ResponseEntity<ResponseMessage> editProject(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody Project updatedProject
    ) {
        projectService.updateProject(userId, projectId, updatedProject);
        return ResponseEntity.ok(new ResponseMessage("Project updated successfully!"));
    }

    @DeleteMapping("/deleteProject/{projectId}")
    public ResponseEntity<ResponseMessage> deleteProject(@PathVariable String userId, @PathVariable String projectId) {
        projectService.deleteProject(userId, projectId);
        return ResponseEntity.ok(new ResponseMessage("Project deleted successfully!"));
    }

    @PostMapping("/{projectId}/createTask")
    public ResponseEntity<ResponseMessage> createTask(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody Task task) {
        projectService.createTask(userId, projectId, task);
        return ResponseEntity.ok(new ResponseMessage("Task created successfully!"));
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getTasksByProject(userId, projectId));
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<ResponseMessage> updateTask(
            @PathVariable String userId,
            @PathVariable String projectId,
            @PathVariable String taskId,
            @RequestBody Task updatedTask) {
        projectService.updateTask(userId, projectId, taskId, updatedTask);
        return ResponseEntity.ok(new ResponseMessage("Task updated successfully!"));
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<ResponseMessage> deleteTask(
            @PathVariable String userId,
            @PathVariable String projectId,
            @PathVariable String taskId) {
        projectService.deleteTask(userId, projectId, taskId);
        return ResponseEntity.ok(new ResponseMessage("Task deleted successfully!"));
    }
}
