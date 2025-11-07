package com.example.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from UserService!";
    }

    @GetMapping("/test")
    public String test() {
        return "testing code change auto-rebuild. HONK!";
    }
}

