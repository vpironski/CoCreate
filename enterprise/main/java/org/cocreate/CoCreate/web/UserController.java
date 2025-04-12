package org.cocreate.CoCreate.web;

import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.record.AuthResponse;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.model.record.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.record.AuthRequest;
import org.cocreate.CoCreate.service.CustomUserDetailsService;
import org.cocreate.CoCreate.config.jwt.JwtUtils;
import org.cocreate.CoCreate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
        User user = userService.createUser(userDto);
        String token = jwtUtils.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                "User registered successfully",
                user.getId(),
                token
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Generate token
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.username());
        String token = jwtUtils.generateToken(userDetails.getUsername());

        // Get user ID
        User user = userService.gerUserByUsername(request.username());
        if (user == null) {
            throw new BadRequestException("Invalid username or password");
        }

        return ResponseEntity.ok(new AuthResponse(
                "User logged in successfully",
                user.getId(),
                token
        ));
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