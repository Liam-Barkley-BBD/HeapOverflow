package com.heapoverflow.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heapoverflow.api.services.AuthService;

@RestController
public class AuthController {

    public final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "Hello unsecured";
    }

    @GetMapping("/auth")
    public String auth(@RequestParam String code) throws Exception {
        return authService.getJWT(code);
    }
}
