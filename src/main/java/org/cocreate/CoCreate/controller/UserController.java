package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.dto.ResponseMessage;
import org.cocreate.CoCreate.model.dto.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.record.AuthRequest;
import org.cocreate.CoCreate.service.CustomUserDetailsService;
import org.cocreate.CoCreate.utility.JwtUtils;
import org.cocreate.CoCreate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public UserController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody UserRegisterDTO userDto) {
        if (!userService.createUser(userDto)) {
            throw new BadRequestException("Registration failed");
        }
        return ResponseEntity.ok(new ResponseMessage("User registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.email());
        String token = jwtUtils.generateToken(userDetails.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
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
