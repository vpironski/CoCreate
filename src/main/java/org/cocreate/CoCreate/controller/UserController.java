package org.cocreate.CoCreate.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.model.record.LoginResponse;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.model.record.UserRegisterDTO;
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
    public ResponseEntity<LoginResponse> registerUser(@RequestBody UserRegisterDTO userDto, HttpServletResponse response) {
        if (!userService.createUser(userDto)) {
            throw new BadRequestException("Registration failed");
        }
        User user = userService.getUserByEmail(userDto.email());

        String token = jwtUtils.generateToken(user.getUsername());

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);  // Prevent access via JavaScript
        cookie.setPath("/");  // Make the cookie accessible to all paths
        cookie.setMaxAge(60 * 60);  // Set cookie expiry (1 hour)
        response.addCookie(cookie);  // Add the cookie to the response

        String message = "User registered successfully";

        String userId = user.getId();

        LoginResponse loginResponse = new LoginResponse(message, userId);
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.username());
        String message = "User logged in successfully";
        String token = jwtUtils.generateToken(userDetails.getUsername());
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        User user = userService.gerUserByUsername(request.username());

        if (user == null) {
            throw new BadRequestException("Invalid username or password");
        }
        String userId = user.getId();

        LoginResponse loginResponse = new LoginResponse(message, userId);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);


        return ResponseEntity.ok(new ResponseMessage("User logged out successfully"));
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
