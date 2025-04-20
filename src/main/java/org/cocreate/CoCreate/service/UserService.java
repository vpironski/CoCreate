package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.config.jwt.JwtUtils;
import org.cocreate.CoCreate.exception.BadRequestException;
import org.cocreate.CoCreate.exception.UserException;
import org.cocreate.CoCreate.model.dto.FieldSettingDTO;
import org.cocreate.CoCreate.model.dto.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.enums.UserRole;
import org.cocreate.CoCreate.model.record.AuthRequest;
import org.cocreate.CoCreate.model.record.AuthResponse;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final LogService logService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService, LogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.logService = logService;
    }

    public AuthResponse registerUser(UserRegisterDTO userDto) {
        User user = createUser(userDto);
        String token = jwtUtils.generateToken(user.getUsername());

        return new AuthResponse(
                "User registered successfully",
                user.getId(),
                user.getRole().toString(),
                token
        );
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.username());
        String token = jwtUtils.generateToken(userDetails.getUsername());

        User user = gerUserByUsername(request.username());
        if (user == null) {
            throw new BadRequestException("Invalid username or password");
        }

        return new AuthResponse(
                "User logged in successfully",
                user.getId(),
                user.getRole().toString(),
                token
        );
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User with email " + email + " not found"));
    }

    public User gerUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User with username" + username + "not found"));
    }

    public User createUser(UserRegisterDTO userDto) {
        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new UserException("Email is already used");
        }
        if (userRepository.findByUsername(userDto.username()).isPresent()) {
            throw new UserException("Username is already used");
        }

        User user = new User();
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public String updateUser(String userId, User updatedUser) {
        try {
            User existingUser = getUserById(userId);
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(existingUser);

             logService.logInfo("User updated successfully", userId, null, "User",
                 Map.of("username", updatedUser.getUsername()));

            return "User updated successfully";
        } catch (Exception e) {
             logService.logError("Failed to update user", userId, null, "User",
                 Map.of("error", e.getMessage()), e);
            throw new BadRequestException("Failed to update user");
        }
    }

    public String deleteUser(String userId) {
        try {
            User user = getUserById(userId);
            userRepository.delete(user);

             logService.logInfo("User deleted successfully", userId, null, "User", null);

            return "User deleted successfully";
        } catch (Exception e) {
             logService.logError("Failed to delete user", userId, null, "User",
                 Map.of("error", e.getMessage()), e);
            throw new BadRequestException("Failed to delete user");
        }
    }

    public ResponseMessage addField(String userId, FieldSettingDTO dto) {
        User user = getUserById(userId);
        user.getFieldSettings().put(dto.name(), dto.type());
        userRepository.save(user);

        return new ResponseMessage("New field added");
    }

    public ResponseMessage removeField(String userId, FieldSettingDTO dto) {
        User user = getUserById(userId);
        user.getFieldSettings().remove(dto.name());
        userRepository.save(user);

        return new ResponseMessage("Field " + dto.name() + "removed");
    }
}

