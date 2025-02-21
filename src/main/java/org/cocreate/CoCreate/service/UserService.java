package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.UserException;
import org.cocreate.CoCreate.model.dto.UserLoginDTO;
import org.cocreate.CoCreate.model.dto.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.enums.UserRoleEnum;
import org.cocreate.CoCreate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User with email " + email + " not found"));
    }

    public boolean createUser(UserRegisterDTO userDto) {
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
        List<UserRoleEnum> roles = new ArrayList<>();
        roles.add(UserRoleEnum.USER);
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    public boolean updateUser(String userId, User updatedUser) {
        User existingUser = getUserById(userId);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(existingUser);
        return true;
    }

    public boolean deleteUser(String userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        return true;
    }
}

