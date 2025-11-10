package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from UserService";
    }

    // List all users
    @GetMapping
    public List<User> list() {
        return userRepository.findAll();
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        Optional<User> u = userRepository.findById(id);
        return u.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("email is required");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("email already exists");
        }

        User saved = userRepository.save(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/users/" + saved.getId()));
        return new ResponseEntity<>(saved, headers, HttpStatus.CREATED);
    }

    // Update existing user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User incoming) {
        return userRepository.findById(id).map(existing -> {
            if (incoming.getFirstName() != null) existing.setFirstName(incoming.getFirstName());
            if (incoming.getLastName() != null) existing.setLastName(incoming.getLastName());
            if (incoming.getEmail() != null && !incoming.getEmail().equals(existing.getEmail())) {
                if (userRepository.existsByEmail(incoming.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("email already exists");
                }
                existing.setEmail(incoming.getEmail());
            }
            existing.setTest(incoming.isTest());
            User saved = userRepository.save(existing);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
