package com.heapoverflow.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heapoverflow.api.services.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("Testing no auth");
    }

    @GetMapping("/auth/token")
    public ResponseEntity<String> auth(@RequestParam String code) throws Exception {
        
        String jwt = authService.authenticateUser(code);
        return ResponseEntity.ok(jwt);
    }
}