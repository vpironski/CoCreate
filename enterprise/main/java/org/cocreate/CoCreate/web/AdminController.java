package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String testReturn(){
        return "Test";
    }

    @PostMapping("{userId}/restore-project/{projectId}")
    public ResponseEntity<ResponseMessage> restoreProject(@PathVariable String userId, @PathVariable String projectId) {
        return ResponseEntity.ok(adminService.restoreProject(userId, projectId));
    }
}
