package com.heapoverflow.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    public static final ObjectMapper objectMapper= new ObjectMapper();

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** GET endpoints */

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{userGoogleId}")
    public ResponseEntity<User> getUserById(@PathVariable String userGoogleId) {
        return userRepository.findById(userGoogleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/username/{username}")
    public List<User> getUsersByUsername(@PathVariable String username) {
        return userRepository.findByUsernameContaining(username);
    }

    @GetMapping("/users/email/{email}")
    public List<User> getUsersByEmail(@PathVariable String email) {
        return userRepository.findByEmailContaining(email);
    }

    /** POST endpoints */

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        if (userRepository.existsById(user.getId())) {
            return ResponseEntity.badRequest().body("{\"error\": \"User already exists\"}");
        } else if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("{\"error\": \"User email already exists\"}");
        }

        return ResponseEntity.ok(userRepository.save(user));
    }
}

