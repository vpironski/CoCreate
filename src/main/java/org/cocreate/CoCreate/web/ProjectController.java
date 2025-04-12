package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.record.CardDTO;
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

    @PostMapping("/{projectId}/add-card")
    public ResponseEntity<ResponseMessage> addCard(@PathVariable String userId, @PathVariable String projectId, @RequestBody CardDTO cardDTO){
        return ResponseEntity.ok(projectService.addCard(userId, projectId, cardDTO));
    }

    @DeleteMapping("/{projectId}/remove-card")
    public ResponseEntity<ResponseMessage> removeCard(@PathVariable String userId, @PathVariable String projectId, @RequestBody CardDTO cardDTO){
        return ResponseEntity.ok(projectService.removeCard(userId, projectId, cardDTO));
    }

    @GetMapping("/create-project")
    public ResponseEntity<CustomFields> getProjectCustomFields(@PathVariable String userId) {
        CustomFields customFields = projectService.getProjectCustomFields(userId);
        return ResponseEntity.ok(customFields);
    }

    @PostMapping("/create-project")
    public ResponseEntity<ResponseMessage> createProject(@PathVariable String userId, @RequestBody ProjectDTO dto) {
        if (!projectService.createProject(userId, dto)) {
            throw new BadRequestException("Failed to create project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project created successfully!"));
    }

    @GetMapping("/edit-project/{projectId}")
    public ResponseEntity<Project> getProjectForEdit(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectForEdit(userId, projectId));
    }

    @PutMapping("/edit-project/{projectId}")
    public ResponseEntity<ResponseMessage> editProject(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody(required = false) Project updatedProject) {
        if (!projectService.updateProject(userId, projectId, updatedProject)) {
            throw new BadRequestException("Failed to update project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project updated successfully!"));
    }

    @DeleteMapping("/delete-project/{projectId}")
    public ResponseEntity<ResponseMessage> deleteProject(@PathVariable String userId,
                                                         @PathVariable String projectId){
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

