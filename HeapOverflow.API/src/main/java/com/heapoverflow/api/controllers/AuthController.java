package com.heapoverflow.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.heapoverflow.api.models.AuthRequest;
import com.heapoverflow.api.services.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/token")
    public ResponseEntity<String> auth(@RequestBody AuthRequest authRequest) throws Exception {
        String code = authRequest.getCode();
        String jwt = authService.authenticateUser(code);
        return ResponseEntity.ok(jwt);
    }
}