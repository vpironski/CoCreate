package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.model.dto.ProjectDTO;
import org.cocreate.CoCreate.model.dto.ReorderCardsDTO;
import org.cocreate.CoCreate.model.dto.TaskDTO;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.dto.CardDTO;
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

    @PutMapping("/{projectId}/reorder-cards")
    public ResponseEntity<ResponseMessage> reorderCards(
            @PathVariable String userId,
            @PathVariable String projectId,
            @RequestBody ReorderCardsDTO reorderRequest) {
        return ResponseEntity.ok(projectService.reorderCards(userId, projectId, reorderRequest.getNewOrder()));
    }

    @GetMapping("/create-project")
    public ResponseEntity<CustomFields> getProjectCustomFields(@PathVariable String userId) {
        return ResponseEntity.ok(projectService.getProjectCustomFields(userId));
    }

    @PostMapping("/create-project")
    public ResponseEntity<ResponseMessage> createProject(@PathVariable String userId, @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(new ResponseMessage(projectService.createProject(userId, dto)));
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
        return ResponseEntity.ok(new ResponseMessage(projectService.updateProject(userId, projectId, updatedProject)));
    }

    @DeleteMapping("/delete-project/{projectId}")
    public ResponseEntity<ResponseMessage> deleteProject(@PathVariable String userId,
                                                         @PathVariable String projectId) {
        return ResponseEntity.ok(new ResponseMessage(projectService.deleteProject(userId, projectId)));
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
        return ResponseEntity.ok(new ResponseMessage(projectService.createTask(userId, projectId, taskDTO)));
    }

    @PutMapping("/{projectId}/{taskId}/edit")
    public ResponseEntity<ResponseMessage> updateTask(@PathVariable String userId,
                                                      @PathVariable String projectId,
                                                      @PathVariable String taskId,
                                                      @RequestBody(required = false) Task updatedTask) {
        return ResponseEntity.ok(new ResponseMessage(projectService.updateTask(userId, projectId, taskId, updatedTask)));
    }

    @DeleteMapping("/{projectId}/{taskId}/delete")
    public ResponseEntity<ResponseMessage> deleteTask(@PathVariable String userId,
                                                      @PathVariable String projectId,
                                                      @PathVariable String taskId) {
        return ResponseEntity.ok(new ResponseMessage(projectService.deleteTask(userId, projectId, taskId)));
    }
}

