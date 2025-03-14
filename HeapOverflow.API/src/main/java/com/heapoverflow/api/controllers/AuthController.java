package com.heapoverflow.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heapoverflow.api.services.AuthService;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/token")
    public ResponseEntity<String> auth(@RequestParam String code) throws Exception {

        try {
            String jwt = authService.authenticateUser(code);
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid authentication code");
        }
    }
}