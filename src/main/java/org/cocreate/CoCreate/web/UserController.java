package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.dto.FieldSettingDTO;
import org.cocreate.CoCreate.model.record.AuthResponse;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.model.dto.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.record.AuthRequest;
import org.cocreate.CoCreate.service.CustomUserDetailsService;
import org.cocreate.CoCreate.config.jwt.JwtUtils;
import org.cocreate.CoCreate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public UserController(UserService userService, JwtUtils jwtUtils,
                          AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegisterDTO userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/{userId}/add-field")
    public ResponseEntity<ResponseMessage> addField(@PathVariable String userId, @RequestBody FieldSettingDTO dto) {
        return ResponseEntity.ok(userService.addField(userId, dto));
    }

    @PostMapping("/{userId}/remove-field")
    public ResponseEntity<ResponseMessage> removeField(@PathVariable String userId, @RequestBody FieldSettingDTO dto) {
        return ResponseEntity.ok(userService.removeField(userId, dto));
    }
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseMessage> updateUser(@PathVariable String userId, @RequestBody User user) {
        if (!userService.updateUser(userId, user)) {
            throw new BadRequestException("Failed to update user");
        }
        return ResponseEntity.ok(new ResponseMessage("User updated successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable String userId) {
        if (!userService.deleteUser(userId)) {
            throw new BadRequestException("Failed to delete user");
        }
        return ResponseEntity.ok(new ResponseMessage("User deleted successfully"));
    }
}