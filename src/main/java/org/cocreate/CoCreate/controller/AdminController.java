package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("{userId}/restoreProject/{projectId}")
    public ResponseEntity<ResponseMessage> restoreProject(@PathVariable String userId, @PathVariable String projectId) {
        if (!adminService.restoreProject(userId, projectId)) {
            throw new BadRequestException("Failed to restore project!");
        }
        return ResponseEntity.ok(new ResponseMessage("Project restored successfully!"));
    }
}
