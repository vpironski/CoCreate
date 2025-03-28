package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.Task;
import org.cocreate.CoCreate.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{userId}/dashboard")
public class ProjectController {


    private final ProjectService projectService;


    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("")
    public ResponseEntity<List<Project>> viewProjects(@PathVariable String userId) {
        return ResponseEntity.ok(projectService.getProjectsByUserId(userId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> viewProject(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectByIdAndUserId(userId, projectId));
    }

    @GetMapping("/createProject")
    public ResponseEntity<ProjectDTO> getEmptyProjectTemplate() {
        return ResponseEntity.ok(new ProjectDTO());
    }

    @PostMapping("/createProject")
    public ResponseEntity<ResponseMessage> createProject(@PathVariable String userId, @RequestBody ProjectDTO dto) {
        if (!projectService.createProject(userId, dto)) {
            throw new BadRequestException("Failed to create project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project created successfully!"));
    }

    @GetMapping("/editProject/{projectId}")
    public ResponseEntity<Project> getProjectForEdit(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectForEdit(userId, projectId));
    }

    @PutMapping("/editProject/{projectId}")
    public ResponseEntity<ResponseMessage> editProject(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody(required = false) Project updatedProject) {
        if (!projectService.updateProject(userId, projectId, updatedProject)) {
            throw new BadRequestException("Failed to update project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project updated successfully!"));
    }

    @DeleteMapping("/deleteProject/{projectId}")
    public ResponseEntity<ResponseMessage> deleteProject(@PathVariable String userId,
                                                         @PathVariable String projectId) throws IllegalAccessException {
        if (!projectService.deleteProject(userId, projectId)) {
            throw new BadRequestException("Failed to delete project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project deleted successfully!"));
    }

    @GetMapping("/{projectId}/{taskId}")
    public ResponseEntity<Task> getTask(
            @PathVariable String userId,
            @PathVariable String projectId,
            @PathVariable String taskId) {
        return ResponseEntity.ok(projectService.getTaskForEdit(userId, projectId, taskId));
    }

    @GetMapping("/{projectId}/task")
    public ResponseEntity<TaskDTO> createTask() {
        return ResponseEntity.ok(new TaskDTO());
    }

    @PostMapping("/{projectId}/task")
    public ResponseEntity<ResponseMessage> createTask(@PathVariable String userId,
                                                      @PathVariable String projectId,
                                                      @RequestBody TaskDTO taskDTO) {
        if (!projectService.createTask(userId, projectId, taskDTO)) {
            throw new BadRequestException("Failed to create task!");
        }
        return ResponseEntity.ok(new ResponseMessage("Task created successfully!"));
    }

    @PutMapping("/{projectId}/{taskId}/edit")
    public ResponseEntity<ResponseMessage> updateTask(@PathVariable String userId,
                                                      @PathVariable String projectId,
                                                      @PathVariable String taskId,
                                                      @RequestBody(required = false) Task updatedTask) {
        if (!projectService.updateTask(userId, projectId, taskId, updatedTask)) {
            throw new BadRequestException("Failed to update task!");
        }
        return ResponseEntity.ok(new ResponseMessage("Task updated successfully!"));
    }

    @DeleteMapping("/{projectId}/{taskId}/delete")
    public ResponseEntity<ResponseMessage> deleteTask(@PathVariable String userId, @PathVariable String projectId, @PathVariable String taskId) {
        if (!projectService.deleteTask(userId, projectId, taskId)) {
            throw new BadRequestException("Failed to delete task!");
        }
        return ResponseEntity.ok(new ResponseMessage("Task deleted successfully!"));
    }
}

