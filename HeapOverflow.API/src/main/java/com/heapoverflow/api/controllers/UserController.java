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
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        Page<User> users = userService.getUsersByFilter(username, email, pageable);

        return users.hasContent() ? ResponseEntity.ok(users) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userGoogleId}")
    public ResponseEntity<?> getUserById(@PathVariable String userGoogleId) {
        return userService.getUserById(userGoogleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoints */

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.userExistsById(user.getId())) {
            return ResponseEntity.badRequest().body("{\"error\": \"User already exists\"}");
        } else if (userService.userExistsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("{\"error\": \"User email already exists\"}");
        }

        return ResponseEntity.ok(userService.saveUser(user));
    }
}
