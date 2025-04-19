package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.model.entity.AuditLog;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<User> showUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/audit-logs/{userId}")
    public List<AuditLog> getAuditLogs(@PathVariable String userId) {
        return adminService.getAllAuditLogsForUser(userId);
    }

    @PostMapping("{userId}/restore-project/{projectId}")
    public ResponseEntity<ResponseMessage> restoreProject(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(adminService.restoreProject(userId, projectId));
    }
}
