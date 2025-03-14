package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** GET endpoints */

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        Page<User> users = userService.getUsersByFilter(username, email, pageable);

        return users.hasContent() ? ResponseEntity.ok(users) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userGoogleId}")
    public ResponseEntity<User> getUserById(@PathVariable String userGoogleId) {
        return userService.getUserById(userGoogleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoints */

    // @PostMapping("/users")
    // public ResponseEntity<User> createUser(@RequestBody User user) {
    //     User savedUser = userService.createUser(user);
    //     return ResponseEntity.ok(savedUser);
    // }

}
