package com.heapoverflow.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    public static final ObjectMapper objectMapper= new ObjectMapper();

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public JsonNode getAllUsers() throws JsonMappingException, JsonProcessingException {
        var users = userRepository.findAll();
        System.out.println("USERS: " + users);
    return objectMapper.readTree(users.toString());
    }

    @GetMapping("/users/{userGoogleId}")
    public Optional<User> getUserById(@PathVariable String userGoogleId) {
        return userRepository.findById(userGoogleId);
    }

    @GetMapping("/users/username/{username}")
    public List<User> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }
}

