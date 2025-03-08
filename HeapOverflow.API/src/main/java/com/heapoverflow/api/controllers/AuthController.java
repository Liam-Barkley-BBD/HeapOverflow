package com.heapoverflow.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "Hello unsecured";
    }

    @GetMapping("/auth")
    public String callback() {
        return "Hello secured";
    }
}
