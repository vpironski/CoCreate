package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.UserException;
import org.cocreate.CoCreate.model.dto.FieldSettingDTO;
import org.cocreate.CoCreate.model.dto.UserRegisterDTO;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.model.enums.UserRole;
import org.cocreate.CoCreate.model.record.ResponseMessage;
import org.cocreate.CoCreate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public User gerUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User with username" + username + "not found"));
    }

    public User createUser(UserRegisterDTO userDto) {
        // Check for existing users
        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new UserException("Email is already used");
        }
        if (userRepository.findByUsername(userDto.username()).isPresent()) {
            throw new UserException("Username is already used");
        }

        // Create and save new user
        User user = new User();
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRoles(List.of(UserRole.USER));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user); // Return the persisted user directly
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

