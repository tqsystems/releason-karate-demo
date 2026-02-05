package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User management
 * Provides CRUD operations for users
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        logger.info("GET /api/users/{} - Fetching user by ID", id);
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with id: " + id));
    }

    /**
     * Create new user
     * Validates email format and uniqueness
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        logger.info("POST /api/users - Creating new user: {}", user.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists: " + user.getEmail());
        }

        // Validate age
        if (user.getAge() != null && user.getAge() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Age must be positive");
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        logger.info("PUT /api/users/{} - Updating user", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    if (userDetails.getName() != null) {
                        user.setName(userDetails.getName());
                    }
                    if (userDetails.getAge() != null) {
                        if (userDetails.getAge() < 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Age must be positive");
                        }
                        user.setAge(userDetails.getAge());
                    }
                    if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(userDetails.getEmail())) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Email already exists: " + userDetails.getEmail());
                        }
                        user.setEmail(userDetails.getEmail());
                    }
                    
                    User updatedUser = userRepository.save(user);
                    logger.info("User updated successfully: {}", id);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with id: " + id));
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    logger.info("User deleted successfully: {}", id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
